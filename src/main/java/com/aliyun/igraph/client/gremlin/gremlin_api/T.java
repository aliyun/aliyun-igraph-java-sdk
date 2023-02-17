package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum T {
    id("T.id"),
    label("T.label"),
    key("T.key"),
    value("T.value");

    String val;

    T(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return this.val;
    }
}
