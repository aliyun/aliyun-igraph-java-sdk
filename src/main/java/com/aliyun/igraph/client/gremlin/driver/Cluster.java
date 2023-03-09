package com.aliyun.igraph.client.gremlin.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.NotSupportedException;

import com.aliyun.igraph.client.config.ClientConfig;
import com.aliyun.igraph.client.exception.IGraphClientException;
import com.aliyun.igraph.client.net.Requester;
import com.aliyun.igraph.client.net.RequesterConfig;
import com.aliyun.igraph.client.utils.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.driver.Connection;
import org.apache.tinkerpop.gremlin.driver.Settings;
import org.apache.tinkerpop.gremlin.driver.Settings.ConnectionPoolSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alibaba
 */
@Slf4j
public class Cluster implements org.apache.tinkerpop.gremlin.driver.Cluster {
    private static final Logger logger = LoggerFactory.getLogger(org.apache.tinkerpop.gremlin.driver.Cluster.class);
    protected Manager manager;
    private static final String DEFAULT_IP = "iGraph_client_invalid_ip";
    private RequesterConfig requesterConfig = new RequesterConfig();
    private ClientConfig clientConfig = new ClientConfig();
    private Requester requester;

    public Cluster(Builder builder) {
        this.manager = new Manager(builder);
    }

    @Override
    public void init() {

    }

    @Override
    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect() {
        return connect(manager.endpoint);
    }

    @Override
    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect(final String endpoint) {
        try {
            requesterConfig.setMaxConnPerRoute(manager.connectionPoolSettings.maxConnPerRoute);
            requesterConfig.setMaxConnTotal(manager.connectionPoolSettings.maxConnTotal);
            requesterConfig.getHttpConnectionConfig().setConnectionRequestTimeout(
                manager.connectionPoolSettings.connectionRequestTimeout);
            requesterConfig.getHttpConnectionConfig().setSocketTimeout(
                manager.connectionPoolSettings.socketTimeout);
            requesterConfig.getHttpConnectionConfig().setConnectTimeout(
                manager.connectionPoolSettings.connectTimeout);
            requesterConfig.getHttpConnectionConfig().setConnectionIdleTimeout(
                manager.connectionPoolSettings.connectionIdleTimeout);
            if (requester == null) {
                requester = new Requester(requesterConfig);
            }
            clientConfig.setRetryTimes(manager.retryTimes);
            clientConfig.setSrc(manager.src);
            clientConfig.setSearchDomain(endpoint);
            clientConfig.setUpdateDomain(endpoint);
            clientConfig.setLocalAddress(NetUtils.getIntranetIp());
            clientConfig.setUserName(manager.userName);
            clientConfig.setUserPasswd(manager.userPasswd);
            Client client = new Client(clientConfig, requester, this, null);
            manager.trackClient(client);
            return (T) client;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IGraphClientException("Failed to build IGraphClient", e);
        }
    }

    @Override
    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect(String sessionId, boolean manageTransactions) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public <T extends org.apache.tinkerpop.gremlin.driver.Client> T connect(final Client.Settings settings) {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public String toString() {
        return manager.toString();
    }

    public static Builder build() {
        return new Builder();
    }

    public static Builder build(final String address) {
        return new Builder(address);
    }

    public static Builder build(final File configurationFile) throws FileNotFoundException {
        throw new NotSupportedException("method is not supported");
    }
    private static Builder getBuilderFromSettings(final Settings settings) {
        if (settings.endpoint == null) {
            throw new IllegalStateException("empty endpoint is not allowed");
        }

        return new Builder(settings.endpoint)
            .src(settings.src)
            .userName(settings.username)
            .userPasswd(settings.userPasswd)
            .maxConnPerRoute(settings.connectionPool.maxConnPerRoute)
            .maxConnTotal(settings.connectionPool.maxConnTotal)
            .connectionRequestTimeout(settings.connectionPool.connectionRequestTimeout)
            .connectionIdleTimeout(settings.connectionPool.connectionIdleTimeout)
            .socketTimeout(settings.connectionPool.socketTimeout)
            .connectTimeout(settings.connectionPool.connectTimeout)
            .retryTimes(settings.retryTimes);
    }

    @Override
    public org.apache.tinkerpop.gremlin.driver.Cluster open() {
        throw new NotSupportedException("method is not supported, have to set username、password and endpoint");
    }

    @Override
    public org.apache.tinkerpop.gremlin.driver.Cluster open(final String configurationFile) throws Exception {
        throw new NotSupportedException("method is not supported");
    }
    @Override
    public void close(){
        manager.close();
        logger.info("Closed Cluster for hosts [{}]", this);
    }

    @Override
    public CompletableFuture<Void> closeAsync() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public boolean isClosing() {
        throw new NotSupportedException("method is not supported");
    }

    @Override
    public boolean isClosed() {
        throw new NotSupportedException("method is not supported");
    }

    public final static class Builder {
        private String src = null;
        private String endpoint = null;
        private String userName = null;
        private String userPasswd = null;
        private int maxConnPerRoute = Connection.DEFAULT_CONNECTION_PER_ROUTE;
        private int maxConnTotal = Connection.DEFAULT_MAX_CONNECTION_TOTAL;
        private int connectionRequestTimeout = Connection.DEFAULT_TIMEOUT_MS;
        private int connectionIdleTimeout = Connection.DEFAULT_IDLE_TIMEOUT_MS;
        private int socketTimeout = Connection.DEFAULT_TIMEOUT_MS;
        private int connectTimeout = Connection.DEFAULT_TIMEOUT_MS;
        private int retryTimes = Connection.DEFAULT_RETRY_TIMES;

        private Builder() {
            // empty to prevent direct instantiation
        }

        private Builder(final String address) {
            addContactPoint(address);
        }

        public Builder src (final String src) {
            this.src = src;
            return this;
        }
        public Builder userName(final String userName) {
            this.userName = userName;
            return this;
        }

        public Builder userPasswd(final String userPasswd) {
            this.userPasswd = userPasswd;
            return this;
        }

        public Builder maxConnPerRoute (final int maxConnPerRoute) {
            this.maxConnPerRoute = maxConnPerRoute;
            return this;
        }

        public Builder maxConnTotal (final int maxConnTotal) {
            this.maxConnTotal = maxConnTotal;
            return this;
        }

        public Builder connectionRequestTimeout (final int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
            return this;
        }

        public Builder connectionIdleTimeout (final int connectionIdleTimeout) {
            this.connectionIdleTimeout = connectionIdleTimeout;
            return this;
        }

        public Builder socketTimeout (final int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder connectTimeout (final int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder retryTimes (final int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        /**
         * Adds the address of a Gremlin Server to the list of servers a {@link org.apache.tinkerpop.gremlin.driver.Client} will try to contact to send
         * requests to.  The address should be parseable by {@link InetAddress#getByName(String)}.  That's the only
         * validation performed at this point.  No connection to the host is attempted.
         *
         * @param address instance_id
         * @return Builder
         */
        public Builder addContactPoint(final String address) {
            this.endpoint = address;
            return this;
        }

        public Cluster create() {
            if (endpoint == null) {
                throw new IGraphClientException("empty endpoint is not allowed");
            }

            return new Cluster(this);
        }
    }

    static class Manager {
        private final String endpoint;
        private final String userName;
        private final String userPasswd;
        private final String src;
        private final int retryTimes;
        private Settings.ConnectionPoolSettings connectionPoolSettings;

        private final List<WeakReference<Client>> openedClients = new ArrayList<>();

        private Manager(final Builder builder) {
            validateBuilder(builder);

            this.endpoint = builder.endpoint;
            this.userName = builder.userName;
            this.userPasswd = builder.userPasswd;
            this.src = builder.src == null? (userName + '_' + endpoint) : builder.src;
            this.retryTimes = builder.retryTimes;
            connectionPoolSettings = new ConnectionPoolSettings();
            connectionPoolSettings.maxConnPerRoute = builder.maxConnPerRoute;
            connectionPoolSettings.maxConnTotal = builder.maxConnTotal;
            connectionPoolSettings.connectionRequestTimeout = builder.connectionRequestTimeout;
            connectionPoolSettings.connectionIdleTimeout = builder.connectionIdleTimeout;
            connectionPoolSettings.socketTimeout = builder.socketTimeout;
            connectionPoolSettings.connectTimeout = builder.connectTimeout;
        }

        private void validateBuilder(final Builder builder) {
            if (builder.userName == null) {
                throw new IllegalArgumentException("userName must be set");
            }

            if (builder.userPasswd == null) {
                throw new IllegalArgumentException("userPasswd must be set");
            }

            if (builder.maxConnPerRoute < 1) {
                throw new IllegalArgumentException("maxConnPerRoute must be be greater than zero");
            }

            if (builder.maxConnTotal < 1) {
                throw new IllegalArgumentException("maxConnTotal must be be greater than zero");
            }

            if (builder.connectionRequestTimeout < 1) {
                throw new IllegalArgumentException("connectionRequestTimeout must be be greater than zero");
            }

            if (builder.connectionIdleTimeout < 1) {
                throw new IllegalArgumentException("connectionIdleTimeout must be be greater than zero");
            }

            if (builder.socketTimeout < 1) {
                throw new IllegalArgumentException("socketTimeout must be be greater than zero");
            }

            if (builder.connectTimeout < 1) {
                throw new IllegalArgumentException("connectTimeout must be be greater than zero");
            }

            if (builder.retryTimes < 0) {
                throw new IllegalArgumentException("retryTimes must be be greater than or equal to zero");
            }
        }

        void trackClient(Client client) {
            openedClients.add(new WeakReference<>(client));
        }

        void close() {
            for (WeakReference<Client> openedClient : openedClients) {
                final Client client = openedClient.get();
                if (client != null) {
                    client.close();
                }
            }
        }

        @Override
        public String toString() {
            return endpoint;
        }
    }

}
