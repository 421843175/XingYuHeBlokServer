package com.jupiter.myblok.controller;

import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.service.RecordService;
import com.jupiter.myblok.service.WebServer;
import com.jupiter.myblok.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/record")
@CrossOrigin("*")
public class RecordController {

    @Autowired
    RecordService recordService;



    @PostMapping("/listRecord")
    public AjaxResult<JSONObject> listRecord(@RequestBody JSONObject pagination){
        return recordService.listRecord(pagination);

    }


    @GetMapping("/goodNum")
    public AjaxResult<Integer> goodNum(Integer id, HttpServletRequest httpRequest){
        return recordService.goodNum(id,httpRequest);

    }


    //破QQ防盗链
    @GetMapping("/poFang")
    public void poFang(HttpServletResponse response, String url){
        try {
             recordService.poFang(response,url);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
