package com.aliyun.igraph.client.utils;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class NetUtilsTest {

    @Test
    public void testGetIntranetIp() {
        String localAddress = NetUtils.getIntranetIp();
        assertNotNull(localAddress);
    }
}