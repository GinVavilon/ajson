/**
 *
 */
package com.github.ginvavilon.ajson.processor.templates;

import java.util.Set;

import javax.lang.model.element.Element;

import com.github.ginvavilon.ajson.processor.Utils;

/**
 * @author Vladimir Baraznovsky
 *
 */
public class ParsersTemplate extends BaseTemplate {

    private Set<? extends Element> mElements;

    // public static final PuzzleItemParser PUZZLE_ITEM = new
    // PuzzleItemParser();

    @Override
    public String getPackageName() {
        return "com.github.ginvavilon.ajson";
    }

    @Override
    public String getClassName() {
        return "JsonParsers";
    }

    @Override
    protected void printClassBody() {

        for (Element element : mElements) {
            String nameParcer = Utils.getNameParser(element);
            println("   public static final %s %s = %s.getInstance();", nameParcer,
                    element.getSimpleName(),
                    nameParcer);
        }
    }

    @Override
    protected void addImports() {
        super.addImports();
        println();

        for (Element element : mElements) {
            addImport(Utils.getPackageParser(element) + "." + Utils.getNameParser(element));
        }

    }

    public void add(Set<? extends Element> pElements) {
        mElements = pElements;

    }

}
