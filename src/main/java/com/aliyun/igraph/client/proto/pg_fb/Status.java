// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

/**
 * @author alibaba
 */
public final class Status {
  private Status() { }
  public static final byte FINISHED = 0;
  public static final byte BADQUERY = 1;
  public static final byte COMMIT_FAILED = 2;
  public static final byte TIMEOUT = 3;
  public static final byte UNKNOWN = 4;

  private static final String[] names = { "FINISHED", "BADQUERY", "COMMIT_FAILED", "TIMEOUT", "UNKNOWN", };

  public static String name(int e) { return names[e]; }
};

