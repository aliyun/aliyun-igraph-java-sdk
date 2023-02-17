package com.aliyun.igraph.client.exception;

import com.aliyun.igraph.client.core.RequestContext;

/**
 * @author alibaba
 */
public class IGraphQueryException extends IGraphClientException {
    private static final long serialVersionUID = -7251711890424635866L;

    public IGraphQueryException(String msg, Throwable t) {
        super(msg, t);
    }

    public IGraphQueryException(String msg) {
        super(msg);
    }

    public static String buildErrorMessage(RequestContext requestContext, String msg) {
        StringBuilder ss = new StringBuilder();
        ss.append("Query failed with errorMsg:[").append(msg).append("], requestContext:[").append(requestContext)
                .append("]");
        return ss.toString();
    }
}
