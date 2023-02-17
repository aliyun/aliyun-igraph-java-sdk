package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum StrategyWithoutParam implements StrategyBase {
    UniqueStrategy("UniqueStrategy");

    String val;

    StrategyWithoutParam(String val) {
        this.val = val;
    }

    public String toSring() {
        return val;
    }
}

