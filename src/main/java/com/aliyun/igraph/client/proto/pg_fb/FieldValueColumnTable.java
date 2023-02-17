// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class FieldValueColumnTable extends Table {
  public static FieldValueColumnTable getRootAsFieldValueColumnTable(ByteBuffer _bb) { return getRootAsFieldValueColumnTable(_bb, new FieldValueColumnTable()); }
  public static FieldValueColumnTable getRootAsFieldValueColumnTable(ByteBuffer _bb, FieldValueColumnTable obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public FieldValueColumnTable __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public byte fieldValueColumnType() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public Table fieldValueColumn(Table obj) { int o = __offset(6); return o != 0 ? __union(obj, o) : null; }

  public static int createFieldValueColumnTable(FlatBufferBuilder builder,
      byte field_value_column_type,
      int field_value_columnOffset) {
    builder.startObject(2);
    FieldValueColumnTable.addFieldValueColumn(builder, field_value_columnOffset);
    FieldValueColumnTable.addFieldValueColumnType(builder, field_value_column_type);
    return FieldValueColumnTable.endFieldValueColumnTable(builder);
  }

  public static void startFieldValueColumnTable(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addFieldValueColumnType(FlatBufferBuilder builder, byte fieldValueColumnType) { builder.addByte(0, fieldValueColumnType, 0); }
  public static void addFieldValueColumn(FlatBufferBuilder builder, int fieldValueColumnOffset) { builder.addOffset(1, fieldValueColumnOffset, 0); }
  public static int endFieldValueColumnTable(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
};

