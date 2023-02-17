package com.aliyun.igraph.client.core;

import com.aliyun.igraph.client.exception.IGraphClientException;
import com.aliyun.igraph.client.net.RequesterConfig;
import com.aliyun.igraph.client.pg.PGClientBuilder;
import com.aliyun.igraph.client.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/8.
 */
@Slf4j
public class IGraphClientBuilderTest {

    @Test
    public void testSetSocketTimeout() {
        PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
        iGraphClientBuilder.setSocketTimeout(20);
        RequesterConfig iGraphRequesterConfig = Deencapsulation.getField(iGraphClientBuilder, "requesterConfig");
        Assert.assertEquals(20, iGraphRequesterConfig.getHttpConnectionConfig().getSocketTimeout());
    }

    @Test
    public void testSetConnectTimeout() {
        PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
        iGraphClientBuilder.setConnectTimeout(20);
        RequesterConfig iGraphRequesterConfig = Deencapsulation.getField(iGraphClientBuilder, "requesterConfig");
        Assert.assertEquals(20, iGraphRequesterConfig.getHttpConnectionConfig().getConnectTimeout());
    }

    @Test
    public void testSetConnectionRequestTimeout() {
        PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
        iGraphClientBuilder.setConnectionRequestTimeout(20);
        RequesterConfig iGraphRequesterConfig = Deencapsulation.getField(iGraphClientBuilder, "requesterConfig");
        Assert.assertEquals(20, iGraphRequesterConfig.getHttpConnectionConfig().getConnectionRequestTimeout());
    }

    @Test
    public void testSetMaxConnTotal() {
        PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
        iGraphClientBuilder.setMaxConnTotal(20);
        RequesterConfig iGraphRequesterConfig = Deencapsulation.getField(iGraphClientBuilder, "requesterConfig");
        Assert.assertEquals(20, iGraphRequesterConfig.getMaxConnTotal());
    }

    @Test
    public void testSetMaxConnPerRoute() {
        PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
        iGraphClientBuilder.setMaxConnPerRoute(20);
        RequesterConfig iGraphRequesterConfig = Deencapsulation.getField(iGraphClientBuilder, "requesterConfig");
        Assert.assertEquals(20, iGraphRequesterConfig.getMaxConnPerRoute());
    }


    @Test
    public void testAsyncHttpConfigBuilder() {
        PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
        RequesterConfig iGraphRequesterConfig = iGraphClientBuilder.getRequesterConfig();
        DefaultAsyncHttpClientConfig.Builder builder = iGraphRequesterConfig.getAsyncHttpClientConfigBuilder();
        builder.setPooledConnectionIdleTimeout(10 * 60 * 1000); // 10min
        AsyncHttpClientConfig asyncHttpClientConfig = builder.build();
        Assert.assertEquals(-1, asyncHttpClientConfig.getConnectionTtl());
        Assert.assertEquals(10 * 60 * 1000, asyncHttpClientConfig.getPooledConnectionIdleTimeout());
    }

    @Test
    public void testBuild(@Mocked NetUtils netUtils) {
        {
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                }
            };
            PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
            IGraphClient client = iGraphClientBuilder.build("theSrc", "dailyDomain", "userName", "userPasswd");
            Assert.assertEquals("theSrc", client.getConfig().getSrc());
            Assert.assertEquals("dailyDomain", client.getConfig().getSearchDomain());
            Assert.assertEquals("userName:userPasswd", client.getConfig().getUserAuth());
        }
        {
            PGClientBuilder iGraphClientBuilder = PGClientBuilder.create();
            try {
                iGraphClientBuilder.build(null, "123",null, null);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            try {
                iGraphClientBuilder.build("src", null,null, null);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            try {
                iGraphClientBuilder.build("src", "", null, null);
                Assert.fail();
            } catch (IGraphClientException | NullPointerException expected) {
            }
            try {
                iGraphClientBuilder.build("", "123", null, null);
                Assert.fail();
            } catch (IGraphClientException | NullPointerException expected) {
            }
            try {
                iGraphClientBuilder.build(null, "123", null, null);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            try {
                iGraphClientBuilder.build("src", null,  null, null);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            try {
                iGraphClientBuilder.build("src", "", null, null);
                Assert.fail();
            } catch (IGraphClientException | NullPointerException expected) {
            }
            try {
                iGraphClientBuilder.build("", "123",  null, null);
                Assert.fail();
            } catch (IGraphClientException | NullPointerException ignored) {
            }

        }
    }
}
