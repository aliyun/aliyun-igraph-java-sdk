package com.aliyun.igraph.client.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class ClientConfigTest {
    @Test
    public void testDefaultConstructor() throws Exception {
        ClientConfig clientConfig = new ClientConfig();
        Assert.assertNull(clientConfig.getSearchDomain());
        Assert.assertNull(clientConfig.getLocalAddress());
        Assert.assertNull(clientConfig.getSrc());
    }

    @Test
    public void testCopyConstructor() throws Exception {
        ClientConfig origin = new ClientConfig();
        origin.setSearchDomain("sample domain");
        origin.setLocalAddress("sample address");
        origin.setSrc("sample src");
        ClientConfig copied = new ClientConfig(origin);
        Assert.assertEquals("sample domain", copied.getSearchDomain());
        Assert.assertEquals("sample address", copied.getLocalAddress());
        Assert.assertEquals("sample src", copied.getSrc());
    }

    @Test
    public void testSetter() throws Exception {
        ClientConfig origin = new ClientConfig();
        try {
            origin.setSearchDomain(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
        try {
            origin.setLocalAddress(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }
}
