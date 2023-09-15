package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.proto.gremlin_fb.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotSupportedException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author alibaba
 */
public abstract class IGraphElement implements Element {
    public static final Charset utf_8 = Charset.forName("utf-8");
    private static final Logger log = LoggerFactory.getLogger(IGraphElement.class);
    protected ElementMeta elementMeta;
    protected MatchRecords fbByColumnMatchRecords;
    protected int matchRecordRowCursor;
    protected Map<String, Integer> fieldName2IndexMap;
    protected int fieldValueCount;

    public IGraphElement() {}

    public IGraphElement(@NonNull ElementMeta elementMeta, int cursor) {
        this.matchRecordRowCursor = cursor;
        this.elementMeta = elementMeta;
        this.fbByColumnMatchRecords = elementMeta.matchRecords;
        this.fieldName2IndexMap = elementMeta.fieldName2index;
        this.fieldValueCount = elementMeta.fieldNames.size();
    }

    protected List<String> getFieldNames() {
        return elementMeta.fieldNames;
    }

    protected List<FieldType> getFieldTypes() { return elementMeta.fieldTypes; }

    @Override
    public Object id() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public String label() {
        return elementMeta.label;
    }

    @Override
    public Graph graph() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Set<String> keys() {
        return new HashSet<>(getFieldNames());
    }

    @Override
    public <V> Property<V> property(String key) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> V value(String key) throws NoSuchElementException {
        Object value = getObject(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return (V)value;
    }

    @Override
    public void remove() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> Iterator<V> values(String... propertyKeys) {
        List<V> valueList = new ArrayList<>();
        for (String propertyKey : propertyKeys) {
            valueList.add(value(propertyKey));
        }
        return valueList.iterator();
    }

    @Override
    public <V> Iterator<? extends Property<V>> properties(String... propertyKeys) {
        throw new NotSupportedException("method is not supported");
    }

    /**
     * 根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return 若解析失败或者类型不匹配则返回null
     */
    private Boolean getBoolean(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getBoolean(fieldIndex);
    }
    /**
     * 根据字段名返回相应类型的值
     * @param fieldIndex 字段索引
     * @return 若解析失败或者类型不匹配则返回null
     */
    private Boolean getBoolean(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        Boolean fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        if (FieldValueColumn.BoolValueColumn == columnTable.fieldValueColumnType()) {
            BoolValueColumn boolValueColumn = new BoolValueColumn();
            if (null != columnTable.fieldValueColumn(boolValueColumn)) {
                fieldValue = boolValueColumn.value(matchRecordRowCursor);
            }
        }
        return fieldValue;
    }

    /**
     * 根据字段名返回相应类型的值
     * @param fieldName 字段名
     * @return 若解析失败或者类型不匹配则返回null
     */
    private Integer getInt(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getInt(fieldIndex);
    }
    /**
     * 根据字段名返回相应类型的值
     * @param fieldIndex 字段索引
     * @return 若解析失败或者类型不匹配则返回null
     */
    private Integer getInt(int fieldIndex) {
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
                    fieldValue = (int) int8ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int16ValueColumn:
                Int16ValueColumn int16ValueColumn = new Int16ValueColumn();
                if (null != columnTable.fieldValueColumn(int16ValueColumn)) {
                    fieldValue = (int) int16ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int32ValueColumn:
                Int32ValueColumn int32ValueColumn = new Int32ValueColumn();
                if (null != columnTable.fieldValueColumn(int32ValueColumn)) {
                    fieldValue = int32ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int64ValueColumn:
                Int64ValueColumn int64ValueColumn = new Int64ValueColumn();
                if (null != columnTable.fieldValueColumn(int64ValueColumn)) {
                    Long longValue = int64ValueColumn.value(matchRecordRowCursor);
                    fieldValue = longValue != null ? longValue.intValue() : null;
                }
                break;
            case FieldValueColumn.UInt8ValueColumn:
                UInt8ValueColumn uint8ValueColumn = new UInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(uint8ValueColumn)) {
                    fieldValue = uint8ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt16ValueColumn:
                UInt16ValueColumn uint16ValueColumn = new UInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(uint16ValueColumn)) {
                    fieldValue = uint16ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt32ValueColumn:
                UInt32ValueColumn uint32ValueColumn = new UInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(uint32ValueColumn)) {
                    Long longValue = uint32ValueColumn.value(matchRecordRowCursor);
                    fieldValue = longValue != null ? longValue.intValue() : null;
                }
                break;
            case FieldValueColumn.UInt64ValueColumn:
                UInt64ValueColumn uint64ValueColumn = new UInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(uint64ValueColumn)) {
                    Long longValue = uint64ValueColumn.value(matchRecordRowCursor);
                    fieldValue = longValue != null ? longValue.intValue() : null;
                }
                break;
            case FieldValueColumn.StringValueColumn:
                try {
                    fieldValue = Integer.valueOf(getString(fieldIndex, utf_8));
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
     * @return
     *     若解析失败或者类型不匹配则返回null
     */
    private Long getLong(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getLong(fieldIndex);
    }
    /**
     *根据字段名返回相应类型的值
     * @param fieldIndex 字段索引
     * @return
     *     若解析失败或者类型不匹配则返回null
     */
    private Long getLong(int fieldIndex) {
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
                    fieldValue = (long) int8ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int16ValueColumn:
                Int16ValueColumn int16ValueColumn = new Int16ValueColumn();
                if (null != columnTable.fieldValueColumn(int16ValueColumn)) {
                    fieldValue = (long) int16ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int32ValueColumn:
                Int32ValueColumn int32ValueColumn = new Int32ValueColumn();
                if (null != columnTable.fieldValueColumn(int32ValueColumn)) {
                    fieldValue = (long) int32ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.Int64ValueColumn:
                Int64ValueColumn int64ValueColumn = new Int64ValueColumn();
                if (null != columnTable.fieldValueColumn(int64ValueColumn)) {
                    fieldValue = int64ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt8ValueColumn:
                UInt8ValueColumn uint8ValueColumn = new UInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(uint8ValueColumn)) {
                    fieldValue = (long) uint8ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt16ValueColumn:
                UInt16ValueColumn uint16ValueColumn = new UInt16ValueColumn();
                if (null != columnTable.fieldValueColumn(uint16ValueColumn)) {
                    fieldValue = (long) uint16ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt32ValueColumn:
                UInt32ValueColumn uint32ValueColumn = new UInt32ValueColumn();
                if (null != columnTable.fieldValueColumn(uint32ValueColumn)) {
                    fieldValue = uint32ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.UInt64ValueColumn:
                UInt64ValueColumn uint64ValueColumn = new UInt64ValueColumn();
                if (null != columnTable.fieldValueColumn(uint64ValueColumn)) {
                    fieldValue = uint64ValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.StringValueColumn:
                String stringValue = getString(fieldIndex, utf_8);
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
     * @return
     *     若解析失败或者类型不匹配则返回null
     */
    private Float getFloat(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getFloat(fieldIndex);
    }
    /**
     *根据字段名返回相应类型的值
     * @param fieldIndex 字段索引
     * @return
     *     若解析失败或者类型不匹配则返回null
     */
    private Float getFloat(int fieldIndex) {
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
                    fieldValue = floatValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.DoubleValueColumn:
                DoubleValueColumn doubleValueColumn = new DoubleValueColumn();
                if (null != columnTable.fieldValueColumn(doubleValueColumn)) {
                    Double doubleValue = doubleValueColumn.value(matchRecordRowCursor);
                    fieldValue = doubleValue != null ? doubleValue.floatValue() : null;
                }
                break;
            case FieldValueColumn.StringValueColumn:
                String stringValue = getString(fieldIndex, utf_8);
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
     * @return
     *      若解析失败或者类型不匹配则返回null
     */
    private Double getDouble(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getDouble(fieldIndex);
    }

    /**
     *根据字段名返回相应类型的值
     * @param fieldIndex 字段索引
     * @return
     *      若解析失败或者类型不匹配则返回null
     */
    private Double getDouble(int fieldIndex) {
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
                    fieldValue = (double) floatValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.DoubleValueColumn:
                DoubleValueColumn doubleValueColumn = new DoubleValueColumn();
                if (null != columnTable.fieldValueColumn(doubleValueColumn)) {
                    fieldValue = doubleValueColumn.value(matchRecordRowCursor);
                }
                break;
            case FieldValueColumn.StringValueColumn:
                String stringValue = getString(fieldIndex, utf_8);
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
     * @return String
     */
    private String getString(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getString(fieldIndex, utf_8);
    }

    private String getString(@NonNull String fieldName, @NonNull Charset charset) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getString(fieldIndex, charset);
    }

    private String getString(@NonNull String fieldName, @NonNull String charset) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getString(fieldIndex, charset);
    }

    private String getString(int fieldIndex) {
        return getString(fieldIndex, utf_8);
    }
    /**
     *根据字段名返回相应类型的值
     * @param fieldIndex 字段索引
     * @param charset utf-8 or gbk
     * @return String
     */
    private String getString(int fieldIndex, @NonNull String charset) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        String fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.StringValueColumn:
                StringValueColumn stringValueColumn = new StringValueColumn();
                if (null != columnTable.fieldValueColumn(stringValueColumn)) {
                    try {
                        byte[] arr = new byte[stringValueColumn.valueAsByteBuffer(matchRecordRowCursor).remaining()];
                        stringValueColumn.valueAsByteBuffer(matchRecordRowCursor).get(arr);
                        return new String(arr, charset);
                    } catch (UnsupportedEncodingException e) {
                        return stringValueColumn.value(matchRecordRowCursor);
                    } catch (Exception e) {
                        return stringValueColumn.value(matchRecordRowCursor);
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
            case FieldValueColumn.BoolValueColumn:
                Boolean boolValue = getBoolean(fieldIndex);
                if (null != boolValue) {
                    return boolValue.toString();
                }
            default:
                break;
        }
        return null;
    }

    private String getString(int fieldIndex, @NonNull Charset charset) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        String fieldValue = null;
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.StringValueColumn:
                StringValueColumn stringValueColumn = new StringValueColumn();
                if (null != columnTable.fieldValueColumn(stringValueColumn)) {
                    try {
                        byte[] arr = new byte[stringValueColumn.valueAsByteBuffer(matchRecordRowCursor).remaining()];
                        stringValueColumn.valueAsByteBuffer(matchRecordRowCursor).get(arr);
                        return new String(arr, charset);
                    } catch (Exception e) {
                        return stringValueColumn.value(matchRecordRowCursor);
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
            case FieldValueColumn.BoolValueColumn:
                Boolean boolValue = getBoolean(fieldIndex);
                if (null != boolValue) {
                    return boolValue.toString();
                }
            default:
                break;
        }
        return null;
    }
    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldName 字段名
     * @return List<Integer>
     */
    private List<Integer> getIntList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getIntList(fieldIndex);
    }
    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List<Integer>
     */
    private List<Integer> getIntList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        List<Integer> fieldValue = new ArrayList<Integer>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiUInt8ValueColumn:
                MultiUInt8ValueColumn mUInt8ValueColumn = new MultiUInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt8ValueColumn)) {
                    MultiUInt8Value multiUInt8Value = mUInt8ValueColumn.value(matchRecordRowCursor);
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
                    MultiUInt16Value multiUInt16Value = mUInt16ValueColumn.value(matchRecordRowCursor);
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
                    MultiUInt32Value multiUInt32Value = mUInt32ValueColumn.value(matchRecordRowCursor);
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
                    MultiUInt64Value multiUInt64Value = mUInt64ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt8Value multiInt8Value = mInt8ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt16Value multiInt16Value = mInt16ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt32Value multiInt32Value = mInt32ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt64Value multiInt64Value = mInt64ValueColumn.value(matchRecordRowCursor);
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
     * @return List<Long>
     */
    private List<Long> getLongList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getLongList(fieldIndex);
    }
    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List<Long>
     */
    private List<Long> getLongList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }

        List<Long> fieldValue = new ArrayList<Long>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiUInt8ValueColumn:
                MultiUInt8ValueColumn mUInt8ValueColumn = new MultiUInt8ValueColumn();
                if (null != columnTable.fieldValueColumn(mUInt8ValueColumn)) {
                    MultiUInt8Value multiUInt8Value = mUInt8ValueColumn.value(matchRecordRowCursor);
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
                    MultiUInt16Value multiUInt16Value = mUInt16ValueColumn.value(matchRecordRowCursor);
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
                    MultiUInt32Value multiUInt32Value = mUInt32ValueColumn.value(matchRecordRowCursor);
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
                    MultiUInt64Value multiUInt64Value = mUInt64ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt8Value multiInt8Value = mInt8ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt16Value multiInt16Value = mInt16ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt32Value multiInt32Value = mInt32ValueColumn.value(matchRecordRowCursor);
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
                    MultiInt64Value multiInt64Value = mInt64ValueColumn.value(matchRecordRowCursor);
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
     * @return List<Float>, 如果解析失败或者没有值,返回null
     */
    private List<Float> getFloatList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getFloatList(fieldIndex);
    }
    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List<Float>, 如果解析失败或者没有值,返回null
     */
    private List<Float> getFloatList(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }
        List<Float> fieldValue = new ArrayList<Float>();
        FieldValueColumnTable columnTable = fbByColumnMatchRecords.recordColumns(fieldIndex);
        if (null == columnTable) {
            return null;
        }

        switch(columnTable.fieldValueColumnType()) {
            case FieldValueColumn.MultiFloatValueColumn:
                MultiFloatValueColumn multiFloatValueColumn = new MultiFloatValueColumn();
                if (null != columnTable.fieldValueColumn(multiFloatValueColumn)) {
                    MultiFloatValue multiFloatValue = multiFloatValueColumn.value(matchRecordRowCursor);
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
                    MultiDoubleValue multiDoubleValue = multiDoubleValueColumn.value(matchRecordRowCursor);
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
     * @return List<Double> 如果解析失败或者没有值,返回null
     */
    private List<Double> getDoubleList(@NonNull String fieldName) {
        Integer fieldIndex = fieldName2IndexMap.get(fieldName);
        if (fieldIndex == null) {
            return null;
        }
        return getDoubleList(fieldIndex);
    }
    /**
     * 根据字段名返回相应类型的值的List
     * @param fieldIndex 字段索引
     * @return List<Double> 如果解析失败或者没有值,返回null
     */
    private List<Double> getDoubleList(int fieldIndex) {
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
                    MultiFloatValue multiFloatValue = multiFloatValueColumn.value(matchRecordRowCursor);
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
                    MultiDoubleValue multiDoubleValue = multiDoubleValueColumn.value(matchRecordRowCursor);
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
     * @return List<String> 如果解析失败或者没有值,返回null
     */
    private List<String> getStringList(String fieldName) {
        Integer index = fieldName2IndexMap.get(fieldName);
        if (null == index) {
            return null;
        }
        return getStringList(index);
    }

    /**
     * 根据字段索引返回相应类型的值的List
     * @param fieldIndex 字段名
     * @return List<String> 如果解析失败或者没有值,返回null
     */
    private List<String> getStringList(int fieldIndex) {
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
                    MultiStringValue multiStringValue = multiStringValueColumn.value(matchRecordRowCursor);
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

    /**
     * 根据字段名，查询字段值
     *
     * @param fieldName
     *            字段名
     * @return 若包含字段值时，返回字段值；否则返回null
     */
    public Object getObject(@NonNull String fieldName) {
        Integer index = fieldName2IndexMap.get(fieldName);
        int fieldIndex;
        if (null == index) {
            return null;
        } else {
            fieldIndex = index;
        }
        return getObejct(fieldIndex);
    }

    /**
     * 根据字段索引返回相应的Object
     * @param fieldIndex 字段名
     * @return Object 如果解析失败或者没有值,返回null
     */
    public Object getObejct(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= fieldValueCount) {
            return null;
        }
        switch(fbByColumnMatchRecords.recordColumns(fieldIndex).fieldValueColumnType()) {
            case FieldValueColumn.BoolValueColumn: {
                return getBoolean(fieldIndex);
            }
            case FieldValueColumn.Int8ValueColumn:
            case FieldValueColumn.Int16ValueColumn:
            case FieldValueColumn.Int32ValueColumn:
            case FieldValueColumn.UInt8ValueColumn:
            case FieldValueColumn.UInt16ValueColumn:
            case FieldValueColumn.UInt32ValueColumn:
                return getInt(fieldIndex);
            case FieldValueColumn.Int64ValueColumn:
            case FieldValueColumn.UInt64ValueColumn:
                return getLong(fieldIndex);
            case FieldValueColumn.FloatValueColumn:
                return getFloat(fieldIndex);
            case FieldValueColumn.DoubleValueColumn:
                return getDouble(fieldIndex);
            case FieldValueColumn.StringValueColumn:
                return getString(fieldIndex, utf_8);
            case FieldValueColumn.MultiInt8ValueColumn:
            case FieldValueColumn.MultiInt16ValueColumn:
            case FieldValueColumn.MultiInt32ValueColumn:
            case FieldValueColumn.MultiUInt8ValueColumn:
            case FieldValueColumn.MultiUInt16ValueColumn:
            case FieldValueColumn.MultiUInt32ValueColumn:
                return getIntList(fieldIndex);
            case FieldValueColumn.MultiInt64ValueColumn:
            case FieldValueColumn.MultiUInt64ValueColumn:
                return getLongList(fieldIndex);
            case FieldValueColumn.MultiFloatValueColumn:
                return getFloatList(fieldIndex);
            case FieldValueColumn.MultiDoubleValueColumn:
                return getDoubleList(fieldIndex);
            case FieldValueColumn.MultiStringValueColumn:
                return getStringList(fieldIndex);
            default:
                break;
        }
        return null;
    }


    public Object toJson () {
        return null;
    }

    protected void addToJsonObject(@NonNull JsonObject jsonObject, String fieldName, FieldType type) {
        Gson gson = new Gson();
        switch (type) {
            case BOOL:
                jsonObject.addProperty(fieldName, getBoolean(fieldName));
                break;
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                jsonObject.addProperty(fieldName, getLong(fieldName));
                break;
            case FLOAT:
            case DOUBLE:
                jsonObject.addProperty(fieldName, getDouble(fieldName));
                break;
            case STRING:
                jsonObject.addProperty(fieldName, getString(fieldName));
                break;
            case MULTIINT8:
            case MULTIINT16:
            case MULTIINT32:
            case MULTIINT64:
            case MULTIUINT8:
            case MULTIUINT16:
            case MULTIUINT32:
            case MULTIUINT64:
                List<Long> longs = getLongList(fieldName);
                jsonObject.add(fieldName, gson.toJsonTree(longs));
                break;
            case MULTIFLOAT:
            case MULTIDOUBLE:
                List<Double> doubles = getDoubleList(fieldName);
                jsonObject.add(fieldName, gson.toJsonTree(doubles));
                break;
            case MULTISTRING:
                List<String> strings = getStringList(fieldName);
                jsonObject.add(fieldName, gson.toJsonTree(strings));
                break;
            default:
                break;
        }
    }


    @Override
    public String toString() {
        StringBuilder ss = new StringBuilder(128);
        ss.append("{");
        ss.append("\"label\":\"").append(label()).append("\",");
        List<String> fieldNames = getFieldNames();
        List<FieldType> fieldTypes = getFieldTypes();
        for (int i = 0; i < fieldNames.size(); ++i) {
            String fieldName = fieldNames.get(i);
            FieldType type = fieldTypes.get(i);
            addToStringObject(ss, fieldName, type);
            if (i == fieldNames.size() -1) {
                ss.setLength(ss.length() - 1);
            }
        }
        ss.append("}");
        return ss.toString();
    }

    protected void addToStringObject(@NonNull StringBuilder ss, String fieldName, FieldType type) {
        switch (type) {
            case BOOL:
                ss.append("\"").append(fieldName).append("\":").append(getBoolean(fieldName)).append(",");
                break;
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                ss.append("\"").append(fieldName).append("\":").append(getLong(fieldName)).append(",");
                break;
            case FLOAT:
            case DOUBLE:
                ss.append("\"").append(fieldName).append("\":").append(getDouble(fieldName)).append(",");
                break;
            case STRING:
                ss.append("\"").append(fieldName).append("\":\"").append(getString(fieldName)).append("\",");
                break;
            case MULTIINT8:
            case MULTIINT16:
            case MULTIINT32:
            case MULTIINT64:
            case MULTIUINT8:
            case MULTIUINT16:
            case MULTIUINT32:
            case MULTIUINT64:
                List<Long> longs = getLongList(fieldName);
                ss.append("\"").append(fieldName).append("\":").append(longs).append(",");
                break;
            case MULTIFLOAT:
            case MULTIDOUBLE:
                List<Double> doubles = getDoubleList(fieldName);
                ss.append("\"").append(fieldName).append("\":").append(doubles).append(",");
                break;
            case MULTISTRING:
                List<String> strings = getStringList(fieldName);
                ss.append("\"").append(fieldName).append("\":");
                if (null != strings) {
                    ss.append("[");
                    for (String value : strings) {
                        ss.append("\"").append(value).append("\",");
                    }
                    ss.append("]");
                }
                ss.append(",");
                break;
            default:
                break;
        }
    }
}
