package com.aliyun.igraph.client.core.model.gremlin;

import com.aliyun.igraph.client.proto.gremlin_fb.*;
import com.google.common.primitives.Ints;
import com.google.flatbuffers.FlatBufferBuilder;
import com.aliyun.igraph.client.gremlin.structure.IGraphResultSetDecoder;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wl on 2018/8/14.
 */
public class IGraphResultSetTest {
    private int kTestRowSize = 10;
    private int errorCode1 = 100;
    private int errorCode2 = 200;
    private String errorMsg1 = "bad left query";
    private String errorMsg2 = "bad right query";
    private String traceMsg = "[this is trace]";

    private ByteBuffer makeFBByColumnResult(boolean hasErrorOrTrace) {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
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

        List<Integer> resultsOffset = new ArrayList<Integer>();
        // single result
        {
            // errorInfo
            List<Integer> errorInfoOffset = new ArrayList<Integer>();
            errorInfoOffset.add(ErrorInfo.createErrorInfo(fbb, errorCode1, fbb.createString(errorMsg1)));
            errorInfoOffset.add(ErrorInfo.createErrorInfo(fbb, errorCode2, fbb.createString(errorMsg2)));
            int multiErrorOffset = MultiErrorInfo.createErrorInfoVector(fbb, Ints.toArray(errorInfoOffset));
            int errorInfos = MultiErrorInfo.createMultiErrorInfo(fbb, multiErrorOffset);

            // elementMeta
            List<Integer> elementMetaOffset = new ArrayList<Integer>();
            List<Integer> propertyNameOffset = new ArrayList<Integer>();
            propertyNameOffset.add(fbb.createString("name"));
            propertyNameOffset.add(fbb.createString("age"));
            int propertyNames = ElementMeta.createPropertyNamesVector(fbb, Ints.toArray(propertyNameOffset));
            elementMetaOffset.add(
                    ElementMeta.createElementMeta(fbb, 1L, fbb.createString("allocator_1"), fbb.createString("person"), propertyNames, 0L));
            int elementMeta = Result.createElementMetaVector(fbb, Ints.toArray(elementMetaOffset));
            // docData
            List<Integer> docDataOffset = new ArrayList<Integer>();
            docDataOffset.add(DocData.createDocData(fbb, fbb.createString("allocator_1"), matchRecords));
            int docData = Result.createDocDataVector(fbb, Ints.toArray(docDataOffset));
            // objectResult
            List<Integer> gremlinObjectOffset = new ArrayList<Integer>();
            for (int i = 0; i < kTestRowSize; ++i) {
                int objectElementOffset = ObjectElement.createObjectElement(fbb, i, 1, 0);
                gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, objectElementOffset));
            }
            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(gremlinObjectOffset));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
            int gremlinObjectValue = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
            resultsOffset.add(Result.createResult(fbb, errorInfos, fbb.createString(traceMsg), elementMeta, docData, gremlinObjectValue));
        }
        int result = resultsOffset.get(0);
        fbb.finish(result);
        return fbb.dataBuffer();
    }

    @Test
    public void resultTest() {
        ByteBuffer byteBuffer = makeFBByColumnResult(false);
        Result fbResult = Result.getRootAsResult(byteBuffer);
        IGraphResultSetDecoder singleResult = new IGraphResultSetDecoder();
        singleResult.decodeFBByColumn(fbResult);
        Assert.assertTrue(singleResult.hasError());
        Assert.assertEquals("error_size:2{code:100, msg:bad left query;code:200, msg:bad right query;}", singleResult.getErrorMsg());
        Assert.assertEquals(traceMsg, singleResult.getTraceInfo());
        Assert.assertEquals(kTestRowSize, singleResult.size());
        String[] intTypeArray = {"fint8__", "fint16__", "fint32__", "fint64__", "fuint8__", "fuint16__", "fuint32__", "fuint64__"};
        for (Integer i = 0; i < kTestRowSize; ++i) {
            org.apache.tinkerpop.gremlin.driver.Result resultObject = singleResult.getResultObjects().get(i);
            Vertex vertex = resultObject.getVertex();
            Assert.assertNotNull(vertex);

            // string
            String expect_string;
            if (0 == i % 2) {
                Integer num = -i;
                expect_string = num.toString();
            } else {
                expect_string = "string" + i;
            }
            Assert.assertEquals(expect_string, vertex.value("fstring__"));
            // bool
            Boolean real = vertex.value("fbool__");
            Assert.assertEquals(true, real);

            // fint8__ ~ fuint64__
            for (int j = 0; j < intTypeArray.length; ++j) {
                Assert.assertEquals(String.valueOf(i), vertex.value(intTypeArray[j]).toString());
            }

            // float
            Double expectDouble = (double)i - (double)kTestRowSize/2.0 + 0.005;
            Assert.assertEquals(expectDouble.floatValue(), vertex.value("ffloat__"), (float) 0.0001);
            // double
            Assert.assertEquals(expectDouble, vertex.value("fdouble__"), 0.0001);
        }
        System.out.println(singleResult.toString());
    }
}
