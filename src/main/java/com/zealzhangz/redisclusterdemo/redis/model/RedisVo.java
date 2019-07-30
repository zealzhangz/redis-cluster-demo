package com.zealzhangz.redisclusterdemo.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author Created by zealzhangz.<br/>
 * @version Version: 0.0.1
 * @date DateTime: 2019/07/30 16:06:00<br/>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisVo  implements Serializable {
    private static final long serialVersionUID = 4594800221848504214L;

    @NotBlank
    private String key;

    @NotBlank
    private String value;
}
