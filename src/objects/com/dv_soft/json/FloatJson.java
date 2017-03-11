/**
 *
 */
package com.dv_soft.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Vladimir Baraznovsky
 *
 */
public class FloatJson extends BaseJson<Float> {

    @Override
    public Float toJson(Float pValue) throws JSONException {
        return pValue;
    }

    @Override
    protected Float[] newArray(int pLength) {
        return new Float[pLength];
    }

    @Override
    public Float parseFromList(JSONArray pArray, int pIndex) throws JSONException {
        return (float)pArray.getDouble(pIndex);
    }

    @Override
    public Float parseFromObject(JSONObject pObject, String pName) throws JSONException {
        return (float) pObject.getDouble(pName);
    }

}
