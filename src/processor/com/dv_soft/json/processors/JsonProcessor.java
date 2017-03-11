/**
 *
 */
package com.dv_soft.json.processors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.dv_soft.json.annatations.JsonObject;
import com.dv_soft.json.processors.templates.BaseTemplate;
import com.dv_soft.json.processors.templates.JsonParserTemplate;

/**
 * @author Vladimir Baraznovsky
 *
 */
@SupportedAnnotationTypes({ "com.dv_soft.json.annatations.JsonObject" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JsonProcessor extends AbstractProcessor {

    private static final boolean LOG_ENABED = false;
    private static final boolean ECHO_ENABLED = false;
    private File mLogFile;
    private boolean mFirst;
    private Set<Element> mElements = new HashSet<Element>();

    public JsonProcessor() {
        super();
        mFirst = true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment pProcessingEnv) {
        super.init(pProcessingEnv);
        mElements.clear();
        if (mFirst) {
            mFirst = true;
            info("----- [First Init]-----");

        } else {
            info("----- [Init]-----");
        }
        mFirst = false;

    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        info("Start");
        boolean ret = false;

        for (Element e : roundEnv.getElementsAnnotatedWith(JsonObject.class)) {
            ret = true;
            mElements.add(e);
            generateJavaSource(new JsonParserTemplate(processingEnv, (TypeElement) e));
        }

        // if (roundEnv.processingOver()) {
        // ParsersTemplate parsersTemplate = new ParsersTemplate();
        // parsersTemplate.add(mElements);
        // generateJavaSource(parsersTemplate);
        // }
        return ret;
    }

    private void generateJavaSource(BaseTemplate template) {
        try {

            JavaFileObject f = processingEnv.getFiler().createSourceFile(
                    template.getPackageName() + "." + template.getClassName());
            info("Creating " + f.toUri());
            f.delete();
            Writer w = f.openWriter();
            try {
                PrintWriter pw = new PrintWriter(w);
                pw.print(template.generateTemplate());
                pw.flush();
            } finally {
                w.close();
            }

        } catch (Exception x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
        }
    }

    private PrintWriter getLog() {

        if (isEnabledLog()) {
            try {
                File file = getLogFile();
                if (!file.exists()) {
                    file.createNewFile();
                }

                return new PrintWriter(new FileOutputStream(file, true), true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new PrintWriter(System.out);
    }

    private void error(Throwable pE) {
        pE.printStackTrace(getLog());
        RuntimeException exception = new RuntimeException(pE);
        exception.setStackTrace(pE.getStackTrace());
        throw exception;
    }

    private void warning(Throwable pE) {
        if (!ECHO_ENABLED) {
            return;
        }
        warning("%s", pE);
        pE.printStackTrace(getLog());
    }

    private void info(String pMessage, Object... pArgs) {
        log(Kind.NOTE, "[INFO] : " + pMessage, pArgs);
    }

    private void log(Kind pKind, String pMessage, Object... pArgs) {
        if (!ECHO_ENABLED) {
            return;
        }
        String msg = String.format(pMessage, pArgs);
        this.processingEnv.getMessager().printMessage(pKind, msg);
        getLog().println(msg);
        getLog().flush();
    }

    private void error(String pMessage, Object... pArgs) {
        log(Kind.ERROR, "[ERROR] : " + pMessage, pArgs);
        throw new RuntimeException(String.format(pMessage, pArgs));
    }

    private void warning(String pMessage, Object... pArgs) {
        if (!ECHO_ENABLED) {
            return;
        }
        log(Kind.WARNING, "[WARNING] : " + pMessage, pArgs);
    }

    private boolean isEnabledLog() {
        return LOG_ENABED;
    }

    private File getLogFile() {
        if (mLogFile != null) {
            return mLogFile;
        }
        String directory = "/home/vbaraznovsky/test/log";
        mLogFile = new File(directory, DateFormat.getDateTimeInstance().format(new Date()) + ".log");
        return mLogFile;
    }

}
