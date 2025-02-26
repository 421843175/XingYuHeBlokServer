package com.jupiter.myblok.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.util.AjaxResult;

public interface AdminService {
    AjaxResult<String> toLogin(String username,String password);

    AjaxResult<JSONArray> artilelist(JSONObject pagination);

}
