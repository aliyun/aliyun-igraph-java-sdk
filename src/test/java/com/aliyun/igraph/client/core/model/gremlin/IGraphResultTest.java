package com.aliyun.igraph.client.core.model.gremlin;

import com.aliyun.igraph.client.core.model.type.BulkSet;
import com.aliyun.igraph.client.proto.gremlin_fb.*;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.flatbuffers.FlatBufferBuilder;
import com.aliyun.igraph.client.gremlin.structure.ElementMetaType;
import com.aliyun.igraph.client.gremlin.structure.IGraphResult;
import com.aliyun.igraph.client.gremlin.structure.IGraphResultObjectType;
import com.aliyun.igraph.client.gremlin.structure.IGraphResultSetDecoder;
import com.aliyun.igraph.client.core.model.type.multi.MultiByte;
import com.aliyun.igraph.client.core.model.type.multi.MultiDouble;
import com.aliyun.igraph.client.core.model.type.multi.MultiFloat;
import com.aliyun.igraph.client.core.model.type.multi.MultiInt;
import com.aliyun.igraph.client.core.model.type.multi.MultiLong;
import com.aliyun.igraph.client.core.model.type.multi.MultiNumber;
import com.aliyun.igraph.client.core.model.type.multi.MultiShort;
import com.aliyun.igraph.client.core.model.type.multi.MultiString;
import com.aliyun.igraph.client.core.model.type.multi.MultiUInt16;
import com.aliyun.igraph.client.core.model.type.multi.MultiUInt32;
import com.aliyun.igraph.client.core.model.type.multi.MultiUInt64;
import com.aliyun.igraph.client.core.model.type.multi.MultiUInt8;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Pop;
import org.apache.tinkerpop.gremlin.structure.*;
import org.javatuples.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by wl on 2018/8/18.
 */
public class IGraphResultTest {
    private int kTestRowSize = 10;
    FlatBufferBuilder fbb = new FlatBufferBuilder();

    private ByteBuffer makeFBByColumnResult() {
        List<Integer> fieldNamesOffset = new ArrayList<Integer>();
        List<Integer> recordColumnOffset = new ArrayList<Integer>();

        {
            int[] offsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                int[] strings = new int[kTestRowSize];
                for (Integer j = 0; j < kTestRowSize; ++j) {
                    strings[j] = fbb.createString(j.toString());
                }
                int mstringVec = MultiStringValue.createValueVector(fbb, strings);
                offsets[i] = MultiStringValue.createMultiStringValue(fbb, mstringVec);
            }
            int columnValueVec = MultiStringValueColumn.createValueVector(fbb, offsets);
            int columnValue = MultiStringValueColumn.createMultiStringValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(
                    FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiStringValueColumn, columnValue));
            fieldNamesOffset.add(fbb.createString("mstring__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                byte[] multiOffset = new byte[kTestRowSize];
                for (byte j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = j;
                }
                int multiValueVec = MultiInt8Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiInt8Value.createMultiInt8Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiInt8ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiInt8ValueColumn.createMultiInt8ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiInt8ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("mint8__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                short[] multiOffset = new short[kTestRowSize];
                for (short j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = j;
                }
                int multiValueVec = MultiInt16Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiInt16Value.createMultiInt16Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiInt16ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiInt16ValueColumn.createMultiInt16ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiInt16ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("mint16__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                int[] multiOffset = new int[kTestRowSize];
                for (int j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = j;
                }
                int multiValueVec = MultiInt32Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiInt32Value.createMultiInt32Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiInt32ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiInt32ValueColumn.createMultiInt32ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiInt32ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("mint32__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                long[] multiOffset = new long[kTestRowSize];
                for (long j = 0; j < kTestRowSize; ++j) {
                    multiOffset[(int) j] = j;
                }
                int multiValueVec = MultiInt64Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiInt64Value.createMultiInt64Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiInt64ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiInt64ValueColumn.createMultiInt64ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiInt64ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("mint64__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                byte[] multiOffset = new byte[kTestRowSize];
                for (byte j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = j;
                }
                int multiValueVec = MultiUInt8Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiUInt8Value.createMultiUInt8Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiUInt8ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiUInt8ValueColumn.createMultiUInt8ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiUInt8ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("muint8__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                short[] multiOffset = new short[kTestRowSize];
                for (short j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = j;
                }
                int multiValueVec = MultiUInt16Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiUInt16Value.createMultiUInt16Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiUInt16ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiUInt16ValueColumn.createMultiUInt16ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiUInt16ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("muint16__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                int[] multiOffset = new int[kTestRowSize];
                for (int j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = j;
                }
                int multiValueVec = MultiUInt32Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiUInt32Value.createMultiUInt32Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiUInt32ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiUInt32ValueColumn.createMultiUInt32ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiUInt32ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("muint32__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                long[] multiOffset = new long[kTestRowSize];
                for (long j = 0; j < kTestRowSize; ++j) {
                    multiOffset[(int) j] = j;
                }
                int multiValueVec = MultiUInt64Value.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiUInt64Value.createMultiUInt64Value(fbb, multiValueVec);
            }
            int columnValueVec = MultiUInt64ValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiUInt64ValueColumn.createMultiUInt64ValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiUInt64ValueColumn, column));
            fieldNamesOffset.add(fbb.createString("muint64__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                float[] multiOffset = new float[kTestRowSize];
                for (int j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = (float)j / 2.0f + 100.005f;
                }
                int multiValueVec = MultiFloatValue.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiFloatValue.createMultiFloatValue(fbb, multiValueVec);
            }
            int columnValueVec = MultiFloatValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiFloatValueColumn.createMultiFloatValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiFloatValueColumn, column));
            fieldNamesOffset.add(fbb.createString("mfloat__"));
        }
        {
            int[] simpleOffsets = new int[kTestRowSize];
            for (int i = 0; i < kTestRowSize; ++i) {
                double[] multiOffset = new double[kTestRowSize];
                for (int j = 0; j < kTestRowSize; ++j) {
                    multiOffset[j] = (double)j / 2.0 + 100.005;
                }
                int multiValueVec = MultiDoubleValue.createValueVector(fbb, multiOffset);
                simpleOffsets[i] = MultiDoubleValue.createMultiDoubleValue(fbb, multiValueVec);
            }
            int columnValueVec = MultiDoubleValueColumn.createValueVector(fbb, simpleOffsets);
            int column = MultiDoubleValueColumn.createMultiDoubleValueColumn(fbb, columnValueVec);
            recordColumnOffset.add(FieldValueColumnTable.createFieldValueColumnTable(fbb, FieldValueColumn.MultiDoubleValueColumn, column));
            fieldNamesOffset.add(fbb.createString("mdouble__"));
        }
        int recordColumns = MatchRecords.createRecordColumnsVector(fbb, Ints.toArray(recordColumnOffset));
        int fieldName = MatchRecords.createFieldNameVector(fbb, Ints.toArray(fieldNamesOffset));
        int matchRecords = MatchRecords.createMatchRecords(fbb, fieldName, recordColumns);

        List<Integer> results = new ArrayList<Integer>();
        {
            List<Integer> elementMetaOffset = new ArrayList<Integer>();
            {
                List<Integer> propertyNameOffset = new ArrayList<Integer>();
                int propertyNameVec = ElementMeta.createPropertyNamesVector(fbb, Ints.toArray((propertyNameOffset)));
                int elementMeta = ElementMeta.createElementMeta(fbb, 1L, fbb.createString("allocator_1"), fbb.createString("entity"), propertyNameVec, ElementMetaType.ENTITY.toLong());
                elementMetaOffset.add(elementMeta);
            }
            {
                List<Integer> propertyNameOffset = new ArrayList<Integer>();
                int propertyNameVec = ElementMeta.createPropertyNamesVector(fbb, Ints.toArray((propertyNameOffset)));
                int elementMeta = ElementMeta.createElementMeta(fbb, 2L, fbb.createString("allocator_1"), fbb.createString("label"), propertyNameVec, ElementMetaType.PROPERTY.toLong());
                elementMetaOffset.add(elementMeta);
            }
            {
                List<Integer> propertyNameOffset = new ArrayList<Integer>();
                propertyNameOffset.add(fbb.createString("mint8__"));
                propertyNameOffset.add(fbb.createString("mint16__"));
                int propertyNameVec = ElementMeta.createPropertyNamesVector(fbb, Ints.toArray((propertyNameOffset)));
                int elementMeta = ElementMeta.createElementMeta(fbb, 3L, fbb.createString("allocator_1"), fbb.createString("label"), propertyNameVec, ElementMetaType.PROPERTY.toLong());
                elementMetaOffset.add(elementMeta);
            }
            {
                List<Integer> propertyNameOffset = new ArrayList<Integer>();
                propertyNameOffset.add(fbb.createString("mint8__"));
                int propertyNameVec = ElementMeta.createPropertyNamesVector(fbb, Ints.toArray((propertyNameOffset)));
                int elementMeta = ElementMeta.createElementMeta(fbb, 4L, fbb.createString("allocator_1"), fbb.createString("label"), propertyNameVec, ElementMetaType.PROPERTY.toLong());
                elementMetaOffset.add(elementMeta);
            }
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
            for (int i = 0; i < kTestRowSize; ++i) {
                int objectElementOffset = ObjectElement.createObjectElement(fbb, i, 1000, 0);
                gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, objectElementOffset));
            }
            for (int i = 0; i < kTestRowSize; ++i) {
                int objectElementOffset = ObjectElement.createObjectElement(fbb, i, 1, 1);
                gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, objectElementOffset));
            }
            for (int i = 0; i < kTestRowSize; ++i) {
                int objectElementOffset = ObjectElement.createObjectElement(fbb, i, 2, 2);
                gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, objectElementOffset));
            }
            for (int i = 0; i < kTestRowSize; ++i) {
                int objectElementOffset = ObjectElement.createObjectElement(fbb, i, 3, 2);
                gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, objectElementOffset));
            }
            for (int i = 0; i < kTestRowSize; ++i) {
                int objectElementOffset = ObjectElement.createObjectElement(fbb, i, 4, 2);
                gremlinObjectOffset.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, objectElementOffset));
            }
            // error
            List<Integer> errorInfoOffset = new ArrayList<Integer>();
            int multiErrorOffset = MultiErrorInfo.createErrorInfoVector(fbb, Ints.toArray(errorInfoOffset));
            int errorInfos = MultiErrorInfo.createMultiErrorInfo(fbb, multiErrorOffset);
            int trace = fbb.createString("");

            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(gremlinObjectOffset));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
            int gremlinObjectValue = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
            results.add(Result.createResult(fbb, errorInfos, trace, elementMeta, docData, gremlinObjectValue));
        }
        int result = results.get(0);
        fbb.finish(result);
        return fbb.dataBuffer();
    }

    private void checkFBByColmn(ByteBuffer byteBuffer) {
        Result fbResult = Result.getRootAsResult(byteBuffer);
        IGraphResultSetDecoder singleResult = new IGraphResultSetDecoder();
        singleResult.decodeFBByColumn(fbResult);
        Assert.assertFalse(singleResult.hasError());
        Assert.assertNull(singleResult.getErrorMsg());

        Map<String, Integer> fieldName2Index = new HashMap<String, Integer>();
        int idx = 0;
        fieldName2Index.put("mstring__", idx++);
        fieldName2Index.put("mint8__", idx++);
        fieldName2Index.put("mint16__", idx++);
        fieldName2Index.put("mint32__", idx++);
        fieldName2Index.put("mint64__", idx++);
        fieldName2Index.put("muint8__", idx++);
        fieldName2Index.put("muint16__", idx++);
        fieldName2Index.put("muint32__", idx++);
        fieldName2Index.put("muint64__", idx++);
        fieldName2Index.put("mfloat__", idx++);
        fieldName2Index.put("mdouble__", idx++);
        Set<String> keys = new HashSet<>(Arrays.asList("mstring__", "mint8__", "mint16__", "mint32__", "mint64__",
                "muint8__", "muint16__", "muint32__", "muint64__", "mfloat__", "mdouble__"));

        List<org.apache.tinkerpop.gremlin.driver.Result> objectResults = singleResult.getResultObjects();
        Assert.assertEquals(6 * kTestRowSize, objectResults.size());

        for (int i = 0; i < kTestRowSize; ++i) {
            org.apache.tinkerpop.gremlin.driver.Result resultObject = objectResults.get(i);
            Vertex vertex = resultObject.getVertex();
            Assert.assertEquals("entity", vertex.label());
            Set<String> realKeys = vertex.keys();
            Assert.assertEquals(keys, realKeys);


            Element element = resultObject.getElement();
            Assert.assertEquals("entity", element.label());
            Assert.assertEquals(keys, element.keys());
            {
                List<String> realList = element.value("mstring__");
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(String.valueOf(j), realList.get(j));
                }
            }
            {

            }
//            StringBuilder builder = new StringBuilder();
//            for (int j = 0; j < kTestRowSize; ++j) {
//                if (j > 0) {
//                    builder.append(" ");
//                }
//                builder.append(j);
//            }
            {
                List<String> realList = vertex.value("mstring__");
                Property property = vertex.property("mstring__");
                Assert.assertTrue(property.isPresent());
                Assert.assertEquals("mstring__", property.key());
                List<String> realProperty = (List<String>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(String.valueOf(j), realList.get(j));
                    Assert.assertEquals(String.valueOf(j), realProperty.get(j));
                }
                Assert.assertEquals(vertex, property.element());
            }
            {
                List<Integer> realList = vertex.value("mint8__");
                Property property = vertex.property("mint8__");
                List<Integer> realProperty = (List<Integer>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).intValue());
                    Assert.assertEquals(j, realProperty.get(j).intValue());
                }
            }
            {
                List<Integer> realList = vertex.value("mint16__");
                Property property = vertex.property("mint16__");
                List<Integer> realProperty = (List<Integer>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).intValue());
                    Assert.assertEquals(j, realProperty.get(j).intValue());
                }
            }
            {
                List<Integer> realList = vertex.value("mint32__");
                Property property = vertex.property("mint32__");
                List<Integer> realProperty = (List<Integer>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).intValue());
                    Assert.assertEquals(j, realProperty.get(j).intValue());
                }
            }
            {
                List<Long> realList = vertex.value("mint64__");
                Property property = vertex.property("mint64__");
                List<Long> realProperty = (List<Long>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).longValue());
                    Assert.assertEquals(j, realProperty.get(j).longValue());
                }
            }
            {
                List<Integer> realList = vertex.value("muint8__");
                Property property = vertex.property("muint8__");
                List<Integer> realProperty = (List<Integer>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).intValue());
                    Assert.assertEquals(j, realProperty.get(j).intValue());
                }
            }
            {
                List<Integer> realList = vertex.value("muint16__");
                Property property = vertex.property("muint16__");
                List<Integer> realProperty = (List<Integer>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).intValue());
                    Assert.assertEquals(j, realProperty.get(j).intValue());
                }
            }
            {
                List<Integer> realList = vertex.value("muint32__");
                Property property = vertex.property("muint32__");
                List<Integer> realProperty = (List<Integer>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).intValue());
                    Assert.assertEquals(j, realProperty.get(j).intValue());
                }
            }
            {
                List<Long> realList = vertex.value("muint64__");
                Property property = vertex.property("muint64__");
                List<Long> realProperty = (List<Long>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j, realList.get(j).longValue());
                    Assert.assertEquals(j, realProperty.get(j).longValue());
                }
            }
            {
                List<Float> realList = vertex.value("mfloat__");
                Property property = vertex.property("mfloat__");
                List<Float> realProperty = (List<Float>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals((float)j / 2.0f + 100.005f, realList.get(j).floatValue(), 0.001);
                    Assert.assertEquals((float)j / 2.0f + 100.005f, realProperty.get(j).floatValue(), 0.001);
                }
            }
            {
                List<Double> realList = vertex.value("mdouble__");
                Property property = vertex.property("mdouble__");
                List<Double> realProperty = (List<Double>) property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(j/2.0 + 100.005, realList.get(j).floatValue(), 0.001);
                    Assert.assertEquals(j/2.0 + 100.005, realProperty.get(j).floatValue(), 0.001);
                }
            }
            {
                Iterator<List<Integer>> realIterator = vertex.values("mint8__", "mint16__", "mint32__", "muint8__", "muint16__", "muint32__");
                Iterator<List<Integer>> realElementIterator = element.values("mint8__", "mint16__", "mint32__", "muint8__", "muint16__", "muint32__");
                Iterator<VertexProperty<List<Integer>>> vertexPropertyIterator = vertex.properties("mint8__", "mint16__", "mint32__", "muint8__", "muint16__", "muint32__");
                while (realIterator.hasNext() && vertexPropertyIterator.hasNext() && realElementIterator.hasNext()) {
                    VertexProperty<List<Integer>> vertexProperty = vertexPropertyIterator.next();
                    List<Integer> realList = realIterator.next();
                    List<Integer> realElementList = realElementIterator.next();
                    List<Integer> realProperty = vertexProperty.value();
                    for (int j = 0; j < kTestRowSize; ++j) {
                        Assert.assertEquals(j, realList.get(j).intValue());
                        Assert.assertEquals(j, realElementList.get(j).intValue());
                        Assert.assertEquals(j, realProperty.get(j).intValue());
                    }
                }
            }
        }
        for (int i = 0; i < kTestRowSize; ++i) {
            org.apache.tinkerpop.gremlin.driver.Result resultObject = objectResults.get(i + kTestRowSize);
            org.apache.tinkerpop.gremlin.structure.Vertex vertex = resultObject.getVertex();
            Assert.assertNull(vertex);
        }
        for (int i = 0; i < kTestRowSize; ++i) {
            org.apache.tinkerpop.gremlin.driver.Result resultObject = objectResults.get(i + 2 * kTestRowSize);
            Edge edge = resultObject.getEdge();
            Assert.assertNotNull(edge);
            Assert.assertEquals("entity", edge.label());
            Set<String> realKeys = edge.keys();
            Assert.assertEquals(keys, realKeys);
            {
                List<String> realList = edge.value("mstring__");
                Property<List<String>> property = edge.property("mstring__");
                Assert.assertTrue(property.isPresent());
                Assert.assertEquals("mstring__", property.key());
                List<String> realProperty = property.value();
                for (int j = 0; j < kTestRowSize; ++j) {
                    Assert.assertEquals(String.valueOf(j), realList.get(j));
                    Assert.assertEquals(String.valueOf(j), realProperty.get(j));
                }
                Assert.assertEquals(edge, property.element());
            }
            {
                Iterator<List<Integer>> realIterator = edge.values("mint8__", "mint16__", "mint32__", "muint8__", "muint16__", "muint32__");
                Iterator<Property<List<Integer>>> propertyIterator = edge.properties("mint8__", "mint16__", "mint32__", "muint8__", "muint16__", "muint32__");
                while (realIterator.hasNext() && propertyIterator.hasNext()) {
                    Property<List<Integer>> property = propertyIterator.next();
                    List<Integer> realList = realIterator.next();
                    List<Integer> realProperty = property.value();
                    for (int j = 0; j < kTestRowSize; ++j) {
                        Assert.assertEquals(j, realList.get(j).intValue());
                        Assert.assertEquals(j, realProperty.get(j).intValue());
                    }
                }
            }
        }
        for (int i = 0; i < kTestRowSize; ++i) {
            org.apache.tinkerpop.gremlin.driver.Result resultObject = objectResults.get(i + 3 * kTestRowSize);
            org.apache.tinkerpop.gremlin.structure.Property property = resultObject.getProperty();
            Assert.assertNotNull(property);
            Assert.assertNull(property.value());
        }
        for (int i = 0; i < kTestRowSize; ++i) {
            org.apache.tinkerpop.gremlin.driver.Result resultObject = objectResults.get(i + 4 * kTestRowSize);
            resultObject.getObject().getClass();
            org.apache.tinkerpop.gremlin.structure.Property<Object> property = resultObject.getProperty();
            Assert.assertNotNull(property);
            Assert.assertNull(property.value());
        }
        for (int i = 0; i < kTestRowSize; ++i) {
            org.apache.tinkerpop.gremlin.driver.Result resultObject = objectResults.get(i + 5 * kTestRowSize);
            org.apache.tinkerpop.gremlin.structure.Property<List<Integer>> property = resultObject.getProperty();
            Assert.assertNotNull(property);
            Assert.assertEquals("mint8__", property.key());
            Assert.assertTrue(property.isPresent());
            List<Integer> integerList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            property.ifPresent(integers->Assert.assertEquals(integerList, integers));
            Assert.assertEquals(integerList, property.orElse(new ArrayList<>()));
            Assert.assertEquals(integerList, property.orElseGet(ArrayList::new));
            try {
                Assert.assertEquals(integerList, property.orElseThrow(Exception::new));
            } catch (Exception e) {
                Assert.fail();
            }
            List<Integer> realInts = property.value();
            Assert.assertNotNull(realInts);
            for (int j = 0; j < kTestRowSize; ++j) {
                Assert.assertEquals(j, realInts.get(j).intValue());
            }

            try {
                property = Property.empty();
                property.key();
            } catch (IllegalStateException e) {
                Assert.assertEquals("The property does not exist as it has no key, value, or associated element", e.getMessage());
            }
        }
        System.out.println(singleResult.toString());
    }

    @Test
    public void elementTest() {
        ByteBuffer byteBuffer = makeFBByColumnResult();
        checkFBByColmn(byteBuffer);
    }

    private IGraphResult transAndAssert(int offset, IGraphResultObjectType type) {
        fbb.finish(offset);
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult IGraphResult = new IGraphResult();
        IGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(type, IGraphResult.getObjectType());
        return IGraphResult;
    }

    @Test
    public void basicTypeTest() {
        {
            boolean value = true;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.BoolValue, BoolValue.createBoolValue(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.BOOL).getBoolean());
        }
        {
            boolean value = false;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.BoolValue, BoolValue.createBoolValue(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.BOOL).getBoolean());
        }
        {
            byte value = 13;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.INT8).getInt());
        }
        {
            short value = -134;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.Int16Value, Int16Value.createInt16Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.INT16).getInt());
        }
        {
            int value = -23456;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.Int32Value, Int32Value.createInt32Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.INT32).getInt());
        }
        {
            long value = 123456789;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.Int64Value, Int64Value.createInt64Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.INT64).getLong());
        }
        {
            byte value = 13;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.UInt8Value, UInt8Value.createUInt8Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.UINT8).getInt());
        }
        {
            short value = 134;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.UInt16Value, UInt16Value.createUInt16Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.UINT16).getInt());
        }
        {
            int value = 23456;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.UInt32Value, UInt32Value.createUInt32Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.UINT32).getInt());
        }
        {
            long value = 123456789;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.UInt64Value, UInt64Value.createUInt64Value(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.UINT64).getLong());
        }
        {
            float value = 123.456f;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.FloatValue, FloatValue.createFloatValue(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.FLOAT).getFloat(), 0.001);
        }
        {
            double value = 123.4567898;
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, value));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.DOUBLE).getDouble(), 0.000001);
        }
        {
            String value = "abc";
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.StringValue, StringValue.createStringValue(fbb, fbb.createString(value)));
            Assert.assertEquals(value, transAndAssert(objectOffset, IGraphResultObjectType.STRING).getString());
        }
    }

    @Test
    public void vectorOfBasicTypeTest() {
        {
            List<Integer> objectVec = new ArrayList<Integer>();
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte)13)));
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34)));
            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(objectVec));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult IGraphResult = new IGraphResult();
        IGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.LIST, IGraphResult.getObjectType());
//        List<ResultObject> vecValue = resultObject.getList();
        List<IGraphResult> vecValue = IGraphResult.get(List.class);
        Assert.assertNotNull(vecValue);
        Assert.assertEquals(2, vecValue.size());

        Assert.assertEquals(IGraphResultObjectType.INT8, vecValue.get(0).getObjectType());
        Assert.assertEquals(13, vecValue.get(0).getInt());
        Assert.assertEquals(IGraphResultObjectType.DOUBLE, vecValue.get(1).getObjectType());
        Assert.assertEquals(16.34, vecValue.get(1).getDouble(), 0.0001);
        System.out.println(IGraphResult.toString());
        System.out.println(IGraphResult);
    }

    @Test
    public void vectorOfVectorTest() {
        {
            List<Integer> objectVec = new ArrayList<Integer>();
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte)13)));
            {
                List<Integer> subObjectVec = new ArrayList<Integer>();
                subObjectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34)));
                int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(subObjectVec));
                int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
                int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
                int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
                objectVec.add(objectOffset);
            }
            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(objectVec));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult IGraphResult = new IGraphResult();
        IGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.LIST, IGraphResult.getObjectType());
        List<IGraphResult> vecValue = IGraphResult.get(List.class);
        Assert.assertNotNull(vecValue);
        Assert.assertEquals(2, vecValue.size());

        Assert.assertEquals(IGraphResultObjectType.INT8, vecValue.get(0).getObjectType());
        Assert.assertEquals(13, vecValue.get(0).getInt());
        IGraphResult subObject = vecValue.get(1);
        Assert.assertEquals(IGraphResultObjectType.LIST, subObject.getObjectType());
        List<IGraphResult> value = subObject.get(List.class);
        Assert.assertEquals(1, value.size());
        Assert.assertEquals(16.34, value.get(0).getDouble(), 0.0001);
        System.out.println(IGraphResult.toString());
        System.out.println(IGraphResult);
    }

    public abstract class CreateVector<T> {
        public CreateVector(TestCase<T> testCase) {
            this.testCase = testCase;
        }
        public abstract int create();
        public abstract void verify(IGraphResult object);

        protected TestCase<T> testCase;
    };

    class TestCase<T> {
        public TestCase(byte objType, IGraphResultObjectType resultObjType, IGraphResultObjectType singleObjType, T... values) {
            this.objectType = objType;
            this.resultObjectType = resultObjType;
            this.singleObjectType = singleObjType;
            this.values = values;
        }

        public void test(CreateVector createVector) {
            {
                List<Integer> objectVec = new ArrayList<Integer>();
                int multiValueOffset = createVector.create();
                int multiValueObjectOffset = GremlinObject.createGremlinObject(fbb, getObjectType(), multiValueOffset);
                objectVec.add(multiValueObjectOffset);

                int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(objectVec));
                int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
                int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
                int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
                fbb.finish(objectOffset);
            }
            GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
            Assert.assertNotNull(gremlinObject);
            IGraphResult IGraphResult = new IGraphResult();
            IGraphResult.resource.setGremlinObject(gremlinObject);
            Assert.assertEquals(IGraphResultObjectType.LIST, IGraphResult.getObjectType());
            List<IGraphResult> vecValue = IGraphResult.get(List.class);
            Assert.assertNotNull(vecValue);
            Assert.assertEquals(1, vecValue.size());
            IGraphResult subObject = vecValue.get(0);
            Assert.assertEquals(getResultObjectType(), subObject.getObjectType());
            createVector.verify(subObject);
            System.out.println(IGraphResult.toString());
            System.out.println(IGraphResult);
        }

        private byte objectType;

        public IGraphResultObjectType getSingleObjectType() {
            return singleObjectType;
        }

        private IGraphResultObjectType resultObjectType;
        private IGraphResultObjectType singleObjectType;

        public T[] getValues() {
            return values;
        }

        private T[] values;

        public byte getObjectType() {
            return objectType;
        }

        public IGraphResultObjectType getResultObjectType() {
            return resultObjectType;
        }
    };

    @Test
    public void MultiValueTest() {

        TestCase<Byte> caseInt8 = new TestCase<>(ObjectValue.MultiInt8Value, IGraphResultObjectType.MULTI_INT8_VALUE,
                IGraphResultObjectType.INT8, Byte.MIN_VALUE, Byte.MAX_VALUE);
        caseInt8.test(new CreateVector(caseInt8) {
            @Override
            public int create() {
                int vctOffset = MultiInt8Value.createValueVector(fbb, ArrayUtils.toPrimitive((Byte[])testCase.getValues()));
                return MultiInt8Value.createMultiInt8Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], (byte)sub_objects.get(i).getInt());
                        Assert.assertEquals(testCase.getValues()[i], (byte)sub_objects.get(i).getLong());
                        Assert.assertEquals(testCase.getValues()[i], (byte)sub_objects.get(i).getFloat());
                        Assert.assertEquals(testCase.getValues()[i], (byte) sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    List<Byte> multiValues = object.get(MultiByte.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
                {
                    MultiNumber<? extends Number> multiNumber = object.get(MultiNumber.class);
                    List<? extends Number> values = multiNumber.getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    MultiByte multiByte = object.get(MultiByte.class);
                    List<Byte> multiValues = multiByte.getValue();
//                    List<Byte> multiValues = object.getMultiInt8Value();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        System.out.println(multiValues.get(i));
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
            }
        });

        TestCase<Short> caseInt16 = new TestCase<>(ObjectValue.MultiInt16Value, IGraphResultObjectType.MULTI_INT16_VALUE,
                IGraphResultObjectType.INT16, Short.MIN_VALUE, Short.MAX_VALUE);
        caseInt16.test(new CreateVector(caseInt16) {
            @Override
            public int create() {
                int vctOffset = MultiInt16Value.createValueVector(fbb, ArrayUtils.toPrimitive((Short[])testCase.getValues()));
                return MultiInt16Value.createMultiInt16Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getInt());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getLong());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getFloat());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    List<Short> multiValues = object.get(MultiShort.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
            }
        });

        TestCase<Integer> caseInt32 = new TestCase<>(ObjectValue.MultiInt32Value, IGraphResultObjectType.MULTI_INT32_VALUE,
                IGraphResultObjectType.INT32, Integer.MIN_VALUE, Integer.MAX_VALUE);
        caseInt32.test(new CreateVector(caseInt32) {
            @Override
            public int create() {
                int vctOffset = MultiInt32Value.createValueVector(fbb, ArrayUtils.toPrimitive((Integer[])testCase.getValues()));
                return MultiInt32Value.createMultiInt32Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], sub_objects.get(i).getInt());
                        Assert.assertEquals(testCase.getValues()[i], (int)sub_objects.get(i).getLong());
                        Assert.assertEquals(testCase.getValues()[i], (int)sub_objects.get(i).getFloat());
                        Assert.assertEquals(testCase.getValues()[i], (int)sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    List<Integer> multiValues = object.get(MultiInt.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
            }
        });

        TestCase<Long> caseInt64 = new TestCase<>(ObjectValue.MultiInt64Value, IGraphResultObjectType.MULTI_INT64_VALUE,
                IGraphResultObjectType.INT64, Long.MIN_VALUE, Long.MAX_VALUE);
        caseInt64.test(new CreateVector(caseInt64) {
            @Override
            public int create() {
                int vctOffset = MultiInt64Value.createValueVector(fbb, ArrayUtils.toPrimitive((Long[])testCase.getValues()));
                return MultiInt64Value.createMultiInt64Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], sub_objects.get(i).getLong());
                        Assert.assertEquals(testCase.getValues()[i], (long)sub_objects.get(i).getFloat());
                        Assert.assertEquals(testCase.getValues()[i], (long)sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    List<Long> multiValues = object.get(MultiLong.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
            }
        });

        TestCase<Byte> caseUInt8 = new TestCase<>(ObjectValue.MultiUInt8Value, IGraphResultObjectType.MULTI_UINT8_VALUE,
                IGraphResultObjectType.UINT8, (byte)0, Byte.MAX_VALUE);
        caseUInt8.test(new CreateVector(caseUInt8) {
            @Override
            public int create() {
                int vctOffset = MultiUInt8Value.createValueVector(fbb, ArrayUtils.toPrimitive((Byte[])testCase.getValues()));
                return MultiUInt8Value.createMultiUInt8Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], (byte)sub_objects.get(i).getInt());
                        Assert.assertEquals(testCase.getValues()[i], (byte)sub_objects.get(i).getLong());
                        Assert.assertEquals(testCase.getValues()[i], (byte)sub_objects.get(i).getFloat());
                        Assert.assertEquals(testCase.getValues()[i], (byte)sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(Integer.valueOf((Byte)testCase.getValues()[i]), values.get(i));
                    }
                    List<Integer> multiValues = object.get(MultiUInt8.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(Integer.valueOf((Byte)testCase.getValues()[i]), multiValues.get(i));
                    }
                }
            }
        });

        TestCase<Short> caseUInt16 = new TestCase<>(ObjectValue.MultiUInt16Value, IGraphResultObjectType.MULTI_UINT16_VALUE,
                IGraphResultObjectType.UINT16, (short)0, Short.MAX_VALUE);
        caseUInt16.test(new CreateVector(caseUInt16) {
            @Override
            public int create() {
                int vctOffset = MultiUInt16Value.createValueVector(fbb, ArrayUtils.toPrimitive((Short[])testCase.getValues()));
                return MultiUInt16Value.createMultiUInt16Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getInt());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getLong());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getFloat());
                        Assert.assertEquals(testCase.getValues()[i], (short)sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(((Short)testCase.getValues()[i]).intValue(), values.get(i));
                    }
                    List<Integer> multiValues = object.get(MultiUInt16.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals((Integer)((Short)testCase.getValues()[i]).intValue(), multiValues.get(i));
                    }
                }
            }
        });

        TestCase<Integer> caseUInt32 = new TestCase<>(ObjectValue.MultiUInt32Value, IGraphResultObjectType.MULTI_UINT32_VALUE,
                IGraphResultObjectType.UINT32,0, Integer.MAX_VALUE);
        caseUInt32.test(new CreateVector(caseUInt32) {
            @Override
            public int create() {
                int vctOffset = MultiUInt32Value.createValueVector(fbb, ArrayUtils.toPrimitive((Integer[])testCase.getValues()));
                return MultiUInt32Value.createMultiUInt32Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals((long)((Integer)testCase.getValues()[i]).intValue(), (long)sub_objects.get(i).getInt());
                        Assert.assertEquals((long)((Integer)testCase.getValues()[i]).intValue(), (long)sub_objects.get(i).getLong());
                        // Note(zuolong.yc): float不能代替所有的long值，存在精度问题。
                        // Assert.assertEquals((long)((Integer)testCase.getValues()[i]).intValue(), (long)sub_objects.get(i).getFloat().floatValue());
                        Assert.assertEquals((long)((Integer)testCase.getValues()[i]).intValue(), (long)sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals((long)((Integer)testCase.getValues()[i]).intValue(), values.get(i));
                    }
                    List<Long> multiValues = object.get(MultiUInt32.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals((long)((Integer)testCase.getValues()[i]).intValue(), (long)multiValues.get(i));
                    }
                }
            }
        });

        TestCase<Long> caseUInt64 = new TestCase<>(ObjectValue.MultiUInt64Value, IGraphResultObjectType.MULTI_UINT64_VALUE,
                IGraphResultObjectType.UINT64, Long.MIN_VALUE, Long.MAX_VALUE);
        caseUInt64.test(new CreateVector(caseUInt64) {
            @Override
            public int create() {
                int vctOffset = MultiUInt64Value.createValueVector(fbb, ArrayUtils.toPrimitive((Long[])testCase.getValues()));
                return MultiUInt64Value.createMultiUInt64Value(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], sub_objects.get(i).getLong());
                        Assert.assertEquals(testCase.getValues()[i], (long)sub_objects.get(i).getFloat());
                        Assert.assertEquals(testCase.getValues()[i], (long)sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    List<Long> multiValues = object.get(MultiUInt64.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
            }
        });

        TestCase caseFloat = new TestCase<>(ObjectValue.MultiFloatValue, IGraphResultObjectType.MULTI_FLOAT_VALUE,
                IGraphResultObjectType.FLOAT, Float.MIN_VALUE, Float.MAX_VALUE);
        caseFloat.test(new CreateVector(caseFloat) {
            @Override
            public int create() {
                int vctOffset = MultiFloatValue.createValueVector(fbb, ArrayUtils.toPrimitive((Float[])testCase.getValues()));
                return MultiFloatValue.createMultiFloatValue(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], sub_objects.get(i).getFloat());
                        //Assert.assertEquals(testCase.getValues()[i], sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    List<Float> multiValues = object.get(MultiFloat.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
            }
        });

        TestCase caseDouble = new TestCase<>(ObjectValue.MultiDoubleValue, IGraphResultObjectType.MULTI_DOUBLE_VALUE,
                IGraphResultObjectType.DOUBLE, Double.MIN_VALUE, Double.MAX_VALUE);
        caseDouble.test(new CreateVector(caseDouble) {
            @Override
            public int create() {
                int vctOffset = MultiDoubleValue.createValueVector(fbb, ArrayUtils.toPrimitive((Double[])testCase.getValues()));
                return MultiDoubleValue.createMultiDoubleValue(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], sub_objects.get(i).getDouble());

                    }
                }
                {
                    List<? extends Number> values = object.get(MultiNumber.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                    List<Double> multiValues = object.get(MultiDouble.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, multiValues.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], multiValues.get(i));
                    }
                }
            }
        });

        TestCase<String> caseString = new TestCase<>(ObjectValue.MultiStringValue, IGraphResultObjectType.MULTI_STRING_VALUE,
                IGraphResultObjectType.STRING, new String("a"), new String("bc"));
        caseString.test(new CreateVector(caseString) {
            @Override
            public int create() {
                String[] strings = (String[])testCase.getValues();
                int[] values = new int[testCase.getValues().length];
                for (int i = 0; i < testCase.getValues().length; i++) {
                    int stringOffset = fbb.createString(ByteBuffer.wrap(strings[i].getBytes()));
                    values[i] = stringOffset;

                }
                int vctOffset = MultiStringValue.createValueVector(fbb, values);
                return MultiStringValue.createMultiStringValue(fbb, vctOffset);
            }

            @Override
            public void verify(IGraphResult object) {
                {
                    List<IGraphResult> sub_objects = object.get(List.class);
                    testCase.getValues();
                    Assert.assertEquals(testCase.getValues().length, sub_objects.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getSingleObjectType(), sub_objects.get(i).getObjectType());
                        Assert.assertEquals(testCase.getValues()[i], sub_objects.get(i).getString());

                    }
                }
                {
                    List<String> values = object.get(MultiString.class).getValue();
                    Assert.assertEquals(testCase.getValues().length, values.size());
                    for (int i = 0; i < testCase.getValues().length; i++) {
                        Assert.assertEquals(testCase.getValues()[i], values.get(i));
                    }
                }
            }
        });
    }
    @Test
    public void vectorOfElementByRowTest() {
        {
            List<Integer> objectVec = new ArrayList<Integer>();
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, ObjectElement.createObjectElement(fbb, 2, 3, 0)));
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, ObjectElement.createObjectElement(fbb, 5, 6, 1)));
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectElement, ObjectElement.createObjectElement(fbb, 8, 9, 2)));
            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(objectVec));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult IGraphResult = new IGraphResult();
        IGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.LIST, IGraphResult.getObjectType());
        List<IGraphResult> vecValue = IGraphResult.get(List.class);
        Assert.assertNotNull(vecValue);
        Assert.assertEquals(3, vecValue.size());
        // test invalid case
        {
            try {
                vecValue.get(0).getBoolean();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getInt();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getLong();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getFloat();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getDouble();
                Assert.fail();
            } catch (NumberFormatException e) {}

//            Assert.assertNull(vecValue.get(0).getBoolean());
//            Assert.assertNull(vecValue.get(0).getInt());
//            Assert.assertNull(vecValue.get(0).getLong());
//            Assert.assertNull(vecValue.get(0).getFloat());
//            Assert.assertNull(vecValue.get(0).getDouble());
            Assert.assertNull(vecValue.get(0).getString());
            Assert.assertNull(vecValue.get(0).get(List.class));
            Assert.assertNull(vecValue.get(0).get(Set.class));
            Assert.assertNull(vecValue.get(0).get(Map.class));
            Assert.assertNull(vecValue.get(0).get(BulkSet.class).getValue());
            Assert.assertNull(vecValue.get(0).getPath());
            Assert.assertNull(vecValue.get(0).getEdge());
            Assert.assertNull(vecValue.get(0).getProperty());
            Assert.assertEquals(IGraphResultObjectType.VERTEX, vecValue.get(0).getObjectType());
            Assert.assertNull(vecValue.get(0).getVertex());
            Assert.assertEquals(IGraphResultObjectType.EDGE, vecValue.get(1).getObjectType());
            Assert.assertNull(vecValue.get(1).getVertex());
            Assert.assertEquals(IGraphResultObjectType.PROPERTY, vecValue.get(2).getObjectType());
            Assert.assertNull(vecValue.get(2).getVertex());
        }
    }

    @Test
    public void vectorOfElementByColumnTest() {
        {
            List<Integer> docIndexVec = new ArrayList<Integer>();
            List<Integer> metaIdVec = new ArrayList<Integer>();
            List<Integer> elementTypeVec = new ArrayList<Integer>();
            docIndexVec.add(2);
            docIndexVec.add(5);
            docIndexVec.add(8);
            metaIdVec.add(3);
            metaIdVec.add(6);
            metaIdVec.add(9);
            elementTypeVec.add(0);
            elementTypeVec.add(1);
            elementTypeVec.add(2);
            int docOffset = ObjectVecOfElementByColumn.createMatchdocIndexVecVector(fbb, Ints.toArray(docIndexVec));
            int metaOffset = ObjectVecOfElementByColumn.createElementMetaIdVecVector(fbb, Longs.toArray(metaIdVec));
            int typeOffset = ObjectVecOfElementByColumn.createElementTypeVecVector(fbb, Ints.toArray(elementTypeVec));
            int objectVecByColumn = ObjectVecOfElementByColumn.createObjectVecOfElementByColumn(fbb, docOffset, metaOffset, typeOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecOfElementByColumn, objectVecByColumn);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVecValue);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult IGraphResult = new IGraphResult();
        IGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.LIST, IGraphResult.getObjectType());
        List<IGraphResult> vecValue = IGraphResult.get(List.class);
        Assert.assertNotNull(vecValue);
        Assert.assertEquals(3, vecValue.size());
        // test invalid case
        {
            try {
                vecValue.get(0).getBoolean();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getInt();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getLong();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getFloat();
                Assert.fail();
            } catch (NumberFormatException e) {}
            try {
                vecValue.get(0).getDouble();
                Assert.fail();
            } catch (NumberFormatException e) {}


//            Assert.assertNull(vecValue.get(0).getBoolean());
//            Assert.assertNull(vecValue.get(0).getInt());
//            Assert.assertNull(vecValue.get(0).getLong());
//            Assert.assertNull(vecValue.get(0).getFloat());
//            Assert.assertNull(vecValue.get(0).getDouble());
            Assert.assertNull(vecValue.get(0).getString());
            Assert.assertNull(vecValue.get(0).get(List.class));
            Assert.assertNull(vecValue.get(0).get(Set.class));
            Assert.assertNull(vecValue.get(0).get(Map.class));
            Assert.assertNull(vecValue.get(0).get(BulkSet.class).getValue());
            Assert.assertNull(vecValue.get(0).getPath());
            Assert.assertNull(vecValue.get(0).getEdge());
            Assert.assertNull(vecValue.get(0).getProperty());
            Assert.assertEquals(IGraphResultObjectType.VERTEX, vecValue.get(0).getObjectType());
            Assert.assertNull(vecValue.get(0).getVertex());
            Assert.assertEquals(IGraphResultObjectType.EDGE, vecValue.get(1).getObjectType());
            Assert.assertNull(vecValue.get(1).getVertex());
            Assert.assertEquals(IGraphResultObjectType.PROPERTY, vecValue.get(2).getObjectType());
            Assert.assertNull(vecValue.get(2).getVertex());
        }
    }

    private List<IGraphResult> transAndAssertColumn(int objectVec, IGraphResultObjectType type) {
        int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectVec, objectVec);
        fbb.finish(objectOffset);

        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult IGraphResult = new IGraphResult();
        IGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.LIST, IGraphResult.getObjectType());
        List<IGraphResult> results = IGraphResult.get(List.class);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(type, results.get(0).getObjectType());
        Assert.assertEquals(type, results.get(1).getObjectType());
        return results;
    }

    @Test
    public void vectorOfBasicTypeByColumnTest() {
        {
            boolean[] valueVec = {true, false};
            int column = BoolValueColumn.createBoolValueColumn(fbb, BoolValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.BoolValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.BOOL);
            Assert.assertEquals(valueVec[0], results.get(0).getBoolean());
            Assert.assertEquals(valueVec[1], results.get(1).getBoolean());
        }
        {
            byte[] valueVec = {3, 6};
            int column = Int8ValueColumn.createInt8ValueColumn(fbb, Int8ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.Int8ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.INT8);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            short[] valueVec = {-123, -234};
            int column = Int16ValueColumn.createInt16ValueColumn(fbb, Int16ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.Int16ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.INT16);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            int[] valueVec = {-23456, 8888};
            int column = Int32ValueColumn.createInt32ValueColumn(fbb, Int32ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.Int32ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.INT32);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            long[] valueVec = {-123456789, 987654321};
            int column = Int64ValueColumn.createInt64ValueColumn(fbb, Int64ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.Int64ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.INT64);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            byte[] valueVec = {3, 6};
            int column = UInt8ValueColumn.createUInt8ValueColumn(fbb, UInt8ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.UInt8ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.UINT8);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            short[] valueVec = {123, 234};
            int column = UInt16ValueColumn.createUInt16ValueColumn(fbb, UInt16ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.UInt16ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.UINT16);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            int[] valueVec = {23456, 8888};
            int column = UInt32ValueColumn.createUInt32ValueColumn(fbb, UInt32ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.UInt32ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.UINT32);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            long[] valueVec = {123456789, 987654321};
            int column = UInt64ValueColumn.createUInt64ValueColumn(fbb, UInt64ValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.UInt64ValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.UINT64);
            Assert.assertEquals(valueVec[0], results.get(0).getInt());
            Assert.assertEquals(valueVec[1], results.get(1).getInt());
        }
        {
            float[] valueVec = {123.4567f, 987.6543f};
            int column = FloatValueColumn.createFloatValueColumn(fbb, FloatValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.FloatValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.FLOAT);
            Assert.assertEquals(valueVec[0], results.get(0).getFloat(), 0.0001);
            Assert.assertEquals(valueVec[1], results.get(1).getFloat(), 0.0001);
        }
        {
            double[] valueVec = {123.4567, 987.6543};
            int column = DoubleValueColumn.createDoubleValueColumn(fbb, DoubleValueColumn.createValueVector(fbb, valueVec));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.DoubleValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.DOUBLE);
            Assert.assertEquals(valueVec[0], results.get(0).getDouble(), 0.000001);
            Assert.assertEquals(valueVec[1], results.get(1).getDouble(), 0.000001);
        }
        {
            String[] valueVec = {"abc", "cde"};
            int[] valueOffset = new int[2];
            valueOffset[0] = fbb.createString(valueVec[0]);
            valueOffset[1] = fbb.createString(valueVec[1]);
            int column = StringValueColumn.createStringValueColumn(fbb, StringValueColumn.createValueVector(fbb, valueOffset));
            int objectVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.StringValueColumn, column);
            List<IGraphResult> results = transAndAssertColumn(objectVec, IGraphResultObjectType.STRING);
            Assert.assertEquals(valueVec[0], results.get(0).getString());
            Assert.assertEquals(valueVec[1], results.get(1).getString());
        }
    }

    @Test
    public void setTest() {
        {
            List<Integer> objectVec = new ArrayList<Integer>();
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte)13)));
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34)));
            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(objectVec));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objectVecValue = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);
            int objectSetValue = ObjectSet.createObjectSet(fbb, objectVecValue);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectSet, objectSetValue);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult iGraphResult = new IGraphResult();
        iGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.SET, iGraphResult.getObjectType());
        // V1
        Set<IGraphResult> setValue = iGraphResult.get(Set.class);
        Assert.assertNotNull(setValue);
        Assert.assertEquals(2, setValue.size());
        Assert.assertTrue(setValue.contains(13));
        Assert.assertTrue(setValue.contains(16.34));
        Assert.assertFalse(setValue.contains(6.34));

        System.out.println(iGraphResult.toString());
        System.out.println(iGraphResult);
    }

    @Test
    public void mapEntryTest() {
        {
            int keyObjectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte) 13));
            int valueObjectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34));

            int objectMapValue = ObjectMap.createObjectMap(fbb, keyObjectOffset, valueObjectOffset);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectMapEntry, objectMapValue);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult iGraphResult = new IGraphResult();
        iGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.MapEntry, iGraphResult.getObjectType());
//        Map.Entry<ResultObject, ResultObject> entry = resultObject.getMapEntry();
        Map.Entry<IGraphResult, IGraphResult> entry = iGraphResult.get(Map.Entry.class);
        Assert.assertNotNull(entry);

        IGraphResult key = entry.getKey();
        Assert.assertEquals(IGraphResultObjectType.INT8, key.getObjectType());
        Assert.assertEquals(13, key.getInt());
        IGraphResult value = entry.getValue();
        Assert.assertEquals(IGraphResultObjectType.DOUBLE, value.getObjectType());
        Assert.assertEquals(16.34, value.getDouble(), 0.000001);

        System.out.println(iGraphResult.toString());
        System.out.println(iGraphResult);
    }

    @Test
    public void bulkSetTest() {
        {
            List<Integer> keyObjectVec = new ArrayList<Integer>();
            keyObjectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte) 13)));
            keyObjectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34)));
            int keyObjectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(keyObjectVec));
            int keyObjectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, keyObjectVecByRowOffset);
            int keyVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, keyObjectVecByRowValue);

            List<Integer> bulkOffset = new ArrayList<>();
            bulkOffset.add(2);
            bulkOffset.add(3);
            int bulkVecValue = ObjectBulkSet.createBulkCountVector(fbb, Ints.toArray(bulkOffset));
            int bulkSetOffset = ObjectBulkSet.createObjectBulkSet(fbb, keyVec, bulkVecValue);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectBulkSet, bulkSetOffset);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult iGraphResult = new IGraphResult();
        iGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.BULKSET, iGraphResult.getObjectType());
        BulkSet bulkSet = iGraphResult.get(BulkSet.class);
        Map<org.apache.tinkerpop.gremlin.driver.Result, Long> value = bulkSet.getValue();
        Assert.assertNotNull(bulkSet);

        for (Map.Entry<org.apache.tinkerpop.gremlin.driver.Result, Long> entry : value.entrySet()) {
            IGraphResult keyObject = (IGraphResult) entry.getKey();
            if (entry.getValue() == 2) {
                Assert.assertEquals(IGraphResultObjectType.INT8, keyObject.getObjectType());
                Assert.assertEquals(13, keyObject.getInt());
            } else if (entry.getValue() == 3) {
                Assert.assertEquals(IGraphResultObjectType.DOUBLE, keyObject.getObjectType());
                Assert.assertEquals(16.34, keyObject.getDouble(), 0.000001);
            } else {
                Assert.assertTrue(false);
            }
        }
    }

    @Test
    public void mapTest() {
        {
            List<Integer> keyObjectVec = new ArrayList<Integer>();
            keyObjectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte)13)));
            keyObjectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34)));
            int keyObjectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(keyObjectVec));
            int keyObjectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, keyObjectVecByRowOffset);
            int keyVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, keyObjectVecByRowValue);

            List<Integer> valueObjectVec = new ArrayList<Integer>();
            valueObjectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 21.45)));
            valueObjectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte)35)));
            int valueObjectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(valueObjectVec));
            int valueObjectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, valueObjectVecByRowOffset);
            int valueVec = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, valueObjectVecByRowValue);

            int objectMapValue = ObjectMap.createObjectMap(fbb, keyVec, valueVec);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectMap, objectMapValue);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult iGraphResult = new IGraphResult();
        iGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.MAP, iGraphResult.getObjectType());
        // V1
//        Map<ResultObject, ResultObject> mapValue = resultObject.getMap();
        Map<Object, org.apache.tinkerpop.gremlin.driver.Result> mapValue = iGraphResult.get(Map.class);
        Assert.assertNotNull(mapValue);
        Assert.assertEquals(2, mapValue.size());
        Assert.assertEquals(21.45, mapValue.get(13).getDouble(), 0.000001);
        Assert.assertEquals(35, mapValue.get(16.34).getInt());

        System.out.println(iGraphResult.toString());
        System.out.println(iGraphResult);
    }

    @Test
    public void pathTest() {
        {
            List<Integer> objectVec = new ArrayList<Integer>();
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.Int8Value, Int8Value.createInt8Value(fbb, (byte) 13)));
            objectVec.add(GremlinObject.createGremlinObject(fbb, ObjectValue.DoubleValue, DoubleValue.createDoubleValue(fbb, 16.34)));
            int objectVecByRowOffset = ObjectVecByRow.createObjectVector(fbb, Ints.toArray(objectVec));
            int objectVecByRowValue = ObjectVecByRow.createObjectVecByRow(fbb, objectVecByRowOffset);
            int objects = ObjectVec.createObjectVec(fbb, ObjectVecValue.ObjectVecByRow, objectVecByRowValue);

            List<Integer> labelsOffsetVec = new ArrayList<Integer>();
            List<Integer> firstLabelOffset = new ArrayList<Integer>();
            firstLabelOffset.add(fbb.createString("label_a"));
            firstLabelOffset.add(fbb.createString("label_b"));
            int first = MultiStringValue.createValueVector(fbb, Ints.toArray(firstLabelOffset));
            labelsOffsetVec.add(MultiStringValue.createMultiStringValue(fbb, first));

            List<Integer> secondLabelOffset = new ArrayList<Integer>();
            secondLabelOffset.add(fbb.createString("label_c"));
            secondLabelOffset.add(fbb.createString("label_d"));
            secondLabelOffset.add(fbb.createString("label_e"));
            int second = MultiStringValue.createValueVector(fbb, Ints.toArray(secondLabelOffset));
            labelsOffsetVec.add(MultiStringValue.createMultiStringValue(fbb, second));
            int labelVec = ObjectPath.createLablesVector(fbb, Ints.toArray(labelsOffsetVec));

            int path = ObjectPath.createObjectPath(fbb, objects, labelVec);
            int objectOffset = GremlinObject.createGremlinObject(fbb, ObjectValue.ObjectPath, path);
            fbb.finish(objectOffset);
        }
        GremlinObject gremlinObject = GremlinObject.getRootAsGremlinObject(fbb.dataBuffer());
        Assert.assertNotNull(gremlinObject);
        IGraphResult IGraphResult = new IGraphResult();
        IGraphResult.resource.setGremlinObject(gremlinObject);
        Assert.assertEquals(IGraphResultObjectType.PATH, IGraphResult.getObjectType());
        org.apache.tinkerpop.gremlin.driver.Result result = (org.apache.tinkerpop.gremlin.driver.Result) IGraphResult;
        Path path = result.getPath();
        Assert.assertNotNull(path);
        Assert.assertFalse(path.isEmpty());
        Assert.assertEquals(2, path.size());
        Assert.assertEquals(2, path.labels().size());

//        Assert.assertEquals(ResultObjectType.INT8, pathValue.objectList.get(0).getObjectType());
        Assert.assertEquals((byte)13, (byte)path.head());
//        Assert.assertEquals(ResultObjectType.DOUBLE, pathValue.objects().get(1).getObjectType());
        Assert.assertEquals(Double.class, path.objects().get(1).getClass());
        Assert.assertEquals(16.34, (Double) path.objects().get(1), 0.0001);
        Set<String> firstLabel = path.labels().get(0);
        Set<String> secondLabel = path.labels().get(1);
        Assert.assertTrue(path.hasLabel("label_a"));
        Assert.assertTrue(path.hasLabel("label_b"));
        Assert.assertTrue(path.hasLabel("label_c"));
        Assert.assertTrue(path.hasLabel("label_d"));
        Assert.assertTrue(path.hasLabel("label_e"));
        String firstLabelString = firstLabel.toString();
        String secondLabelString = secondLabel.toString();
        Assert.assertTrue(firstLabelString.contains("label_a"));
        Assert.assertTrue(firstLabelString.contains("label_b"));
        Assert.assertTrue(secondLabelString.contains("label_c"));
        Assert.assertTrue(secondLabelString.contains("label_d"));
        Assert.assertTrue(secondLabelString.contains("label_e"));
        Assert.assertEquals(new Byte("13"), path.get("label_b"));
        Assert.assertEquals(new Byte("13"), path.get("label_a"));
        Assert.assertEquals(16.34, path.get("label_c"), 0.0001);
        Assert.assertEquals(16.34, path.get("label_d"), 0.0001);
        Assert.assertEquals(16.34, path.get("label_e"), 0.0001);

        Assert.assertEquals(16.34, path.get(1), 0.0001);
        Assert.assertEquals(16.34, path.get(Pop.first, "label_c"), 0.0001);
        Assert.assertEquals(16.34, path.get(Pop.last, "label_c"),0.0001);
        Assert.assertEquals(Collections.singletonList((Double)16.34), path.get(Pop.all, "label_c"));
        Assert.assertEquals(16.34, path.get(Pop.mixed, "label_c"),0.0001);
        Path clonePath = path.clone();
        Assert.assertEquals(path, clonePath);
        Assert.assertTrue(path.isSimple());
        Iterator iterator = path.iterator();
        Iterator objectIterator = Arrays.asList(new Byte("13"), 16.34).listIterator();
        while (iterator.hasNext()) {
           Assert.assertEquals(objectIterator.next(), iterator.next());
        }
        int [] i = {0};
        List<Object> valueList = Arrays.asList(new Byte("13"), 16.34);
        List<Set<String>> labelList = Arrays.asList(new HashSet<>(Arrays.asList("label_a", "label_b")),
                new HashSet<>(Arrays.asList("label_c", "label_d", "label_e")));
        path.forEach(new BiConsumer<Object, Set<String>>() {
            @Override
            public void accept(Object o, Set<String> strings) {
                Assert.assertEquals(o, valueList.get(i[0]));
                Assert.assertEquals(strings, labelList.get(i[0]++));
            }
        });
        Stream<Pair<Object, Set<String>>> stream = path.stream();
        i[0] = 0;
        stream.forEach(new Consumer<Pair<Object, Set<String>>>() {
            @Override
            public void accept(Pair<Object, Set<String>> objects) {
                Assert.assertEquals(objects.getValue0(), valueList.get(i[0]));
                Assert.assertEquals(objects.getValue1(), labelList.get(i[0]++));
            }
        });
        Assert.assertTrue(path.popEquals(Pop.last, clonePath));
        Assert.assertTrue(path.popEquals(Pop.first, clonePath));
        Assert.assertTrue(path.popEquals(Pop.all, clonePath));
        Assert.assertTrue(path.popEquals(Pop.mixed, clonePath));
        Path subPath = path.subPath("label_a", "label_c");
        Assert.assertEquals(path, subPath);

        System.out.println(IGraphResult);
    }
}
