package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum Direction {
    OUT("Direction.OUT"),
    IN("Direction.IN"),
    BOTH("Direction.BOTH");

    String value;

    Direction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
