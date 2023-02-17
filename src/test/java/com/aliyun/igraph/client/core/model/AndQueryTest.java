package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.pg.AndQuery;
import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.KeyList;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class AndQueryTest {
    @Test
    public void testConstructor() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            AndQuery andQuery = AndQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .andField("f01")
                    .build();
            String expected = "(" + leftQuery.toString() + "and{andfield=f01}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, andQuery.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            try {
                AndQuery andQuery = AndQuery.builder()
                        .leftQuery(leftQuery)
                        .rightQuery(rightQuery)
                        .build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            try {
                AndQuery andQuery = AndQuery.builder()
                        .leftQuery(leftQuery)
                        .andField("f01")
                        .build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
        {
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            try {
                AndQuery andQuery = AndQuery.builder()
                        .rightQuery(rightQuery)
                        .andField("f01")
                        .build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
    }

    @Test
    public void testLeftMostQuery() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            AndQuery andQuery = AndQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .andField("f01")
                    .build();
            String expected = "(" + leftQuery.toString() + "and{andfield=f01}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, andQuery.toString());
            Assert.assertEquals(leftQuery, andQuery.getLeftmostAtomicQuery());
            Assert.assertEquals("pkey_left", andQuery.getLeftmostAtomicQuery().getKeys());
        }
    }
}
