package com.aliyun.igraph.client.net;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author alibaba
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpConnectionConfig {
    private static final int DEFAULT_TIMEOUT_MS = 3000;
    private static final int DEFAULT_TIMEOUT_60_S = 60000;
    private int socketTimeout = DEFAULT_TIMEOUT_MS;
    private int connectTimeout = DEFAULT_TIMEOUT_MS;
    private int connectionRequestTimeout = DEFAULT_TIMEOUT_MS;
    private int connectionIdleTimeout = DEFAULT_TIMEOUT_60_S;

    public HttpConnectionConfig(HttpConnectionConfig other) {
        this.socketTimeout = other.socketTimeout;
        this.connectTimeout = other.connectTimeout;
        this.connectionRequestTimeout = other.connectionRequestTimeout;
        this.connectionIdleTimeout = other.connectionIdleTimeout;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof HttpConnectionConfig)) {
            return false;
        } else {
            HttpConnectionConfig other = (HttpConnectionConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getSocketTimeout() != other.getSocketTimeout()) {
                return false;
            } else if (this.getConnectTimeout() != other.getConnectTimeout()) {
                return false;
            } else {
                return this.getConnectionRequestTimeout() == other.getConnectionRequestTimeout();
            }
        }
    }

    public boolean canEqual(Object other) {
        return other instanceof HttpConnectionConfig;
    }
}
