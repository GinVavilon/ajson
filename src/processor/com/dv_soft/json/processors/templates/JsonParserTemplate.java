/**"
 *
 */
package com.dv_soft.json.processors.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.dv_soft.json.annatations.JsonField;
import com.dv_soft.json.annatations.JsonFieldCreator;
import com.dv_soft.json.annatations.JsonFieldParser;
import com.dv_soft.json.annatations.JsonGetField;
import com.dv_soft.json.annatations.JsonObject;
import com.dv_soft.json.annatations.JsonOrder;
import com.dv_soft.json.annatations.JsonSetField;
import com.dv_soft.json.processors.Utils;

/**
 * @author Vladimir Baraznovsky
 *
 */
public class JsonParserTemplate extends BaseTemplate {

    private TypeElement mElement;
    private String mName;
    private String mPackageName;
    private String mInstance = "";
    private ProcessingEnvironment mProcessingEnv;

    private Set<String> mImports = new TreeSet<String>();
    private Map<String, String> mCreators = new HashMap<String, String>();
    private Map<String, String> mParsers = new HashMap<String, String>();
    private Map<String, String> mConverter = new HashMap<String, String>();

    public JsonParserTemplate(ProcessingEnvironment pProcessingEnv, TypeElement pElement,
            String pPackageName, String pName) {
        super();
        mProcessingEnv = pProcessingEnv;
        mElement = pElement;
        mPackageName = pPackageName;
        mName = pName;
    }

    public JsonParserTemplate(ProcessingEnvironment pProcessingEnv, TypeElement pElement,
            String pName) {
        super();
        mProcessingEnv = pProcessingEnv;
        mElement = pElement;
        mPackageName = ((PackageElement) pElement.getEnclosingElement()).getQualifiedName()
                .toString();
        mName = pName;
    }

    public JsonParserTemplate(ProcessingEnvironment pProcessingEnv, TypeElement pElement) {
        super();
        mProcessingEnv = pProcessingEnv;
        mElement = pElement;
        mPackageName = Utils.getPackageParser(pElement);
        mName = Utils.getNameParser(pElement);
        JsonObject annotation = pElement.getAnnotation(JsonObject.class);
        mInstance=annotation.instance();
        if((mInstance==null)||(mInstance.isEmpty())){
            mInstance= String.valueOf(mElement.getSimpleName());
        }
    }

    @Override
    protected void printClassBody() {
        initFieldParser();
        println("    private static final %s INSTANCE = new %s();", getClassName(), getClassName());
        println();

        println("    public static %s getInstance() {", getClassName());
        println("       return INSTANCE;");
        println("    }");
        println();
        printGenerateObject();
        printNewArray();
        printFieldsCreator();
        printParse();
        printConvert();
    }

    private void printFieldsCreator() {

        List<? extends Element> elements = mElement.getEnclosedElements();

        for (ExecutableElement element : ElementFilter.methodsIn(elements)) {

            JsonFieldCreator annotation = element.getAnnotation(JsonFieldCreator.class);
            if (annotation != null) {
                printFieldCreator(element, annotation.value());
            }
        }

    }

    private void initFieldParser() {

        List<? extends Element> elements = mElement.getEnclosedElements();

        for (ExecutableElement element : ElementFilter.methodsIn(elements)) {

            JsonFieldParser annotation = element.getAnnotation(JsonFieldParser.class);
            if (annotation != null) {
                String[] value = annotation.value();
                for (String field : value) {
                    mParsers.put(field, element.getSimpleName().toString());
                }

            }
        }
        mConverter.putAll(mParsers);
        for (ExecutableElement element : ElementFilter.methodsIn(elements)) {
            JsonFieldParser annotationConverter = element.getAnnotation(JsonFieldParser.class);
            if (annotationConverter != null) {
                String[] value = annotationConverter.value();
                for (String field : value) {
                    mConverter.put(field, element.getSimpleName().toString());
                }

            }
        }

    }

    @Override
    protected String getSuperName() {
        return String.format("ObjectJson<%s>", mElement.getSimpleName());
    }

    @Override
    public String getClassName() {
        return mName;
    }

    @Override
    public String getPackageName() {
        return mPackageName;
    }

    @Override
    protected void addImports() {
        println();
        addImport("com.dv_soft.json.ObjectJson");
        println();
        addImport("org.json.JSONException");
        addImport("org.json.JSONObject");
        mImports.add(mElement.getQualifiedName().toString());
        if (mImports.size() > 0) {
            println();
            for (String pack : mImports) {
                addImport(pack);
            }

        }
    }

    private void printGenerateObject() {
        println("    @Override");
        println("    public %s createObject() {", mElement.getSimpleName());
        if (hasDefaultConstructor()) {
            println("        return new %s();", mInstance);
        } else {
            println("        throw new RuntimeException(\"Parser can not create Object %s. You must create default constructor\");",
                    mElement.getSimpleName());
        }
        println("    }");
        println();

    }

    private void printNewArray() {
        println("    @Override");
        println("    protected %s[] newArray(int pLength)  {", mElement.getSimpleName());

        println("        return new %s[pLength];", mElement.getSimpleName());
        println("    }");
        println();

    }

    public boolean hasDefaultConstructor() {
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(mElement
                .getEnclosedElements());
        for (ExecutableElement executableElement : constructors) {
            if (executableElement.getParameters().isEmpty()) {
                if (executableElement.getModifiers().contains(Modifier.PRIVATE)) {
                    return false;
                }
                return true;

            }
        }

        return false;
    }

    private void printParse() {
        println("    @Override");
        println("    protected %s doParse(JSONObject pJsonObject, %s pObject) throws JSONException {",
                mElement.getSimpleName(), mElement.getSimpleName());
        List<? extends Element> elements = new ArrayList<Element>(mElement.getEnclosedElements());
        sortElentByOrder(elements);
        for (Element element : elements) {
            JsonSetField annotationMethod = element.getAnnotation(JsonSetField.class);
            JsonField annotationField = element.getAnnotation(JsonField.class);
            boolean isJson = false;
            String jsonType = null;
            String name = null;
            String maskSet = "pObject.%s(%s);";
            boolean ignoreNull = false;
            String fieldName = element.getSimpleName().toString();

            if (annotationMethod != null) {
                isJson = true;
                maskSet = "pObject.%s(%s);";
                ignoreNull = annotationMethod.ignoreNull();
                jsonType = annotationMethod.type().getJsonType();
                name = annotationMethod.value();
            }
            if (annotationField != null) {
                isJson = true;
                ignoreNull = annotationField.ignoreNull();
                jsonType = annotationField.type().getJsonType();
                name = annotationField.value();


                if (element.getModifiers().contains(Modifier.PUBLIC)) {
                    maskSet = "pObject.%s = %s;";

                } else {
                    maskSet = "pObject.set%s(%s);";
                    if (fieldName.matches("m[A-Z][A-Z0-9a-z_]*")) {
                        fieldName = fieldName.substring(1);
                    } else {
                        fieldName = fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);
                    }

                }
            }

            if (isJson) {
                String checkMethod;
                String checkMethodPostfix = "";
                if (ignoreNull) {
                    checkMethod = "isNull";
                    checkMethodPostfix = "!";

                } else {
                    checkMethod = "has";

                }
                println("        if (%spJsonObject.%s(\"%s\")%s){",
                        checkMethodPostfix,
                        checkMethod,
                        name,
                        jsonType != null ? String.format(
                                "&&(pJsonObject.get(\"%s\") instanceof %s)", name, jsonType) : "");

                println("            " + maskSet, fieldName,
                        getSetValue(element, name));
                println("        }");
            }
        }
        println("        return pObject;");
        println("    }");
        println();

    }

    private void sortElentByOrder(List<? extends Element> elements) {
        Comparator<Element> comparator = new Comparator<Element>() {

            @Override
            public int compare(Element pO1, Element pO2) {
                return getOrder(pO1) - getOrder(pO2);
            }

            private int getOrder(Element pO1) {
                int order1 = 0;
                JsonOrder annotation1 = pO1.getAnnotation(JsonOrder.class);
                if (annotation1 != null) {
                    order1 = annotation1.value();
                }
                return order1;
            }
        };
        Collections.sort(elements, comparator);
    }

    private void printConvert() {
        println("    @Override");
        println("    protected void doConvertJson(JSONObject pJsonObject, %s pObject) throws JSONException {",
                mElement.getSimpleName());
        List<? extends Element> elements = new ArrayList<Element>(mElement.getEnclosedElements());
        sortElentByOrder(elements);
        println("        Object value;");
        for (Element element : elements) {
            JsonGetField annotationMethod = element.getAnnotation(JsonGetField.class);
            JsonField annotationField = element.getAnnotation(JsonField.class);
            boolean isJson = false;
            String name = null;
            boolean ignoreNull = false;
            String fieldName = element.getSimpleName().toString();
            String maskGet = null;
            if (annotationField != null) {
                isJson = true;
                ignoreNull = annotationField.ignoreNull();
                name = annotationField.value();
                if (element.getModifiers().contains(Modifier.PUBLIC)) {
                    maskGet = "pObject.%s";

                } else {
                    maskGet = "pObject.get%s()";
                    if (fieldName.matches("m[A-Z][A-Z0-9a-z_]*")) {
                        fieldName = fieldName.substring(1);
                    } else {
                        fieldName = fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);
                    }

                }
            }
            if (annotationMethod != null) {
                isJson = true;
                maskGet = "pObject.%s()";
                ignoreNull = annotationMethod.ignoreNull();
                name = annotationMethod.value();
            }
            if (isJson) {

                String getValue = getGetValue(element, name);
                println("        value = %s;",
                        String.format(getValue, String.format(maskGet, fieldName)));
                println("        if (value != null) {");
                println("            pJsonObject.put(\"%s\", value);", name);

                if (!ignoreNull) {
                    println("        } else {");
                    println("            pJsonObject.put(\"%s\", JSONObject.NULL);", name);
                }
                println("        }");

            }
        }
        println("    }");
        println();

    }

    private String getSetValue(Element pElement, String pNameField) {
        TypeMirror type;
        if (pElement instanceof ExecutableElement) {
        ExecutableElement executableElement = (ExecutableElement) pElement;
            type = executableElement.getParameters().get(0).asType();
        } else {
            type = pElement.asType();
        }

        switch (type.getKind()) {
        case INT:
            return String.format("pJsonObject.getInt(\"%s\")", pNameField);
        case BOOLEAN:
            return String.format("pJsonObject.getBoolean(\"%s\")", pNameField);
        case LONG:
            return String.format("pJsonObject.getLong(\"%s\")", pNameField);
        case FLOAT:
            return String.format("(float) pJsonObject.getDouble(\"%s\")", pNameField);


        case DOUBLE:
            return String.format("pJsonObject.getDouble(\"%s\")", pNameField);
        case ARRAY:
            TypeMirror typeArray = ((ArrayType) type).getComponentType();

            if (typeArray instanceof DeclaredType) {
                String creator = mCreators.get(pNameField);
                String parser = reguestParserInstance(typeArray, pNameField, mParsers);
                return String.format("%s.parseArray(pJsonObject.getJSONArray(\"%s\")%s)", parser,
                        pNameField, creator != null ? String.format(", %s(pObject)", creator) : "");
            }
        case DECLARED:
            if (isType(type, "java.lang.String")) {
                return String.format("pJsonObject.getString(\"%s\")", pNameField);
            }
            if (isType(type, "java.lang.Integer")) {
                return String.format("pJsonObject.getInt(\"%s\")", pNameField);
            }
            if (isType(type, "java.lang.Float")) {
                return String.format("(float) pJsonObject.getDouble(\"%s\")", pNameField);
            }
            if (isType(type, "java.lang.Double")) {
                return String.format("pJsonObject.getDouble(\"%s\")", pNameField);
            }
            DeclaredType declaredType = (DeclaredType) type;
            String creator = mCreators.get(pNameField);
            if (Utils.isClass(declaredType, "java.util.List")) {

                TypeMirror typeList = declaredType.getTypeArguments().get(0);
                if (typeList instanceof DeclaredType) {

                    String parser = reguestParserInstance(typeList, pNameField, mParsers);
                    return String.format("%s.parseList(pJsonObject.getJSONArray(\"%s\")%s)",
                            parser, pNameField,
                            creator != null ? String.format(", %s(pObject)", creator) : "");
                }

            }
            if (Utils.isClass(declaredType, "java.util.Map")) {

                TypeMirror typeList = declaredType.getTypeArguments().get(1);
                if (typeList instanceof DeclaredType) {

                    String parser = reguestParserInstance(typeList, pNameField, mParsers);
                    return String.format(
"%s.parseMap(pJsonObject.getJSONObject(\"%s\")%s)",
                            parser, pNameField,
                            creator != null ? String.format(", %s(pObject)", creator) : "");
                }

            }

            String parser = reguestParserInstance(declaredType, pNameField, mParsers);
            if (creator==null){
                return String.format("%s.parseFromObject(pJsonObject,\"%s\")",
                        parser, pNameField);
            }
            return String.format("%s.parse(pJsonObject.getJSONObject(\"%s\"), %s(pObject))",
                    parser, pNameField, creator);

            // return "null /* " + type.toString() + "*/";

        default:
            break;
        }
        return "null /* " + type.toString() + "*/";
    }

    private String getGetValue(Element pElement, String pNameField) {
            TypeMirror type;
            if (pElement instanceof ExecutableElement) {
            ExecutableElement executableElement = (ExecutableElement) pElement;
                type = executableElement.getReturnType();
            } else {
                type = pElement.asType();
            }

            switch (type.getKind()) {
        case FLOAT:
            return "(double)%s";
        case INT:
        case BOOLEAN:
        case LONG:
        case DOUBLE:
            return "%s";
        case ARRAY:
            return String.format("%s.toJson(%%s)",
                    reguestParserInstance(((ArrayType) type).getComponentType(), pNameField,
                            mConverter));
        case DECLARED:
            if (isType(type, "java.lang.String")) {
                return "%s";
            }
            if (isType(type, "java.lang.Float")) {
                return "%s";
            }
            if (isType(type, "java.lang.Integer")) {
                return "%s";
            }
            if (isType(type, "java.lang.Double")) {
                return "%s";
            }
            DeclaredType declaredType = (DeclaredType) type;
            if (Utils.isClass(declaredType, "java.util.List")) {

                TypeMirror typeList = declaredType.getTypeArguments().get(0);
                if (typeList instanceof DeclaredType) {
                    String parser = reguestParserInstance(typeList, pNameField, mConverter);
                    return String.format("%s.toJson(%%s)", parser);
                }

            }
            if (Utils.isClass(declaredType, "java.util.Map")) {

                TypeMirror typeList = declaredType.getTypeArguments().get(1);
                if (typeList instanceof DeclaredType) {
                    String parser = reguestParserInstance(typeList, pNameField, mConverter);
                    return String.format("%s.toJson(%%s)", parser);
                }

            }

            String converter = mConverter.get(pNameField);
            if (converter != null) {
                return String.format("pObject.%s().toJsonField(%%s)", converter);
            }
            String parser = reguestParserInstance(declaredType, pNameField, mConverter);
            return String.format("%s.toJson(%%s)", parser);

            // return "null /* " + type.toString() + "*/";

        default:
            break;
        }

        return "%s /* " + type.toString() + "*/";

    }


    private void printFieldCreator(ExecutableElement pElement, String... pFields) {
        TypeMirror returnType = pElement.getReturnType();
        mImports.add("com.dv_soft.json.IJsonObjectCreator");
        String fieldType;
        if (returnType instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) returnType;

            mImports.add(returnType.toString());
            fieldType = declaredType.asElement().getSimpleName().toString();
        } else {
            fieldType = returnType.toString();
        }

        String filedName = String.format("create%s%sCreator",
                Character.toUpperCase(pFields[0].charAt(0)), pFields[0].substring(1));
        String methodName = pElement.getSimpleName().toString();
        println("    public IJsonObjectCreator<%s> %s(final %s pObject) {", fieldType,
                filedName, mElement.getSimpleName());
        println("       return new IJsonObjectCreator<%s>() {", fieldType);
        println();
        println("            @Override");
        println("            public %s createObject() {", fieldType);
        println("               return pObject.%s();", methodName);
        println("            }");
        println("       };");
        println("    }");
        println();

        for (String field : pFields) {
            mCreators.put(field, filedName);
        }
    }

    private String reguestParser(TypeMirror typeList) {
        Element element = ((DeclaredType) typeList).asElement();
        mImports.add(Utils.getFullParser(element));
        String parser = Utils.getNameParser(element);
        return parser;
    }

    private String reguestParserInstance(TypeMirror typeList, String pField,Map<String, String> pMethods) {
        String parser = pMethods.get(pField);
        if (parser != null) {
            return String.format("pObject.%s()", parser);
        }
        return reguestParser(typeList) + ".getInstance()";
    }

    private boolean isType(TypeMirror pType, String pName) {
        TypeElement newType = mProcessingEnv.getElementUtils().getTypeElement(pName);
        return newType != null ? mProcessingEnv.getTypeUtils().isSameType(pType, newType.asType())
                : false;

    }

    @Deprecated
    private boolean isSuper(TypeMirror pType, String pName) {
        TypeElement newType = mProcessingEnv.getElementUtils().getTypeElement(pName);
        return newType != null ? mProcessingEnv.getTypeUtils().isSubtype(newType.asType(), pType)
                : false;

    }

}
