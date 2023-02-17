package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Operator {
    assign("Operator.assign"),
    max("Operator.max"),
    min("Operator.min"),
    sum("Operator.sum"),
    minus("Operator.minus"),
    mult("Operator.mult"),
    div("Operator.div"),
    addall("Operator.addall");

    String value;

    Operator(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
