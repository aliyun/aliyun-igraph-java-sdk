package com.aliyun.igraph.client.config;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author alibaba
 */
@Data
@NoArgsConstructor
public class ClientConfig {
    @NonNull
    private String searchDomain;
    @NonNull
    private String updateDomain;
    @NonNull
    private String src;
    @NonNull
    private transient String userName;
    @NonNull
    private transient String userPasswd;
    @NonNull
    private Integer retryTimes;
    /**
     * FIXME: obtain from pom.xml file
     */
    private String clientVersion = com.aliyun.igraph.client.config.Version.VERSION;
    @NonNull
    private String localAddress;

    public ClientConfig(ClientConfig clientConfig) {
        this.searchDomain = clientConfig.searchDomain;
        this.updateDomain = clientConfig.updateDomain;
        this.src = clientConfig.src;
        this.userName = clientConfig.userName;
        this.userPasswd = clientConfig.userPasswd;
        this.retryTimes = clientConfig.retryTimes;
        this.clientVersion = clientConfig.clientVersion;
        this.localAddress = clientConfig.localAddress;
    }

    public String getUserAuth() {
        if (null == userName || userName.isEmpty() || null == userPasswd || userPasswd.isEmpty()) {
            return null;
        }
        return userName + ":" + userPasswd;
    }

    @Override
    public String toString() {
        StringBuilder ss = new StringBuilder(128);
        ss.append("IGraphClientConfig: ");
        ss.append("searchDomain [").append(searchDomain).append("];");
        ss.append("updateDomain [").append(updateDomain).append("];");
        ss.append("src [").append(src).append("];");
        ss.append("localAddress [").append(localAddress).append("];");
        ss.append("clientVersion [").append(clientVersion).append("];");
        return ss.toString();
    }
}
