package com.aliyun.igraph.client.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import javax.ws.rs.NotSupportedException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author alibaba
 */
public class IGraphVertexProperty<V> extends IGraphElement implements VertexProperty<V> {
    private final IGraphVertex IGraphVertex;
    private final String key;
    private final V value;

    public IGraphVertexProperty(IGraphVertex IGraphVertex, String key, V value) {
        super();
        this.IGraphVertex = IGraphVertex;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public V value() throws NoSuchElementException {
        return value;
    }

    @Override
    public boolean isPresent() {
        return value != null;
    }

    @Override
    public Vertex element() {
        return IGraphVertex;
    }

    @Override
    public <V> Property<V> property(String key) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Iterator<Property> properties(String... propertyKeys) {
        throw new NotSupportedException("method is not supported");
    }
}
