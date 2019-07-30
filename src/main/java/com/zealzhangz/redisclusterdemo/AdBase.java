package com.zealzhangz.redisclusterdemo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Created by zealzhangz.<br/>
 * @version Version: 0.0.1
 * @date DateTime: 2019/03/29 16:57:00<br/>
 */
@Data
@AllArgsConstructor
public class AdBase {
    /**
     * AD base DN
     */
    private String baseDn;
    /**
     * Base of pull users from AD base on OU or CN
     */
    private String pullCn;
}