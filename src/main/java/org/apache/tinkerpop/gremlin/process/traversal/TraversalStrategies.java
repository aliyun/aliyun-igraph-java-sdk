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
package org.apache.tinkerpop.gremlin.process.traversal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.NotSupportedException;

/**
 * A {@link Traversal} maintains a set of {@link TraversalStrategy} instances within a TraversalStrategies object.
 * TraversalStrategies are responsible for compiling a traversal prior to its execution.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface TraversalStrategies extends Serializable, Cloneable, Iterable<TraversalStrategy<?>> {

    static List<Class<? extends TraversalStrategy>> STRATEGY_CATEGORIES = Collections.unmodifiableList(Arrays
        .asList(TraversalStrategy.DecorationStrategy.class, TraversalStrategy.OptimizationStrategy.class,
            TraversalStrategy.ProviderOptimizationStrategy.class, TraversalStrategy.FinalizationStrategy.class,
            TraversalStrategy.VerificationStrategy.class));

    /**
     * Return an immutable list of the {@link TraversalStrategy} instances.
     *
     * @return List
     */
    public List<TraversalStrategy<?>> toList();

    /**
     * Return an {@code Iterator} of the {@link TraversalStrategy} instances.
     *
     * @return Iterator
     */
    @Override
    public Iterator<TraversalStrategy<?>> iterator();

    /**
     * Return the {@link TraversalStrategy} instance associated with the provided class.
     *
     * @param traversalStrategyClass the class of the strategy to get
     * @param <T>                    the strategy class type
     * @return an optional containing the strategy instance or not
     */
    public <T extends TraversalStrategy> Optional<T> getStrategy(final Class<T> traversalStrategyClass);

    /**
     * Add all the provided {@link TraversalStrategy} instances to the current collection. When all the provided
     * strategies have been added, the collection is resorted. If a strategy class is found to already be defined, it is
     * removed and replaced by the newly added one.
     *
     * @param strategies the traversal strategies to add
     * @return the newly updated/sorted traversal strategies collection
     */
    public TraversalStrategies addStrategies(final TraversalStrategy<?>... strategies);

    /**
     * Remove all the provided {@link TraversalStrategy} classes from the current collection. When all the provided
     * strategies have been removed, the collection is resorted.
     *
     * @param strategyClasses the traversal strategies to remove by their class
     * @return the newly updated/sorted traversal strategies collection
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public TraversalStrategies removeStrategies(final Class<? extends TraversalStrategy>... strategyClasses);

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public TraversalStrategies clone();

    /**
     * Sorts the list of provided strategies such that the {@link TraversalStrategy#applyPost()} and {@link
     * TraversalStrategy#applyPrior()} dependencies are respected.
     * Note, that the order may not be unique.
     *
     * @param strategies the traversal strategies to sort
     * @return Set
     */
    public static Set<TraversalStrategy<?>> sortStrategies(final Set<TraversalStrategy<?>> strategies) {
        throw new NotSupportedException("method is not supported");
    }

    static void visit(
        final Map<Class<? extends TraversalStrategy>, Set<Class<? extends TraversalStrategy>>> dependencyMap,
        final List<Class<? extends TraversalStrategy>> sortedStrategyClasses,
        final Set<Class<? extends TraversalStrategy>> seenStrategyClases,
        final List<Class<? extends TraversalStrategy>> unprocessedStrategyClasses,
        Class<? extends TraversalStrategy> strategyClass) {
        throw new NotSupportedException("method is not supported");
    }

}
