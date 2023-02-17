package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Sample {
    duplicatable("Sample.duplicatable"),
    upsampling("Sample.upsampling");

    String value;

    Sample(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
