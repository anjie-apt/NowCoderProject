package com.company.toutiao.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 专门用来做视图展示的对象，内部只使用HashMap做简单的映射
 */
public class ViewObject {
    private Map<String, Object> objectMap = new HashMap<>();

    public void set(String key, Object value){
        objectMap.put(key, value);
    }

    public Object get(String key){
        return objectMap.get(key);
    }
}
