package com.yunbao.common.utils;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class JsonUtil {


    /**
     * 功能描述：把JSON数据转换成指定的java对象
     * @param jsonData JSON数据
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    public static <T> T getJsonToBean(String jsonData, Class<T> clazz) {
        if(jsonData==null)
            return null;

        return JSON.parseObject(jsonData, clazz);
    }

    /**
     * 功能描述：把java对象转换成JSON数据
     * @param object java对象
     * @return JSON数据
     */
    public static String getBeanToJson(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 功能描述：把JSON数据转换成指定的java对象列表
     * @param jsonData JSON数据
     * @param clazz 指定的java对象
     * @return List<T>
     */
    public static <T> List<T> getJsonToList(String jsonData, Class<T> clazz) {
        if(jsonData==null)
            return null;
        try {
            return JSON.parseArray(jsonData, clazz);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 功能描述：把JSON数据转换成较为复杂的List<Map<String, Object>>
     * @param jsonData JSON数据
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getJsonToListMap(String jsonData) {
        if(jsonData==null)
            return null;

        return JSON.parseObject(jsonData, new TypeReference<List<Map<String, Object>>>() {
        });
    }



    public static String getString(String data,String key){
        try {
            org.json.JSONObject jsonObject=new org.json.JSONObject(data);
            return jsonObject.getString(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static int getInt(String data,String key){
        try {
            org.json.JSONObject jsonObject=new org.json.JSONObject(data);
            return jsonObject.getInt(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
