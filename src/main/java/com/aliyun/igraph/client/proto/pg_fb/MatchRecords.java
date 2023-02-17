// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class MatchRecords extends Table {
  public static MatchRecords getRootAsMatchRecords(ByteBuffer _bb) { return getRootAsMatchRecords(_bb, new MatchRecords()); }
  public static MatchRecords getRootAsMatchRecords(ByteBuffer _bb, MatchRecords obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public MatchRecords __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public String fieldName(int j) { int o = __offset(4); return o != 0 ? __string(__vector(o) + j * 4) : null; }
  public int fieldNameLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public FieldValueColumnTable recordColumns(int j) { return recordColumns(new FieldValueColumnTable(), j); }
  public FieldValueColumnTable recordColumns(FieldValueColumnTable obj, int j) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int recordColumnsLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }

  public static int createMatchRecords(FlatBufferBuilder builder,
      int field_nameOffset,
      int record_columnsOffset) {
    builder.startObject(2);
    MatchRecords.addRecordColumns(builder, record_columnsOffset);
    MatchRecords.addFieldName(builder, field_nameOffset);
    return MatchRecords.endMatchRecords(builder);
  }

  public static void startMatchRecords(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addFieldName(FlatBufferBuilder builder, int fieldNameOffset) { builder.addOffset(0, fieldNameOffset, 0); }
  public static int createFieldNameVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startFieldNameVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addRecordColumns(FlatBufferBuilder builder, int recordColumnsOffset) { builder.addOffset(1, recordColumnsOffset, 0); }
  public static int createRecordColumnsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startRecordColumnsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endMatchRecords(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

