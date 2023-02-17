// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class UInt8ValueColumn extends Table {
  public static UInt8ValueColumn getRootAsUInt8ValueColumn(ByteBuffer _bb) { return getRootAsUInt8ValueColumn(_bb, new UInt8ValueColumn()); }
  public static UInt8ValueColumn getRootAsUInt8ValueColumn(ByteBuffer _bb, UInt8ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public UInt8ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public int value(int j) { int o = __offset(4); return o != 0 ? bb.get(__vector(o) + j * 1) & 0xFF : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }

  public static int createUInt8ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    UInt8ValueColumn.addValue(builder, valueOffset);
    return UInt8ValueColumn.endUInt8ValueColumn(builder);
  }

  public static void startUInt8ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, byte[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addByte(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static int endUInt8ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

