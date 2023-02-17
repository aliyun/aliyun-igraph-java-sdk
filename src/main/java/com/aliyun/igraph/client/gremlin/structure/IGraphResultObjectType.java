package com.aliyun.igraph.client.gremlin.structure;

/**
 * @author alibaba
 */
public enum IGraphResultObjectType {
    UNKNOWN("Unknow"),
    BOOL("Bool"),
    INT8("Int8"),
    INT16("Int16"),
    INT32("Int32"),
    INT64("Int64"),
    UINT8("Uint8"),
    UINT16("Uint16"),
    UINT32("Uint32"),
    UINT64("Uint64"),
    FLOAT("Float"),
    DOUBLE("Double"),
    STRING("String"),
    LIST("List"),
    SET("Set"),
    MAP("Map"),
    BULKSET("BulkSet"),
    VERTEX("Vertex"),
    EDGE("Edge"),
    ELEMENT("Element"),
    PROPERTY("Property"),
    PATH("Path"),
    MapEntry("MapEntry"),
    MULTI_INT8_VALUE("MultiInt8Value"),
    MULTI_INT16_VALUE("MultiInt16Value"),
    MULTI_INT32_VALUE("MultiInt32Value"),
    MULTI_INT64_VALUE("MultiInt64Value"),
    MULTI_UINT8_VALUE("MultiUInt8Value"),
    MULTI_UINT16_VALUE("MultiUInt16Value"),
    MULTI_UINT32_VALUE("MultiUInt32Value"),
    MULTI_UINT64_VALUE("MultiUInt64Value"),
    MULTI_FLOAT_VALUE("MultiFloatValue"),
    MULTI_DOUBLE_VALUE("MultiDoubleValue"),
    MULTI_STRING_VALUE("MultiStringValue");

    String value;
    IGraphResultObjectType(String value) { this.value = value; }
    @Override
    public String toString() {
        return value;
    }
}
