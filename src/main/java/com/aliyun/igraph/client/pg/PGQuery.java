package com.aliyun.igraph.client.pg;

import com.aliyun.igraph.client.core.model.Query;

/**
 * @author alibaba
 */
public class PGQuery extends Query {

    /**
     *
     * 从性能方面考量，调用方需要保证各个Query中List的add顺序，equals函数不会对Query中的List字句进行排序
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PGQuery)) {
            return false;
        }

        PGQuery pgQuery = (PGQuery) o;

        return this.toString().equals(pgQuery.toString());

    }

    public AtomicQuery getLeftmostAtomicQuery() {
        return leftmostAtomicQuery;
    }

    protected transient AtomicQuery leftmostAtomicQuery;
}
