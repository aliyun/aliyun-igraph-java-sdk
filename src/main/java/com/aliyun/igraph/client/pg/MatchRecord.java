package com.aliyun.igraph.client.pg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.aliyun.igraph.client.proto.pg_fb.*;
import lombok.NonNull;

/**
 * @author alibaba
 */
public class MatchRecord {
    protected Map<String, Integer> fieldName2IndexMap;
    protected int fieldValueCount;
    static String pbMultiValueSeparator = " ";
    private MatchRecords fbByColumnMatchRecords;
    private int MatchRecordRowCursor;

    public MatchRecord(@NonNull MatchRecords fbByColumnMatchRecords,
                          int MatchRecordRowCursor,
                          Map<String, Integer> fieldName2IndexMap) {
        this.fieldName2IndexMap = fieldName2IndexMap;
        this.fbByColumnMatchRecords = fbByColumnMatchRecords;
        this.MatchRecordRowCursor = MatchRecordRowCursor;
        this.fieldValueCount = this.fbByColumnMatchRecords.fieldNameLength();
    }

    /**
     * 根据字段名，查询字段值
     * 
     * @param fieldName 字段名
     * @return 若包含字段值时，返回字段值；否则返回null
     */
    public String getFieldValue(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getFieldValue(fieldIndex);
    }

    /**
     * 根据字段索引，查询字段值
     * 推荐使用这个方法来获取字段值，避免不必要的根据字段名查询字段序号的过程
     *
     * @param fieldIndex 字段索引
     * @return 若包含字段值时，返回字段值；否则返回null
     */
    public String getFieldValue(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        String fieldValue = null;
        switch(fbByColumnMatchRecords.recordColumns(fieldIndex).fieldValueColumnType()) {
            case FieldValueColumn.Int8ValueColumn:
            case FieldValueColumn.Int16ValueColumn:
            case FieldValueColumn.Int32ValueColumn:
            case FieldValueColumn.Int64ValueColumn:
            case FieldValueColumn.UInt8ValueColumn:
            case FieldValueColumn.UInt16ValueColumn:
            case FieldValueColumn.UInt32ValueColumn:
            case FieldValueColumn.UInt64ValueColumn: {
                Long value = getLong(fieldIndex);
                fieldValue = null == value ? null : value.toString();
                break;
            }
            case FieldValueColumn.FloatValueColumn:
            case FieldValueColumn.DoubleValueColumn: {
                Double value = getDouble(fieldIndex);
                fieldValue = null == value ? null : value.toString();
                break;
            }
            case FieldValueColumn.StringValueColumn:
                fieldValue = getString(fieldIndex, "utf-8");
                break;
            case FieldValueColumn.MultiInt8ValueColumn:
            case FieldValueColumn.MultiInt16ValueColumn:
            case FieldValueColumn.MultiInt32ValueColumn:
            case FieldValueColumn.MultiInt64ValueColumn:
            case FieldValueColumn.MultiUInt8ValueColumn:
            case FieldValueColumn.MultiUInt16ValueColumn:
            case FieldValueColumn.MultiUInt32ValueColumn:
            case FieldValueColumn.MultiUInt64ValueColumn:
                fieldValue = MultiTypeValueWrapper(getLongList(fieldIndex));
                break;
            case FieldValueColumn.MultiFloatValueColumn:
            case FieldValueColumn.MultiDoubleValueColumn:
                fieldValue = MultiTypeValueWrapper(getDoubleList(fieldIndex));
                break;
            case FieldValueColumn.MultiStringValueColumn:
                fieldValue = MultiTypeValueWrapper(getStringList(fieldIndex));
            default:
                break;
        }
        return fieldValue;
    }

    /**
     * 根据字段名，查询字段值
     *
     * @param fieldName 字段名
     * @return 若包含字段值时，返回字段值；否则返回null
     */
    public byte[] getFieldValueInBytes(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getFieldValueInBytes(fieldIndex);
    }

    /**
     * 根据字段索引，查询字段值
     * 推荐使用这个方法来获取字段值，避免不必要的根据字段名查询字段序号的过程
     *
     * @param fieldIndex 字段索引
     * @return 若包含字段值时，返回字段值；否则返回null
     */
    public byte[] getFieldValueInBytes(int fieldIndex) {
        String fieldValue = getFieldValue(fieldIndex);
        if (fieldValue == null) {
            return null;
        }
        return fieldValue.getBytes();
    }

    /**
     * 根据字段名，查询字段值
     *
     * @param fieldName 字段名
     * @param charset 编码类型
     * @return 若包含字段值时，返回字段值；否则返回null
     */
    public byte[] getFieldValueInBytes(@NonNull String fieldName, String charset) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getFieldValueInBytes(fieldIndex, charset);
    }

    /**
     * 根据字段索引，查询字段值
     * 推荐使用这个方法来获取字段值，避免不必要的根据字段名查询字段序号的过程
     *
     * @param fieldIndex 字段号
     * @param charset 编码类型
     * @return 若包含字段值时，返回字段值；否则返回null
     */
    public byte[] getFieldValueInBytes(int fieldIndex, String charset) {
        String fieldValue = getFieldValue(fieldIndex);
        try {
            return fieldValue != null ? fieldValue.getBytes(charset) : null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /** @return 字段数量 */
    public int getFieldValueCount() {
        return fieldValueCount;
    }

    /** @return 字段名映射索引Map */
    public Map<String, Integer> getFieldName2IndexMap() {
        return fieldName2IndexMap;
    }

    /**
     * 根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Integer getInt(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getInt(fieldIndex);
    }

    /**
     * 根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Integer getInt(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        Integer fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.Int8ValueColumn:
                Int8ValueColumn int8ValueColumn = new Int8ValueColumn();
                if (null != columnTable.fieldValueColumn(int8ValueColumn)) {
                    fieldValue = (int) int8ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int16ValueColumn:
                Int16ValueColumn int16ValueColumn = new Int16ValueColumn();
                if (null != columnTable.fieldValueColumn(int16ValueColumn)) {
                    fieldValue = (int) int16ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int32ValueColumn:
                Int32ValueColumn int32ValueColumn = new Int32ValueColumn();
                if (null != columnTable.fieldValueColumn(int32ValueColumn)) {
                    fieldValue = int32ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int64ValueColumn:
                Int64ValueColumn int64ValueColumn = new Int64ValueColumn();
                if (null != columnTable.fieldValueColumn(int64ValueColumn)) {
                    Long longValue = int64ValueColumn.value(MatchRecordRowCursor);
                    fieldValue = longValue != null ? longValue.intValue() : null;
                }
                break;
            case FieldValueColumn.UInt8ValueColumn:
                UInt8ValueColumn uint8ValueColumn = new UInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(uint8ValueColumn)) {
                    fieldValue = uint8ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt16ValueColumn:
                UInt16ValueColumn uint16ValueColumn = new UInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(uint16ValueColumn)) {
                    fieldValue = uint16ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt32ValueColumn:
                UInt32ValueColumn uint32ValueColumn = new UInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(uint32ValueColumn)) {
                    Long longValue = uint32ValueColumn.value(MatchRecordRowCursor);
                    fieldValue = longValue != null ? longValue.intValue() : null;
                }
                break;
            case FieldValueColumn.UInt64ValueColumn:
                UInt64ValueColumn uint64ValueColumn = new UInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(uint64ValueColumn)) {
                    Long longValue = uint64ValueColumn.value(MatchRecordRowCursor);
                    fieldValue = longValue != null ? longValue.intValue() : null;
                }
                break;
            case FieldValueColumn.StringValueColumn:
                try {
                    fieldValue = Integer.valueOf(getString(fieldIndex, "utf-8"));
                } catch (Exception e) {
                    return null;
                }
                break;
            default:
                break;
        }
        return fieldValue;
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Long getLong(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getLong(fieldIndex);
    }

    /**
     *根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Long getLong(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        Long fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.Int8ValueColumn:
                Int8ValueColumn int8ValueColumn = new Int8ValueColumn();
                if (null != columnTable.fieldValueColumn(int8ValueColumn)) {
                    fieldValue = (long) int8ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int16ValueColumn:
                Int16ValueColumn int16ValueColumn = new Int16ValueColumn();
                if (null != columnTable.fieldValueColumn(int16ValueColumn)) {
                    fieldValue = (long) int16ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int32ValueColumn:
                Int32ValueColumn int32ValueColumn = new Int32ValueColumn();
                if (null != columnTable.fieldValueColumn(int32ValueColumn)) {
                    fieldValue = (long) int32ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int64ValueColumn:
                Int64ValueColumn int64ValueColumn = new Int64ValueColumn();
                if (null != columnTable.fieldValueColumn(int64ValueColumn)) {
                    fieldValue = int64ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt8ValueColumn:
                UInt8ValueColumn uint8ValueColumn = new UInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(uint8ValueColumn)) {
                    fieldValue = (long) uint8ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt16ValueColumn:
                UInt16ValueColumn uint16ValueColumn = new UInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(uint16ValueColumn)) {
                    fieldValue = (long) uint16ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt32ValueColumn:
                UInt32ValueColumn uint32ValueColumn = new UInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(uint32ValueColumn)) {
                    fieldValue = uint32ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt64ValueColumn:
                UInt64ValueColumn uint64ValueColumn = new UInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(uint64ValueColumn)) {
                    fieldValue = uint64ValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.StringValueColumn:
                String stringValue = getString(fieldIndex, "utf-8");
                if (stringValue != null) {
                    try {
                        fieldValue = Long.valueOf(stringValue);
                    } catch (Exception e) {
                        return null;
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue;
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Float getFloat(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getFloat(fieldIndex);
    }

    /**
     *根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Float getFloat(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        Float fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.Int8ValueColumn:
            case FieldValueColumn.Int16ValueColumn:
            case FieldValueColumn.Int32ValueColumn:
            case FieldValueColumn.Int64ValueColumn:
            case FieldValueColumn.UInt8ValueColumn:
            case FieldValueColumn.UInt16ValueColumn:
            case FieldValueColumn.UInt32ValueColumn:
            case FieldValueColumn.UInt64ValueColumn:
                Long value = getLong(fieldIndex);
                if (null != value) {
                    fieldValue = value.floatValue();
                }
                break;
            case FieldValueColumn.FloatValueColumn:
                FloatValueColumn floatValueColumn = new FloatValueColumn();
                if (null != columnTable.fieldValueColumn(floatValueColumn)) {
                    fieldValue = floatValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.DoubleValueColumn:
                DoubleValueColumn doubleValueColumn = new DoubleValueColumn();
                if (null != columnTable.fieldValueColumn(doubleValueColumn)) {
                    Double doubleValue = doubleValueColumn.value(MatchRecordRowCursor);
                    fieldValue = doubleValue != null ? doubleValue.floatValue() : null;
                }
                break;
            case FieldValueColumn.StringValueColumn:
                String stringValue = getString(fieldIndex, "utf-8");
                if (null != stringValue) {
                    try {
                        fieldValue = Float.valueOf(stringValue);
                    } catch (Exception e) {
                        return null;
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue;
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Double getDouble(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getDouble(fieldIndex);
    }

    /**
     *根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Double getDouble(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        Double fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.Int8ValueColumn:
            case FieldValueColumn.Int16ValueColumn:
            case FieldValueColumn.Int32ValueColumn:
            case FieldValueColumn.Int64ValueColumn:
            case FieldValueColumn.UInt8ValueColumn:
            case FieldValueColumn.UInt16ValueColumn:
            case FieldValueColumn.UInt32ValueColumn:
            case FieldValueColumn.UInt64ValueColumn:
                Long value = getLong(fieldIndex);
                if (null != value) {
                    fieldValue = value.doubleValue();
                }
                break;
            case FieldValueColumn.FloatValueColumn:
                FloatValueColumn floatValueColumn = new FloatValueColumn();
                if (null != columnTable.fieldValueColumn(floatValueColumn)) {
                    fieldValue = (double) floatValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.DoubleValueColumn:
                DoubleValueColumn doubleValueColumn = new DoubleValueColumn();
                if (null != columnTable.fieldValueColumn(doubleValueColumn)) {
                    fieldValue = doubleValueColumn.value(MatchRecordRowCursor);
                }
                break;
            case FieldValueColumn.StringValueColumn:
                String stringValue = getString(fieldIndex, "utf-8");
                if (null != stringValue) {
                    try {
                        fieldValue = Double.valueOf(stringValue);
                    } catch (Exception e) {
                        return null;
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue;
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Boolean getBoolean(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getBoolean(fieldIndex);
    }

    /**
     *根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @return 若解析失败或者类型不匹配则返回null
     */
    public Boolean getBoolean(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        Boolean fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.BoolValueColumn:
                BoolValueColumn boolValueColumn = new BoolValueColumn();
                if (null != columnTable.fieldValueColumn(boolValueColumn)) {
                    fieldValue = boolValueColumn.value(MatchRecordRowCursor);
                }
                break;
            default:
                break;
        }
        return fieldValue;
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return String
     */
    public String getString(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getString(fieldIndex, "utf-8");
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @param charset utf-8 or gbk
     * @return String
     */
    public String getString(@NonNull String fieldName, @NonNull Charset charset) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getString(fieldIndex, charset);
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @param charset utf-8 or gbk
     * @return String
     */
    public String getString(@NonNull String fieldName, @NonNull String charset) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getString(fieldIndex, charset);
    }

    /**
     *根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @return String
     */
    public String getString(int fieldIndex) {
        return getString(fieldIndex, "utf-8");
    }

    /**
     *根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @param charset utf-8 or gbk
     * @return String
     */
    public String getString(int fieldIndex, @NonNull Charset charset) {
        return getString(fieldIndex, charset.name());
    }

    /**
     *根据字段索引返回相应类型的值
     * @param fieldIndex 字段索引
     * @param charset utf-8 or gbk
     * @return String
     */
    public String getString(int fieldIndex, @NonNull String charset) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.StringValueColumn:
                StringValueColumn stringValueColumn = new StringValueColumn();
                if (null != columnTable.fieldValueColumn(stringValueColumn)) {
                    byte[] arr = new byte[stringValueColumn.valueAsByteBuffer(MatchRecordRowCursor).remaining()];
                    stringValueColumn.valueAsByteBuffer(MatchRecordRowCursor).get(arr);
                    try {
                        return new String(arr, charset);
                    } catch (UnsupportedEncodingException e) {
                        return stringValueColumn.value(MatchRecordRowCursor);
                    }
                }
                break;
            case FieldValueColumn.Int8ValueColumn:
            case FieldValueColumn.Int16ValueColumn:
            case FieldValueColumn.Int32ValueColumn:
            case FieldValueColumn.Int64ValueColumn:
            case FieldValueColumn.UInt8ValueColumn:
            case FieldValueColumn.UInt16ValueColumn:
            case FieldValueColumn.UInt32ValueColumn:
            case FieldValueColumn.UInt64ValueColumn:
                Long longValue = getLong(fieldIndex);
                if (null != longValue) {
                    return longValue.toString();
                }
            case FieldValueColumn.FloatValueColumn:
            case FieldValueColumn.DoubleValueColumn:
                Double doubleValue = getDouble(fieldIndex);
                if (null != doubleValue) {
                    return doubleValue.toString();
                }
            default:
                break;
        }
        return null;
    }

    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldName 字段名
     * @return List of Integer
     */
    public List<Integer> getIntList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getIntList(fieldIndex);
    }

    /**
     * 根据字段索引返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List of Integer
     */
    public List<Integer> getIntList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        List<Integer> fieldValue = new ArrayList<>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiUInt8ValueColumn:
                MultiUInt8ValueColumn mUInt8ValueColumn = new MultiUInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt8ValueColumn)) {
                    MultiUInt8Value multiUInt8Value = mUInt8ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt8Value) {
                        for (int i = 0; i < multiUInt8Value.valueLength(); ++i) {
                            fieldValue.add(multiUInt8Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiUInt16ValueColumn:
                MultiUInt16ValueColumn mUInt16ValueColumn = new MultiUInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt16ValueColumn)) {
                    MultiUInt16Value multiUInt16Value = mUInt16ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt16Value) {
                        for (int i = 0; i < multiUInt16Value.valueLength(); ++i) {
                            fieldValue.add(multiUInt16Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiUInt32ValueColumn:
                MultiUInt32ValueColumn mUInt32ValueColumn = new MultiUInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt32ValueColumn)) {
                    MultiUInt32Value multiUInt32Value = mUInt32ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt32Value) {
                        for (int i = 0; i < multiUInt32Value.valueLength(); ++i) {
                            fieldValue.add((int)multiUInt32Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiUInt64ValueColumn:
                MultiUInt64ValueColumn mUInt64ValueColumn = new MultiUInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt64ValueColumn)) {
                    MultiUInt64Value multiUInt64Value = mUInt64ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt64Value) {
                        for (int i = 0; i < multiUInt64Value.valueLength(); ++i) {
                            fieldValue.add((int)multiUInt64Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt8ValueColumn:
                MultiInt8ValueColumn mInt8ValueColumn = new MultiInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt8ValueColumn)) {
                    MultiInt8Value multiInt8Value = mInt8ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt8Value) {
                        for (int i = 0; i < multiInt8Value.valueLength(); ++i) {
                            fieldValue.add((int)multiInt8Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt16ValueColumn:
                MultiInt16ValueColumn mInt16ValueColumn = new MultiInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt16ValueColumn)) {
                    MultiInt16Value multiInt16Value = mInt16ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt16Value) {
                        for (int i = 0; i < multiInt16Value.valueLength(); ++i) {
                            fieldValue.add((int)multiInt16Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt32ValueColumn:
                MultiInt32ValueColumn mInt32ValueColumn = new MultiInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt32ValueColumn)) {
                    MultiInt32Value multiInt32Value = mInt32ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt32Value) {
                        for (int i = 0; i < multiInt32Value.valueLength(); ++i) {
                            fieldValue.add(multiInt32Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt64ValueColumn:
                MultiInt64ValueColumn mInt64ValueColumn = new MultiInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt64ValueColumn)) {
                    MultiInt64Value multiInt64Value = mInt64ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt64Value) {
                        for (int i = 0; i < multiInt64Value.valueLength(); ++i) {
                            fieldValue.add((int)multiInt64Value.value(i));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue.isEmpty() ? null : fieldValue;
    }

    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldName 字段名
     * @return List of Long
     */
    public List<Long> getLongList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getLongList(fieldIndex);
    }

    /**
     * 根据字段索引返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List of Long
     */
    public List<Long> getLongList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        List<Long> fieldValue = new ArrayList<>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiUInt8ValueColumn:
                MultiUInt8ValueColumn mUInt8ValueColumn = new MultiUInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt8ValueColumn)) {
                    MultiUInt8Value multiUInt8Value = mUInt8ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt8Value) {
                        for (int i = 0; i < multiUInt8Value.valueLength(); ++i) {
                            fieldValue.add((long)multiUInt8Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiUInt16ValueColumn:
                MultiUInt16ValueColumn mUInt16ValueColumn = new MultiUInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt16ValueColumn)) {
                    MultiUInt16Value multiUInt16Value = mUInt16ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt16Value) {
                        for (int i = 0; i < multiUInt16Value.valueLength(); ++i) {
                            fieldValue.add((long)multiUInt16Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiUInt32ValueColumn:
                MultiUInt32ValueColumn mUInt32ValueColumn = new MultiUInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt32ValueColumn)) {
                    MultiUInt32Value multiUInt32Value = mUInt32ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt32Value) {
                        for (int i = 0; i < multiUInt32Value.valueLength(); ++i) {
                            fieldValue.add(multiUInt32Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiUInt64ValueColumn:
                MultiUInt64ValueColumn mUInt64ValueColumn = new MultiUInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt64ValueColumn)) {
                    MultiUInt64Value multiUInt64Value = mUInt64ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiUInt64Value) {
                        for (int i = 0; i < multiUInt64Value.valueLength(); ++i) {
                            fieldValue.add(multiUInt64Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt8ValueColumn:
                MultiInt8ValueColumn mInt8ValueColumn = new MultiInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt8ValueColumn)) {
                    MultiInt8Value multiInt8Value = mInt8ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt8Value) {
                        for (int i = 0; i < multiInt8Value.valueLength(); ++i) {
                            fieldValue.add((long)multiInt8Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt16ValueColumn:
                MultiInt16ValueColumn mInt16ValueColumn = new MultiInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt16ValueColumn)) {
                    MultiInt16Value multiInt16Value = mInt16ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt16Value) {
                        for (int i = 0; i < multiInt16Value.valueLength(); ++i) {
                            fieldValue.add((long)multiInt16Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt32ValueColumn:
                MultiInt32ValueColumn mInt32ValueColumn = new MultiInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt32ValueColumn)) {
                    MultiInt32Value multiInt32Value = mInt32ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt32Value) {
                        for (int i = 0; i < multiInt32Value.valueLength(); ++i) {
                            fieldValue.add((long)multiInt32Value.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiInt64ValueColumn:
                MultiInt64ValueColumn mInt64ValueColumn = new MultiInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(mInt64ValueColumn)) {
                    MultiInt64Value multiInt64Value = mInt64ValueColumn.value(MatchRecordRowCursor);
                    if (null != multiInt64Value) {
                        for (int i = 0; i < multiInt64Value.valueLength(); ++i) {
                            fieldValue.add(multiInt64Value.value(i));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue.isEmpty() ? null : fieldValue;
    }

    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldName 字段索引
     * @return List of Float, 如果解析失败或者没有值,返回null
     */
    public List<Float> getFloatList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getFloatList(fieldIndex);
    }

    /**
     * 根据字段索引返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List of Float, 如果解析失败或者没有值,返回null
     */
    public List<Float> getFloatList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }
        List<Float> fieldValue = new ArrayList<>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiFloatValueColumn:
                MultiFloatValueColumn multiFloatValueColumn = new MultiFloatValueColumn();
                if (null != columnTable.fieldValueColumn(multiFloatValueColumn)) {
                    MultiFloatValue multiFloatValue = multiFloatValueColumn.value(MatchRecordRowCursor);
                    if (null != multiFloatValue) {
                        for (int i = 0; i < multiFloatValue.valueLength(); ++i) {
                            fieldValue.add(multiFloatValue.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiDoubleValueColumn:
                MultiDoubleValueColumn multiDoubleValueColumn = new MultiDoubleValueColumn();
                if (null != columnTable.fieldValueColumn(multiDoubleValueColumn)) {
                    MultiDoubleValue multiDoubleValue = multiDoubleValueColumn.value(MatchRecordRowCursor);
                    if (null != multiDoubleValue) {
                        for (int i = 0; i < multiDoubleValue.valueLength(); ++i) {
                            fieldValue.add((float)multiDoubleValue.value(i));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue.isEmpty() ? null : fieldValue;
    }

    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldName 字段名
     * @return List of Double, 如果解析失败或者没有值,返回null
     */
    public List<Double> getDoubleList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getDoubleList(fieldIndex);
    }

    /**
     * 根据字段索引返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List of Double, 如果解析失败或者没有值,返回null
     */
    public List<Double> getDoubleList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }
        List<Double> fieldValue = new ArrayList<Double>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiFloatValueColumn:
                MultiFloatValueColumn multiFloatValueColumn = new MultiFloatValueColumn();
                if (null != columnTable.fieldValueColumn(multiFloatValueColumn)) {
                    MultiFloatValue multiFloatValue = multiFloatValueColumn.value(MatchRecordRowCursor);
                    if (null != multiFloatValue) {
                        for (int i = 0; i < multiFloatValue.valueLength(); ++i) {
                            fieldValue.add((double)multiFloatValue.value(i));
                        }
                    }
                }
                break;
            case FieldValueColumn.MultiDoubleValueColumn:
                MultiDoubleValueColumn multiDoubleValueColumn = new MultiDoubleValueColumn();
                if (null != columnTable.fieldValueColumn(multiDoubleValueColumn)) {
                    MultiDoubleValue multiDoubleValue = multiDoubleValueColumn.value(MatchRecordRowCursor);
                    if (null != multiDoubleValue) {
                        for (int i = 0; i < multiDoubleValue.valueLength(); ++i) {
                            fieldValue.add(multiDoubleValue.value(i));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue.isEmpty() ? null : fieldValue;
    }

    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldName 字段名
     * @return List of String, 如果解析失败或者没有值,返回null
     */
    public List<String> getStringList(String fieldName) {
        Integer index = fieldName2IndexMap.get(fieldName);
        if (null == index) {
            return null;
        }
        return getStringList(index);
    }

    /**
     * 根据字段索引返回相应类型的值的List
     * @param fieldIndex 字段名
     * @return List of String, 如果解析失败或者没有值,返回null
     */
    public List<String> getStringList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        List<String> fieldValue = new ArrayList<String>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiStringValueColumn:
                MultiStringValueColumn multiStringValueColumn = new MultiStringValueColumn();
                if (null != columnTable.fieldValueColumn(multiStringValueColumn)) {
                    MultiStringValue multiStringValue = multiStringValueColumn.value(MatchRecordRowCursor);
                    if (null != multiStringValue) {
                        for (int i = 0; i < multiStringValue.valueLength(); ++i) {
                            fieldValue.add(multiStringValue.value(i));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return fieldValue.isEmpty() ? null : fieldValue;
    }

    private <E> String MultiTypeValueWrapper(List<E> fieldValue) {
        if (fieldValue == null)
            return null;
        StringBuilder sb = new StringBuilder(128);
        for (E e : fieldValue) {
            sb.append(e.toString());
            sb.append(pbMultiValueSeparator);
        }
        return sb.substring(0, sb.length()-1);
    }
}
