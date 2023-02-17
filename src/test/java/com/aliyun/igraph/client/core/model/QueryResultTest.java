package com.aliyun.igraph.client.core.model;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.igraph.client.pg.QueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResultFBByColumn;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chekong.ygm on 15/10/7.
 */
public class QueryResultTest {
    @Test
    public void testGetSingleQueryResult() throws Exception {
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResult());
            queryResult.setResults(singleQueryResults);
            Assert.assertEquals(singleQueryResults.get(0), queryResult.getSingleQueryResult());
        }
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResultFBByColumn());
            queryResult.setResults(singleQueryResults);
            Assert.assertEquals(singleQueryResults.get(0), queryResult.getSingleQueryResult());
        }
        {
            QueryResult queryResult = new QueryResult();
            Assert.assertNull(queryResult.getSingleQueryResult());
        }
        {
            QueryResult queryResult = new QueryResult();
            queryResult.setResults(new ArrayList<SingleQueryResult>());
            Assert.assertNull(queryResult.getSingleQueryResult());
        }
    }

    @Test
    public void testGetAllQueryResult() throws Exception {
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResult());
            queryResult.setResults(singleQueryResults);
            Assert.assertSame(singleQueryResults, queryResult.getAllQueryResult());
        }
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResultFBByColumn());
            queryResult.setResults(singleQueryResults);
            Assert.assertSame(singleQueryResults, queryResult.getAllQueryResult());
        }
        {
            QueryResult queryResult = new QueryResult();
            Assert.assertNull(queryResult.getAllQueryResult());
        }
    }

    @Test
    public void testGetQueryResult() throws Exception {
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResult());
            queryResult.setResults(singleQueryResults);
            Assert.assertSame(singleQueryResults.get(0), queryResult.getQueryResult(0));
        }
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResultFBByColumn());
            queryResult.setResults(singleQueryResults);
            Assert.assertSame(singleQueryResults.get(0), queryResult.getQueryResult(0));
        }
        {
            QueryResult queryResult = new QueryResult();
            Assert.assertNull(queryResult.getQueryResult(0));
        }
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            queryResult.setResults(singleQueryResults);
            Assert.assertNull(queryResult.getQueryResult(-1));
        }
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResult());
            queryResult.setResults(singleQueryResults);
            Assert.assertNull(queryResult.getQueryResult(2));
        }
        {
            QueryResult queryResult = new QueryResult();
            List<SingleQueryResult> singleQueryResults = new ArrayList<SingleQueryResult>();
            singleQueryResults.add(new SingleQueryResultFBByColumn());
            queryResult.setResults(singleQueryResults);
            Assert.assertNull(queryResult.getQueryResult(2));
        }
    }

    @Test
    public void testGetControlInfo() throws Exception {
        {
            QueryResult queryResult = new QueryResult();
            ControlInfo controlInfo = new ControlInfo();
            queryResult.setControlInfo(controlInfo);
            Assert.assertEquals(100, queryResult.getMulticallWeight());
            Assert.assertFalse(queryResult.containHotKey());
        }
        {
            QueryResult queryResult = new QueryResult();
            ControlInfo controlInfo = new ControlInfo();
            queryResult.setControlInfo(controlInfo);
            queryResult.getControlInfo().setMulticallWeight(10086);
            queryResult.getControlInfo().setContainHotKey(true);
            Assert.assertEquals(10086, queryResult.getMulticallWeight());
            Assert.assertTrue(queryResult.containHotKey());
        }
    }
}
