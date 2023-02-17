package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Pick {
    any("any"),
    none("none");

    String value;

    Pick(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
