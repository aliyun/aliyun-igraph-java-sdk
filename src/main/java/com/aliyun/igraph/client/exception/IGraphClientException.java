package com.aliyun.igraph.client.exception;

/**
 * @author alibaba
 */
public class IGraphClientException extends RuntimeException {
    /**
     * The system line separator string.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /**
	 *
	 */
    private static final long serialVersionUID = 4314821141356896178L;
    private final int code;

    /**
     * @param msg
     *            the message
     */
    public IGraphClientException(String msg) {
        super(msg);
        this.code = -3;
    }

    /**
     *
     * @param code
     *            the error code
     * @param msg
     *            the message
     */
    public IGraphClientException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     *
     * @param msg
     *            the message
     * @param t
     *            the throwable cause
     */
    public IGraphClientException(String msg, Throwable t) {
        super(msg, t);
        this.code = -4;
    }

    /**
     *
     * @param code
     *            the error code
     * @param msg
     *            the message
     * @param t
     *            the throwable cause
     */
    public IGraphClientException(int code, String msg, Throwable t) {
        super(msg, t);
        this.code = code;
    }

    /**
     * Gets the exception code
     *
     * @return error code
     */
    public int getCode() {
        return this.code;
    }
}
