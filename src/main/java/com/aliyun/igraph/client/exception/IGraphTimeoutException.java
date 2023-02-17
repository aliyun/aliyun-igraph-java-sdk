package com.aliyun.igraph.client.exception;

/**
 * @author alibaba
 */
public class IGraphTimeoutException extends IGraphServerException {
    private static final long serialVersionUID = 1480912809821945311L;

    public IGraphTimeoutException(String msg, Throwable t) {
        super(msg, t);
    }

    public IGraphTimeoutException(String msg) {
        super(msg);
    }
}
