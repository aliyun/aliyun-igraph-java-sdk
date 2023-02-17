package com.aliyun.igraph.client.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fancheng.wl on 2021/1/18.
 */
public class GremlinConfigTest {
    @Test
    public void testDefaultConstructor() throws Exception {
        GremlinConfig gremlinConfig = new GremlinConfig();
        Assert.assertEquals("fb2", gremlinConfig.getOutfmt());
        Assert.assertNull(gremlinConfig.getTrace());
        Assert.assertEquals("false", gremlinConfig.getNoCache());
        Assert.assertEquals("false", gremlinConfig.getCacheOnly());
        Assert.assertEquals(0, gremlinConfig.getSearcherMaxSeekCount());
        Assert.assertEquals(1, gremlinConfig.getRetryTimes());
    }

    @Test
    public void testGetConfigString() throws Exception {
        GremlinConfig gremlinConfig = new GremlinConfig();
        String configString0 = gremlinConfig.getConfigString();
        Assert.assertEquals("config{outfmt=fb2&no_cache=false&cache_only=false}", configString0);
        Assert.assertEquals(configString0, gremlinConfig.getConfigString());
        gremlinConfig.setOutfmt("json");
        Assert.assertEquals("config{outfmt=json&no_cache=false&cache_only=false}", gremlinConfig.getConfigString());
        gremlinConfig.setTrace("debug");
        Assert.assertEquals("config{outfmt=json&no_cache=false&cache_only=false}", gremlinConfig.getConfigString());
        gremlinConfig.setNoCache("true");
        Assert.assertEquals("config{outfmt=json&no_cache=true&cache_only=false}", gremlinConfig.getConfigString());
        gremlinConfig.setCacheOnly("true");
        Assert.assertEquals("config{outfmt=json&no_cache=true&cache_only=true}",
                gremlinConfig.getConfigString());
        gremlinConfig.setSearcherMaxSeekCount(9527);
        Assert.assertEquals(
                "config{outfmt=json&no_cache=true&cache_only=true&searcher_max_seek_count=9527}",
                gremlinConfig.getConfigString());
        gremlinConfig.setTimeoutInMs(100);
        Assert.assertEquals(
                "config{outfmt=json&no_cache=true&cache_only=true&searcher_max_seek_count=9527&request_timeout=100}",
                gremlinConfig.getConfigString());
    }
}
