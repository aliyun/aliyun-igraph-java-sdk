package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.pg.KeyList;
import org.junit.Assert;
import org.junit.Test;

import com.aliyun.igraph.client.exception.IGraphClientException;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class KeyListTest {
    @Test
    public void testConstructor() throws Exception {
        {
            KeyList keyList = new KeyList("pkey ");
            Assert.assertEquals("pkey+", keyList.toString());
            Assert.assertEquals("pkey ", keyList.getPkey());
            Assert.assertNull(keyList.getSkeys());
        }
        {
            KeyList keyList = new KeyList("pkey ", "skey 0");
            Assert.assertEquals("pkey+:skey+0", keyList.toString());
            Assert.assertEquals("pkey ", keyList.getPkey());
            Assert.assertEquals("skey 0", keyList.getSkeys()[0]);
        }
        {
            KeyList keyList = new KeyList("pkey ", "skey 0", "skey 1");
            Assert.assertEquals("pkey+:skey+0|skey+1", keyList.toString());
            Assert.assertEquals("pkey ", keyList.getPkey());
            Assert.assertEquals("skey 0", keyList.getSkeys()[0]);
            Assert.assertEquals("skey 1", keyList.getSkeys()[1]);
        }
        {
            try {
                new KeyList(null, "skey");
                Assert.fail();
            } catch (NullPointerException ignored) {
            }
        }
        {
            try {
                new KeyList("pkey ", (String[]) null);
                Assert.fail();
            } catch (NullPointerException ignored) {
            }
        }
        {
            try {
                new KeyList(null, (String[]) null);
                Assert.fail();
            } catch (NullPointerException ignored) {
            }
        }
        {
            try {
                String[] skeys = new String[0];
                new KeyList("pkey ", skeys);
                Assert.fail();
            } catch (IGraphClientException ignored) {
            }
        }
    }
}
