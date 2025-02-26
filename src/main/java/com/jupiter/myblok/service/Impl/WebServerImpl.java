
package com.jupiter.myblok.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jupiter.myblok.mapper.AdmireMapper;
import com.jupiter.myblok.mapper.MyCodeListMapper;
import com.jupiter.myblok.mapper.SharetitleMapper;
import com.jupiter.myblok.pojo.DirInfo;
import com.jupiter.myblok.pojo.FileInfo;
import com.jupiter.myblok.pojo.PO.Admire;
import com.jupiter.myblok.pojo.PO.MyCodeList;
import com.jupiter.myblok.pojo.PO.Sharetitle;
import com.jupiter.myblok.service.WebServer;
import com.jupiter.myblok.util.AjaxResult;
import com.jupiter.myblok.util.FileUtil;
import com.jupiter.myblok.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static com.jupiter.myblok.publiccontext.PublicMemory.filePath;

@Service
public class WebServerImpl implements WebServer {

    @Autowired
    AdmireMapper admireMapper;

    @Autowired
    SharetitleMapper sharetitleMapper;

    @Autowired
    MyCodeListMapper myCodeListMapper;


    @Override
    public AjaxResult<Integer> getAdmire() {
        //查一查 有没有id是-1的
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("flag",0);
         Admire admire = admireMapper.selectOne(queryWrapper);
         if(admire==null){
             //没有就创建一个
                admire=new Admire();
                admire.setFlag(0);
                admire.setYourlike(0);
                //插进去
                admireMapper.insert(admire);
                return  AjaxResult.success(admire.getYourlike());
         }

        return AjaxResult.success(admire.getYourlike());
    }


    @Transactional  //加上事务
    @Override
    public AjaxResult<Integer> giveAdmire(HttpServletRequest httpRequest) {
        String ipAddr = IPUtils.getIpAddr(httpRequest);
        //有没有这个ip
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("ip", ipAddr);
        Admire admire = admireMapper.selectOne(queryWrapper);

        if (admire == null) {
            //没有就创建一个
            admire = new Admire();
            admire.setFlag(1);
            admire.setIp(ipAddr);
            admire.setDate(LocalDateTime.now());

            admire.setYourlike(1);
            admire.setRecorder(1);
            admireMapper.insert(admire);

        }else {
            //有了  判断下日期是否是今天之内
            if(admire.getDate().toLocalDate().isEqual(LocalDateTime.now().toLocalDate())){
                //是今天之内  超过数量 就不给他点赞了
                if(admire.getRecorder()>=10){
                    return AjaxResult.error("您今天点赞超过次数 明天再来吧");
                }else{
                    //没有超过数量  就给他点赞
                    admire.setRecorder(admire.getRecorder()+1);
                    admire.setYourlike(admire.getYourlike()+1);
                }

            }else {
                //不是今天之内  就重置
                admire.setRecorder(1);
                admire.setYourlike(admire.getYourlike()+1);
                admire.setDate(LocalDateTime.now());
            }
            admireMapper.updateById(admire);
        }



        //再更新总数居
        QueryWrapper queryWrapper1=new QueryWrapper();
        queryWrapper1.eq("flag",0);
        Admire admire1 = admireMapper.selectOne(queryWrapper1);

        admire1.setYourlike(admire1.getYourlike()+1);

        admireMapper.updateById(admire1);


        return AjaxResult.success(admire1.getYourlike());
    }

    @Override
    public AjaxResult<List> listShare() {
         List<Sharetitle> sharelist = sharetitleMapper.selectList(new QueryWrapper<Sharetitle>().orderByDesc("time"));
        return AjaxResult.success(sharelist);
    }

    @Override
    public AjaxResult<JSONObject> listMyCode() {
        JSONObject resultObject = new JSONObject();

        // 使用 MyBatis Plus 的查询方法获取数据列表
        List<MyCodeList> itemList = myCodeListMapper.selectList(null);

        // 构造返回给前端的数据格式
        for (MyCodeList item : itemList) {
            String category = item.getCategory();
            JSONObject itemObject = new JSONObject();
            itemObject.put("title", item.getTitle());
            itemObject.put("introduction", item.getIntroduction());
            itemObject.put("url", item.getUrl());
            itemObject.put("id",item.getId());
            itemObject.put("category",item.getCategory());

            if (!resultObject.containsKey(category)) {
                resultObject.put(category, new ArrayList<>());
            }
            resultObject.getJSONArray(category).add(itemObject);
        }

        return AjaxResult.success(resultObject);
    }

    @Override
    public AjaxResult<JSONObject> getlistSortAndLabel() {
        JSONObject result=new JSONObject();
        JSONArray jsonArray=new JSONArray();

        for (DirInfo dirInfo : filePath) {
            JSONObject sortInfo=new JSONObject();
            sortInfo.put("id",dirInfo.getId());
            sortInfo.put("labelName",dirInfo.getDirName());
            sortInfo.put("sortName",dirInfo.getDirName());

            jsonArray.add(sortInfo);

        }

        result.put("sorts",jsonArray);

        String str = JSON.toJSONString(jsonArray, SerializerFeature.DisableCircularReferenceDetect);

        //JSONObject重复引用导致结果中出现$ref的问题
        jsonArray = JSONArray.parseArray(str);
        result.put("labels",jsonArray);

        System.out.println(result);
        return AjaxResult.success(result);


    }

    @Override
    public AjaxResult<String> saveColi(MyCodeList myCodeList) {
        int insert = myCodeListMapper.insert(myCodeList);
        if(insert==1){
            return AjaxResult.success("保存成功");
        }else {
            return AjaxResult.error("保存失败");
        }
    }

    @Override
    public AjaxResult<String> updateColi(MyCodeList myCodeList) {
        int i = myCodeListMapper.updateById(myCodeList);
        if(i==1) {
            return AjaxResult.success("更新成功");
        }else {
            return AjaxResult.error("更新失败");
        }
    }

    @Override
    public AjaxResult<String> deleteColi(int id) {
        int i = myCodeListMapper.deleteById(id);
        if(i==1) {
            return AjaxResult.success("删除成功");
        }else {
            return AjaxResult.error("删除失败");
        }
    }


//    public static void main(String[] args) {
//        String dateString = "2024-02-23T15:58:08.934";
//        LocalDateTime dateTime = LocalDateTime.parse(dateString);
//
//        LocalDateTime now = LocalDateTime.now();
//        boolean isToday = dateTime.toLocalDate().isEqual(now.toLocalDate());
//
//        System.out.println("是否在今天之内：" + isToday);
//    }


}
