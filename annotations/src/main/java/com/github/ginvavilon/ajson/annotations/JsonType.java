/**
 *
 */
package com.github.ginvavilon.ajson.annotations;

/**
 * @author Vladimir Baraznovsky
 * 
 */
public enum JsonType {
    AUTO, ARRAY("org.json.JSONArray"), STRING(String.class), DOUBLE(Double.class), INT(Integer.class), OBJECT("org.json.JSONObject"), LONG(
            Long.class), BOOLEAN(Boolean.class), NONE, MAP("org.json.JSONObject");
    private final String mJsonType;

    private JsonType(String pJsonType) {
        mJsonType = pJsonType;
    }

    private JsonType(Class<?> pJsonType) {
        mJsonType = pJsonType.getName();
    }

    private JsonType() {
        mJsonType = null;
    }

    public String getJsonType() {
        return mJsonType;
    }
}
