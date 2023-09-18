package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.core.RequestContext;
import com.aliyun.igraph.client.core.UpdateSession;
import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.exception.IGraphUpdateException;
import com.aliyun.igraph.client.gremlin.driver.Client;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.driver.Host;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.driver.message.RequestMessage;
import org.asynchttpclient.Response;

import javax.ws.rs.NotSupportedException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author alibaba
 */
public class IGraphUpdateResultSet implements ResultSet, Serializable {
    private Executor executor;
    private List<Result> resultObjects;
    private CompletableFuture<Response> responseFuture;
    private UpdateSession updateSession;
    private Client iGraphClient;
    private CompletableFuture<List<Result>> resultFuture;



    public IGraphUpdateResultSet(@NonNull Client iGraphClient, @NonNull UpdateSession updateSessio, @NonNull CompletableFuture<Response> responseFuture) {
        this.iGraphClient = iGraphClient;
        executor = iGraphClient.getExecutor();
        this.updateSession = updateSessio;
        this.responseFuture = responseFuture;
    }

    private void decodeUpdateResult() {
        if (resultFuture == null) {
            resultFuture = responseFuture.handleAsync((response, exception) -> {
                RequestContext requestContext = updateSession.getRequestContext();
                if (null != exception) {
                    throw new IGraphServerException("Failed to sendRequest with Exception:[" + exception.getMessage()
                        + "] and requestContext:[" + requestContext + "]", exception);
                }
                byte[] bytes = iGraphClient.getRequester().handleResponse(requestContext, response);
                String responseBody = new String(bytes);
                if (!responseBody.contains("<errno>0</errno>") && !responseBody.contains("\"errno\":0")) {
                    throw new IGraphUpdateException("update failed, with response:[" + responseBody + "]");
                }
                List<Result> results = new ArrayList<>();
                results.add(new IGraphUpdateResult(responseBody));
                return results;
            }, executor);
            if (resultFuture == null) {
                throw new IGraphServerException("get update result error");
            }
        }
    }

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
        decodeUpdateResult();
        List<Result> results = (List)this.some(1).join();
        assert results.size() <= 1;
        return results.size() == 1 ? results.get(0) : null;
    }

    @Override
    public CompletableFuture<List<Result>> some(final int items) {
        decodeUpdateResult();
        return resultFuture.thenApplyAsync(results -> {
            if (resultObjects != null) {
                results = resultObjects;
            }
            if (items > results.size()) {
                throw new IGraphQueryException("get the number of items is greater than the size of the result");
            }
            resultObjects = results.subList(items,results.size());
            return results.subList(0, items);
        });
    }

    @Override
    public CompletableFuture<List<Result>> all() {
        decodeUpdateResult();
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
        return null;
    }
}
