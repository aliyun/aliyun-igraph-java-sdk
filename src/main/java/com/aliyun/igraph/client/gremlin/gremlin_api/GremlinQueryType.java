package com.aliyun.igraph.client.gremlin.gremlin_api;

/**
 * @author alibaba
 */
public enum GremlinQueryType {
    /**
     * 未知类型
     */
    UNKNOWN("Unknow"),
    /**
     * 查询
     */
    SEARCH("Search"),
    /**
     * 更新
     */
    UPDATE("Update"),
    /**
     * 删除
     */
    DELETE("Delete");

    String value;
    GremlinQueryType(String value) { this.value = value; }
    @Override
    public String toString() {
        return value;
    }
}
