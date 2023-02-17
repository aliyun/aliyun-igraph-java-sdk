package com.aliyun.igraph.client.exception;

/**
 * @author shouya
 */
public class IGraphRetryableException extends IGraphServerException {
    private static final long serialVersionUID = 1480912809821945312L;

    public IGraphRetryableException(String msg, Throwable t) {
        super(msg, t);
    }

    public IGraphRetryableException(String msg) {
        super(msg);
    }
}
