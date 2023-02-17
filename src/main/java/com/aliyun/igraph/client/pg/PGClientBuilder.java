package com.aliyun.igraph.client.pg;

import java.util.concurrent.ThreadFactory;

import com.aliyun.igraph.client.config.ClientConfig;
import com.aliyun.igraph.client.exception.IGraphClientException;
import com.aliyun.igraph.client.net.Requester;
import com.aliyun.igraph.client.net.RequesterConfig;
import com.aliyun.igraph.client.utils.NetUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author alibaba
 */
@Data
@Slf4j
public class PGClientBuilder {
    private RequesterConfig requesterConfig = new RequesterConfig();
    private ClientConfig clientConfig = new ClientConfig();

    public static PGClientBuilder create() {
        return new PGClientBuilder();
    }

    public PGClient build(@NonNull String src, @NonNull String endpoint,
        @NonNull String userName, @NonNull String userPasswd) {
        if (src.isEmpty() || endpoint.isEmpty()) {
            throw new IGraphClientException("Failed to build IGraphClient with invalid args:[src:[" + src
                + "], endpoint:[" + endpoint + "]]");
        }
        Requester requester = new Requester(requesterConfig);
        clientConfig.setSrc(src);
        clientConfig.setSearchDomain(endpoint);
        clientConfig.setUpdateDomain(endpoint);
        clientConfig.setLocalAddress(NetUtils.getIntranetIp());
        clientConfig.setUserName(userName);
        clientConfig.setUserPasswd(userPasswd);
        return new PGClient(clientConfig, requester);
    }

    public void setRetryTimes(int retryTimes) {
        clientConfig.setRetryTimes(retryTimes);
    }

    public void setSocketTimeout(int socketTimeout) {
        requesterConfig.getHttpConnectionConfig().setSocketTimeout(socketTimeout);
    }

    public void setConnectTimeout(int connectTimeout) {
        requesterConfig.getHttpConnectionConfig().setConnectTimeout(connectTimeout);
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        requesterConfig.getHttpConnectionConfig().setConnectionRequestTimeout(connectionRequestTimeout);
    }

    public void setMaxConnTotal(int maxConnTotal) {
        requesterConfig.setMaxConnTotal(maxConnTotal);
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        requesterConfig.setMaxConnPerRoute(maxConnPerRoute);
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        requesterConfig.setThreadFactory(threadFactory);
    }
}
