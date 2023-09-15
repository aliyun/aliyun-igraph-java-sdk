package com.aliyun.igraph.client.gremlin.structure;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.proto.gremlin_fb.MatchRecords;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.apache.tinkerpop.gremlin.driver.Host;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.message.RequestMessage;

import javax.ws.rs.NotSupportedException;

/**
 * @author alibaba
 */
public class IGraphResultSet implements ResultSet, Serializable {
    private Executor executor;
    protected boolean hasError = false;
    protected List<Long> errorCodes;
    protected String errorMsg;
    protected String traceInfo;
    protected Map<String, MatchRecords> docDataMap;
    protected Map<Long, ElementMeta> elementMetaMap;
    protected List<Result> resultObjects;
    private CompletableFuture<List<Result>> resultFuture;
    protected CompletableFuture<Boolean> completeFuture;

    public IGraphResultSet(Executor executor) {
        this.executor = executor;
        completeFuture = new CompletableFuture<>();
    }

    public IGraphResultSet() {
        completeFuture = new CompletableFuture<>();
    }

    public void setResultSetFuture(CompletableFuture<IGraphResultSet> resultSetFuture) {
        resultSetFuture.whenCompleteAsync((resultSet, exception) -> {
            if (exception != null) {
                completeFuture.completeExceptionally(exception);
            }
            if (resultSet != null) {
                clone(resultSet);
                completeFuture.complete(true);
            }
        }, executor);
    }

    public List<Result> getResultObjects() { return resultObjects; }

    public int size() {
        return resultObjects == null ? 0 : resultObjects.size();
    }

    public boolean empty() { return 0 == size(); }

    public boolean hasError() { return hasError; }

    public String getErrorMsg() { return errorMsg; }

    public List<Long> getErrorCodes() { return errorCodes; }

    protected String toJson() {
        StringBuilder result = new StringBuilder(128);
        for (int i = 0 ; i < resultObjects.size(); ++i) {
            result.append("{");
            IGraphResult IGraphResult = (IGraphResult) resultObjects.get(i);
            result.append(IGraphResult.toString());
            result.append("}");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder ss = new StringBuilder(128);
        if (resultObjects == null) {
            return null;
        }
        ss.append("result size: ").append(size()).append(";");
        StringBuilder result = new StringBuilder(128);
        for (int i = 0 ; i < resultObjects.size(); ++i) {
            result.append("{");
            IGraphResult IGraphResult = (IGraphResult) resultObjects.get(i);
            result.append(IGraphResult.toString());
            result.append("}");
        }
        ss.append("result: ").append(result).append(";");
        ss.append("error_info: ").append(errorMsg).append(":");
        ss.append("trace: ").append(traceInfo).append(";");
        return ss.toString();
    }

    public void clone(IGraphResultSet iGraphResultSet) {
        this.hasError = iGraphResultSet.hasError;
        this.errorMsg = iGraphResultSet.errorMsg;
        this.traceInfo = iGraphResultSet.traceInfo;
        this.docDataMap = iGraphResultSet.docDataMap;
        this.elementMetaMap = iGraphResultSet.elementMetaMap;
        this.resultObjects = iGraphResultSet.resultObjects;
    }

    private void getGremlinResult() {
        if (resultFuture == null) {
            if (executor != null) {
                resultFuture = completeFuture.handleAsync((complete, exception) -> {
                    if (exception instanceof RuntimeException) {
                        throw (RuntimeException) exception;
                    }
                    return resultObjects;
                }, executor);
            } else {
                resultFuture = CompletableFuture.completedFuture(resultObjects);
            }
        }
    }

    public String getTraceInfo() { return traceInfo; }

    protected Map<Long, ElementMeta> getElementMetaMap() { return elementMetaMap; }

    @Override
    public RequestMessage getOriginalRequestMessage() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Host getHost() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public CompletableFuture<Map<String, Object>> statusAttributes() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public boolean allItemsAvailable() {
        return resultObjects != null;
    }

    @Override
    public CompletableFuture<Void> allItemsAvailableAsync() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public int getAvailableItemCount() {
        return resultObjects.size();
    }

    @Override
    public Result one() {
        getGremlinResult();
        List<Result> results = (List)this.some(1).join();
        assert results.size() <= 1;
        return results.size() == 1 ? results.get(0) : null;
    }

    @Override
    public CompletableFuture<List<Result>> some(final int items) {
        getGremlinResult();
        return resultFuture.thenApplyAsync(results -> {
            if (resultObjects != null) {
                results = resultObjects;
            }
            if (items > results.size()) {
                throw new IGraphQueryException("get the number of items is greater than the size of the result");
            }
            resultObjects = results.subList(items,resultObjects.size());
            return results.subList(0, items);
        });
    }

    @Override
    public CompletableFuture<List<Result>> all() {
        getGremlinResult();
        return resultFuture;
    }

    @Override
    public Stream<Result> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(),
            Spliterator.IMMUTABLE | Spliterator.SIZED), false);
    }

    @Override
    public Iterator<Result> iterator() {
        return new Iterator<Result>() {
            private Result nextOne = null;

            @Override
            public boolean hasNext() {
                if (null == nextOne) {
                    nextOne = one();
                }
                return nextOne != null;
            }

            @Override
            public Result next() {
                if (null != nextOne || hasNext()) {
                    final Result r = nextOne;
                    nextOne = null;
                    return r;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new NotSupportedException();
            }
        };
    }

    @Override
    public Object getJson() {
        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();
        if (resultObjects == null){
            return jsonArray;
        }
        for (Result resultObject : resultObjects) {
            jsonArray.add(gson.toJsonTree(resultObject.getJson()));
        }
        return jsonArray;
    }
}
