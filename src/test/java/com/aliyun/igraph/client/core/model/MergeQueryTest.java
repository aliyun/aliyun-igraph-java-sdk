package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.KeyList;
import com.aliyun.igraph.client.pg.MergeQuery;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import com.aliyun.igraph.client.exception.IGraphClientException;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class MergeQueryTest {
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
            MergeQuery mergeQuery = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .build();
            String expected = "(" + leftQuery.toString() + "merge{}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, mergeQuery.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            try {
                MergeQuery mergeQuery = MergeQuery.builder()
                        .leftQuery(leftQuery)
                        .rightQuery(null)
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
                MergeQuery mergeQuery = MergeQuery.builder()
                        .leftQuery(null)
                        .rightQuery(rightQuery)
                        .build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
    }

    @Test
    public void testSetSorter() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            MergeQuery mergeQuery1 = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .sorter("s00")
                    .build();
            String expected1 = "(" + leftQuery.toString() + "merge{sorter=s00}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, mergeQuery1.toString());
            MergeQuery mergeQuery2 = mergeQuery1.toBuilder().sorter("s01").build();
            String expected2 = "(" + leftQuery.toString() + "merge{sorter=s01}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, mergeQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            MergeQuery mergeQuery = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .sorter(null)
                    .build();
            String expected = "(" + leftQuery.toString() + "merge{}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, mergeQuery.toString());
        }
    }

    @Test
    public void testSetDistinct() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            MergeQuery mergeQuery1 = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .distinct("s00")
                    .build();
            String expected1 = "(" + leftQuery.toString() + "merge{distinct=s00}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, mergeQuery1.toString());
            MergeQuery mergeQuery2 = mergeQuery1.toBuilder().distinct("s01").build();
            String expected2 = "(" + leftQuery.toString() + "merge{distinct=s01}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, mergeQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            MergeQuery mergeQuery = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .build();
            String expected = "(" + leftQuery.toString() + "merge{}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, mergeQuery.toString());
        }
    }

    @Test
    public void testSetOrderby() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            MergeQuery mergeQuery1 = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .orderby("s00")
                    .build();
            String expected1 = "(" + leftQuery.toString() + "merge{orderby=s00}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, mergeQuery1.toString());
            MergeQuery mergeQuery2 = mergeQuery1.toBuilder().orderby("s01").build();
            String expected2 = "(" + leftQuery.toString() + "merge{orderby=s01}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, mergeQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            MergeQuery mergeQuery = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .build();
            String expected = "(" + leftQuery.toString() + "merge{}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, mergeQuery.toString());
        }
    }
    @Test
    public void testSetTag() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            MergeQuery mergeQuery1 = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .tag(2,3)
                    .build();
            String expected1 = "(" + leftQuery.toString() + "merge{tag=2:3}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, mergeQuery1.toString());
            MergeQuery mergeQuery2 = mergeQuery1.toBuilder().tag(1,2).build();
            String expected2 = "(" + leftQuery.toString() + "merge{tag=1:2}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, mergeQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyLists(Lists.newArrayList(new KeyList("pkey_left")))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyLists(Lists.newArrayList(new KeyList("pkey_right")))
                    .build();
            MergeQuery mergeQuery = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .build();
            try {
                MergeQuery mergeQuery1 = mergeQuery.toBuilder().tag(0,2).build();
                Assert.fail();
            } catch (IGraphClientException expected) {
            }
            try {
                MergeQuery mergeQuery2 = mergeQuery.toBuilder().tag(20,2).build();
                Assert.fail();
            } catch (IGraphClientException expected) {
            }
            try {
                MergeQuery mergeQuery3 = mergeQuery.toBuilder().tag(1,0).build();
                Assert.fail();
            } catch (IGraphClientException expected) {
            }
            try {
                MergeQuery mergeQuery4 = mergeQuery.toBuilder().tag(1,20).build();
                Assert.fail();
            } catch (IGraphClientException expected) {
            }
        }
    }

    @Test
    public void testSetRange() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            MergeQuery mergeQuery1 = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .range(0,1)
                    .build();
            String expected1 = "(" + leftQuery.toString() + "merge{range=0:1}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, mergeQuery1.toString());
            MergeQuery mergeQuery2 = mergeQuery1.toBuilder().range(1,2).build();
            String expected2 = "(" + leftQuery.toString() + "merge{range=1:2}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, mergeQuery2.toString());
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
            MergeQuery mergeQuery = MergeQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .build();
            String expected = "(" + leftQuery.toString() + "merge{}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, mergeQuery.toString());
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testToString() throws Exception {
        AtomicQuery leftQuery = AtomicQuery.builder()
                .table("tbl_left")
                .keyList(new KeyList("pkey_left"))
                .build();
        AtomicQuery rightQuery = AtomicQuery.builder()
                .table("tbl_right")
                .keyList(new KeyList("pkey_right"))
                .build();
        MergeQuery mergeQuery = MergeQuery.builder()
                .leftQuery(leftQuery)
                .rightQuery(rightQuery)
                .build();

        String expected = "(" + leftQuery.toString() + "merge{}" + rightQuery.toString() + ")";
        Assert.assertEquals(expected, mergeQuery.toString());

        MergeQuery mergeQuery2 = mergeQuery.toBuilder().sorter("s01").build();
        expected = "(" + leftQuery.toString() + "merge{sorter=s01}" + rightQuery.toString() + ")";
        Assert.assertEquals(expected, mergeQuery2.toString());

        MergeQuery mergeQuery3 = mergeQuery2.toBuilder().tag(1,2).build();
        expected = "(" + leftQuery.toString() + "merge{sorter=s01&tag=1:2}" + rightQuery.toString() + ")";
        Assert.assertEquals(expected, mergeQuery3.toString());

        MergeQuery mergeQuery4 = mergeQuery3.toBuilder().range(0,20).build();
        expected = "(" + leftQuery.toString() + "merge{sorter=s01&tag=1:2&range=0:20}" + rightQuery.toString() + ")";
        Assert.assertEquals(expected, mergeQuery4.toString());
    }

    @Test
    public void testLeftMostQuery() throws Exception {
        AtomicQuery leftQuery = AtomicQuery.builder()
                .table("tbl_left")
                .keyList(new KeyList("pkey_left"))
                .build();
        AtomicQuery rightQuery = AtomicQuery.builder()
                .table("tbl_right")
                .keyList(new KeyList("pkey_right"))
                .build();
        MergeQuery mergeQuery = MergeQuery.builder()
                .leftQuery(leftQuery)
                .rightQuery(rightQuery)
                .build();
        String expected = "(" + leftQuery.toString() + "merge{}" + rightQuery.toString() + ")";
        Assert.assertEquals(expected, mergeQuery.toString());
        Assert.assertEquals(leftQuery, mergeQuery.getLeftmostAtomicQuery());
        Assert.assertEquals("pkey_left", mergeQuery.getLeftmostAtomicQuery().getKeys());
    }

    @Test
    public  void testToBuilder() throws Exception {
        try {
            AtomicQuery atomicQuery = AtomicQuery.builder().table("a").build();
            AtomicQuery atomicQuery1 = AtomicQuery.builder().table("b").build();
            MergeQuery mergeQuery = MergeQuery.builder().leftQuery(atomicQuery).rightQuery(atomicQuery1).build();
            MergeQuery mergeQuery1 = mergeQuery.toBuilder().build();
        } catch (Exception unexpected) {
            Assert.fail();
        }
    }
}
