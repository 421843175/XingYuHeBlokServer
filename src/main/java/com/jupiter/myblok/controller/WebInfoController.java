package com.jupiter.myblok.controller;


import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.service.NoteServer;
import com.jupiter.myblok.service.WebServer;
import com.jupiter.myblok.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/webInfo")
@CrossOrigin("*")
public class WebInfoController {

    @Autowired
    NoteServer fileServer;

    @Autowired
    WebServer webServer;

    @GetMapping("/getSortInfo")
    public AjaxResult<String> sortInfo() {
        System.out.println("go");
         AjaxResult<String> fileDir = fileServer.getFileDir();
        return fileDir;
    }


    @GetMapping("/getAdmire")
    public AjaxResult<Integer> getAdmire(){
        return webServer.getAdmire();
    }

    @GetMapping("/giveAdmire")
    public AjaxResult<Integer> giveAdmire(HttpServletRequest httpRequest){
        return webServer.giveAdmire(httpRequest);
    }

    @GetMapping("/listShare")
    public AjaxResult<List> listShare(){
        return webServer.listShare();
    }

    @GetMapping("/listMyCode")
    public AjaxResult<JSONObject> listMyCode(){
        return webServer.listMyCode();
    }

    @GetMapping("/listSortAndLabel")
    public AjaxResult<JSONObject> getlistSortAndLabel(HttpServletRequest request){
        return webServer.getlistSortAndLabel();
    }

}
