package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Join {
    left("Join.left"),
    innner("Join.innner");

    String value;

    Join(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
