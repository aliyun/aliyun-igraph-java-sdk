package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Pop {
    first("Pop.first"),
    last("Pop.last"),
    all("Pop.all");

    String value;

    Pop(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
