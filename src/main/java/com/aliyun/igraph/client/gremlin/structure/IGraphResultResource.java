package com.aliyun.igraph.client.gremlin.structure;

import com.aliyun.igraph.client.core.model.type.BulkSet;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.proto.gremlin_fb.*;
import com.google.flatbuffers.Table;
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
import lombok.NonNull;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * @author alibaba
 */
public class IGraphResultResource {
    public static final Charset utf_8 = Charset.forName("utf-8");
    private static final Logger log = LoggerFactory.getLogger(IGraphResultResource.class);
    private GremlinObject gremlinObject;
    private IGraphResultSet IGraphResultSet;
    private int fbColumnVecRow = 0;
    private Object fbColumnVec = null;
    private Object fbMultiValue = null;
    private IGraphResultObjectType resultObjectType = IGraphResultObjectType.UNKNOWN;

    public void setGremlinObject(GremlinObject gremlinObject) {
        this.gremlinObject = gremlinObject;
    }

    public void setGremlinSingleResult(IGraphResultSet IGraphResultSet) {
        this.IGraphResultSet = IGraphResultSet;
    }

    private IGraphResultObjectType elementType2ObjectType(Long type) {
        if (type.equals(0L)) {
            return IGraphResultObjectType.VERTEX;
        } else if (type.equals(1L)) {
            return IGraphResultObjectType.EDGE;
        } else if (type.equals(2L)) {
            return IGraphResultObjectType.PROPERTY;
        } else {
            throw new IGraphServerException("element type [" + type + "] is invalid");
        }
    }

    public IGraphResultObjectType getObjectType() {
        if (IGraphResultObjectType.UNKNOWN != resultObjectType) {
            return resultObjectType;
        }
        switch (gremlinObject.valueType()) {
            case ObjectValue.BoolValue:
                resultObjectType = IGraphResultObjectType.BOOL;
                break;
            case ObjectValue.Int8Value:
                resultObjectType = IGraphResultObjectType.INT8;
                break;
            case ObjectValue.Int16Value:
                resultObjectType = IGraphResultObjectType.INT16;
                break;
            case ObjectValue.Int32Value:
                resultObjectType = IGraphResultObjectType.INT32;
                break;
            case ObjectValue.Int64Value:
                resultObjectType = IGraphResultObjectType.INT64;
                break;
            case ObjectValue.UInt8Value:
                resultObjectType = IGraphResultObjectType.UINT8;
                break;
            case ObjectValue.UInt16Value:
                resultObjectType = IGraphResultObjectType.UINT16;
                break;
            case ObjectValue.UInt32Value:
                resultObjectType = IGraphResultObjectType.UINT32;
                break;
            case ObjectValue.UInt64Value:
                resultObjectType = IGraphResultObjectType.UINT64;
                break;
            case ObjectValue.FloatValue:
                resultObjectType = IGraphResultObjectType.FLOAT;
                break;
            case ObjectValue.DoubleValue:
                resultObjectType = IGraphResultObjectType.DOUBLE;
                break;
            case ObjectValue.StringValue:
                resultObjectType = IGraphResultObjectType.STRING;
                break;
            case ObjectValue.ObjectVec:
                resultObjectType = IGraphResultObjectType.LIST;
                break;
            case ObjectValue.ObjectSet:
                resultObjectType = IGraphResultObjectType.SET;
                break;
            case ObjectValue.ObjectMap:
                resultObjectType = IGraphResultObjectType.MAP;
                break;
            case ObjectValue.ObjectMapEntry:
                resultObjectType = IGraphResultObjectType.MapEntry;
                break;
            case ObjectValue.ObjectBulkSet:
                resultObjectType = IGraphResultObjectType.BULKSET;
                break;
            case ObjectValue.ObjectElement:
                ObjectElement objectElement = new ObjectElement();
                if (null != gremlinObject.value(objectElement)) {
                    resultObjectType = elementType2ObjectType(objectElement.elementType());
                }
                break;
            case ObjectValue.ObjectPath:
                resultObjectType = IGraphResultObjectType.PATH;
                break;
            case ObjectValue.MultiInt8Value:
                resultObjectType = IGraphResultObjectType.MULTI_INT8_VALUE;
                break;
            case ObjectValue.MultiInt16Value:
                resultObjectType = IGraphResultObjectType.MULTI_INT16_VALUE;
                break;
            case ObjectValue.MultiInt32Value:
                resultObjectType = IGraphResultObjectType.MULTI_INT32_VALUE;
                break;
            case ObjectValue.MultiInt64Value:
                resultObjectType = IGraphResultObjectType.MULTI_INT64_VALUE;
                break;
            case ObjectValue.MultiUInt8Value:
                resultObjectType = IGraphResultObjectType.MULTI_UINT8_VALUE;
                break;
            case ObjectValue.MultiUInt16Value:
                resultObjectType = IGraphResultObjectType.MULTI_UINT16_VALUE;
                break;
            case ObjectValue.MultiUInt32Value:
                resultObjectType = IGraphResultObjectType.MULTI_UINT32_VALUE;
                break;
            case ObjectValue.MultiUInt64Value:
                resultObjectType = IGraphResultObjectType.MULTI_UINT64_VALUE;
                break;
            case ObjectValue.MultiFloatValue:
                resultObjectType = IGraphResultObjectType.MULTI_FLOAT_VALUE;
                break;
            case ObjectValue.MultiDoubleValue:
                resultObjectType = IGraphResultObjectType.MULTI_DOUBLE_VALUE;
                break;
            case ObjectValue.MultiStringValue:
                resultObjectType = IGraphResultObjectType.MULTI_STRING_VALUE;
                break;
            default:
                break;
        }
        return resultObjectType;
    }

    public Object getObject() {
        switch (getObjectType()) {
            case UNKNOWN:
                break;
            case BOOL:
                return getBoolean();
            case INT8:
            case UINT8:
                return getByte();
            case INT16:
            case UINT16:
                return getShort();
            case INT32:
            case UINT32:
                return getInt();
            case INT64:
            case UINT64:
                return getLong();
            case FLOAT:
                return getFloat();
            case DOUBLE:
                return getDouble();
            case STRING:
                return getString();
            case LIST:
                return getList();
            case SET:
                return getSet();
            case MAP:
                return getMap();
            case BULKSET:
                return getBulkSet();
            case VERTEX:
                return getVertex();
            case EDGE:
                return getEdge();
            case ELEMENT:
                return getElement();
            case PROPERTY:
                return getProperty();
            case PATH:
                return getPath();
            case MapEntry:
                return getMapEntry();
            case MULTI_INT8_VALUE:
                return getMultiInt8Value();
            case MULTI_INT16_VALUE:
                return getMultiInt16Value();
            case MULTI_INT32_VALUE:
                return getMultiInt32Value();
            case MULTI_INT64_VALUE:
                return getMultiInt64Value();
            case MULTI_UINT8_VALUE:
                return getMultiUInt8Value();
            case MULTI_UINT16_VALUE:
                return getMultiUInt16Value();
            case MULTI_UINT32_VALUE:
                return getMultiUInt32Value();
            case MULTI_UINT64_VALUE:
                return getMultiUInt64Value();
            case MULTI_FLOAT_VALUE:
                return getMultiFloatValue();
            case MULTI_DOUBLE_VALUE:
                return getMultiDoubleValue();
            case MULTI_STRING_VALUE:
                return getMultiString();
            default:
                return null;
        }
        return null;
    }

    public List<? extends Number> getMultiNumber() {
        switch (gremlinObject.valueType()) {
            case ObjectValue.MultiInt8Value:
                return getMultiInt8Value();
            case ObjectValue.MultiInt16Value:
                return getMultiInt16Value();
            case ObjectValue.MultiInt32Value:
                return getMultiInt32Value();
            case ObjectValue.MultiInt64Value:
                return getMultiInt64Value();
            case ObjectValue.MultiUInt8Value:
                return getMultiUInt8Value();
            case ObjectValue.MultiUInt16Value:
                return getMultiUInt16Value();
            case ObjectValue.MultiUInt32Value:
                return getMultiUInt32Value();
            case ObjectValue.MultiUInt64Value:
                return getMultiUInt64Value();
            case ObjectValue.MultiFloatValue:
                return getMultiFloatValue();
            case ObjectValue.MultiDoubleValue:
                return getMultiDoubleValue();
            default:
                return null;
        }
    }

    public List<String> getMultiString() {
        if (IGraphResultObjectType.MULTI_STRING_VALUE != getObjectType()) {
            return null;
        }
        List<String> result = new ArrayList<>();
        MultiStringValue values = new MultiStringValue();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }

    public List<Byte> getMultiInt8Value() {
        if (IGraphResultObjectType.MULTI_INT8_VALUE != getObjectType()) {
            return null;
        }
        List<Byte> result = new ArrayList<>();
        MultiInt8Value values = new MultiInt8Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }
    public List<Short> getMultiInt16Value() {
        if (IGraphResultObjectType.MULTI_INT16_VALUE != getObjectType()) {
            return null;
        }
        List<Short> result = new ArrayList<>();
        MultiInt16Value values = new MultiInt16Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }
    public List<Integer> getMultiInt32Value() {
        if (IGraphResultObjectType.MULTI_INT32_VALUE != getObjectType()) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        MultiInt32Value values = new MultiInt32Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }
    public List<Long> getMultiInt64Value() {
        if (IGraphResultObjectType.MULTI_INT64_VALUE != getObjectType()) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        MultiInt64Value values = new MultiInt64Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }
    public List<Integer> getMultiUInt8Value() {
        if (IGraphResultObjectType.MULTI_UINT8_VALUE != getObjectType()) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        MultiUInt8Value values = new MultiUInt8Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }

    public List<Integer> getMultiUInt16Value() {
        if (IGraphResultObjectType.MULTI_UINT16_VALUE != getObjectType()) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        MultiUInt16Value values = new MultiUInt16Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }

    public List<Long> getMultiUInt32Value() {
        if (IGraphResultObjectType.MULTI_UINT32_VALUE != getObjectType()) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        MultiUInt32Value values = new MultiUInt32Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }

    public List<Long> getMultiUInt64Value() {
        if (IGraphResultObjectType.MULTI_UINT64_VALUE != getObjectType()) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        MultiUInt64Value values = new MultiUInt64Value();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }

    public List<Float> getMultiFloatValue() {
        if (IGraphResultObjectType.MULTI_FLOAT_VALUE != getObjectType()) {
            return null;
        }
        List<Float> result = new ArrayList<>();
        MultiFloatValue values = new MultiFloatValue();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }

    public List<Double> getMultiDoubleValue() {
        if (IGraphResultObjectType.MULTI_DOUBLE_VALUE != getObjectType()) {
            return null;
        }
        List<Double> result = new ArrayList<>();
        MultiDoubleValue values = new MultiDoubleValue();
        if (null == gremlinObject.value(values)) {
            return null;
        }
        int length = values.valueLength();
        for (int i = 0; i < length; ++i) {
            result.add(values.value(i));
        }
        return result;
    }

    public Boolean getBoolean() {
        if (IGraphResultObjectType.BOOL != getObjectType()) {
            log.warn("Actual object type is [" + getObjectType().toString() + "] when call getBoolean()");
            throw new NumberFormatException();
        }
        Boolean fieldValue;
        if (null != fbColumnVec) {
            fieldValue = ((BoolValueColumn)fbColumnVec).value(fbColumnVecRow);
        } else {
            BoolValue fbValue = new BoolValue();
            fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
        }
        return fieldValue;
    }

    public boolean isNull() {
        Object fieldValue = null;
        switch (getObjectType()) {
            case BOOL:
                fieldValue = getBoolean();
                break;
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                fieldValue = getLong();
                break;
            case FLOAT:
            case DOUBLE:
                fieldValue = getDouble();
                break;
            case STRING:
                fieldValue = getString();
                break;
            case LIST:
                fieldValue = listToString();
                break;
            case SET:
                fieldValue = setToString();
                break;
            case MapEntry:
                fieldValue = mapEntryToString();
                break;
            case MAP:
                fieldValue = mapToString();
                break;
            case VERTEX:
            case EDGE:
            case PROPERTY:
                fieldValue = elementToString(getObjectType());
                break;
            case PATH:
                IGraphPath path = getPath();
                if (null != path) {
                    fieldValue = path.toString();
                }
                break;
            default:
                break;
        }
        return fieldValue == null;
    }

    public Integer getInt() {
        Integer fieldValue = null;
        switch (getObjectType()) {
            case INT8:
                if (null != fbColumnVec) {
                    fieldValue = (int)((Int8ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (int)((MultiInt8Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int8Value fbValue = new Int8Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (int)fbValue.value() : null;
                }
                break;
            case INT16:
                if (null != fbColumnVec) {
                    fieldValue = (int)((Int16ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (int)((MultiInt16Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int16Value fbValue = new Int16Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (int)fbValue.value() : null;
                }
                break;
            case INT32:
                if (null != fbColumnVec) {
                    fieldValue = ((Int32ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiInt32Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int32Value fbValue = new Int32Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case INT64:
                Long long64;
                if (null != fbColumnVec) {
                    long64 = ((Int64ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    long64 = ((MultiInt64Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int64Value fbValue = new Int64Value();
                    long64 = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                fieldValue = long64 != null ? long64.intValue() : null;
                break;
            case UINT8:
                if (null != fbColumnVec) {
                    fieldValue = ((UInt8ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiUInt8Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt8Value fbValue = new UInt8Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case UINT16:
                if (null != fbColumnVec) {
                    fieldValue = ((UInt16ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiUInt16Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt16Value fbValue = new UInt16Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case UINT32:
                Long longU32;
                if (null != fbColumnVec) {
                    longU32 = ((UInt32ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    longU32 = ((MultiUInt32Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt32Value fbValue = new UInt32Value();
                    longU32 = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                fieldValue = longU32 != null ? longU32.intValue() : null;
                break;
            case UINT64:
                Long longU64;
                if (null != fbColumnVec) {
                    longU64 = ((UInt64ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    longU64 = ((MultiUInt64Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt64Value fbValue = new UInt64Value();
                    longU64 = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                fieldValue = longU64 != null ? longU64.intValue() : null;
                break;
            case STRING:
                try {
                    fieldValue = Integer.valueOf(getString());
                } catch (Exception e) {
                    return null;
                }
                break;
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getInt()");
                break;
        }
        return fieldValue;
    }

    public Byte getByte() {
        Byte fieldValue = 0;
        switch (getObjectType()) {
            case INT8:
                if (null != fbColumnVec) {
                    fieldValue = (byte)((Int8ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (byte)((MultiInt8Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int8Value fbValue = new Int8Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (byte)fbValue.value() : null;
                }
                break;
            case UINT8:
                if (null != fbColumnVec) {
                    fieldValue = (byte)((UInt8ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (byte)((MultiUInt8Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt8Value fbValue = new UInt8Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (byte)fbValue.value() : null;
                }
                break;
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getByte()");
                break;
        }
        return fieldValue;
    }

    public Short getShort() {
        Short fieldValue = 0;
        switch (getObjectType()) {
            case INT16:
                if (null != fbColumnVec) {
                    fieldValue = ((Int16ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiInt16Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int16Value fbValue = new Int16Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (short)fbValue.value() : null;
                }
                break;
            case UINT16:
                if (null != fbColumnVec) {
                    fieldValue = (short)((UInt16ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (short)((MultiUInt16Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt16Value fbValue = new UInt16Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (short)fbValue.value() : null;
                }
                break;
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getShort()");
                break;
        }
        return fieldValue;
    }

    public Long getLong() {
        Long fieldValue = null;
        switch (getObjectType()) {
            case INT8:
                if (null != fbColumnVec) {
                    fieldValue = (long)((Int8ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue =(long)((MultiInt8Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int8Value fbValue = new Int8Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (long)fbValue.value() : null;
                }
                break;
            case INT16:
                if (null != fbColumnVec) {
                    fieldValue = (long)((Int16ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (long)((MultiInt16Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int16Value fbValue = new Int16Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (long)fbValue.value() : null;
                }
                break;
            case INT32:
                if (null != fbColumnVec) {
                    fieldValue = (long)((Int32ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (long)((MultiInt32Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int32Value fbValue = new Int32Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (long)fbValue.value() : null;
                }
                break;
            case INT64:
                if (null != fbColumnVec) {
                    fieldValue = ((Int64ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiInt64Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    Int64Value fbValue = new Int64Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case UINT8:
                if (null != fbColumnVec) {
                    fieldValue = (long)((UInt8ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (long)((MultiUInt8Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt8Value fbValue = new UInt8Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (long)fbValue.value() : null;
                }
                break;
            case UINT16:
                if (null != fbColumnVec) {
                    fieldValue = (long)((UInt16ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (long) ((MultiUInt16Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt16Value fbValue = new UInt16Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? (long)fbValue.value() : null;
                }
                break;
            case UINT32:
                if (null != fbColumnVec) {
                    fieldValue = ((UInt32ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiUInt32Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt32Value fbValue = new UInt32Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case UINT64:
                if (null != fbColumnVec) {
                    fieldValue = ((UInt64ValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiUInt64Value)fbMultiValue).value(fbColumnVecRow);
                } else {
                    UInt64Value fbValue = new UInt64Value();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case STRING:
                try {
                    fieldValue = Long.valueOf(getString());
                } catch (Exception e) {
                    return null;
                }
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getLong()");
                break;
        }
        return fieldValue;
    }

    public Float getFloat() {
        Float fieldValue = null;
        switch (getObjectType()) {
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                Long value = getLong();
                if (null != value) {
                    fieldValue = value.floatValue();
                }
                break;
            case FLOAT:
                if (null != fbColumnVec) {
                    fieldValue = ((FloatValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiFloatValue)fbMultiValue).value(fbColumnVecRow);
                } else {
                    FloatValue fbValue = new FloatValue();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case DOUBLE:
                Double doubleValue;
                if (null != fbColumnVec) {
                    doubleValue = ((DoubleValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    doubleValue = ((MultiDoubleValue)fbMultiValue).value(fbColumnVecRow);
                } else {
                    DoubleValue fbValue = new DoubleValue();
                    doubleValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                if (null != doubleValue) {
                    fieldValue = doubleValue.floatValue();
                }
                break;
            case STRING:
                try {
                    fieldValue = Float.valueOf(getString());
                } catch (Exception e) {
                    return null;
                }
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getFloat()");
                break;
        }
        return fieldValue;
    }

    public Double getDouble() {
        Double fieldValue = null;
        switch (getObjectType()) {
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                Long value = getLong();
                if (null != value) {
                    fieldValue = value.doubleValue();
                }
                break;
            case FLOAT:
                if (null != fbColumnVec) {
                    fieldValue = (double)((FloatValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = (double)((MultiFloatValue)fbMultiValue).value(fbColumnVecRow);
                } else {
                    FloatValue fbValue = new FloatValue();
                    fieldValue = gremlinObject.value(fbValue) != null ? (double)fbValue.value() : null;
                }
                break;
            case DOUBLE:
                if (null != fbColumnVec) {
                    fieldValue = ((DoubleValueColumn)fbColumnVec).value(fbColumnVecRow);
                } else if (null != fbMultiValue) {
                    fieldValue = ((MultiDoubleValue)fbMultiValue).value(fbColumnVecRow);
                } else {
                    DoubleValue fbValue = new DoubleValue();
                    fieldValue = gremlinObject.value(fbValue) != null ? fbValue.value() : null;
                }
                break;
            case STRING:
                try {
                    fieldValue = Double.valueOf(getString());
                } catch (Exception e) {
                    return null;
                }
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getDouble()");
                break;
        }
        return fieldValue;
    }

    public String getString() {
        return getString(utf_8);
    }

    public String getString(@NonNull Charset charset) {
        String fieldValue = null;
        switch (getObjectType()) {
            case STRING:
                byte[] arr = null;
                if (null != fbColumnVec) {
                    StringValueColumn stringValueColumn = (StringValueColumn)fbColumnVec;
                    arr = new byte[stringValueColumn.valueAsByteBuffer(fbColumnVecRow).remaining()];
                    stringValueColumn.valueAsByteBuffer(fbColumnVecRow).get(arr);
                } else if (null != fbMultiValue) {
                    return ((MultiStringValue)fbMultiValue).value(fbColumnVecRow);
                } else {
                    StringValue fbValue = new StringValue();
                    if (null != gremlinObject.value(fbValue)) {
                        arr = new byte[fbValue.valueAsByteBuffer().remaining()];
                        fbValue.valueAsByteBuffer().get(arr);
                    }
                }
                return new String(arr, charset);
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                Long longValue = getLong();
                if (null != longValue) {
                    fieldValue = longValue.toString();
                }
                break;
            case FLOAT:
            case DOUBLE:
                Double doubleValue = getDouble();
                if (null != doubleValue) {
                    fieldValue = doubleValue.toString();
                }
                break;
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getString()");
                break;
        }
        return fieldValue;
    }

    public String getString(@NonNull String charset) {
        String fieldValue = null;
        switch (getObjectType()) {
            case STRING:
                byte[] arr = null;
                if (null != fbColumnVec) {
                    StringValueColumn stringValueColumn = (StringValueColumn)fbColumnVec;
                    arr = new byte[stringValueColumn.valueAsByteBuffer(fbColumnVecRow).remaining()];
                    stringValueColumn.valueAsByteBuffer(fbColumnVecRow).get(arr);
                } else if (null != fbMultiValue) {
                    return ((MultiStringValue)fbMultiValue).value(fbColumnVecRow);
                } else {
                    StringValue fbValue = new StringValue();
                    if (null != gremlinObject.value(fbValue)) {
                        arr = new byte[fbValue.valueAsByteBuffer().remaining()];
                        fbValue.valueAsByteBuffer().get(arr);
                    }
                }
                try {
                    return new String(arr, charset);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                Long longValue = getLong();
                if (null != longValue) {
                    fieldValue = longValue.toString();
                }
                break;
            case FLOAT:
            case DOUBLE:
                Double doubleValue = getDouble();
                if (null != doubleValue) {
                    fieldValue = doubleValue.toString();
                }
                break;
            default:
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getString()");
                break;
        }
        return fieldValue;
    }

    private void AddObjectsToList(List<IGraphResult> IGraphResultList, IGraphResultObjectType type, Table values, int len) {
        for (int i = 0; i < len; ++i) {
            IGraphResult subObject = new IGraphResult();
            subObject.resource.fbMultiValue = values;
            subObject.resource.fbColumnVecRow = i;
            subObject.resource.setGremlinSingleResult(IGraphResultSet);
            subObject.resource.resultObjectType = type;
            IGraphResultList.add(subObject);
        }
    }

    public List<IGraphResult> getList() {
        List<IGraphResult> IGraphResultList = new ArrayList<IGraphResult>();
        switch (getObjectType()) {
            case MULTI_INT8_VALUE: {
                MultiInt8Value values = new MultiInt8Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.INT8, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_INT16_VALUE: {
                MultiInt16Value values = new MultiInt16Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.INT16, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_INT32_VALUE: {
                MultiInt32Value values = new MultiInt32Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.INT32, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_INT64_VALUE: {
                MultiInt64Value values = new MultiInt64Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.INT64, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_UINT8_VALUE: {
                MultiUInt8Value values = new MultiUInt8Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.UINT8, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_UINT16_VALUE: {
                MultiUInt16Value values = new MultiUInt16Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.UINT16, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_UINT32_VALUE: {
                MultiUInt32Value values = new MultiUInt32Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.UINT32, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_UINT64_VALUE: {
                MultiUInt64Value values = new MultiUInt64Value();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.UINT64, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_FLOAT_VALUE: {
                MultiFloatValue values = new MultiFloatValue();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.FLOAT, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_DOUBLE_VALUE: {
                MultiDoubleValue values = new MultiDoubleValue();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.DOUBLE, values, values.valueLength());
                return IGraphResultList;
            }
            case MULTI_STRING_VALUE: {
                MultiStringValue values = new MultiStringValue();
                if (null == gremlinObject.value(values)) {
                    return null;
                }
                AddObjectsToList(IGraphResultList, IGraphResultObjectType.STRING, values, values.valueLength());
                return IGraphResultList;
            }
            case LIST: {
                ObjectVec objectVec = new ObjectVec();
                if (null == gremlinObject.value(objectVec)) {
                    return null;
                }
                return getListImp(objectVec);
            }
            default: {
                log.warn("Actual object type is [" + getObjectType().toString() + "] when call getList()");
                return null;
            }
        }
    }

    public Set<Object> getSet() {
        if (IGraphResultObjectType.SET != getObjectType()) {
            log.warn("Actual object type is [" + getObjectType().toString() + "] when call getSet()");
            return null;
        }
        ObjectSet objectSet = new ObjectSet();
        if (null == gremlinObject.value(objectSet)) {
            return null;
        }
        ObjectVec objectVec = objectSet.object();
        if (null == objectVec) {
            return null;
        }
        List<IGraphResult> IGraphResultList = getListImp(objectVec);
        Set<Object> set = new HashSet<>(IGraphResultList.size());
        for (IGraphResult IGraphResult : IGraphResultList) {
            IGraphResultObjectType type = IGraphResult.getObjectType();
            switch (type) {
                case INT8: case INT16: case INT32: case INT64:
                case UINT8:case UINT16:case UINT32:case UINT64: {
                    set.add(IGraphResult.getInt());
                    break;
                }
                case FLOAT: case DOUBLE: {
                    set.add(IGraphResult.getDouble());
                    break;
                }
                case STRING: {
                    set.add(IGraphResult.getString());
                    break;
                }
                default:
                    set.add(IGraphResult);
            }
        }
        return set;
    }

    public Map<Result, Long> getBulkSet() {

        if (IGraphResultObjectType.BULKSET != getObjectType()) {
            log.warn("Actual object type is [" + getObjectType().toString() + "] when call getBulkSet()");
            return null;
        }
        ObjectBulkSet objectBulkSet = new ObjectBulkSet();
        if (null == gremlinObject.value(objectBulkSet)) {
            return null;
        }
        ObjectVec keyObjectVec = objectBulkSet.keyObject();
        List<IGraphResult> keyObjectList = getListImp(keyObjectVec);
        if (null == keyObjectList) {
            return null;
        }
        Map<Result, Long> resultMap = new LinkedHashMap<>();
        for (int i = 0; i < keyObjectList.size(); ++i) {
            Long bulk = objectBulkSet.bulkCount(i);
            resultMap.put(keyObjectList.get(i), bulk);
        }
        return resultMap;
    }

    public Map.Entry<IGraphResult, IGraphResult> getMapEntry() {
        if (IGraphResultObjectType.MapEntry != getObjectType()) {
            log.warn("Actual object type is [" + getObjectType().toString() + "] when call getMapEntry()");
            return null;
        }
        ObjectMapEntry objectMapEntry = new ObjectMapEntry();
        if (null == gremlinObject.value(objectMapEntry)) {
            return null;
        }
        GremlinObject keyFbObject = objectMapEntry.keyObject();
        GremlinObject valueFbObject = objectMapEntry.valueObject();

        IGraphResult keyObject = new IGraphResult();
        keyObject.resource.setGremlinObject(keyFbObject);
        keyObject.resource.setGremlinSingleResult(IGraphResultSet);
        IGraphResult valueObject = new IGraphResult();
        valueObject.resource.setGremlinObject(valueFbObject);
        valueObject.resource.setGremlinSingleResult(IGraphResultSet);

        Map.Entry<IGraphResult, IGraphResult> result =
                new SimpleEntry<IGraphResult, IGraphResult>(keyObject, valueObject);
        return result;
    }

    public Map<Object, IGraphResult> getMap() {
        if (IGraphResultObjectType.MAP != getObjectType()) {
            log.warn("Actual object type is [" + getObjectType().toString() + "] when call getMap()");
            return null;
        }
        ObjectMap objectMap = new ObjectMap();
        if (null == gremlinObject.value(objectMap)) {
            return null;
        }
        ObjectVec keyObjectVec = objectMap.keyObject();
        List<IGraphResult> keyObjectList = getListImp(keyObjectVec);
        if (null == keyObjectList) {
            return null;
        }
        ObjectVec valueObjectVec = objectMap.valueObject();
        List<IGraphResult> valueObjectList = getListImp(valueObjectVec);
        if (null == valueObjectList) {
            return null;
        }
        if (valueObjectList.size() != keyObjectList.size()) {
            log.warn("keys' size not equal to values' size when call getMap");
            return null;
        }
        Map<Object, IGraphResult> resultMap = new LinkedHashMap<>();
        for (int i = 0; i < keyObjectList.size(); ++i) {
            IGraphResult keyIGraphResult = keyObjectList.get(i);
            IGraphResultObjectType type = keyIGraphResult.getObjectType();
            switch (type) {
                case INT8: case INT16: case INT32: case INT64:
                case UINT8:case UINT16:case UINT32:case UINT64: {
                    resultMap.put(keyIGraphResult.getInt(), valueObjectList.get(i));
                    break;
                }
                case FLOAT: case DOUBLE: {
                    resultMap.put(keyIGraphResult.getDouble(), valueObjectList.get(i));
                    break;
                }
                case STRING: {
                    resultMap.put(keyIGraphResult.getString(), valueObjectList.get(i));
                    break;
                }
                default:
                    resultMap.put(keyIGraphResult, valueObjectList.get(i));
            }
        }
        return resultMap;
    }

    public IGraphPath getPath() {
        if (IGraphResultObjectType.PATH != getObjectType()) {
            log.warn("Actual object type is [" + getObjectType().toString() + "] when call getPath()");
            return null;
        }
        ObjectPath objectPath = new ObjectPath();
        IGraphPath path = new IGraphPath();
        if (null == gremlinObject.value(objectPath)) {
            return null;
        }
        ObjectVec objectVec = objectPath.objectList();
        path.iGraphResultList = getListImp(objectVec);
        if (null == path.iGraphResultList) {
            return null;
        }
        path.resultList = new ArrayList<>();
        for (int i = 0; i < path.iGraphResultList.size(); ++i) {
            IGraphResult iGraphResult = path.iGraphResultList.get(i);
            path.resultList.add(iGraphResult.getObject());
        }
        if (path.resultList.size() != path.iGraphResultList.size()) {
            log.warn("the size of resultList and iGraphResultList is not equal in Path");
            return null;
        }
        for (int i = 0; i < objectPath.lablesLength(); ++i) {
            Set<String> labelSet = new HashSet<String>();
            MultiStringValue labels = objectPath.lables(i);
            for (int j = 0; j < labels.valueLength(); ++j) {
                labelSet.add(labels.value(j));
            }
            path.labels.add(labelSet);
        }
        return path;
    }

    private List<IGraphResult> getBasicTypeObjectByColumnVector(Table columnVecValue, int size, IGraphResultObjectType type) {
        List<IGraphResult> IGraphResultList = new ArrayList<IGraphResult>(size);
        for (int i = 0; i < size; ++i) {
            IGraphResult subObject = new IGraphResult();
            subObject.resource.fbColumnVec = columnVecValue;
            subObject.resource.fbColumnVecRow = i;
            subObject.resource.setGremlinSingleResult(IGraphResultSet);
            subObject.resource.resultObjectType = type;
            IGraphResultList.add(subObject);
        }
        return IGraphResultList;
    }

    private List<IGraphResult> getListImp(ObjectVec objectVec) {
        List<IGraphResult> iGraphResultList = null;
        if (ObjectVecValue.ObjectVecByRow == objectVec.valueType()) {
            ObjectVecByRow objectVecByRow = new ObjectVecByRow();
            if (null == objectVec.value(objectVecByRow)) {
                return null;
            }
            iGraphResultList = new ArrayList<IGraphResult>(objectVecByRow.objectLength());
            for (int i = 0; i < objectVecByRow.objectLength(); ++i) {
                IGraphResult subObject = new IGraphResult();
                subObject.resource.setGremlinObject(objectVecByRow.object(i));
                subObject.resource.setGremlinSingleResult(IGraphResultSet);
                iGraphResultList.add(subObject);
            }
        } else if (ObjectVecValue.ObjectVecOfElementByColumn == objectVec.valueType()) {
            ObjectVecOfElementByColumn objectVecOfElementByColumn = new ObjectVecOfElementByColumn();
            if (null == objectVec.value(objectVecOfElementByColumn)) {
                return null;
            }
            int matchdocIndexVecLength = objectVecOfElementByColumn.matchdocIndexVecLength();
            iGraphResultList = new ArrayList<IGraphResult>(matchdocIndexVecLength);
            for (int i = 0; i < matchdocIndexVecLength; ++i) {
                IGraphResult subObject = new IGraphResult();
                subObject.resource.fbColumnVec = objectVecOfElementByColumn;
                subObject.resource.fbColumnVecRow = i;
                subObject.resource.setGremlinSingleResult(IGraphResultSet);
                Long type = objectVecOfElementByColumn.elementTypeVec(i);
                subObject.resource.resultObjectType = elementType2ObjectType(type);
                iGraphResultList.add(subObject);
            }
        } else {
            switch (objectVec.valueType()) {
                case ObjectVecValue.BoolValueColumn: {
                    BoolValueColumn columnVecValue = new BoolValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.BOOL);
                    break;
                }
                case ObjectVecValue.Int8ValueColumn: {
                    Int8ValueColumn columnVecValue = new Int8ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.INT8);
                    break;
                }
                case ObjectVecValue.Int16ValueColumn: {
                    Int16ValueColumn columnVecValue = new Int16ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.INT16);
                    break;
                }
                case ObjectVecValue.Int32ValueColumn: {
                    Int32ValueColumn columnVecValue = new Int32ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.INT32);
                    break;
                }
                case ObjectVecValue.Int64ValueColumn: {
                    Int64ValueColumn columnVecValue = new Int64ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.INT64);
                    break;
                }
                case ObjectVecValue.UInt8ValueColumn: {
                    UInt8ValueColumn columnVecValue = new UInt8ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.UINT8);
                    break;
                }
                case ObjectVecValue.UInt16ValueColumn: {
                    UInt16ValueColumn columnVecValue = new UInt16ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.UINT16);
                    break;
                }
                case ObjectVecValue.UInt32ValueColumn: {
                    UInt32ValueColumn columnVecValue = new UInt32ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.UINT32);
                    break;
                }
                case ObjectVecValue.UInt64ValueColumn: {
                    UInt64ValueColumn columnVecValue = new UInt64ValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.UINT64);
                    break;
                }
                case ObjectVecValue.FloatValueColumn: {
                    FloatValueColumn columnVecValue = new FloatValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.FLOAT);
                    break;
                }
                case ObjectVecValue.DoubleValueColumn: {
                    DoubleValueColumn columnVecValue = new DoubleValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.DOUBLE);
                    break;
                }
                case ObjectVecValue.StringValueColumn: {
                    StringValueColumn columnVecValue = new StringValueColumn();
                    if (null == objectVec.value(columnVecValue)) {
                        return null;
                    }
                    int length = columnVecValue.valueLength();
                    iGraphResultList = getBasicTypeObjectByColumnVector(columnVecValue, length, IGraphResultObjectType.STRING);
                    break;
                }
                default:
                    log.warn("unexpected type [" + objectVec.valueType() + "] when decode gremlin result by column");
                    return null;
            }
        }
        return iGraphResultList;
    }

    public IGraphVertex getVertex() {
        return (IGraphVertex)getElement(IGraphResultObjectType.VERTEX);
    }

    public IGraphEdge getEdge() {
        return (IGraphEdge)getElement(IGraphResultObjectType.EDGE);
    }

    public IGraphElement getElement() {
        return (IGraphElement)getElement(IGraphResultObjectType.ELEMENT);
    }

    public IGraphProperty getProperty() {
        return (IGraphProperty)getElement(IGraphResultObjectType.PROPERTY);
    }

    private IGraphElement getElement(IGraphResultObjectType wantedObjectType) {
        if (wantedObjectType != getObjectType() && (wantedObjectType == IGraphResultObjectType.ELEMENT &&
                (getObjectType() != IGraphResultObjectType.VERTEX && getObjectType() != IGraphResultObjectType.EDGE))) {
            log.warn("Actual object type is [" + getObjectType().toString() + "] when call get" + wantedObjectType.toString() + "()");
            return null;
        }
        IGraphElement IGraphElement = null;
        ElementMeta elementMeta = null;
        Long cursor = null;
        if (null != fbColumnVec) {
            ObjectVecOfElementByColumn fbElementVec = (ObjectVecOfElementByColumn)fbColumnVec;
            Long elementMetaId = fbElementVec.elementMetaIdVec(fbColumnVecRow);
            if (null == IGraphResultSet || null == IGraphResultSet.getElementMetaMap()) {
                return null;
            }
            elementMeta = IGraphResultSet.getElementMetaMap().get(elementMetaId);
            cursor = fbElementVec.matchdocIndexVec(fbColumnVecRow);
        } else {
            ObjectElement fbElement = new ObjectElement();
            if (null == gremlinObject.value(fbElement)) {
                return null;
            }
            Long elementMetaId = fbElement.elementMetaId();
            if (null == IGraphResultSet || null == IGraphResultSet.getElementMetaMap()) {
                return null;
            }
            elementMeta = IGraphResultSet.getElementMetaMap().get(elementMetaId);
            if (null == elementMeta) {
                return null;
            }
            cursor = fbElement.matchdocIndex();
        }
        wantedObjectType = wantedObjectType == IGraphResultObjectType.ELEMENT ? getObjectType() : wantedObjectType;
        switch (wantedObjectType) {
            case VERTEX:
                IGraphElement = new IGraphVertex(elementMeta, cursor.intValue());
                break;
            case EDGE:
                IGraphElement = new IGraphEdge(elementMeta, cursor.intValue());
                break;
            case PROPERTY:
                IGraphElement = new IGraphProperty(elementMeta, cursor.intValue());
                break;
            default:
                break;
        }
        return IGraphElement;
    }

    public <T> T get(final Class<T> clazz) {
        try {
            if (MultiNumber.class == clazz) {
                List<? extends Number> value = getMultiNumber();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiByte.class == clazz) {
                List<Byte> value = getMultiInt8Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiShort.class == clazz) {
                List<Short> value = getMultiInt16Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiInt.class == clazz) {
                List<Integer> value = getMultiInt32Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiLong.class == clazz) {
                List<Long> value = getMultiInt64Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiUInt8.class == clazz) {
                List<Integer> value = getMultiUInt8Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiUInt16.class == clazz) {
                List<Integer> value = getMultiUInt16Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiUInt32.class == clazz) {
                List<Long> value = getMultiUInt32Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiUInt64.class == clazz) {
                List<Long> value = getMultiUInt64Value();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiFloat.class == clazz) {
                List<Float> value = getMultiFloatValue();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiDouble.class == clazz) {
                List<Double> value = getMultiDoubleValue();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (MultiString.class == clazz) {
                List<String> value = getMultiString();
                return clazz.getDeclaredConstructor(List.class).newInstance(value);
            } else if (BulkSet.class.isAssignableFrom(clazz)) {
                Map<Result, Long> value = getBulkSet();
                return clazz.getDeclaredConstructor(Map.class).newInstance(value);
            } else if (List.class == clazz) {
                List<IGraphResult> value = getList();
                return (T) value;
            } else if (Map.Entry.class == clazz) {
                Map.Entry<IGraphResult, IGraphResult> value = getMapEntry();
                return (T) value;
            } else if (Set.class.isAssignableFrom(clazz)) {
                Set<Object>  value = getSet();
                return (T) value;
            } else if (Map.class.isAssignableFrom(clazz)) {
                Map<Object, IGraphResult>  value = getMap();
                return (T) value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NumberFormatException();
        }
        return null;
    }
    public <T> T set(T input) {
        T output = input;
        return output;
    }

    @Override
    public String toString() {
        switch (getObjectType()) {
            case BOOL:
                return String.valueOf(getBoolean());
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                return String.valueOf(getLong());
            case FLOAT:
            case DOUBLE:
                return String.valueOf(getDouble());
            case STRING:
                return getString();
            case LIST:
                return listToString();
            case SET:
                return setToString();
            case MapEntry:
                return mapEntryToString();
            case MAP:
                return mapToString();
            case VERTEX:
            case EDGE:
            case PROPERTY:
                return elementToString(getObjectType());
            case PATH:
                IGraphPath path = getPath();
                if (null != path) {
                    return path.toString();
                }
                break;
            case BULKSET:
                return bulkSetToString();
            default:  // TODO(fancheng) 缺少很多类型
                break;
        }
        return null;
    }

    private String listToString() {
        StringBuilder ss = new StringBuilder(128);
        List<IGraphResult> iGraphResultList = getList();
        if (null == iGraphResultList) {
            return null;
        }
        ss.append("[");
        for (int i = 0; i < iGraphResultList.size(); ++i) {
            ss.append(iGraphResultList.get(i).toJson()).append(",");
            if (i == iGraphResultList.size()-1) {
                ss.setLength(ss.length() - 1);
            }
        }
        ss.append("]");
        return ss.toString();
    }

    private String setToString() {
        StringBuilder ss = new StringBuilder(128);
        Set<Object> objectSet = getSet();
        if (null == objectSet) {
            return null;
        }
        ss.append("[");
        for (Object obj : objectSet) {
            if (obj instanceof String) {
                obj = "\"" + obj + "\"";
            }
            ss.append(obj).append(",");
        }
        if (!objectSet.isEmpty()) {
            ss.setLength(ss.length() - 1);
        }
        ss.append("]");
        return ss.toString();
    }

    private String bulkSetToString() {
        StringBuilder ss = new StringBuilder(128);
        Map<Result, Long> resultObjectBulkSet = getBulkSet();
        if (null == resultObjectBulkSet) {
                return null;
           }
        ss.append("[");
        for (Map.Entry<Result, Long> entry : resultObjectBulkSet.entrySet()) {
                String key = "\"" + entry.getKey() + "\"";
                Long bulk = entry.getValue();
                ss.append(key).append(":").append(bulk).append(",");
            }
        if (!resultObjectBulkSet.isEmpty()) {
            ss.setLength(ss.length() - 1);
        }
        ss.append("]");
        return ss.toString();
    }

    private String mapEntryToString() {
        StringBuilder ss = new StringBuilder(128);
        Map.Entry<IGraphResult, IGraphResult> resultObjectMapEntry = getMapEntry();
        if (null == resultObjectMapEntry) {
            return null;
        }
        String key = "\"" + resultObjectMapEntry.getKey().toString() + "\"";
        Object value = resultObjectMapEntry.getValue().toJson();
        ss.append(key).append(":").append(value).append(";");
        return ss.toString();
    }

    private String mapToString() {
        StringBuilder ss = new StringBuilder(128);
        Map<Object, IGraphResult> resultObjectMap = getMap();
        if (null == resultObjectMap) {
            return null;
        }
        ss.append("{");
        for (Map.Entry<Object, IGraphResult> entry : resultObjectMap.entrySet()) {
            String key = "\"" + entry.getKey().toString() + "\"";
            Object value = entry.getValue().toJson();
            ss.append(key).append(":").append(value).append(",");
        }
        if (!resultObjectMap.isEmpty()) {
            ss.setLength(ss.length() - 1);
        }
        ss.append("}");
        return ss.toString();
    }

    private String elementToString(IGraphResultObjectType type) {
        IGraphElement IGraphElement = getElement(type);
        if (null == IGraphElement) {
            return null;
        }
        return IGraphElement.toString();
    }
}
