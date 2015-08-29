package com.celink.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil {
	private static ObjectMapper om = new ObjectMapper();
	private static JsonFactory f = null;

	public static String getJson(Object object) throws JsonGenerationException,
			JsonMappingException, IOException {
		return om.writeValueAsString(object);
	}

	public static <T> T getBean(Class<T> EntityClass, String json)
			throws JsonParseException, JsonMappingException, IOException {
		return om.readValue(json, EntityClass);
	}

	public static <T> List<T> readJson2Objs(String json, Class<T> clazz) throws JsonParseException, IOException {
		System.out.println(json);
		T t;
		List<T> retList = new ArrayList<T>();
		f = new JsonFactory();
		JsonParser jp = f.createJsonParser(json);
		jp.nextToken();
		while (jp.nextToken() == JsonToken.START_OBJECT) {
			t = om.readValue(jp, clazz);
			retList.add(t);
		}
		jp.close();
		return retList;
	}
	
	public static List<Map<String, Object>> parseJSON2List(String jsonStr){  
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);  
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
        Iterator<JSONObject> it = jsonArr.iterator();  
        while(it.hasNext()){  
            JSONObject json2 = it.next();  
            list.add(parseJSON2Map(json2.toString()));  
        }  
        return list;  
    }  
      
     
    public static Map<String, Object> parseJSON2Map(String jsonStr) throws JSONException {  
        Map<String, Object> map = new HashMap<String, Object>();  
        //最外层解析  
        JSONObject json = JSONObject.fromObject(jsonStr);  
        for(Object k : json.keySet()){  
            Object v = json.get(k);   
            //如果内层还是数组的话，继续解析  
            if(v instanceof JSONArray){  
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
                Iterator<JSONObject> it = ((JSONArray)v).iterator();  
                while(it.hasNext()){  
                    JSONObject json2 = it.next();  
                    list.add(parseJSON2Map(json2.toString()));  
                }  
                map.put(k.toString(), list);  
            } else {  
                map.put(k.toString(), v);  
            }  
        }  
        return map;  
    }  
    
    public static Map<String, Object> parseJSON2Map1(String jsonStr) throws JSONException {  
    	Map<String, Object> map = new HashMap<String, Object>();  
    	//最外层解析  
    	JSONObject json = JSONObject.fromObject(jsonStr);  
    	for(Object k : json.keySet()){  
    		Object v = json.get(k);   
    		map.put(k.toString(), v); 
    	}  
    	return map;  
    }  
}
