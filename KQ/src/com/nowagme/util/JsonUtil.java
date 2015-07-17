package com.nowagme.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

	/**
	 * 解析json.
	 * 
	 * @param jsonStr
	 * @return Map<String,Object>
	 *         Map中的Object分2种情况，1种是String,另一种是List<Map<String,Object>>
	 * @throws Exception
	 */
	public static Map<String, Object> parse(String jsonStr) throws Exception {
		JSONObject json = JSON.parseObject(jsonStr);
		Map<String, Object> data = new HashMap<String, Object>();
		parse(json, data);
		return data;
	}

	/**
	 * 解析JSONObject.
	 * 
	 * @param json
	 * @throws Exception
	 */
	private static void parse(JSONObject json, Map<String, Object> data)
			throws Exception {
		for (String key : json.keySet()) {
			Object obj = json.get(key);
			if (obj instanceof String) {//普通字符串
				data.put(key, (String) obj);
			} else if (obj instanceof JSONArray) {//[]括起来的数组
				data.put(key, null);
				parse((JSONArray) obj, key, data);
			} else if(obj instanceof JSONObject){//{}括起来的对象
				data.put(key, null);
				parse((JSONObject) obj, key, data);
			}
		}
	}
	
	/**
	 * 解析JSONObject.
	 * 
	 * @param jsonObject
	 * @param key
	 * @param data
	 * @throws Exception
	 */
	private static void parse(JSONObject json, String parentKey,
			Map<String, Object> data) throws Exception {
		Map<String, Object> mapData = new HashMap<String, Object>();
		parse(json,mapData);
		data.put(parentKey, mapData);
	}

	/**
	 * 解析JSONArray.
	 * 
	 * @param array
	 * @param key
	 * @param data
	 * @throws Exception
	 */
	private static void parse(JSONArray array, String parentKey,
			Map<String, Object> data) throws Exception {
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		int size = array.size();
		for (int i = 0; i < size; i++) {
			JSONObject json = (JSONObject) array.get(i);
			Map<String, Object> childData = new HashMap<String, Object>();
			parse(json, childData);
			listData.add(childData);
		}
		data.put(parentKey, listData);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
