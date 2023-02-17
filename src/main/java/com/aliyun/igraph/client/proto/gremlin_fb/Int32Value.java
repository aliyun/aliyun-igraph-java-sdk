// automatically generated by the FlatBuffers compiler, do not modify

package com.aliyun.igraph.client.proto.gremlin_fb;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class Int32Value extends Table {
  public static Int32Value getRootAsInt32Value(ByteBuffer _bb) { return getRootAsInt32Value(_bb, new Int32Value()); }
  public static Int32Value getRootAsInt32Value(ByteBuffer _bb, Int32Value obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Int32Value __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int value() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createInt32Value(FlatBufferBuilder builder,
      int value) {
    builder.startObject(1);
    Int32Value.addValue(builder, value);
    return Int32Value.endInt32Value(builder);
  }

  public static void startInt32Value(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int value) { builder.addInt(0, value, 0); }
  public static int endInt32Value(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

