package com.aliyun.igraph.client.core.model;

/**
 * @author alibaba
 */
public enum QueryType {
    /**
     * query type
     */
    PG("pg"),
    GREMLIN("gremlin"),
    STQ("stq");

    private String value;

    QueryType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}