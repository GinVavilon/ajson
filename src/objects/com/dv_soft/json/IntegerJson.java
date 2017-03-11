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
public class IntegerJson extends BaseJson<Integer> {

    private static final IntegerJson INSTANCE = new IntegerJson();

    public static IntegerJson getInstance() {
        return INSTANCE;
    }

    @Override
    public Integer parseFromList(JSONArray pArray, int pIndex) throws JSONException {
        return pArray.getInt(pIndex);
    }

    @Override
    public Integer parseFromObject(JSONObject pObject, String pName) throws JSONException {
        return pObject.getInt(pName);
    }

    @Override
    public Integer toJson(Integer pValue) {
        return pValue;
    }

    @Override
    protected Integer[] newArray(int pLength) {
        return new Integer[pLength];
    }

}
