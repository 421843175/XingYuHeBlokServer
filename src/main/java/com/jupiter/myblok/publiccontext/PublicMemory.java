package com.jupiter.myblok.publiccontext;

import com.jupiter.myblok.pojo.DirInfo;
import com.jupiter.myblok.pojo.FileInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class PublicMemory {
    public static String ip="www.mxweb.world";

    public static String sccannerpath="/opt/JUcode/blok/notion";

    //根路径/无——的 路径  ->文件路径
    public static LinkedList<DirInfo> filePath = new LinkedList<>();

    //存储 id->文件
    public static ArrayList<FileInfo> idtofile=new ArrayList<>();

    public static Path theDir=null;

    //映射 url -> 文件夹
    public static  HashMap<String, String> urlMap = new HashMap<>();
}





//public class PublicMemory {
//    public static String ip="127.0.0.1";
//
//    public static String sccannerpath="D:\\judata\\notion";
//
//    //根路径/无——的 路径  ->文件路径
//    public static LinkedList<DirInfo> filePath = new LinkedList<>();
//
//    //存储 id->文件
//    public static ArrayList<FileInfo> idtofile=new ArrayList<>();
//
//    public static Path theDir=null;
//
//    //映射 url -> 文件夹
//    public static  HashMap<String, String> urlMap = new HashMap<>();
//}
