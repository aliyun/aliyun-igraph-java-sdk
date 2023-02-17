package com.aliyun.igraph.client.core.model.gremlin;

import com.aliyun.igraph.client.proto.gremlin_fb.*;
import com.google.common.primitives.Ints;
import com.google.flatbuffers.FlatBufferBuilder;
import com.aliyun.igraph.client.core.IGraphResultParser;
import com.aliyun.igraph.client.core.RequestContext;
import com.aliyun.igraph.client.gremlin.structure.IGraphMultiResultSet;
import com.aliyun.igraph.client.core.model.Outfmt;
import org.junit.Assert;
import org.junit.Test;
import org.xerial.snappy.Snappy;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wl on 2018/8/16.
 */
public class MultiIGraphResultSetTest {
    private int kTestRowSize = 10;
    private int errorCode1 = 100;
    private int errorCode2 = 200;
    private String errorMsg1 = "bad left query";
    private String errorMsg2 = "bad right query";
    private String traceMsg = "[this is trace]";

    private byte[] makeFBByColumnResult(boolean hasError, boolean hasTrace, int resultSize, boolean isCompress) {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        List<Integer> results = new ArrayList<Integer>();

        for (int r = 0; r < resultSize; ++r) {
            List<Integer> fieldNamesOffset = new ArrayList<Integer>();
            List<Integer> recordColumnOffset = new ArrayList<Integer>();

            // matchRecords
            {
                int[] offsets = new int[kTestRowSize];
                String value;
                for (int i = 0; i < kTestRowSize; ++i) {
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
                        FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.StringValueColumn, stringValueColumn));
                fieldNamesOffset.add(fbb.createString("fstring__"));
            }
            {
                boolean[] simpleOffsets = new boolean[kTestRowSize];
                for (int i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = true;
                }
                int columnOffset = BoolValueColumn.createValueVector(fbb, simpleOffsets);
                int column = BoolValueColumn.createBoolValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.BoolValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fbool__"));
            }
            {
                byte[] simpleOffsets = new byte[kTestRowSize];
                for (byte i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = i;
                }
                int columnOffset = Int8ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = Int8ValueColumn.createInt8ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.Int8ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fint8__"));
            }
            {
                short[] simpleOffsets = new short[kTestRowSize];
                for (short i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = i;
                }
                int columnOffset = Int16ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = Int16ValueColumn.createInt16ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.Int16ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fint16__"));
            }
            {
                int[] simpleOffsets = new int[kTestRowSize];
                for (int i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = i;
                }
                int columnOffset = Int32ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = Int32ValueColumn.createInt32ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.Int32ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fint32__"));
            }
            {
                long[] simpleOffsets = new long[kTestRowSize];
                for (long i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[(int)i] = i;
                }
                int columnOffset = Int64ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = Int64ValueColumn.createInt64ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.Int64ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fint64__"));
            }
            {
                byte[] simpleOffsets = new byte[kTestRowSize];
                for (byte i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = i;
                }
                int columnOffset = UInt8ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = UInt8ValueColumn.createUInt8ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.UInt8ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fuint8__"));
            }
            {
                short[] simpleOffsets = new short[kTestRowSize];
                for (short i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = i;
                }
                int columnOffset = UInt16ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = UInt16ValueColumn.createUInt16ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.UInt16ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fuint16__"));
            }
            {
                int[] simpleOffsets = new int[kTestRowSize];
                for (int i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = i;
                }
                int columnOffset = UInt32ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = UInt32ValueColumn.createUInt32ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.UInt32ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fuint32__"));
            }
            {
                long[] simpleOffsets = new long[kTestRowSize];
                for (long i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[(int) i] = i;
                }
                int columnOffset = UInt64ValueColumn.createValueVector(fbb, simpleOffsets);
                int column = UInt64ValueColumn.createUInt64ValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.UInt64ValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fuint64__"));
            }
            {
                float[] simpleOffsets = new float[kTestRowSize];
                for (int i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = (float)i - (float)kTestRowSize/2.0f + 0.005f;
                }
                int columnOffset = FloatValueColumn.createValueVector(fbb, simpleOffsets);
                int column = FloatValueColumn.createFloatValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.FloatValueColumn, column));
                fieldNamesOffset.add(fbb.createString("ffloat__"));
            }
            {
                double[] simpleOffsets = new double[kTestRowSize];
                for (int i = 0; i < kTestRowSize; ++i) {
                    simpleOffsets[i] = (double)i - (double)kTestRowSize/2.0 + 0.005;
                }
                int columnOffset = DoubleValueColumn.createValueVector(fbb, simpleOffsets);
                int column = DoubleValueColumn.createDoubleValueColumn(fbb, columnOffset);
                recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.DoubleValueColumn, column));
                fieldNamesOffset.add(fbb.createString("fdouble__"));
            }
            int recordColumns = MatchRecords.createRecordColumnsVector(fbb, Ints.toArray(recordColumnOffset));
            int fieldName = MatchRecords.createFieldNameVector(fbb, Ints.toArray(fieldNamesOffset));
            int matchRecords = MatchRecords.createMatchRecords(fbb, fieldName, recordColumns);

            // elementMeta
            List<Integer> elementMetaOffset = new ArrayList<Integer>();
            List<Integer> propertyNameOffset = new ArrayList<Integer>();
            propertyNameOffset.add(fbb.createString("name"));
            propertyNameOffset.add(fbb.createString("age"));
            int propertyNames = ElementMeta.createPropertyNamesVector(fbb, Ints.toArray(propertyNameOffset));
            elementMetaOffset.add(
                    ElementMeta.createElementMeta(fbb, 0L, fbb.createString("allocator_1"), fbb.createString("label"), propertyNames, 0L));
            int elementMeta = Result.createElementMetaVector(fbb, Ints.toArray(elementMetaOffset));
            // docData
            List<Integer> docDataOffset = new ArrayList<Integer>();
            docDataOffset.add(DocData.createDocData(fbb, fbb.createString("allocator_1"), matchRecords));
            int docData = Result.createDocDataVector(fbb, Ints.toArray(docDataOffset));
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
            int[] errorInfoOffset = new int[0];
            int multiErrorOffset = MultiErrorInfo.createErrorInfoVector(fbb, errorInfoOffset);
            int errorInfos = MultiErrorInfo.createMultiErrorInfo(fbb, multiErrorOffset);
            results.add(Result.createResult(fbb, errorInfos, fbb.createString(traceMsg), elementMeta, docData, gremlinObjectValue));
        }
        int resultVec = GremlinResult.createResultVector(fbb, Ints.toArray(results));

        // error and trace
        List<Integer> errorInfoOffset = new ArrayList<Integer>();
        String traceInfo = "";
        if (hasError) {
            errorInfoOffset.add(ErrorInfo.createErrorInfo(fbb, errorCode1, fbb.createString(errorMsg1)));
            errorInfoOffset.add(ErrorInfo.createErrorInfo(fbb, errorCode2, fbb.createString(errorMsg2)));
        }
        int multiErrorOffset = MultiErrorInfo.createErrorInfoVector(fbb, Ints.toArray(errorInfoOffset));
        int errorInfos = MultiErrorInfo.createMultiErrorInfo(fbb, multiErrorOffset);

        if (hasTrace) {
            traceInfo = traceMsg;
        }
        int trace = fbb.createString(traceInfo);

        int gremlinResult = GremlinResult.createGremlinResult(fbb, errorInfos, trace, resultVec);
        fbb.finish(gremlinResult);
        ByteBuffer byteBuffer = fbb.dataBuffer();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        if (isCompress) {
            try {
                return Snappy.compress(bytes);
            } catch (Exception e) {
                return null;
            }
        } else {
            return bytes;
        }
    }

    void checkFBByColumnMultiResult(boolean hasTrace, int resultSize, IGraphMultiResultSet multiResult) {
        if (hasTrace) {
            Assert.assertEquals(traceMsg, multiResult.getTraceInfo());
        } else {
            Assert.assertEquals("", multiResult.getTraceInfo());
        }

        if (0 == resultSize) {
            Assert.assertTrue(multiResult.empty());
        } else {
            Assert.assertFalse(multiResult.empty());
        }
        Assert.assertEquals(resultSize, multiResult.size());
        for (int r = 0; r < resultSize; ++r) {
            Assert.assertNotNull(multiResult.getQueryResult(r));
        }
    }

    @Test
    public void multiResultTest() {
        for (int i = 1; i < 5; ++i) {
            boolean hasError = false;
            boolean hasTrace = false;
            byte[] bytes = makeFBByColumnResult(hasError, hasTrace, i, true);
            RequestContext requestContext = new RequestContext();
            IGraphMultiResultSet multiResult = IGraphResultParser.parseGremlin(requestContext, bytes, Outfmt.FBBYCOLUMN.toString());
            checkFBByColumnMultiResult(hasTrace, i, multiResult);
        }

        for (int i = 1; i < 2; ++i) {
            boolean hasError = false;
            boolean hasTrace = true;
            byte[] bytes = makeFBByColumnResult(hasError, hasTrace, i, true);
            RequestContext requestContext = new RequestContext();
            IGraphMultiResultSet multiResult = IGraphResultParser.parseGremlin(requestContext, bytes, Outfmt.FBBYCOLUMN.toString());
            checkFBByColumnMultiResult(hasTrace, i, multiResult);
        }

        for (int i = 1; i < 2; ++i) {
            boolean hasError = true;
            boolean hasTrace = false;
            byte[] bytes = makeFBByColumnResult(hasError, hasTrace, i, true);
            RequestContext requestContext = new RequestContext();
            try {
                IGraphMultiResultSet multiResult = IGraphResultParser.parseGremlin(requestContext, bytes, Outfmt.FBBYCOLUMN.toString());
            } catch (Exception e) {
                String exceptionMsg = e.toString();
                Assert.assertTrue(exceptionMsg.contains(errorMsg1));
                Assert.assertTrue(exceptionMsg.contains(errorMsg2));
                Assert.assertFalse(exceptionMsg.contains(traceMsg));
            }
        }

        for (int i = 1; i < 2; ++i) {
            boolean hasError = true;
            boolean hasTrace = true;
            byte[] bytes = makeFBByColumnResult(hasError, hasTrace, i, true);
            RequestContext requestContext = new RequestContext();
            try {
                IGraphMultiResultSet multiResult = IGraphResultParser.parseGremlin(requestContext, bytes, Outfmt.FBBYCOLUMN.toString());
            } catch (Exception e) {
                String exceptionMsg = e.toString();
                Assert.assertTrue(exceptionMsg.contains(errorMsg1));
                Assert.assertTrue(exceptionMsg.contains(errorMsg2));
                Assert.assertTrue(exceptionMsg.contains(traceMsg));
            }
        }
    }
}
