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

import org.apache.tinkerpop.gremlin.structure.util.Host;

/**
 * A {@link Graph} is a container object for a collection of {@link Vertex}, {@link Edge}, {@link VertexProperty},
 * and {@link Property} objects.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 * @author Pieter Martin
 */
public interface Graph extends AutoCloseable, Host {

    /**
     * Configuration key used by GraphFactory to determine which graph to instantiate.
     */
    public static final String GRAPH = "gremlin.graph";

    /**
     * This should only be used by providers to create keys, labels, etc. in a namespace safe from users.
     * Users are not allowed to generate property keys, step labels, etc. that are key'd "hidden".
     */
    public static class Hidden {

        /**
         * The prefix to denote that a key is a hidden key.
         */
        private static final String HIDDEN_PREFIX = "~";
        private static final int HIDDEN_PREFIX_LENGTH = HIDDEN_PREFIX.length();

        /**
         * Turn the provided key into a hidden key. If the key is already a hidden key, return key.
         *
         * @param key The key to make a hidden key
         * @return The hidden key
         */
        public static String hide(final String key) {
            return isHidden(key) ? key : HIDDEN_PREFIX.concat(key);
        }

        /**
         * Turn the provided hidden key into an non-hidden key. If the key is not a hidden key, return key.
         *
         * @param key The hidden key
         * @return The non-hidden representation of the key
         */
        public static String unHide(final String key) {
            return isHidden(key) ? key.substring(HIDDEN_PREFIX_LENGTH) : key;
        }

        /**
         * Determines whether the provided key is a hidden key or not.
         *
         * @param key The key to check for hidden status
         * @return Whether the provided key is a hidden key or not
         */
        public static boolean isHidden(final String key) {
            return key.startsWith(HIDDEN_PREFIX);
        }
    }

    @Override
    void close() throws Exception;

}
