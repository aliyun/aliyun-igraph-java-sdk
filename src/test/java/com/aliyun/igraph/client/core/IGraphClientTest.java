package com.aliyun.igraph.client.core;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import com.aliyun.igraph.client.config.ClientConfig;
import com.aliyun.igraph.client.config.GremlinConfig;
import com.aliyun.igraph.client.config.PGConfig;
import com.aliyun.igraph.client.config.UpdateConfig;
import com.aliyun.igraph.client.core.model.Query;
import com.aliyun.igraph.client.core.model.QueryType;
import com.aliyun.igraph.client.core.model.UpdateQuery;
import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.gremlin.driver.Client;
import com.aliyun.igraph.client.gremlin.driver.Cluster;
import com.aliyun.igraph.client.gremlin.driver.GremlinSession;
import com.aliyun.igraph.client.gremlin.structure.IGraphMultiResultSet;
import com.aliyun.igraph.client.net.Requester;
import com.aliyun.igraph.client.net.RequesterConfig;
import com.aliyun.igraph.client.utils.URLCodecUtil;
import com.google.common.collect.Lists;
import com.aliyun.igraph.client.utils.NetUtils;
import com.aliyun.igraph.client.gremlin.gremlin_api.GraphTraversal;
import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.KeyList;
import com.aliyun.igraph.client.pg.PGClient;
import com.aliyun.igraph.client.pg.PGSession;
import com.aliyun.igraph.client.pg.QueryResult;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import org.apache.tinkerpop.gremlin.driver.RequestOptions;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.asynchttpclient.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.aliyun.igraph.client.gremlin.gremlin_api.GraphTraversalSource.g;

/**
 * Created by chekong.ygm on 15/10/8.
 */
public class IGraphClientTest {
    private Requester requester;

    @Before
    public void setUp() {
        RequesterConfig requesterConfig = new RequesterConfig();
        requester = new Requester(requesterConfig);
    }

    @Test
    public void testConstructor() throws Exception {
        {
            ClientConfig clientConfig = new ClientConfig();
            IGraphClient iGraphClient = new IGraphClient(clientConfig, requester);
            Assert.assertSame(clientConfig, iGraphClient.getConfig());
        }
        {
            try {
                new IGraphClient(new ClientConfig(), null);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
        {
            try {
                new IGraphClient(null, requester);
                Assert.fail();
            } catch (NullPointerException expected) {
            }
        }
    }

    @Test
    public void testEmptyQuery() throws Exception {
        {
            ClientConfig clientConfig = new ClientConfig();
            PGClient iGraphClient = new PGClient(clientConfig, requester);
            try {
                iGraphClient.searchSync(new PGSession());
                Assert.assertTrue(false);
            } catch (IGraphQueryException e) {
                Assert.assertTrue(e.getMessage().contains("empty query!"));
            }
        }
        {
            ClientConfig clientConfig = new ClientConfig();
            PGClient iGraphClient = new PGClient(clientConfig, requester);
            try {
                iGraphClient.searchAsync(new PGSession());
                Assert.assertTrue(false);
            } catch (IGraphQueryException e) {
                Assert.assertTrue(e.getMessage().contains("empty query!"));
            }
        }
    }

    @Test
    public void testBuildSearchRequest() throws Exception {
        {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("thesrc");
            clientConfig.setLocalAddress("theAddress");
            PGClient pgClient = new PGClient(clientConfig, requester);
            String queryString = "q fvgb";
            String appName = "q";
            String expected = "app=" + appName + "&src=" + clientConfig.getSrc() + "&ip="
                    + clientConfig.getLocalAddress() + "&ver=java_" + clientConfig.getClientVersion()
                    + "?" + URLCodecUtil.encode(queryString);
            RequestContext requestContext = new RequestContext();
            Deencapsulation.invoke(pgClient, "buildSearchRequest", requestContext, queryString, appName, clientConfig.getSrc());
            String result = requestContext.getRequestContent();
            Assert.assertEquals(expected, result);
        }
    }

    @Test
    public void testBuildUpdateRequest() throws Exception {
        {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("thesrc");
            IGraphClient iGraphClient = new IGraphClient(clientConfig, requester);
            UpdateQuery updateQuery = UpdateQuery.builder().table("kv").pkey("1").valueMaps(new HashMap<String, String>(){{put("mykey", "myvalue");}}).build();
            String expected = "type=" + 1
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery.toString()
                    + "&_message_time_stamp=";
            RequestContext requestContext = new RequestContext();
            Deencapsulation.invoke(iGraphClient, "buildUpdateRequest", requestContext, new UpdateConfig(), 1, updateQuery);
            String result = requestContext.getRequestContent();
            Assert.assertTrue(result.startsWith(expected));
        }
        {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("thesrc");
            IGraphClient iGraphClient = new IGraphClient(clientConfig, requester);
            UpdateConfig updateConfig = new UpdateConfig();
            updateConfig.setSrc("thesrc2");
            UpdateQuery updateQuery = UpdateQuery.builder().table("kv").pkey("2").build();
            String expected = "type=" + 2
                    + "&_src=" + updateConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery.toString()
                    + "&_message_time_stamp=";
            RequestContext requestContext = new RequestContext();
            Deencapsulation.invoke(iGraphClient, "buildUpdateRequest", requestContext, updateConfig, 2, updateQuery);
            String result = requestContext.getRequestContent();
            Assert.assertTrue(result.startsWith(expected));
        }
    }

    @Test
    public void testEncodeRequest() throws Exception {
        {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("thesrc");
            clientConfig.setLocalAddress("theAddress");
            IGraphClient iGraphClient = new IGraphClient(clientConfig, requester);
            PGConfig pgConfig = new PGConfig();
            AtomicQuery atomicQuery1 = AtomicQuery.builder().table("tbl1").keys("1;2;3").filter("value=2").build();
            AtomicQuery atomicQuery2 = AtomicQuery.builder().table("tbl2").keys("4").build();
            String expected = "app=pg"
                    + "&src=" + clientConfig.getSrc()
                    + "&ip=" + clientConfig.getLocalAddress()
                    + "&ver=java_" + clientConfig.getClientVersion()
                    + "?" + URLCodecUtil.encode(pgConfig.getConfigString() + "&&" + atomicQuery1 + "||" + atomicQuery2);
            RequestContext requestContext = new RequestContext();
            Query[] queries = {atomicQuery1, atomicQuery2};
            Deencapsulation.invoke(iGraphClient, "encodeRequest", requestContext, pgConfig, queries);
            String result = requestContext.getRequestContent();
            Assert.assertEquals(expected, result);
        }
        {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("thesrc");
            clientConfig.setLocalAddress("theAddress");
            IGraphClient iGraphClient = new IGraphClient(clientConfig, requester);
            PGConfig pgConfig = new PGConfig();
            pgConfig.setSrc("src2");
            AtomicQuery atomicQuery1 = AtomicQuery.builder().table("tbl1").keys("1;2;3").filter("value=2").build();
            AtomicQuery atomicQuery2 = AtomicQuery.builder().table("tbl2").keys("4").build();
            String expected = "app=pg"
                    + "&src=" + pgConfig.getSrc()
                    + "&ip=" + clientConfig.getLocalAddress()
                    + "&ver=java_" + clientConfig.getClientVersion()
                    + "?" + URLCodecUtil.encode(pgConfig.getConfigString() + "&&" + atomicQuery1 + "||" + atomicQuery2);
            RequestContext requestContext = new RequestContext();
            Query[] queries = {atomicQuery1, atomicQuery2};
            Deencapsulation.invoke(iGraphClient, "encodeRequest", requestContext, pgConfig, queries);
            String result = requestContext.getRequestContent();
            Assert.assertEquals(expected, result);
        }
    }

    @Test
    public void testAsyncSearchPGQuery(@Mocked final Requester iGraphRequester, @Mocked final IGraphResultParser iGraphResultParser) throws Exception {
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            byte[] expectedResponse = "r".getBytes();
            QueryResult expectedResult = new QueryResult();
            new Expectations() {
                {
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parse((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("theSrc");
            clientConfig.setLocalAddress("theAddress");
            clientConfig.setSearchDomain("theDomain");
            final PGClient iGraphClient = new PGClient(clientConfig, iGraphRequester);

            AtomicQuery q1 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey1")))
                    .build();
            AtomicQuery q2 = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(Lists.newArrayList(new KeyList("pkey2")))
                    .build();
            PGSession pgSessionCtx = new PGSession();
            pgSessionCtx.getPgConfig().setSrc("xiaozhi_test_src");

            CompletableFuture<QueryResult> queryResultFuture = iGraphClient.searchAsync(pgSessionCtx, q1, q2);
            responseFuture.complete(null);
            try {
                QueryResult queryResult = queryResultFuture.get();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app="
                    + QueryType.PG.toString()
                    + "&src="
                    + "xiaozhi_test_src"
                    + "&ip="
                    + clientConfig.getLocalAddress()
                    + "&ver=java_"
                    + clientConfig.getClientVersion()
                    + "?"
                    + URLCodecUtil.encode(pgSessionCtx.getPgConfig().getConfigString() + "&&" + q1.toString()
                    + "||" + q2.toString());
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parse((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                }
            };
        }
    }

    @Test
    public void testSearchPGQuery(@Mocked final Requester iGraphRequester, @Mocked final IGraphResultParser iGraphResultParser) throws Exception {
        {
            final byte[] expectedResponse = "r".getBytes();
            final QueryResult expectedResult = new QueryResult();
            new Expectations() {
                {
                    iGraphRequester.sendRequest((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = expectedResponse;
                    IGraphResultParser.parse((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("theSrc");
            clientConfig.setLocalAddress("theAddress");
            clientConfig.setSearchDomain("theDomain");
            final PGClient iGraphClient = new PGClient(clientConfig, iGraphRequester);
            AtomicQuery atomicQuery = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey1")))
                    .build();
            try {
                iGraphClient.searchSync(new PGSession(), atomicQuery);
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app="
                    + QueryType.PG.toString()
                    + "&src="
                    + clientConfig.getSrc()
                    + "&ip="
                    + clientConfig.getLocalAddress()
                    + "&ver=java_"
                    + clientConfig.getClientVersion()
                    + "?"
                    + URLCodecUtil.encode((new PGConfig()).getConfigString() + "&&"
                    + atomicQuery.toString());
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequest(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
        }
        {
            final byte[] expectedResponse = "r".getBytes();
            final QueryResult expectedResult = new QueryResult();
            new Expectations() {
                {
                    iGraphRequester.sendRequest((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = expectedResponse;
                    IGraphResultParser.parse((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("theSrc");
            clientConfig.setLocalAddress("theAddress");
            clientConfig.setSearchDomain("theDomain");
            final PGClient iGraphClient = new PGClient(clientConfig, iGraphRequester);
            AtomicQuery q1 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey1")))
                    .build();
            AtomicQuery q2 = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(Lists.newArrayList(new KeyList("pkey2")))
                    .build();
            try {
                iGraphClient.searchSync(new PGSession(), q1, q2);
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app="
                    + QueryType.PG.toString()
                    + "&src="
                    + clientConfig.getSrc()
                    + "&ip="
                    + clientConfig.getLocalAddress()
                    + "&ver=java_"
                    + clientConfig.getClientVersion()
                    + "?"
                    + URLCodecUtil.encode((new PGConfig()).getConfigString() + "&&" + q1.toString()
                    + "||" + q2.toString());
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequest(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
        }
        {
            final byte[] expectedResponse = "r".getBytes();
            final QueryResult expectedResult = new QueryResult();
            new Expectations() {
                {
                    iGraphRequester.sendRequest((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = expectedResponse;
                    IGraphResultParser.parse((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setSrc("theSrc");
            clientConfig.setLocalAddress("theAddress");
            clientConfig.setSearchDomain("theDomain");
            final PGClient iGraphClient = new PGClient(clientConfig, iGraphRequester);
            AtomicQuery q1 = AtomicQuery.builder()
                    .table("tbl1")
                    .keyLists(Lists.newArrayList(new KeyList("pkey1")))
                    .build();
            AtomicQuery q2 = AtomicQuery.builder()
                    .table("tbl2")
                    .keyLists(Lists.newArrayList(new KeyList("pkey2")))
                    .build();
            PGSession pgSessionCtx = new PGSession();
            pgSessionCtx.getPgConfig().setSrc("xiaozhi_test_src");
            try {
                iGraphClient.searchSync(pgSessionCtx, q1, q2);
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app="
                    + QueryType.PG.toString()
                    + "&src="
                    + "xiaozhi_test_src"
                    + "&ip="
                    + clientConfig.getLocalAddress()
                    + "&ver=java_"
                    + clientConfig.getClientVersion()
                    + "?"
                    + URLCodecUtil.encode(pgSessionCtx.getPgConfig().getConfigString() + "&&" + q1.toString()
                    + "||" + q2.toString());
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequest(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
        }
    }

    @Test
    public void testSearchGremlinQuery(@Mocked final Requester iGraphRequester,
                                       @Mocked final IGraphResultParser iGraphResultParser,
                                       @Mocked final NetUtils netUtils) throws Exception {
        // test graphTraversal
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal graphTraversal = g("graph_name").V("key_1").hasLabel("label_1");
            ResultSet resultSet = client.submit(graphTraversal);
            responseFuture.complete(null);
            try {
                List<Result> results =  resultSet.all().get();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app=gremlin&src=theSrc&ip=1.1.1.1&ver=java_"
                    + new ClientConfig().getClientVersion()
                    + "?" + URLCodecUtil.encode(new GremlinSession(client.config).getGremlinConfig().getConfigString()
                    + "&&" + graphTraversal.toString());

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                    Assert.assertEquals("theDomain", requestContext.getServerAddress());
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
            cluster.close();
        }
        // test retryable graphTraversal
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            responseFuture.completeExceptionally(new TimeoutException());
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd").retryTimes(3);
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal graphTraversal = g("graph_name").V("key_1").hasLabel("label_1");
            ResultSet resultSet = client.submit(graphTraversal);
            responseFuture.complete(null);
            try {
                List<Result> results =  resultSet.all().get();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("timeout to sendRequest"));
            }

            final String expectedQuery = "app=gremlin&src=theSrc&ip=1.1.1.1&ver=java_"
                + new ClientConfig().getClientVersion()
                + "?" + URLCodecUtil.encode(new GremlinSession(client.config).getGremlinConfig().getConfigString()
                + "&&" + graphTraversal.toString());

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 3;
                }
            };
            cluster.close();
        }
        // test String
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal graphTraversal = g("graph_name").V("key_1").hasLabel("label_1");
            String searchStr = "g(\"graph_name\").V(\"key_1\").hasLabel(\"label_1\")";
            ResultSet resultSet = client.submit(searchStr);
            responseFuture.complete(null);
            try {
                List<Result> results =  resultSet.all().get();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app=gremlin&src=theSrc&ip=1.1.1.1&ver=java_"
                    + new ClientConfig().getClientVersion()
                    + "?" + URLCodecUtil.encode(new GremlinSession(client.config).getGremlinConfig().getConfigString()
                    + "&&" + graphTraversal.toString());

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                    Assert.assertEquals("theDomain", requestContext.getServerAddress());
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
            cluster.close();
        }
        // test binding
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            String searchStr = "g(\"graph_name\").V($binding_1).hasLabel(\"label_1\").range(0,$binding_2)";
            Map<String, Object> bindings = new LinkedHashMap<>();
            bindings.put("$binding_1", "key_1");
            bindings.put("$binding_2", 5);
            ResultSet resultSet = client.submit(searchStr, bindings);
            responseFuture.complete(null);
            try {
                List<Result> results =  resultSet.all().get();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app=gremlin&src=theSrc&ip=1.1.1.1&ver=java_"
                    + new ClientConfig().getClientVersion()
                    + "?" + URLCodecUtil.encode(new GremlinSession(client.config).getGremlinConfig().getConfigString()
                    + "&&" + searchStr + "&&bindings=" + "{\"$binding_1\":\"key_1\",\"$binding_2\":\"5\"}");

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                    Assert.assertEquals("theDomain", requestContext.getServerAddress());
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
            cluster.close();
        }
        // test empty binding
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal graphTraversal = g("graph_name").V("key_1").hasLabel("label_1");
            String searchStr = "g(\"graph_name\").V(\"key_1\").hasLabel(\"label_1\")";
            Map<String, Object> bindings = new LinkedHashMap<>();
            ResultSet resultSet = client.submit(searchStr, bindings);
            responseFuture.complete(null);
            try {
                List<Result> results =  resultSet.all().get();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app=gremlin&src=theSrc&ip=1.1.1.1&ver=java_"
                    + new ClientConfig().getClientVersion()
                    + "?" + URLCodecUtil.encode(new GremlinSession(client.config).getGremlinConfig().getConfigString()
                    + "&&" + graphTraversal);

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                    Assert.assertEquals("theDomain", requestContext.getServerAddress());
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
            cluster.close();
        }
        // test optional submit
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            String searchStr = "g(\"graph_name\").V($binding_1).hasLabel(\"label_1\").range(0,$binding_2)";
            RequestOptions requestOptions = RequestOptions.build()
                    .addParameter("$binding_1", "key_1")
                    .addParameter("$binding_2", 5)
                    .src("optionSrc")
                    .timeout(1024)
                    .retryTimes(2048)
                    .create();
            ResultSet resultSet = client.submit(searchStr, requestOptions);
            responseFuture.complete(null);
            try {
                List<Result> results =  resultSet.all().get();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            GremlinSession gremlinSession = new GremlinSession(client.config);
            GremlinConfig gremlinConfig = gremlinSession.getGremlinConfig();
            gremlinConfig.setRetryTimes(2048);
            gremlinConfig.setTimeoutInMs(1024);
            gremlinConfig.setSrc("optionSrc");
            final String expectedQuery = "app=gremlin&src=optionSrc&ip=1.1.1.1&ver=java_"
                    + new ClientConfig().getClientVersion()
                    + "?" + URLCodecUtil.encode(gremlinConfig.getConfigString()
                    + "&&" + searchStr + "&&bindings=" + "{\"$binding_1\":\"key_1\",\"$binding_2\":\"5\"}");

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                    Assert.assertEquals("theDomain", requestContext.getServerAddress());
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
            cluster.close();
        }
        // test batch
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal graphTraversal1 = g("graph_name").V("key_1").hasLabel("label_1");
            GraphTraversal graphTraversal2 = g("graph_name").V("key_2").hasLabel("label_2");
            IGraphMultiResultSet result = client.submit(graphTraversal1, graphTraversal2);
            responseFuture.complete(null);
            try {
                List<ResultSet> resultSets = result.getAllQueryResult();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app=gremlin&src=theSrc&ip=1.1.1.1&ver=java_"
                    + new ClientConfig().getClientVersion()
                    + "?" + URLCodecUtil.encode(new GremlinSession(client.config).getGremlinConfig().getConfigString()
                    + "&&" + graphTraversal1.toString() + "||" + graphTraversal2.toString());

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                    Assert.assertEquals("theDomain", requestContext.getServerAddress());
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                }
            };
            cluster.close();
        }
        {
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            final byte[] expectedResponse = "r".getBytes();
            final IGraphMultiResultSet expectedResult = new IGraphMultiResultSet();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    result = expectedResult;
                }
            };

            Cluster.Builder builder = Cluster.build();
            builder.src("theSrc").addContactPoint("theDomain").userName("theName").userPasswd("thePasswd");
            Cluster cluster = builder.create();
            Client client = cluster.connect("theDomain1").init();
            GraphTraversal graphTraversal1 = g("graph_name").V("key_1").hasLabel("table_1");
            GraphTraversal graphTraversal2 = g("graph_name").V("key_1").hasLabel("table_1");
            List<GraphTraversal> graphTraversals = new ArrayList<>();
            graphTraversals.add(graphTraversal1);
            graphTraversals.add(graphTraversal2);
            IGraphMultiResultSet iGraphMultiResultSet = client.submit( graphTraversals.toArray(new GraphTraversal[0]));
            responseFuture.complete(null);
            try {
                List<ResultSet> resultSets = iGraphMultiResultSet.getAllQueryResult();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("empty result"));
            }

            final String expectedQuery = "app=gremlin&src=theSrc&ip=1.1.1.1&ver=java_"
                    + new ClientConfig().getClientVersion()
                    + "?" + URLCodecUtil.encode(new GremlinSession(client.config).getGremlinConfig().getConfigString()
                    + "&&" + graphTraversal1.toString() + "||" + graphTraversal2.toString());

            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent());
                    Assert.assertEquals("theDomain1", requestContext.getServerAddress());
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                    IGraphResultParser.parseGremlin((RequestContext) any, (byte[]) any, (String) any);
                    times = 1;
                }
            };
            cluster.close();
        }
    }

    @Test
    public void testUpdateSync(@Mocked final Requester iGraphRequester) throws Exception {
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            new Expectations() {
                {
                    iGraphRequester.sendRequest((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = expectedResponse;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setUpdateDomain("updateDomain");
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            Map<String,String> valueMap = new HashMap<String, String>() {
                {
                    put("value1", "value1");
                    put("value2", "value2");
                }
            };
            UpdateQuery updateQuery = UpdateQuery.builder().table("tbl1").pkey("pkey1").skey("skey1").valueMaps(valueMap).build();

            iGraphClient.updateSync(new UpdateSession(), updateQuery);

            final String expectedQuery = "type=1"
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery
                    + "&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequest(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                }
            };
        }
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            new Expectations() {
                {
                    iGraphRequester.sendRequest((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = expectedResponse;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setUpdateDomain("updateDomain");
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            Map<String,String> valueMap = new HashMap<String, String>() {
                {
                    put("value1", "value1");
                    put("value2", "value2");
                }
            };
            UpdateQuery updateQuery = UpdateQuery.builder().table("tbl1").pkey("pkey1").valueMaps(valueMap).build();

            iGraphClient.updateSync(new UpdateSession(), updateQuery);

            final String expectedQuery = "type=1"
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery
                    + "&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequest(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                }
            };
        }
    }

    @Test
    public void testUpdateAsync(@Mocked final Requester iGraphRequester,
                                @Mocked final NetUtils netUtils) throws Exception {
        // PG 语法
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setUpdateDomain("updateDomain");
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            Map<String, String> valueMap = new HashMap<String, String>() {
                {
                    put("value1", "value1");
                    put("value2", "value2");
                }
            };
            UpdateQuery updateQuery = UpdateQuery.builder().table("tbl1").pkey("pkey1").skey("skey1").valueMaps(valueMap).build();

            CompletableFuture future = iGraphClient.updateAsync(new UpdateSession(), updateQuery);
            responseFuture.complete(null);
            future.get();

            final String expectedQuery = "type=1"
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery
                    + "&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length() - 16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
        }
        {
            final byte[] expectedResponse = "<errno>1</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setUpdateDomain("updateDomain");
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            Map<String, String> valueMap = new HashMap<String, String>() {
                {
                    put("value1", "value1");
                    put("value2", "value2");
                }
            };
            UpdateQuery updateQuery = UpdateQuery.builder().table("tbl1").pkey("pkey1").skey("skey1").valueMaps(valueMap).build();

            CompletableFuture future = iGraphClient.updateAsync(new UpdateSession(), updateQuery);
            responseFuture.complete(null);
            try {
                future.get();
                Assert.fail();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("update failed"));
            }

            final String expectedQuery = "type=1"
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery
                    + "&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length() - 16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
        }
        // Gremlin 语法
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            Cluster.Builder builder = Cluster.build();
            builder.userName("test_name").userPasswd("test_passwd").addContactPoint("updateDomain");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal gt = g("graph_name").addV("label_name").property("pkey", "pk").property("value", "0.5");
            ResultSet resultSet = client.submit(gt);
            responseFuture.complete(null);
            List<Result> results = resultSet.all().get();
            Assert.assertEquals(1, results.size());
            Assert.assertEquals("<errno>0</errno>", results.get(0).getString());

            final String expectedQuery = "type=1"
                    + "&_src=test_name_updateDomain&_ver=java_" + new ClientConfig().getClientVersion()
                    + "&table=graph_name_label_name&pkey=pk&value=0.5&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
            cluster.close();
        }
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            Cluster.Builder builder = Cluster.build();
            builder.userName("test_name").userPasswd("test_passwd").addContactPoint("updateDomain");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal gt = g("graph_name").addE("label_name").property("pkey", "pk").property("skey", "sk").property("value", "0.5");
            ResultSet resultSet = client.submit(gt);
            responseFuture.complete(null);
            List<Result> results = resultSet.all().get();
            Assert.assertEquals(1, results.size());
            Assert.assertEquals("<errno>0</errno>", results.get(0).getString());

            final String expectedQuery = "type=1"
                    + "&_src=test_name_updateDomain&_ver=java_" + new ClientConfig().getClientVersion()
                    + "&table=graph_name_label_name&pkey=pk&skey=sk&value=0.5&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
            cluster.close();
        }
        {
            final byte[] expectedResponse = "<errno>1</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            Cluster.Builder builder = Cluster.build();
            builder.userName("test_name").userPasswd("test_passwd").addContactPoint("updateDomain");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal gt = g("graph_name").addV("label_name").property("pkey", "pk").property("value", "0.5");
            try {
                ResultSet resultSet = client.submit(gt);
                responseFuture.complete(null);
                resultSet.all().get();
                Assert.fail();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("update failed"));
            }

            final String expectedQuery = "type=1"
                    + "&_src=test_name_updateDomain&_ver=java_" + new ClientConfig().getClientVersion()
                    + "&table=graph_name_label_name&pkey=pk&value=0.5&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
            cluster.close();
        }
    }

    @Test
    public void testDeleteSync(@Mocked final Requester iGraphRequester) throws Exception {
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            new Expectations() {
                {
                    iGraphRequester.sendRequest((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = expectedResponse;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setUpdateDomain("updateDomain");
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            UpdateQuery updateQuery = UpdateQuery.builder().table("tbl1").pkey("pkey1").build();
            iGraphClient.deleteSync(new UpdateSession(), updateQuery);

            final String expectedQuery = "type=2"
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery
                    + "&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequest(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                }
            };
        }
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            new Expectations() {
                {
                    iGraphRequester.sendRequest((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = expectedResponse;
                }
            };

            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setUpdateDomain("updateDomain");
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            UpdateQuery updateQuery = UpdateQuery.builder().table("tbl1").pkey("pkey1").skey("skey1").build();
            iGraphClient.deleteSync(new UpdateSession(), updateQuery);

            final String expectedQuery = "type=2"
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery
                    + "&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequest(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                }
            };
        }
    }

    @Test
    public void testDeleteAsync(@Mocked final Requester iGraphRequester,
                                @Mocked NetUtils netUtils) throws Exception {
        // PG 语法
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.setUpdateDomain("updateDomain");
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            UpdateQuery updateQuery = UpdateQuery.builder().table("tbl1").pkey("pkey1").skey("skey1").build();

            CompletableFuture<Response> future = iGraphClient.deleteAsync(new UpdateSession(), updateQuery);
            responseFuture.complete(null);
            future.get();
            final String expectedQuery = "type=2"
                    + "&_src=" + clientConfig.getSrc()
                    + "&_ver=java_" + clientConfig.getClientVersion()
                    + "&" + updateQuery
                    + "&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
        }
        // Gremlin 语法
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            Cluster.Builder builder = Cluster.build();
            builder.userName("test_name").userPasswd("test_passwd").addContactPoint("updateDomain");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal gt = g("graph_name").E("pkey1:skey1").hasLabel("label_name").drop();
            ResultSet resultSet = client.submit(gt);
            responseFuture.complete(null);
            List<Result> results = resultSet.all().get();
            Assert.assertEquals(1, results.size());
            Assert.assertEquals("<errno>0</errno>", results.get(0).getString());

            final String expectedQuery = "type=2"
                    + "&_src=test_name_updateDomain&_ver=java_" + new ClientConfig().getClientVersion()
                    + "&table=graph_name_label_name&pkey=pkey1&skey=skey1&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
            cluster.close();
        }
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            Cluster.Builder builder = Cluster.build();
            builder.userName("test_name").userPasswd("test_passwd").addContactPoint("updateDomain");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal gt = g("graph_name").E("pkey1").hasLabel("label_name").drop();
            ResultSet resultSet = client.submit(gt);
            responseFuture.complete(null);
            List<Result> results = resultSet.all().get();
            Assert.assertEquals(1, results.size());
            Assert.assertEquals("<errno>0</errno>", results.get(0).getString());

            final String expectedQuery = "type=2"
                    + "&_src=test_name_updateDomain&_ver=java_" + new ClientConfig().getClientVersion()
                    + "&table=graph_name_label_name&pkey=pkey1&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
            cluster.close();
        }
        {
            final byte[] expectedResponse = "<errno>0</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            Cluster.Builder builder = Cluster.build();
            builder.userName("test_name").userPasswd("test_passwd").addContactPoint("updateDomain");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal gt = g("graph_name").V("pkey1").hasLabel("label_name").drop();
            ResultSet resultSet = client.submit(gt);
            responseFuture.complete(null);
            List<Result> results = resultSet.all().get();
            Assert.assertEquals(1, results.size());
            Assert.assertEquals("<errno>0</errno>", results.get(0).getString());

            final String expectedQuery = "type=2"
                    + "&_src=test_name_updateDomain&_ver=java_" + new ClientConfig().getClientVersion()
                    + "&table=graph_name_label_name&pkey=pkey1&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
            cluster.close();
        }
        {
            final byte[] expectedResponse = "<errno>1</errno>".getBytes();
            CompletableFuture<Response> responseFuture = new CompletableFuture<>();
            new Expectations() {
                {
                    NetUtils.getIntranetIp();
                    result = "1.1.1.1";
                    iGraphRequester.sendRequestAsync((RequestContext) any, (Integer) any, (String) any, (Boolean) any);
                    result = responseFuture;
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    result = expectedResponse;
                }
            };
            Cluster.Builder builder = Cluster.build();
            builder.userName("test_name").userPasswd("test_passwd").addContactPoint("updateDomain");
            Cluster cluster = builder.create();
            Client client = cluster.connect().init();
            GraphTraversal gt = g("graph_name").V("pkey1").hasLabel("label_name").drop();
            try {
                ResultSet resultSet = client.submit(gt);
                responseFuture.complete(null);
                resultSet.all().get();
                Assert.fail();
            } catch (Exception e) {
                Assert.assertTrue(e.getMessage().contains("update failed"));
            }

            final String expectedQuery = "type=2"
                    + "&_src=test_name_updateDomain&_ver=java_" + new ClientConfig().getClientVersion()
                    + "&table=graph_name_label_name&pkey=pkey1&_message_time_stamp=";
            new Verifications() {
                {
                    RequestContext requestContext;
                    iGraphRequester.sendRequestAsync(requestContext = withCapture(), (Integer) any, (String) any, (Boolean) any);
                    times = 1;
                    Assert.assertEquals(expectedQuery, requestContext.getRequestContent()
                            .substring(0, requestContext.getRequestContent().length()-16));
                    iGraphRequester.handleResponse((RequestContext) any, (Response) any);
                    times = 1;
                }
            };
            cluster.close();
        }
    }

    @Test
    public void testClose() throws Exception {
        {
            final boolean[] run = { false };
            new MockUp<Requester>() {
                @Mock
                void close() {
                    run[0] = true;
                }
            };
            Requester iGraphRequester = new Requester(new RequesterConfig());
            final ClientConfig clientConfig = new ClientConfig();
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            iGraphClient.close();
            Assert.assertTrue(run[0]);
        }
        {
            new MockUp<Requester>() {
                @Mock
                void close() {
                    throw new RuntimeException();
                }
            };
            Requester iGraphRequester = new Requester(new RequesterConfig());
            final ClientConfig clientConfig = new ClientConfig();
            final IGraphClient iGraphClient = new IGraphClient(clientConfig, iGraphRequester);
            try {
                iGraphClient.close();
                Assert.fail();
            } catch (RuntimeException expected) {
            }
        }
    }
}
