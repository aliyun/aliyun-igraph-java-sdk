package com.aliyun.igraph.client.pg;

import lombok.NonNull;

/**
 * @author alibaba
 */
public class AndNotQuery extends PGQuery {
    private PGQuery leftQuery;
    private PGQuery rightQuery;
    private String andNotField;

    AndNotQuery(@NonNull PGQuery leftQuery, @NonNull PGQuery rightQuery, @NonNull String andNotField) {
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
        this.andNotField = andNotField;
    }

    private void andNot(StringBuilder ss) {
        ss.append("andnot{andnotfield=").append(andNotField).append('}');
    }

    public String toString() {
        String left = leftQuery.toString();
        String right = rightQuery.toString();
        StringBuilder ss = new StringBuilder(left.length() + right.length() + 32);
        ss.append('(').append(left);
        andNot(ss);
        ss.append(right).append(')');
        leftmostAtomicQuery = leftQuery.getLeftmostAtomicQuery();
        return ss.toString();
    }

    public static AndNotQueryBuilder builder() {
        return new AndNotQueryBuilder();
    }

    public AndNotQueryBuilder toBuilder() {
        return (new AndNotQueryBuilder()).leftQuery(leftQuery).rightQuery(rightQuery).andNotField(andNotField);
    }

    public static class AndNotQueryBuilder {
        private PGQuery leftQuery;
        private PGQuery rightQuery;
        private String andNotField;

        AndNotQueryBuilder() {
        }

        public AndNotQueryBuilder leftQuery(PGQuery leftQuery) {
            this.leftQuery = leftQuery;
            return this;
        }

        public AndNotQueryBuilder rightQuery(PGQuery rightQuery) {
            this.rightQuery = rightQuery;
            return this;
        }

        public AndNotQueryBuilder andNotField(String andNotField) {
            this.andNotField = andNotField;
            return this;
        }

        public AndNotQuery build() {
            return new AndNotQuery(leftQuery, rightQuery, andNotField);
        }
    }
}