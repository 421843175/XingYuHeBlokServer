package com.jupiter.myblok.pojo.PO;

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
 * @since 2024-01-24
 */
@Data
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
public class Sharetitle implements Serializable {

    private static final long serialVersionUID=1L;

      private Integer id;

    private String title;

    private String content;

    private String href;

    private LocalDateTime time;


}
