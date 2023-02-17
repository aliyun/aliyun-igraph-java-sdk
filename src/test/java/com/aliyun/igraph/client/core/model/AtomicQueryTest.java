package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.KeyList;
import com.google.common.collect.Lists;
import com.aliyun.igraph.client.exception.IGraphClientException;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class AtomicQueryTest {
    @Test
    public void testEquals() throws Exception {
        {
            AtomicQuery atomicQuery1 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .build();
            AtomicQuery atomicQuery2 = atomicQuery1;
            Assert.assertTrue(atomicQuery1.equals(atomicQuery2));
        }
        {

            AtomicQuery atomicQuery1 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .build();
            AtomicQuery atomicQuery2 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .build();
            Assert.assertTrue(atomicQuery1.equals(atomicQuery2));
        }
        {
            AtomicQuery atomicQuery1 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .build();
            AtomicQuery atomicQuery2 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey2"))
                    .build();
            Assert.assertFalse(atomicQuery1.equals(atomicQuery2));
        }
        {
            List<KeyList> keyLists1 = new ArrayList<>();
            keyLists1.add(new KeyList("pkey0", "skey1", "skey2"));
            AtomicQuery atomicQuery1 = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(keyLists1)
                    .build();

            List<KeyList> keyLists2 = new ArrayList<>();
            keyLists2.add(new KeyList("pkey0", "skey1", "skey2"));
            AtomicQuery atomicQuery2 = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(keyLists2)
                    .build();
            Assert.assertTrue(atomicQuery1.equals(atomicQuery2));
        }
        {
            List<KeyList> keyLists1 = new ArrayList<>();
            keyLists1.add(new KeyList("pkey0", "skey2", "skey1"));
            AtomicQuery atomicQuery1 = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(keyLists1)
                    .build();

            List<KeyList> keyLists2 = new ArrayList<>();
            keyLists2.add(new KeyList("pkey0", "skey1", "skey2"));
            AtomicQuery atomicQuery2 = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(keyLists2)
                    .build();
            Assert.assertFalse(atomicQuery1.equals(atomicQuery2));
        }
    }

    @Test
    public void testConstructor() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl0")
                    .build();
            Assert.assertEquals("search{table=tbl0}", atomicQuery.toString());
            Assert.assertTrue(atomicQuery.isJoinQueryOnly());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
            Assert.assertFalse(atomicQuery.isJoinQueryOnly());
        }
        {
            List<KeyList> keyLists = new ArrayList<>();
            keyLists.add(new KeyList("pkey0"));
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(keyLists)
                    .build();
            Assert.assertEquals("search{table=tbl2&keys=pkey0}", atomicQuery.toString());
            Assert.assertFalse(atomicQuery.isJoinQueryOnly());
        }
        {
            List<KeyList> keyLists = new ArrayList<>();
            keyLists.add(new KeyList("pkey0"));
            keyLists.add(new KeyList("pkey1"));
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl3")
                    .keyLists(keyLists)
                    .build();
            Assert.assertEquals("search{table=tbl3&keys=pkey0;pkey1}", atomicQuery.toString());
            Assert.assertFalse(atomicQuery.isJoinQueryOnly());
        }
        {
            String indexSearch = "\\{\"match\":\\{\"nick_index\":\"zhangsan\"\\}\\}";
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("test_table")
                    .indexSearch(indexSearch)
                    .build();
            String expect = "search{table=test_table&indexsearch=\\{\"match\":\\{\"nick_index\":\"zhangsan\"\\}\\}}";
            Assert.assertEquals(expect, atomicQuery.toString());
        }
    }

    @Test
    public void testConstructorWithNull() throws Exception {
        {
            try {
                AtomicQuery atomicQuery = AtomicQuery.builder()
                        .table(null)
                        .build();
                Assert.fail();
            } catch (NullPointerException ignored) {

            }
        }
        {
            try {
                AtomicQuery atomicQuery = AtomicQuery.builder()
                        .table(null)
                        .keyList((KeyList) null)
                        .build();
                Assert.fail();
            } catch (NullPointerException ignored) {
            }
        }
        {
            try {
                AtomicQuery atomicQuery = AtomicQuery.builder()
                        .table(null)
                        .keyLists((List<KeyList>) null)
                        .build();
                Assert.fail();
            } catch (NullPointerException ignored) {

            }
        }
    }

    @Test
    public void testSetReturnFields() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .field("f0")
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&fields=f0}", atomicQuery.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .fields(Lists.newArrayList("f0", "f1"))
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&fields=f0;f1}", atomicQuery.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .fields(null)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
        }
    }

    @Test
    public void testSetRange() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .range(1,2)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&range=1:2}", atomicQuery.toString());
        }
        {
            try{
                AtomicQuery atomicQuery = AtomicQuery.builder()
                        .table("tbl1")
                        .keyList(new KeyList("pkey"))
                        .range(-1,-2)
                        .build();
                Assert.fail();
            } catch (IGraphClientException ignored) {
            }
        }
    }

    @Test
    public void testSetFilter() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .filter("pkey>0")
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&filter=pkey>0}", atomicQuery.toString());
            AtomicQuery atomicQuery2 = atomicQuery.toBuilder().filter("pkey=0").build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&filter=pkey=0}", atomicQuery2.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .filter(null)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
        }
    }

    @Test
    public void testSetOrderby() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .orderby("age1")
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&orderby=age1}", atomicQuery.toString());
            AtomicQuery atomicQuery2 = atomicQuery.toBuilder().orderby("age2").build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&orderby=age2}", atomicQuery2.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .orderby(null)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
        }
    }

    @Test
    public void testSetSorter() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .sorter("desc")
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&sorter=desc}", atomicQuery.toString());
            AtomicQuery atomicQuery2 = atomicQuery.toBuilder().sorter("asc").build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&sorter=asc}", atomicQuery2.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .sorter(null)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
        }
    }

    @Test
    public void testSetDistinct() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .distinct("pkey")
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&distinct=pkey}", atomicQuery.toString());
            AtomicQuery atomicQuery2 = atomicQuery.toBuilder().distinct("skey").build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&distinct=skey}", atomicQuery2.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .distinct(null)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
        }
    }

    @Test
    public void testLocalCount() throws Exception {
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .localCount(10)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&localcount=10}", atomicQuery.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .localCount(-1)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
        }
    }

    @Test
    public void testAlias() throws Exception {
        {
            Map<String, String> alias = new LinkedHashMap<String, String>() {
                {
                    put("f_1", "f1");
                }
            };
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyList(new KeyList("pkey"))
                    .aliases(alias)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&alias=f_1:f1}", atomicQuery.toString());
        }
        {
            Map<String, String> alias = new LinkedHashMap<String, String>() {
                {
                    put("f_1", "f1");
                    put("f_2", "f2");
                }
            };
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey")))
                    .aliases(alias)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&alias=f_2:f2;f_1:f1}", atomicQuery.toString());
        }
        {
            Map<String, String> alias1 = new LinkedHashMap<String, String>() {
                {
                    put("f_1", "f1");
                    put("f_2", "f2");
                }
            };
            AtomicQuery atomicQuery1 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey")))
                    .aliases(alias1)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&alias=f_2:f2;f_1:f1}", atomicQuery1.toString());
            Map<String, String> alias2 = new LinkedHashMap<String, String>();
            alias2.put("f_3", "f3");
            AtomicQuery atomicQuery2 = atomicQuery1.toBuilder().clearAliases().aliases(alias2).build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&alias=f_3:f3}", atomicQuery2.toString());
        }
        {
            Map<String, String> alias = new LinkedHashMap<String, String>();
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey")))
                    .aliases(alias)
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
        }
        {
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey")))
                    .alias("f_1","f1")
                    .build();
            Assert.assertEquals("search{table=tbl1&keys=pkey&alias=f_1:f1}", atomicQuery.toString());
        }
        {
            try {
                AtomicQuery atomicQuery = AtomicQuery.builder()
                        .table("tbl1")
                        .keyLists(Lists.newArrayList(new KeyList("pkey")))
                        .aliases(null)
                        .build();
                Assert.assertEquals("search{table=tbl1&keys=pkey}", atomicQuery.toString());
            } catch (NullPointerException exception) {
            }
        }
    }

    @Test
    public void testLeftMostQuery() throws Exception {
        AtomicQuery atomicQuery = AtomicQuery.builder()
                .table("tbl1")
                .keyList(new KeyList("pkey"))
                .build();
        atomicQuery.toString();
        Assert.assertEquals(atomicQuery, atomicQuery.getLeftmostAtomicQuery());
        Assert.assertEquals("pkey", atomicQuery.getLeftmostAtomicQuery().getKeys());
    }

    @Test
    public  void testToBuilder() throws Exception {
        try {
            AtomicQuery atomicQuery = AtomicQuery.builder().table("a").build();
            AtomicQuery atomicQuery1 = atomicQuery.toBuilder().build();
        } catch (Exception unexpected) {
            Assert.fail();
        }
    }
}
