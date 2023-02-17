package com.aliyun.igraph.client.core.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Lists;
import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.pg.AndQuery;
import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.JoinQuery;
import com.aliyun.igraph.client.pg.KeyList;
import com.aliyun.igraph.client.pg.MergeQuery;
import org.junit.Assert;
import org.junit.Test;

import com.aliyun.igraph.client.exception.IGraphClientException;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class JoinQueryTest {
    @Test
    public void testConstructor() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}" + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            try {
                JoinQuery joinQuery = JoinQuery.builder()
                        .leftQuery(leftQuery)
                        .rightQuery(rightQuery)
                        .leftJoinField(null)
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
                JoinQuery joinQuery = JoinQuery.builder()
                        .leftQuery(leftQuery)
                        .rightQuery(null)
                        .leftJoinField("join_field_1")
                        .build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
        {
            try {
                JoinQuery joinQuery = JoinQuery.builder()
                        .leftQuery(null)
                        .rightQuery(AtomicQuery.builder().table("tbl_right").build())
                        .leftJoinField("join_field_1")
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
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .keyList(new KeyList("pkey_right"))
                    .build();
            try {
                JoinQuery joinQuery = JoinQuery.builder()
                        .leftQuery(leftQuery)
                        .rightQuery(rightQuery)
                        .leftJoinField("join_field_1")
                        .build();
                joinQuery.toString();
                Assert.fail();
            } catch (IGraphQueryException expected) {
            }
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("left_field")
                    .rightJoinField("right_field")
                    .build();
            String expected = "(" + leftQuery.toString()
                    + "join{joinfield=left_field;right_field}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());
        }
    }

    @Test
    public void testSetFilter() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .filter("f01")
                    .build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1&filter=f01}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .filter(null)
                    .build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());
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
                    .build();
            JoinQuery joinQuery1 = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .sorter("s00")
                    .build();
            String expected1 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&sorter=s00}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, joinQuery1.toString());
            JoinQuery joinQuery2 = joinQuery1.toBuilder().sorter("s01").build();
            String expected2 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&sorter=s01}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, joinQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .sorter(null)
                    .build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());
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
                    .build();
            JoinQuery joinQuery1 = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .distinct("s00")
                    .build();
            String expected1 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&distinct=s00}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, joinQuery1.toString());
            JoinQuery joinQuery2 = joinQuery1.toBuilder().distinct("s01").build();
            String expected2 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&distinct=s01}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, joinQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .distinct(null)
                    .build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());
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
                    .build();
            JoinQuery joinQuery1 = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .orderby("desc")
                    .build();
            String expected1 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&orderby=desc}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, joinQuery1.toString());
            JoinQuery joinQuery2 = joinQuery1.toBuilder().orderby("sec").build();
            String expected2 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&orderby=sec}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, joinQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .distinct(null)
                    .build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());
        }
    }

    @Test
    public void testSetReturnFields() throws Exception {
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery1 = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .field("f1")
                    .build();
            String expected1 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&fields=f1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, joinQuery1.toString());

            JoinQuery joinQuery2 = joinQuery1.toBuilder().clearFields().fields(Lists.newArrayList("f1", "f2")).build();
            String expected2 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&fields=f1;f2}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, joinQuery2.toString());
        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyLists(Lists.newArrayList(new KeyList("pkey_left")))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .field(null)
                    .build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery.toString());

            JoinQuery joinQuery1 = joinQuery.toBuilder().clearFields().fields(null).build();
            Assert.assertEquals(expected, joinQuery1.toString());
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
                    .build();
            JoinQuery joinQuery1 = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .range(0,1)
                    .build();
            String expected1 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&range=0:1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, joinQuery1.toString());
            JoinQuery joinQuery2 = joinQuery1.toBuilder().range(1,2).build();
            String expected2 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&range=1:2}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, joinQuery2.toString());

        }
        {
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            try {
                JoinQuery joinQuery = JoinQuery.builder()
                        .leftQuery(leftQuery)
                        .rightQuery(rightQuery)
                        .leftJoinField("join_field_1")
                        .range(-1,-2)
                        .build();
                Assert.fail();
            } catch (IGraphClientException expected) {
            }
        }
    }

    @Test
    public void testAddAlias() throws Exception {
        {
            Map<String, String> alias1 = new LinkedHashMap<String, String>() {
                {
                    put("k1", "v1");
                    put("k2", "v2");
                }
            };
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyList(new KeyList("pkey_left"))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery1 = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .aliases(alias1)
                    .build();
            String expected1 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&alias=k1:v1;k2:v2}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected1, joinQuery1.toString());

            JoinQuery joinQuery2 = joinQuery1.toBuilder().clearAliases().alias("k3", "v3").build();
            String expected2 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&alias=k3:v3}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected2, joinQuery2.toString());

            JoinQuery joinQuery3 = joinQuery2.toBuilder().alias("k4", "v4").build();
            String expected3 = "(" + leftQuery.toString() + "join{joinfield=join_field_1&alias=k3:v3;k4:v4}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected3, joinQuery3.toString());
        }
        {
            Map<String, String> alias1 = new LinkedHashMap<String, String>() {
                {
                    put("k1", null);
                }
            };
            Map<String, String> alias2 = new LinkedHashMap<String, String>() {
                {
                    put(null, "v1");
                }
            };
            AtomicQuery leftQuery = AtomicQuery.builder()
                    .table("tbl_left")
                    .keyLists(Lists.newArrayList(new KeyList("pkey_left")))
                    .build();
            AtomicQuery rightQuery = AtomicQuery.builder()
                    .table("tbl_right")
                    .build();
            JoinQuery joinQuery = JoinQuery.builder()
                    .leftQuery(leftQuery)
                    .rightQuery(rightQuery)
                    .leftJoinField("join_field_1")
                    .build();
            try {
                JoinQuery joinQuery1 = joinQuery.toBuilder().clearAliases().aliases(alias1).build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            try {
                JoinQuery joinQuery2 = joinQuery.toBuilder().clearAliases().aliases(alias2).build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            try {
                JoinQuery joinQuery3 = joinQuery.toBuilder().clearAliases().alias("k1", null).build();
                Assert.fail();
            } catch (NullPointerException expected) {
            }
            JoinQuery joinQuery3 = joinQuery.toBuilder().clearAliases().build();
            String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}"
                    + rightQuery.toString() + ")";
            Assert.assertEquals(expected, joinQuery3.toString());
        }
    }

    @Test
    public void testLeftJoinTag() throws Exception {
        AtomicQuery leftQuery = AtomicQuery.builder()
                .table("tbl_left")
                .keyList(new KeyList("pkey_left"))
                .build();
        AtomicQuery rightQuery = AtomicQuery.builder()
                .table("tbl_right")
                .build();
        JoinQuery joinQuery = JoinQuery.builder()
                .leftQuery(leftQuery)
                .rightQuery(rightQuery)
                .leftJoinField("join_field_1")
                .leftJoinTag("tag_name:1|2")
                .build();
        String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1&jointype=left_join&jointag=tag_name:1|2}"
                + rightQuery.toString() + ")";
        Assert.assertEquals(expected, joinQuery.toString());
    }

    @Test
    public void testLeftMostQuery() throws Exception {
        AtomicQuery leftQuery = AtomicQuery.builder()
                .table("tbl_left")
                .keyList(new KeyList("pkey_left"))
                .build();
        AtomicQuery rightQuery = AtomicQuery.builder()
                .table("tbl_right")
                .build();
        JoinQuery joinQuery = JoinQuery.builder()
                .leftQuery(leftQuery)
                .rightQuery(rightQuery)
                .leftJoinField("join_field_1")
                .build();
        String expected = "(" + leftQuery.toString() + "join{joinfield=join_field_1}"
                + rightQuery.toString() + ")";
        Assert.assertEquals(expected, joinQuery.toString());
        Assert.assertEquals(leftQuery, joinQuery.getLeftmostAtomicQuery());
        Assert.assertEquals("pkey_left", joinQuery.getLeftmostAtomicQuery().getKeys());
    }

    @Test
    public void testLeftMostQuery2() throws Exception {
        AtomicQuery leftQuery = AtomicQuery.builder()
                .table("tbl_left")
                .keyList(new KeyList("pkey_left"))
                .build();

        AtomicQuery leftQuery2 = AtomicQuery.builder()
                .table("tbl_left")
                .keyList(new KeyList("pkey_left2"))
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

        MergeQuery mergeQuery = MergeQuery.builder()
                .leftQuery(leftQuery2)
                .rightQuery(andQuery)
                .build();
        AtomicQuery rightQuery2 = AtomicQuery.builder()
                .table("tbl_right")
                .build();
        JoinQuery joinQuery = JoinQuery.builder()
                .leftQuery(mergeQuery)
                .rightQuery(rightQuery2)
                .leftJoinField("join_field_1")
                .build();
        joinQuery.toString();
        Assert.assertEquals(leftQuery2, joinQuery.getLeftmostAtomicQuery());
        Assert.assertEquals("pkey_left2", joinQuery.getLeftmostAtomicQuery().getKeys());
    }
}
