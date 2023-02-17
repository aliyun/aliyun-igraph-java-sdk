// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MultiUInt8ValueColumn extends Table {
  public static MultiUInt8ValueColumn getRootAsMultiUInt8ValueColumn(ByteBuffer _bb) { return getRootAsMultiUInt8ValueColumn(_bb, new MultiUInt8ValueColumn()); }
  public static MultiUInt8ValueColumn getRootAsMultiUInt8ValueColumn(ByteBuffer _bb, MultiUInt8ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MultiUInt8ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public MultiUInt8Value value(int j) { return value(new MultiUInt8Value(), j); }
  public MultiUInt8Value value(MultiUInt8Value obj, int j) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createMultiUInt8ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    MultiUInt8ValueColumn.addValue(builder, valueOffset);
    return MultiUInt8ValueColumn.endMultiUInt8ValueColumn(builder);
  }

  public static void startMultiUInt8ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMultiUInt8ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

