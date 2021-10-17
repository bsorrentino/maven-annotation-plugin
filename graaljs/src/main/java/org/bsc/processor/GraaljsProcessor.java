package org.bsc.processor;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class GraaljsProcessor extends BaseAbstractProcessor {

    private static final Logger log = Logger.getLogger(GraaljsProcessor.class.getName());

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        /**
         * @ref https://www.graalvm.org/reference-manual/js/Options/#to-the-launcher
         *
         * The following options are currently available:
         *
         * --js.annex-b:
         * enable ECMAScript Annex B web compatibility features. Boolean value, default is true.
         * --js.array-sort-inherited:
         * define whether Array.protoype.sort should sort inherited keys (implementation-defined behavior). Boolean value, default is true.
         * --js.atomics:
         * enable ES2017 Atomics. Boolean value, default is true.
         * --js.ecmascript-version:
         * emulate a specific ECMAScript version. Integer value (5-13, or 2015-2022), default is the latest stable version.
         * --js.foreign-object-prototype:
         * provide JavaScript’s default prototype to foreign objects that mimic JavaScript’s own types (foreign Arrays, Objects and Functions). Boolean value, default is false.
         * --js.intl-402:
         * enable ECMAScript Internationalization API. Boolean value, default is false.
         * --js.regexp-static-result:
         * provide static RegExp properties containing the results of the last successful match, e.g., RegExp.$1 (legacy). Boolean value, default is true.
         * --js.shared-array-buffer:
         * enable ES2017 SharedArrayBuffer. Boolean value, default is false.
         * --js.strict:
         * enable strict mode for all scripts. Boolean value, default is false.
         * --js.timezone:
         * set the local time zone. String value, default is the system default.
         * --js.v8-compat:
         * provide better compatibility with Google’s V8 engine. Boolean value, default is false.
         */
        final Context context = Context.newBuilder("js")
                //.allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                //.allowCreateThread(true)
                .allowHostClassLookup( s -> true )
                .allowIO(true)
                //.allowExperimentalOptions(true)
                .option("js.foreign-object-prototype", "true")

                .build();

        final Map<String,String> options = getOptions();

        context.getBindings("js").putMember("$options", options );

        final String scriptSource = options.get( "script" );
        if( scriptSource == null ) {
            error( "option 'script' not set!");
            return false;
        }

        try( java.io.Reader app = new java.io.FileReader(scriptSource)) {
            final Source source = Source.newBuilder("js", app,  scriptSource).build();
            context.eval( source );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
