package com.jupiter.myblok.service;

import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.pojo.PO.Record;
import com.jupiter.myblok.util.AjaxResult;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface RecordService {
    AjaxResult<JSONObject> listRecord(JSONObject pagination);

    AjaxResult<Integer> goodNum(Integer id, HttpServletRequest httpRequest);

    void poFang(HttpServletResponse response, String url) throws Exception;

    AjaxResult<String> saveSpace(Record space);

    AjaxResult<String> updateSpace(Record space);


    AjaxResult<String> deleteRecord(int id);

}

