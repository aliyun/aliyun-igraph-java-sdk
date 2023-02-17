package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum PathRecord {
    vertex("PathRecord.vertex"),
    edge("PathRecord.edge"),
    map("PathRecord.map");

    String val;

    PathRecord(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return this.val;
    }
}
