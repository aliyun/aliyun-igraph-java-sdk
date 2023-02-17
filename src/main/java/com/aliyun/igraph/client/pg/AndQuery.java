package com.aliyun.igraph.client.pg;

import lombok.NonNull;

/**
 * @author alibaba
 */
public class AndQuery extends PGQuery {
    private PGQuery leftQuery;
    private PGQuery rightQuery;
    private String andField;

    AndQuery(@NonNull PGQuery leftQuery, @NonNull PGQuery rightQuery, @NonNull String andField) {
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
        this.andField = andField;
    }

    public String toString() {
        String left = leftQuery.toString();
        String right = rightQuery.toString();
        StringBuilder ss = new StringBuilder(left.length() + right.length() + 32);
        ss.append('(').append(left);
        and(ss);
        ss.append(right).append(')');
        leftmostAtomicQuery = leftQuery.getLeftmostAtomicQuery();
        return ss.toString();
    }

    private void and(StringBuilder ss) {
        ss.append("and{andfield=").append(andField).append('}');
    }

    public static AndQueryBuilder builder() {
        return new AndQueryBuilder();
    }

    public AndQueryBuilder toBuilder() {
        return (new AndQueryBuilder()).leftQuery(leftQuery).rightQuery(rightQuery).andField(andField);
    }

    public static class AndQueryBuilder {
        private PGQuery leftQuery;
        private PGQuery rightQuery;
        private String andField;

        AndQueryBuilder() {
        }

        public AndQueryBuilder leftQuery(@NonNull PGQuery leftQuery) {
            this.leftQuery = leftQuery;
            return this;
        }

        public AndQueryBuilder rightQuery(@NonNull PGQuery rightQuery) {
            this.rightQuery = rightQuery;
            return this;
        }

        public AndQueryBuilder andField(@NonNull String andField) {
            this.andField = andField;
            return this;
        }

        public AndQuery build() {
            return new AndQuery(leftQuery, rightQuery, andField);
        }
    }
}