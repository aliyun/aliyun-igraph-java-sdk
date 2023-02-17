package org.apache.tinkerpop.gremlin.driver;

import java.util.*;

/**
 * @author alibaba
 */
public class RequestOptions {

    public static final RequestOptions EMPTY = RequestOptions.build().create();

    private final Map<String, String> aliases;
    private final Map<String, Object> parameters;
    private final Integer retryTimes;
    private final Integer timeout;
    private final String src;


    private RequestOptions(final Builder builder) {
        this.aliases = builder.aliases;
        this.parameters = builder.parameters;
        this.retryTimes = builder.retryTimes;
        this.timeout = builder.timeout;
        this.src = builder.src;
    }

    public Optional<Map<String, String>> getAliases() {
        return Optional.ofNullable(aliases);
    }

    public Optional<Map<String, Object>> getParameters() {
        return Optional.ofNullable(parameters);
    }

    public Optional<Integer> getTimeout() {
        return Optional.ofNullable(timeout);
    }

    public Optional<Integer> getRetryTimes() {
        return Optional.ofNullable(retryTimes);
    }

    public Optional<String> getSrc() {
        return Optional.ofNullable(src);
    }

    public static Builder build() {
        return new Builder();
    }

    public static final class Builder {
        private Map<String, String> aliases = null;
        private Map<String, Object> parameters = null;
        private Integer retryTimes = null;
        private Integer timeout = null;
        private String src = null;

        /**
         * The aliases to set on the request.（iGraph暂不支持）
         * @param aliasName key
         * @param actualName value
         * @return Builder
         */
        public Builder addAlias(final String aliasName, final String actualName) {
            if (null == aliases) {
                aliases = new HashMap<>();
            }
            aliases.put(aliasName, actualName);
            return this;
        }

        /**
         * The parameters to pass on the request.
         *
         * @param name key
         * @param value value
         * @return Builder
         */
        public Builder addParameter(final String name, final Object value) {
            if (null == parameters) {
                parameters = new LinkedHashMap<>();
            }
            parameters.put(name, value);
            return this;
        }

        /**
         * The per client request override in milliseconds for the server configured {@code evaluationTimeout}.
         * If this value is not set, then the configuration for the server is used.
         */
        public Builder timeout(final Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets the src(userAgent) identifier to be sent on the request.
         */
        public Builder src(final String src) {
            this.src = src;
            return this;
        }

        public Builder retryTimes(final Integer retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        public RequestOptions create() {
            return new RequestOptions(this);
        }
    }
}
