package com.aliyun.igraph.client.core;

import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.AsyncCompletionHandlerBase;
import org.asynchttpclient.Response;

/**
 * @author alibaba
 */
public class CompletableAsyncHandler extends AsyncCompletionHandlerBase {
    private CompletableFuture<Response> future;

    public CompletableAsyncHandler(CompletableFuture<Response> future) {
        this.future = future;
    }

    @Override
    public Response onCompleted(Response response) throws Exception {
        future.complete(response);
        return super.onCompleted(response);
    }

    @Override
    public void onThrowable(Throwable t) {
        future.completeExceptionally(t);
        super.onThrowable(t);
    }
}
