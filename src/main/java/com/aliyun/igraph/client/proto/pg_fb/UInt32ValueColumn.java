// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class UInt32ValueColumn extends Table {
  public static UInt32ValueColumn getRootAsUInt32ValueColumn(ByteBuffer _bb) { return getRootAsUInt32ValueColumn(_bb, new UInt32ValueColumn()); }
  public static UInt32ValueColumn getRootAsUInt32ValueColumn(ByteBuffer _bb, UInt32ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public UInt32ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long value(int j) { int o = __offset(4); return o != 0 ? (long)bb.getInt(__vector(o) + j * 4) & 0xFFFFFFFFL : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 4); }

  public static int createUInt32ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    UInt32ValueColumn.addValue(builder, valueOffset);
    return UInt32ValueColumn.endUInt32ValueColumn(builder);
  }

  public static void startUInt32ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endUInt32ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

