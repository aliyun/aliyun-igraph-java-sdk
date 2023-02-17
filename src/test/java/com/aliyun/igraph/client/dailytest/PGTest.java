package com.aliyun.igraph.client.dailytest;

import com.aliyun.igraph.client.core.model.Outfmt;
import com.aliyun.igraph.client.core.model.UpdateQuery;
import com.google.common.collect.Lists;
import com.aliyun.igraph.client.pg.PGClientBuilder;
import com.aliyun.igraph.client.pg.PGClient;
import com.aliyun.igraph.client.pg.PGSession;
import com.aliyun.igraph.client.core.UpdateSession;
import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.KeyList;
import com.aliyun.igraph.client.pg.MatchRecord;
import com.aliyun.igraph.client.pg.QueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResult;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import java.io.IOException;

public class PGTest {
    private static Logger logger;
    private static void initLogger() {
        logger = LoggerFactory.getLogger("com.taobao.iGraph");
    }
    private static final AtomicLong time = new AtomicLong(0);
    private static final AtomicLong allRt = new AtomicLong(0);
    private static final AtomicInteger qps = new AtomicInteger(0);
    private static final AtomicInteger errorQps = new AtomicInteger(0);
    private static Long start;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final TestCase testCase = new TestCase();
    private static final AtomicLong errorCount = new AtomicLong(0);
    private static final StringBuilder errorInfo = new StringBuilder(128);

    @Getter
    private static class TestCase {
        public AtomicQuery kvSearchCase1 = AtomicQuery.builder()
                .table("kv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("1")))
                .build();
        public AtomicQuery kvSearchCase2 = AtomicQuery.builder()
                .table("kv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("2")))
                .build();
        public AtomicQuery kkvSearchCase1 = AtomicQuery.builder()
                .table("kkv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("1")))
                .range(0,30)
                .build();
        public AtomicQuery kkvSearchCase2 = AtomicQuery.builder()
                .table("kkv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("2")))
                .build();
        public Map<String, String> kvSearchResult1 = new LinkedHashMap<String, String>() {
            {
                put("pkey", "1");
                put("skey", "1");
                put("value", "2");
            }
        };
        public Map<String, String> kvSearchResult2 = new LinkedHashMap<String, String>() {
            {
                put("pkey", "2");
                put("skey", "2");
                put("value", "3");
            }
        };
        public Map<String, String> kkvSearchResult1 = new LinkedHashMap<String, String>() {
            {
                put("pkey", "1");
                put("skey", "1");
                put("value", "3");
            }
        };
        public Map<String, String> kkvSearchResult2 = new LinkedHashMap<String, String>() {
            {
                put("pkey", "2");
                put("skey", "2");
                put("value", "4");
            }
        };
        private final Long tmpTime = System.nanoTime()%100000;
        private final String randomValue = tmpTime.toString();
        private final Map<String,String> kvValueMap = new LinkedHashMap<String, String>() {
            {
                put("sk", "测试skey");
                put("value", randomValue);
            }
        };
        private final Map<String,String> kkvValueMap = new LinkedHashMap<String, String>() {
            {
                put("value", randomValue);
            }
        };
        public UpdateQuery kvUpdateCase = UpdateQuery.builder()
                .table("kv_shouya_inc")
                .pkey("测试pkey")
                .valueMaps(kvValueMap)
                .build();
        public UpdateQuery kkvUpdateCase = UpdateQuery.builder()
                .table("kkv_fancheng_inc")
                .pkey("测试pkey")
                .skey("测试skey")
                .valueMaps(kkvValueMap)
                .build();
        public AtomicQuery kvUpdateSearchCase = AtomicQuery.builder()
                .table("kv_shouya_inc")
                .keyLists(Lists.newArrayList(new KeyList("测试pkey")))
                .build();
        public AtomicQuery kkvUpdateSearchCase = AtomicQuery.builder()
                .table("kkv_fancheng_inc")
                .keyLists(Lists.newArrayList(new KeyList("测试pkey")))
                .build();
        public UpdateQuery kvDeleteCase = UpdateQuery.builder()
                .table("kv_shouya_inc")
                .pkey("测试pkey")
                .build();
        public UpdateQuery kkvDeleteCase = UpdateQuery.builder()
                .table("kkv_fancheng_inc")
                .pkey("测试pkey")
                .skey("测试skey")
                .build();
        public Map<String, String> kvupdateResult = new LinkedHashMap<String, String>() {
            {
                put("pkey", "测试pkey");
                put("sk", "测试skey");
                put("value", randomValue);
            }
        };
        public Map<String, String> kkvupdateResult = new LinkedHashMap<String, String>() {
            {
                put("pkey", "测试pkey");
                put("skey", "测试skey");
                put("value", randomValue);
            }
        };
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
                        qps.get(), errorQps.get(), (double)errorQps.get() / qps.get(), (double)allRt.get() / qps.get() / 1000000.0);
                qps.set(0);
                errorQps.set(0);
                allRt.set(0);
                time.set(System.currentTimeMillis());
            }
            lock.writeLock().unlock();
        }
    }

    @SafeVarargs
    public static void checkResultEqual(QueryResult search, List<Map<String, String>> ... expectResultLists) throws Exception {
        try {
            if (search == null) {
                if (expectResultLists.length == 0) {
                    return;
                }
                throw new Exception("query result is empty! expect result number: " + expectResultLists.length);
            }
            List<SingleQueryResult> results = search.getAllQueryResult();
            if (expectResultLists.length != results.size()) {
                throw new Exception("expect result number: " + expectResultLists.length + ", actual: " + results.size());
            }
            for (int i = 0; i < results.size(); i++) {
                System.out.println("result " + i + ":");
                List<Map<String, String>> expectResultList = expectResultLists[i];
                SingleQueryResult singleQueryResult = results.get(i);
                if (singleQueryResult.hasError()) {
                    System.out.println("    " + singleQueryResult.getErrorMsg());
                    continue;
                }
                List<MatchRecord> matchRecords = singleQueryResult.getMatchRecords();
                if (expectResultList.size() != matchRecords.size()) {
                    throw new Exception("expect result size: " + expectResultList.size()  + ", actual: " + matchRecords.size());
                }
                for (int j = 0; j < matchRecords.size(); j++) {
                    MatchRecord matchRecord = matchRecords.get(j);
                    Map<String, String> expectResult = expectResultList.get(j);
                    if (expectResult.size() != matchRecord.getFieldValueCount()) {
                        throw new Exception("expect result count: " + expectResultList.size()  + ", actual: " + matchRecords.size());
                    }
                    int k = 0;
                    for (Map.Entry<String, String> entry : expectResult.entrySet()) {
                        String fieldName = singleQueryResult.getFieldNames().get(k);
                        String fieldValue = matchRecord.getFieldValue(k);
                        System.out.print("    " + fieldName + ": " + fieldValue + "    ");
                        if (!entry.getKey().equals(fieldName)) {
                            throw new Exception("expect fieldName: " + entry.getKey() + "; actual: " + fieldName);
                        }
                        if (!entry.getValue().equals(fieldValue)) {
                            throw new Exception("expect " + fieldName + ": " + entry.getValue() + "; actual: " + fieldValue);
                        }
                        k++;
                    }
                    System.out.println();
                }
            }
            String containHotKey = search.containHotKey() ? "true" : "false";
            System.out.println("contain hot key : " + containHotKey);
            System.out.println();
        } catch (Exception e) {
            logger.error("result is not equal with expected! " + e);
            e.printStackTrace();
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
        }
    }


    private static void testSyncPG(PGClient iGraphClient, boolean isInfinite, Outfmt fmt) {
        PGSession pgSessionCtx = new PGSession();
        pgSessionCtx.getPgConfig().setOutfmt(fmt.toString());
        pgSessionCtx.getPgConfig().setTimeoutInMs(200);
        UpdateSession updateSessionCtx = new UpdateSession();
        updateSessionCtx.getUpdateConfig().setTimeoutInMs(200);
        try {
            {
                logger.info("single kv search query:");
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kvSearchCase1), Lists.newArrayList(testCase.kvSearchResult1));
            }
            {
                logger.info("single kkv search query:");
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kkvSearchCase1), Lists.newArrayList(testCase.kkvSearchResult1));
            }
            {
                logger.info("batch kv search query:");
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kvSearchCase1, testCase.kvSearchCase2), Lists.newArrayList(testCase.kvSearchResult1), Lists.newArrayList(testCase.kvSearchResult2));
            }
            {
                logger.info("batch kkv search query:");
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kkvSearchCase1, testCase.kkvSearchCase2), Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(testCase.kkvSearchResult2));
            }
            logger.info("sync search test finish.");
            {
                logger.info("sync kv update query:");
                iGraphClient.updateSync(updateSessionCtx, testCase.kvUpdateCase);
                Thread.sleep(5000);
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kvUpdateSearchCase), Lists.newArrayList(testCase.kvupdateResult));
                logger.info("sync kv delete query:");
                iGraphClient.deleteSync(updateSessionCtx, testCase.kvDeleteCase);
                Thread.sleep(5000);
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kvUpdateSearchCase), Lists.newArrayList());
            }
            {
                logger.info("sync kkv update query:");
                iGraphClient.updateSync(updateSessionCtx, testCase.kkvUpdateCase);
                Thread.sleep(5000);
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kkvUpdateSearchCase), Lists.newArrayList(testCase.kkvupdateResult));
                logger.info("sync kkv delete query:");
                iGraphClient.deleteSync(updateSessionCtx, testCase.kkvDeleteCase);
                Thread.sleep(5000);
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                checkResultEqual(iGraphClient.searchSync(pgSessionCtx, testCase.kkvUpdateSearchCase), Lists.newArrayList());
            }
            logger.info("sync delete and update test finish.");

        } catch (Exception e) {
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
            logger.error("search exception! " + e);
            e.printStackTrace();
        }
    }

    private static void testAsyncPG(PGClient iGraphClient, boolean isInfinite, Outfmt fmt) {
        PGSession pgSessionCtx = new PGSession();
        pgSessionCtx.getPgConfig().setOutfmt(fmt.toString());
        UpdateSession updateSessionCtx = new UpdateSession();
        updateSessionCtx.getUpdateConfig().setTimeoutInMs(2000);
        try {
            {
                logger.info("single kv search query:");
                CompletableFuture<QueryResult> future = iGraphClient.searchAsync(pgSessionCtx, testCase.kvSearchCase1);
                checkResultEqual(future.get(), Lists.newArrayList(testCase.kvSearchResult1));
            }

            {
                logger.info("single kkv search query:");
                CompletableFuture<QueryResult> future = iGraphClient.searchAsync(pgSessionCtx, testCase.kkvSearchCase1);
                checkResultEqual(future.get(), Lists.newArrayList(testCase.kkvSearchResult1));
            }

            {
                logger.info("batch kv search query:");
                CompletableFuture<QueryResult> future = iGraphClient.searchAsync(pgSessionCtx, testCase.kvSearchCase1, testCase.kvSearchCase2);
                checkResultEqual(future.get(), Lists.newArrayList(testCase.kvSearchResult1), Lists.newArrayList(testCase.kvSearchResult2));
            }

            {
                logger.info("batch kkv search query:");
                CompletableFuture<QueryResult> future = iGraphClient.searchAsync(pgSessionCtx, testCase.kkvSearchCase1, testCase.kkvSearchCase2);
                checkResultEqual(future.get(), Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(testCase.kkvSearchResult2));
            }
            logger.info("async search test finish.");

            {
                logger.info("async kv update query:");
                CompletableFuture updateFuture = iGraphClient.updateAsync(updateSessionCtx, testCase.kvUpdateCase);
                updateFuture.get();
                Thread.sleep(5000);
                logger.info("async kv update result query:");
                CompletableFuture<QueryResult> future = iGraphClient.searchAsync(pgSessionCtx, testCase.kvUpdateSearchCase);
                checkResultEqual(future.get(), Lists.newArrayList(testCase.kvupdateResult));

                logger.info("async kv delete query:");
                CompletableFuture deleteFuture = iGraphClient.deleteAsync(updateSessionCtx, testCase.kvDeleteCase);
                deleteFuture.get();
                Thread.sleep(5000);
                CompletableFuture<QueryResult> future2 = iGraphClient.searchAsync(pgSessionCtx, testCase.kvUpdateSearchCase);
                checkResultEqual(future2.get(), Lists.newArrayList());
            }
            {
                logger.info("async kkv update query:");
                CompletableFuture updateFuture = iGraphClient.updateAsync(updateSessionCtx, testCase.kkvUpdateCase);
                updateFuture.get();
                Thread.sleep(5000);
                CompletableFuture<QueryResult> future = iGraphClient.searchAsync(pgSessionCtx, testCase.kkvUpdateSearchCase);
                checkResultEqual(future.get(), Lists.newArrayList(testCase.kkvupdateResult));

                logger.info("async kkv delete query:");
                CompletableFuture deleteFuture = iGraphClient.deleteAsync(updateSessionCtx, testCase.kkvDeleteCase);
                deleteFuture.get();
                Thread.sleep(5000);
                CompletableFuture<QueryResult> future2 = iGraphClient.searchAsync(pgSessionCtx, testCase.kkvUpdateSearchCase);
                checkResultEqual(future2.get(), Lists.newArrayList());
            }
            logger.info("Async update test finish.");
        } catch (Throwable e) {
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
            logger.error("search exception! " + e);
            e.printStackTrace();
        }
    }

    private static void testInvalidQuery(PGClient iGraphClient, Outfmt fmt) {
        PGSession pgSessionCtx = new PGSession();
        pgSessionCtx.getPgConfig().setOutfmt(fmt.toString());
        pgSessionCtx.getPgConfig().setTimeoutInMs(500);
        AtomicQuery validStq = AtomicQuery.builder()
                .table("kkv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("1")))
                .range(0,30)
                .build();
        AtomicQuery invalidStq = AtomicQuery.builder()
                .table("kkv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("")))
                .build();
        AtomicQuery validAtomic = AtomicQuery.builder()
                .table("kv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("1")))
                .build();
        AtomicQuery invalidAtomic = AtomicQuery.builder()
                .table("kv_fancheng")
                .keyLists(Lists.newArrayList(new KeyList("")))
                .build();
        Map<String, String> invalidResult = new LinkedHashMap<String, String>() {
            {
                put("7", "parse keys clause failed.");
            }
        };
        try {
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, validStq, validStq, validStq);
                checkResultEqual(queryResult, Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(testCase.kkvSearchResult1));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, invalidStq, invalidStq);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, invalidStq, validStq);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult), Lists.newArrayList(testCase.kkvSearchResult1));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, invalidStq, invalidAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, validStq, invalidAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, invalidStq, validAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult), Lists.newArrayList(testCase.kvSearchResult1));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, validStq, validAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(testCase.kvSearchResult1));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, invalidAtomic, invalidAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidStq, validAtomic, invalidAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(testCase.kvSearchResult1), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, validStq, invalidAtomic, invalidAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, validStq, validAtomic, invalidAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(testCase.kkvSearchResult1), Lists.newArrayList(testCase.kvSearchResult1), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, invalidAtomic, invalidAtomic, invalidAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult), Lists.newArrayList(invalidResult));
            }
            {
                pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                QueryResult queryResult = iGraphClient.searchSync(pgSessionCtx, validAtomic, validAtomic, validAtomic);
                checkResultEqual(queryResult, Lists.newArrayList(testCase.kvSearchResult1), Lists.newArrayList(testCase.kvSearchResult1), Lists.newArrayList(testCase.kvSearchResult1));
            }
        } catch (Exception e) {
            logger.error("search exception! " + e);
            errorCount.incrementAndGet();
            errorInfo.append("error information: ").append(e).append("\n");
            e.printStackTrace();
        }
        logger.info("invalid query test finish.");
    }

    private static void iGraphJoint(int mode, Integer outfmt) {
        try {
            PGClientBuilder iGraphBuilder = PGClientBuilder.create();
            String endpoint = "33.25.145.200:20102";
            String updateEndpoint = "11.180.250.130:3088";
            PGClient iGraphClient = iGraphBuilder.build("fancheng_demo", endpoint, null, null);
            iGraphClient.getConfig().setUpdateDomain(updateEndpoint);

            Outfmt fmt;
            switch (outfmt) {
                case 0: fmt = Outfmt.FBBYCOLUMN; break;
                case 1: fmt = Outfmt.FBBYCOLUMNNZ; break;
                default:
                    logger.error("outfmt error");
                    return;
            }
            if (0 == mode) {
                testSyncPG(iGraphClient, false, fmt);
            } else if (1 == mode) {
                testAsyncPG(iGraphClient, false, fmt);
                Thread.sleep(1000);
            } else if (2 == mode) {
                testInvalidQuery(iGraphClient, fmt);
            } else if (-1 == mode) {
                while (true) {
                    PGSession pgSessionCtx = new PGSession();
                    pgSessionCtx.getRequestContext().setHasRetryTimes(0);
                    checkResultEqual(iGraphClient.searchSync(pgSessionCtx,testCase.kkvSearchCase1), Lists.newArrayList(testCase.kkvSearchResult1));
                    Thread.sleep(1000);
                }
            }
            iGraphClient.close();
        } catch (Exception e) {
            logger.error("test failed! " + e + "\nstack:\n");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        initLogger();
        logger.info("please select joint debug mode (\n" +
                "0 sync test\n1 async test\n" +
                "2 test invalid search query):");
        Scanner sc = new Scanner(System.in);
        int mode = sc.nextInt();
        logger.info("please select outfmt type(\n" +
                "0 fb2\n" +
                "1 fb2nz):");
        Integer outfmt = sc.nextInt();

        iGraphJoint(mode, outfmt);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.error("sleep failed! " + e);
        }
        if(errorCount.get() != 0) {
            logger.error("test failed! error count:[" + errorCount.get() + "]\n" + errorInfo);
        } else {
            logger.info("test success");
        }
    }
}
