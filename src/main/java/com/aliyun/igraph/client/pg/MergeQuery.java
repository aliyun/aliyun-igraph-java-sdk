package com.aliyun.igraph.client.pg;

import com.aliyun.igraph.client.exception.IGraphClientException;
import lombok.NonNull;

/**
 * MergeSearch的语义是把leftQuery和rightQuery的结果进行merge，注意， leftQuery和rightQuery的MatchRecord必须是同构的
 * 如果指定了sorter参数，那么MergeSearch会对merge之后的结果进行排序，这里使用用户定制的排序模块。
 * 
 * @author alibaba
 */
public class MergeQuery extends PGQuery {
    private PGQuery leftQuery;
    private PGQuery rightQuery;
    private String sorter;
    private String distinct;
    private String orderby;
    private String tag;
    private int rangeStart;
    private int rangeCount;
    private int leftTag;
    private int rightTag;

    MergeQuery(@NonNull PGQuery leftQuery, @NonNull PGQuery rightQuery, String sorter, String distinct, String orderby,
               String tag, Integer rangeStart, Integer rangeCount, Integer leftTag, Integer rightTag) {
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
        this.sorter = sorter;
        this.distinct = distinct;
        this.orderby = orderby;
        this.tag = tag;
        this.rangeStart = rangeStart;
        this.rangeCount = rangeCount;
        this.leftTag = leftTag;
        this.rightTag = rightTag;
    }

    public String toString() {
        String left = leftQuery.toString();
        String right = rightQuery.toString();
        StringBuilder ss = new StringBuilder(left.length() + right.length() + 128);
        ss.append('(').append(left);
        merge(ss);
        ss.append(right).append(')');
        leftmostAtomicQuery = leftQuery.getLeftmostAtomicQuery();
        return ss.toString();
    }

    private void merge(StringBuilder ss) {
        ss.append("merge{");
        boolean hasField = false;
        if (sorter != null) {
            ss.append("sorter=").append(sorter);
            hasField = true;
        }
        if (distinct != null) {
            if (hasField) {
                ss.append('&');
            } else {
                hasField = true;
            }
            ss.append("distinct=").append(distinct);
        }
        if (orderby != null) {
            if (hasField) {
                ss.append('&');
            } else {
                hasField = true;
            }
            ss.append("orderby=").append(orderby);
        }
        if (leftTag != 0 && rightTag != 0) {
            if (hasField) {
                ss.append('&');
            } else {
                hasField = true;
            }
            ss.append("tag=").append(leftTag).append(':').append(rightTag);
        } else if (tag != null) {
            if (hasField) {
                ss.append('&');
            } else {
                hasField = true;
            }
            ss.append("tag=").append(tag);
        }

        if (rangeStart != 0 || rangeCount != 0) {
            if (hasField) {
                ss.append('&');
            } else {
                hasField = true;
            }
            ss.append("range=").append(rangeStart).append(":").append(rangeCount);
        }
        ss.append('}');
    }

    public static MergeQueryBuilder builder() {
        return new MergeQueryBuilder();
    }

    public MergeQueryBuilder toBuilder() {
        MergeQueryBuilder builder = (new MergeQuery.MergeQueryBuilder()).leftQuery(leftQuery).rightQuery(rightQuery)
                .sorter(sorter).distinct(distinct).orderby(orderby).range(rangeStart, rangeCount).tag(tag);
        if (leftTag != 0 && rightTag != 0) {
            builder.tag(leftTag, rightTag);
        }
        return builder;
    }

    public static class MergeQueryBuilder {
        private PGQuery leftQuery;
        private PGQuery rightQuery;
        private String sorter;
        private String distinct;
        private String orderby;
        private String tag;
        private int rangeStart;
        private int rangeCount;
        private int leftTag;
        private int rightTag;

        MergeQueryBuilder() {
        }

        public MergeQueryBuilder leftQuery(PGQuery leftQuery) {
            this.leftQuery = leftQuery;
            return this;
        }

        public MergeQueryBuilder rightQuery(PGQuery rightQuery) {
            this.rightQuery = rightQuery;
            return this;
        }

        public MergeQueryBuilder sorter(String sorter) {
            this.sorter = sorter;
            return this;
        }

        public MergeQueryBuilder distinct(String distinct) {
            this.distinct = distinct;
            return this;
        }

        public MergeQueryBuilder orderby(String orderby) {
            this.orderby = orderby;
            return this;
        }

        public MergeQueryBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public MergeQueryBuilder tag(int leftTag, int rightTag) {
            if (leftTag <= 0 || leftTag >= 16 || rightTag <= 0 || rightTag >= 16) {
                throw new IGraphClientException("tag values among [0, 15], leftTag=[" + leftTag + "], rightTag=["
                        + rightTag + "] is invalid!");
            }
            this.leftTag = leftTag;
            this.rightTag = rightTag;
            return this;
        }

        public MergeQueryBuilder range(int rangeStart, int rangeCount) {
            if (rangeStart < 0 || rangeCount < 0) {
                throw new IGraphClientException("negative rangeStart or rangeCount is not allowed, rangeStart=[" + rangeStart + "], rangeCount=["
                        + rangeCount + "] is invalid!");
            }
            this.rangeStart = rangeStart;
            this.rangeCount = rangeCount;
            return this;
        }

        public MergeQuery build() {
            return new MergeQuery(leftQuery, rightQuery, sorter, distinct, orderby, tag, rangeStart, rangeCount, leftTag, rightTag);
        }
    }
}