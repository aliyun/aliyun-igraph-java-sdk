package com.aliyun.igraph.client.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.aliyun.igraph.client.config.ClientConfig;
import com.aliyun.igraph.client.config.QueryConfig;
import com.aliyun.igraph.client.config.UpdateConfig;
import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.exception.IGraphUpdateException;
import com.aliyun.igraph.client.utils.URLCodecUtil;
import com.aliyun.igraph.client.core.model.Query;
import com.aliyun.igraph.client.core.model.UpdateQuery;
import com.aliyun.igraph.client.net.Requester;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Response;

/**
 * @author alibaba
 */
@Data
@Slf4j
public class IGraphClient {
    protected static final Integer TYPE_UPDATE = 1;
    protected static final Integer TYPE_DELETE = 2;

    protected ClientConfig config;
    protected Requester requester;
    protected Executor executor = Executors.newFixedThreadPool(10);

    protected IGraphClient(@NonNull ClientConfig config, @NonNull Requester requester) {
        this.config = config;
        this.requester = requester;
        log.info("iGraph client init : " + config);
    }

    /**
     * @param updateSession 与这次请求相关的Context
     * @param updateQuery        单条更新query
     * @throws Exception 所有的错误将会以异常的形式给出
     * @see #doUpdateSync(UpdateSession, Integer, UpdateQuery)
     */
    public void updateSync(@NonNull UpdateSession updateSession,
        @NonNull UpdateQuery updateQuery) throws Exception {
        doUpdateSync(updateSession, TYPE_UPDATE, updateQuery);
    }

    /**
     * @param updateSession 与这次请求相关的Context
     * @param updateQuery        单条更新query
     * @return CompletableFuture
     * @throws Exception 所有的错误将会以异常的形式给出
     * @see #doUpdateAsync(UpdateSession, Integer, UpdateQuery )
     */
    public CompletableFuture<Response> updateAsync(@NonNull UpdateSession updateSession,
        @NonNull UpdateQuery updateQuery) throws Exception {
        return doUpdateAsync(updateSession, TYPE_UPDATE, updateQuery);
    }

    /**
     * @param updateSession 与这次请求相关的Context
     * @param updateQuery        单条删除query
     * @throws Exception 所有的错误将会以异常的形式给出
     * @see #doUpdateSync(UpdateSession, Integer, UpdateQuery )
     */
    public void deleteSync(@NonNull UpdateSession updateSession,
        @NonNull UpdateQuery updateQuery) throws Exception {
        doUpdateSync(updateSession, TYPE_DELETE, updateQuery);
    }

    /**
     * @param updateSession 与这次请求相关的Context
     * @param updateQuery        单条删除query
     * @return CompletableFuture
     * @throws Exception 所有的错误将会以异常的形式给出
     * @see #doUpdateAsync(UpdateSession, Integer, UpdateQuery )
     */
    public CompletableFuture<Response> deleteAsync(@NonNull UpdateSession updateSession,
        @NonNull UpdateQuery updateQuery) throws Exception {
        return doUpdateAsync(updateSession, TYPE_DELETE, updateQuery);
    }

    public ClientConfig getConfig() {
        return config;
    }

    public void close() {
        requester.close();
    }

    protected void encodeRequest(RequestContext requestContext, QueryConfig queryConfig, Query... queries) {
        if (queries.length == 0) {
            log.warn("empty query!");
            throw new IGraphQueryException("empty query!");
        }
        if (requestContext.getRequestContent() != null) {
            return;
        }
        requestContext.beginQueryEncode();
        StringBuilder ss = new StringBuilder();
        ss.append(queryConfig.getConfigString()).append("&&");
        boolean isFirst = true;
        for (Query query : queries) {
            if (isFirst) {
                isFirst = false;
            } else {
                ss.append("||");
            }
            ss.append(query.toString());
        }
        String src = queryConfig.getSrc();
        if (null == src) {
            src = config.getSrc();
        }
        ss.append(queryConfig.getBindingString());
        buildSearchRequest(requestContext, ss.toString(), queryConfig.getQueryType().toString(), src);
        requestContext.endQueryEncode();
    }

    protected void encodeRequest(RequestContext requestContext, QueryConfig queryConfig, String submitId, Query... queries) {
        if (queries.length == 0) {
            log.warn("empty query!");
            throw new IGraphQueryException("empty query!");
        }
        if (requestContext.getRequestContent() != null) {
            return;
        }
        requestContext.beginQueryEncode();
        StringBuilder ss = new StringBuilder();
        ss.append(queryConfig.getConfigString()).append("&&");
        boolean isFirst = true;
        for (Query query : queries) {
            if (isFirst) {
                isFirst = false;
            } else {
                ss.append("||");
            }
            ss.append(query.toString());
        }
        String src = queryConfig.getSrc();
        if (null == src) {
            src = config.getSrc();
        }
        ss.append(queryConfig.getBindingString());
        buildSearchRequest(requestContext, ss.toString(), queryConfig.getQueryType().toString(), src, submitId);
        requestContext.endQueryEncode();
    }

    private void doUpdateSync(UpdateSession updateSession,
                              Integer updateType,
                              UpdateQuery updateQuery) throws IGraphUpdateException {
        RequestContext requestContext = updateSession.getRequestContext();
        UpdateConfig updateConfig = updateSession.getUpdateConfig();
        requestContext.setServerAddress(config.getUpdateDomain());
        buildUpdateRequest(requestContext, updateConfig, updateType, updateQuery);
        byte[] bytes = requester.sendRequest(requestContext, updateConfig.getTimeoutInMs(), config.getUserAuth(), false);
        String response = new String(bytes);
        if (!response.contains("<errno>0</errno>") && !response.contains("\"errno\":0")) {
            throw new IGraphUpdateException("update failed, with response:[" + response + "]");
        }
    }

    protected CompletableFuture<Response> doUpdateAsync(UpdateSession updateSession,
                                                        Integer updateType,
                                                        UpdateQuery updateQuery) throws IGraphUpdateException {
        RequestContext requestContext = updateSession.getRequestContext();
        UpdateConfig updateConfig = updateSession.getUpdateConfig();
        requestContext.setServerAddress(config.getUpdateDomain());
        buildUpdateRequest(requestContext, updateConfig, updateType, updateQuery);
        CompletableFuture<Response> responseFuture = requester.sendRequestAsync(requestContext, updateConfig.getTimeoutInMs(), config.getUserAuth(), false);

        return responseFuture.whenCompleteAsync((response, exception) -> {
            if (null != exception) {
                throw new IGraphServerException("Failed to sendRequest with Exception:[" + exception.getMessage()
                    + "] and requestContext:[" + requestContext + "]", exception);
            }
            byte[] bytes = requester.handleResponse(requestContext, response);
            String responseBody = new String(bytes);
            if (!responseBody.contains("<errno>0</errno>") && !responseBody.contains("\"errno\":0")) {
                throw new IGraphUpdateException("update failed, with response:[" + responseBody + "]");
            }
        }, executor);
    }

    private void buildSearchRequest(RequestContext requestContext, String queryString, String appName, String src) {
        StringBuilder builder = new StringBuilder(128);
        builder.append("/app?app=").append(appName).
            append("&src=").append(src).
            append("&ip=").append(config.getLocalAddress()).
            append("&ver=java_").append(config.getClientVersion()).
            append('?').append(URLCodecUtil.encode(queryString));
        requestContext.setRequestContent(builder.toString());
    }

    private void buildSearchRequest(RequestContext requestContext, String queryString, String appName, String src, String submitId) {
        StringBuilder builder = new StringBuilder(128);
        builder.append("/app?app=").append(appName).
            append("&src=").append(src).
            append("&submitId=").append(submitId).
            append("&ip=").append(config.getLocalAddress()).
            append("&ver=java_").append(config.getClientVersion()).
        append('?').append(URLCodecUtil.encode(queryString));
        requestContext.setRequestContent(builder.toString());
    }

    protected void buildUpdateRequest(RequestContext requestContext, UpdateConfig updateConfig, Integer updateType, UpdateQuery updateQuery) {
        requestContext.beginQueryEncode();
        String src = updateConfig.getSrc();
        if (null == src) {
            src = config.getSrc();
        }
        StringBuilder builder = new StringBuilder(128);
        builder.append("/update?type=").append(updateType).
            append("&_src=").append(src).
            append("&_ver=java_").append(config.getClientVersion()).
            append("&").append(updateQuery.toString()).
            append("&_message_time_stamp=").append(System.currentTimeMillis()).append("000");
        requestContext.setRequestContent(builder.toString());
        requestContext.endQueryEncode();
    }
}
