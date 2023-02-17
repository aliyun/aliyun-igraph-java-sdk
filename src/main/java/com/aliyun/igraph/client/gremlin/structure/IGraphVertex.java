package com.aliyun.igraph.client.gremlin.structure;

import lombok.NonNull;
import org.apache.tinkerpop.gremlin.structure.*;

import javax.ws.rs.NotSupportedException;
import java.util.*;

/**
 * @author alibaba
 */
public class IGraphVertex extends IGraphElement implements Vertex {
    public IGraphVertex(@NonNull ElementMeta elementMeta, int cursor) {
        super(elementMeta, cursor);
    }

    @Override
    public String label() {
        return super.label();
    }

    @Override
    public Set<String> keys() {
        return super.keys();
    }

    @Override
    public <V> V value(String key) throws NoSuchElementException {
        return super.value(key);
    }

    @Override
    public void remove() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> Iterator<V> values(String... propertyKeys) {
        return super.values(propertyKeys);
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> VertexProperty<V> property(String key) {
         V value = (V)getObject(key);
         return new IGraphVertexProperty<>(this, key, value);
    }

    @Override
    public <V> VertexProperty<V> property(String key, V value) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> VertexProperty<V> property(String key, V value, Object... keyValues) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        List<VertexProperty<V>> vertexPropertyList = new ArrayList<>();
        for (String propertyKey : propertyKeys) {
            vertexPropertyList.add(property(propertyKey));
        }
        return vertexPropertyList.iterator();
    }
}
