package com.aliyun.igraph.client.gremlin.gremlin_api;
/**
 * @author alibaba
 */
public enum Distinct {
    round("Distinct.round"),
    amount("Distinct.amount"),
    isReserved("Distinct.isReserved");

    String value;

    Distinct(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}