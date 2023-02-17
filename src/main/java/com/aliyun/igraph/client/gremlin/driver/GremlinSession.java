package com.aliyun.igraph.client.gremlin.driver;

import com.aliyun.igraph.client.config.ClientConfig;
import com.aliyun.igraph.client.config.GremlinConfig;
import com.aliyun.igraph.client.core.RequestContext;
import lombok.Data;

/**
 * @author alibaba
 */
@Data
public class GremlinSession {
    private RequestContext requestContext;
    private GremlinConfig gremlinConfig;

    public GremlinSession(ClientConfig clientConfig) {
        requestContext = new RequestContext();
        gremlinConfig = new GremlinConfig();
        gremlinConfig.setRetryTimes(clientConfig.getRetryTimes());
    }
}
