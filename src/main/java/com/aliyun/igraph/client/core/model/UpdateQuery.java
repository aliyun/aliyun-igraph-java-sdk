package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.utils.URLCodecUtil;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author alibaba
 */
public class UpdateQuery extends Query {
    private String table;
    private String pkey;
    private String skey;
    private Map<String, String> valueMaps;
    private String charset;

    void table(String table) {
        this.table = table;
    }

    void pkey(String pkey) {
        this.pkey = pkey;
    }

    void skey(String skey) {
        this.skey = skey;
    }

    void valueMap(String key, String value) {
        if (this.valueMaps == null) {
            this.valueMaps = new HashMap<>();
        }
        valueMaps.put(key, value);
    }

    @Override
    public String toString() {
        if (null == table || null == pkey) {
            throw new IGraphQueryException("empty table/pkey is not allowed!");
        }
        if (null == charset) {
            charset = "UTF-8";
        }
        StringBuilder builder = new StringBuilder(128);
        builder.append("table=").append(table).
                append("&pkey=").append(URLCodecUtil.encode(pkey, charset));
        if (null != skey) {
            builder.append("&skey=").append(URLCodecUtil.encode(skey, charset));
        }
        if (null != valueMaps) {
            for (Map.Entry<String, String>entry : valueMaps.entrySet()) {
                builder.append("&").append(entry.getKey()).append("=");
                if (entry.getValue() != null) {
                    builder.append(URLCodecUtil.encode(entry.getValue(), charset));
                }
            }
        }
        return builder.toString();
    }

    UpdateQuery(@NonNull String table, String pkey, String skey, Map<String, String> valueMaps, String charset) {
        this.table = table;
        this.pkey = pkey;
        this.skey = skey;
        this.valueMaps = valueMaps;
        this.charset = charset;
    }

    public static UpdateQuery.UpdateQueryBuilder builder() {
        return new UpdateQuery.UpdateQueryBuilder();
    }

    public UpdateQuery.UpdateQueryBuilder toBuilder() {
        return (new UpdateQuery.UpdateQueryBuilder()).table(this.table).pkey(this.pkey).skey(this.skey).valueMaps(this.valueMaps).charset(this.charset);
    }

    public static class UpdateQueryBuilder {
        private String table;
        private String pkey;
        private String skey;
        private Map<String, String> valueMaps;
        private String charset;

        UpdateQueryBuilder() {
        }

        public UpdateQuery.UpdateQueryBuilder table(String table) {
            this.table = table;
            return this;
        }

        public UpdateQuery.UpdateQueryBuilder pkey(String pkey) {
            this.pkey = pkey;
            return this;
        }

        public UpdateQuery.UpdateQueryBuilder skey(String skey) {
            this.skey = skey;
            return this;
        }

        public UpdateQuery.UpdateQueryBuilder valueMap(@NonNull String key, @NonNull String value) {
            if (this.valueMaps == null) {
                this.valueMaps = new HashMap<>();
            }
            valueMaps.put(key, value);
            return this;
        }

        public UpdateQuery.UpdateQueryBuilder valueMaps(Map<String, String> valueMap) {
            this.valueMaps = valueMap;
            return this;
        }

        public UpdateQuery.UpdateQueryBuilder charset(String charset) {
            this.charset = charset;
            return this;
        }

        public UpdateQuery build() {
            return new UpdateQuery(table, pkey, skey, valueMaps, charset);
        }
    }
}
