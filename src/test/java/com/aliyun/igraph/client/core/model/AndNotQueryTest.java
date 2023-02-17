package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.pg.AndNotQuery;
import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.KeyList;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class AndNotQueryTest {
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
            AndNotQuery andNotQuery1 = AndNotQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .andNotField("f01")
                    .build();
            AndNotQuery andNotQuery2 = AndNotQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .andNotField("f01")
                    .build();
            Assert.assertTrue(andNotQuery1.equals(andNotQuery2));
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
            Assert.assertFalse(leftQuery.equals(rightQuery));
            AndNotQuery andNotQuery = AndNotQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .andNotField("f01")
                    .build();
            String expected = "(" + leftQuery.toString() + "andnot{andnotfield=f01}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, andNotQuery.toString());
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
                AndNotQuery andNotQuery = AndNotQuery.builder()
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
                AndNotQuery andNotQuery = AndNotQuery.builder()
                        .leftQuery(leftQuery)
                        .andNotField("f01")
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
                AndNotQuery andNotQuery = AndNotQuery.builder()
                        .rightQuery(rightQuery)
                        .andNotField("f01")
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
                    .alias("1","1")
                    .field("2")
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            Assert.assertFalse(leftQuery.equals(rightQuery));
            AndNotQuery andNotQuery = AndNotQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .andNotField("f01")
                    .build();
            String expected = "(" + leftQuery.toString() + "andnot{andnotfield=f01}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, andNotQuery.toString());
            Assert.assertEquals(leftQuery, andNotQuery.getLeftmostAtomicQuery());
            Assert.assertEquals("pkey_left", andNotQuery.getLeftmostAtomicQuery().getKeys());
        }
    }
}
