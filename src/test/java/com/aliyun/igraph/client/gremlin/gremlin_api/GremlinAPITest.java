package com.aliyun.igraph.client.gremlin.gremlin_api;

import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.pg.KeyList;
import com.aliyun.igraph.client.core.model.UpdateQuery;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.igraph.client.gremlin.gremlin_api.Supplier.supplier;
import static com.aliyun.igraph.client.gremlin.gremlin_api.__.*;

/**
 * Created by wl on 2018/12/10.
 */
public class GremlinAPITest {

    @Test
    public void testObject() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a");
        }
        {
            try {
                Integer ax = 123;
                GraphTraversal gt = GraphTraversalSource.g("my_graph").V(ax).hasLabel("lbl_a");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }
    }
    // probably not true grammar
    @Test
    public void replaceKeyApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a");
            gt.setKeys("pk2;pk3");
            String expected = "g(\"my_graph\").V(\"pk2;pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            Assert.assertEquals("\"pk2;pk3\"", gt.getKeys());
            gt.setKeys("pk2", "pk3");
            expected = "g(\"my_graph\").V(\"pk2\",\"pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            Assert.assertEquals("\"pk2\",\"pk3\"", gt.getKeys());
            gt.setKeys("pk4", "pk5", "pk6");
            expected = "g(\"my_graph\").V(\"pk4\",\"pk5\",\"pk6\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            Assert.assertEquals("\"pk4\",\"pk5\",\"pk6\"", gt.getKeys());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a");
            gt.setKeys("pk2;pk3");
            String expected = "g(\"my_graph\").E(\"pk2;pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            gt.setKeys("pk2", "pk3");
            expected = "g(\"my_graph\").E(\"pk2\",\"pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            gt.setKeys("pk4", "pk5", "pk6");
            expected = "g(\"my_graph\").E(\"pk4\",\"pk5\",\"pk6\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V().hasLabel("lbl_a");
            String expected = "g(\"my_graph\").V().hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            gt.setKeys("pk2;pk3");
            expected = "g(\"my_graph\").V(\"pk2;pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            gt.setKeys("pk2", "pk3");
            expected = "g(\"my_graph\").V(\"pk2\",\"pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            gt.setKeys("pk4", "pk5", "pk6");
            expected = "g(\"my_graph\").V(\"pk4\",\"pk5\",\"pk6\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            KeyList key1 = new KeyList("a", "skey");
            KeyList key2 = new KeyList("b", "子key");
            KeyList key3 = new KeyList("c", "子key2", "skey");
            List<KeyList> keys = new ArrayList<KeyList>();
            keys.add(key1);
            keys.add(key2);
            keys.add(key3);
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V(keys).hasLabel("lbl_a");
            String expected = "g(\"my_graph\").V(\"a:skey;b:%E5%AD%90key;c:%E5%AD%90key2|skey\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            gt.setKeys("pk2", "pk3");
            expected = "g(\"my_graph\").V(\"pk2\",\"pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            KeyList key3 = new KeyList("c", "子key2", "skey");
            List<KeyList> keys = new ArrayList<KeyList>();
            keys.add(key3);
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E(keys).hasLabel("lbl_a");
            String expected = "g(\"my_graph\").E(\"c:%E5%AD%90key2|skey\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            gt.setKeys("pk2", "pk3");
            expected = "g(\"my_graph\").E(\"pk2\",\"pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a");
            gt.setKeys("pk2;pk3");
            String expected = "g(\"my_graph\").E(\"pk2;pk3\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
            KeyList key1 = new KeyList("a", "skey");
            KeyList key2 = new KeyList("b", "子key");
            KeyList key3 = new KeyList("c", "子key2", "skey");
            List<KeyList> keys = new ArrayList<KeyList>();
            keys.add(key1);
            keys.add(key2);
            keys.add(key3);
            gt.setKeys(keys);
            expected = "g(\"my_graph\").E(\"a:skey;b:%E5%AD%90key;c:%E5%AD%90key2|skey\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
    }

    @Test
    public void sourceApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a", "lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\",\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1", "pk2", "pk3", "pk4").hasLabel("lbl_a", "lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").V(\"pk1\",\"pk2\",\"pk3\",\"pk4\").hasLabel(\"lbl_a\",\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").by("tbl_name");
            String expected = "g(\"my_graph\").V(\"pk1\").by(\"tbl_name\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            KeyList key3 = new KeyList("c", "子key2", "skey");
            List<KeyList> keys = new ArrayList<KeyList>();
            keys.add(key3);
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V(keys).hasLabel("lbl_a", "lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").V(\"c:%E5%AD%90key2|skey\").hasLabel(\"lbl_a\",\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            KeyList key1 = new KeyList("a", "skey");
            KeyList key2 = new KeyList("b", "子key");
            KeyList key3 = new KeyList("c", "子key2", "skey");
            List<KeyList> keys = new ArrayList<KeyList>();
            keys.add(key1);
            keys.add(key2);
            keys.add(key3);
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V(keys).hasLabel("lbl_a", "lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").V(\"a:skey;b:%E5%AD%90key;c:%E5%AD%90key2|skey\").hasLabel(\"lbl_a\",\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            KeyList key3 = new KeyList("c", "子key2", "skey");
            List<KeyList> keys = new ArrayList<KeyList>();
            keys.add(key3);
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E(keys).hasLabel("lbl_a", "lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").E(\"c:%E5%AD%90key2|skey\").hasLabel(\"lbl_a\",\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            KeyList key1 = new KeyList("a", "skey");
            KeyList key2 = new KeyList("b", "子key");
            KeyList key3 = new KeyList("c", "子key2", "skey");
            List<KeyList> keys = new ArrayList<KeyList>();
            keys.add(key1);
            keys.add(key2);
            keys.add(key3);
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E(keys).hasLabel("lbl_a", "lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").E(\"a:skey;b:%E5%AD%90key;c:%E5%AD%90key2|skey\").hasLabel(\"lbl_a\",\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).V("pk1").hasLabel("lbl_a");
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).V(\"pk1\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithParam.PushDownStrategy("x", "y")).V("pk1").hasLabel("lbl_a");
            String expected = "g(\"my_graph\").withStrategies(PushDownStrategy(\"x\",\"y\")).V(\"pk1\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithParam.PathRecordStrategy(PathRecord.edge, PathRecord.vertex)).V("pk1").hasLabel("lbl_a");
            String expected = "g(\"my_graph\").withStrategies(PathRecordStrategy(PathRecord.edge,PathRecord.vertex)).V(\"pk1\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithParam.PushDownStrategy("x"), StrategyWithoutParam.UniqueStrategy, StrategyWithParam.PathRecordStrategy(PathRecord.edge, PathRecord.map)).V("pk1").hasLabel("lbl_a");
            String expected = "g(\"my_graph\").withStrategies(PushDownStrategy(\"x\"),UniqueStrategy,PathRecordStrategy(PathRecord.edge,PathRecord.map)).V(\"pk1\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KKV, Supplier.SackType.FLOAT, "123")).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kkv,float,\"123\")).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KV, Supplier.SackType.FLOAT, "123")).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kv,float,\"123\")).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.NORMAL, Supplier.SackType.FLOAT, "123")).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(normal,float,\"123\")).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KKV, Supplier.SackType.INTEGER, "123")).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kkv,integer,\"123\")).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KKV, Supplier.SackType.STRING, "123")).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kkv,string,\"123\")).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KKV, Supplier.SackType.STRING, "123"), Splitter.identity, Operator.assign).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kkv,string,\"123\"),Splitter.identity,Operator.assign).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KKV, Supplier.SackType.STRING, "123"), Splitter.clone, Operator.assign).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kkv,string,\"123\"),Splitter.clone,Operator.assign).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KKV, Supplier.SackType.STRING, "123"), Splitter.fastclone, Operator.assign).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kkv,string,\"123\"),Splitter.fastclone,Operator.assign).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).withSack(supplier(Supplier.SupplierType.KKV, Supplier.SackType.STRING, "123"), Splitter.identity, Operator.assign).V("pk").has(T.label, P.eq("lbl_a"));
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).withSack(supplier(kkv,string,\"123\"),Splitter.identity,Operator.assign).V(\"pk\").has(T.label,P.eq(\"lbl_a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").has(T.label, P.eq("abc"));
            String expected = "g(\"my_graph\").V(\"pk1\").has(T.label,P.eq(\"abc\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).V().hasLabel("lbl_a").indexQuery("{\"match\":{\"lang\":\"java\"}}");
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).V().hasLabel(\"lbl_a\").indexQuery(\"{\"match\":{\"lang\":\"java\"}}\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").withStrategies(StrategyWithoutParam.UniqueStrategy).V().indexQuery("{\"match\":{\"lang\":\"java\"}}").hasLabel("lbl_a");
            String expected = "g(\"my_graph\").withStrategies(UniqueStrategy).V().indexQuery(\"{\"match\":{\"lang\":\"java\"}}\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
    }
    @Test
    public void directTableAccessTest() {
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.V("pk1").by("my_table");
            String expected = "g.V(\"pk1\").by(\"my_table\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.V("pk1","pk2").by("my_table1").outE().by("my_table2");
            String expected = "g.V(\"pk1\",\"pk2\").by(\"my_table1\").outE().by(\"my_table2\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.V("pk1","pk2").by("my_table1").outE().by("my_table2").inV().by("my_table3");
            String expected = "g.V(\"pk1\",\"pk2\").by(\"my_table1\").outE().by(\"my_table2\").inV().by(\"my_table3\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.E("pk1").by("my_table");
            String expected = "g.E(\"pk1\").by(\"my_table\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.E("pk1").by("my_table1").inV().by("my_table2");
            String expected = "g.E(\"pk1\").by(\"my_table1\").inV().by(\"my_table2\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.E("pk1").by("my_table1").outE().by("my_table2", "pk").barrier().local(dedup());
            String expected = "g.E(\"pk1\").by(\"my_table1\").outE().by(\"my_table2\",\"pk\").barrier().local(__.dedup())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.V("pk1").by("my_table1").alias("pk:pkey")
                    .join(Join.left,"join_tag:1|2").by("pkey","my_table2",filter("value>0.2")
                    .order().by("value",Order.decr).range(0,10)).order().by("value",Order.decr);
            String expected = "g.V(\"pk1\").by(\"my_table1\").alias(\"pk:pkey\").join(Join.left,\"join_tag:1|2\")" +
                    ".by(\"pkey\",\"my_table2\",__.filter(\"value>0.2\").order().by(\"value\",Order.decr).range(0,10)).order().by(\"value\",Order.decr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.V("pk1").by("my_table1").alias("pk:pkey").join()
                    .by("pkey","my_table2",filter("value>0.2").order()
                    .by("value",Order.decr).range(0,10)).order().by("value",Order.decr);
            String expected = "g.V(\"pk1\").by(\"my_table1\").alias(\"pk:pkey\").join()" +
                    ".by(\"pkey\",\"my_table2\",__.filter(\"value>0.2\").order()" +
                    ".by(\"value\",Order.decr).range(0,10)).order().by(\"value\",Order.decr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversalSource g = new GraphTraversalSource();
            GraphTraversal gt = g.V("pk1").by("my_table1").alias("pk:pkey").join()
                    .by("pkey","my_table2",filter("value>0.2").order()
                    .by("value",Order.decr).range(0,10)).order().by("value",Order.decr).outE().by("my_table3","sk");
            String expected = "g.V(\"pk1\").by(\"my_table1\").alias(\"pk:pkey\").join()" +
                    ".by(\"pkey\",\"my_table2\",__.filter(\"value>0.2\").order().by(\"value\",Order.decr)" +
                    ".range(0,10)).order().by(\"value\",Order.decr).outE().by(\"my_table3\",\"sk\")";
            Assert.assertEquals(expected, gt.toString());
        }
    }

    @Test
    public void mapApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").by("lbl_name").map("score > 10");
            String expected = "g(\"my_graph\").E(\"pk1\").by(\"lbl_name\").map(\"score > 10\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map("score > 10");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(\"score > 10\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.map("score > 10"));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.map(\"score > 10\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").flatMap(__.label());
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").flatMap(__.label())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.flatMap(__.label()));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.flatMap(__.label()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").label();
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").label()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.label());
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.label())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").identity();
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").identity()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.identity());
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.identity())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").constant(1);
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").constant(1)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").constant(1.23);
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").constant(1.23)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").constant("const_val");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").constant(\"const_val\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.constant(1));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.constant(1))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.constant("const_val"));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.constant(\"const_val\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").to(org.apache.tinkerpop.gremlin.structure.Direction.IN);
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").to(Direction.IN)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").to(org.apache.tinkerpop.gremlin.structure.Direction.IN, "lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").to(Direction.IN,\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.to(org.apache.tinkerpop.gremlin.structure.Direction.OUT, "lbl_b"));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.to(Direction.OUT,\"lbl_b\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").out("lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").out(\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").in("lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").in(\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.both("lbl_b"));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.both(\"lbl_b\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.toE(org.apache.tinkerpop.gremlin.structure.Direction.IN, "lbl_b"));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.toE(Direction.IN,\"lbl_b\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").out();
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").out()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").toE(org.apache.tinkerpop.gremlin.structure.Direction.IN, "lbl_b");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").toE(Direction.IN,\"lbl_b\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").outE();
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").outE()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").inE("lbl_b");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").inE(\"lbl_b\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.inE("lbl_b"));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.inE(\"lbl_b\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").bothE("lbl_b", "lbl_c");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").bothE(\"lbl_b\",\"lbl_c\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").map(__.bothE("lbl_b", "lbl_c"));
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").map(__.bothE(\"lbl_b\",\"lbl_c\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").toV(org.apache.tinkerpop.gremlin.structure.Direction.IN);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").toV(Direction.IN)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.toV(org.apache.tinkerpop.gremlin.structure.Direction.IN));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.toV(Direction.IN))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").inV();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").inV()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.inV());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.inV())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").outV();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").outV()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.outV());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.outV())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.bothV());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.bothV())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.order());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.order())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").properties();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").properties()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").properties("pk1");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").properties(\"pk1\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.properties("p1", "p2"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.properties(\"p1\",\"p2\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").values();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").values()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").values("pk1");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").values(\"pk1\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.values("p1", "p2"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.values(\"p1\",\"p2\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.propertyMap("p1", "p2"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.propertyMap(\"p1\",\"p2\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.valueMap("p1", "p2"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.valueMap(\"p1\",\"p2\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.select(Column.keys));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.select(Column.keys))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.select(Column.values));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.select(Column.values))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.key());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.key())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.value());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.value())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.path());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.path())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(sack());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.sack())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.loops());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.loops())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.select(org.apache.tinkerpop.gremlin.process.traversal.Pop.all, "a"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.select(Pop.all,\"a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.select(org.apache.tinkerpop.gremlin.process.traversal.Pop.all, "a", "b"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.select(Pop.all,\"a\",\"b\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.select("a"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.select(\"a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.select("a", "b", "c"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.select(\"a\",\"b\",\"c\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.fold());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.fold())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.unfold());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.unfold())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.count());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.count())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.count(org.apache.tinkerpop.gremlin.process.traversal.Scope.global));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.count(Scope.global))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.sum());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.sum())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.sum(org.apache.tinkerpop.gremlin.process.traversal.Scope.local));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.sum(Scope.local))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.max());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.max())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.max(org.apache.tinkerpop.gremlin.process.traversal.Scope.local));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.max(Scope.local))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.min());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.min())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.mean());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.mean())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.group());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.group())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.groupCount());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.groupCount())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.to("abc"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.to(\"abc\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.from("abc"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.from(\"abc\"))";
            Assert.assertEquals(expected, gt.toString());
        }

    }

    @Test
    public void filterApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter("a > 10");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(\"a > 10\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.from("abc"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.from(\"abc\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").or(__.outE("knows"), __.or(__.max(), __.map(__.bothV())));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").or(__.outE(\"knows\"),__.or(__.max(),__.map(__.bothV())))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").or();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").or()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").and(__.outE("knows"), __.or(__.max(), __.map(__.bothV())));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").and(__.outE(\"knows\"),__.or(__.max(),__.map(__.bothV())))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").and();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").and()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(dedup("abc"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.dedup(\"abc\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.where(P.eq("abc")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.where(P.eq(\"abc\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.where("c", P.eq("abc")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.where(\"c\",P.eq(\"abc\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.where(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.where(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").has("name", P.eq("Peter").negate());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").has(\"name\",P.eq(\"Peter\").negate())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.has("age", P.gt(10)));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.has(\"age\",P.gt(10)))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.has(T.label, P.eq("lbl_a")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.has(T.label,P.eq(\"lbl_a\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").has("name", __.outE("edge").inV());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").has(\"name\",__.outE(\"edge\").inV())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.has("age", 10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.has(\"age\",10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.has("age", 10.3));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.has(\"age\",10.3))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.has("name", "10"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.has(\"name\",\"10\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.has("name"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.has(\"name\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.hasLabel("name"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.hasLabel(\"name\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.hasLabel("name", "age"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.hasLabel(\"name\",\"age\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.hasKey("name", "age"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.hasKey(\"name\",\"age\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.hasKey(P.eq("name")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.hasKey(P.eq(\"name\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.hasValue(1, 2, 3));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.hasValue(1,2,3))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.hasValue("1", "2", "3"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.hasValue(\"1\",\"2\",\"3\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.hasValue(P.gt(10).and(P.lte(20))));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.hasValue(P.gt(10).and(P.lte(20))))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.is(P.gt(10).and(P.lte(20))));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.is(P.gt(10).and(P.lte(20))))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.is(10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.is(10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.is("10"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.is(\"10\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").not(__.is("10"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").not(__.is(\"10\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.range(10, 20));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.range(10,20))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.limit(10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.limit(10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.tail());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.tail())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.tail(10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.tail(10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.tail(org.apache.tinkerpop.gremlin.process.traversal.Scope.global, 10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.tail(Scope.global,10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.tail(org.apache.tinkerpop.gremlin.process.traversal.Scope.global));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.tail(Scope.global))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.simplePath());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.simplePath())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.cyclicPath());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.cyclicPath())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.sample(10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sample(10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.sample(Sample.duplicatable, 10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sample(Sample.duplicatable,10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.sample(Sample.duplicatable, Sample.upsampling, 10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sample(Sample.duplicatable,Sample.upsampling,10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").distinct().by("pk");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").distinct().by(\"pk\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").distinct(Distinct.isReserved).by("pk");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").distinct(Distinct.isReserved).by(\"pk\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").distinct(Distinct.round,2).by("pk");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").distinct(Distinct.round,2).by(\"pk\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").distinct(Distinct.amount,2, Distinct.isReserved).by("pk");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").distinct(Distinct.amount,2,Distinct.isReserved).by(\"pk\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").distinct(Distinct.round,2,Distinct.amount,1).by("pk");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").distinct(Distinct.round,2,Distinct.amount,1).by(\"pk\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").distinct(Distinct.round,2,Distinct.amount,1, Distinct.isReserved).by("pk");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").distinct(Distinct.round,2,Distinct.amount,1,Distinct.isReserved).by(\"pk\")";
            Assert.assertEquals(expected, gt.toString());
        }
    }

    @Test
    public void sideEffectApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.aggregate("name"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.aggregate(\"name\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").aggregate("x").by("name").cap("x");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").aggregate(\"x\").by(\"name\").cap(\"x\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").aggregate("x").by("name").outE().aggregate("y").cap("x","y").dedup();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").aggregate(\"x\").by(\"name\").outE().aggregate(\"y\").cap(\"x\",\"y\").dedup()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").group("x").by("name").outE().group("y").cap("x","y").dedup();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").group(\"x\").by(\"name\").outE().group(\"y\").cap(\"x\",\"y\").dedup()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").groupCount("x").by("name").outE().groupCount("y").cap("x","y").dedup();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").groupCount(\"x\").by(\"name\").outE().groupCount(\"y\").cap(\"x\",\"y\").dedup()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(sack(Operator.assign));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sack(Operator.assign))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(sack(Operator.max));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sack(Operator.max))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(sack(Operator.min));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sack(Operator.min))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(sack(Operator.sum));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sack(Operator.sum))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(sack(Operator.minus));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sack(Operator.minus))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(sack(Operator.div));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sack(Operator.div))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.bulk());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.bulk())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.toList());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.toList())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.withBulk());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.withBulk())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.withSack());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.withSack())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.needFold());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.needFold())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.fields("a"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.fields(\"a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").filter(__.fields("a", "b", "c"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").filter(__.fields(\"a\",\"b\",\"c\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1").hasLabel("lbl_a").store("x").by("name").cap("x");
            String expected = "g(\"my_graph\").V(\"pk1\").hasLabel(\"lbl_a\").store(\"x\").by(\"name\").cap(\"x\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph")
                    .withSack(supplier(Supplier.SupplierType.NORMAL, "{\"k1\":[\"abc\"], \"k2\": 1}"), Splitter.fastclone, "{\"k1\": \"Operator.addall\", \"k2\": \"Operator.sum\"}")
                    .V("pk1").hasLabel("lbl_a").sideEffect(sack().get("k2").aggregate("x")).select("x");
            String expected = "g(\"my_graph\").withSack(supplier(normal,\"{\"k1\":[\"abc\"], \"k2\": 1}\"),Splitter.fastclone,\"{\"k1\": \"Operator.addall\", \"k2\": \"Operator.sum\"}\").V(\"pk1\").hasLabel(\"lbl_a\").sideEffect(__.sack().get(\"k2\").aggregate(\"x\")).select(\"x\")";
            Assert.assertEquals(expected, gt.toString());
        }
    }

    @Test
    public void branchApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.branch(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.branch(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").choose(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").choose(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.choose(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.choose(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").choose(__.hasLabel("name"), __.outE("know"), __.outE("like"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").choose(__.hasLabel(\"name\"),__.outE(\"know\"),__.outE(\"like\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.choose(__.hasLabel("name"), __.outE("know"), __.outE("like")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.choose(__.hasLabel(\"name\"),__.outE(\"know\"),__.outE(\"like\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").choose(__.hasLabel("name"), __.outE("know"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").choose(__.hasLabel(\"name\"),__.outE(\"know\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.choose(__.hasLabel("name"), __.outE("know")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.choose(__.hasLabel(\"name\"),__.outE(\"know\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").optional(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").optional(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.optional(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.optional(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").union(__.outE("a"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").union(__.outE(\"a\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").union(__.outE("a"), __.outE("b"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").union(__.outE(\"a\"),__.outE(\"b\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").union(__.outE("a"), __.outE("b"), __.outE("c"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").union(__.outE(\"a\"),__.outE(\"b\"),__.outE(\"c\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.union(__.outE("a")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.union(__.outE(\"a\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").map(__.coalesce(__.outE("a"), __.outE("b"), __.outE("c")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").map(__.coalesce(__.outE(\"a\"),__.outE(\"b\"),__.outE(\"c\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").repeat(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").repeat(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.repeat(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.repeat(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").emit(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").emit(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.emit(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.emit(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.emit());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.emit())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").until(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").until(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.until(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.until(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.emit(__.outE()).times(10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.emit(__.outE()).times(10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.times(10));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.times(10))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").local(sack(Operator.mult));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").local(__.sack(Operator.mult))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.local(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.local(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
    }

    @Test
    public void utilityApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.local(__.as("a")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.local(__.as(\"a\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.local(__.as("a", "b")));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.local(__.as(\"a\",\"b\")))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.barrier());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.barrier())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.barrier(5));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.barrier(5))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.barrier(Barrier.dedup));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.barrier(Barrier.dedup))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.barrier(Barrier.nodedup));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.barrier(Barrier.nodedup))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by();
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by()";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(__.outE());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(__.outE())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by(T.value));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(T.value))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(T.value);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(T.value)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by("name"));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(\"name\"))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by("name");
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(\"name\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by(__.outE(), Order.incr));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(__.outE(),Order.incr))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(__.outE(), Order.decr);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(__.outE(),Order.decr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by(Order.incr));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(Order.incr))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Order.incr);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Order.incr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by(T.label, Order.decr));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(T.label,Order.decr))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(T.key, Order.incr);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(T.key,Order.incr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by("name", Order.incr));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(\"name\",Order.incr))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by("name", Order.decr);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(\"name\",Order.decr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.order().by(Column.keys, Order.incr));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.order().by(Column.keys,Order.incr))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr);
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr)";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).option(1, __.outE());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).option(1,__.outE())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).option("abc", __.outE());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).option(\"abc\",__.outE())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).option(Pick.any, __.outE());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).option(any,__.outE())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).option(Pick.none, __.outE());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).option(none,__.outE())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).branch(__.option(1, __.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).branch(__.option(1,__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).branch(__.option("abc", __.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).branch(__.option(\"abc\",__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).branch(__.option(Pick.any, __.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).branch(__.option(any,__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").order().by(Column.values, Order.decr).branch(__.option(Pick.none, __.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").order().by(Column.values,Order.decr).branch(__.option(none,__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
    }

    @Test
    public void noSupportApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").noSupportedStep("newWithStep", 123, supplier(Supplier.SupplierType.KV, Supplier.SackType.FLOAT, "1=3")).V("pk1").hasLabel("lbl_a");
            String expected = "g(\"my_graph\").newWithStep(123,supplier(kv,float,\"1=3\")).V(\"pk1\").hasLabel(\"lbl_a\")";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").noSupportedStep("newStep").union(__.outE());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").newStep().union(__.outE())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").noSupportedStep("newStep", 123, "efg", __.outE()).union(__.outE());
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").newStep(123,\"efg\",__.outE()).union(__.outE())";
            Assert.assertEquals(expected, gt.toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").E("pk1").hasLabel("lbl_a").branch(__.noSupportedStep("newStep", 123, "efg", __.outE()).union(__.outE()));
            String expected = "g(\"my_graph\").E(\"pk1\").hasLabel(\"lbl_a\").branch(__.newStep(123,\"efg\",__.outE()).union(__.outE()))";
            Assert.assertEquals(expected, gt.toString());
        }
    }

    @Test
    public void updateApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").addV("my_label").property("pkey","pk").property("value_1", "0.1").property("value_2", "0.5");
            Assert.assertEquals(GremlinQueryType.UPDATE, gt.getGremlinQueryType());
            UpdateQuery updateQuery = UpdateQuery.builder()
                    .table("my_graph_my_label")
                    .pkey("pk")
                    .valueMap("value_1", "0.1")
                    .valueMap("value_2", "0.5")
                    .build();
            Assert.assertEquals(updateQuery.toString(), gt.getUpdateQuery().toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").addV("my_label").property("value_1", "0.1").property("value_2", "0.5");
            Assert.assertEquals(GremlinQueryType.UPDATE, gt.getGremlinQueryType());
            try {
                gt.getUpdateQuery().toString();
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("empty table/pkey is not allowed!", e.getMessage());
            }
        }
        {
            try {
                GraphTraversal gt = GraphTraversalSource.g("my_graph").addV("my_label").has("fake_label").property("value_1", "0.1").property("value_2", "0.5");
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("complex step is not supported in update query!", e.getMessage());
            }
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").addE("my_label").property("pkey","pk").property("skey", "sk").property("value_1", "0.1").property("value_2", "0.5");
            Assert.assertEquals(GremlinQueryType.UPDATE, gt.getGremlinQueryType());
            UpdateQuery updateQuery = UpdateQuery.builder()
                    .table("my_graph_my_label")
                    .pkey("pk")
                    .skey("sk")
                    .valueMap("value_1", "0.1")
                    .valueMap("value_2", "0.5")
                    .build();
            Assert.assertEquals(updateQuery.toString(), gt.getUpdateQuery().toString());
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").addE("my_label").property("skey", "sk").property("value_1", "0.1").property("value_2", "0.5");
            Assert.assertEquals(GremlinQueryType.UPDATE, gt.getGremlinQueryType());
            try {
                gt.getUpdateQuery().toString();
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("empty table/pkey is not allowed!", e.getMessage());
            }
        }
        {
            try {
                GraphTraversal gt = GraphTraversalSource.g("my_graph").addE("my_label").property("pkey", "pk").property("skey", "sk").property("value_1", "0.1").property("value_2", "0.5").outE("fake_label");
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("complex step is not supported in update query!", e.getMessage());
            }
        }
    }

    @Test
    public void deleteApiTest() {
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk").hasLabel("my_label").drop();
            Assert.assertEquals(GremlinQueryType.DELETE, gt.getGremlinQueryType());
            UpdateQuery updateQuery = UpdateQuery.builder()
                    .table("my_graph_my_label")
                    .pkey("pk")
                    .build();
            Assert.assertEquals(updateQuery.toString(), gt.getUpdateQuery().toString());
        }
        {
            try {
                GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1", "pk2").hasLabel("my_label").drop();
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("batch delete is not supported!", e.getMessage());
            }
        }
        {
            try {
                GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk").hasLabel("my_label").outE().drop();
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("complex step is not supported in delete query!", e.getMessage());
            }
        }
        {
            GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk:sk").hasLabel("my_label").drop();
            Assert.assertEquals(GremlinQueryType.DELETE, gt.getGremlinQueryType());
            UpdateQuery updateQuery = UpdateQuery.builder()
                    .table("my_graph_my_label")
                    .pkey("pk")
                    .skey("sk")
                    .build();
            Assert.assertEquals(updateQuery.toString(), gt.getUpdateQuery().toString());
        }
        {
            try {
                GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk1:sk1", "pk2:sk1").hasLabel("my_label").drop();
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("batch delete is not supported!", e.getMessage());
            }
        }
        {
            try {
                GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk:sk").hasLabel("my_label").outE().drop();
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("complex step is not supported in delete query!", e.getMessage());
            }
        }
        {
            try {
                GraphTraversal gt = GraphTraversalSource.g("my_graph").V("pk:sk1|sk2|sk3").hasLabel("my_label").drop();
                Assert.fail();
            } catch (IGraphQueryException e) {
                Assert.assertEquals("batch delete is not supported!", e.getMessage());
            }
        }
    }
}

