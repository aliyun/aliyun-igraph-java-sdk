package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Order {
    incr("Order.incr"),
    decr("Order.decr"),
    shuffle("Order.shuffle");

    String value;

    Order(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
