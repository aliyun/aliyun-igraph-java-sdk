package com.aliyun.igraph.client.config;

import com.aliyun.igraph.client.core.model.QueryType;

/**
 * @author alibaba
 */
public class GremlinConfig extends QueryConfig {
    public GremlinConfig() {
        this.outfmt = "fb2";
        this.noCache = "false";
        this.cacheOnly = "false";
        this.queryType = QueryType.GREMLIN;
    }

    @Override
    public String getConfigString() {
        StringBuilder ss = new StringBuilder("config{");
        ss.append("outfmt=").append(outfmt);
        if (noCache != null) {
            ss.append("&no_cache=").append(noCache);
        }
        if (cacheOnly != null) {
            ss.append("&cache_only=").append(cacheOnly);
        }
        if (searcherMaxSeekCount > 0) {
            ss.append("&searcher_max_seek_count=").append(searcherMaxSeekCount);
        }
        if (timeoutInMs > 0) {
            ss.append("&request_timeout=").append(timeoutInMs);
        }
        ss.append("}");
        return ss.toString();
    }
}
