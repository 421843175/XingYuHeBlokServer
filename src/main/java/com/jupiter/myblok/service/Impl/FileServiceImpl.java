package com.jupiter.myblok.service.Impl;


import com.google.common.primitives.Bytes;
import com.jupiter.myblok.pojo.DirInfo;
import com.jupiter.myblok.pojo.FileInfo;
import com.jupiter.myblok.properties.FileUploadProperties;
import com.jupiter.myblok.publiccontext.PublicMemory;
import com.jupiter.myblok.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;


@Service
public class FileServiceImpl {

    @Autowired
    FileUploadProperties uploadProperties;

    @Value("${server.port}")
    private String port;




    public void getPrice(HttpServletResponse response, File file) throws IOException {
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        //    如果 Content-Type 是image/jpeg，请求图片链接后是直接展示。
//        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(username, "UTF-8"));
        response.addHeader("Content-Disposition", "attachment;");
        //如果 Content-Type 是 application/octet-stream 或者 multipart/form-data 请求图片链接后是直接下载。
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(FileUtil.readBytes(file));
//        System.out.println("成功");
        os.flush();
        os.close();
    }

    public File getPricePath(String requestPath) {
        String paths = requestPath.replace("/article/price/", "");
        String decodpath = null;
        try {
            decodpath = URLDecoder.decode(paths, "UTF-8");
//            System.out.println("图片解析url:"+decodpath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] path = decodpath.split("/");


        //获取目标目录
        getDir(path[0],path[1]);


        //图片 不在这级缓存
        File file = new File(PublicMemory.theDir.toString() + "/" + path[path.length - 1]);
        if (!file.exists()) {
            return null;
        } else {
            return file;
        }


    }

    public void getDir(String requestPath,String picPath) {


        //url解码

//        System.out.println("Decoded URL: " + requestPath);

        String regex = ".*" + requestPath + ".*"; // 正则表达式，匹配包含"abc"字符串的目录名
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:" + regex);

        try {

            Files.walkFileTree(Paths.get(PublicMemory.sccannerpath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (matcher.matches(dir.getFileName())) {

//                        System.out.println("找到目标目录：" + dir);
                        //文件不存在
                        if(!FileUtil.exist(dir+"/"+picPath)) {
                            return FileVisitResult.CONTINUE;
                        }
                        //计入缓存
                        PublicMemory.theDir = dir;
                        return FileVisitResult.TERMINATE; // 如果找到目标目录，且文件存在 直接终止遍历
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public AjaxResult<String> upload(MultipartFile file, String key) throws Exception {


        //类型错误
        if (!uploadProperties.getAllowTypes().contains(file.getContentType())) {
            throw new IOException("文件上传类型错误！");
        }

//    String dir= PublicMemory.theDir

        String[] keyarr = key.split("/");
        if (keyarr.length != 3) {
            return AjaxResult.error("key格式存在问题");
        }
        String sort = keyarr[0];
        String title = keyarr[1];
        String imgname = keyarr[2];

        String exisTitle = null;
        String exisSort = null;




        //判断文件是否已经存在
        for (DirInfo dirInfo : PublicMemory.filePath) {

            //sort 存在吗
            if (dirInfo.getDirName().contains(sort)) {
                exisSort = com.jupiter.myblok.util.FileUtil.findFolder(PublicMemory.sccannerpath, sort);

                for (FileInfo dirInfoFile : dirInfo.getFiles()) {

                    //title 存在吗
                    if (dirInfoFile.getPath().getFileName().toString().contains(title)) {


                        String imgfold = dirInfoFile.getPath().toString().replace(".md", "");

                        //exisTitle 是绝对路径
                        exisTitle = imgfold;

                        System.out.println("existSort=" + exisSort + ",existTitle=" + exisTitle);
                        File imgfilefold = new File(imgfold);
                        String endpath = imgfold + "/" + imgname;
                        if (imgfilefold.isDirectory()) {

                            file.transferTo(new File(endpath));
                            System.out.println("是目录:" + endpath.replace(PublicMemory.sccannerpath, ""));


                        } else {
                            //不是一个目录
                            boolean ismkdirs = imgfilefold.mkdirs();
                            if (ismkdirs) {
                                file.transferTo(new File(endpath));
                                System.out.println("不是目录:" + endpath.replace(PublicMemory.sccannerpath, ""));
                            }

                        }
                        //返回url 添加url到映射
                        return AjaxResult.success(getUrl(endpath, imgname));


                    }
                }
            }
        }
            //扫完一遍了 title不存在 但sort存在
            if (exisTitle == null) {
                if (exisSort != null) {
                    //sort 存在 title 不存在  有两种 没这个文章 没这个文件夹
                    //创建title
                    //获取title最后的一句话
                    String titleend=PublicMemory.sccannerpath+"\\"+exisSort+"\\"+title;
                     File titlefold = new File(titleend);
                    titlefold.mkdirs();

                     String newname=titlefold.getAbsolutePath()+"\\"+imgname;
                     file.transferTo(new File(newname));
                     return AjaxResult.success(getUrl(newname,imgname));


                } else {
                    //sort 不存在
                    //创建Sort
                   new File(PublicMemory.sccannerpath + "\\" + sort + "\\" + title).mkdirs();


                   String newname=PublicMemory.sccannerpath + "\\" + sort + "\\" + title+"\\"+"\\"+imgname;
                   file.transferTo(new File(newname));
                   return AjaxResult.success(getUrl(newname,imgname));


                }

            }

//            file.transferTo(new File(uploadProperties.getPath()+"\\headprice\\"+"."+type));

        return null;

    }


    //含有图片名的最后路径
    public  String getUrl(String endpath,String imgname){
        System.out.println("shabiendpath="+endpath.replace(PublicMemory.sccannerpath, ""));
        String parent=new File(endpath).getParentFile().getName().replace(" ","%20");
        String url = "http://" + PublicMemory.ip + ":" + port + "/article/price/" + parent + "/" + imgname;
        PublicMemory.urlMap.put( url,"../"+endpath.replace(PublicMemory.sccannerpath, "").replace("\\","/"));
        return url;
    }

}
