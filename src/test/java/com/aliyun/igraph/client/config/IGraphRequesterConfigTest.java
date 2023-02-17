package com.aliyun.igraph.client.config;

import com.aliyun.igraph.client.net.RequesterConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class IGraphRequesterConfigTest {
    @Test
    public void testDefaultConstructor() throws Exception {
        RequesterConfig iGraphRequesterConfig = new RequesterConfig();
        Assert.assertEquals(1024, iGraphRequesterConfig.getMaxConnTotal());
        Assert.assertEquals(200, iGraphRequesterConfig.getMaxConnPerRoute());
        Assert.assertNotNull(iGraphRequesterConfig.getHttpConnectionConfig());
    }

    @Test
    public void testSetter() throws Exception {
        RequesterConfig iGraphRequesterConfig = new RequesterConfig();
        iGraphRequesterConfig.setMaxConnTotal(9527);
        Assert.assertEquals(9527, iGraphRequesterConfig.getMaxConnTotal());
        iGraphRequesterConfig.setMaxConnPerRoute(10086);
        Assert.assertEquals(10086, iGraphRequesterConfig.getMaxConnPerRoute());
    }
}
