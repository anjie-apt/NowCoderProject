package com.company.toutiao.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class TouTiaoUtil {

    private static final Logger logger = LoggerFactory.getLogger(TouTiaoUtil.class);

    public static String MD5(String password) {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
        return password;

    }

    public static String getJSONString(int code){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()){
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toJSONString();
    }
}
