package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Scope {
    global("Scope.global"),
    local("Scope.local");

    String value;

    Scope(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
