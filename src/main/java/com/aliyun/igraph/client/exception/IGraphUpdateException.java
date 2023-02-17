package com.aliyun.igraph.client.exception;

import java.util.List;

/**
 * @author alibaba
 */
public class IGraphUpdateException extends IGraphClientException {
    private static final long serialVersionUID = -4982930420311945561L;
    List<IGraphUpdateException> exceptions;

    public IGraphUpdateException(String msg, Throwable t) {
        super(msg, t);
    }

    public IGraphUpdateException(String msg) {
        super(msg);
    }

    public IGraphUpdateException(String msg, List<IGraphUpdateException> exceptions) {
        super(msg);
        this.exceptions = exceptions;
    }

    public List<IGraphUpdateException> getExceptions() {
        return exceptions;
    }
}