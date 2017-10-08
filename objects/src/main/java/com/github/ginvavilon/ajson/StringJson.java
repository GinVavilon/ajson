/**
 *
 */
package com.github.ginvavilon.ajson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Vladimir Baraznovsky
 *
 */
public class StringJson extends BaseJson<String> {

    private static final StringJson INSTANCE = new StringJson();

    public static StringJson getInstance() {
        return INSTANCE;
    }

    @Override
    public String parseFromList(JSONArray pArray, int pIndex) throws JSONException {
        return pArray.getString(pIndex);
    }

    @Override
    public String parseFromObject(JSONObject pObject, String pName) throws JSONException {
        return pObject.getString(pName);
    }

    @Override
    public String toJson(String pValue) {
        return pValue;
    }

    @Override
    protected String[] newArray(int pLength) {
        return new String[pLength];
    }

}
