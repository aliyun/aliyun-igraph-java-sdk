package com.aliyun.igraph.client.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class URLCodecUtilTest {
    @Test
    public void testEncode() throws Exception {
        {
            String encoded = URLCodecUtil.encode("");
            Assert.assertEquals("", encoded);
        }
        {
            String encoded = URLCodecUtil.encode(" ");
            Assert.assertEquals("+", encoded);
        }
        {
            try {
                URLCodecUtil.encode(null);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
    }

    @Test
    public void testDecode() throws Exception {
        {
            String encoded = URLCodecUtil.decode("");
            Assert.assertEquals("", encoded);
        }
        {
            String encoded = URLCodecUtil.decode("+");
            Assert.assertEquals(" ", encoded);
        }
        {
            try {
                URLCodecUtil.decode(null);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
    }
}
