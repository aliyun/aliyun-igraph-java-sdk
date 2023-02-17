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
package org.apache.tinkerpop.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyVertexProperty;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Property} denotes a key/value pair associated with an {@link Edge}. A property is much like a Java8
 * {@code Optional} in that a property can be not present (i.e. empty). The key of a property is always a
 * String and the value of a property is an arbitrary Java object. Each underlying graph engine will typically have
 * constraints on what Java objects are allowed to be used as values.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public interface Property<V> {

    /**
     * The key of the property.
     *
     * @return The property key
     */
    public String key();

    /**
     * The value of the property.
     *
     * @return The property value
     * @throws NoSuchElementException thrown if the property is empty
     */
    public V value() throws NoSuchElementException;

    /**
     * Whether the property is empty or not.
     *
     * @return True if the property exists, else false
     */
    public boolean isPresent();

    /**
     * If the property is present, the consume the value as specified by the {@link Consumer}.
     *
     * @param consumer The consumer to process the existing value.
     */
    public default void ifPresent(final Consumer<? super V> consumer) {
        if (this.isPresent())
            consumer.accept(this.value());
    }

    /**
     * If the value is present, return the value, else return the provided value.
     *
     * @param otherValue The value to return if the property is not present
     * @return A value
     */
    public default V orElse(final V otherValue) {
        return this.isPresent() ? this.value() : otherValue;
    }

    /**
     * If the value is present, return the value, else generate a value given the {@link Supplier}.
     *
     * @param valueSupplier The supplier to use to generate a value if the property is not present
     * @return A value
     */
    public default V orElseGet(final Supplier<? extends V> valueSupplier) {
        return this.isPresent() ? this.value() : valueSupplier.get();
    }

    /**
     * If the value is present, return the value, else throw the exception generated by the {@link Supplier}.
     *
     * @param exceptionSupplier The supplier to generate an exception if the property is not present
     * @param <E>               The exception type
     * @return A value
     * @throws E if the property is not present, the exception is thrown
     */
    public default <E extends Throwable> V orElseThrow(final Supplier<? extends E> exceptionSupplier) throws E {
        if (this.isPresent()) return this.value();
        else
            throw exceptionSupplier.get();
    }

    /**
     * Get the element that this property is associated with.
     *
     * @return The element associated with this property (i.e. {@link Vertex}, {@link Edge}, or {@link VertexProperty}).
     */
    public Element element();

    /**
     * Remove the property from the associated element.
     */
    public void remove();

    /**
     * Create an empty property that is not present.
     *
     * @param <V> The value class of the empty property
     * @return A property that is not present
     */
    public static <V> Property<V> empty() {
        return EmptyVertexProperty.instance();
    }

    /**
     * Common exceptions to use with a property.
     */
    public static class Exceptions {

        private Exceptions() {
        }

        public static IllegalArgumentException propertyKeyCanNotBeEmpty() {
            return new IllegalArgumentException("Property key can not be the empty string");
        }

        public static IllegalArgumentException propertyKeyCanNotBeNull() {
            return new IllegalArgumentException("Property key can not be null");
        }

        public static IllegalArgumentException propertyValueCanNotBeNull() {
            return new IllegalArgumentException("Property value can not be null");
        }

        public static IllegalArgumentException propertyKeyCanNotBeAHiddenKey(final String key) {
            return new IllegalArgumentException("Property key can not be a hidden key: " + key);
        }

        public static IllegalStateException propertyDoesNotExist() {
            return new IllegalStateException("The property does not exist as it has no key, value, or associated element");
        }

        public static IllegalStateException propertyDoesNotExist(final Element element, final String key) {
            return new IllegalStateException("The property does not exist as the key has no associated value for the provided element: " + element + ":" + key);
        }

        public static IllegalArgumentException dataTypeOfPropertyValueNotSupported(final Object val) {
            return dataTypeOfPropertyValueNotSupported(val, null);
        }

        public static IllegalArgumentException dataTypeOfPropertyValueNotSupported(final Object val, final Exception rootCause) {
            return new IllegalArgumentException(String.format("Property value [%s] is of type %s is not supported", val, val.getClass()), rootCause);
        }

        public static IllegalStateException propertyRemovalNotSupported() {
            return new IllegalStateException("Property removal is not supported");
        }
    }

}
