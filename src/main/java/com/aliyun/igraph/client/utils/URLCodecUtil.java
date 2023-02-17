package com.aliyun.igraph.client.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import lombok.NonNull;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

/**
 * A collection of utilities for encoding URLs.
 * 
 * @author alibaba
 * @since 4.0
 */
public class URLCodecUtil {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final Escaper escaper = UrlEscapers.urlFormParameterEscaper();

    public static String encode(@NonNull String srcString) {
        return escaper.escape(srcString);
    }

    public static String decode(@NonNull String srcString) {
        try {
            return URLDecoder.decode(srcString, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public static String encode(@NonNull String srcString, @NonNull String charset) {
        try {
            return URLEncoder.encode(srcString, charset);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public static String decode(@NonNull String srcString, @NonNull String charset) {
        try {
            return URLDecoder.decode(srcString, charset);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}