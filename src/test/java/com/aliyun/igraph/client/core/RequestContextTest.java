package com.aliyun.igraph.client.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class RequestContextTest {
    @Test
    public void testConstructor() throws Exception {
        RequestContext requestContext = new RequestContext();
        Assert.assertNull(requestContext.getRequestContent());
        Assert.assertEquals(0, requestContext.getResponseContentLength());
        Assert.assertNull(requestContext.getServerAddress());
        Assert.assertEquals(0, requestContext.getQueryEncodeLatency());
        Assert.assertEquals(0, requestContext.getServerRequestLatency());
        Assert.assertEquals(0, requestContext.getResponseDecodeLatency());
    }

    @Test
    public void testToString() throws Exception {
        RequestContext requestContext = new RequestContext();
        String expected = "serverAddress=[" + requestContext.getServerAddress() + "], requestContent=["
                + requestContext.getRequestContent() + "], responseContentLength=["
                + requestContext.getResponseContentLength() + "], queryEncodeLatency=["
                + requestContext.getQueryEncodeLatency() + "], serverRequestLatency=["
                + requestContext.getServerRequestLatency() + "], responseDecodeLatency=["
                + requestContext.getResponseDecodeLatency() + "], hasRetryTime=["
                + requestContext.getHasRetryTimes() + "], requestId=["
                + requestContext.getRequestId() + "]";
        Assert.assertEquals(expected, requestContext.toString());
    }

    @Test
    public void testQueryEncodeLatency() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.beginQueryEncode();
        Thread.sleep(10);
        requestContext.endQueryEncode();
        Assert.assertTrue(10 <= requestContext.getQueryEncodeLatency());
        requestContext.beginQueryEncode();
        Thread.sleep(10);
        requestContext.endQueryEncode();
        Assert.assertTrue(20 <= requestContext.getQueryEncodeLatency());
    }

    @Test
    public void testServerRequestLatency() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.beginServerRequest();
        Thread.sleep(10);
        requestContext.endServerRequest();
        Assert.assertTrue(10 <= requestContext.getServerRequestLatency());
        requestContext.beginServerRequest();
        Thread.sleep(10);
        requestContext.endServerRequest();
        Assert.assertTrue(20 <= requestContext.getServerRequestLatency());
    }

    @Test
    public void testResponseDecodeLatency() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.beginResponseDecode();
        Thread.sleep(10);
        requestContext.endResponseDecode();
        Assert.assertTrue(10 <= requestContext.getResponseDecodeLatency());
        requestContext.beginResponseDecode();
        Thread.sleep(10);
        requestContext.endResponseDecode();
        Assert.assertTrue(20 <= requestContext.getResponseDecodeLatency());
    }

    @Test
    public void testServerAddress() throws Exception {
        RequestContext requestContext = new RequestContext();
        String expected = "123";
        requestContext.setServerAddress(expected);
        Assert.assertEquals(expected, requestContext.getServerAddress());
    }

    @Test
    public void testRequestContent() throws Exception {
        RequestContext requestContext = new RequestContext();
        String expected = "456";
        requestContext.setRequestContent(expected);
        Assert.assertEquals(expected, requestContext.getRequestContent());
    }

    @Test
    public void testResponseContentLength() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.setResponseContentLength(888);
        Assert.assertEquals(888, requestContext.getResponseContentLength());
    }
}
