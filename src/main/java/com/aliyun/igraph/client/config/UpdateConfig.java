package com.aliyun.igraph.client.config;

import lombok.Data;

/**
 * @author alibaba
 */
@Data
public class UpdateConfig {
    protected String src;
    protected int timeoutInMs = 0;
}
