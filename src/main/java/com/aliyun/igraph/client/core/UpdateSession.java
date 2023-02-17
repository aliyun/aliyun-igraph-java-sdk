package com.aliyun.igraph.client.core;

import com.aliyun.igraph.client.config.UpdateConfig;
import lombok.Data;

/**
 * @author alibaba
 */
@Data
public class UpdateSession {
    private RequestContext requestContext = new RequestContext();
    private UpdateConfig updateConfig = new UpdateConfig();
}
