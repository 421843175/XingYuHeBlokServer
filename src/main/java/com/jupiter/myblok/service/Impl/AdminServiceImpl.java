package com.jupiter.myblok.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jupiter.myblok.mapper.AdminTableMapper;
import com.jupiter.myblok.pojo.DirInfo;
import com.jupiter.myblok.pojo.FileInfo;
import com.jupiter.myblok.pojo.PO.AdminTable;
import com.jupiter.myblok.publiccontext.PublicMemory;
import com.jupiter.myblok.service.AdminService;
import com.jupiter.myblok.util.AjaxResult;
import com.jupiter.myblok.util.EncryTool;
import com.jupiter.myblok.util.FileUtil;
import com.jupiter.myblok.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminTableMapper adminTableMapper;

//    public static void main(String[] args) {
//        System.out.println(EncryTool.getDoubleMd5("123456qq"));
//    }
    @Override
    public AjaxResult<String> toLogin(String username,String password) {
        if(username==null||password==null){
            return AjaxResult.error("用户名或密码不能为空");
        }
        //不能只有空格
        if(username.trim().equals("")||password.trim().equals("")){
            return AjaxResult.error("用户名或密码不能为空");
        }
        //不携带特殊字符
        if(username.contains("'")||username.contains("\"")||username.contains("<")||username.contains(">")||username.contains("=")){
            return AjaxResult.error("用户名或密码不合法");
        }
        //连库
         AdminTable admin = adminTableMapper.selectOne(new QueryWrapper<AdminTable>().eq("username", username)
                .eq("password", EncryTool.getDoubleMd5(password)));
        if(admin==null){
            return AjaxResult.error("用户名或密码错误");
        }
        return AjaxResult.success(TokenUtils.sign("admin", LocalDateTime.now().toString(),1));
    }

    @Override
    public AjaxResult<JSONArray> artilelist(JSONObject pagination) {
         Integer sortId = pagination.getInteger("sortId");
        String searchKey = pagination.getString("searchKey");
        JSONArray jsonArray=new JSONArray();
        List<FileInfo> textFile = null;

         if(sortId==null){
             if(!searchKey.equals("")){
                 textFile = FileUtil.findTextFile(searchKey);
             }else {
                 textFile= PublicMemory.idtofile;
             }
         }else {
             //sortId有值
             Integer sortId1=null;
             for(int i=0;i<PublicMemory.filePath.size();i++){
                 if(PublicMemory.filePath.get(i).getId()==sortId){
                     sortId1=i;
                     break;
                 }
             }
             DirInfo dirInfo=null;
             if(sortId1!=null)
                 dirInfo  = PublicMemory.filePath.get(sortId1);
             else
                 AjaxResult.error("系统错误");

             if(!searchKey.equals("")){
                textFile = FileUtil.findTextFile(dirInfo,searchKey);
             }else {
                 //没有关键字 sortId有值
                 textFile= PublicMemory.filePath.get(sortId1).getFiles();
             }
         }
        for (FileInfo file : textFile) {
            if(!file.getDirName().equals("notion") && !file.getDirName().equals("delete")){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("id",file.getId());
                jsonObject.put("articleTitle",file.getArticleTitle());
                jsonObject.put("sortName",file.getDirName());
                jsonObject.put("labelName",file.getDirName());
                jsonArray.add(jsonObject);
            }

        }
        return AjaxResult.success(jsonArray);
    }
}
