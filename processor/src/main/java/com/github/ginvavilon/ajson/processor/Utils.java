/**
 *
 */
package com.github.ginvavilon.ajson.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

/**
 * @author Vladimir Baraznovsky
 *
 */
public class Utils {

    public static String getNameParser(Element pElement) {
        if (isClass("java.lang.String", pElement)) {
            return "StringJson";
        }
        return pElement.getSimpleName() + "Json";
    }

    public static String getPackageParser(Element pElement) {
        if (isClass("java.lang.String", pElement)) {
            return "com.github.ginvavilon.ajson";
        }

        Element enclosingElement = pElement.getEnclosingElement();
        if (enclosingElement instanceof PackageElement) {
            return ((PackageElement) enclosingElement).getQualifiedName().toString();
        } else {
            return getPackageParser(enclosingElement);
        }

    }

    public static String getFullParser(Element pElement) {
        return getPackageParser(pElement) + "." + getNameParser(pElement);
    }

    public static boolean isClass(DeclaredType pType, String pName) {
        Element element = pType.asElement();
        return isClass(pName, element);

    }

    private static boolean isClass(String pName, Element element) {
        if (element instanceof TypeElement){
            TypeElement typeElement = (TypeElement) element;
            return pName.equals(typeElement.getQualifiedName().toString());
        }
        return false;
    }

}
