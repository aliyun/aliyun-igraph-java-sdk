package com.aliyun.igraph.client.core.model;

import lombok.NonNull;

public class SetUpdateQueryHelper {
    static public void table(UpdateQuery updateQuery, String table) {
        updateQuery.table(table);
    }

    static public void pkey(UpdateQuery updateQuery, String pkey) {
        updateQuery.pkey(pkey);
    }

    static public void skey(UpdateQuery updateQuery, String skey) {
        updateQuery.skey(skey);
    }

    static public void valueMap(UpdateQuery updateQuery, @NonNull String key, @NonNull String value) {
        updateQuery.valueMap(key, value);
    }
}
