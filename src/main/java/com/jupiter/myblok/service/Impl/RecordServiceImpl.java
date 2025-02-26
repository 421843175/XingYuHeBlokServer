package com.jupiter.myblok.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jupiter.myblok.mapper.RecordMapper;
import com.jupiter.myblok.mapper.RecordgoodnumMapper;
import com.jupiter.myblok.pojo.PO.Record;
import com.jupiter.myblok.pojo.PO.Recordgoodnum;
import com.jupiter.myblok.service.RecordService;
import com.jupiter.myblok.util.AjaxResult;
import com.jupiter.myblok.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    RecordMapper recordMapper;

    @Autowired
    RecordgoodnumMapper recordgoodnumMapper;


    @Override
    public AjaxResult<JSONObject> listRecord(JSONObject pagination) {
        if(pagination.isEmpty()){
            //返回一个总计 和查询结果
            JSONObject result=new JSONObject();
// 使用QueryWrapper
            List<Record> recordList = recordMapper.selectList(new QueryWrapper<Record>()
                    .orderByDesc("createTime")); //获取3-6条



            result.put("records",recordList);
            return AjaxResult.success(result);
        }

        Integer size=pagination.getInteger("size");
         Integer current = pagination.getInteger("current");
        //返回一个总计 和查询结果
        JSONObject result=new JSONObject();





// 使用QueryWrapper
        List<Record> recordList = recordMapper.selectList(new QueryWrapper<Record>()
                .orderByDesc("createTime").last("LIMIT 0"+", "+((current)*size))); //获取3-6条



        result.put("total",recordMapper.selectCount(null));
        result.put("records",recordList);
        return AjaxResult.success(result);
    }

    @Transactional
    @Override
    public AjaxResult<Integer> goodNum(Integer id, HttpServletRequest httpRequest) {
        System.out.println("id="+id+",httpRequest="+httpRequest);
         String ipAddr = IPUtils.getIpAddr(httpRequest);
         Recordgoodnum recordgoodnum = recordgoodnumMapper.selectOne(new QueryWrapper<Recordgoodnum>().eq("ip", ipAddr).eq("forid", id));
        //如果不存在
        if(recordgoodnum ==null){
            recordgoodnum =new Recordgoodnum();
            recordgoodnum.setIp(ipAddr);
            recordgoodnum.setForid(id);
            //设置点一个赞
            recordgoodnum.setNum(1);
            recordgoodnumMapper.insert(recordgoodnum);

        }else{
            if(recordgoodnum.getNum()>10){
                return AjaxResult.error("您已经对此条消息点了超过十个赞了 非常感谢您的支持");
            }
        }


        recordgoodnum.setNum(recordgoodnum.getNum()+1);
        //更新点赞数
        recordgoodnumMapper.updateById(recordgoodnum);

        Record record1 = recordMapper.selectOne(new QueryWrapper<Record>().eq("id", id));
        if(record1.getGoodNum()==null){
            record1.setGoodNum(0);
        }
        int goodnum=record1.getGoodNum()+1;
        record1.setGoodNum(goodnum);
        //更新总表
        recordMapper.updateById(record1);

        return AjaxResult.success(goodnum);
    }

    @Override
    public void poFang(HttpServletResponse response, String url) throws Exception {
        OutputStream os = response.getOutputStream();
        System.out.println("url="+url);
        URLConnection u = new URL(url).openConnection();
        InputStream in = u.getInputStream();

        if (null != in) {
            int len;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) { // 循环读取
                os.write(b, 0, len); // 写入到输出流
            }
            os.flush();
            in.close();
        }
        os.close();
    }

    @Override
    public AjaxResult<String> saveSpace(Record space) {
        int insert = recordMapper.insert(space);
        if(insert==1){
            return AjaxResult.success("保存成功");
        }else {
            return AjaxResult.error("保存失败");
        }

    }

    @Override
    public AjaxResult<String> updateSpace(Record space) {
         int i = recordMapper.updateById(space);
        if(i==1) {
            return AjaxResult.success("更新成功");
        }else {
            return AjaxResult.error("更新失败");
        }
    }

    @Override
    public AjaxResult<String> deleteRecord(int id) {
        int i = recordMapper.deleteById(id);
        if(i==1) {
            return AjaxResult.success("删除成功");
        }else {
            return AjaxResult.error("删除失败");
        }
    }

}
