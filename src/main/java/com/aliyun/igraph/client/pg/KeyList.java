package com.aliyun.igraph.client.pg;

import com.aliyun.igraph.client.exception.IGraphClientException;
import com.aliyun.igraph.client.utils.URLCodecUtil;
import lombok.NonNull;

import java.util.Arrays;

/**
 * @author alibaba
 */
public class KeyList {
    private StringBuilder ss = new StringBuilder();
    private String pkey;
    private String[] skeys;

    public KeyList(@NonNull String pkey) {
        ss.append(URLCodecUtil.encode(pkey));
        this.pkey = pkey;
    }

    public KeyList(@NonNull String pkey,
                   @NonNull String... skeys) {
        if (skeys.length == 0) {
            throw new IGraphClientException("empty skeys in not allowed!");
        }
        ss.append(URLCodecUtil.encode(pkey)).append(":");
        boolean first = true;
        for (String skey : skeys) {
            if (!first) {
                ss.append("|");
            }
            first = false;
            ss.append(URLCodecUtil.encode(skey));
        }
        this.pkey = pkey;
        this.skeys = skeys;
    }

    @Override
    public String toString() {
        return ss.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyList)) return false;

        KeyList keyList = (KeyList) o;

        if (pkey != null ? !pkey.equals(keyList.pkey) : keyList.pkey != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(skeys, keyList.skeys);

    }

    @Override
    public int hashCode() {
        int result = pkey != null ? pkey.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(skeys);
        return result;
    }

    public String getPkey() {
        return pkey;
    }

    public String[] getSkeys() {
        return skeys;
    }
}
