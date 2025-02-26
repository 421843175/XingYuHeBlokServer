package com.jupiter.myblok.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.pojo.PO.MyCodeList;
import com.jupiter.myblok.pojo.PO.Record;
import com.jupiter.myblok.service.AdminService;
import com.jupiter.myblok.service.Impl.FileServiceImpl;
import com.jupiter.myblok.service.NoteServer;
import com.jupiter.myblok.service.RecordService;
import com.jupiter.myblok.service.WebServer;
import com.jupiter.myblok.util.AjaxResult;
import com.jupiter.myblok.util.EncryTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    NoteServer noteServer;

    @Autowired
    RecordService recordService;


    @Autowired
    WebServer webServer;
    @PostMapping("/login")
    public AjaxResult<String> login(String username,String password) throws Exception {

        String encodepassword = EncryTool.decrypt(password, "JupiterAdminSyst");

        return adminService.toLogin(username,encodepassword);
    }

    //保存文章
    @PostMapping("/saveArticle")
    public AjaxResult<String> saveArticle(@RequestBody JSONObject article) throws Exception {
        return noteServer.saveArticle(article);
    }


    //        不好用的办法
//        http://127.0.0.1:8081/article/price/img/a.png
//        图片上传直接上传到 根目录下的 img  然后 从里面可以读取 x.png

    //        我们上传图片 .contain 标签下文件名 是否有对应的文件夹 有的话 返回文件夹名 然后把随机的.png 给它 返回 相对路径
    @PostMapping("/upload")
    public AjaxResult<String> upload(@RequestParam("file") MultipartFile file,@RequestParam("key") String key){
        System.out.println("key="+key);
        try {
          return  fileService.upload(file,key);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/artilelist")
    public AjaxResult<JSONArray> artilelist(@RequestBody JSONObject pagination)  {

        return adminService.artilelist(pagination);
    }


    //后台获取文章
    @GetMapping("/getArticleById")
    public AjaxResult<JSONObject> getArticleById(int id) throws Exception {
        System.out.println("v+="+id);
        return noteServer.getArticleById(id);
    }

//    新增标签
    @PostMapping("/saveSort")
    public AjaxResult<String> saveSort(@RequestBody JSONObject sortForHttp) throws Exception {
        return noteServer.saveSort(sortForHttp);
    }

    //隐藏文章
    @GetMapping("/deleteArticle")
    public AjaxResult<String> deleteArticle(int id) throws Exception {
        return noteServer.deleteArticle(id);
    }

    //更新标签
    @PostMapping("/updateSort")
    public AjaxResult<String> updateSort(@RequestBody JSONObject labelForHttp) throws Exception {
        return noteServer.updateSort(labelForHttp);
    }


//新增一条说说
    @PostMapping("/saveSpace")
    public AjaxResult<String> saveSpace(@RequestBody Record space) throws Exception {
        return recordService.saveSpace(space);
    }

    //修改一条说说
    @PostMapping("/updateSpace")
    public AjaxResult<String> updateSpace(@RequestBody Record space) throws Exception {
        return recordService.updateSpace(space);
    }

    @GetMapping("/deleteRecord")
    public AjaxResult<String> deleteRecord(int id) throws Exception {
        return recordService.deleteRecord(id);
    }

    //新增一条COLI
    @PostMapping("/saveColi")
    public AjaxResult<String> saveColi(@RequestBody MyCodeList myCodeList) throws Exception {
        return webServer.saveColi(myCodeList);
    }

    //修改一条COLI
    @PostMapping("/updateColi")
    public AjaxResult<String> updateColi(@RequestBody MyCodeList myCodeList) throws Exception {
        return webServer.updateColi(myCodeList);
    }

    @GetMapping("/deleteColi")
    public AjaxResult<String> deleteColi(int id) throws Exception {
        return webServer.deleteColi(id);
    }

}
