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
public final class MultiInt32ValueColumn extends Table {
  public static MultiInt32ValueColumn getRootAsMultiInt32ValueColumn(ByteBuffer _bb) { return getRootAsMultiInt32ValueColumn(_bb, new MultiInt32ValueColumn()); }
  public static MultiInt32ValueColumn getRootAsMultiInt32ValueColumn(ByteBuffer _bb, MultiInt32ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiInt32ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public MultiInt32Value value(int j) { return value(new MultiInt32Value(), j); }
  public MultiInt32Value value(MultiInt32Value obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createMultiInt32ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiInt32ValueColumn.addValue(builder, valueOffset);
    return MultiInt32ValueColumn.endMultiInt32ValueColumn(builder);
  }

  public static void startMultiInt32ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMultiInt32ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

