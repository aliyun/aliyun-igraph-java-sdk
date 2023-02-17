package com.aliyun.igraph.client.gremlin.structure;

import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.structure.*;

/**
 * @author alibaba
 */
public class IGraphUpdateResult implements Result {
    String responseBody;

    IGraphUpdateResult(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String getString() {
        return responseBody;
    }

    @Override
    public int getInt() {
        throw new NumberFormatException();
    }

    @Override
    public byte getByte() {
        throw new NumberFormatException();
    }

    @Override
    public short getShort() {
        throw new NumberFormatException();
    }

    @Override
    public long getLong() {
        throw new NumberFormatException();
    }

    @Override
    public float getFloat() {
        throw new NumberFormatException();
    }

    @Override
    public double getDouble() {
        throw new NumberFormatException();
    }

    @Override
    public boolean getBoolean() {
        throw new NumberFormatException();
    }

    @Override
    public boolean isNull() {
        return responseBody == null;
    }

    @Override
    public Vertex getVertex() {
        throw new NumberFormatException();
    }

    @Override
    public Edge getEdge() {
        throw new NumberFormatException();
    }

    @Override
    public Element getElement() {
        throw new NumberFormatException();
    }

    @Override
    public Path getPath() {
        throw new NumberFormatException();
    }

    @Override
    public <V> Property<V> getProperty() {
        throw new NumberFormatException();
    }

    @Override
    public <V> VertexProperty<V> getVertexProperty() {
        throw new NumberFormatException();
    }

    @Override
    public Object getObject() {
        return responseBody;
    }

    @Override
    public <T> T get(final Class<? extends T> clazz) {
        throw new NumberFormatException();
    }
}
