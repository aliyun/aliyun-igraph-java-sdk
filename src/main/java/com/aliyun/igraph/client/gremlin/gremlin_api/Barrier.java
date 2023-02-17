package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Barrier {
    dedup("Barrier.dedup"),
    nodedup("Barrier.nodedup"),
    normSack("Barrier.normSack");

    String value;

    Barrier(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
