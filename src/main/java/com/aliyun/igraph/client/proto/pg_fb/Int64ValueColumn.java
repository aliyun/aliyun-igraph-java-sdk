// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class Int64ValueColumn extends Table {
  public static Int64ValueColumn getRootAsInt64ValueColumn(ByteBuffer _bb) { return getRootAsInt64ValueColumn(_bb, new Int64ValueColumn()); }
  public static Int64ValueColumn getRootAsInt64ValueColumn(ByteBuffer _bb, Int64ValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Int64ValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public long value(int j) { int o = __offset(4); return o != 0 ? bb.getLong(__vector(o) + j * 8) : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 8); }

  public static int createInt64ValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    Int64ValueColumn.addValue(builder, valueOffset);
    return Int64ValueColumn.endInt64ValueColumn(builder);
  }

  public static void startInt64ValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, long[] data) { builder.startVector(8, data.length, 8); for (int i = data.length - 1; i >= 0; i--) builder.addLong(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(8, numElems, 8); }
  public static int endInt64ValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

