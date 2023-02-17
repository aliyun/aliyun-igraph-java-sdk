package com.aliyun.igraph.client.pg;

import com.aliyun.igraph.client.config.PGConfig;
import com.aliyun.igraph.client.core.RequestContext;
import lombok.Data;

/**
 * @author alibaba
 */
@Data
public class PGSession {
    private RequestContext requestContext = new RequestContext();
    private PGConfig pgConfig = new PGConfig();
}
