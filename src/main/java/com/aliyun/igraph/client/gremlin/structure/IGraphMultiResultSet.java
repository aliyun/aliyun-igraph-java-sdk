package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.core.model.BaseResult;
import com.aliyun.igraph.client.exception.IGraphServerException;
import org.apache.tinkerpop.gremlin.driver.ResultSet;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author alibaba
 */
public class IGraphMultiResultSet extends BaseResult {
    private Executor executor;
    private List<ResultSet> results;
    private CompletableFuture<Boolean> completeFuture;

    public IGraphMultiResultSet(Executor executor) {
        this.executor = executor;
        completeFuture = new CompletableFuture<>();
    }
    public IGraphMultiResultSet() {
        completeFuture = new CompletableFuture<>();
    }

    public void setMultiResultSetFuture(CompletableFuture<IGraphMultiResultSet> multiResultSetFuture) {
        multiResultSetFuture.whenCompleteAsync((multiResultSet, exception) -> {
            if (exception != null) {
                completeFuture.completeExceptionally(exception);
            }
            if (multiResultSet != null) {
                this.results = multiResultSet.results;
                completeFuture.complete(true);
            }
        }, executor);
    }

    private void getGremlinResult() {
        if (results == null && executor != null) {
            try {
                results = completeFuture.handleAsync((complete, exception)-> {
                    if (exception instanceof RuntimeException) {
                        throw (RuntimeException)exception;
                    }
                    return results;
                }, executor).get();
            } catch (InterruptedException | ExecutionException e) {
               Throwable cause = e.getCause();
               if (cause instanceof RuntimeException) {
                   throw (RuntimeException) cause;
               }
                throw new IGraphServerException("get gremlin multi result failed", cause);
            }
        }
    }

    @Override
    public int size() { return results.size(); }

    /**
     * 查询时有多个query时，调用此方法。
     *
     * @return 多个query，要么所有的query结果按query顺序都返回，要么不返回。
     */
    public List<ResultSet> getAllQueryResult() {
        getGremlinResult();
        return results;
    }

    /**
     * 查询只有一个query时，调用此方法
     *
     * @return 返回单个ResultSet
     */
    public ResultSet getSingleQueryResult() {
        getGremlinResult();
        if (results == null || results.size() != 1) {
            return null;
        }
        return results.get(0);
    }

    /**
     * 查询特定query对应请求结果
     *
     * @param index
     *            query的index
     * @return query对应的结果
     */
    public ResultSet getQueryResult(int index) {
        getGremlinResult();
        if (results == null || index < 0 || index >= results.size()) {
            return null;
        }
        return results.get(index);
    }

    @Override
    public String toString() {
        StringBuilder ss = new StringBuilder(128);
        StringBuilder resultArray = new StringBuilder(128);;
        if (results == null) {
            return null;
        }
        for (ResultSet resultSet : results) {
            IGraphResultSet result = (IGraphResultSet) resultSet;
            StringBuilder s0 = new StringBuilder(128);
            StringBuilder resultObject = new StringBuilder(128);
            resultObject.append("result size:").append(result.size()).append(";");
            resultObject.append("result:").append(result).append(";");
            resultObject.append("error_info:").append(result.getErrorMsg()).append(";");
            resultObject.append("trace:").append(result.getTraceInfo()).append(";");
            resultArray.append(resultObject);
        }
        ss.append("results:").append(resultArray).append(";");
        ss.append("trace:").append(traceInfo).append(";");
        return ss.toString();
    }

    public void setResults(List<ResultSet> results) { this.results = results; }

}
