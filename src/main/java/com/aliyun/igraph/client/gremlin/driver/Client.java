package com.aliyun.igraph.client.gremlin.driver;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import com.aliyun.igraph.client.config.ClientConfig;
import com.aliyun.igraph.client.config.UpdateConfig;
import com.aliyun.igraph.client.exception.*;
import com.aliyun.igraph.client.gremlin.gremlin_api.GremlinQueryType;
import com.aliyun.igraph.client.gremlin.structure.IGraphUpdateResultSet;
import com.aliyun.igraph.client.core.model.UpdateQuery;
import com.aliyun.igraph.client.config.GremlinConfig;
import com.aliyun.igraph.client.core.IGraphClient;
import com.aliyun.igraph.client.core.IGraphResultParser;
import com.aliyun.igraph.client.core.RequestContext;
import com.aliyun.igraph.client.core.UpdateSession;
import com.aliyun.igraph.client.gremlin.structure.GremlinQuery;
import com.aliyun.igraph.client.gremlin.structure.IGraphMultiResultSet;
import com.aliyun.igraph.client.gremlin.structure.IGraphResultSet;
import com.aliyun.igraph.client.gremlin.gremlin_api.GraphTraversal;
import com.aliyun.igraph.client.net.Requester;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Connection;
import org.apache.tinkerpop.gremlin.driver.RequestOptions;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.message.RequestMessage;
import org.apache.tinkerpop.gremlin.process.traversal.Bytecode;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.asynchttpclient.Response;

import javax.ws.rs.NotSupportedException;

/**
 * @author alibaba
 */
@Slf4j
public class Client extends IGraphClient implements org.apache.tinkerpop.gremlin.driver.Client {
    private final Cluster cluster;
    private final Settings settings;

    public Client(@NonNull ClientConfig config,
                  @NonNull Requester requester, final Cluster cluster, final Settings settings) {
        super(config, requester);
        this.cluster = cluster;
        this.settings = settings;
    }

    @Override
    public RequestMessage.Builder buildMessage(final RequestMessage.Builder builder) {
        return builder;
    }

    @Override
    public void initializeImplementation() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Connection chooseConnection(final RequestMessage msg) throws TimeoutException {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public org.apache.tinkerpop.gremlin.driver.Client alias(final String graphOrTraversalSource) {
        return alias(makeDefaultAliasMap(graphOrTraversalSource));
    }

    @Override
    public org.apache.tinkerpop.gremlin.driver.Client alias(final Map<String, String> aliases) {
        throw new NotSupportedException("method is not supported");
    }

    public IGraphMultiResultSet submit(Traversal... traversals) {
        try {
            return submitAsync(traversals).get();
        } catch (Exception e) {
            throw new IGraphClientException("submit failed with Exception [ " + e.getMessage() + " ]", e);
        }
    }

    public CompletableFuture<IGraphMultiResultSet> submitAsync(Traversal... traversals) {
        List<GremlinQuery> gremlinQueries = new ArrayList<>();
        for (Traversal traversal : traversals) {
            gremlinQueries.add(new GremlinQuery(traversal));
        }
        return searchAsync(new GremlinSession(this.config), gremlinQueries.toArray(new GremlinQuery[0]));
    }

    @Override
    public ResultSet submit(Traversal traversal) {
        try {
            return submitAsync(traversal).get();
        } catch (Exception e) {
            throw new IGraphClientException("submit failed with Exception [ " + e.getMessage() + " ]", e);
        }
    }

    @Override
    public ResultSet submit(Traversal traversal, String submitId) {
        try {

            return submitAsync(traversal, submitId).get();
        } catch (Exception e) {
            throw new IGraphClientException("submit failed with Exception [ " + e.getMessage() + " ]", e);
        }
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final Traversal traversal) {
        GraphTraversal graphTraversal = (GraphTraversal) traversal;
        GremlinQueryType gremlinQueryType = graphTraversal.getGremlinQueryType();
        CompletableFuture<ResultSet> future;
        try {
            switch (gremlinQueryType) {
                case SEARCH:
                    future = searchAsync(new GremlinSession(this.config), new GremlinQuery(graphTraversal));
                    break;
                case UPDATE:
                    future = gremlinUpdateAsync(new UpdateSession(), graphTraversal.getUpdateQuery());
                    break;
                case DELETE:
                    future = gremlinDeleteAsync(new UpdateSession(), graphTraversal.getUpdateQuery());
                    break;
                default:
                    throw new IGraphQueryException("invalid query type: " + gremlinQueryType.name());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final Traversal traversal, String submitId) {
        GraphTraversal graphTraversal = (GraphTraversal) traversal;
        GremlinQueryType gremlinQueryType = graphTraversal.getGremlinQueryType();
        CompletableFuture<ResultSet> future;
        try {
            switch (gremlinQueryType) {
                case SEARCH:
                    future = searchAsync(new GremlinSession(this.config), submitId, new GremlinQuery(graphTraversal));
                    break;
                case UPDATE:
                    future = gremlinUpdateAsync(new UpdateSession(), graphTraversal.getUpdateQuery());
                    break;
                case DELETE:
                    future = gremlinDeleteAsync(new UpdateSession(), graphTraversal.getUpdateQuery());
                    break;
                default:
                    throw new IGraphQueryException("invalid query type: " + gremlinQueryType.name());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return future;
    }

    @Override
    public ResultSet submit(final Bytecode bytecode) {
        try {
            return submitAsync(bytecode).get();
        } catch (NotSupportedException uoe) {
            throw uoe;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ResultSet submit(final Bytecode bytecode, final RequestOptions options) {
        try {
            return submitAsync(bytecode, options).get();
        } catch (NotSupportedException uoe) {
            throw uoe;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final Bytecode bytecode) {
        throw new NotSupportedException("This implementation does not support Traversal submission - use a sessionless Client created with from the alias() method");
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final Bytecode bytecode, final RequestOptions options) {
        throw new NotSupportedException("This implementation does not support Traversal submission - use a sessionless Client created with from the alias() method");
    }

    @Override
    public Client init() {
        return this;
    }

    @Override
    public ResultSet submit(final String gremlin) {
        return submit(gremlin, RequestOptions.EMPTY);
    }

    @Override
    public ResultSet submit(final String gremlin, final Map<String, Object> parameters) {
        try {
            return submitAsync(gremlin, parameters).get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ResultSet submit(final String gremlin, final RequestOptions options) {
        try {
            return submitAsync(gremlin, options).get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final String gremlin) {
        return submitAsync(gremlin, RequestOptions.EMPTY);
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final String gremlin, final Map<String, Object> parameters) {
        final RequestOptions.Builder options = RequestOptions.build();
        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach(options::addParameter);
        }
        return submitAsync(gremlin, options.create());
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final String gremlin, final RequestOptions options) {
        CompletableFuture<ResultSet> future;
        try {
            GremlinQueryType gremlinQueryType = GremlinQueryType.SEARCH;
            switch (gremlinQueryType) {
                case SEARCH:
                    GremlinQuery gremlinQuery = new GremlinQuery(gremlin);
                    GremlinSession gremlinSession = new GremlinSession(this.config);
                    options.getTimeout().ifPresent(timeout -> gremlinSession.getGremlinConfig().setTimeoutInMs(timeout));
                    options.getRetryTimes().ifPresent(retryTimes -> gremlinSession.getGremlinConfig().setRetryTimes(retryTimes));
                    options.getParameters().ifPresent(params -> gremlinSession.getGremlinConfig().setBinding(params));
                    options.getSrc().ifPresent(src -> gremlinSession.getGremlinConfig().setSrc(src));
                    future = searchAsync(gremlinSession, gremlinQuery);
                    break;
                case UPDATE:
                    UpdateSession updateSession = new UpdateSession();
                    options.getTimeout().ifPresent(timeout -> updateSession.getUpdateConfig().setTimeoutInMs(timeout));
                    options.getSrc().ifPresent(src -> updateSession.getUpdateConfig().setSrc(src));
                    future = gremlinUpdateAsync(updateSession, UpdateQuery.builder().table("not_exist_table").build());
                    break;
                case DELETE:
                    updateSession = new UpdateSession();
                    options.getTimeout().ifPresent(timeout -> updateSession.getUpdateConfig().setTimeoutInMs(timeout));
                    options.getSrc().ifPresent(src -> updateSession.getUpdateConfig().setSrc(src));
                    future = gremlinDeleteAsync(updateSession, UpdateQuery.builder().table("not_exist_table").build());
                    break;
                default:
                    throw new IGraphQueryException("invalid query type: " + gremlinQueryType.name());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<ResultSet> submitAsync(final RequestMessage msg) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public boolean isClosing() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public void close() {
        super.close();
    }

    /**
     * Gets the {@link Settings}.
     * @return Settings
     */
    public Settings getSettings() {
        return settings;
    }

    @Override
    public Cluster getCluster() {
        return cluster;
    }

    @Override
    public Map<String, String> makeDefaultAliasMap(final String graphOrTraversalSource) {
        final Map<String, String> aliases = new HashMap<>();
        aliases.put("g", graphOrTraversalSource);
        return aliases;
    }

    /**
     * Settings given to Cluster#connect(Settings) that configures how a {@link org.apache.tinkerpop.gremlin.driver.Client} will behave.
     */
    public static class Settings {
    }

    /**
     * Settings for a {@link org.apache.tinkerpop.gremlin.driver.Client} that involve a session.
     */
    public static class SessionSettings {

    }

    /**
     * @param gremlinSession 与这次请求相关的Context
     * @param queries        一个或多个query
     * @return 请求结果
     * @throws Exception 所有的错误将会以异常的形式给出
     */
    public IGraphMultiResultSet searchSync(@NonNull GremlinSession gremlinSession,
                                           @NonNull GremlinQuery... queries) throws Exception {
        return doSearchSync(gremlinSession, queries);
    }

    private CompletableFuture<IGraphMultiResultSet> searchAsync(@NonNull GremlinSession gremlinSession,
                                                                @NonNull GremlinQuery... gremlinQuery) {
        return CompletableFuture.supplyAsync(() -> {
            IGraphMultiResultSet multiResult = new IGraphMultiResultSet(getExecutor());
            RequestContext requestContext = gremlinSession.getRequestContext();
            GremlinConfig gremlinConfig = gremlinSession.getGremlinConfig();
            doRetryableSearch(multiResult, requestContext, gremlinConfig, gremlinQuery);
            return multiResult;
        }, getExecutor());
    }

    private CompletableFuture<ResultSet> searchAsync(@NonNull GremlinSession gremlinSession,
                                                     @NonNull GremlinQuery gremlinQuery) {
        return CompletableFuture.supplyAsync(() -> {
            IGraphResultSet result = new IGraphResultSet(getExecutor());
            RequestContext requestContext = gremlinSession.getRequestContext();
            GremlinConfig gremlinConfig = gremlinSession.getGremlinConfig();
            doRetryableSearch(result, requestContext, gremlinConfig, gremlinQuery);
            return result;
        }, getExecutor());
    }

    private CompletableFuture<ResultSet> searchAsync(@NonNull GremlinSession gremlinSession,
                                                     @NonNull String submitId,
                                                     @NonNull GremlinQuery gremlinQuery) {
        return CompletableFuture.supplyAsync(() -> {
            IGraphResultSet result = new IGraphResultSet(getExecutor());
            RequestContext requestContext = gremlinSession.getRequestContext();
            GremlinConfig gremlinConfig = gremlinSession.getGremlinConfig();
            doRetryableSearch(result, requestContext, gremlinConfig, submitId, gremlinQuery);
            return result;
        }, getExecutor());
    }

    private void doRetryableSearch(IGraphMultiResultSet multiResult,
                                   RequestContext requestContext,
                                   GremlinConfig gremlinConfig,
                                   GremlinQuery... gremlinQuery) {
        requestContext.setHasRetryTimes(requestContext.getHasRetryTimes() + 1);
        CompletableFuture<IGraphMultiResultSet> multiResultSetFuture =
            doSearchAsync(requestContext, gremlinConfig, gremlinQuery).handleAsync((response, exception) -> {
                IGraphMultiResultSet iGraphMultiResultSet = null;
                try {
                    iGraphMultiResultSet = handleResult(requestContext, gremlinConfig, response, exception);
                } catch (IGraphRetryableException | IGraphTimeoutException e) {
                    log.warn("search failed, hasRetryTimes[{}], config.retryTimes[{}], Exception:[{}]",
                        requestContext.getHasRetryTimes(), gremlinConfig.getRetryTimes(), e.getMessage());
                    requestContext.endServerRequest();
                    if (requestContext.getHasRetryTimes() < gremlinConfig.getRetryTimes()) {
                        doRetryableSearch(multiResult, requestContext, gremlinConfig, gremlinQuery);
                    } else {
                        throw e;
                    }
                }
                return iGraphMultiResultSet;
            }, getExecutor());
        multiResult.setMultiResultSetFuture(multiResultSetFuture);
    }

    private void doRetryableSearch(IGraphResultSet result,
                                   RequestContext requestContext,
                                   GremlinConfig gremlinConfig,
                                   GremlinQuery gremlinQuery) {
        requestContext.setHasRetryTimes(requestContext.getHasRetryTimes() + 1);
        CompletableFuture<IGraphResultSet> resultSetFuture =
            doSearchAsync(requestContext, gremlinConfig, gremlinQuery).handleAsync((response, exception) -> {
                IGraphResultSet iGraphResultSet = null;
                try {
                    IGraphMultiResultSet iGraphMultiResultSet =
                        handleResult(requestContext, gremlinConfig, response, exception);
                    iGraphResultSet = (IGraphResultSet) iGraphMultiResultSet.getSingleQueryResult();
                    if (iGraphResultSet == null) {
                        throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext,
                            Client.buildTotalErrorMsg(iGraphMultiResultSet)));
                    }
                } catch (IGraphRetryableException | IGraphTimeoutException e) {
                    log.warn("search failed, hasRetryTimes[{}], config.retryTimes[{}], Exception:[{}]",
                        requestContext.getHasRetryTimes(), gremlinConfig.getRetryTimes(), e.getMessage());
                    requestContext.endServerRequest();
                    if (requestContext.getHasRetryTimes() < gremlinConfig.getRetryTimes()) {
                        doRetryableSearch(result, requestContext, gremlinConfig, gremlinQuery);
                    } else {
                        throw e;
                    }
                }
                return iGraphResultSet;
            }, getExecutor());
       result.setResultSetFuture(resultSetFuture);
    }

    private void doRetryableSearch(IGraphResultSet result,
                                   RequestContext requestContext,
                                   GremlinConfig gremlinConfig,
                                   String submitId,
                                   GremlinQuery gremlinQuery) {
        requestContext.setHasRetryTimes(requestContext.getHasRetryTimes() + 1);
        CompletableFuture<IGraphResultSet> resultSetFuture =
            doSearchAsync(requestContext, gremlinConfig, submitId, gremlinQuery).handleAsync((response, exception) -> {
                IGraphResultSet iGraphResultSet = null;
                try {
                    IGraphMultiResultSet iGraphMultiResultSet =
                        handleResult(requestContext, gremlinConfig, response, exception);
                    iGraphResultSet = (IGraphResultSet) iGraphMultiResultSet.getSingleQueryResult();
                    if (iGraphResultSet == null) {
                        throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext,
                            Client.buildTotalErrorMsg(iGraphMultiResultSet)));
                    }
                } catch (IGraphRetryableException | IGraphTimeoutException e) {
                    log.warn("search failed, hasRetryTimes[{}], config.retryTimes[{}], Exception:[{}]",
                        requestContext.getHasRetryTimes(), gremlinConfig.getRetryTimes(), e.getMessage());
                    requestContext.endServerRequest();
                    if (requestContext.getHasRetryTimes() < gremlinConfig.getRetryTimes()) {
                        doRetryableSearch(result, requestContext, gremlinConfig, gremlinQuery);
                    } else {
                        throw e;
                    }
                }
                return iGraphResultSet;
            }, getExecutor());
        result.setResultSetFuture(resultSetFuture);
    }

    private CompletableFuture<ResultSet> gremlinUpdateAsync(@NonNull UpdateSession updateSession,
                                                            @NonNull UpdateQuery updateQuery) {
        Client client = this;
        return CompletableFuture.supplyAsync(() -> {
            CompletableFuture<Response> response;
            try {
                response = doUpdateAsync(updateSession, TYPE_UPDATE, updateQuery);
                return new IGraphUpdateResultSet(client, updateSession, response);
            } catch (Exception e) {
                log.error("update failed", e);
                throw new RuntimeException("update failed," + e);
            }
        }, getExecutor());
    }

    private CompletableFuture<ResultSet> gremlinDeleteAsync(@NonNull UpdateSession updateSession,
                                                            @NonNull UpdateQuery updateQuery) {
        Client client = this;
        return CompletableFuture.supplyAsync(() -> {
            CompletableFuture<Response> response;
            try {
                response = doUpdateAsync(updateSession, TYPE_DELETE, updateQuery);
                return new IGraphUpdateResultSet(client, updateSession, response);
            } catch (Exception e) {
                log.error("delete failed", e);
                throw new RuntimeException("delete failed," + e);
            }
        }, getExecutor());
    }

    private IGraphMultiResultSet doSearchSync(@NonNull GremlinSession gremlinSession,
                                              @NonNull GremlinQuery... queries) {
        RequestContext requestContext = gremlinSession.getRequestContext();
        GremlinConfig gremlinConfig = gremlinSession.getGremlinConfig();
        encodeRequest(requestContext, gremlinConfig, queries);
        requestContext.setServerAddress(config.getSearchDomain());
        IGraphMultiResultSet gremlinMultiResult = null;
        while (requestContext.getHasRetryTimes() < gremlinConfig.getRetryTimes()) {
            requestContext.setHasRetryTimes(requestContext.getHasRetryTimes() + 1);
            try {
                byte[] bytes = requester.sendRequest(requestContext, gremlinConfig.getTimeoutInMs(), config.getUserAuth(), true);
                if (bytes != null) {
                    gremlinMultiResult = IGraphResultParser.parseGremlin(requestContext, bytes, gremlinConfig.getOutfmt());
                }
                break;
            } catch (IGraphRetryableException | IGraphTimeoutException e) {
                log.warn("search failed, hasRetryTimes[{}], config.retryTimes[{}], Exception:[{}]",
                    requestContext.getHasRetryTimes(), gremlinConfig.getRetryTimes(), e.getMessage());
                if (requestContext.getHasRetryTimes() == gremlinConfig.getRetryTimes()) {
                    throw e;
                }
            }
        }
        if (!requestContext.isValidResult()) {
            throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext, buildTotalErrorMsg(gremlinMultiResult)));
        }
        return gremlinMultiResult;
    }

    private CompletableFuture<Response> doSearchAsync(@NonNull RequestContext requestContext,
                                                      @NonNull GremlinConfig gremlinConfig,
                                                      @NonNull GremlinQuery... queries) throws IGraphServerException {
        encodeRequest(requestContext, gremlinConfig, queries);
        requestContext.setServerAddress(config.getSearchDomain());
        return requester.sendRequestAsync(requestContext, gremlinConfig.getTimeoutInMs(), config.getUserAuth(), true);
    }

    private CompletableFuture<Response> doSearchAsync(@NonNull RequestContext requestContext,
                                                      @NonNull GremlinConfig gremlinConfig,
                                                      @NonNull String submitId,
                                                      @NonNull GremlinQuery... queries) throws IGraphServerException {
        encodeRequest(requestContext, gremlinConfig, submitId, queries);
        requestContext.setServerAddress(config.getSearchDomain());
        return requester.sendRequestAsync(requestContext, gremlinConfig.getTimeoutInMs(), config.getUserAuth(), true);
    }

    @Override
    protected CompletableFuture<Response> doUpdateAsync(UpdateSession updateSession,
                                                        Integer updateType,
                                                        UpdateQuery updateQuery) throws IGraphUpdateException {
        RequestContext requestContext = updateSession.getRequestContext();
        UpdateConfig updateConfig = updateSession.getUpdateConfig();
        requestContext.setServerAddress(config.getUpdateDomain());
        buildUpdateRequest(requestContext, updateConfig, updateType, updateQuery);
        return requester.sendRequestAsync(requestContext, updateConfig.getTimeoutInMs(), config.getUserAuth(), false);
    }

    private IGraphMultiResultSet handleResult(RequestContext requestContext,
                                              GremlinConfig gremlinConfig,
                                              Response response,
                                              Throwable exception) {
        if (exception != null) {
            handleException(requestContext, gremlinConfig, exception);
        }
        byte[] bytes = this.getRequester().handleResponse(requestContext, response);
        if (bytes == null) {
            throw new IGraphRetryableException("request timeout, need retry.");
        }
        IGraphMultiResultSet iGraphMultiResultSet =
            IGraphResultParser.parseGremlin(requestContext, bytes, gremlinConfig.getOutfmt());
        log.debug("search with requestContext {}", requestContext);
        if (!requestContext.isValidResult()) {
            throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext,
                Client.buildTotalErrorMsg(iGraphMultiResultSet)));
        }
        if (iGraphMultiResultSet == null) {
            throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext,
                Client.buildTotalErrorMsg(null)));
        }
        return iGraphMultiResultSet;
    }

    private void handleException(RequestContext requestContext, GremlinConfig gremlinConfig, Throwable exception) {
        if (exception instanceof InterruptedException) {
            throw new IGraphServerException("Failed to sendRequest with InterruptedException:[" + exception.getMessage()
                + "] and requestContext:[" + requestContext + "]", exception);
        } else if (exception instanceof TimeoutException) {
            throw new IGraphTimeoutException("timeout to sendRequest, timeout[" +
                (gremlinConfig.getTimeoutInMs() != 0 ? gremlinConfig.getTimeoutInMs() : requester.getRequestTimeout())
                + "], with TimeoutException:[" + exception.getMessage()
                + "] and requestContext:[" + requestContext + "]", exception);
        } else {
            throw new IGraphServerException("Failed to sendRequest with Exception:[" + exception.getMessage()
                + "] and requestContext:[" + requestContext + "]", exception);
        }
    }

    public static String buildTotalErrorMsg(IGraphMultiResultSet gremlinMultiResult) {
        if (null == gremlinMultiResult) {
            return "empty result";
        }
        StringBuilder totalErrorMsg = new StringBuilder();
        List<ResultSet> ResultSets = gremlinMultiResult.getAllQueryResult();
        if (null == ResultSets) {
            return "empty result";
        }
        List<IGraphResultSet> iGraphResultSets = new ArrayList<>();
        for (ResultSet resultSet : ResultSets) {
            iGraphResultSets.add((IGraphResultSet) resultSet);
        }
        for (int i = 0; i < iGraphResultSets.size(); ++i) {
            totalErrorMsg.append("query ").append(i).append(":{").append(iGraphResultSets.get(i).getErrorMsg()).append("}; ");
        }
        return totalErrorMsg.toString();

    }

}
