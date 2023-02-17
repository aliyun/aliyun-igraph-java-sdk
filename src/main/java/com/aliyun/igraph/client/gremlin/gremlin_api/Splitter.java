package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Splitter {
    identity("Splitter.identity"),
    clone("Splitter.clone"),
    fastclone("Splitter.fastclone");

    String value;

    Splitter(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
