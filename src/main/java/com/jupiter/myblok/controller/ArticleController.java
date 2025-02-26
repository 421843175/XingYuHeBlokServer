package com.jupiter.myblok.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.publiccontext.PublicMemory;
import com.jupiter.myblok.service.Impl.FileServiceImpl;
import com.jupiter.myblok.service.NoteServer;
import com.jupiter.myblok.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/article")
@CrossOrigin("*")
public class ArticleController {
    @Autowired
    NoteServer fileServer;

    @Autowired
    FileServiceImpl fileServiceImpl;

    @GetMapping("/listSortArticle")
    public AjaxResult<JSONArray> listSortArticle(){
            return fileServer.getAllArticle();
    }


    @GetMapping("/getArticleById")
    public AjaxResult<JSONObject> getArticleById(int id,String password){
        System.out.println("id="+id+",password="+password);
        return fileServer.getArticleById(id);
    }


    @PostMapping("/listArticle")
    public AjaxResult<JSONObject> listArticle(@RequestBody JSONObject pagination){
      //只做sortId


        return fileServer.listArticle(pagination);
    }


//    请求图片的接口
    @GetMapping("/price/**")
    public void getPrice(HttpServletResponse response, HttpServletRequest request) throws IOException {


        // 获取请求路径
        String requestPath = request.getRequestURI();
         File pricePath = fileServiceImpl.getPricePath(requestPath);

        fileServiceImpl.getPrice(response,pricePath);


    }





}
