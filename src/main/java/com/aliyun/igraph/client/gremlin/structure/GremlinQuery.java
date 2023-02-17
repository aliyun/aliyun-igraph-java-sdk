package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.core.model.Query;
import com.aliyun.igraph.client.gremlin.gremlin_api.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;

/**
 * @author alibaba
 */
public class GremlinQuery extends Query {
    private String rawQuery;
    private GraphTraversal graphTraversal = null;

    public GremlinQuery() {}

    public GremlinQuery(Traversal traversal) {
        this.graphTraversal = (GraphTraversal)traversal;
    }

    public GremlinQuery(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    @Override
    public String toString() {
        if (null != graphTraversal) {
            return graphTraversal.toString();
        }
        return this.rawQuery;
    }

    public void setRawQuery(String rawQuery) {
        this.rawQuery = rawQuery;
    }

    public void setKeys(String... keys) {
        if (null != graphTraversal) {
            graphTraversal.setKeys(keys);
        }
    }

    @Deprecated
    public String getKeys() {
        if (null != graphTraversal) {
            return graphTraversal.getKeys();
        }
        return null;
    }
}
