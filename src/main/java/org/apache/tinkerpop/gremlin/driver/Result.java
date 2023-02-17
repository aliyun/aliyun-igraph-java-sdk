/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.driver;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.Iterator;

/**
 * A {@code Result} represents a result value from the server-side {@link Iterator} of results.  This would be
 * one item from that result set.  This class provides methods for coercing the result {@link Object} to an
 * expected type.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public interface Result {
    /**
     * Gets the result item by coercing it to a {@code String} via {@code toString()}.
     *
     * @return String
     */
    public String getString();

    /**
     * Gets the result item by coercing it to an {@code int}.
     *
     * @return int
     * @throws NumberFormatException if the value is not parsable as an {@code int}.
     */
    public int getInt();

    /**
     * Gets the result item by coercing it to an {@code byte}.
     *
     * @return byte
     * @throws NumberFormatException if the value is not parsable as an {@code byte}.
     */
    public byte getByte();

    /**
     * Gets the result item by coercing it to an {@code short}.
     *
     * @return short
     * @throws NumberFormatException if the value is not parsable as an {@code short}.
     */
    public short getShort();

    /**
     * Gets the result item by coercing it to an {@code long}.
     *
     * @return long
     * @throws NumberFormatException if the value is not parsable as an {@code long}.
     */
    public long getLong();

    /**
     * Gets the result item by coercing it to an {@code float}.
     *
     * @return float
     * @throws NumberFormatException if the value is not parsable as an {@code float}.
     */
    public float getFloat();

    /**
     * Gets the result item by coercing it to an {@code double}.
     *
     * @return double
     * @throws NumberFormatException if the value is not parsable as an {@code double}.
     */
    public double getDouble();

    /**
     * Gets the result item by coercing it to an {@code boolean}.
     *
     * @return boolean
     * @throws NumberFormatException if the value is not parsable as an {@code boolean}.
     */
    public boolean getBoolean();

    /**
     * Determines if the result item is null or not.  This is often a good check prior to calling other methods to
     * get the object as they could throw an unexpected {@link NullPointerException} if the result item is
     * {@code null}.
     *
     * @return boolean
     */
    public boolean isNull();

    /**
     * Gets the result item by casting it to a {@link Vertex}.
     *
     * @return Vertex
     */
    public Vertex getVertex();
    /**
     * Gets the result item by casting it to an {@link Edge}.
     *
     * @return Edge
     */
    public Edge getEdge();
    /**
     * Gets the result item by casting it to an {@link Element}.
     *
     * @return Element
     */
    public Element getElement();

    /**
     * Gets the result item by casting it to a {@link Path}.
     *
     * @return Path
     */
    public Path getPath();

    /**
     * Gets the result item by casting it to a {@link Property}.
     *
     * @param <V> value type
     * @return Property
     */
    public <V> Property<V> getProperty();

    /**
     * Gets the result item by casting it to a {@link VertexProperty}.
     *
     * @param <V> value type
     * @return VertexProperty
     */
    public <V> VertexProperty<V> getVertexProperty();

    /**
     * Gets the result item by casting it to the specified {@link Class}.
     *
     * @param <T> MultiNumber、Map、Set...
     * @param clazz T
     * @return T
     */
    public <T> T get(final Class<? extends T> clazz);

    /**
     * Gets the result item.
     *
     * @return Object
     */
    public Object getObject();
}
