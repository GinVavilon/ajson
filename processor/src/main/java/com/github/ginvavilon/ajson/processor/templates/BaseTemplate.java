/**
 *
 */
package com.github.ginvavilon.ajson.processor.templates;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vladimir Baraznovsky
 *
 */
public abstract class BaseTemplate {

    private static final int BODY = 0;

    private StringBuilder mCurrentBuilder;

    private StringBuilder mMainBuilder;
    private Map<Integer, StringBuilder> mBuffers = new HashMap<Integer, StringBuilder>();

    protected void addImports() {

    };

    public abstract String getPackageName();

    public abstract String getClassName();

    protected String getSuperName(){
        return null;
    };

    protected void printClassBody() {

    }

    protected void changeToBuffer(int pIndex) {
        StringBuilder buffer = mBuffers.get(pIndex);
        if (buffer == null) {
            buffer = new StringBuilder();
            mBuffers.put(pIndex, buffer);
        }
        mCurrentBuilder = buffer;
    }

    protected void changeToMain() {
        mCurrentBuilder = mMainBuilder;
    }

    protected void printBuffer(int pIndex) {
        StringBuilder buffer = mBuffers.get(pIndex);
        mMainBuilder.append(buffer);
        if (mCurrentBuilder == buffer) {
            mCurrentBuilder = mMainBuilder;
        }
    }

    public String generateTemplate() {
        mBuffers.clear();
        mMainBuilder = new StringBuilder();
        changeToMain();
        changeToBuffer(BODY);
        printClassBody();
        changeToMain();
        println("package %s;", getPackageName());

        addImports();
        println();
        print("public class %s ", getClassName());

        String superName = getSuperName();
        if (superName != null) {
            print("extends %s ", superName);
        }
        println("{");
        println();
        printBuffer(BODY);
        println("}");
        return mCurrentBuilder.toString();

    }

    public void addImport(String pImport) {
        println("import %s;", pImport);
        return;
    }

    protected void print(String string, Object... pArgs) {

        mCurrentBuilder.append(String.format(string, pArgs));
    }

    protected void println() {
        mCurrentBuilder.append("\n");
    }

    protected void println(String string, Object... pArgs) {
        print(string, pArgs);
        println();
    }

}