package org.apache.tinkerpop.gremlin.process.traversal;

import java.io.Serializable;

/**
 * When a {@link TraversalSource} is manipulated and then a {@link Traversal} is spawned and mutated, a language
 * agnostic representation of those mutations is recorded in a bytecode instance. Bytecode is simply a list
 * of ordered instructions where an instruction is a string operator and a (flattened) array of arguments.
 * Bytecode is used by Translator instances which are able to translate a traversal in one language to another
 * by analyzing the bytecode as opposed to the Java traversal object representation on heap.
 * <p>
 * Bytecode can be serialized between environments and machines by way of a GraphSON representation.
 * Thus, Gremlin-Python can create bytecode in Python and ship it to Gremlin-Java for evaluation in Java.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Bytecode implements Cloneable, Serializable {
}
