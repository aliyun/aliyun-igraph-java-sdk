package com.aliyun.igraph.client.dailytest;

import com.aliyun.igraph.client.gremlin.gremlin_api.*;
import com.aliyun.igraph.client.core.IGraphClient;
import com.aliyun.igraph.client.gremlin.driver.Client;
import com.aliyun.igraph.client.gremlin.driver.Cluster;
import com.aliyun.igraph.client.gremlin.gremlin_api.T;
import com.aliyun.igraph.client.gremlin.structure.IGraphMultiResultSet;
import com.aliyun.igraph.client.gremlin.structure.IGraphResult;
import com.aliyun.igraph.client.gremlin.structure.IGraphResultSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Pop;
import org.apache.tinkerpop.gremlin.structure.*;
import org.javatuples.Pair;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.aliyun.igraph.client.gremlin.gremlin_api.GraphTraversalSource.g;
import static com.aliyun.igraph.client.gremlin.gremlin_api.StrategyWithParam.PathRecordStrategy;
import static com.aliyun.igraph.client.gremlin.gremlin_api.StrategyWithParam.PushDownStrategy;
import static com.aliyun.igraph.client.gremlin.gremlin_api.Supplier.SackType.FLOAT;
import static com.aliyun.igraph.client.gremlin.gremlin_api.Supplier.SupplierType.NORMAL;
import static com.aliyun.igraph.client.gremlin.gremlin_api.Supplier.supplier;
import static com.aliyun.igraph.client.gremlin.gremlin_api.__.*;

public class GremlinTest {
    private static Logger logger;

    private static void initLogger() {
        logger = LoggerFactory.getLogger("com.taobao.iGraph");
    }

    private static final AtomicLong time = new AtomicLong(0);
    private static final AtomicLong allRt = new AtomicLong(0);
    private static final AtomicInteger qps = new AtomicInteger(0);
    private static final AtomicInteger errorQps = new AtomicInteger(0);
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final TestCase testCase = new TestCase();
    private static final AtomicLong errorCount = new AtomicLong(0);
    private static final StringBuilder errorInfo = new StringBuilder(128);

    private static class TestCase {
        public String randomValue = ((Long) (System.nanoTime() % 100000)).toString();
        public GraphTraversal kvSearchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person");
        public GraphTraversal kkvSearchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").hasLabel("created");

        public GraphTraversal kvUpdateCase = g("shouya_test").addV("node").property("pkey", "2").property("sk", randomValue).property("value", randomValue);
        public GraphTraversal kkvUpdateCase = g("shouya_test").addE("edge").property("pkey", "2").property("skey", randomValue).property("value", randomValue);
        public GraphTraversal kvDeleteCase = g("shouya_test").V("2").hasLabel("node").drop();
        public GraphTraversal kkvDeleteCase = g("shouya_test").E("2:" + randomValue).hasLabel("edge").drop();

        public List<String> kvSearchResult = new ArrayList<String>() {{
            add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
            add("{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
            add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
            add("{\"label\":\"person\",\"age\":35,\"name\":\"peter\",\"pk\":\"6\"}");
        }};

        public JsonArray generateKvResultList(){
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(gnerateKvResult("person", "1", "marko", 29));
            jsonArray.add(gnerateKvResult("person", "4", "josh", 32));
            jsonArray.add(gnerateKvResult("person", "2", "vadas", 27));
            jsonArray.add(gnerateKvResult("person", "6", "peter", 35));
            return jsonArray;
        }
        private JsonObject gnerateKvResult(String label, String pk, String name, int age) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("label", label);
            jsonObject.addProperty("age", age);
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("pk", pk);
            return jsonObject;
        }

        private JsonArray generatePathResultList(List<String> keys){
            JsonArray jsonArray = new JsonArray();
            for (String key : keys){
                jsonArray.add(generatePathResult(key, new JsonArray()));
            }
            return jsonArray;
        }

        private JsonObject generatePathResult(String object, JsonArray label){
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("label", label);
            jsonObject.addProperty("object", object);
            return jsonObject;
        }

        public List<String> kkvSearchResult = new ArrayList<String>() {{
            add("{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
            add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
            add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"5\",\"weight\":1.0}");
            add("{\"label\":\"created\",\"pk\":\"6\",\"sk\":\"3\",\"weight\":0.2}");
        }};

        public JsonArray generateKkvResultList(){
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(gnerateKkvResult("created", "1", "3", 0.4));
            jsonArray.add(gnerateKkvResult("created", "4", "3", 0.4));
            jsonArray.add(gnerateKkvResult("created", "4", "5", 1.0));
            jsonArray.add(gnerateKkvResult("created", "6", "3", 0.2));
            return jsonArray;
        }
        private JsonObject gnerateKkvResult(String label, String pk,String sk, double weight) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("label", label);
            jsonObject.addProperty("weight", weight);
            jsonObject.addProperty("sk", sk);
            jsonObject.addProperty("pk", pk);
            return jsonObject;
        }


    }

    private static void analyze(Long rt, boolean hasError) {
        qps.incrementAndGet();
        allRt.addAndGet(rt);
        if (hasError) {
            errorQps.incrementAndGet();
        }
        if (System.currentTimeMillis() - time.get() > 1000) {
            lock.writeLock().lock();
            if (System.currentTimeMillis() - time.get() > 1000) {
                logger.error("qps is {}, error qps is {}, error ratio {}, average rt is {} ms",
                    qps.get(), errorQps.get(), (double) errorQps.get() / qps.get(), (double) allRt.get() / qps.get() / 1000000.0);
                qps.set(0);
                errorQps.set(0);
                allRt.set(0);
                time.set(System.currentTimeMillis());
            }
            lock.writeLock().unlock();
        }
    }

    private static void checkResultEqual(ResultSet result, List<String> expectResult) throws InterruptedException {
        try {
            List<Result> results = result.all().get();
            if (expectResult.size() != results.size()) {
                for (Result value : results) {
                    System.out.println(value.toString());
                }
                throw new Exception("expected size [" + expectResult.size() + "], actual size [" + results.size() + "]");
            } else {
                for (int j = 0; j < results.size(); ++j) {
                    Object object = results.get(j).getObject();
                    String objectResult = object.toString();
                    System.out.println(objectResult);
                    if (!(expectResult.get(j).equals(objectResult))) {
                        throw new Exception("expect: " + expectResult.get(j) + " ; actual: " + objectResult);
//                        Assert.assertEquals(expectResult.get(j), results.get(j).getObject().toString());
                    }
                }
            }
            System.out.println();
        } catch (Exception e) {
            logger.error("result is not equal with expected! " + e.getMessage());
            e.printStackTrace();
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
            Thread.sleep(50);
        }
    }

    private static void testAsyncSearch(Client client) {
        try {
            {
                logger.info("case:" + testCase.kvSearchCase.toString());
                ResultSet resultSet = client.submit(testCase.kvSearchCase);
                try {
                    checkResultEqual(resultSet, testCase.kvSearchResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            {
                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\")";
                logger.info("case(String):" + searchStr);
                ResultSet resultSet = client.submit(searchStr);
                try {
                    checkResultEqual(resultSet, testCase.kvSearchResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            {
                String searchStr = "g(\"tinkerpop_modern\").V($1).hasLabel(\"person\")";
                Map<String, Object> bindings = new LinkedHashMap<>();
                bindings.put("$1", "1;2;3;4;5;6");
                logger.info("case(String):" + searchStr + bindings);
                ResultSet resultSet = client.submit(searchStr, bindings);
                try {
                    checkResultEqual(resultSet, testCase.kvSearchResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            {
                logger.info("case:" + testCase.kkvSearchCase.toString());
                ResultSet resultSet = client.submit(testCase.kkvSearchCase);
                try {
                    checkResultEqual(resultSet, testCase.kkvSearchResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            {
                String searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"created\")";
                logger.info("case(String):" + searchStr);
                ResultSet resultSet = client.submit(searchStr);
                try {
                    checkResultEqual(resultSet, testCase.kkvSearchResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            {
                String searchStr = "g(\"tinkerpop_modern\").E($1).hasLabel(\"created\")";
                Map<String, Object> bindings = new LinkedHashMap<>();
                bindings.put("$1", "1:3;4:3|5;6");
                logger.info("case(String):" + searchStr + bindings);
                ResultSet resultSet = client.submit(searchStr, bindings);
                try {
                    checkResultEqual(resultSet, testCase.kkvSearchResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            {
                String searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"created\")";
                Map<String, Object> bindings = new LinkedHashMap<>();
                logger.info("case(String):" + searchStr + bindings);
                ResultSet resultSet = client.submit(searchStr, bindings);
                try {
                    checkResultEqual(resultSet, testCase.kkvSearchResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
            {
                logger.info("multi case:" + testCase.kvSearchCase.toString());
                IGraphMultiResultSet iGraphMultiResultSet = client.submit(testCase.kvSearchCase, testCase.kvSearchCase);
                List<ResultSet> resultSets = iGraphMultiResultSet.getAllQueryResult();
                for (ResultSet resultSet : resultSets) {
                    try {
                        checkResultEqual(resultSet, testCase.kvSearchResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("result is not equal with expected! " + e);
            e.printStackTrace();
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
        }

    }

    private static void testAsyncUpdate(Client client) {
        try {
            logger.info("randomValue: " + testCase.randomValue);
            {
                ResultSet resultSet = client.submit(testCase.kvUpdateCase);
                List<Result> results = resultSet.all().get();
                logger.info("update result size :" + results.size());
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                logger.info("update finished");
                Thread.sleep(5000);

                GraphTraversal searchCase = g("shouya_test").V("2").hasLabel("node");
                resultSet = client.submit(searchCase);
                results = resultSet.all().get();
                if (results.size() != 1) {
                    throw new Exception("expect result size [1], actual result size [" + results.size() + "]");
                }
                logger.info("search result size :" + results.size());
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                logger.info("search finished");

                resultSet = client.submit(testCase.kvDeleteCase);
                results = resultSet.all().get();
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                logger.info("delete finished");
                Thread.sleep(5000);

                resultSet = client.submit(searchCase);
                results = resultSet.all().get();
                if (results.size() != 0) {
                    throw new Exception("expect result size [0], actual result size [" + results.size() + "]");
                }
                logger.info("search result size :" + results.size());
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                logger.info("search finished");
            }
            {
                ResultSet resultSet = client.submit(testCase.kkvUpdateCase);
                List<Result> results = resultSet.all().get();
                logger.info("update result size :" + results.size());
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                logger.info("update finished");
                Thread.sleep(5000);
                GraphTraversal searchCase = g("shouya_test").E("2:" + testCase.randomValue).hasLabel("edge");
                resultSet = client.submit(searchCase);
                results = resultSet.all().get();
                if (results.size() != 1) {
                    throw new Exception("expect result size [1], actual result size [" + results.size() + "]");
                }
                logger.info("search result size :" + results.size());
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                logger.info("search finished");

                resultSet = client.submit(testCase.kkvDeleteCase);
                results = resultSet.all().get();
                logger.info("delete result size :" + results.size());
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                logger.info("delete finished");
                Thread.sleep(5000);
                resultSet = client.submit(searchCase);
                results = resultSet.all().get();
                if (results.size() != 0) {
                    throw new Exception("expect result size [0], actual result size [" + results.size() + "]");
                }
                logger.info("search result size :" + results.size());
                for (Result result : results) {
                    System.out.println(result.getObject());
                }
                System.out.println("search finished");
            }
        } catch (Exception e) {
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
            logger.error("search exception! " + e);
            e.printStackTrace();
        }
    }

    private static void testFunction(Client client) {
        Gson gson = new Gson();
        try {
            // test V() + hasLabel()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kvSearchResult);
                System.out.println(gson.toJson(testCase.generateKvResultList()));
                System.out.println(gson.toJson(resultSet.getJson()));
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKvResultList()),true);

                Map<String, Object> bindings = new LinkedHashMap<>();
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                searchStr = "g(\"tinkerpop_modern\").V($1).hasLabel(\"person\")";
                bindings.put("$1", "1;2;3;4;5;6");
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, testCase.kvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKvResultList()),true);

                // test Vertex
                try {
                    searchCase = g("tinkerpop_modern").V("1").hasLabel("person");
                    logger.info("case:" + searchCase.toString());
                    resultSet = client.submit(searchCase);
                    List<Result> resultList = resultSet.all().join();
                    Set<String> keySet = new HashSet<>(Arrays.asList("pk", "name", "age"));
                    Map<String, Object> valueMap = new HashMap<>();
                    valueMap.put("pk", "1");
                    valueMap.put("name", "marko");
                    valueMap.put("age", 29);
                    List<String> valueList = Arrays.asList("1", "marko");
                    List<String> keyList = Arrays.asList("pk", "name");
                    for (Result result : resultList) {
                        Vertex vertex = result.getVertex();
                        if (vertex == null) {
                            throw new Exception("expected vertex type [Vertex], actual vertex type [" + ((IGraphResult) result).getObjectType() + "]");
                        }
                        if (!vertex.label().equals("person")) {
                            throw new Exception("expected vertex label [person], actual vertex label [" + vertex.label() + "]");
                        }
                        VertexProperty<Integer> integerVertexProperty = vertex.property("age");
                        if (!integerVertexProperty.value().equals(29)) {
                            throw new Exception("expected vertex key:value [age:" + 29 + "], actual vertex key:value [age:" + integerVertexProperty.value() + "]");
                        }
                        Set<String> realKeys = vertex.keys();
                        if (!realKeys.equals(keySet)) {
                            throw new Exception("expected vertex keys [" + keySet + "], actual vertex keys [" + realKeys + "]");
                        }
                        for (String key : realKeys) {
                            if (!valueMap.get(key).equals(vertex.value(key))) {
                                throw new Exception("expected vertex key:value [" + key + ":" + valueMap.get(key) + "], actual vertex key:value [" + key + ":" + vertex.value(key) + "]");
                            }
                        }
                        Iterator<String> iterator = vertex.values("pk", "name");
                        int i = 0;
                        while (iterator.hasNext()) {
                            String value = iterator.next();
                            if (!value.equals(valueList.get(i++))) {
                                throw new Exception("expected vertex value [" + valueList.get(i - 1) + "], actual vertex value [" + value + "]");
                            }
                        }
                        i = 0;
                        Iterator<VertexProperty<String>> vertexPropertyIterator = vertex.properties("pk", "name");
                        while (vertexPropertyIterator.hasNext()) {
                            VertexProperty<String> vertexProperty = vertexPropertyIterator.next();
                            if (!vertexProperty.isPresent()) {
                                throw new Exception("VertexProperty value is empty!");
                            }
                            if (!vertexProperty.element().equals(vertex)) {
                                throw new Exception("VertexProperty element is not equal with origin");
                            }
                            if (!vertexProperty.key().equals(keyList.get(i))) {
                                throw new Exception("expected VertecProperty key [" + keyList.get(i) + "], actual key [" + vertexProperty.key() + "]");
                            }
                            if (!vertexProperty.value().equals(valueList.get(i++))) {
                                throw new Exception("expected VertecProperty value [" + valueList.get(i - 1) + "], actual value [" + vertexProperty.value() + "]");
                            }
                        }

                        //test Element
                        Element element = result.getElement();
                        if (element == null) {
                            throw new Exception("expected element type [Vertex], actual element type [" + ((IGraphResult) result).getObjectType() + "]");
                        }
                        if (!element.label().equals("person")) {
                            throw new Exception("expected element label [person], actual element label [" + vertex.label() + "]");
                        }
                        if (!element.keys().equals(keySet)) {
                            throw new Exception("expected element keys [" + keySet + "], actual element keys [" + realKeys + "]");
                        }
                        for (String key : realKeys) {
                            if (!valueMap.get(key).equals(element.value(key))) {
                                throw new Exception("expected element key:value [" + key + ":" + valueMap.get(key) + "], actual element key:value [" + key + ":" + vertex.value(key) + "]");
                            }
                        }
                        iterator = element.values("pk", "name");
                        i = 0;
                        while (iterator.hasNext()) {
                            String value = iterator.next();
                            if (!value.equals(valueList.get(i++))) {
                                throw new Exception("expected element value [" + valueList.get(i - 1) + "], actual element value [" + value + "]");
                            }
                        }

                    }
                } catch (Exception e) {
                    logger.error("result is not equal with expected! " + e.getMessage());
                    e.printStackTrace();
                    errorCount.incrementAndGet();
                    errorInfo.append("error information: ").append(e).append("\n");
                    Thread.sleep(50);
                }
            }
            // test E()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").hasLabel("created");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKkvResultList()),true);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"created\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKkvResultList()),true);

                searchStr = "g(\"tinkerpop_modern\").E($1).hasLabel(\"created\")";
                Map<String, Object> bindings = new LinkedHashMap<>();
                bindings.put("$1", "1:3;4:3|5;6");
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKkvResultList()),true);

                // test Edge
                try {
                    searchCase = g("tinkerpop_modern").E("1").hasLabel("created");
                    logger.info("case:" + searchCase.toString());
                    resultSet = client.submit(searchCase);
                    List<Result> resultList = resultSet.all().join();
                    Set<String> keySet = new HashSet<>(Arrays.asList("pk", "sk", "weight"));
                    Map<String, Object> valueMap = new HashMap<>();
                    valueMap.put("pk", "1");
                    valueMap.put("sk", "3");
                    valueMap.put("weight", 0.4);
                    List<String> valueList = Arrays.asList("1", "3");
                    List<String> keyList = Arrays.asList("pk", "sk");
                    for (Result result : resultList) {
                        Edge edge = result.getEdge();
                        if (edge == null) {
                            throw new Exception("expected type [Edge], actual type [" + ((IGraphResult) result).getObjectType() + "]");
                        }
                        if (!edge.label().equals("created")) {
                            throw new Exception("expected label [created], actual label [" + edge.label() + "]");
                        }
                        System.out.println(edge.value("weight").getClass());
                        Property<Double> doubleProperty = edge.property("weight");
                        if (!doubleProperty.value().equals(0.4)) {
                            throw new Exception("expected key:value [weight:" + 0.4 + "], actual key:value [weight:" + doubleProperty.value() + "]");
                        }
                        Set<String> realKeys = edge.keys();
                        if (!realKeys.equals(keySet)) {
                            throw new Exception("expected keys [" + keySet + "], actual keys [" + realKeys + "]");
                        }
                        for (String key : realKeys) {
                            if (!valueMap.get(key).equals(edge.value(key))) {
                                throw new Exception("expected key:value [" + key + ":" + valueMap.get(key) + "], actual key:value [" + key + ":" + edge.value(key) + "]");
                            }
                        }
                        Iterator<String> iterator = edge.values("pk", "sk");
                        int i = 0;
                        while (iterator.hasNext()) {
                            String value = iterator.next();
                            if (!value.equals(valueList.get(i++))) {
                                throw new Exception("expected value [" + valueList.get(i - 1) + "], actual value [" + value + "]");
                            }
                        }
                        i = 0;
                        Iterator<Property<String>> propertyIterator = edge.properties("pk", "sk");
                        while (propertyIterator.hasNext()) {
                            Property<String> property = propertyIterator.next();
                            if (!property.isPresent()) {
                                throw new Exception("Property value is empty!");
                            }
                            if (!property.element().equals(edge)) {
                                throw new Exception("Property element is not equal with origin");
                            }
                            if (!property.key().equals(keyList.get(i))) {
                                throw new Exception("expected Property key [" + keyList.get(i) + "], actual key [" + property.key() + "]");
                            }
                            if (!property.value().equals(valueList.get(i++))) {
                                throw new Exception("expected Property value [" + valueList.get(i - 1) + "], actual value [" + property.value() + "]");
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("result is not equal with expected! " + e.getMessage());
                    e.printStackTrace();
                    errorCount.incrementAndGet();
                    errorInfo.append("error information: ").append(e).append("\n");
                    Thread.sleep(50);
                }
            }
            // directTableAccessMode
            {
                GraphTraversalSource g = new GraphTraversalSource();
                GraphTraversal searchCase = g.V("1;2;3;4;5;6").by("thinkerpop_modern_person");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"thinkerpop_modern_person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                    add("{\"label\":\"thinkerpop_modern_person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
                    add("{\"label\":\"thinkerpop_modern_person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                    add("{\"label\":\"thinkerpop_modern_person\",\"age\":35,\"name\":\"peter\",\"pk\":\"6\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g.V(\"1;2;3;4;5;6\").by(\"thinkerpop_modern_person\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g.V("1;2;3;4;5;6").by("thinkerpop_modern_person").outE().by("thinkerpop_modern_created").inV().by("thinkerpop_modern_software");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"thinkerpop_modern_software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                    add("{\"label\":\"thinkerpop_modern_software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                    add("{\"label\":\"thinkerpop_modern_software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                    add("{\"label\":\"thinkerpop_modern_software\",\"lang\":\"java\",\"name\":\"ripple\",\"pk\":\"5\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                searchStr = "g.V(\"1;2;3;4;5;6\").by(\"thinkerpop_modern_person\").outE().by(\"thinkerpop_modern_created\").inV().by(\"thinkerpop_modern_software\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // test E() + has() + P + T
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").has(T.label, P.eq("created"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKkvResultList()),true);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").has(T.label, P.eq(\"created\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKkvResultList()),true);


                searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").has(T.label, P.eq("created")).has("weight", P.inside(0.19, 1.01));
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKkvResultList()),true);


                searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").has(T.label, P.eq(\"created\")).has(\"weight\", P.inside(0.19, 1.01))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(testCase.generateKkvResultList()),true);

                searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").has(T.label, P.eq("created")).has("weight", 0.4);
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                }};
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").has(T.label, P.eq(\"created\")).has(\"weight\", 0.4)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").has(T.label, P.eq("person")).has("age", is(P.eq(29)));
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").has(T.label, P.eq(\"person\")).has(\"age\", is(P.eq(29)))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // and() + count()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person")
                    .and(has("age", P.gt(28)), outE("knows").count().is(P.gte(2)));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\")" +
                    ".and(has(\"age\",P.gt(28)), outE(\"knows\").count().is(P.gte(2)))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // alias()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").hasLabel("person").alias("name:nick;age*2:double_age");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":29,\"double_age\":58,\"nick\":\"marko\",\"pk\":\"1\"}");
                    add("{\"label\":\"person\",\"age\":32,\"double_age\":64,\"nick\":\"josh\",\"pk\":\"4\"}");
                    add("{\"label\":\"person\",\"age\":35,\"double_age\":70,\"nick\":\"peter\",\"pk\":\"6\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"person\").alias(\"name:nick;age*2:double_age\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // barrier() + bulk() + dedup()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1;2;3;4;5;6").hasLabel("created").inV().barrier();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                    add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                    add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                    add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"ripple\",\"pk\":\"5\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1;2;3;4;5;6\").hasLabel(\"created\").inV().barrier()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").E("1;2;3;4;5;6").hasLabel("created").inV().barrier(Barrier.nodedup).bulk();
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("1"); searchResult.add("1"); searchResult.add("1"); searchResult.add("1");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").E(\"1;2;3;4;5;6\").hasLabel(\"created\").inV().barrier(Barrier.nodedup).bulk()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").E("1;2;3;4;5;6").hasLabel("created").inV().barrier().dedup().bulk();
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("1"); searchResult.add("1");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").E(\"1;2;3;4;5;6\").hasLabel(\"created\").inV().barrier().dedup().bulk()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // branch() + option()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").branch(values("name"))
                    .option("marko", values("age")).option(Pick.none, values("name"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("29");
                    add("josh");
                    add("vadas");
                    add("peter");
                }};
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(29);
                jsonArray.add("josh");
                jsonArray.add("vadas");
                jsonArray.add("peter");
                System.out.println(gson.toJson(jsonArray));
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").branch(values(\"name\"))" +
                    ".option(\"marko\",values(\"age\")).option(none,values(\"name\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

            }
            // choose()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person")
                    .choose(has("name", "marko"), values("age"), values("name"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("29");
                    add("josh");
                    add("vadas");
                    add("peter");
                }};
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(29);
                jsonArray.add("josh");
                jsonArray.add("vadas");
                jsonArray.add("peter");
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\")" +
                    ".choose(has(\"name\",\"marko\"),values(\"age\"), values(\"name\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);
            }
            // coalesce()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1").hasLabel("person")
                    .coalesce(outE("knows"), outE("created"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"knows\",\"pk\":\"1\",\"sk\":\"4\",\"weight\":1.0}");
                    add("{\"label\":\"knows\",\"pk\":\"1\",\"sk\":\"2\",\"weight\":0.5}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\")" +
                    ".coalesce(outE(\"knows\"),outE(\"created\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // constant()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person")
                    .choose(filter("age<30"), constant("young man"), values("age"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("young man");
                    add("32");
                    add("young man");
                    add("35");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\")" +
                    ".choose(filter(\"age<30\"),constant(\"young man\"),values(\"age\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // cyclicPath() + out() + path() + simplePath()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1").hasLabel("person").out().out().cyclicPath().path();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<>();
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").out().out().cyclicPath().path()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").out().out().path().by("name");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("[{\"object\":\"marko\",\"label\":[]},{\"object\":\"josh\",\"label\":[]},{\"object\":\"lop\",\"label\":[]}]");
                searchResult.add("[{\"object\":\"marko\",\"label\":[]},{\"object\":\"josh\",\"label\":[]},{\"object\":\"ripple\",\"label\":[]}]");
                JsonArray jsonArray = new JsonArray();
                JsonArray jsonArray1 = testCase.generatePathResultList(Arrays.asList("marko","josh","lop"));
                JsonArray jsonArray2 = testCase.generatePathResultList(Arrays.asList("marko","josh","ripple"));
                jsonArray.add(jsonArray1);
                jsonArray.add(jsonArray2);
                checkResultEqual(resultSet, searchResult);
                System.out.println(gson.toJson(resultSet.getJson()));
                System.out.println(gson.toJson(jsonArray));
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").out().out().path().by(\"name\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1").hasLabel("person").out().out().simplePath().path().by("name");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("[{\"object\":\"marko\",\"label\":[]},{\"object\":\"josh\",\"label\":[]},{\"object\":\"lop\",\"label\":[]}]");
                searchResult.add("[{\"object\":\"marko\",\"label\":[]},{\"object\":\"josh\",\"label\":[]},{\"object\":\"ripple\",\"label\":[]}]");
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);


                searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").out().out().simplePath().path().by(\"name\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1").hasLabel("person").out().out().simplePath().path().by(T.id);
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("[{\"object\":\"1\",\"label\":[]},{\"object\":\"4\",\"label\":[]},{\"object\":\"3\",\"label\":[]}]");
                searchResult.add("[{\"object\":\"1\",\"label\":[]},{\"object\":\"4\",\"label\":[]},{\"object\":\"5\",\"label\":[]}]");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").out().out().simplePath().path().by(T.id)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                // test Path
                try {
                    searchCase = g("tinkerpop_modern").V("1").hasLabel("person").outE("created").path();
                    logger.info("case:" + searchCase.toString());
                    resultSet = client.submit(searchCase);
                    List<Result> resultList = resultSet.all().join();
                    List<String> valueList = Arrays.asList("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}",
                        "{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                    for (Result result : resultList) {
                        Path path = result.getPath();
                        if (path == null) {
                            throw new Exception("expected type [Path], actual type [" + ((IGraphResult) result).getObjectType() + "]");
                        }
                        if (path.isEmpty() || path.size() != 2) {
                            throw new Exception("expected size [2], actual size [" + path.size() + "]");
                        }
                        if (!path.isSimple()) {
                            throw new Exception("path is not simple!");
                        }
                        Vertex vertex = path.head();
                        if (!vertex.toString().equals(valueList.get(0))) {
                            throw new Exception("expected path head [" + valueList.get(0) + "], actual path head [" + vertex + "]");
                        }
                        Edge edge = path.get(1);
                        if (!edge.toString().equals(valueList.get(1))) {
                            throw new Exception("expected path value[1] [" + valueList.get(1) + "], actual path value[1] [" + edge + "]");
                        }
                        List<Object> objectList = path.objects();
                        if (!objectList.toString().equals(valueList.toString())) {
                            throw new Exception("expected objectList [" + valueList + "], actual objectList [" + objectList + "]");
                        }
                        Path clonePath = path.clone();
                        if (!clonePath.equals(path)) {
                            throw new Exception("clone path is not equal with origin path!");
                        }
                        List<Set<String>> labels = path.labels();
                        if (labels.size() != valueList.size()) {
                            throw new Exception("expected label size [" + valueList.size() + "], actual label size [" + labels.size() + "]");
                        }
                        Iterator<Object> iterator = path.iterator();
                        final int[] i = {0};
                        while (iterator.hasNext()) {
                            Object object = iterator.next();
                            if (!object.toString().equals(valueList.get(i[0]++))) {
                                throw new Exception("expected iterator value [" + valueList.get(i[0] - 1) + "], actual iterator value [" + object.toString() + "], i = " + (i[0] - 1));
                            }
                        }
                        i[0] = 0;
                        path.forEach(new BiConsumer<Object, Set<String>>() {
                            @SneakyThrows
                            @Override
                            public void accept(Object o, Set<String> strings) {
                                if (!o.toString().equals(valueList.get(i[0]++))) {
                                    throw new Exception("expected Consumer object [" + valueList.get(i[0] - 1) + "], actual iterator value [" + o.toString() + "], i = " + (i[0] - 1));
                                }
                                if (strings.size() != 0) {
                                    throw new Exception("Set<String> is not empty!");
                                }
                            }
                        });
                        Stream<Pair<Object, Set<String>>> stream = path.stream();
                        i[0] = 0;
                        stream.forEach(new Consumer<Pair<Object, Set<String>>>() {
                            @SneakyThrows
                            @Override
                            public void accept(Pair<Object, Set<String>> objects) {
                                if (!objects.getValue0().toString().equals(valueList.get(i[0]++))) {
                                    throw new Exception("expected Consumer object [" + valueList.get(i[0] - 1) + "], actual iterator value [" + objects.getValue0().toString() + "], i = " + (i[0] - 1));
                                }
                                if (objects.getValue1().size() != 0) {
                                    throw new Exception("Set<String> is not empty!");
                                }
                            }
                        });
                        if (!path.popEquals(Pop.mixed, clonePath)) {
                            throw new Exception("clonePath is not popEquals with path, pop [" + Pop.mixed + "]");
                        }
                        Path subPath = path.subPath(null, null);
                        if (!path.equals(subPath)) {
                            throw new Exception("expected subPath [" + path + "], actual subPath [" + subPath + "]");
                        }
                    }
                } catch (Exception e) {
                    logger.error("result is not equal with expected! " + e.getMessage());
                    e.printStackTrace();
                    errorCount.incrementAndGet();
                    errorInfo.append("error information: ").append(e).append("\n");
                    Thread.sleep(50);
                }
            }
            // distinct()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1;2;3;4;5;6").hasLabel("created").distinct().by("pk");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"created\",\"pk\":\"6\",\"sk\":\"3\",\"weight\":0.2}");
                    add("{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1;2;3;4;5;6\").hasLabel(\"created\").distinct().by(\"pk\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").E("1;2;3;4;5;6").hasLabel("created").distinct(Distinct.round, 2, Distinct.amount, 1).by("pk");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"label\":\"created\",\"pk\":\"6\",\"sk\":\"3\",\"weight\":0.2}");
                searchResult.add("{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                searchResult.add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                searchResult.add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"5\",\"weight\":1.0}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").E(\"1;2;3;4;5;6\").hasLabel(\"created\").distinct(Distinct.round,2,Distinct.amount,1).by(\"pk\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").distinct(Distinct.isReserved).by(__.outE("created").count());
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                searchResult.add("{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
                searchResult.add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                searchResult.add("{\"label\":\"person\",\"age\":35,\"name\":\"peter\",\"pk\":\"6\"}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").distinct(Distinct.isReserved).by(__.outE(\"created\").count())";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // fields()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").hasLabel("created").fields("pk;sk");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\"}");
                    add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"3\"}");
                    add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"5\"}");
                    add("{\"label\":\"created\",\"pk\":\"6\",\"sk\":\"3\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"created\").fields(\"pk;sk\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // filter()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").hasLabel("created").filter("weight<=1.0 AND weight>=0.2");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kkvSearchResult);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"created\").filter(\"weight<=1.0 AND weight>=0.2\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kkvSearchResult);

                searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"created\").filter($1)";
                Map<String, Object> bindings = new LinkedHashMap<>();
                bindings.put("$1", "weight<=1.0 AND weight>=0.2");
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, testCase.kkvSearchResult);

                searchCase = g("tinkerpop_modern").E("1:3;4:3|5;6").hasLabel("created").filter(__.has(T.label, P.eq("created")));
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kkvSearchResult);

                searchStr = "g(\"tinkerpop_modern\").E(\"1:3;4:3|5;6\").hasLabel(\"created\").filter(__.has(T.label, P.eq(\"created\")))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kkvSearchResult);
            }
            // group()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1;2;3;4;5;6").hasLabel("person").group().by(outE("knows").count()).by("name");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{0=[\"josh\",\"vadas\",\"peter\"], 2=[\"marko\"]}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1;2;3;4;5;6\").hasLabel(\"person\").group().by(outE(\"knows\").count()).by(\"name\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // groupCount()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").E("1;2;3;4;5;6").hasLabel("created").groupCount("x").by("pk")
                    .groupCount("y").by("weight").cap("x", "y");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{x={\"1\":1,\"4\":2,\"6\":1}, y={\"0.2\":1,\"0.4\":2,\"1.0\":1}}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").E(\"1;2;3;4;5;6\").hasLabel(\"created\").groupCount(\"x\").by(\"pk\")" +
                    ".groupCount(\"y\").by(\"weight\").cap(\"x\",\"y\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // hasKey()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").properties().hasKey(P.within("location", "name"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"name\":\"marko\"}");
                    add("{\"name\":\"josh\"}");
                    add("{\"name\":\"vadas\"}");
                    add("{\"name\":\"peter\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").properties().hasKey(P.within(\"location\",\"name\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").properties().hasKey("name");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").properties().hasKey(\"name\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // hasValue()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").properties().hasValue(P.gt(30));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"age\":32}");
                    add("{\"age\":35}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").properties().hasValue(P.gt(30))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // identity() + union() + outE() + inV()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").identity();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").identity()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                searchCase = g("tinkerpop_modern").V("1").hasLabel("person").union(identity(), outE("knows").inV());
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                    add("{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").union(identity(),outE(\"knows\").inV())";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // indexQuery()
            {
                String invert_query = "{\\\"match\\\":{\\\"lang\\\":\\\"java\\\"}}";
                GraphTraversal searchCase = g("shouya_test").V().hasLabel("software").indexQuery(invert_query);
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"ripple\",\"pk\":\"5\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"shouya_test\").V().hasLabel(\"software\").indexQuery(\"{\\\"match\\\":{\\\"lang\\\":\\\"java\\\"}}\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // values() + is() + where()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").values("name").is("marko");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("marko");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").values(\"name\").is(\"marko\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").where(values("age").is(P.inside(28, 34)));
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                searchResult.add("{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").where(values(\"age\").is(P.inside(28,34)))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // join()
            {
                //left join
                GraphTraversalSource g = new GraphTraversalSource();
                GraphTraversal searchCase = g.V("1;2;3;4;5;6").by("thinkerpop_modern_person").alias("pk:pkey")
                    .join(Join.left, "join_tag:1|2").by("pkey", "thinkerpop_modern_created", filter("weight>0.2")
                    .order().by("weight", Order.decr).range(0, 10)).order().by("weight", Order.decr);
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":32,\"join_tag\":1,\"name\":\"josh\",\"pkey\":\"4\",\"sk\":\"5\",\"weight\":1.0}");
                    add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":29,\"join_tag\":1,\"name\":\"marko\",\"pkey\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":32,\"join_tag\":1,\"name\":\"josh\",\"pkey\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":27,\"join_tag\":2,\"name\":\"vadas\",\"pkey\":\"2\",\"sk\":\"\",\"weight\":0.0}");
                    add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":35,\"join_tag\":2,\"name\":\"peter\",\"pkey\":\"6\",\"sk\":\"\",\"weight\":0.0}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g.V(\"1;2;3;4;5;6\").by(\"thinkerpop_modern_person\").alias(\"pk:pkey\")" +
                    ".join(Join.left,\"join_tag:1|2\").by(\"pkey\",\"thinkerpop_modern_created\",filter(\"weight>0.2\")" +
                    ".order().by(\"weight\",Order.decr).range(0,10)).order().by(\"weight\",Order.decr)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                // inner join
                searchCase = g.V("1;2;3;4;5;6").by("thinkerpop_modern_person").alias("pk:pkey").join()
                    .by("pkey", "thinkerpop_modern_created", filter("weight>0.2").order()
                    .by("weight", Order.decr).range(0, 10)).order().by("weight", Order.decr);
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":32,\"name\":\"josh\",\"pkey\":\"4\",\"sk\":\"5\",\"weight\":1.0}");
                searchResult.add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":29,\"name\":\"marko\",\"pkey\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                searchResult.add("{\"label\":\"0#thinkerpop_modern_person$thinkerpop_modern_created\",\"age\":32,\"name\":\"josh\",\"pkey\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g.V(\"1;2;3;4;5;6\").by(\"thinkerpop_modern_person\").alias(\"pk:pkey\").join()" +
                    ".by(\"pkey\",\"thinkerpop_modern_created\",filter(\"weight>0.2\").order()" +
                    ".by(\"weight\",Order.decr).range(0,10)).order().by(\"weight\",Order.decr)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g.V("1;2;3;4;5;6").by("thinkerpop_modern_person").alias("pk:pkey").join()
                    .by("pkey", "thinkerpop_modern_knows", filter("weight>0.2").order()
                    .by("weight", Order.decr).range(0, 10)).order().by("weight", Order.decr).outE().by("thinkerpop_modern_created", "sk");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"label\":\"thinkerpop_modern_created\",\"pk\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                searchResult.add("{\"label\":\"thinkerpop_modern_created\",\"pk\":\"4\",\"sk\":\"5\",\"weight\":1.0}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g.V(\"1;2;3;4;5;6\").by(\"thinkerpop_modern_person\").alias(\"pk:pkey\").join()" +
                    ".by(\"pkey\",\"thinkerpop_modern_knows\",filter(\"weight>0.2\").order()" +
                    ".by(\"weight\",Order.decr).range(0,10)).order().by(\"weight\",Order.decr).outE().by(\"thinkerpop_modern_created\",\"sk\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // label()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1").hasLabel("person").outE().label();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("created");
                    add("knows");
                    add("knows");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").outE().label()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // limit()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").limit(2);
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                    add("{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").limit(2)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // local()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").local(outE("created").limit(1));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"created\",\"pk\":\"6\",\"sk\":\"3\",\"weight\":0.2}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").local(outE(\"created\").limit(1))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // emit() + loops() + repeat() + or()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person")
                    .emit(or(__.has("name", "marko"), loops().is(P.eq(2)))).repeat(__.out()).values("name");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("marko");
                    add("lop");
                    add("ripple");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\")" +
                    ".emit(or(__.has(\"name\",\"marko\"),loops().is(P.eq(2)))).repeat(__.out()).values(\"name\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;4;6").hasLabel("person").or(outE("knows")
                    .count().is(P.gte(2)), filter("age=27"));
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;4;6\").hasLabel(\"person\").or(outE(\"knows\")" +
                    ".count().is(P.gte(2)),filter(\"age=27\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // math() + aggregate() + as() + select() + unfold()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1").hasLabel("person").values("age").math("_ * _");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("841");
                }};
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(841);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").values(\"age\").math(\"_ * _\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1").hasLabel("person").aggregate("x")
                    .outE("knows").inV().values("age").as("y").local(select("x").unfold().values("age").math("_ + y"));
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("61");
                searchResult.add("56");
                jsonArray = new JsonArray(2);
                jsonArray.add(61);
                jsonArray.add(56);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

                searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").aggregate(\"x\")\n" +
                    ".outE(\"knows\").inV().values(\"age\").as(\"y\").local(select(\"x\").unfold().values(\"age\").math(\"_ + y\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchResult = new ArrayList<String>() {{
                    add("{{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}=1}");
                }};
                searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").aggregate(\"x\").cap(\"x\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
                System.out.println(resultSet.getJson());
                jsonArray = new JsonArray();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(gson.toJson(testCase.gnerateKvResult("person","1","marko", 29)),1);
                jsonArray.add(jsonObject);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);
            }
            // max() + mean() + min() + fold() + cap()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").aggregate("x").by("age").cap("x").max(Scope.local);
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("35");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").aggregate(\"x\").by(\"age\").cap(\"x\").max(Scope.local)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").values("age").mean();
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("30.75");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").values(\"age\").mean()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").values("age").fold().min(Scope.local);
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("27");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").values(\"age\").fold().min(Scope.local)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // not()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").not(outE("knows"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                    add("{\"label\":\"person\",\"age\":35,\"name\":\"peter\",\"pk\":\"6\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").not(outE(\"knows\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // optional()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").optional(outE("knows"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"knows\",\"pk\":\"1\",\"sk\":\"4\",\"weight\":1.0}");
                    add("{\"label\":\"knows\",\"pk\":\"1\",\"sk\":\"2\",\"weight\":0.5}");
                    add("{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"}");
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                    add("{\"label\":\"person\",\"age\":35,\"name\":\"peter\",\"pk\":\"6\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").optional(outE(\"knows\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // order() + range()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").order().by(outE("knows").count(), Order.decr);
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").order().by(outE(\"knows\").count(),Order.decr)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").values("age").order().by(Order.shuffle);
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                List<Result> resultList = resultSet.all().get();
                if (resultList.size() != 4) {
                    throw new Exception("expected size [15], actual size [" + resultList.size() + "]");
                }

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").values(\"age\").order().by(Order.shuffle)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                resultList = resultSet.all().get();
                if (resultList.size() != 4) {
                    throw new Exception("expected size [15], actual size [" + resultList.size() + "]");
                }

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").order().by("pk").range(1, 2);
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                }};
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(testCase.gnerateKvResult("person","2", "vadas", 27));
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").order().by(\"pk\").range(1,2)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

                searchStr = "g(\"tinkerpop_modern\").V($1).hasLabel(\"person\").order().by(\"pk\").range(1,$2)";
                Map<String, Object> bindings = new LinkedHashMap<>();
                bindings.put("$1", "1;2;3;4;5;6");
                bindings.put("$2", 2);
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

                searchStr = "g(\"tinkerpop_modern\").V($1).hasLabel(\"person\").order().by(\"pk\").range($2,$3)";
                bindings = new LinkedHashMap<>();
                bindings.put("$3", 2);
                bindings.put("$1", "1;2;3;4;5;6");
                bindings.put("$2", 1);
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);


                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").order().by("pk").range(Scope.global, 1, 2);
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                }};
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);


                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").order().by(\"pk\").range(Scope.global,1,2)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);


                searchStr = "g(\"tinkerpop_modern\").V($1).hasLabel(\"person\").order().by(\"pk\").range(Scope.global,1,$2)";
                bindings = new LinkedHashMap<>();
                bindings.put("$1", "1;2;3;4;5;6");
                bindings.put("$2", 2);
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);


                searchStr = "g(\"tinkerpop_modern\").V($1).hasLabel(\"person\").order().by(\"pk\").range(Scope.global,$2,$3)";
                bindings = new LinkedHashMap<>();
                bindings.put("$1", "1;2;3;4;5;6");
                bindings.put("$2", 1);
                bindings.put("$3", 2);
                logger.info("case(String):" + searchStr + bindings);
                resultSet = client.submit(searchStr, bindings);
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);
            }
            // withStrategies() + PathRecordStrategy() + PushDownStrategy()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").withStrategies(PathRecordStrategy(PathRecord.vertex)).V("4")
                    .hasLabel("person").outE().inV().path();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("[{\"object\":{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"},\"label\":[]}," +
                        "{\"object\":{\"label\":\"software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"},\"label\":[]}]");
                    add("[{\"object\":{\"label\":\"person\",\"age\":32,\"name\":\"josh\",\"pk\":\"4\"},\"label\":[]}," +
                        "{\"object\":{\"label\":\"software\",\"lang\":\"java\",\"name\":\"ripple\",\"pk\":\"5\"},\"label\":[]}]");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").withStrategies(PathRecordStrategy(PathRecord.vertex)).V(\"4\")" +
                    ".hasLabel(\"person\").outE().inV().path()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").withStrategies(PushDownStrategy("x")).V("1;2;3;4;5;6").hasLabel("person")
                    .aggregate("x").by("age").V("1;2;3;4;5;6").hasLabel("person").values("age").as("y")
                    .local(select("x").unfold().math("_ * y").max());
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("1015");
                searchResult.add("1120");
                searchResult.add("945");
                searchResult.add("1225");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").withStrategies(PushDownStrategy(\"x\")).V(\"1;2;3;4;5;6\").hasLabel(\"person\")\n" +
                    ".aggregate(\"x\").by(\"age\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").values(\"age\").as(\"y\")\n" +
                    ".local(select(\"x\").unfold().math(\"_ * y\").max())";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // project()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").project("a", "b")
                    .by(outE("knows").count()).by("age").order().by(select("b"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{a=0, b=27}");
                    add("{a=2, b=29}");
                    add("{a=0, b=32}");
                    add("{a=0, b=35}");
                }};
                JsonArray jsonArray = new JsonArray();
                for (String searchResultStr : searchResult) {
                    jsonArray.add(new JsonParser().parse(searchResultStr));
                }
                List<Result> resultList = resultSet.all().get();
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").project(\"a\",\"b\")\n" +
                    ".by(outE(\"knows\").count()).by(\"age\").order().by(select(\"b\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // properties() + Property
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2").hasLabel("person").properties();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"age\":29}");
                    add("{\"name\":\"marko\"}");
                    add("{\"pk\":\"1\"}");
                    add("{\"age\":27}");
                    add("{\"name\":\"vadas\"}");
                    add("{\"pk\":\"2\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2\").hasLabel(\"person\").properties()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2").hasLabel("person").properties("name", "age");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"age\":29}");
                searchResult.add("{\"name\":\"marko\"}");
                searchResult.add("{\"age\":27}");
                searchResult.add("{\"name\":\"vadas\"}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2\").hasLabel(\"person\").properties(\"name\", \"age\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                // test Property
                try {
                    searchCase = g("tinkerpop_modern").V("1").hasLabel("person").properties("name");
                    logger.info("case:" + searchCase.toString());
                    resultSet = client.submit(searchCase);
                    List<Result> resultList = resultSet.all().join();
                    for (Result result : resultList) {
                        Property<String> property = result.getProperty();
                        if (property == null) {
                            throw new Exception("expected type [Property], actual type [" + ((IGraphResult) result).getObjectType() + "]");
                        }
                        if (!property.isPresent()) {
                            throw new Exception("Property's value is empty!");
                        }
                        if (!property.key().equals("name") || !property.value().equals("marko")) {
                            throw new Exception("expected key:value [name:marko], actual is [" + property.key() + ":" + property.value() + "]");
                        }
                        property.ifPresent(value -> Assert.assertEquals("marko", value));
                        property.ifPresent(new Consumer<String>() {
                            @SneakyThrows
                            @Override
                            public void accept(String s) {
                                if (!s.equals("marko")) {
                                    throw new Exception("expected value [marko], actual value [" + s + "]");
                                }
                            }
                        });
                        String realValue = property.orElse("marko2");
                        if (!realValue.equals("marko")) {
                            throw new Exception("expected value [marko], actual value [" + realValue + "]");
                        }
                        realValue = property.orElseGet(String::new);
                        if (!realValue.equals("marko")) {
                            throw new Exception("expected value [marko], actual value [" + realValue + "]");
                        }
                        realValue = property.orElseThrow(() -> new Exception("empty value"));
                        if (!realValue.equals("marko")) {
                            throw new Exception("expected value [marko], actual value [" + realValue + "]");
                        }
                    }
                } catch (Exception e) {
                    logger.error("result is not equal with expected! " + e.getMessage());
                    e.printStackTrace();
                    errorCount.incrementAndGet();
                    errorInfo.append("error information: ").append(e).append("\n");
                    Thread.sleep(50);
                }
            }
            // propertyMap()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2").hasLabel("person").propertyMap();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{age={\"age\":29}, name={\"name\":\"marko\"}, pk={\"pk\":\"1\"}}");
                    add("{age={\"age\":27}, name={\"name\":\"vadas\"}, pk={\"pk\":\"2\"}}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2\").hasLabel(\"person\").propertyMap()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2").hasLabel("person").propertyMap("name", "age");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{age={\"age\":29}, name={\"name\":\"marko\"}}");
                searchResult.add("{age={\"age\":27}, name={\"name\":\"vadas\"}}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2\").hasLabel(\"person\").propertyMap(\"name\", \"age\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // range()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").order().by("pk").range(1, 2);
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                }};
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(testCase.gnerateKvResult("person","2","vadas",27));
                checkResultEqual(resultSet, searchResult);
                JSONAssert.assertEquals(gson.toJson(resultSet.getJson()), gson.toJson(jsonArray), true);


                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").order().by(\"pk\").range(1,2)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // repeat() + times() + until()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1").hasLabel("person").repeat(out()).times(2).path().by("name");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("[{\"object\":\"marko\",\"label\":[]},{\"object\":\"josh\",\"label\":[]},{\"object\":\"lop\",\"label\":[]}]");
                    add("[{\"object\":\"marko\",\"label\":[]},{\"object\":\"josh\",\"label\":[]},{\"object\":\"ripple\",\"label\":[]}]");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1\").hasLabel(\"person\").repeat(out()).times(2).path().by(\"name\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person")
                    .until(has("name", "ripple")).repeat(out()).path().by("name");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("[{\"object\":\"marko\",\"label\":[]},{\"object\":\"josh\",\"label\":[]},{\"object\":\"ripple\",\"label\":[]}]");
                searchResult.add("[{\"object\":\"josh\",\"label\":[]},{\"object\":\"ripple\",\"label\":[]}]");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\")" +
                    ".until(has(\"name\",\"ripple\")).repeat(out()).path().by(\"name\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // withSack() + sack()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").withSack(supplier(NORMAL, FLOAT, "1.0"))
                    .E("1;2;3;4;5;6").hasLabel("created").inV().barrier().sack();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("1.0");
                    add("1.0");
                    add("1.0");
                    add("1.0");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").withSack(supplier(NORMAL, FLOAT, \"1.0\"))" +
                    ".E(\"1;2;3;4;5;6\").hasLabel(\"created\").inV().barrier().sack()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").withSack(supplier(NORMAL, FLOAT, "1.0"), Splitter.identity, Operator.sum)
                    .E("1;2;3;4;5;6").hasLabel("created").inV().barrier().withSack();
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                searchResult.add("3.0");
                searchResult.add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                searchResult.add("3.0");
                searchResult.add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"lop\",\"pk\":\"3\"}");
                searchResult.add("3.0");
                searchResult.add("{\"label\":\"software\",\"lang\":\"java\",\"name\":\"ripple\",\"pk\":\"5\"}");
                searchResult.add("1.0");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").withSack(supplier(NORMAL, FLOAT, \"1.0\"), Splitter.identity, Operator.sum)" +
                    ".E(\"1;2;3;4;5;6\").hasLabel(\"created\").inV().barrier().withSack()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").withSack(supplier(NORMAL, FLOAT, "1.0"), Splitter.identity, Operator.sum)
                    .E("1;2;3;4;5;6").hasLabel("created").sack(Operator.assign).by("weight*__SACK__").sack();
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("0.4");
                searchResult.add("0.4");
                searchResult.add("1.0");
                searchResult.add("0.2");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").withSack(supplier(NORMAL, FLOAT, \"1.0\"), Splitter.identity, Operator.sum)" +
                    ".E(\"1;2;3;4;5;6\").hasLabel(\"created\").sack(Operator.assign).by(\"weight*__SACK__\").sack()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // sideEffect()
            {
                GraphTraversal searchCase = g("tinkerpop_modern")
                    .withSack(supplier(Supplier.SupplierType.NORMAL, "{\\\"k1\\\":[\\\"abc\\\"], \\\"k2\\\": 1}"), Splitter.fastclone, "{\\\"k1\\\": \\\"Operator.addall\\\", \\\"k2\\\": \\\"Operator.sum\\\"}")
                    .V("1", "2", "3", "4", "5", "6").hasLabel("person").sideEffect(__.sack("k2", Operator.sum).by(" to_int(age) ")).sack().get("k2");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("30");
                    add("33");
                    add("28");
                    add("36");
                }};
                checkResultEqual(resultSet, searchResult);

            }
            // sample()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").outE("created")
                    .sample(Sample.upsampling, Sample.duplicatable, 15).by("weight");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<Result> resultList = resultSet.all().get();
                if (resultList.size() != 15) {
                    throw new Exception("expected size [15], actual size [" + resultList.size() + "]");
                }

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").outE(\"created\")" +
                    ".sample(Sample.upsampling, Sample.duplicatable, 15).by(\"weight\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                resultList = resultSet.all().get();
                if (resultList.size() != 15) {
                    throw new Exception("expected size [15], actual size [" + resultList.size() + "]");
                }
            }
            // select()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").as("x").select("x");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").as(\"x\").select(\"x\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, testCase.kvSearchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").as("x", "y", "z")
                    .select("x", "y", "z").by("pk").by("name").by("age");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{x=1, y=marko, z=29}");
                    add("{x=4, y=josh, z=32}");
                    add("{x=2, y=vadas, z=27}");
                    add("{x=6, y=peter, z=35}");
                }};
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").as(\"x\",\"y\",\"z\")" +
                    ".select(\"x\",\"y\",\"z\").by(\"pk\").by(\"name\").by(\"age\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);

                searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").as("a")
                    .repeat(out().as("a")).times(2).select(Pop.first, "a");
                logger.info("case:" + searchCase.toString());
                resultSet = client.submit(searchCase);
                searchResult.clear();
                searchResult.add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                searchResult.add("{\"label\":\"person\",\"age\":29,\"name\":\"marko\",\"pk\":\"1\"}");
                checkResultEqual(resultSet, searchResult);

                searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").as(\"a\")" +
                    ".repeat(out().as(\"a\")).times(2).select(Pop.first,\"a\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // store()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").store("x").by("name").cap("x");
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{josh=1, marko=1, peter=1, vadas=1}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").store(\"x\").by(\"name\").cap(\"x\")";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // sum()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").values("age").sum();
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("123");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").values(\"age\").sum()";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // tail()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").tail(2);
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"person\",\"age\":27,\"name\":\"vadas\",\"pk\":\"2\"}");
                    add("{\"label\":\"person\",\"age\":35,\"name\":\"peter\",\"pk\":\"6\"}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").tail(2)";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
            // union()
            {
                GraphTraversal searchCase = g("tinkerpop_modern").V("1;2;3;4;5;6").hasLabel("person").union(outE("knows"), outE("created"));
                logger.info("case:" + searchCase.toString());
                ResultSet resultSet = client.submit(searchCase);
                List<String> searchResult = new ArrayList<String>() {{
                    add("{\"label\":\"knows\",\"pk\":\"1\",\"sk\":\"4\",\"weight\":1.0}");
                    add("{\"label\":\"knows\",\"pk\":\"1\",\"sk\":\"2\",\"weight\":0.5}");
                    add("{\"label\":\"created\",\"pk\":\"1\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"3\",\"weight\":0.4}");
                    add("{\"label\":\"created\",\"pk\":\"4\",\"sk\":\"5\",\"weight\":1.0}");
                    add("{\"label\":\"created\",\"pk\":\"6\",\"sk\":\"3\",\"weight\":0.2}");
                }};
                checkResultEqual(resultSet, searchResult);

                String searchStr = "g(\"tinkerpop_modern\").V(\"1;2;3;4;5;6\").hasLabel(\"person\").union(outE(\"knows\"), outE(\"created\"))";
                logger.info("case(String):" + searchStr);
                resultSet = client.submit(searchStr);
                checkResultEqual(resultSet, searchResult);
            }
        } catch (Exception e) {
            logger.error("result is not equal with expected! " + e);
            e.printStackTrace();
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
        }
    }

    private static void testBenchMark(Client client) {
        Executor executor = Executors.newFixedThreadPool(10);
        int sleepTime = 1;
        while (true) {
            for (Integer i = 0; i < 10000; ++i) {
                Long start = System.nanoTime();
                GraphTraversal gt = g("tinkerpop_modern").V(String.valueOf(i)).hasLabel("person");
                IGraphResultSet resultSet = (IGraphResultSet) client.submit(gt);
                try {
                    if (resultSet.hasError()) {
                        logger.warn("resultSet error:" + resultSet.getErrorMsg());
                    }
                    CompletableFuture<List<Result>> future = resultSet.all();
                    future.whenCompleteAsync((r, e) -> {
                        if (null != e) {
                            logger.warn("search with exception: " + e);
                            analyze(System.nanoTime() - start, true);
                            return;
                        }
                        if (resultSet.hasError()) {
                            logger.warn("search resultSet error:" + resultSet.getErrorMsg());
                        }
                        for (Result result : r) {
                            logger.warn(result.toString());
                        }
                        analyze(System.nanoTime() - start, false);
                    }, executor);
                    if (sleepTime > 0) {
                        if (i % sleepTime == 0) {
                            Thread.sleep(1);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("search with exception: " + e.getMessage());
                    analyze(System.nanoTime() - start, true);
                    return;
                }
            }

        }
    }


    private static void iGraphJoint(int mode) {
        try {
            Cluster.Builder builder = Cluster.build();
            String endpoint = "upsquery-daily2.taobao.com"; //"psquery-daily2.taobao.com";
            String updateEndpoint = "igraph-dailyupdate.taobao.org"; //"igraph-dailyupdate.taobao.org";
            builder.addContactPoint(endpoint).userName("test_name").userPasswd("test_passwd")
                .retryTimes(3).connectionRequestTimeout(200).maxConnTotal(10000).maxConnPerRoute(5000);
            Cluster cluster = builder.create();
            Client client = cluster.connect();
            ((IGraphClient) client).getConfig().setUpdateDomain(updateEndpoint);

            if (0 == mode) {
                testAsyncSearch(client);
                Thread.sleep(1000);
            } else if (1 == mode) {
                testAsyncUpdate(client);
                Thread.sleep(1000);
            } else if (2 == mode) {
                testFunction(client);
                Thread.sleep(1000);
            } else if (3 == mode) {
                testBenchMark(client);
                Thread.sleep(1000);
            } else if (-1 == mode) {
                while (true) {
                    ResultSet resultSet = client.submit(testCase.kvSearchCase);
                    checkResultEqual(resultSet, testCase.kvSearchResult);
                    Thread.sleep(1000);
                }
            }
            client.close();
        } catch (Exception e) {
            logger.error("test failed! " + e + "\nstack:\n");
            e.printStackTrace();
        }
    }

    // bytes = Arrays.copyOfRange(bytes, 2, bytes.length);
    public static void main(String[] args) throws IOException {
        initLogger();
        while (true) {
            errorCount.set(0);
            errorInfo.reverse();
            logger.info("please select joint debug mode (\n" +
                "0 search test\n1 update test\n" +
                "2 function test\n3 benchmark:");
            Scanner sc = new Scanner(System.in);
            int mode = sc.nextInt();

            iGraphJoint(mode);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                logger.error("sleep failed! " + e);
            }
            if (errorCount.get() != 0) {
                logger.error("test failed! error count:[" + errorCount.get() + "]\n" + errorInfo);
            } else {
                logger.info("test success");
            }
        }
    }

}
