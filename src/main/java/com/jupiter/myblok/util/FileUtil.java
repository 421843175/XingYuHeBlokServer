package com.jupiter.myblok.util;

import com.jupiter.myblok.pojo.DirInfo;
import com.jupiter.myblok.pojo.FileInfo;
import com.jupiter.myblok.publiccontext.PublicMemory;
import lombok.var;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

public class FileUtil {

    public static String findFile(String directory, String searchStr) {
        File dir = new File(directory);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String result = findFile(file.getAbsolutePath(), searchStr);
                    if (result != null) {
                        return result;
                    }
                } else if (file.getName().toLowerCase().endsWith(".md") && file.getName().contains(searchStr)) {
                    return file.getName();
                }
            }
        }
        return null; // 如果未找到匹配的文件，返回null
    }
        public static String readFile(Path filePath) throws IOException {
            try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
                ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
                channel.read(buffer);
                buffer.flip();

                Charset charset = StandardCharsets.UTF_8;
                CharsetDecoder decoder = charset.newDecoder();
                CharBuffer charBuffer = decoder.decode(buffer);

                return charBuffer.toString();
            }
        }

        public static String readFristChar(FileInfo file){
            String firstChars = null;
            try {
                BufferedReader br = Files.newBufferedReader(file.getPath(), StandardCharsets.UTF_8);

                char[] buffer = new char[100];
                int charsRead = br.read(buffer, 0, 100);

                if (charsRead != -1) {
                    firstChars = new String(buffer, 0, charsRead);
                    firstChars= firstChars.replaceAll("#", "");
                    firstChars=  firstChars.replaceAll("\\*","");
                    firstChars=  firstChars.replaceAll("`","");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return firstChars+"…";
        }


        //不递归
    public static String findFolder(String folderPath,String containName) throws IOException {
        // 获取目录下所有文件和文件夹
        DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath));
        for (Path path : stream) {
            // 判断是否为文件夹并且名称包含 TENTCENT
            if (Files.isDirectory(path) && path.getFileName().toString().contains(containName)) {
                return path.getFileName().toString();
            }
        }
        return null; // 没有找到符合条件的文件夹
    }

    public static LinkedList<FileInfo> findTextFile(String searchFor){
        LinkedList<FileInfo> result = new LinkedList<>();
        for (FileInfo fileInfo : PublicMemory.idtofile) {
            try (var reader = Files.newBufferedReader(fileInfo.getPath(), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(searchFor)) {
                        System.out.println("文件中包含目标字符串！");
                        result.add(fileInfo);
                    }
                }
            } catch (IOException e) {
                System.out.println("读取文件时发生错误：" + e.getMessage());
            }
        }
        return result;
        }

    public static LinkedList<FileInfo> findTextFile(DirInfo dirInfo,String searchFor){
        LinkedList<FileInfo> result = new LinkedList<>();
        for (FileInfo fileInfo : dirInfo.getFiles()) {
            try (var reader = Files.newBufferedReader(fileInfo.getPath(), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(searchFor)) {
                        System.out.println("文件中包含目标字符串！");
                        //如果result里面已经包含这个文件
                        if(result.contains(fileInfo)){
                            continue;
                        }
                        result.add(fileInfo);
                    }
                }
            } catch (IOException e) {
                System.out.println("读取文件时发生错误：" + e.getMessage());
            }
        }
        return result;
    }

    //递归 是否存在同名
    public static String DGfindFile(String folderPath, String containName) throws IOException {
        File folder = new File(folderPath);
        // 获取目录下所有文件和文件夹
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                // 如果是文件，并且文件名包含指定字符串，直接返回文件名
                System.out.println("file.getName()"+file.getName());
                if (file.isFile() && file.getName().contains(containName)) {
                    //判断文件去除md 和后面十六进制数是否一样
                     String oldfile = file.getName().replace(".md", "");
                    if (oldfile.matches(".*[0-9a-fA-F]{32}$")) {
                        System.out.println("走了判断十六你在哪hi");
                        String newfilename = oldfile.substring(0, oldfile.length() - 33);
                        if (newfilename.equals(containName)) {
                            return file.getName();
                        }
                    }else {
                        if (file.getName().equals(containName)) {
                            return file.getName();
                        }
                    }
                }
                // 如果是文件夹，递归查找其中的文件
                if (file.isDirectory()) {
                    String result = findFile(file.getPath(), containName);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null; // 没有找到符合条件的文件
    }

}
