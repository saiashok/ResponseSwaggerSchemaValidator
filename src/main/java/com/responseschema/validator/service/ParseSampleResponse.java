package com.responseschema.validator.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseSampleResponse {

    private List<String> pathList;
    private Map<String,Object> getElements = new TreeMap <String, Object>();
    private String json;

    

	public ParseSampleResponse(String json) throws JSONException {
        this.json = json;
        this.pathList = new ArrayList<String>();
        setJsonPaths(json);
    }

    public List<String> getPathList() {
        return this.pathList;
    }
    
    public Map<String,Object> getKeyValue(){
    	return getElements;
    }

    private void setJsonPaths(String json) throws JSONException {
        this.pathList = new ArrayList<String>();
        JSONObject object = new JSONObject(json);
        String jsonPath = "$";
        if(json != JSONObject.NULL) {
            readObject(object, jsonPath);
        }   
    }

    private void readObject(JSONObject object, String jsonPath) throws JSONException {
        Iterator<String> keysItr = object.keys();
        String parentPath = jsonPath;
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            jsonPath = parentPath + "." + key;

            if(value instanceof JSONArray) {            
                readArray((JSONArray) value, jsonPath+"[A]");
            }
            else if(value instanceof JSONObject) {
                readObject((JSONObject) value, jsonPath+"(O)");
            } else { // is a value
            	getElements.put(jsonPath,value );
//            	System.out.println(jsonPath);
                this.pathList.add(jsonPath);    
            }          
        }  
    }

    private void readArray(JSONArray array, String jsonPath) throws JSONException {      
        String parentPath = jsonPath;
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);        
            jsonPath = parentPath;

            if(value instanceof JSONArray) {
                readArray((JSONArray) value, jsonPath+"[A]");
            } else if(value instanceof JSONObject) {                
                readObject((JSONObject) value, jsonPath+"(O)");
            } else { // is a value
            //	System.out.println(jsonPath);
                this.pathList.add(jsonPath);
            }       
        }
    }

}

