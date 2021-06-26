package org.bsc.maven.plugin.processor;

import java.util.Objects;

/**
 * Maven-coordinates of a dependency.
 * 
 * @author Ulysses R. Ribeiro
 *
 */
public class DependencyCoordinate {

    private String groupId;

    private String artifactId;

    private String version;

    private String classifier;

    private String type = "jar";

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClassifier() {
		return this.classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(artifactId, classifier, groupId, type, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DependencyCoordinate other = (DependencyCoordinate) obj;
		return Objects.equals(artifactId, other.artifactId) && Objects.equals(classifier, other.classifier)
				&& Objects.equals(groupId, other.groupId) && Objects.equals(type, other.type)
				&& Objects.equals(version, other.version);
	}

	@Override
	public String toString() {
		return "DependencyCoordinate [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version
				+ ", classifier=" + classifier + ", type=" + type + "]";
	}
    
}
