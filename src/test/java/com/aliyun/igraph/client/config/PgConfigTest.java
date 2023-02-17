package com.aliyun.igraph.client.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class PgConfigTest {
    @Test
    public void testDefaultConstructor() throws Exception {
        PGConfig pgConfig = new PGConfig();
        Assert.assertEquals("fb2", pgConfig.getOutfmt());
        Assert.assertNull(pgConfig.getTrace());
        Assert.assertNull(pgConfig.getNoCache());
        Assert.assertNull(pgConfig.getCacheOnly());
        Assert.assertEquals(0, pgConfig.getSearcherMaxSeekCount());
    }

    @Test
    public void testCopyConstructor() throws Exception {
        PGConfig origin = new PGConfig();
        origin.setOutfmt("json");
        origin.setTrace("debug");
        origin.setNoCache("false");
        origin.setCacheOnly("true");
        origin.setSearcherMaxSeekCount(9527);
    }

    @Test
    public void testGetConfigString() throws Exception {
        PGConfig pgConfig = new PGConfig();
        String configString0 = pgConfig.getConfigString();
        Assert.assertEquals("config{outfmt=fb2&cache_hot_key=true}", configString0);
        Assert.assertEquals(configString0, pgConfig.getConfigString());
        pgConfig.setOutfmt("json");
        Assert.assertEquals("config{outfmt=json&cache_hot_key=true}", pgConfig.getConfigString());
        pgConfig.setTrace("debug");
        Assert.assertEquals("config{outfmt=json&trace=debug&cache_hot_key=true}", pgConfig.getConfigString());
        pgConfig.setNoCache("false");
        Assert.assertEquals("config{outfmt=json&trace=debug&no_cache=false&cache_hot_key=true}", pgConfig.getConfigString());
        pgConfig.setCacheOnly("true");
        Assert.assertEquals("config{outfmt=json&trace=debug&no_cache=false&cache_only=true&cache_hot_key=true}",
                pgConfig.getConfigString());
        pgConfig.setSearcherMaxSeekCount(9527);
        Assert.assertEquals(
                "config{outfmt=json&trace=debug&no_cache=false&cache_only=true&searcher_max_seek_count=9527&cache_hot_key=true}",
                pgConfig.getConfigString());
    }

    @Test
    public void testSetOutfmt() throws Exception {
        PGConfig pgConfig = new PGConfig();
        try {
            pgConfig.setOutfmt(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }
}
