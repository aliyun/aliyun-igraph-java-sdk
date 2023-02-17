package com.aliyun.igraph.client.core;

import com.aliyun.igraph.client.exception.IGraphRetryableException;
import com.aliyun.igraph.client.gremlin.structure.IGraphMultiResultSet;
import com.aliyun.igraph.client.pg.QueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResult;
import com.aliyun.igraph.client.proto.gremlin_fb.*;
import com.aliyun.igraph.client.proto.gremlin_fb.StringValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.*;
import com.aliyun.igraph.client.proto.pg_fb.FieldValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.FieldValueColumnTable;
import com.aliyun.igraph.client.proto.pg_fb.Int32ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MatchRecords;
import com.aliyun.igraph.client.proto.pg_fb.Result;
import com.google.common.primitives.Ints;
import com.google.flatbuffers.FlatBufferBuilder;
import com.aliyun.igraph.client.exception.IGraphServerException;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.xerial.snappy.Snappy;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhi on 9/12/16.
 */
public class IGraphResultParserTest {
    private int kMaxColumnSize = 10;

    private ByteBuffer createFBBycolumnTestData() {
        FlatBufferBuilder fbb = new FlatBufferBuilder(1);

        int[] fieldNames = {
                fbb.createString("int32"),
        };
        int fieldNamesOffset = MatchRecords.
                createFieldNameVector(fbb, fieldNames);

        int index = 0;
        int[] fieldColumnOffsets = new int[fieldNames.length];

        // int32
        int[] int32 = new int[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            if (0 == i%2) {
                int32[i] = i;
            } else {
                int32[i] = -i;
            }
        }
        int int32ValueOffset = Int32ValueColumn.
                createValueVector(fbb, int32);
        int int32ColumnOffset = Int32ValueColumn.
                createInt32ValueColumn(fbb, int32ValueOffset);
        fieldColumnOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.Int32ValueColumn, int32ColumnOffset);

        int matchRecordValueOffset = MatchRecords.
                createRecordColumnsVector(fbb, fieldColumnOffsets);
        int mrOffset = MatchRecords.
                createMatchRecords(fbb, fieldNamesOffset, matchRecordValueOffset);

        int error_messageOffset = fbb.createString("test error mesage");
        int error_resultOffset = ErrorResult.
                createErrorResult(fbb, 123, error_messageOffset);
        int[] error_resultOffsets = new int[] {error_resultOffset};
        int errorOffset = Result.
                createErrorVector(fbb, error_resultOffsets);
        int trace_infoOffset = fbb.createString("query result from cache");
        int resultOffset = Result.
                createResult(fbb, errorOffset, mrOffset, trace_infoOffset);
        int control_infosOffset = ControlInfos.
                createControlInfos(fbb, 10086, true);

        int pgResultValueOffset = PGResult.createResultVector(fbb, new int[] {resultOffset});
        int pgResultOffset = PGResult.createPGResult(fbb, Status.FINISHED, pgResultValueOffset, control_infosOffset);

        fbb.finish(pgResultOffset);
        return fbb.dataBuffer();
    }

    private ByteBuffer createGremlinTestData(boolean hasError, Long errorCode) {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        List<Integer> results = new ArrayList<Integer>();

        {
            List<Integer> fieldNamesOffset = new ArrayList<Integer>();
            List<Integer> recordColumnOffset = new ArrayList<Integer>();
            // matchRecords
            int[] offsets = new int[kMaxColumnSize];
            String value;
            for (int i = 0; i < kMaxColumnSize; ++i) {
                if (0 == i % 2) {
                    Integer num = -i;
                    value = num.toString();
                } else {
                    value = "string" + i;
                }
                offsets[i] = fbb.createString(value);
            }
            int stringValueColumnOffset = StringValueColumn.createValueVector(fbb, offsets);
            int stringValueColumn = StringValueColumn.createStringValueColumn(fbb, stringValueColumnOffset);
            recordColumnOffset.add(
                com.aliyun.igraph.client.proto.gremlin_fb.FieldValueColumnTable.createFieldValueColumnTable(fbb, com.aliyun.igraph.client.proto.gremlin_fb.FieldValueColumn.StringValueColumn, stringValueColumn));
            fieldNamesOffset.add(fbb.createString("fstring__"));

            int recordColumns = com.aliyun.igraph.client.proto.gremlin_fb.MatchRecords.createRecordColumnsVector(fbb, Ints.toArray(recordColumnOffset));
            int fieldName = com.aliyun.igraph.client.proto.gremlin_fb.MatchRecords.createFieldNameVector(fbb, Ints.toArray(fieldNamesOffset));
            int matchRecords = com.aliyun.igraph.client.proto.gremlin_fb.MatchRecords.createMatchRecords(fbb, fieldName, recordColumns);

            // elementMeta
            List<Integer> elementMetaOffset = new ArrayList<Integer>();
            List<Integer> propertyNameOffset = new ArrayList<Integer>();
            propertyNameOffset.add(fbb.createString("name"));
            propertyNameOffset.add(fbb.createString("age"));
            int propertyNames = ElementMeta.createPropertyNamesVector(fbb, Ints.toArray(propertyNameOffset));
            elementMetaOffset.add(
                ElementMeta.createElementMeta(fbb, 0L, fbb.createString("allocator_1"), fbb.createString("label"), propertyNames, 0L));
            int elementMeta = com.aliyun.igraph.client.proto.gremlin_fb.Result.createElementMetaVector(fbb, Ints.toArray(elementMetaOffset));
            // docData
            List<Integer> docDataOffset = new ArrayList<Integer>();
            docDataOffset.add(DocData.createDocData(fbb, fbb.createString("allocator_1"), matchRecords));
            int docData = com.aliyun.igraph.client.proto.gremlin_fb.Result.createDocDataVector(fbb, Ints.toArray(docDataOffset));
            // objectResult
            List<Integer> gremlinObjectOffset = new ArrayList<Integer>();
            byte int8Value = 13;
            gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, int8Value)));
            gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34)));
            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(gremlinObjectOffset));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
            int gremlinObjectValue = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
            // error
            List<Integer> errorInfoOffset = new ArrayList<Integer>();
            if (hasError) {
                errorInfoOffset.add(ErrorInfo.createErrorInfo(fbb, errorCode, fbb.createString("this is error message")));
                gremlinObjectValue = 0;
            }
            int multiErrorOffset = MultiErrorInfo.createErrorInfoVector(fbb, Ints.toArray(errorInfoOffset));
            int errorInfos = MultiErrorInfo.createMultiErrorInfo(fbb, multiErrorOffset);
            results.add(com.aliyun.igraph.client.proto.gremlin_fb.Result.createResult(fbb, errorInfos, fbb.createString("this is trace"), elementMeta, docData, gremlinObjectValue));
        }
        int resultVec = GremlinResult.createResultVector(fbb, Ints.toArray(results));
        int[] errorInfoOffset = new int[0];
        int multiErrorOffset = MultiErrorInfo.createErrorInfoVector(fbb, errorInfoOffset);
        int errorInfos = MultiErrorInfo.createMultiErrorInfo(fbb, multiErrorOffset);
        String traceInfo = "this is trace";
        int trace = fbb.createString(traceInfo);

        int gremlinResult = GremlinResult.createGremlinResult(fbb, errorInfos, trace, resultVec);
        fbb.finish(gremlinResult);
        return fbb.dataBuffer();
    }

    @Test
    public void testParse() throws  Exception {
        RequestContext requestContext = new RequestContext();
        Assert.assertEquals(null, IGraphResultParser.parse(requestContext, new byte[]{1,2,3}, "pb"));
    }

    @Test
    public void testCheckStatusFBByColumn() throws Exception {
        {
            try {
                IGraphResultParser.checkStatusFBByColumn(Status.TIMEOUT,
                        new RequestContext());
                Assert.fail();
            } catch (Exception e) {
            }

            try {
                IGraphResultParser.checkStatusFBByColumn(Status.FINISHED,
                        new RequestContext());
            } catch (Exception e) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testParseFromFBByColumn() throws Exception {
        { // snappy exception
            byte [] buffer = {1,2,3,4};
            RequestContext requestContext = new RequestContext();
            try {
                IGraphResultParser.parse(requestContext, buffer, "fb2");
                Assert.fail();
            } catch (IGraphServerException expected) {
            }
        }
        {  // snappy exception
            ByteBuffer fbb_buffer = createFBBycolumnTestData();
            byte[] bytes = new byte[fbb_buffer.remaining()];
            fbb_buffer.get(bytes);

            RequestContext requestContext = new RequestContext();
            try {
                IGraphResultParser.parse(requestContext, bytes, "fb2");
                Assert.fail();
            } catch (IGraphServerException e) {
            }
        }
        {
            // compress
            ByteBuffer fbb_buffer = createFBBycolumnTestData();
            byte[] bytes = new byte[fbb_buffer.remaining()];
            fbb_buffer.get(bytes);
            byte[] bytesCompressed = Snappy.compress(bytes);

            RequestContext requestContext = new RequestContext();
            try {
                QueryResult queryResult = IGraphResultParser.parse(requestContext, bytesCompressed, "fb2");
                Assert.assertNotNull(queryResult);
                List<SingleQueryResult> results = queryResult.getAllQueryResult();
                Assert.assertEquals(1, results.size());
                Assert.assertEquals(kMaxColumnSize, results.get(0).size());
                Assert.assertTrue(queryResult.getSingleQueryResult().hasError());
                Assert.assertEquals("123:test error mesage", queryResult.getSingleQueryResult().getErrorMsg());
                for (int i = 0; i < kMaxColumnSize; i++) {
                    if (0 == i%2) {
                        Assert.assertEquals(i,
                                results.get(0).getMatchRecords().get(i).getInt("int32").intValue());
                    } else {
                        Assert.assertEquals(-i,
                                results.get(0).getMatchRecords().get(i).getInt("int32").intValue());
                    }
                }
                Assert.assertEquals(10086, queryResult.getMulticallWeight());
                Assert.assertTrue(queryResult.containHotKey());
            } catch (IGraphServerException e) {
                Assert.fail();
            }
        }
        {
            // no compress
            ByteBuffer fbb_buffer = createFBBycolumnTestData();
            byte[] bytes = new byte[fbb_buffer.remaining()];
            fbb_buffer.get(bytes);
            RequestContext requestContext = new RequestContext();
            try {
                QueryResult queryResult = IGraphResultParser.parse(requestContext, bytes, "fb2nz");
                Assert.assertNotNull(queryResult);
                List<SingleQueryResult> results = queryResult.getAllQueryResult();
                Assert.assertEquals(1, results.size());
                Assert.assertEquals(kMaxColumnSize, results.get(0).size());
                for (int i = 0; i < kMaxColumnSize; i++) {
                    if (0 == i%2) {
                        Assert.assertEquals(i,
                                results.get(0).getMatchRecords().get(i).getInt("int32").intValue());
                    } else {
                        Assert.assertEquals(-i,
                                results.get(0).getMatchRecords().get(i).getInt("int32").intValue());
                    }
                }
                Assert.assertEquals(10086, queryResult.getMulticallWeight());
                Assert.assertTrue(queryResult.containHotKey());
            } catch (Exception e) {
                Assert.fail();
            }
        }
    }

    @Test
    public void testParseGremlin() throws Exception {
        {
            // normal result
            ByteBuffer fbb_buffer = createGremlinTestData(false, 0L);
            byte[] bytes = new byte[fbb_buffer.remaining()];
            fbb_buffer.get(bytes);
            RequestContext requestContext = new RequestContext();
            try {
                IGraphMultiResultSet iGraphMultiResultSet = IGraphResultParser.parseGremlin(requestContext, bytes, "fb2nz");
                Assert.assertNotNull(iGraphMultiResultSet);
                List<ResultSet> results = iGraphMultiResultSet.getAllQueryResult();
                Assert.assertEquals(1, results.size());
            } catch (Exception e) {
                Assert.fail();
            }
        }
        {
            // retryable result
            ByteBuffer fbb_buffer = createGremlinTestData(true, 5211L);
            byte[] bytes = new byte[fbb_buffer.remaining()];
            fbb_buffer.get(bytes);
            RequestContext requestContext = new RequestContext();
            try {
                IGraphResultParser.parseGremlin(requestContext, bytes, "fb2nz");
            } catch (IGraphRetryableException e) {
                Assert.assertTrue(e.getMessage().contains("need retry"));
            } catch (Exception e) {
                Assert.fail();
            }
        }
        {
            // error result
            ByteBuffer fbb_buffer = createGremlinTestData(true, 1L);
            byte[] bytes = new byte[fbb_buffer.remaining()];
            fbb_buffer.get(bytes);
            RequestContext requestContext = new RequestContext();
            try {
                IGraphResultParser.parseGremlin(requestContext, bytes, "fb2nz");
            } catch (IGraphServerException e) {
                Assert.assertTrue(e.getMessage().contains("query error with IGraphQueryException"));
            } catch (Exception e) {
                Assert.fail();
            }
        }
    }
}
