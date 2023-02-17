package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.proto.gremlin_fb.FieldValueColumn;

/**
 * @author alibaba
 */
public enum FieldType {
    UNKNOWN(0),
    BOOL(1),
    INT8(2),
    INT16(3),
    INT32(4),
    INT64(5),
    UINT8(6),
    UINT16(7),
    UINT32(8),
    UINT64(9),
    FLOAT(10),
    DOUBLE(11),
    STRING(12),
    MULTIINT8(13),
    MULTIINT16(14),
    MULTIINT32(15),
    MULTIINT64(16),
    MULTIUINT8(17),
    MULTIUINT16(18),
    MULTIUINT32(19),
    MULTIUINT64(20),
    MULTIFLOAT(21),
    MULTIDOUBLE(22),
    MULTISTRING(23);

    byte value;

    FieldType(Integer value) {
        this.value = value.byteValue();
    }

    public static FieldType convertFieldType(byte fieldValueColumn) {
        FieldType fieldType = FieldType.UNKNOWN;
        switch (fieldValueColumn) {
            case FieldValueColumn.BoolValueColumn:
                fieldType = FieldType.BOOL;
                break;
            case FieldValueColumn.Int8ValueColumn:
                fieldType = FieldType.INT8;
                break;
            case FieldValueColumn.Int16ValueColumn:
                fieldType = FieldType.INT16;
                break;
            case FieldValueColumn.Int32ValueColumn:
                fieldType = FieldType.INT32;
                break;
            case FieldValueColumn.Int64ValueColumn:
                fieldType = FieldType.INT64;
                break;
            case FieldValueColumn.UInt8ValueColumn:
                fieldType = FieldType.UINT8;
                break;
            case FieldValueColumn.UInt16ValueColumn:
                fieldType = FieldType.UINT16;
                break;
            case FieldValueColumn.UInt32ValueColumn:
                fieldType = FieldType.UINT32;
                break;
            case FieldValueColumn.UInt64ValueColumn:
                fieldType = FieldType.UINT64;
                break;
            case FieldValueColumn.FloatValueColumn:
                fieldType = FieldType.FLOAT;
                break;
            case FieldValueColumn.DoubleValueColumn:
                fieldType = FieldType.DOUBLE;
                break;
            case FieldValueColumn.StringValueColumn:
                fieldType = FieldType.STRING;
                break;
            case FieldValueColumn.MultiInt8ValueColumn:
                fieldType = FieldType.MULTIINT8;
                break;
            case FieldValueColumn.MultiInt16ValueColumn:
                fieldType = FieldType.MULTIINT16;
                break;
            case FieldValueColumn.MultiInt32ValueColumn:
                fieldType = FieldType.MULTIINT32;
                break;
            case FieldValueColumn.MultiInt64ValueColumn:
                fieldType = FieldType.MULTIINT64;
                break;
            case FieldValueColumn.MultiUInt8ValueColumn:
                fieldType = FieldType.MULTIUINT8;
                break;
            case FieldValueColumn.MultiUInt16ValueColumn:
                fieldType = FieldType.MULTIUINT16;
                break;
            case FieldValueColumn.MultiUInt32ValueColumn:
                fieldType = FieldType.MULTIUINT32;
                break;
            case FieldValueColumn.MultiUInt64ValueColumn:
                fieldType = FieldType.MULTIUINT64;
                break;
            case FieldValueColumn.MultiFloatValueColumn:
                fieldType = FieldType.MULTIFLOAT;
                break;
            case FieldValueColumn.MultiDoubleValueColumn:
                fieldType = FieldType.MULTIDOUBLE;
                break;
            case FieldValueColumn.MultiStringValueColumn:
                fieldType = FieldType.MULTISTRING;
            default:
                break;
        }
        return fieldType;
    }
}
