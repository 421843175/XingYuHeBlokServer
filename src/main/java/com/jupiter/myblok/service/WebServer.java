package com.jupiter.myblok.service;

import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.pojo.PO.MyCodeList;
import com.jupiter.myblok.util.AjaxResult;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface WebServer {
    AjaxResult<Integer> getAdmire();



    AjaxResult<Integer> giveAdmire(HttpServletRequest httpRequest);

    AjaxResult<List> listShare();

    AjaxResult<JSONObject> listMyCode();

    AjaxResult<JSONObject> getlistSortAndLabel();


    AjaxResult<String> saveColi(MyCodeList myCodeList);

    AjaxResult<String> updateColi(MyCodeList myCodeList);

    AjaxResult<String> deleteColi(int id);

}

