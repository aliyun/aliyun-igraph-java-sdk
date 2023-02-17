package com.aliyun.igraph.client.exception;

/**
 * @author alibaba
 */
public class IGraphServerException extends IGraphClientException {
    private static final long serialVersionUID = -7251711890424635866L;

    public IGraphServerException(String msg, Throwable t) {
        super(msg, t);
    }

    public IGraphServerException(String msg) {
        super(msg);
    }
}
