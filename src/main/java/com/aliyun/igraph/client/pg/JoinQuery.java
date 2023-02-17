package com.aliyun.igraph.client.pg;

import java.util.*;
import java.util.Map.Entry;

import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.exception.IGraphClientException;
import lombok.NonNull;

/**
 * @author alibaba
 */
public class JoinQuery extends PGQuery {
    private PGQuery leftQuery;
    private AtomicQuery rightQuery;
    private String leftJoinField;
    private String rightJoinField;
    private Map<String, String > aliases;
    private String filter;
    private String sorter;
    private String distinct;
    private String orderby;
    private String leftJoinTag;
    private List<String> fields;
    private int rangeStart;
    private int rangeCount;

    JoinQuery(@NonNull PGQuery leftQuery, @NonNull AtomicQuery rightQuery, @NonNull String leftJoinField,
              String rightJoinField, Map<String, String> aliases, String filter, String sorter, String distinct,
              String orderby, String leftJoinTag, List<String> fields, Integer rangeStart, Integer rangeCount) {
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
        this.leftJoinField = leftJoinField;
        this.rightJoinField = rightJoinField;
        this.aliases = aliases;
        this.filter = filter;
        this.sorter = sorter;
        this.distinct = distinct;
        this.orderby = orderby;
        this.leftJoinTag = leftJoinTag;
        this.fields = fields;
        this.rangeStart = rangeStart;
        this.rangeCount = rangeCount;
    }

    public String toString() {
        if (!rightQuery.isJoinQueryOnly()) {
            throw new IGraphQueryException("the right AtomicQuery of JoinQuery should not contain KeyList/indexSearch");
        }
        String left = leftQuery.toString();
        String right = rightQuery.toString();
        StringBuilder ss = new StringBuilder(left.length() + right.length() + 128);
        ss.append('(').append(left);
        join(ss);
        ss.append(right).append(')');
        leftmostAtomicQuery = leftQuery.getLeftmostAtomicQuery();
        return ss.toString();
    }

    private void join(StringBuilder ss) {
        ss.append("join{").append("joinfield=").append(leftJoinField);
        if (rightJoinField != null) {
            ss.append(";").append(rightJoinField);
        }
        if (leftJoinTag != null) {
            ss.append("&jointype=left_join&jointag=").append(leftJoinTag);
        }
        if (filter != null) {
            ss.append("&filter=").append(filter);
        }
        if (sorter != null) {
            ss.append("&sorter=").append(sorter);
        }
        if (distinct != null) {
            ss.append("&distinct=").append(distinct);
        }
        if (orderby != null) {
            ss.append("&orderby=").append(orderby);
        }

        if (rangeStart != 0 || rangeCount != 0) {
            ss.append("&range=").append(rangeStart).append(':').append(rangeCount);
        }
        if (aliases != null && !aliases.isEmpty()) {
            ss.append("&alias=");
            boolean first = true;
            for (Entry<String, String> entry : aliases.entrySet()) {
                if (!first) {
                    ss.append(";");
                }
                first = false;
                ss.append(entry.getKey());
                ss.append(":");
                ss.append(entry.getValue());
            }
        }
        if (fields != null && fields.size() > 0) {
            ss.append("&fields=");
            boolean first = true;
            for (String field : fields) {
                if (!first) {
                    ss.append(";");
                }
                first = false;
                ss.append(field);
            }
        }
        ss.append('}');
    }

    public static JoinQueryBuilder builder() {
        return new JoinQueryBuilder();
    }

    public JoinQueryBuilder toBuilder() {
       return (new JoinQueryBuilder()).leftQuery(leftQuery).rightQuery(rightQuery).leftJoinField(leftJoinField)
               .rightJoinField(rightJoinField).aliases(aliases).filter(filter).sorter(sorter).distinct(distinct)
               .orderby(orderby).leftJoinTag(leftJoinTag).fields(fields).range(rangeStart, rangeCount);
    }

    public static class JoinQueryBuilder {
        private PGQuery leftQuery;
        private AtomicQuery rightQuery;
        private String leftJoinField;
        private String rightJoinField;
        private Map<String, String> aliases;
        private String filter;
        private String sorter;
        private String distinct;
        private String orderby;
        private String leftJoinTag;
        private List<String> fields;
        private int rangeStart;
        private int rangeCount;

        JoinQueryBuilder() {
        }

        public JoinQueryBuilder leftQuery(PGQuery leftQuery) {
            this.leftQuery = leftQuery;
            return this;
        }

        public JoinQueryBuilder rightQuery(AtomicQuery rightQuery) {
            this.rightQuery = rightQuery;
            return this;
        }

        public JoinQueryBuilder leftJoinField(String leftJoinField) {
            this.leftJoinField = leftJoinField;
            return this;
        }

        public JoinQueryBuilder rightJoinField(String rightJoinField) {
            this.rightJoinField = rightJoinField;
            return this;
        }

        public JoinQueryBuilder alias(@NonNull String aliasKey, @NonNull String aliasValue) {
            if (aliases == null) {
                aliases = new HashMap<>();
            }
            aliases.put(aliasKey, aliasValue);
            return  this;
        }

        public JoinQueryBuilder aliases(Map<String, String> aliases) {
            if (aliases != null) {
                if (this.aliases == null) {
                    this.aliases = new HashMap<>();
                }
                for (Entry<String, String> entry : aliases.entrySet()) {
                    if (entry.getKey() == null) {
                        throw new NullPointerException("empty key for alias is not allowed!");
                    }
                    if (entry.getValue() == null) {
                        throw new NullPointerException("mpty value for alias is not allowed!");
                    }
                    this.aliases.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public JoinQueryBuilder clearAliases() {
            if (aliases != null) {
                aliases.clear();
            }
            return this;
        }

        public JoinQueryBuilder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public JoinQueryBuilder sorter(String sorter) {
            this.sorter = sorter;
            return this;
        }

        public JoinQueryBuilder distinct(String distinct) {
            this.distinct = distinct;
            return this;
        }

        public JoinQueryBuilder orderby(String orderby) {
            this.orderby = orderby;
            return this;
        }

        public JoinQueryBuilder leftJoinTag(String leftJoinTag) {
            this.leftJoinTag = leftJoinTag;
            return this;
        }

        public JoinQueryBuilder field(String field) {
            if (field != null) {
                if (this.fields == null) {
                    this.fields = new ArrayList<>();
                }
                this.fields.add(field);
            }
            return this;
        }

        public JoinQueryBuilder fields(List<String> fields) {
            if (fields != null) {
                if (this.fields == null) {
                    this.fields = new ArrayList<>();
                }
                this.fields.addAll(fields);
            }
            return this;
        }

        public JoinQueryBuilder clearFields() {
            if (fields != null) {
                fields.clear();
            }
            return this;
        }

        public JoinQueryBuilder range(int rangeStart, int rangeCount) {
            if (rangeStart < 0 || rangeCount < 0) {
                throw new IGraphClientException("negative rangeStart or rangeCount is not allowed, rangeStart=[" + rangeStart + "], rangeCount=["
                        + rangeCount + "] is invalid!");
            }
            this.rangeStart = rangeStart;
            this.rangeCount = rangeCount;
            return this;
        }

        public JoinQuery build() {
            return new JoinQuery(this.leftQuery, this.rightQuery, this.leftJoinField, this.rightJoinField, this.aliases, this.filter,
                    this.sorter, this.distinct, this.orderby, this.leftJoinTag, this.fields, this.rangeStart, this.rangeCount);
        }
    }
}