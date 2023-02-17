package com.aliyun.igraph.client.pg;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.config.ClientConfig;
import com.aliyun.igraph.client.config.PGConfig;
import com.aliyun.igraph.client.core.IGraphClient;
import com.aliyun.igraph.client.core.IGraphResultParser;
import com.aliyun.igraph.client.core.RequestContext;
import com.aliyun.igraph.client.net.Requester;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Response;

/**
 * @author alibaba
 */
@Slf4j
public class PGClient extends IGraphClient {

    public PGClient(@NonNull ClientConfig config,
        @NonNull Requester requester) {
        super(config, requester);
    }

    /**
     * @param pgSession 与这次请求相关的Context
     * @param queries        一个或多个query
     * @return 请求结果
     * @throws Exception 所有的错误将会以异常的形式给出
     * @see #doSearchSync(PGSession, PGQuery...)
     */
    public QueryResult searchSync(@NonNull PGSession pgSession,
        @NonNull PGQuery... queries) throws Exception {
        return doSearchSync(pgSession, queries);
    }

    /**
     * @param pgSession 与这次请求相关的Context
     * @param queries        一个或多个query
     * @return 请求结果
     * @throws Exception 所有的错误将会以异常的形式给出
     * @see #doSearchAsync(PGSession, PGQuery...)
     */
    public CompletableFuture<QueryResult> searchAsync(@NonNull PGSession pgSession,
        @NonNull PGQuery... queries) throws Exception {
        return doSearchAsync(pgSession, queries);
    }


    private QueryResult doSearchSync(@NonNull PGSession pgSession,
        @NonNull PGQuery... queries) throws Exception {
        RequestContext requestContext = pgSession.getRequestContext();
        PGConfig pgConfig = pgSession.getPgConfig();
        encodeRequest(requestContext, pgConfig, queries);
        requestContext.setServerAddress(config.getSearchDomain());
        QueryResult queryResult = null;
        while (requestContext.getHasRetryTimes() < pgConfig.getRetryTimes()) {
            requestContext.setHasRetryTimes(requestContext.getHasRetryTimes() + 1);
            try {
                byte[] bytes = requester.sendRequest(requestContext, pgConfig.getTimeoutInMs(), config.getUserAuth(), true);
                if (bytes != null) {
                    queryResult = IGraphResultParser.parse(requestContext, bytes, pgConfig.getOutfmt());
                }
                break;
            } catch (Throwable e) {
                log.warn("search failed, hasRetryTimes[{}], config.retryTimes[{}], Exception:[{}]",
                    requestContext.getHasRetryTimes(), pgConfig.getRetryTimes(), e.getMessage());
                if (requestContext.getHasRetryTimes() == pgConfig.getRetryTimes()) {
                    throw e;
                }
            }
        }
        if (!requestContext.isValidResult()) {
            throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext, buildTotalErrorMsg(queryResult)));
        }
        return queryResult;
    }

    private CompletableFuture<QueryResult> doSearchAsync(@NonNull PGSession pgSession,
        @NonNull PGQuery... queries) throws IGraphQueryException {
        RequestContext requestContext = pgSession.getRequestContext();
        PGConfig pgConfig = pgSession.getPgConfig();
        encodeRequest(requestContext, pgConfig, queries);
        requestContext.setServerAddress(config.getSearchDomain());
        CompletableFuture<Response> responseFuture = requester.sendRequestAsync(requestContext, pgConfig.getTimeoutInMs(), config.getUserAuth(), true);

        return responseFuture.handleAsync((response, exception) -> {
            if (null != exception) {
                throw new IGraphServerException("Failed to sendRequest with Exception:[" + exception.getMessage()
                    + "] and requestContext:[" + requestContext + "]", exception);
            }
            byte[] bytes = requester.handleResponse(requestContext, response);
            QueryResult queryResult = IGraphResultParser.parse(requestContext, bytes, pgConfig.getOutfmt());
            if (!requestContext.isValidResult()) {
                throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext, buildTotalErrorMsg(queryResult)));
            }
            return queryResult;
        }, getExecutor());
    }

    private String buildTotalErrorMsg(QueryResult queryResult) {
        if (null == queryResult) {
            return "empty result";
        }
        StringBuilder totalErrorMsg = new StringBuilder();
        List<SingleQueryResult> singleQueryResults = queryResult.getAllQueryResult();
        if (null == singleQueryResults) {
            return "empty result";
        }
        for (int i = 0; i < singleQueryResults.size(); ++i) {
            totalErrorMsg.append("query ").append(i).append(":{").append(singleQueryResults.get(i).getErrorMsg()).append("}; ");
        }
        return totalErrorMsg.toString();
    }

}
