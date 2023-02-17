package com.aliyun.igraph.client.pg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.igraph.client.proto.pg_fb.FieldValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiInt16ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiInt8ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiUInt16ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.DoubleValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.FieldValueColumnTable;
import com.aliyun.igraph.client.proto.pg_fb.FloatValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.Int16ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.Int32ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.Int64ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.Int8ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MatchRecords;
import com.aliyun.igraph.client.proto.pg_fb.MultiDoubleValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiFloatValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiInt32ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiInt64ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiStringValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiUInt32ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiUInt64ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.MultiUInt8ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.StringValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.UInt16ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.UInt32ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.UInt64ValueColumn;
import com.aliyun.igraph.client.proto.pg_fb.UInt8ValueColumn;

/**
 * @author alibaba
 */
public class SingleQueryResultFBByColumn extends SingleQueryResult {
    private MatchRecords fbByColumnMatchRecords;

    public MatchRecords getFbByColumnMatchRecords() {
        return fbByColumnMatchRecords;
    }

    private List<Map<String, String>> convertFbMatchRecords() {
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        int size = fieldNames.size();
        for (MatchRecord matchRecord : matchRecords) {
            if (matchRecord != null) {
                Map<String, String> row = new HashMap<String, String>(size);
                rows.add(row);
                for (int i = 0; i < size; i++) {
                    row.put(fieldNames.get(i), matchRecord.getFieldValue(fieldNames.get(i)));
                }
            }
        }
        return rows;
    }

    public void setResult(MatchRecords fbByColumnMatchRecords) {
        this.fbByColumnMatchRecords = fbByColumnMatchRecords;
        decode();
    }

    private void decode() {
        fieldName2IndexMap = new HashMap<String, Integer>();
        fieldNames = new ArrayList<String>();
        matchRecords = new ArrayList<MatchRecord>();

        if (fbByColumnMatchRecords == null ||
                fbByColumnMatchRecords.recordColumnsLength() == 0 ||
                fbByColumnMatchRecords.fieldNameLength() == 0) {
            return;
        }

        for (int i = 0; i < fbByColumnMatchRecords.fieldNameLength(); ++i) {
            fieldNames.add(fbByColumnMatchRecords.fieldName(i));
            fieldName2IndexMap.put(fbByColumnMatchRecords.fieldName(i), i);
        }

        int columnLength = 0;
        FieldValueColumnTable column0 = fbByColumnMatchRecords.recordColumns(0);
        switch (column0.fieldValueColumnType()) {
            case FieldValueColumn.Int8ValueColumn:
                Int8ValueColumn int8ValueColumn = new Int8ValueColumn();
                if (null != column0.fieldValueColumn(int8ValueColumn)) {
                    columnLength = int8ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.Int16ValueColumn:
                Int16ValueColumn int16ValueColumn = new Int16ValueColumn();
                if (null != column0.fieldValueColumn(int16ValueColumn)) {
                    columnLength = int16ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.Int32ValueColumn:
                Int32ValueColumn int32ValueColumn = new Int32ValueColumn();
                if (null != column0.fieldValueColumn(int32ValueColumn)) {
                    columnLength = int32ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.Int64ValueColumn:
                Int64ValueColumn int64ValueColumn = new Int64ValueColumn();
                if (null != column0.fieldValueColumn(int64ValueColumn)) {
                    columnLength = int64ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.UInt8ValueColumn:
                UInt8ValueColumn uint8ValueColumn = new UInt8ValueColumn();
                if (null != column0.fieldValueColumn(uint8ValueColumn)) {
                    columnLength = uint8ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.UInt16ValueColumn:
                UInt16ValueColumn uint16ValueColumn = new UInt16ValueColumn();
                if (null != column0.fieldValueColumn(uint16ValueColumn)) {
                    columnLength = uint16ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.UInt32ValueColumn:
                UInt32ValueColumn uint32ValueColumn = new UInt32ValueColumn();
                if (null != column0.fieldValueColumn(uint32ValueColumn)) {
                    columnLength = uint32ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.UInt64ValueColumn:
                UInt64ValueColumn uint64ValueColumn = new UInt64ValueColumn();
                if (null != column0.fieldValueColumn(uint64ValueColumn)) {
                    columnLength = uint64ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.FloatValueColumn:
                FloatValueColumn floatValueColumn = new FloatValueColumn();
                if (null != column0.fieldValueColumn(floatValueColumn)) {
                    columnLength = floatValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.DoubleValueColumn:
                DoubleValueColumn doubleValueColumn = new DoubleValueColumn();
                if (null != column0.fieldValueColumn(doubleValueColumn)) {
                    columnLength = doubleValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.StringValueColumn:
                StringValueColumn stringValueColumn = new StringValueColumn();
                if (null != column0.fieldValueColumn(stringValueColumn)) {
                    columnLength = stringValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiInt8ValueColumn:
                MultiInt8ValueColumn mint8ValueColumn = new MultiInt8ValueColumn();
                if (null != column0.fieldValueColumn(mint8ValueColumn)) {
                    columnLength = mint8ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiInt16ValueColumn:
                MultiInt16ValueColumn mint16ValueColumn = new MultiInt16ValueColumn();
                if (null != column0.fieldValueColumn(mint16ValueColumn)) {
                    columnLength = mint16ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiInt32ValueColumn:
                MultiInt32ValueColumn mint32ValueColumn = new MultiInt32ValueColumn();
                if (null != column0.fieldValueColumn(mint32ValueColumn)) {
                    columnLength = mint32ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiInt64ValueColumn:
                MultiInt64ValueColumn mint64ValueColumn = new MultiInt64ValueColumn();
                if (null != column0.fieldValueColumn(mint64ValueColumn)) {
                    columnLength = mint64ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiUInt8ValueColumn:
                MultiUInt8ValueColumn muint8ValueColumn = new MultiUInt8ValueColumn();
                if (null != column0.fieldValueColumn(muint8ValueColumn)) {
                    columnLength = muint8ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiUInt16ValueColumn:
                MultiUInt16ValueColumn muint16ValueColumn = new MultiUInt16ValueColumn();
                if (null != column0.fieldValueColumn(muint16ValueColumn)) {
                    columnLength = muint16ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiUInt32ValueColumn:
                MultiUInt32ValueColumn muint32ValueColumn = new MultiUInt32ValueColumn();
                if (null != column0.fieldValueColumn(muint32ValueColumn)) {
                    columnLength = muint32ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiUInt64ValueColumn:
                MultiUInt64ValueColumn muint64ValueColumn = new MultiUInt64ValueColumn();
                if (null != column0.fieldValueColumn(muint64ValueColumn)) {
                    columnLength = muint64ValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiFloatValueColumn:
                MultiFloatValueColumn mfloatValueColumn = new MultiFloatValueColumn();
                if (null != column0.fieldValueColumn(mfloatValueColumn)) {
                    columnLength = mfloatValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiDoubleValueColumn:
                MultiDoubleValueColumn mdoubleValueColumn = new MultiDoubleValueColumn();
                if (null != column0.fieldValueColumn(mdoubleValueColumn)) {
                    columnLength = mdoubleValueColumn.valueLength();
                }
                break;
            case FieldValueColumn.MultiStringValueColumn:
                MultiStringValueColumn mstringValueColumn = new MultiStringValueColumn();
                if (null != column0.fieldValueColumn(mstringValueColumn)) {
                    columnLength = mstringValueColumn.valueLength();
                }
                break;
            default:
                break;
        }

        /**
         * tpp 图化项目直接使用了 原始的 match record 这一层转换不需要了
         */
        for (int i = 0; i < columnLength; i++) {
            MatchRecord matchRecord = new MatchRecord(fbByColumnMatchRecords, i, fieldName2IndexMap);
            matchRecords.add(matchRecord);
        }
    }
}

