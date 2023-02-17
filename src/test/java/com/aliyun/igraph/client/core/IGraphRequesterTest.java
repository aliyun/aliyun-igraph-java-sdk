package com.aliyun.igraph.client.core;

import com.aliyun.igraph.client.net.RequesterConfig;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.exception.IGraphTimeoutException;

import com.aliyun.igraph.client.net.Requester;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

import org.asynchttpclient.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by chekong.ygm on 15/10/8.
 */
public class IGraphRequesterTest {
    @Test
    public void testClose() throws Exception {
        Requester iGraphRequester = new Requester(new RequesterConfig());
        iGraphRequester.close();
    }

    @Test
    public void testCloseWithException(@Mocked final AsyncHttpClient httpClient) throws Exception {
        new Expectations() {
            {
                httpClient.close();
                result = new IOException();
            }
        };

        Requester iGraphRequester = new Requester(new RequesterConfig());
        Deencapsulation.setField(iGraphRequester, httpClient);
        iGraphRequester.close();
    }


    @Test
    public void testSearchRequestFailedWithTimeoutException(@Mocked final ListenableFuture<Response> futureResponse,
                                                            @Mocked final BoundRequestBuilder requestBuilder,
                                                            @Mocked final DefaultAsyncHttpClient httpClient)
        throws Exception {
        new Expectations() {
            {
                futureResponse.get(anyLong, (TimeUnit)any);
                result = new ExecutionException("blabla", new TimeoutException("timeout !!"));
                requestBuilder.execute();
                result = futureResponse;
                httpClient.preparePost(anyString);
                result = requestBuilder;
            }
        };

        RequesterConfig iGraphRequesterConfig = new RequesterConfig();
        Requester iGraphRequester = new Requester(iGraphRequesterConfig);
        RequestContext requestContext = new RequestContext();
        requestContext.setServerAddress("http://127.0.0.1");
        requestContext.setRequestContent("/app?app=pg");
        try {
            iGraphRequester.sendRequest(requestContext, 100, null, true);
            Assert.fail();
        } catch (IGraphTimeoutException expected) {
            Assert.assertTrue(expected.getMessage().contains("TimeoutException"));
        }

        new Verifications() {
            {
                httpClient.preparePost(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testSearchRequestFailedWithExecutionException(@Mocked final ListenableFuture<Response> futureResponse,
                                                              @Mocked final BoundRequestBuilder requestBuilder,
                                                              @Mocked final DefaultAsyncHttpClient httpClient)
        throws Exception {
        new Expectations() {
            {
                futureResponse.get(anyLong, (TimeUnit)any);
                result = new ExecutionException("q2q", null);
                requestBuilder.execute();
                result = futureResponse;
                httpClient.preparePost(anyString);
                result = requestBuilder;
            }
        };

        RequesterConfig iGraphRequesterConfig = new RequesterConfig();
        Requester iGraphRequester = new Requester(iGraphRequesterConfig);
        RequestContext requestContext = new RequestContext();
        requestContext.setServerAddress("http://127.0.0.1");
        requestContext.setRequestContent("/app?app=pg");
        try {
            iGraphRequester.sendRequest(requestContext, 100, null, true);
            Assert.fail();
        } catch (IGraphServerException expected) {
            Assert.assertTrue(expected.getMessage().contains("ExecutionException"));
        }

        new Verifications() {
            {
                httpClient.preparePost(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testSearchRequestFailedWithInterruptedException(@Mocked final ListenableFuture<Response> futureResponse,
                                                                @Mocked final BoundRequestBuilder requestBuilder,
                                                                @Mocked final DefaultAsyncHttpClient httpClient)
        throws Exception {
        new Expectations() {
            {
                futureResponse.get(anyLong, (TimeUnit)any);
                result = new InterruptedException("blabla");
                requestBuilder.execute();
                result = futureResponse;
                httpClient.preparePost(anyString);
                result = requestBuilder;
            }
        };

        RequesterConfig iGraphRequesterConfig = new RequesterConfig();
        Requester iGraphRequester = new Requester(iGraphRequesterConfig);
        RequestContext requestContext = new RequestContext();
        requestContext.setServerAddress("http://127.0.0.1");
        requestContext.setRequestContent("/app?app=pg");
        try {
            iGraphRequester.sendRequest(requestContext, 100, null, true);
            Assert.fail();
        } catch (IGraphServerException expected) {
            Assert.assertTrue(expected.getMessage().contains("InterruptedException"));
        }

        new Verifications() {
            {
                httpClient.preparePost(anyString);
                times = 1;
            }
        };
    }

    @Test
    public void testSearchRequestFailedWithNullResponse(@Mocked final Response response,
                                                        @Mocked final ListenableFuture<Response> futureResponse,
                                                        @Mocked final BoundRequestBuilder requestBuilder,
                                                        @Mocked final DefaultAsyncHttpClient httpClient)
        throws Exception {
        new Expectations() {
            {
                response.getStatusCode();
                result = 200;
                response.getResponseBodyAsBytes();
                result = null;
                futureResponse.get(anyLong, (TimeUnit)any);
                result = response;
                requestBuilder.execute();
                result = futureResponse;
                httpClient.preparePost(anyString);
                result = requestBuilder;
            }
        };

        RequesterConfig iGraphRequesterConfig = new RequesterConfig();
        Requester iGraphRequester = new Requester(iGraphRequesterConfig);
        RequestContext requestContext = new RequestContext();
        requestContext.setServerAddress("http://127.0.0.1");
        requestContext.setRequestContent("/app?app=pg");
        try {
            iGraphRequester.sendRequest(requestContext, 100, null, true);
            Assert.fail();
        } catch (IGraphServerException expected) {
            Assert.assertTrue(expected.getMessage().contains("null response"));
        }

        new Verifications() {
            {
                httpClient.preparePost(anyString);
                times = 1;
            }
        };
    }
}
