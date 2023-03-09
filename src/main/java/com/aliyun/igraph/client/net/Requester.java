package com.aliyun.igraph.client.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.aliyun.igraph.client.exception.IGraphRetryableException;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.exception.IGraphTimeoutException;
import com.aliyun.igraph.client.core.CompletableAsyncHandler;
import com.aliyun.igraph.client.core.RequestContext;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.asynchttpclient.*;

/**
 * @author alibaba
 */
@Slf4j
public class Requester {
    private AsyncHttpClient httpClient;

    public Requester(RequesterConfig requesterConfig) {
        HttpConnectionConfig httpConnectionConfig = requesterConfig.getHttpConnectionConfig();
        DefaultAsyncHttpClientConfig.Builder builder = requesterConfig.getAsyncHttpClientConfigBuilder();

        builder.setMaxConnections(requesterConfig.getMaxConnTotal());
        builder.setMaxConnectionsPerHost(requesterConfig.getMaxConnPerRoute());
        builder.setRequestTimeout(httpConnectionConfig.getConnectionRequestTimeout());
        builder.setConnectTimeout(httpConnectionConfig.getConnectTimeout());
        builder.setTcpNoDelay(true);
        builder.setPooledConnectionIdleTimeout(httpConnectionConfig.getConnectionIdleTimeout());

        HashedWheelTimer timer = new HashedWheelTimer(10, TimeUnit.MILLISECONDS);
        builder.setNettyTimer(timer);


        if (requesterConfig.getThreadFactory() != null) {
            builder.setThreadFactory(requesterConfig.getThreadFactory());
        }
        AsyncHttpClientConfig asyncHttpClientConfig = builder.build();
        httpClient = new DefaultAsyncHttpClient(asyncHttpClientConfig);
    }

    public int getRequestTimeout() {
        return httpClient.getConfig().getRequestTimeout();
    }

    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.error("close httpclient failed with Exception", e);
            }
        }
    }

    public byte[] handleResponse(RequestContext requestContext, Response response) {
        fillContext(requestContext, response);
        int statusCode = response.getStatusCode();
        checkStatusCode(requestContext, statusCode);
        byte[] bytes;
        try {
            bytes = response.getResponseBodyAsBytes();
            if (null == bytes || 0 == bytes.length) {
                throw new IGraphServerException("Failed to sendRequest with " + (null == bytes ? "null" : "empty")
                        + " response, statusCode:[" + statusCode + "] and requestContext:[" + requestContext + "]");
            }
            requestContext.setResponseContentLength(bytes.length);
        } finally {
            requestContext.endServerRequest();
        }
        return bytes;
    }

    public CompletableFuture<Response> sendRequestAsync(RequestContext requestContext, Integer timeout,
                                                        String auth, Boolean isSearch) throws IGraphServerException {
        if (0 == timeout) {
            timeout = httpClient.getConfig().getRequestTimeout();
        }
        BoundRequestBuilder requestBuilder = prepareBoundRequestBuilder(requestContext, timeout, auth, isSearch);

        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        try {
            requestContext.beginServerRequest();
            requestBuilder.execute(new CompletableAsyncHandler(responseFuture));
        } catch (Exception e) {
            requestContext.endServerRequest();
            throw new IGraphServerException("Failed to sendRequest with IOException:[" + e.getMessage()
                    + "] and requestContext:[" + requestContext + "]", e);
        }
        return responseFuture;
    }

    public byte[] sendRequest(RequestContext requestContext, Integer timeout,
                              String auth, Boolean isSearch) throws IGraphServerException {
        if (0 == timeout) {
            timeout = getRequestTimeout();
        }
        BoundRequestBuilder requestBuilder = prepareBoundRequestBuilder(requestContext, timeout, auth, isSearch);

        byte[] byteArray;
        try {
            requestContext.beginServerRequest();
            Response response = requestBuilder.execute().get(timeout,
                    TimeUnit.MILLISECONDS);
            requestContext.endServerRequest();
            byteArray = handleResponse(requestContext, response);
        } catch (InterruptedException e) {
            requestContext.endServerRequest();
            throw new IGraphServerException("Failed to sendRequest with InterruptedException:[" + e.getMessage()
                    + "] and requestContext:[" + requestContext + "]", e);
        } catch (TimeoutException e) {
            requestContext.endServerRequest();
            throw new IGraphTimeoutException("timeout to sendRequest, timeout[" + timeout + "], with TimeoutException:[" + e.getMessage()
                    + "] and requestContext:[" + requestContext + "]", e);
        } catch (ExecutionException e) {
            requestContext.endServerRequest();
            Throwable cause = e.getCause();
            if (cause instanceof TimeoutException) {
                throw new IGraphTimeoutException("timeout to sendRequest, timeout[" + timeout + "], with TimeoutException:[" + cause.getMessage()
                        + "] and requestContext:[" + requestContext + "]", e);
            } else {
                throw new IGraphServerException("Failed to sendRequest with ExecutionException:[" + e.getMessage()
                        + "] and requestContext:[" + requestContext + "]", e);
            }
        }
        return byteArray;
    }

    private void checkStatusCode(RequestContext requestContext, int statusCode) {
        switch (statusCode) {
            case 200:
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                throw new IGraphRetryableException("Failed to sendRequest with statusCode:[" + statusCode
                    + "] and requestContext:[" + requestContext + "], need retry.");
            default:
                throw new IGraphServerException("Failed to sendRequest with statusCode:[" + statusCode
                    + "] and requestContext:[" + requestContext + "]");
        }
    }

    private void fillContext(RequestContext requestContext, Response response) {
        requestContext.setRequestId(response.getHeaders().get("X-Request-Id"));
    }

    private BoundRequestBuilder prepareBoundRequestBuilder(RequestContext requestContext,
                                                           Integer timeout,
                                                           String auth,
                                                           Boolean isSearch) {
        BoundRequestBuilder requestBuilder;
        String serverAddr = requestContext.getServerAddress();
        if (!serverAddr.startsWith("http://")) {
            serverAddr = "http://" + serverAddr;
        }
        if (isSearch) {
            requestBuilder = httpClient.preparePost(serverAddr + "/app");
        } else {
            requestBuilder = httpClient.preparePost(serverAddr + "/update");
        }
        requestBuilder.setBody(requestContext.getRequestContent());

        requestBuilder.setRequestTimeout(timeout);
        if (null != auth) {
            String encodeAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            requestBuilder.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodeAuth);
        }
        return requestBuilder;
    }
}
