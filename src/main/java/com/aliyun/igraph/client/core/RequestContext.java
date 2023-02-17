package com.aliyun.igraph.client.core;

import lombok.Data;

/**
 * @author alibaba
 */
@Data
public class RequestContext {
    private static final long NS_TO_MS = 1000000L;
    private static final int MAX_DISPLAY_REQUEST_LENGTH = 1024;
    private String serverAddress;
    private String requestContent;
    private int responseContentLength;
    private long queryEncodeStartNs;
    private long queryEncodeElapsedNs;
    private boolean isQueryEncodeTimerRunning;
    private long serverRequestStartNs;
    private long serverRequestElapsedNs;
    private boolean isServerRequestTimerRunning;
    private long responseDecodeStartNs;
    private long responseDecodeElapsedNs;
    private boolean isResponseDecodeTimerRunning;
    private int hasRetryTimes = 0;
    private boolean validResult = false;
    private String requestId;


    public RequestContext() {
        clear();
    }
    /**
     * @return query encode latency in ms
     */
    public long getQueryEncodeLatency() {
        return queryEncodeElapsedNs / NS_TO_MS;
    }

    /**
     * @return server sendSearchRequest latency in ms
     */
    public long getServerRequestLatency() {
        return serverRequestElapsedNs / NS_TO_MS;
    }

    /**
     * @return response decode latency in ms
     */
    public long getResponseDecodeLatency() {
        return responseDecodeElapsedNs / NS_TO_MS;
    }

    @Override
    public String toString() {
        String nickRequestContent;
        if (requestContent == null) {
            nickRequestContent = null;
        } else if (requestContent.length() < MAX_DISPLAY_REQUEST_LENGTH) {
            nickRequestContent = requestContent;
        } else {
            StringBuilder ss = new StringBuilder(MAX_DISPLAY_REQUEST_LENGTH + 100);
            ss.append(requestContent.substring(0, MAX_DISPLAY_REQUEST_LENGTH / 2))
                    .append(" ... ")
                    .append("requestLength=[")
                    .append(requestContent.length())
                    .append("] ... ")
                    .append(requestContent.substring(requestContent.length() - MAX_DISPLAY_REQUEST_LENGTH / 2,
                            requestContent.length()));
            nickRequestContent = ss.toString();
        }
        return "serverAddress=[" + serverAddress + "], requestContent=[" + nickRequestContent
                + "], responseContentLength=[" + responseContentLength + "], queryEncodeLatency=["
                + getQueryEncodeLatency() + "], serverRequestLatency=[" + getServerRequestLatency()
                + "], responseDecodeLatency=[" + getResponseDecodeLatency() + "]"
                + ", hasRetryTime=[" + hasRetryTimes + "], requestId=[" + requestId + "]";
    }

    public void clear() {
        requestContent = null;
        responseContentLength = 0;
        serverAddress = null;

        queryEncodeStartNs = 0L;
        queryEncodeElapsedNs = 0L;
        isQueryEncodeTimerRunning = false;

        serverRequestStartNs = 0L;
        serverRequestElapsedNs = 0L;
        isServerRequestTimerRunning = false;

        responseDecodeStartNs = 0L;
        responseDecodeElapsedNs = 0L;
        isResponseDecodeTimerRunning = false;
        
        hasRetryTimes = 0;
    }

    public void beginQueryEncode() {
        if (!isQueryEncodeTimerRunning) {
            queryEncodeStartNs = System.nanoTime();
            isQueryEncodeTimerRunning = true;
        }
    }

    public void endQueryEncode() {
        if (isQueryEncodeTimerRunning) {
            queryEncodeElapsedNs += System.nanoTime() - queryEncodeStartNs;
            isQueryEncodeTimerRunning = false;
        }
    }

    public void beginServerRequest() {
        if (!isServerRequestTimerRunning) {
            serverRequestStartNs = System.nanoTime();
            isServerRequestTimerRunning = true;
        }
    }

    public void endServerRequest() {
        if (isServerRequestTimerRunning) {
            serverRequestElapsedNs += System.nanoTime() - serverRequestStartNs;
            isServerRequestTimerRunning = false;
        }
    }

    public void beginResponseDecode() {
        if (!isResponseDecodeTimerRunning) {
            responseDecodeStartNs = System.nanoTime();
            isResponseDecodeTimerRunning = true;
        }
    }

    public void endResponseDecode() {
        if (isResponseDecodeTimerRunning) {
            responseDecodeElapsedNs += System.nanoTime() - responseDecodeStartNs;
            isResponseDecodeTimerRunning = false;
        }
    }
}
