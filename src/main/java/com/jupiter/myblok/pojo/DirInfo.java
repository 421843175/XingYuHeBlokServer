package com.jupiter.myblok.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@NoArgsConstructor
public class DirInfo {
    private Integer id;
    private String dirName;

    private LinkedList<FileInfo> files=new LinkedList<>();

    public DirInfo(Integer id,String dirName, FileInfo file) {
        this.id=id;
        this.dirName = dirName;
        files.add(file);
    }

    public DirInfo(Integer id, String dirName) {
        this.id = id;
        this.dirName = dirName;
    }
}
