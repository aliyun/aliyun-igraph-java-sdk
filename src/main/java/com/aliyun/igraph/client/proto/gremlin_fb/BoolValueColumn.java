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
public final class BoolValueColumn extends Table {
  public static BoolValueColumn getRootAsBoolValueColumn(ByteBuffer _bb) { return getRootAsBoolValueColumn(_bb, new BoolValueColumn()); }
  public static BoolValueColumn getRootAsBoolValueColumn(ByteBuffer _bb, BoolValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public BoolValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public boolean value(int j) { int o = __offset(4); return o != 0 ? 0!=bb.get(__vector(o) + j * 1) : false; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }

  public static int createBoolValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    BoolValueColumn.addValue(builder, valueOffset);
    return BoolValueColumn.endBoolValueColumn(builder);
  }

  public static void startBoolValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, boolean[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addBoolean(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static int endBoolValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

