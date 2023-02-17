package com.aliyun.igraph.client.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author alibaba
 */
@Slf4j
public class NetUtils {
    private static final String DEFAULT_IP = "127.0.0.1";

    public static String getIntranetIp() {
        String localAddress;
        try {
            localAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("getLocalHost failed", e);
            localAddress = DEFAULT_IP;
        }
        if (null == localAddress) {
            log.error("get empty localAddress");
            localAddress = DEFAULT_IP;
        }
        return localAddress;
    }
}
