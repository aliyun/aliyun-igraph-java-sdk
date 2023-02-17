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
public final class Result extends Table {
  public static Result getRootAsResult(ByteBuffer _bb) { return getRootAsResult(_bb, new Result()); }
  public static Result getRootAsResult(ByteBuffer _bb, Result obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public Result __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; return this; }

  public MultiErrorInfo errorInfos() { return errorInfos(new MultiErrorInfo()); }
  public MultiErrorInfo errorInfos(MultiErrorInfo obj) { int o = __offset(4); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }
  public String traceInfo() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer traceInfoAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public ElementMeta elementMeta(int j) { return elementMeta(new ElementMeta(), j); }
  public ElementMeta elementMeta(ElementMeta obj, int j) { int o = __offset(8); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int elementMetaLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }
  public DocData docData(int j) { return docData(new DocData(), j); }
  public DocData docData(DocData obj, int j) { int o = __offset(10); return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null; }
  public int docDataLength() { int o = __offset(10); return o != 0 ? __vector_len(o) : 0; }
  public GremlinObject objectResult() { return objectResult(new GremlinObject()); }
  public GremlinObject objectResult(GremlinObject obj) { int o = __offset(12); return o != 0 ? obj.__init(__indirect(o + bb_pos), bb) : null; }

  public static int createResult(FlatBufferBuilder builder,
      int error_infosOffset,
      int trace_infoOffset,
      int element_metaOffset,
      int doc_dataOffset,
      int object_resultOffset) {
    builder.startObject(5);
    Result.addObjectResult(builder, object_resultOffset);
    Result.addDocData(builder, doc_dataOffset);
    Result.addElementMeta(builder, element_metaOffset);
    Result.addTraceInfo(builder, trace_infoOffset);
    Result.addErrorInfos(builder, error_infosOffset);
    return Result.endResult(builder);
  }

  public static void startResult(FlatBufferBuilder builder) { builder.startObject(5); }
  public static void addErrorInfos(FlatBufferBuilder builder, int errorInfosOffset) { builder.addOffset(0, errorInfosOffset, 0); }
  public static void addTraceInfo(FlatBufferBuilder builder, int traceInfoOffset) { builder.addOffset(1, traceInfoOffset, 0); }
  public static void addElementMeta(FlatBufferBuilder builder, int elementMetaOffset) { builder.addOffset(2, elementMetaOffset, 0); }
  public static int createElementMetaVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startElementMetaVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addDocData(FlatBufferBuilder builder, int docDataOffset) { builder.addOffset(3, docDataOffset, 0); }
  public static int createDocDataVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startDocDataVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addObjectResult(FlatBufferBuilder builder, int objectResultOffset) { builder.addOffset(4, objectResultOffset, 0); }
  public static int endResult(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

