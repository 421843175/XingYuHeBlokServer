package com.jupiter.myblok.pojo.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jupiter
 * @since 2024-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Admire implements Serializable {

    private static final long serialVersionUID=1L;

  //不是主键  是标识
  //0 统计 1 用户

        @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

      private Integer flag;

    private String ip;

    private Integer yourlike;

    private LocalDateTime date;

    private int recorder;


}
