// automatically generated, do not modify

package com.aliyun.igraph.client.proto.pg_fb;

import java.nio.*;
import java.lang.*;

import com.google.flatbuffers.*;

/**
 * @author alibaba
 */
@SuppressWarnings("unused")
public final class PGResult extends Table {
  public static PGResult getRootAsPGResult(ByteBuffer _bb) { return getRootAsPGResult(_bb, new PGResult()); }
  public static PGResult getRootAsPGResult(ByteBuffer _bb, PGResult obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public PGResult __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public byte status() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public Result result(int j) { return result(new Result(), j); }
  public Result result(Result obj, int j) { int o = __offset(6); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int resultLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }
  public ControlInfos controlInfos() { return controlInfos(new ControlInfos()); }
  public ControlInfos controlInfos(ControlInfos obj) { int o = __offset(8); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }

  public static int createPGResult(FlatBufferBuilder builder,
      byte status,
      int resultOffset,
      int control_infosOffset) {
    builder.startObject(3);
    PGResult.addControlInfos(builder, control_infosOffset);
    PGResult.addResult(builder, resultOffset);
    PGResult.addStatus(builder, status);
    return PGResult.endPGResult(builder);
  }

  public static void startPGResult(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addStatus(FlatBufferBuilder builder, byte status) { builder.addByte(0, status, 0); }
  public static void addResult(FlatBufferBuilder builder, int resultOffset) { builder.addOffset(1, resultOffset, 0); }
  public static int createResultVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startResultVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addControlInfos(FlatBufferBuilder builder, int controlInfosOffset) { builder.addOffset(2, controlInfosOffset, 0); }
  public static int endPGResult(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishPGResultBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
};

