package com.nowagme.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;



public class Test {
	
	/**
	 * 解析 json .
	 * @throws Exception
	 */
	public static void parseJson() throws Exception{
		String jsonStr = "{\"result_code\":\"0\",\"id\":\"1\",\"live_province\":\"\",\"birth\":\"\",\"sex\":\"0\",\"nickname\":\"\u5468\u0078\u0078\",\"tag\":\"\u5927\u54e5\u002c\u516b\u7237\",\"live_country\":\"\",\"faceimg\":\"http://fb.nowagame.com/img/default_head_base.png\",\"native_province\":\"\",\"time\":\"\",\"native_country\":\"\",\"veteran\":\"3\",\"dis\":\"\",\"live_city\":\"\",\"place\":\"\u8fb9\u950b\u002c\u8fb9\u524d\u536b\",\"industry\":\"\u91d1\u878d\u002f\u94f6\u884c\u002f\u6295\u8d44\u002f\u4fdd\u9669\",\"signature\":\"\u6211\u7231\u8db3\u7403\u7684\",\"native_city\":\"\",\"teams\":[{\"id\":\"1\",\"name\":\"\u4e0a\u6d77\u5927\u4f17\u961f\",\"faceimg\":\"http://fb.nowagame.com/img/default_team.png\",\"membercount\":\"4\",\"place1_addr\":\"\",\"place2_addr\":\"\",\"component\":\"\u8001\u4e61\"}],\"datas\":[{\"id\":\"0\",\"name\":\"0\",\"faceimg\":\"0\",\"total\":\"1\",\"total_jq\":\"0\",\"avarge_jq\":\"0\",\"win_rate\":\"0%\",\"total_zg\":\"0\",\"avarge_zg\":\"0\"},{\"id\":\"1\",\"name\":\"\u4e0a\u6d77\u5927\u4f17\u961f\",\"faceimg\":\"http://fb.nowagame.com/img/default_team.png\",\"total\":\"1\",\"total_jq\":\"0\",\"avarge_jq\":\"0\",\"win_rate\":\"0%\",\"total_zg\":\"0\",\"avarge_zg\":\"0\"}]}";
//		String jsonStr = "{\"result_code\":\"0\",\"id\":\"1\",\"nickname\":\"\u5468\u0078\u0078\",\"faceimg\":\"http://fb.nowagame.com/img/default_head_base.png\"}";
		System.out.println("JSON STR="+jsonStr);
		JSONObject json = JSON.parseObject(jsonStr);
		Map<String,Object> data = new HashMap<String,Object>();
		parseJson(json,data);
		//显示data的数据信息
		System.out.println("---------DATA PRINT----------------------");
		show(data);
	}
	
	@SuppressWarnings("unchecked")
	public static void show(Map<String,Object> data) throws Exception{
		for(String key:data.keySet()){
			Object obj = data.get(key);
			if(obj instanceof String){
				System.out.println(key+"="+data.get(key));
			}else if(obj instanceof List){
				List<Map<String,Object>> listData = (List<Map<String,Object>>)obj;
				int size = (listData==null)?0:listData.size();
				System.out.println(key+":"+size);
				for(Map<String,Object> m:listData){
					show(m);
				}
			}
		}
	}
	
	
	/**
	 * 解析JSONObject.
	 * @param json
	 * @throws Exception
	 */
	public static void parseJson(JSONObject json,Map<String,Object> data) throws Exception{
		int size = json.size();
		System.out.println("json.size()="+size);
		System.out.println("-------------------------------");
		for(String key:json.keySet()){
			Object obj = json.get(key);
			if(obj instanceof String){
				data.put(key, (String)obj);
				System.out.println("STRING:"+key+"="+(String)obj);
			}else if(obj instanceof JSONArray){
				data.put(key, null);
				System.out.println("JSONArray:"+key);
				parseJson((JSONArray)obj,key,data);
			}
		}
	}
	
	/**
	 * 解析JSONArray.
	 * @param array
	 * @throws Exception
	 */
	public static void parseJson(JSONArray array,String key,Map<String,Object> data) throws Exception{
		List<Map<String,Object>> listData = new ArrayList<Map<String,Object>>();
		System.out.println("********ARRAY START*******");
		int size = array.size();
		System.out.println("array.size()="+size);
		System.out.println("********ARRAY END*******");
		for(int i=0;i<size;i++){
			JSONObject json =(JSONObject)array.get(i);
			Map<String,Object> childData = new HashMap<String,Object>();
			parseJson(json,childData);
			listData.add(childData);
		}
		data.put(key, listData);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("start....");
		try {
			parseJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end.");
	}

}
