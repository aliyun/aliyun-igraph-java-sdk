package com.aliyun.igraph.client.dailytest;

import com.aliyun.igraph.client.pg.PGClient;
import com.aliyun.igraph.client.pg.PGClientBuilder;
import com.aliyun.igraph.client.pg.PGSession;
import com.aliyun.igraph.client.pg.AtomicQuery;
import com.aliyun.igraph.client.pg.KeyList;
import com.aliyun.igraph.client.pg.MatchRecord;
import com.aliyun.igraph.client.pg.QueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CloudTest {
    private static Logger logger;

    private static void initLogger() {
            logger = LoggerFactory.getLogger("com.taobao.iGraph");
    }

    private static AtomicLong time = new AtomicLong(0);
    private static AtomicLong allRt = new AtomicLong(0);
    private static AtomicInteger qps = new AtomicInteger(0);
    private static AtomicInteger errorQps = new AtomicInteger(0);
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
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

    public static void main(String[] args) throws IOException {
        Executor executor = Executors.newFixedThreadPool(10);
        initLogger();
        PGClientBuilder iGraphBuilder = PGClientBuilder.create();
        iGraphBuilder.setSocketTimeout(100);
        iGraphBuilder.setConnectTimeout(100);
        iGraphBuilder.setConnectionRequestTimeout(100);
        iGraphBuilder.setMaxConnTotal(10000);
        iGraphBuilder.setMaxConnPerRoute(5000);
        PGClient iGraphClient = iGraphBuilder.build("fancheng_demo", args[0], args[1], args[2]);
        Integer sleepTime = Integer.valueOf(args[3]).intValue();

        try {
            Thread.sleep(3000);
        } catch (Exception e){
            logger.error("sleep error: " + e);
        }

        // search
        while (true) {
            for (Integer i = 0; i < 10000000; ++i) {
                PGSession pgSessionCtx = new PGSession();
                pgSessionCtx.getPgConfig().setOutfmt("fb2");
                pgSessionCtx.getPgConfig().setTimeoutInMs(200);

                List<KeyList> keyList = new ArrayList<>();
                keyList.add(new KeyList(i.toString()));
                AtomicQuery atomicQuery = AtomicQuery.builder().table(args[4]).keyLists(keyList).build();

                Long start = System.nanoTime();
                try {
                    CompletableFuture<QueryResult> future = iGraphClient.searchAsync(pgSessionCtx, atomicQuery);
                    future.whenCompleteAsync((r, e) -> {
                        if (null != e) {
                            logger.warn("search with exception: " + e);
                            analyze(System.nanoTime() - start, true);
                            return;
                        }
                        SingleQueryResult singleQueryResult = r.getQueryResult(0);
                        if (singleQueryResult.hasError()) {
                            logger.warn("single query result error:" + singleQueryResult.getErrorMsg());
                        }
                        MatchRecord matchRecord = singleQueryResult.getMatchRecord(0);
                        if (null == matchRecord) {
                            logger.warn("empty match record");
                            analyze(System.nanoTime() - start, true);
                        } else {
                            analyze(System.nanoTime() - start, false);
                        }
                    }, executor);
                    if (sleepTime > 0) {
                        if (i % sleepTime == 0) {
                            Thread.sleep(1);
                        }
                    }
                } catch (Exception e) {
                    logger.error("search failed!" + e);
                }
            }
            logger.info("finish one loop");
        }

    }
}
