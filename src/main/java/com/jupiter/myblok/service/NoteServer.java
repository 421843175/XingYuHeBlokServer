package com.jupiter.myblok.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.util.AjaxResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface NoteServer {
     AjaxResult<String> getFileDir();


     AjaxResult<JSONArray> getAllArticle();

     AjaxResult<JSONObject> getArticleById(int id);


     AjaxResult<JSONObject> listArticle(JSONObject pagination);

    AjaxResult<String> saveArticle(JSONObject article);


    AjaxResult<String> saveSort(JSONObject sortForHttp);

    AjaxResult<String> deleteArticle(int id);

    AjaxResult<String> updateSort(JSONObject labelForHttp);
}
