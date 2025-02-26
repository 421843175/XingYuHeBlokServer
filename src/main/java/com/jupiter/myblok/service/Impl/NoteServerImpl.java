package com.jupiter.myblok.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jupiter.myblok.pojo.DirInfo;
import com.jupiter.myblok.pojo.FileInfo;
import com.jupiter.myblok.pojo.SortInfo;
import com.jupiter.myblok.publiccontext.PublicMemory;
import com.jupiter.myblok.service.NoteServer;
import com.jupiter.myblok.util.AjaxResult;
import com.jupiter.myblok.util.FileUtil;
import com.jupiter.myblok.util.JUPrint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jupiter.myblok.publiccontext.PublicMemory.*;

@Service
public class NoteServerImpl implements NoteServer {


    @Value("${server.port}")
    private String port;
    public static int dirid=1;
    public static int id=1;
    static {
        inserteFileDir();
    }
   public static void inserteFileDir(){
        filePath.clear();
        AtomicInteger fileCount = new AtomicInteger();
        try {
            Files.walkFileTree(Paths.get(sccannerpath),new SimpleFileVisitor<Path>(){

                //使用栈记录
                private Deque<String> directoryStack = new ArrayDeque<>();

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    directoryStack.push(dir.getFileName().toString());
                    return super.preVisitDirectory(dir, attrs);
                }
                //            遍历文件



                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    if (file.toString().endsWith(".md")) {
                        fileCount.incrementAndGet();
                        String keystr = new String();
                        for (String dir : directoryStack) {
                            if (dir.toString().matches(".*[0-9a-fA-F]{32}$")) {
                                dir = dir.toString().substring(0, dir.toString().length() - 33);
                                if (!dir.endsWith("——")) {
                                    keystr = dir;
                                    break;
                                }
                            }
                            // 正常的文件夹
                            else {
                                keystr = dir;
                                break;
                            }

                        }
                        String key = keystr;

                        if(!key.equals("")){
//                            System.out.println("key="+key+",value="+file);
                            //去除————后缀md文件
                            String articletile= file.getFileName().toString().replace(".md","");
                            if (articletile.matches(".*[0-9a-fA-F]{32}$")) {
                                articletile= articletile.substring(0, articletile.length() - 33);
                                if(articletile.endsWith("——")){
//                                    System.out.println("articletile="+articletile+",value="+file);
                                    return super.visitFile(file, attrs);
                                }
                            }
                            //记录id到数组
                             FileInfo fileinfo = new FileInfo(id++, file);


                            //设置文章标题
                             articletile= String.valueOf(file.getFileName()).replace(".md","");
                            if (articletile.matches(".*[0-9a-fA-F]{32}$")) {
                                articletile = articletile.substring(0, articletile.length() - 33);
                            }
                            fileinfo.setArticleTitle(articletile);


                            idtofile.add(fileinfo);


                            for (DirInfo dirInfo : filePath) {
                                if(dirInfo.getDirName().equals(key)){
                                    fileinfo.setDirName(dirInfo.getDirName());
                                    dirInfo.getFiles().add(fileinfo);
                                    return super.visitFile(file, attrs);
                                }
                            }
                             DirInfo dirInfo = new DirInfo(dirid++, key, fileinfo);
                            filePath.add(dirInfo);
                            fileinfo.setDirName(dirInfo.getDirName());
                        }

                    }
                    return super.visitFile(file, attrs);
                }


                //退出目录
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    directoryStack.pop();
                    return super.postVisitDirectory(dir, exc);
                }
            });
            dirid=1;
            for(int i=0;i<filePath.size();i++){
                System.out.println("buai="+filePath.get(i).getDirName());

                if(filePath.get(i).getDirName().equals("notion") || filePath.get(i).getDirName().equals("delete")){
                    System.out.println("thei="+i);
                    filePath.remove(i);

                    //改变了i  此时i值回退
                    i--;
                }else {
                    filePath.get(i).setId(dirid++);
                }

            }


            for (int i=0;i<filePath.size();i++) {
                System.out.println("mei:"+filePath.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

@Override
        public AjaxResult<String> getFileDir(){
            int id=0;
            JSONArray sortInfos = new JSONArray();
            for (DirInfo dirInfo : filePath) {
                String sortName= dirInfo.getDirName();
                id++;
                String sortDescription="共有"+dirInfo.getFiles().size()+"篇文章";
                SortInfo sortInfo = new SortInfo(id, sortName, sortDescription, id, dirInfo.getFiles().size());
                sortInfos.add(sortInfo);
            }
//            System.out.println(sortInfos.toJSONString());
            return AjaxResult.success(sortInfos.toJSONString());
        }

    @Override
    public AjaxResult<JSONArray> getAllArticle() {
        JSONArray sortInfos = new JSONArray();

        JSONArray jsonArray=new JSONArray();
        jsonArray.add(null);
        sortInfos.add(jsonArray);
        for (DirInfo dirInfo : filePath) {


             jsonArray=new JSONArray();
           LinkedList<FileInfo> files = dirInfo.getFiles();
            for (FileInfo file : files) {
                JSONObject sortInfos2 = new JSONObject();


                sortInfos2.put("articleTitle",file.getArticleTitle());

                sortInfos2.put("id",file.getId());


                sortInfos2.put("articleContent",FileUtil.readFristChar(file));


                JSONObject label = new JSONObject();


                label.put("labelName", dirInfo.getDirName());
                sortInfos2.put("label",label);

                JSONObject sort = new JSONObject();
                sort.put("sortName",dirInfo.getDirName());
                sortInfos2.put("sort",sort);

                sortInfos2.put("articleCover","path/to/cover1.jpg");
                sortInfos2.put("sortId",dirInfo.getId());
                sortInfos2.put("labelId",dirInfo.getId());
//                System.out.println(sortInfos2);
                jsonArray.add(sortInfos2);
            }

            sortInfos.add(jsonArray);

        }
//        System.out.println(sortInfos.toJSONString());
        return AjaxResult.success(sortInfos);

    }

    //获取文章
    @Override
    public AjaxResult<JSONObject> getArticleById(int id) {
      JSONObject jsonObject=new JSONObject();
        try {
             String context = FileUtil.readFile(idtofile.get(id - 1).getPath());
             //处理格式
            context=convertMarkdownImageURL(context);
            jsonObject.put("articleContent",context);
        } catch (IndexOutOfBoundsException e) {
            return AjaxResult.error("不存在这个id对应的文章");
        }catch (Exception e){
            return AjaxResult.error("服务器异常");
        }


        jsonObject.put("labelName",idtofile.get(id-1).getDirName());
//        jsonObject.put("sortName",idtofile.get(id-1).getDirName());
        for(DirInfo dirInfo:filePath){
            if(dirInfo.getDirName().equals(idtofile.get(id-1).getDirName())){
                //获取完整标题
                String lei = null;
                try {
                  lei =FileUtil.findFolder(sccannerpath,dirInfo.getDirName());
                    System.out.println("lei="+lei);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String file = FileUtil.findFile(sccannerpath+"/"+lei, idtofile.get(id - 1).getArticleTitle());

                if(file!=null){
                    jsonObject.put("articleTitle",file.replace(".md",""));
                }
                jsonObject.put("sortId",dirInfo.getId());
                jsonObject.put("labelId",dirInfo.getId());
                break;
            }
        }
//        System.out.println(jsonObject.toJSONString());
      return AjaxResult.success(jsonObject);
    }

    @Override
    public AjaxResult<JSONObject> listArticle(JSONObject pagination) {
//        System.out.println("pagination="+pagination);
        //当前第几页
         Integer current = pagination.getInteger("current")-1;
         //总计
        Integer total ;   //=0  0条过去了
        //每页显示多少条
        Integer size = pagination.getInteger("size");

         String sortIdOld = pagination.getString("sortId");
         Integer sortId = Integer.parseInt(sortIdOld)-1;

         //依据sortId看

        JSONArray jsonArray=new JSONArray();

         LinkedList<FileInfo> files = filePath.get(sortId).getFiles();
//        System.out.println("file.size()="+files.size());
        total=current*size+size;
        if(total>files.size()){
            total=files.size();
        }
        for (int i=current*size;i<total;i++) {
            //resultObject
            JSONObject resultObject=new JSONObject();

            resultObject.put("articleTitle",files.get(i).getArticleTitle());
            resultObject.put("id",files.get(i).getId());
            resultObject.put("articleContent",FileUtil.readFristChar(files.get(i)));
            resultObject.put("articleCover","path/to/cover1.jpg");
            resultObject.put("sortName",files.get(i).getDirName());

            jsonArray.add(resultObject);
        }
//        System.out.println(jsonArray);

        JSONObject result=new JSONObject();
        result.put("records",jsonArray);
        result.put("total",files.size());

        return AjaxResult.success(result);

    }

    @Override
    public AjaxResult<String> saveArticle(JSONObject article) {
         String articleTitle = article.getString("articleTitle");
         int sortId = article.getInteger("sortId");
            String articleContent = article.getString("articleContent");
        //处理 将 图片的url 通过urlMap 转换为本地路径
        for (Map.Entry<String, String> entry : urlMap.entrySet()) {
//            System.out.println("entry.getkey:"+entry.getKey()+",entry.getValue:"+entry.getValue());
            articleContent = articleContent.replaceAll(entry.getKey(), entry.getValue());
        }
        urlMap.clear();



        System.out.println("sort="+filePath.get(sortId-1).getDirName());
        final LinkedList<FileInfo> files = filePath.get(sortId - 1).getFiles();
        for (FileInfo file : files) {
            //文件名必须一样 防止覆盖风险
            if (file.getPath().getFileName().toString().equals(articleTitle+".md")){
                String path = file.getPath().toString();
                try {
                    // 将内容写入文件
                    Path filePath = Paths.get(path);
                    Files.write(filePath, articleContent.getBytes());
                    System.out.println("文件内容已成功覆盖！");



                    return AjaxResult.success("博客成功更新！");
                } catch (IOException e) {
                    System.out.println("写入文件时发生错误：" + e.getMessage());
                }
            }
        }
        //文件名不一样
        try {

            //判断文件是否存在同名
            String folder = FileUtil.findFolder(sccannerpath, filePath.get(sortId - 1).getDirName());
            JUPrint.print(sccannerpath + "/" + folder);
             String s = FileUtil.DGfindFile(sccannerpath + "/" + folder, articleTitle);
            if(s!=null) {
                return AjaxResult.error("已存在同名博客");
            }

            //得到文件夹全名
            Files.write(Paths.get(sccannerpath+"/"+folder+"/"+articleTitle+".md"), articleContent.getBytes());

            //放到public里面
            FileInfo fileinfo = new FileInfo(id++, Paths.get(sccannerpath+"/"+folder+"/"+articleTitle+".md"));
            fileinfo.setArticleTitle(articleTitle);
            fileinfo.setDirName(filePath.get(sortId - 1).getDirName());
            idtofile.add(fileinfo);

             DirInfo dirInfo1 = filePath.get(sortId - 1);
             dirInfo1.getFiles().add(fileinfo);
             filePath.set(sortId-1,dirInfo1);


            return AjaxResult.success("新博客成功写入！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //新增标签
    @Override
    public AjaxResult<String> saveSort(JSONObject sortForHttp) {
         String sortName = sortForHttp.getString("sortName");
        //判断是否存在这个标签
        for (DirInfo dirInfo : filePath) {
            if(dirInfo.getDirName().contains(sortName)){
                return AjaxResult.error("已存在类似名称的标签");
            }
        }
        //不存在
        try {
            //创建文件夹
            try{
                Files.createDirectory(Paths.get(sccannerpath+"/"+sortName));
            }catch (FileAlreadyExistsException e) {
                return AjaxResult.error("已存在同名目录");
            }
            Path path  = Paths.get(sccannerpath + "/" + sortName + "/" + "默认的.md");

            try{

                Files.createFile(path);
            }catch (Exception e) {
                return AjaxResult.error("未知错误");
            }

             FileInfo fileInfo = new FileInfo();
            //在目录下面创建一个文件吧


            fileInfo.setArticleTitle("空文件");
            fileInfo.setId(id++);
            fileInfo.setDirName(sortName);
            fileInfo.setPath(path);
            //创建文件夹信息
            DirInfo dirInfo = new DirInfo(dirid++, sortName,fileInfo);
            dirInfo.setDirName(sortName);
            filePath.add(dirInfo);

            idtofile.add(fileInfo);
            System.out.println("filePath="+filePath);
            System.out.println("dirInfo="+dirInfo);

            return AjaxResult.success("新标签创建成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AjaxResult<String> deleteArticle(int id) {
        //不存在delete文件夹创建
        File deletefile = new File(sccannerpath + "/" + "delete");
        if (!deletefile.exists()) {
            deletefile.mkdir();
        }
        for (FileInfo fileInfo : idtofile) {
            if(fileInfo.getId()==id){
                try {
                    Files.move(fileInfo.getPath(),Paths.get(sccannerpath+"/"+"delete"+"/"+fileInfo.getPath().getFileName()));
                    //判断fileInfo.getPath() 底下有没有 包含文件名的文件夹
                    System.out.println("11:"+fileInfo.getPath().getParent()+"");
                    String folder = FileUtil.findFolder(fileInfo.getPath().getParent()+"", fileInfo.getPath().getFileName().toString().replace(".md", ""));
                    System.out.println("folerfor:"+folder);
                    if(folder!=null){
                        //移动图片文件夹到delete
                        Files.move(Paths.get(fileInfo.getPath().getParent()+"/"+folder),Paths.get(sccannerpath+"/"+"delete"+"/"+folder));
                    }

                    //删除 filepath里的
                    for (DirInfo dirInfo : filePath) {
                        for (FileInfo file : dirInfo.getFiles()) {
                            if(file.getId()==id){
                                dirInfo.getFiles().remove(file);
                                filePath.set(filePath.indexOf(dirInfo),dirInfo);
                                break;
                            }
                        }
                    }

                    fileInfo.setDirName("delete");
                    idtofile.set(fileInfo.getId()-1,fileInfo);
                    return AjaxResult.success("删除成功！");
                } catch (NoSuchFileException e) {
                   return AjaxResult.error("不能从delete中再删除文件");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public AjaxResult<String> updateSort(JSONObject labelForHttp) {
         Integer id = labelForHttp.getInteger("id");
         String sortName = labelForHttp.getString("sortName");
         String oldname = filePath.get(id - 1).getDirName();
        try {
           //文件夹重命名
             String folder = FileUtil.findFolder(sccannerpath, oldname);
            if(folder!=null){
                try{
                    Files.move(Paths.get(sccannerpath+"/"+folder),Paths.get(sccannerpath+"/"+sortName));
                }catch (AccessDeniedException e){
                    return AjaxResult.error("访问被拒绝");
                }
            }
            inserteFileDir();

            return AjaxResult.success("标签修改成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("labelForHttp="+labelForHttp);
        return AjaxResult.success("标签修改成功！");
    }

//
//    public AjaxResult<String> updateArticle(@RequestBody JSONObject article) {
//        return null;
//    }

//    public static void main(String[] args) {
//        String str1="http://127.0.0.1:8081/article/price/编辑器问题%2070db867e2d164becb96d4c0b60354861/1706261460573940.png";
//        String str2="http://127.0.0.1:8081/article/price/编辑器问题%2070db867e2d164becb96d4c0b60354861/1706261614862210.png";
//        System.out.println(str1.equals(str2));
//    }
//    public static void main(String[] args) {
//        String a="\n" +
//                "![JAVA%20b93862cb12c542dfba1519539e599262/image4.png](JAVA%20b93862cb12c542dfba1519539e599262/image4.png)\n" +
//                "\n" +
//                "提示（方法，参数)ctrl+shift+空格\n" +
//                "\n" +
//                "## **方法文档注释**\n" +
//                "\n" +
//                "[https://blog.csdn.net/qq_43647821/article/details/123297521](https://blog.csdn.net/qq_43647821/article/details/123297521)\n" +
//                "\n" +
//                "我的内容：\n" +
//                "\n" +
//                "- \n" +
//                "\n" +
//                "* $VAR1$\n" +
//                "\n" +
//                "* @return\n" +
//                "\n" +
//                "* @date: $date$ $time$\n" +
//                "\n" +
//                "*/\n" +
//                "\n" +
//                "!(https://picx.zhimg.com/v2-7aaf321b9bebb757de0637c4af120ca5_b.jpg)\n" +
//                "\n" +
//                "![JAVA%20b93862cb12c542dfba1519539e599262/image5.png](JAVA%20b93862cb12c542dfba1519539e599262/image5.png)\n" +
//                "\n" +
//                "![JAVA%20b93862cb12c542dfba1519539e599262/image6.png](JAVA%20b93862cb12c542dfba1519539e599262/image6.png)\n" +
//                "\n" +
//                "# **IDEA JAVAWEB**\n" +
//                "\n" +
//                "[配置JAVAWEB模块](https://www.jb51.net/article/220162.htm)\n" +
//                "\n" +
//                "1.run>Edit Configuration\n" +
//                "\n" +
//                "2 点击左上角+，找到Tomcat Server>Local，点击。\n" +
//                "\n" +
//                "![JAVA%20b93862cb12c542dfba1519539e599262/image7.png](JAVA%20b93862cb12c542dfba1519539e599262/image7.png)\n" +
//                "\n" +
//                "部署WEB工程\n" +
//                "\n" +
//                "![JAVA%20b93862cb12c542dfba1519539e599262/image8.png](JAVA%20b93862cb12c542dfba1519539e599262/image8.png)\n" +
//                "\n" +
//                "![JAVA%20b93862cb12c542dfba1519539e599262/image9.png](JAVA%20b93862cb12c542dfba1519539e599262/image9.png)";
//        System.out.println(convertMarkdownImageURL(a));
//    }


    public  String convertMarkdownImageURL(String markdown) {
//        System.out.println("ip="+ip+"port="+port);
         String baseUrl="http://"+ ip+":"+port+"/article/price/";
        // 匹配 ![图片描述](图片路径) 的正则表达式
        String regex = "!\\[([^\\]]*)\\]\\(([^\\)]*)\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(markdown);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String description = matcher.group(1);
            String path = matcher.group(2);

            // 解析路径中的值
            String[] parts = path.split("/");
            String imageName = parts[parts.length - 1];
            String imageId = parts[parts.length - 2];

            // 构造替换后的markdown图片标签
            String replacement = "![%s](%s)";
            String replacedMarkdown = String.format(replacement, description, baseUrl + imageId + "/" + imageName);

            replacedMarkdown = replacedMarkdown.replace(" ", "%20");
            JUPrint.print("relacedMarkdown="+replacedMarkdown);
            matcher.appendReplacement(sb, replacedMarkdown);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

}
