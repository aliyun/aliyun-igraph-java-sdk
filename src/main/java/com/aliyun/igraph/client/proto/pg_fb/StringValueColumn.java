// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class StringValueColumn extends Table {
  public static StringValueColumn getRootAsStringValueColumn(ByteBuffer _bb) { return getRootAsStringValueColumn(_bb, new StringValueColumn()); }
  public static StringValueColumn getRootAsStringValueColumn(ByteBuffer _bb, StringValueColumn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public StringValueColumn __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String value(int j) { int o = __offset(4); return o != 0 ? __string(__vector(o) + j * 4) : null; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public ByteBuffer valueAsByteBuffer(int j) {
    final int SIZEOF_INT = 4;
    int o = __offset(4);
    int offset = __vector(o) + j * 4;
    offset += bb.getInt(offset);
    ByteBuffer src = bb.duplicate().order(ByteOrder.LITTLE_ENDIAN);
    int length = src.getInt(offset);
    src.position(offset + SIZEOF_INT);
    src.limit(offset + SIZEOF_INT + length);
    return src;
  }
  public static int createStringValueColumn(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    StringValueColumn.addValue(builder, valueOffset);
    return StringValueColumn.endStringValueColumn(builder);
  }

  public static void startStringValueColumn(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endStringValueColumn(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

