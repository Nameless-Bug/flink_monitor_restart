package com.nameless.monitor_restart.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class JsonUtil {

    private JsonUtil(){}

    public static String getCheckpoint(String historyJsonInfo){
        JSONObject obj = JSONObject.parseObject(historyJsonInfo);
        JSONObject latest = obj.getJSONObject("latest");
        JSONObject completed = latest.getJSONObject("completed");
        return completed.getString("external_path");
    }

    public static <T> List<T> getJobList(String jobsInfoJson, Class<T> tClass){
        JSONObject obj = JSONObject.parseObject(jobsInfoJson);
        JSONArray jobs = obj.getJSONArray("jobs");
        String js = JSONObject.toJSONString(jobs);
        return JSONObject.parseArray(js,tClass);
    }

}
