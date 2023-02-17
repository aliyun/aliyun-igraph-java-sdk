package com.aliyun.igraph.client.net;

import lombok.Data;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import java.util.concurrent.ThreadFactory;

/**
 * @author alibaba
 */
@Data
public class RequesterConfig {
    private static final int DEFAULT_MAX_CONNECTION_TOTAL = 1024;
    private static final int DEFAULT_CONNECTION_PER_ROUTE = 200;

    private HttpConnectionConfig httpConnectionConfig = new HttpConnectionConfig();
    private int maxConnTotal = DEFAULT_MAX_CONNECTION_TOTAL;
    private int maxConnPerRoute = DEFAULT_CONNECTION_PER_ROUTE;
    private ThreadFactory threadFactory;
    private DefaultAsyncHttpClientConfig.Builder asyncHttpClientConfigBuilder;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RequesterConfig)) {
            return false;
        } else {
            RequesterConfig other = (RequesterConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object thisHttpConnectionConfig = this.getHttpConnectionConfig();
                Object otherHttpConnectionConfig = other.getHttpConnectionConfig();
                if (thisHttpConnectionConfig == null) {
                    if (otherHttpConnectionConfig != null) {
                        return false;
                    }
                } else if (!thisHttpConnectionConfig.equals(otherHttpConnectionConfig)) {
                    return false;
                }

                if (this.getMaxConnTotal() != other.getMaxConnTotal()) {
                    return false;
                } else if (this.getMaxConnPerRoute() != other.getMaxConnPerRoute()) {
                    return false;
                } else {
                    label57: {
                        Object thisThreadFactory = this.getThreadFactory();
                        Object otherThreadFactory = other.getThreadFactory();
                        if (thisThreadFactory == null) {
                            if (otherThreadFactory == null) {
                                break label57;
                            }
                        } else if (thisThreadFactory.equals(otherThreadFactory)) {
                            break label57;
                        }

                        return false;
                    }

                    Object thisAsyncHttpClientConfigBuilder = this.getAsyncHttpClientConfigBuilder();
                    Object otherAsyncHttpClientConfigBuilder = other.getAsyncHttpClientConfigBuilder();
                    if (thisAsyncHttpClientConfigBuilder == null) {
                        return otherAsyncHttpClientConfigBuilder == null;
                    } else {
                        return thisAsyncHttpClientConfigBuilder.equals(otherAsyncHttpClientConfigBuilder);
                    }
                }
            }
        }
    }

    public boolean canEqual(Object other) {
        return other instanceof RequesterConfig;
    }

    public RequesterConfig() {
        asyncHttpClientConfigBuilder = new DefaultAsyncHttpClientConfig.Builder();
        asyncHttpClientConfigBuilder.setDisableUrlEncodingForBoundRequests(true);
//        asyncHttpClientConfigBuilder.setAllowPoolingConnections(true);
    }
}
