package com.aliyun.igraph.client.core.model.type;

import org.apache.tinkerpop.gremlin.driver.Result;

import java.util.Map;

/**
 * @author alibaba
 */
public class BulkSet {
    private Map<Result, Long> value;

    public BulkSet(Map<Result, Long> value) {
        this.value = value;
    }

    public Map<Result, Long> getValue() {
        return value;
    }
}
