package com.jupiter.myblok.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    private Integer id;
    private Path path;
    private String articleTitle;
//    private String articleContent;
    private String createTime;
    private String password;
    private String dirName;


    public FileInfo(Path path) {
        this.path = path;
    }

    public FileInfo(Integer id, Path path) {
        this.id = id;
        this.path = path;
    }

    public FileInfo(Integer id, Path path,String dirName) {
        this.id = id;
        this.path = path;
        this.dirName=dirName;
    }
}
