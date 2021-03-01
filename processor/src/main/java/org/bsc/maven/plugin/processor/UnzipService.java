package org.bsc.maven.plugin.processor;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

/**
 *
 * @author Heiko Braun
 */
class ZipFileObject extends SimpleJavaFileObject
{
  private ZipEntry zipEntry;
  private ZipFile zipFile;

  public ZipFileObject(ZipFile zipFile, ZipEntry zipEntry, URI uri)
  {
    super(uri, JavaFileObject.Kind.SOURCE);
    this.zipEntry = zipEntry;
    this.zipFile = zipFile;
  }

  @Override
  public InputStream openInputStream() throws IOException
  {
    return this.zipFile.getInputStream(this.zipEntry);
  }

  @Override
  public String getName()
  {
    return this.zipEntry.getName();
  }

  @Override
  public CharSequence getCharContent(boolean b)
      throws IOException
  {
    InputStreamReader is = new InputStreamReader(openInputStream());
    StringBuilder sb = new StringBuilder();
    BufferedReader br = new BufferedReader(is);
    String read = br.readLine();

    while (read != null) {
      sb.append(read).append("\n");
      read = br.readLine();
    }

    return sb.toString();
  }

  @Override
  public Reader openReader(boolean b) throws IOException
  {
    return new BufferedReader(new InputStreamReader(openInputStream()));
  }

  @Override
  public long getLastModified()
  {
    return this.zipEntry.getTime();
  }

  public static ZipFileObject create(ZipFile zipFile, ZipEntry entry) {
    try {
      final String uri = String.format("jar://%s!%s", zipFile.getName().replace("\\", "/"), entry.getName() );
      return new ZipFileObject(zipFile, entry, new URI(uri));
    }
    catch (URISyntaxException e)
    {
      throw new RuntimeException("Invalid zip entry:" + e.getMessage());
    }
  }
}

class ZipFileObjectExtracted extends SimpleJavaFileObject {

  public ZipFileObjectExtracted(URI uri, Kind kind) {
    super(uri, kind);
  }
}

public final class UnzipService {


  public static final UnzipService newInstance(Log log ) {
    requireNonNull( log, "log argument is null!");

    return new UnzipService(log);
  }

  private final Log log;

  private UnzipService(Log log ) {
    this.log = log;
  }

  private boolean isJavaEntry( ZipEntry entry ) {
    return entry.getName().endsWith(".java");
  }

  public final void extractSourcesFromArtifact(Artifact artifact, java.util.List<JavaFileObject> allSources ) {
    requireNonNull( artifact, "artifact argument is null!");
    requireNonNull( allSources, "allSources argument is null!");

    final File f = artifact.getFile();

    try {

      final ZipFile zipFile = new ZipFile(f);
      final Enumeration<? extends ZipEntry> entries = zipFile.entries();
      int sourceCount = 0;

      while (entries.hasMoreElements()) {
        final ZipEntry entry = entries.nextElement();

        if (isJavaEntry(entry)) {
          ++sourceCount;
          allSources.add(ZipFileObject.create(zipFile, entry));
        }
      }
      log.debug(format("** Discovered %d java sources in %s", sourceCount, f.getAbsolutePath()));

    } catch (Exception ex) {
      log.warn(format("Problem reading source archive [%s]", f.getPath()));
      log.debug(ex);
    }
  }

  public final void extractSourcesFromArtifactToTempDirectory(Artifact artifact, java.util.List<JavaFileObject> allSources ) {
    requireNonNull( artifact, "artifact argument is null!");
    requireNonNull( allSources, "allSources argument is null!");

    final File fileZip = artifact.getFile();

    final Path root;
    try {
      root = Files.createTempDirectory(fileZip.getName());
    }
    catch( IOException ex ) {
      log.warn("Problem creating temporary directory", ex);
      return;
    }

    try( final ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip)) ) {

      final byte[] buffer = new byte[4096];

      ZipEntry zipEntry = zis.getNextEntry();

      while (zipEntry != null) {

        final Path newFile = Paths.get(root.toString(), zipEntry.getName());

        if (zipEntry.isDirectory()) {

          Files.createDirectories(newFile);

        } else if (isJavaEntry(zipEntry)) {

          try (final FileOutputStream fos = new FileOutputStream(newFile.toFile())) {
            int len;
            while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
            allSources.add( new ZipFileObjectExtracted(newFile.toUri(), JavaFileObject.Kind.SOURCE));

          }

        }
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
    }
    catch( Exception ex ) {
      log.warn(format("Problem reading source archive [%s]", fileZip));
      log.debug(ex);

    }

    log.info( format( "artifact [%s] succesfully extracted to [%s]", artifact, root));
  }
}
