package com.aliyun.igraph.client.core.model;

import com.aliyun.igraph.client.proto.pg_fb.*;
import com.google.flatbuffers.FlatBufferBuilder;
import com.aliyun.igraph.client.pg.MatchRecord;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

public class MatchRecordTest {
    private int kMaxColumnSize = 10;
    private int fieldCount = 0;

    private ByteBuffer createBasicTypeTestData() {
        FlatBufferBuilder fbb = new FlatBufferBuilder(1);

        int[] fieldNames = {
                fbb.createString("int32"),
                fbb.createString("string"),
                fbb.createString("double"),
        };
        this.fieldCount = fieldNames.length;
        int fieldNamesOffset = MatchRecords.createFieldNameVector(fbb, fieldNames);

        int index = 0;
        int[] fieldColumnOffsets = new int[this.fieldCount];

        // int32
        int[] int32 = new int[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            if (0 == i%2) {
                int32[i] = i;
            } else {
                int32[i] = -i;
            }
        }
        int int32ValueOffset = Int32ValueColumn.createValueVector(fbb, int32);
        int int32ColumnOffset = Int32ValueColumn.createInt32ValueColumn(fbb, int32ValueOffset);
        fieldColumnOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.Int32ValueColumn, int32ColumnOffset);

        // string
        int[] stringOffsets = new int[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            stringOffsets[i] = fbb.createString("string_" + Integer.toString(i));
        }
        int stringValueOffset = StringValueColumn.createValueVector(fbb, stringOffsets);
        int stringColumnOffset = StringValueColumn.createStringValueColumn(fbb, stringValueOffset);
        fieldColumnOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.StringValueColumn, stringColumnOffset);

        // double
        double[] doubles = new double[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            if (0 == i%2) {
                doubles[i] = Float.MAX_VALUE - 0.0015;
            } else {
                doubles[i] = 0.0015 - Float.MAX_VALUE;
            }
        }
        int doubleValueOffset = DoubleValueColumn.createValueVector(fbb, doubles);
        int doubleColumnOffset = DoubleValueColumn.createDoubleValueColumn(fbb, doubleValueOffset);
        fieldColumnOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.DoubleValueColumn, doubleColumnOffset);

        int fieldValuesOffset = MatchRecords.createRecordColumnsVector(fbb, fieldColumnOffsets);
        int mrOffset = MatchRecords.createMatchRecords(fbb, fieldNamesOffset, fieldValuesOffset);

        fbb.finish(mrOffset);
        return fbb.dataBuffer();
    }

    private ByteBuffer createMultiTypeTestData() {
        FlatBufferBuilder fbb = new FlatBufferBuilder(1);

        int[] fieldNames = {
                fbb.createString("mint32"),
                fbb.createString("mstring"),
                fbb.createString("mdouble"),
        };
        this.fieldCount = fieldNames.length;
        int fieldNamesOffset = MatchRecords.createFieldNameVector(fbb, fieldNames);
        int[] mMatchRecordValueOffsets = new int[this.fieldCount];
        int index = 0;

        // Multi int32
        int[] mInt32Offsets = new int[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            int[] int32 = new int[kMaxColumnSize];
            for (int k = 0; k < kMaxColumnSize; k++) {
                int32[k] = Integer.MAX_VALUE;
            }
            int int32ValueOffset = MultiInt32Value.createValueVector(fbb, int32);
            mInt32Offsets[i] = MultiInt32Value.createMultiInt32Value(fbb, int32ValueOffset);
        }
        int mIntColumnValueOffset = MultiInt32ValueColumn.createValueVector(fbb, mInt32Offsets);
        int mIntColumnOffset = MultiInt32ValueColumn.createMultiInt32ValueColumn(fbb, mIntColumnValueOffset);
        mMatchRecordValueOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.MultiInt32ValueColumn, mIntColumnOffset);

        // Multi String
        int[] mStringOffsets = new int[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            int[] stringValueOffsets = new int[kMaxColumnSize];
            for (int k = 0; k < kMaxColumnSize; k++) {
                stringValueOffsets[k] = fbb.createString("string_" + Integer.toString(k));
            }
            int mStringValueOffset = MultiStringValue.createValueVector(fbb, stringValueOffsets);
            mStringOffsets[i]  = MultiStringValue.createMultiStringValue(fbb, mStringValueOffset);
        }
        int mStringValueColumnOffset = MultiStringValueColumn.createValueVector(fbb, mStringOffsets);
        int mStringColumnOffset = MultiStringValueColumn.createMultiStringValueColumn(fbb, mStringValueColumnOffset);
        mMatchRecordValueOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.MultiStringValueColumn, mStringColumnOffset);

        // Multi Double
        int[] mDoubleOffsets = new int[kMaxColumnSize];
        for (int i = 0; i < kMaxColumnSize; i++) {
            double[] doubles = new double[kMaxColumnSize];
            for (int k = 0; k < kMaxColumnSize; k++) {
                doubles[k] = Float.MAX_VALUE - 1.0005;
            }
            int mDoubleOffset = MultiDoubleValue.createValueVector(fbb, doubles);
            mDoubleOffsets[i] = MultiDoubleValue.createMultiDoubleValue(fbb, mDoubleOffset);
        }
        int mDoubleValueColumnOffset = MultiDoubleValueColumn.createValueVector(fbb, mDoubleOffsets);
        int mDoubleColumnOffset = MultiDoubleValueColumn.createMultiDoubleValueColumn(fbb, mDoubleValueColumnOffset);
        mMatchRecordValueOffsets[index++] = FieldValueColumnTable.createFieldValueColumnTable(fbb,
                FieldValueColumn.MultiDoubleValueColumn, mDoubleColumnOffset);

        int mMatchRecordColumnsOffset = MatchRecords.createRecordColumnsVector(fbb, mMatchRecordValueOffsets);
        int mMatchRecordOffset = MatchRecords.createMatchRecords(fbb, fieldNamesOffset, mMatchRecordColumnsOffset);
        fbb.finish(mMatchRecordOffset);
        return fbb.dataBuffer();
    }

    @Test
    public void ErrorTest() {
        MatchRecords matchRecords = MatchRecords.getRootAsMatchRecords(createBasicTypeTestData());
        Map<String, Integer> fieldName2Index = new HashMap<String, Integer>();
        MatchRecord matchRecordFBByColumn = new MatchRecord(matchRecords,
                0,
                fieldName2Index);
        Assert.assertEquals(null, matchRecordFBByColumn.getInt("error"));
        Assert.assertEquals(null, matchRecordFBByColumn.getLong("error"));
        Assert.assertEquals(null, matchRecordFBByColumn.getFloat("error"));
        Assert.assertEquals(null, matchRecordFBByColumn.getDouble("error"));
        Assert.assertEquals(null, matchRecordFBByColumn.getString("error"));
        Assert.assertEquals(null, matchRecordFBByColumn.getFieldValue("error"));
        Assert.assertEquals(null, matchRecordFBByColumn.getFieldValue("string"));
    }

    @Test
    public void BasicTypeTest() {
        ByteBuffer buffer = createBasicTypeTestData();
        MatchRecords matchRecords = MatchRecords.getRootAsMatchRecords(buffer);

        Assert.assertEquals(this.fieldCount, matchRecords.fieldNameLength());
        Assert.assertEquals(this.fieldCount, matchRecords.recordColumnsLength());

        Map<String, Integer> fieldName2IndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < matchRecords.fieldNameLength(); i++) {
            fieldName2IndexMap.put(matchRecords.fieldName(i), i);
        }

        for (int i = 0; i < kMaxColumnSize; i++) {
            MatchRecord matchRecordFBByColumn = new MatchRecord(
                    matchRecords, i, fieldName2IndexMap);

            // int32
            String actualValue = matchRecordFBByColumn.getFieldValue("int32");
            if (0 == i%2) {
                Assert.assertEquals(Integer.toString(i), actualValue);
            } else {
                Assert.assertEquals(Integer.toString(-i), actualValue);
            }
            Assert.assertEquals(i, Math.abs(matchRecordFBByColumn.getInt("int32")));
            Assert.assertEquals(i, Math.abs(matchRecordFBByColumn.getLong("int32")));

            // string
            actualValue = matchRecordFBByColumn.getFieldValue("string");
            Assert.assertEquals("string_" + Integer.toString(i), actualValue);
            actualValue = matchRecordFBByColumn.getString("string");
            Assert.assertEquals("string_" + Integer.toString(i), actualValue);
            Assert.assertEquals("string_" + Integer.toString(i), matchRecordFBByColumn.getString("string", "error_charset"));

            // double
            actualValue = matchRecordFBByColumn.getFieldValue("double");
            double actualValueDouble = matchRecordFBByColumn.getDouble("double");
            if (0 == i%2) {
                Assert.assertEquals(Double.toString(Float.MAX_VALUE - 0.0015), actualValue);
                Assert.assertEquals(Float.MAX_VALUE - 0.0015, actualValueDouble, 0.00001);
            } else {
                Assert.assertEquals(Double.toString(0.0015 - Float.MAX_VALUE), actualValue);
                Assert.assertEquals(0.0015 - Float.MAX_VALUE, actualValueDouble, 0.00001);
            }
        }
    }

    @Test
    public void MultiTypeTest() {
        MatchRecords matchRecords = MatchRecords.getRootAsMatchRecords(createMultiTypeTestData());
        Assert.assertNotNull(matchRecords);
        Assert.assertEquals(fieldCount, matchRecords.fieldNameLength());
        Assert.assertEquals(fieldCount, matchRecords.recordColumnsLength());

        Map<String, Integer> fieldName2Index = new HashMap<String, Integer>();
        for (int i = 0; i < matchRecords.fieldNameLength(); i++) {
            fieldName2Index.put(matchRecords.fieldName(i), i);
        }

        for (int i = 0; i < kMaxColumnSize; i++) {
            String actualValue = null;
            String expectValue = null;
            MatchRecord mr = new MatchRecord(matchRecords, i , fieldName2Index);

            // Multi int32
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (int k = 0; k < kMaxColumnSize; k++) {
                    stringBuilder.append(Integer.MAX_VALUE);
                    stringBuilder.append(' ');
                }
                expectValue = stringBuilder.substring(0, stringBuilder.length() - 1);
                actualValue = mr.getFieldValue("mint32");
                Assert.assertEquals(expectValue, actualValue);

                List<Integer> actualValueList = mr.getIntList("mint32");
                Assert.assertEquals(kMaxColumnSize, actualValueList.size());
                for (int k = 0; k < kMaxColumnSize; k++) {
                    Assert.assertEquals(Integer.MAX_VALUE, (int)actualValueList.get(k));
                }
            }

            // Multi string
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (int k = 0; k < kMaxColumnSize; k++) {
                    stringBuilder.append("string_");
                    stringBuilder.append(k);
                    stringBuilder.append(' ');
                }
                expectValue = stringBuilder.substring(0, stringBuilder.length() - 1);
                actualValue = mr.getFieldValue("mstring");
                Assert.assertEquals(expectValue, actualValue);
                List<String> actualValueList = mr.getStringList("mstring");
                for (int k = 0; k < kMaxColumnSize; k++) {
                    Assert.assertEquals("string_" + Integer.toString(k), actualValueList.get(k));
                }
            }

            // Multi double
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (int k = 0; k < kMaxColumnSize; k++) {
                    stringBuilder.append(Float.MAX_VALUE - 1.0005);
                    stringBuilder.append(' ');
                }
                expectValue = stringBuilder.substring(0, stringBuilder.length() - 1);
                actualValue = mr.getFieldValue("mdouble");
                Assert.assertEquals(expectValue, actualValue);

                List<Double> actualDoubleList = mr.getDoubleList("mdouble");
                for (int k = 0; k < kMaxColumnSize; k++) {
                    Assert.assertEquals(Float.MAX_VALUE - 1.0005, actualDoubleList.get(k), 0.000001);
                }
            }
        }
    }
}
