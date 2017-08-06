/**
 *
 */
package com.github.ginvavilon.ajson;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Vladimir Baraznovsky
 *
 */
public abstract class ObjectJson<T> extends BaseJson<T> implements IJsonObjectCreator<T> {

    protected abstract T doParse(JSONObject pJsonObject, T pObject) throws JSONException;

    protected abstract void doConvertJson(JSONObject pJsonObject, T pValue) throws JSONException;

    public T parse(JSONObject pJsonObject, T pObject){
        try {
            return doParse(pJsonObject, pObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public T parse(JSONObject pJsonObject){
        return parse(pJsonObject, this);
    }

    public T parse(JSONObject pJsonObject, IJsonObjectCreator<? extends T> pCreator) {
        return parse(pJsonObject, pCreator.createObject());
    }

    @Override
    public JSONObject toJson(T pValue) throws JSONException {
        if (pValue == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        doConvertJson(jsonObject, pValue);
        return jsonObject;
    }


    public List<T> parseList(JSONArray pArray, IJsonObjectCreator<? extends T> pCreator)
            throws JSONException {
        List<T> list=new ArrayList<T>(pArray.length());
        for (int i = 0; i < pArray.length(); i++) {
            list.add(parse(pArray.getJSONObject(i),pCreator));
        }
        return list;
    }

    public T[] parseArray(JSONArray pArray, IJsonObjectCreator<? extends T> pCreator)
            throws JSONException {

        T[] list = newArray(pArray.length());
        for (int i = 0; i < pArray.length(); i++) {
            list[i] = parse(pArray.getJSONObject(i), pCreator);
        }
        return list;
    }


    @Override
    public T parseFromList(JSONArray pArray, int pI) throws JSONException {
        return parse(pArray.getJSONObject(pI));
    }

    @Override
    public T parseFromObject(JSONObject pObject, String pName) throws JSONException {
        return parse(pObject.getJSONObject(pName));
    }


}
