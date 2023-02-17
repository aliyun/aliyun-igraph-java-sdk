package com.aliyun.igraph.client.pg;

import com.aliyun.igraph.client.exception.IGraphClientException;
import lombok.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author alibaba
 */
public class AtomicQuery extends PGQuery {
    private Map<String, String> aliases ;
    private String table ;
    private String keys ;
    private String filter ;
    private String orderby ;
    private String distinct ;
    private String sorter ;
    private List<String> fields;
    private int rangeStart ;
    private int rangeCount ;
    private int localCount;
    private List<KeyList> keyLists;
    private String indexSearch ;

    AtomicQuery(Map<String, String> aliases, @NonNull String table, String keys, String filter,
                String orderby, String distinct, String sorter, List<String> fields, int rangeStart,
                int rangeCount, int localCount, List<KeyList> keyLists, String indexSearch) {
        this.aliases = aliases;
        this.table = table;
        this.keys = keys;
        this.filter = filter;
        this.orderby = orderby;
        this.distinct = distinct;
        this.sorter = sorter;
        this.fields = fields;
        this.rangeStart = rangeStart;
        this.rangeCount = rangeCount;
        this.localCount = localCount;
        this.keyLists = keyLists;
        this.indexSearch = indexSearch;
    }

    public String getKeys() {
        if (null == keyLists && null == keys) {
            return null;
        }
        if (null == this.keys) {
            StringBuilder ss = new StringBuilder();
            boolean first = true;
            for (KeyList keyList : keyLists) {
                if (!first) {
                    ss.append(";");
                }
                first = false;
                ss.append(keyList.toString());
            }
            this.keys = ss.toString();
        }
        return this.keys;
    }

    public List<KeyList> getKeyLists() { return this.keyLists; }

    public String toString() {
        leftmostAtomicQuery = this;
        return toString("search");
    }

    public String toString(String queryName) {
        StringBuilder ss = new StringBuilder(128);
        ss.append(queryName).append("{table=").append(table);
        keys = getKeys();
        if (keys != null) {
            ss.append("&keys=").append(keys);
        }
        if (localCount > 0) {
            ss.append("&localcount=").append(localCount);
        }
        if (indexSearch != null) {
            ss.append("&indexsearch=").append(indexSearch);
        }
        if (filter != null) {
            ss.append("&filter=").append(filter);
        }
        if (orderby != null) {
            ss.append("&orderby=").append(orderby);
        }
        if (sorter != null) {
            ss.append("&sorter=").append(sorter);
        }
        if (distinct != null) {
            ss.append("&distinct=").append(distinct);
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
        if (rangeStart != 0 || rangeCount != 0) {
            ss.append("&range=").append(rangeStart).append(":").append(rangeCount);
        }
        ss.append('}');
        return ss.toString();
    }

    public boolean isJoinQueryOnly() {
        return getKeys() == null && indexSearch == null;
    }

    public static AtomicQueryBuilder builder() {
        return new AtomicQueryBuilder();
    }

    public AtomicQueryBuilder toBuilder() {
        return (new AtomicQueryBuilder()).aliases(aliases).table(table).keys(keys).filter(filter)
                .orderby(orderby).distinct(distinct).sorter(sorter).fields(fields).range(rangeStart, rangeCount)
                .localCount(localCount).keyLists(keyLists).indexSearch(indexSearch);
    }

    public static class AtomicQueryBuilder {
        private Map<String, String> aliases;
        private String table;
        private String keys;
        private String filter;
        private String orderby;
        private String distinct;
        private String sorter;
        private ArrayList<String> fields;
        private int rangeStart;
        private int rangeCount;
        private int localCount = 0;
        private ArrayList<KeyList> keyLists;
        private String indexSearch;

        AtomicQueryBuilder() {
        }

        public AtomicQueryBuilder alias(@NonNull String aliasKey, @NonNull String aliasValue) {
            if (this.aliases == null) {
                this.aliases = new HashMap<>();
            }
            this.aliases.put(aliasKey, aliasValue);
            return this;
        }

        public AtomicQueryBuilder aliases(Map<String, String> aliases) {
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

        public AtomicQueryBuilder clearAliases() {
            if (aliases != null) {
                aliases.clear();
            }
            return this;
        }

        public AtomicQueryBuilder table(String table) {
            this.table = table;
            return this;
        }

        public AtomicQueryBuilder keys(String keys) {
            this.keys = keys;
            return this;
        }

        public AtomicQueryBuilder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public AtomicQueryBuilder orderby(String orderby) {
            this.orderby = orderby;
            return this;
        }

        public AtomicQueryBuilder distinct(String distinct) {
            this.distinct = distinct;
            return this;
        }

        public AtomicQueryBuilder sorter(String sorter) {
            this.sorter = sorter;
            return this;
        }

        public AtomicQueryBuilder field(String field) {
            if (field != null) {
                if (this.fields == null) {
                    this.fields = new ArrayList<>();
                }
                this.fields.add(field);
            }
            return this;
        }

        public AtomicQueryBuilder fields(List<String> fields) {
            if (fields != null) {
                if (this.fields == null) {
                    this.fields = new ArrayList<>();
                }
                this.fields.addAll(fields);
            }
            return this;
        }

        public AtomicQueryBuilder clearFields() {
            if (fields != null) {
                fields.clear();
            }
            return this;
        }

        public AtomicQueryBuilder range(int rangeStart, int rangeCount) {
            if (rangeStart < 0 || rangeCount < 0) {
                throw new IGraphClientException("negative rangeStart or rangeCount is not allowed, rangeStart=[" + rangeStart + "], rangeCount=["
                        + rangeCount + "] is invalid!");
            }
            this.rangeStart = rangeStart;
            this.rangeCount = rangeCount;
            return this;
        }

        public AtomicQueryBuilder localCount(int localCount) {
            this.localCount = localCount;
            return this;
        }

        public AtomicQueryBuilder keyList(KeyList keyList) {
            if (keyList != null) {
                if (this.keyLists == null) {
                    this.keyLists = new ArrayList<>();
                }
                this.keyLists.add(keyList);
            }
            return this;
        }

        public AtomicQueryBuilder keyLists(List<KeyList> keyLists) {
            if (keyLists != null) {
                if (this.keyLists == null) {
                    this.keyLists = new ArrayList<>();
                }
                this.keyLists.addAll(keyLists);
            }
            return this;
        }

        public AtomicQueryBuilder clearKeyLists() {
            if (keyLists != null) {
                keyLists.clear();
            }

            return this;
        }

        public AtomicQueryBuilder indexSearch(String indexSearch) {
            this.indexSearch = indexSearch;
            return this;
        }

        public AtomicQuery build() {
            return new AtomicQuery(aliases, table, keys, filter, orderby, distinct, sorter, fields, rangeStart,
                    rangeCount, localCount, keyLists, indexSearch);
        }
    }
}