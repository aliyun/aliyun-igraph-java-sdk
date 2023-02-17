package com.aliyun.igraph.client.config;

import com.aliyun.igraph.client.net.HttpConnectionConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class HttpConnectionConfigTest {
    @Test
    public void testDefaultConstructor() throws Exception {
        HttpConnectionConfig httpConnectionConfig = new HttpConnectionConfig();
        Assert.assertEquals(3000, httpConnectionConfig.getSocketTimeout());
        Assert.assertEquals(3000, httpConnectionConfig.getConnectTimeout());
        Assert.assertEquals(3000, httpConnectionConfig.getConnectionRequestTimeout());
    }

    @Test
    public void testCopyConstructor() throws Exception {
        int expectedTimeOut = 128;
        HttpConnectionConfig origin = new HttpConnectionConfig();
        origin.setSocketTimeout(expectedTimeOut);
        origin.setConnectTimeout(expectedTimeOut);
        origin.setConnectionRequestTimeout(expectedTimeOut);

        HttpConnectionConfig copied = new HttpConnectionConfig(origin);
        Assert.assertEquals(expectedTimeOut, copied.getSocketTimeout());
        Assert.assertEquals(expectedTimeOut, copied.getConnectTimeout());
        Assert.assertEquals(expectedTimeOut, copied.getConnectionRequestTimeout());
    }

    @Test
    public void testSocketTimeout() throws Exception {
        int expectedTimeOut = 128;
        HttpConnectionConfig httpConnectionConfig = new HttpConnectionConfig();
        Assert.assertEquals(3000, httpConnectionConfig.getSocketTimeout());
        httpConnectionConfig.setSocketTimeout(expectedTimeOut);
        Assert.assertEquals(expectedTimeOut, httpConnectionConfig.getSocketTimeout());
    }

    @Test
    public void testConnectTimeout() throws Exception {
        int expectedTimeOut = 128;
        HttpConnectionConfig httpConnectionConfig = new HttpConnectionConfig();
        Assert.assertEquals(3000, httpConnectionConfig.getConnectTimeout());
        httpConnectionConfig.setConnectTimeout(expectedTimeOut);
        Assert.assertEquals(expectedTimeOut, httpConnectionConfig.getConnectTimeout());
    }

    @Test
    public void testConnectionRequestTimeout() throws Exception {
        int expectedTimeOut = 128;
        HttpConnectionConfig httpConnectionConfig = new HttpConnectionConfig();
        Assert.assertEquals(3000, httpConnectionConfig.getConnectionRequestTimeout());
        httpConnectionConfig.setConnectionRequestTimeout(expectedTimeOut);
        Assert.assertEquals(expectedTimeOut, httpConnectionConfig.getConnectionRequestTimeout());
    }
}
