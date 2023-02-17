package com.aliyun.igraph.client.config;

import com.aliyun.igraph.client.core.model.QueryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;

/**
 * @author alibaba
 */
@Data
@NoArgsConstructor
public class QueryConfig {
    @NonNull
    protected String outfmt = "fb2";
    protected String src;
    protected String trace;
    protected String noCache;
    protected String cacheOnly;
    protected Map<String, Object> binding;
    /**
     * FIXME: why default is 0 ?
     */
    protected int searcherMaxSeekCount = 0;
    protected int retryTimes = 1;
    protected String cacheHotKey = "true";
    protected int timeoutInMs = 0;
    protected QueryType queryType = QueryType.PG;

    public String getConfigString() {
        StringBuilder ss = new StringBuilder("config{");
        ss.append("outfmt=").append(outfmt);
        if (trace != null) {
            ss.append("&trace=").append(trace);
        }
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
        if (cacheHotKey != null) {
            ss.append("&cache_hot_key=").append(cacheHotKey);
        }
        ss.append("}");
        return ss.toString();
    }

    public String getBindingString() {
        if (binding == null || binding.isEmpty()) {
            return "";
        }
        StringBuilder ss = new StringBuilder("&&bindings={");
        for (Map.Entry<String, Object> entry : binding.entrySet()) {
            ss.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        ss.setLength(ss.length() - 1);
        ss.append("}");
        return ss.toString();
    }
}
