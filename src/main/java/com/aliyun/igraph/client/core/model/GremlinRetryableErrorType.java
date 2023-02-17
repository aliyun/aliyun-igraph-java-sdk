package com.aliyun.igraph.client.core.model;

/**
 * @author alibaba
 */
public enum GremlinRetryableErrorType {
    /**
     * retryable error type
     */
    ERROR_LEFT_EXECUTOR_EXECUTE_TIMEOUT(91L),
    ERROR_RIGHT_EXECUTOR_EXECUTE_TIMEOUT(92L),
    ERROR_HANDLE_SEARCH_TIMEOUT(501L),
    ERROR_ATOMIC_EXECUTOR_EXECUTE_TIMEOUT(503L),
    ERROR_TIMEOUT_PACKET(5101L),
    ERROR_SOME_PARTITIONS_FAILED(5211L),
    ERROR_REQUEST_RESPONSE_TIMEOUT(10027L);
    Long value;

    GremlinRetryableErrorType(Long value) {
        this.value = value;
    }

    public Long getValue() {return value;}

    @Override
    public String toString() {
        return value.toString();
    }
}
