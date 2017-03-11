/**
 *
 */
package com.dv_soft.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Vladimir Baraznovsky
 *
 * @param <T>
 */
public abstract class BaseJson<T extends Object> {

    public List<T> parseList(JSONArray pArray) throws JSONException {
        List<T> list = new ArrayList<T>(pArray.length());
        for (int i = 0; i < pArray.length(); i++) {
            list.add(parseFromList(pArray, i));
        }
        return list;
    }

    public Map<String, T> parseMap(JSONObject pObject) throws JSONException {
        Map<String, T> map = new HashMap<String, T>(pObject.length());
        JSONArray names = pObject.names();

        for (int i = 0; i < names.length(); i++) {
            String name = names.getString(i);
            map.put(name, parseFromObject(pObject, name));
        }

        return map;
    }

    public abstract Object toJson(T pValue) throws JSONException;


    public JSONArray toJson(List<T> pList) throws JSONException {
        if (pList == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (T value : pList) {
            jsonArray.put(toJson(value));
        }
        return jsonArray;
    }

    public Object toJsonField(Object pObject) throws JSONException {

        return toJson((T) pObject);
    }

    public JSONArray toJson(T[] pList) throws JSONException {
        if (pList == null) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (T value : pList) {
            jsonArray.put(toJson(value));
        }
        return jsonArray;
    }

    public T[] parseArray(JSONArray pArray)
            throws JSONException {

        T[] list = newArray(pArray.length());
        for (int i = 0; i < pArray.length(); i++) {
            list[i] = parseFromList(pArray, i);
        }
        return list;
    }


    public JSONObject toJson(Map<? extends String, ? extends T> pMap) throws JSONException {
        if (pMap == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        Set<? extends String> keySet = pMap.keySet();
        for (String key : keySet) {
            jsonObject.put(key, toJson(pMap.get(key)));
        }
        return jsonObject;
    }

    protected abstract T[] newArray(int pLength);

    public abstract T parseFromList(JSONArray pArray, int pIndex) throws JSONException;

    public abstract T parseFromObject(JSONObject pObject, String pName) throws JSONException;

}