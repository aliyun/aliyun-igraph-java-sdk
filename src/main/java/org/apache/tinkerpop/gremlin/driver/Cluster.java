
package org.apache.tinkerpop.gremlin.driver;

import java.util.concurrent.CompletableFuture;

import com.aliyun.igraph.client.gremlin.driver.Client;
/**
 * @author alibaba
 */
public interface Cluster {

    /**
     * Initialize the Cluster
     */
    public void init();

    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect();

    /**
     * Creates a {@link Client} instance to this {@code Cluster}, meaning requests will be routed to
     * a single server (randomly selected from the cluster), where the same bindings will be available on each request.
     * Requests are bound to the same thread on the server and thus transactions may extend beyond the bounds of a
     * single request.  The transactions are managed by the user and must be committed or rolled-back manually.
     * Note that calling this method does not imply that a connection is made to the server itself at this point.
     * Therefore, if there is only one server specified in the {@code Cluster} and that server is not available an
     * error will not be raised at this point.  Connections get initialized in the {@link org.apache.tinkerpop.gremlin.driver.Client} when a request is
     * submitted or can be directly initialized via {@link org.apache.tinkerpop.gremlin.driver.Client#init()}.
     *
     * @param sessionId user supplied id for the session which should be unique (a UUID is ideal).
     * @param <T> extends org.apache.tinkerpop.gremlin.driver.Client
     * @return Client
     */
    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect(final String sessionId);

    /**
     * Creates a {@link Client} instance to this {@code Cluster}, meaning requests will be routed to
     * a single server (randomly selected from the cluster), where the same bindings will be available on each request.
     * Requests are bound to the same thread on the server and thus transactions may extend beyond the bounds of a
     * single request.  If {@code manageTransactions} is set to {@code false} then transactions are managed by the
     * user and must be committed or rolled-back manually. When set to {@code true} the transaction is committed or
     * rolled-back at the end of each request.
     * Note that calling this method does not imply that a connection is made to the server itself at this point.
     * Therefore, if there is only one server specified in the {@code Cluster} and that server is not available an
     * error will not be raised at this point.  Connections get initialized in the {@link org.apache.tinkerpop.gremlin.driver.Client} when a request is
     * submitted or can be directly initialized via {@link org.apache.tinkerpop.gremlin.driver.Client#init()}.
     *
     * @param sessionId user supplied id for the session which should be unique (a UUID is ideal).
     * @param manageTransactions enables auto-transactions when set to true
     * @param <T> extends org.apache.tinkerpop.gremlin.driver.Client
     * @return Client
     */
    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect(final String sessionId, final boolean manageTransactions);

    /**
     * Creates a new {@link org.apache.tinkerpop.gremlin.driver.Client} based on the settings provided.
     *
     * @param settings Client.Settings
     * @param <T> extends org.apache.tinkerpop.gremlin.driver.Client
     * @return Client
     */
    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect(final Client.Settings settings);

    /**
     * Create a {@code Cluster} with all default settings which will connect to one contact point at {@code localhost}.
     *
     * @return Cluster
     */
    public Cluster open();

    /**
     * Create a {@code Cluster} using a YAML-based configuration file.
     * First try to read the file from the file system and then from resources.
     *
     * @param configurationFile YAML-based configuration file
     * @return Cluster
     * @throws Exception exception
     */
    public Cluster open(final String configurationFile) throws Exception;

    public void close();

    public CompletableFuture<Void> closeAsync();

    /**
     * Determines if the {@code Cluster} is in the process of closing given a call to {@link #close} or
     * {@link #closeAsync()}.
     *
     * @return boolean
     */
    public boolean isClosing();

    /**
     * Determines if the {@code Cluster} has completed its closing process after a call to {@link #close} or
     * {@link #closeAsync()}.
     *
     * @return boolean
     */
    public boolean isClosed();

}
