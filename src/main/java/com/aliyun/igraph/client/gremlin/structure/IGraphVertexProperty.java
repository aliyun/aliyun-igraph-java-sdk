package com.aliyun.igraph.client.gremlin.structure;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotSupportedException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author alibaba
 */
public class IGraphVertexProperty<V> extends IGraphElement implements VertexProperty<V> {
    private final IGraphVertex IGraphVertex;
    private final String key;
    private final V value;

    private static final Logger log = LoggerFactory.getLogger(IGraphElement.class);

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

    @Override
    public Object toJson () {
        JsonObject jsonObject = new JsonObject();
        if (null == key) {
            log.warn("get property's key failed when generate json");
            return null;
        }
        Gson gson = new Gson();
        jsonObject.add(key, gson.toJsonTree(value));
        return jsonObject;
    }
}
