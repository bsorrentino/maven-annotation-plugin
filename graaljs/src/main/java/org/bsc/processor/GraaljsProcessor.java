package org.bsc.processor;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;


interface GraaljsEvaluator {

    default Value eval(java.io.Reader sourceScript, String sourceName) throws Exception {

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
                .allowHostClassLookup(s -> true)
                .allowIO(true)
                //.allowExperimentalOptions(true)
                .option("js.foreign-object-prototype", "true")
                .build();
        final Source source = Source.newBuilder("js", sourceScript, sourceName).build();

        return context.eval(source);

    }

}

public class GraaljsProcessor extends BaseAbstractProcessor implements GraaljsEvaluator {

    static final Logger log = Logger.getLogger(GraaljsProcessor.class.getName());

    private Optional<Value> evalValue = empty();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        final Map<String, String> options = super.getOptions();

        final String scriptSourceName = options.get("script");
        if (scriptSourceName == null) {
            error("option 'script' not set!");
            return;
        }


        try {
            final FileObject fileObject = super.getResourceFormClassPath("", scriptSourceName);

            try (java.io.Reader scriptSourceReader = fileObject.openReader(true)) {
                evalValue = ofNullable(eval(scriptSourceReader, scriptSourceName));
            } catch (Exception e) {
                error("error loading/evaluating script", e);
            }
        } catch (Exception e) {
            error("error reading script", e);
        }
    }

    private boolean executeProcess( Value evalResult,
                                    Set<? extends TypeElement> annotations,
                                    RoundEnvironment roundEnv )
    {

        if (evalResult.canInvokeMember("e -> process")) {
            error("it is not possible invoke 'process' member!");
            return false;
        }

        final Value invokeResult =
                evalResult.invokeMember("process", annotations, roundEnv);

        if (invokeResult.isBoolean()) {
            return invokeResult.asBoolean();
        }

        return false;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        return evalValue
                .map( v -> executeProcess( v, annotations, roundEnv ) )
                .orElseGet( () -> {
                    warn( "no script evaluation detected!");
                    return true;
                });
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return  evalValue
                .map( value -> {
                    warn( "no 'SupportedAnnotationTypes' specified!");
                    Set<String> result = Collections.emptySet();
                    return result;
                })
                .orElseGet( () -> {
                    warn( "no script evaluation detected!");
                    return super.getSupportedAnnotationTypes();
                });

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return  evalValue
                .map( value -> {
                    warn( "no 'SupportedSourceVersion' specified!");
                    return SourceVersion.latestSupported();
                })
                .orElseGet( () -> {
                    warn( "no script evaluation detected!");
                    return super.getSupportedSourceVersion();
                });

    }
}
