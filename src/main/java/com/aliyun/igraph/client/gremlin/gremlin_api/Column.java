package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Column {
    values("Column.values"),
    keys("Column.keys");

    String value;

    Column(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
