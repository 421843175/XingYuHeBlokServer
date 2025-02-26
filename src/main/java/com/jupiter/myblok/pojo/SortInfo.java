package com.jupiter.myblok.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortInfo {
    private Integer id;
    private String sortName;
    private String sortDescription;
    private Integer priority;
    private Integer countOfSort;

}
