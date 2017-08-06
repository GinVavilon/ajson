/**
 *
 */
package org.json;

import com.github.ginvavilon.ajson.BaseJson;

/**
 * @author Vladimir Baraznovsky
 *
 */
public class JSONObjectJson extends BaseJson<JSONObject> {

    private static final JSONObjectJson INSTANCE = new JSONObjectJson();

    public static JSONObjectJson getInstance() {
        return INSTANCE;
    }

    @Override
    public Object toJson(JSONObject pValue) throws JSONException {
        return pValue;
    }

    @Override
    protected JSONObject[] newArray(int pLength) {
        return new JSONObject[pLength];
    }

    @Override
    public JSONObject parseFromList(JSONArray pArray, int pIndex) throws JSONException {
        return pArray.getJSONObject(pIndex);
    }

    @Override
    public JSONObject parseFromObject(JSONObject pObject, String pName) throws JSONException {
        return pObject.getJSONObject(pName);
    }


}
