package org.apache.tinkerpop.gremlin.driver;

import org.apache.tinkerpop.gremlin.driver.message.RequestMessage;
import org.apache.tinkerpop.gremlin.process.traversal.Bytecode;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public interface Client {
    /**
     * Makes any initial changes to the builder and returns the constructed {@link RequestMessage}.  Implementers
     * may choose to override this message to append data to the request before sending.  By default, this method
     * will simply return the {@code builder} passed in by the caller.
     *
     * @param builder RequestMessage.Builder
     * @return RequestMessage.Builder
     */
    public RequestMessage.Builder buildMessage(final RequestMessage.Builder builder);

    /**
     * Called in the {@link #init} method.
     */
    public void initializeImplementation();

    /**
     * Chooses a {@link Connection} to write the message to.
     *
     * @param msg RequestMessage
     * @return Connection
     * @throws TimeoutException Timeout
     */
    public Connection chooseConnection(final RequestMessage msg) throws TimeoutException;

    /**
     * Asynchronous close of the {@code Client}.
     *
     * @return CompletableFuture
     */
    public CompletableFuture<Void> closeAsync();

    /**
     * Create a new {@code Client} that aliases the specified {@link Graph} name on the
     * server to a variable called "g" for the context of the requests made through that {@code Client}.
     *
     * @param graphOrTraversalSource rebinds the specified global Gremlin Server variable to "g"
     * @return Client
     */
    public Client alias(final String graphOrTraversalSource);

    /**
     * Creates a {@code Client} that supplies the specified set of aliases, thus allowing the user to re-name
     * one or more globally defined {@link Graph} server bindings for the context of
     * the created {@code Client}.
     *
     * @param aliases Map
     * @return Client
     */
    public Client alias(final Map<String, String> aliases);

    /**
     * Submit a {@link Traversal} to the server for remote execution.Results are returned as Traverser
     * instances and are therefore bulked, meaning that to properly iterate the contents of the result each
     * Traverser#bulk() must be examined to determine the number of times that object should be presented in
     * iteration.
     *
     * @param traversal Traversal
     * @return ResultSet
     */
    public ResultSet submit(final Traversal traversal);

    /**
     * An asynchronous version of {@link #submit(Traversal)}. Results are returned as Traverser instances and
     * are therefore bulked, meaning that to properly iterate the contents of the result each Traverser#bulk()
     * must be examined to determine the number of times that object should be presented in iteration.
     *
     * @param traversal Traversal
     * @return CompletableFuture
     */
    public CompletableFuture<ResultSet> submitAsync(final Traversal traversal);

    /**
     * Submit a {@link Bytecode} to the server for remote execution. Results are returned as Traverser
     * instances and are therefore bulked, meaning that to properly iterate the contents of the result each
     * Traverser#bulk() must be examined to determine the number of times that object should be presented in
     * iteration.
     *
     * @param bytecode request in the form of gremlin {@link Bytecode}
     * @return ResultSet
     */
    public ResultSet submit(final Bytecode bytecode);

    /**
     * A version of {@link #submit(Bytecode)} which provides the ability to set per-request options.
     *
     * @param bytecode request in the form of gremlin {@link Bytecode}
     * @param options  for the request
     * @return ResultSet
     * @see #submit(Bytecode)
     */
    public ResultSet submit(final Bytecode bytecode, final RequestOptions options);

    /**
     * An asynchronous version of {@link #submit(Traversal)}. Results are returned as Traverser instances and
     * are therefore bulked, meaning that to properly iterate the contents of the result each Traverser#bulk()
     * must be examined to determine the number of times that object should be presented in iteration.
     *
     * @param bytecode request in the form of gremlin {@link Bytecode}
     * @return CompletableFuture
     */
    public CompletableFuture<ResultSet> submitAsync(final Bytecode bytecode);

    /**
     * A version of {@link #submit(Bytecode)} which provides the ability to set per-request options.
     *
     * @param bytecode request in the form of gremlin {@link Bytecode}
     * @param options  for the request
     * @return CompletableFuture
     * @see #submitAsync(Bytecode)
     */
    public CompletableFuture<ResultSet> submitAsync(final Bytecode bytecode, final RequestOptions options);

    /**
     * Initializes the client which typically means that a connection is established to the server.  Depending on the
     * implementation and configuration this blocking call may take some time.  This method will be called
     * automatically if it is not called directly and multiple calls will not have effect.
     *
     * @param <T> extends Client
     * @return T
     */
    public <T extends Client> T init();

    /**
     * Submits a Gremlin script to the server and returns a {@link ResultSet} once the write of the request is
     * complete.
     *
     * @param gremlin the gremlin script to execute
     * @return ResultSet
     */
    public ResultSet submit(final String gremlin);

    /**
     * Submits a Gremlin script and bound parameters to the server and returns a {@link ResultSet} once the write of
     * the request is complete.  If a script is to be executed repeatedly with slightly different arguments, prefer
     * this method to concatenating a Gremlin script from dynamically produced strings and sending it to
     * {@link #submit(String)}.  Parameterized scripts will perform better.
     *
     * @param gremlin    the gremlin script to execute
     * @param parameters a map of parameters that will be bound to the script on execution
     * @return ResultSet
     */
    public ResultSet submit(final String gremlin, final Map<String, Object> parameters);

    /**
     * Submits a Gremlin script to the server and returns a {@link ResultSet} once the write of the request is
     * complete.
     *
     * @param gremlin the gremlin script to execute
     * @param options for the request
     * @return ResultSet
     */
    public ResultSet submit(final String gremlin, final RequestOptions options);

    /**
     * The asynchronous version of {@link #submit(String)} where the returned future will complete when the
     * write of the request completes.
     *
     * @param gremlin the gremlin script to execute
     * @return CompletableFuture
     */
    public CompletableFuture<ResultSet> submitAsync(final String gremlin);

    /**
     * The asynchronous version of {@link #submit(String, Map)}} where the returned future will complete when the
     * write of the request completes.
     *
     * @param gremlin    the gremlin script to execute
     * @param parameters a map of parameters that will be bound to the script on execution
     * @return CompletableFuture
     */
    public CompletableFuture<ResultSet> submitAsync(final String gremlin, final Map<String, Object> parameters);

    /**
     * The asynchronous version of {@link #submit(String, RequestOptions)}} where the returned future will complete when the
     * write of the request completes.
     *
     * @param gremlin the gremlin script to execute
     * @param options the options to supply for this request
     * @return CompletableFuture
     */
    public CompletableFuture<ResultSet> submitAsync(final String gremlin, final RequestOptions options);

    /**
     * A low-level method that allows the submission of a manually constructed {@link RequestMessage}.
     *
     * @param msg RequestMessage
     * @return CompletableFuture
     */
    public CompletableFuture<ResultSet> submitAsync(final RequestMessage msg);

    public boolean isClosing();

    /**
     * Closes the client by making a synchronous call to {@link #closeAsync()}.
     */
    public void close();

    /**
     * Gets the {@link Cluster} that spawned this {@code Client}.
     *
     * @return Cluster
     */
    public Cluster getCluster();

    public Map<String, String> makeDefaultAliasMap(final String graphOrTraversalSource);
}
