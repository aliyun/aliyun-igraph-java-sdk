package org.apache.tinkerpop.gremlin.driver;

/**
 * @author alibaba
 */
public class Settings {
    /**
     * User-defined scene name.
     */
    public String src = null;

    /**
     * The endpoint to submit on requests that require authentication.
     */
    public String endpoint = null;

    /**
     * The username to submit on requests that require authentication.
     */
    public String username = null;

    /**
     * The password to submit on requests that require authentication.
     */
    public String userPasswd = null;

    /**
     * 超时重试次数.
     */
    public int  retryTimes = Connection.DEFAULT_RETRY_TIMES;

    /**
     * Settings for connections and connection pool.
     */
    public ConnectionPoolSettings connectionPool = new ConnectionPoolSettings();

    public static class ConnectionPoolSettings {
        /**
         * 与服务端单机 http 的最大连接数
         */
        public int maxConnPerRoute = Connection.DEFAULT_CONNECTION_PER_ROUTE;

        /**
         * 与服务端 http 的最大总连接数
         */
        public int maxConnTotal = Connection.DEFAULT_MAX_CONNECTION_TOTAL;

        /**
         * 查询超时时间
         */
        public int connectionRequestTimeout = Connection.DEFAULT_TIMEOUT_MS;

        /**
         * netty 参数，与connectionRequestTimeout保持一致即可
         */
        public int socketTimeout = Connection.DEFAULT_TIMEOUT_MS;

        /**
         * netty 参数，与connectionRequestTimeout保持一致即可
         */
        public int connectTimeout = Connection.DEFAULT_TIMEOUT_MS;

    }

    public static class SerializerSettings {
    }
}
