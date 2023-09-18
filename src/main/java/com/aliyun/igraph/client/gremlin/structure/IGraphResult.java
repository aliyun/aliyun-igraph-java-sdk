package com.aliyun.igraph.client.gremlin.structure;


import com.google.gson.JsonObject;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.nio.charset.Charset;

/**
 * @author alibaba
 */
public class IGraphResult implements Result {
    public static final Charset utf_8 = Charset.forName("utf-8");
    public IGraphResultResource resource;

    public IGraphResult() {
        super();
        resource = new IGraphResultResource(); }

    private enum EncodeType {
        UTF8("UTF-8"), GBK("GBK");
        String value;
        EncodeType(String value) { this.value = value; }
        @Override
        public String toString() { return value; }
    }

    public IGraphResultObjectType getObjectType() { return resource.getObjectType(); }

    @Override
    public String getString() { return resource.getString(utf_8); }

    @Override
    public int getInt() {
        Integer fieldValue = resource.getInt();
        if (fieldValue == null) {
            throw new NumberFormatException();
        }
        return fieldValue;
    }

    @Override
    public byte getByte() {
        Byte fieldValue = resource.getByte();
        if (fieldValue == null) {
            throw new NumberFormatException();
        }
        return fieldValue;
    }

    @Override
    public short getShort() {
        Short fieldValue = resource.getShort();
        if (fieldValue == null) {
            throw new NumberFormatException();
        }
        return fieldValue;
    }

    @Override
    public long getLong() {
        Long fieldValue = resource.getLong();
        if (fieldValue == null) {
            throw new NumberFormatException();
        }
        return fieldValue;
    }

    @Override
    public float getFloat() {
        Float fieldValue = resource.getFloat();
        if (fieldValue == null) {
            throw new NumberFormatException();
        }
        return fieldValue;
    }

    @Override
    public double getDouble() {
        Double fieldValue = resource.getDouble();
        if (fieldValue == null) {
            throw new NumberFormatException();
        }
        return fieldValue;
    }

    @Override
    public boolean getBoolean() {
        Boolean fieldValue = resource.getBoolean();
        if (fieldValue == null) {
            throw new NumberFormatException();
        }
        return fieldValue;
    }

    @Override
    public boolean isNull() { return resource.isNull(); }

    @Override
    public IGraphVertex getVertex() { return resource.getVertex(); }

    @Override
    public IGraphEdge getEdge() { return resource.getEdge(); }

    @Override
    public IGraphElement getElement() { return resource.getElement(); }

    @Override
    public IGraphPath getPath() { return resource.getPath(); }

    @Override
    public IGraphProperty getProperty() { return resource.getProperty(); }

    @Override
    public <V> VertexProperty<V> getVertexProperty() {  throw new UnsupportedOperationException("method is not supported"); }

    @Override
    public <T> T get(final Class<? extends T> clazz) {return resource.get(clazz); }

    @Override
    public Object getObject() { return resource.getObject(); }

    @Override
    public Object getJson() {
        return toJsonObject();
    }

    protected String toJson() {
        return resource.getObjectType() == IGraphResultObjectType.STRING ?  "\"" + resource.toString() + "\"" : resource.toString();
    }

    protected Object toJsonObject(){
        return resource.toJson();
    }

    @Override
    public String toString() {
        return resource.toString();
    }
}
