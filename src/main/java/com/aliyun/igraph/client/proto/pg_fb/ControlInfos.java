// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class ControlInfos extends Table {
  public static ControlInfos getRootAsControlInfos(ByteBuffer _bb) { return getRootAsControlInfos(_bb, new ControlInfos()); }
  public static ControlInfos getRootAsControlInfos(ByteBuffer _bb, ControlInfos obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public ControlInfos __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int multicallWeight() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean containHotKey() { int o = __offset(6); return o != 0 ? 0!=bb.get(o + bb_pos) : false; }

  public static int createControlInfos(FlatBufferBuilder builder,
      int multicall_weight,
      boolean contain_hot_key) {
    builder.startObject(2);
    ControlInfos.addMulticallWeight(builder, multicall_weight);
    ControlInfos.addContainHotKey(builder, contain_hot_key);
    return ControlInfos.endControlInfos(builder);
  }

  public static void startControlInfos(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addMulticallWeight(FlatBufferBuilder builder, int multicallWeight) { builder.addInt(0, multicallWeight, 0); }
  public static void addContainHotKey(FlatBufferBuilder builder, boolean containHotKey) { builder.addBoolean(1, containHotKey, false); }
  public static int endControlInfos(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

