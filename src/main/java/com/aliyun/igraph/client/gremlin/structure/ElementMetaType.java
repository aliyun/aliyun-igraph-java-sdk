package com.aliyun.igraph.client.gremlin.structure;

/**
 * @author alibaba
 */
public enum ElementMetaType {
    ENTITY(1L), PROPERTY(2L);
    Long value;

    ElementMetaType(Long value) {
        this.value = value;
    }
    public Long toLong() {
        return this.value;
    }
}
