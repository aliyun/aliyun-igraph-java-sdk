package com.aliyun.igraph.client.core;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aliyun.igraph.client.core.model.GremlinRetryableErrorType;
import com.aliyun.igraph.client.exception.IGraphQueryException;
import com.aliyun.igraph.client.exception.IGraphRetryableException;
import com.aliyun.igraph.client.exception.IGraphServerException;
import com.aliyun.igraph.client.gremlin.structure.IGraphMultiResultSet;
import com.aliyun.igraph.client.gremlin.structure.IGraphResultSetDecoder;
import com.aliyun.igraph.client.pg.QueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResult;
import com.aliyun.igraph.client.pg.SingleQueryResultFBByColumn;
import com.aliyun.igraph.client.proto.gremlin_fb.GremlinResult;
import com.aliyun.igraph.client.proto.gremlin_fb.MultiErrorInfo;
import com.aliyun.igraph.client.proto.gremlin_fb.Result;
import com.aliyun.igraph.client.proto.pg_fb.ControlInfos;
import com.aliyun.igraph.client.proto.pg_fb.MatchRecords;
import com.aliyun.igraph.client.proto.pg_fb.PGResult;
import com.aliyun.igraph.client.proto.pg_fb.Status;
import com.aliyun.igraph.client.core.model.ControlInfo;
import com.aliyun.igraph.client.core.model.Outfmt;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

/**
 * Created alibaba
 */
public class IGraphResultParser {
    private static final Logger log = LoggerFactory.getLogger(IGraphResultParser.class);

    public static QueryResult parse(RequestContext requestContext, byte[] bytes, String outfmt) throws IGraphServerException {
        if (outfmt.equals(Outfmt.FBBYCOLUMNNZ.toString())) {
            return parseFromFBByColumn(requestContext, bytes, false);
        } else if (outfmt.equals(Outfmt.FBBYCOLUMN.toString())) {
            return parseFromFBByColumn(requestContext, bytes, true);
        } else {
            return null;
        }
    }

    public static void checkStatusFBByColumn(byte status, RequestContext requestContext) {
        if (status != Status.FINISHED) {
            throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext,
                    Status.name(status)));
        }
    }

    public static QueryResult parseFromFBByColumn(RequestContext requestContext, byte[] bytes, boolean is_compress) throws IGraphServerException {
        QueryResult queryResult = new QueryResult();
        PGResult pgResult;
        try {
            requestContext.beginResponseDecode();
            if (is_compress) {
                byte[] uncompress_byte = Snappy.uncompress(bytes);
                pgResult = PGResult.getRootAsPGResult(ByteBuffer.wrap(uncompress_byte));
            } else {
                pgResult = PGResult.getRootAsPGResult(ByteBuffer.wrap(bytes));
            }
            requestContext.endResponseDecode();
            checkStatusFBByColumn(pgResult.status(), requestContext);
            requestContext.beginResponseDecode();
            List<SingleQueryResult> results = parseResultFBByColumn(pgResult, requestContext);
            requestContext.endResponseDecode();
            queryResult.setResults(results);
            ControlInfo controlInfo = parseControlInfoFBByColumn(pgResult);
            queryResult.setControlInfo(controlInfo);
        } catch (IGraphQueryException e) {
            requestContext.endResponseDecode();
            throw e;
        } catch (Exception e) {
            requestContext.endResponseDecode();
            throw new IGraphServerException("Parse from FB2 Exception:["
                    + e.getMessage() + "] and response body[" + (new String(bytes)) + "]");
        }
        return queryResult;
    }

    public static IGraphMultiResultSet parseGremlin(RequestContext requestContext, byte[] bytes, String outfmt) throws IGraphServerException {
        boolean isCompress = true;
        if (outfmt.equals(Outfmt.FBBYCOLUMNNZ.toString())) {
            isCompress = false;
        } else if (!outfmt.equals(Outfmt.FBBYCOLUMN.toString())) {
            throw new IGraphServerException("Nonsupport format [" + outfmt + "]for gremlin");
        }
        IGraphMultiResultSet gremlinMultiResult = new IGraphMultiResultSet();
        GremlinResult gremlinResult;
        try {
            requestContext.beginResponseDecode();
            if (isCompress) {
                byte[] uncompreseeByte = Snappy.uncompress(bytes);
                gremlinResult = GremlinResult.getRootAsGremlinResult(ByteBuffer.wrap(uncompreseeByte));
            } else {
                gremlinResult = GremlinResult.getRootAsGremlinResult(ByteBuffer.wrap(bytes));
            }
            requestContext.endResponseDecode();
            MultiErrorInfo multiErrorInfo = gremlinResult.errorInfos();
            if (multiErrorInfo != null) {
                int errorInfoLength = multiErrorInfo.errorInfoLength();
                if (errorInfoLength > 0) {
                    StringBuilder errorString = new StringBuilder();
                    errorString.append("error size [").append(errorInfoLength).append("], ");
                    errorString.append("error msg[");
                    for (int i = 0; i < errorInfoLength; ++i) {
                        String errorMessage = multiErrorInfo.errorInfo(i).errorMessage();
                        Long errorCode = multiErrorInfo.errorInfo(i).errorCode();
                        errorString.append(errorCode).append(":").append(errorMessage).append("; ");
                    }
                    errorString.append("], ").append("trace [").append(gremlinResult.traceInfo()).append(']');
                    throw new IGraphServerException(errorString.toString());
                }
            }
            requestContext.beginResponseDecode();
            gremlinMultiResult.setTraceInfo(gremlinResult.traceInfo());
            List<ResultSet> results = parseGremlinMultiResult(gremlinResult, requestContext);
            requestContext.endResponseDecode();
            gremlinMultiResult.setResults(results);

        } catch (IGraphQueryException e) {
            requestContext.endResponseDecode();
            throw new IGraphServerException("query error with IGraphQueryException:["
                    + e.getMessage() + "]", e);
        } catch (IGraphRetryableException e) {
            throw e;
        } catch (Exception e) {
            requestContext.endResponseDecode();
            throw new IGraphServerException("Parse from gremlin fb Exception:["
                    + e.getMessage() + "] and requestContext:[" + requestContext + "]", e);
        }
        return gremlinMultiResult;
    }

    private static List<SingleQueryResult> parseResultFBByColumn(PGResult pgResult,
                                                                 RequestContext requestContext) {
        int resultLength = pgResult.resultLength();
        List<SingleQueryResult> singleResultList = new ArrayList<SingleQueryResult>(resultLength);
        for (int i=0; i<resultLength; ++i) {
            com.aliyun.igraph.client.proto.pg_fb.Result result = pgResult.result(i);
            SingleQueryResultFBByColumn singleQueryResultFBByColumn = new SingleQueryResultFBByColumn();
            singleResultList.add(singleQueryResultFBByColumn);
            int errorLength = result.errorLength();
            if (errorLength > 0) {
                StringBuilder errorString = new StringBuilder();
                singleQueryResultFBByColumn.setHasError(true);
                for (int j = 0; j < errorLength; ++j) {
                    long errorCode = result.error(j).errorCode();
                    String errorMessage = result.error(j).errorMessage();
                    errorString.append(errorCode).append(":").append(errorMessage);
                }
                singleQueryResultFBByColumn.setErrorMsg(errorString.toString());
            }

            MatchRecords matchRecords = result.records();
            singleQueryResultFBByColumn.setResult(matchRecords);
            int recordLength = singleQueryResultFBByColumn.size();
            if (!singleQueryResultFBByColumn.hasError() || recordLength > 0) {
                requestContext.setValidResult(true);
            }
        }
        return singleResultList;
    }

    private static ControlInfo parseControlInfoFBByColumn(PGResult pgResult) {
        ControlInfo controlInfo = new ControlInfo();
        ControlInfos controlInfos = pgResult.controlInfos();
        if (controlInfos != null) {
            controlInfo.setMulticallWeight(controlInfos.multicallWeight());
            controlInfo.setContainHotKey(controlInfos.containHotKey());
        }
        return controlInfo;
    }

    private static List<ResultSet> parseGremlinMultiResult(
            GremlinResult gremlinResult, RequestContext requestContext) {
        int resultLength = gremlinResult.resultLength();
        List<ResultSet> singleResultList = new ArrayList<>(resultLength);
        boolean hasValidResult = false;
        boolean retryable = false;
        StringBuilder totalErrorMsg = new StringBuilder();
        for (int i = 0; i < resultLength; ++i) {
            Result fbResult = gremlinResult.result(i);
            IGraphResultSetDecoder gremlinSingleResultDecoder = new IGraphResultSetDecoder();
            gremlinSingleResultDecoder.decodeFBByColumn(fbResult);
            singleResultList.add((ResultSet) gremlinSingleResultDecoder);
            if (gremlinSingleResultDecoder.hasError()) {
                totalErrorMsg.append("query ").append(i).append(":").append(gremlinSingleResultDecoder.getErrorMsg()).append('\n');
            }
            hasValidResult |= !(gremlinSingleResultDecoder.hasError() && gremlinSingleResultDecoder.empty());
            retryable |= isRetryable(gremlinSingleResultDecoder);
        }
        requestContext.setValidResult(hasValidResult);
        if (!hasValidResult) {
            if (retryable) {
                throw new IGraphRetryableException(IGraphQueryException.buildErrorMessage(requestContext,
                    totalErrorMsg.toString()) + ", need retry.");
            } else {
                throw new IGraphQueryException(IGraphQueryException.buildErrorMessage(requestContext,
                    totalErrorMsg.toString()));
            }
        }
        return singleResultList;
    }

    /**
     * Only when the resultSet is empty and has an error, in addition, the error code is in
     * set {@link GremlinRetryableErrorType}, the request is retryable.
     *
     * @param gremlinSingleResultDecoder single resultSet of gremlin {@link IGraphResultSetDecoder}
     * @return boolean
     */
    private static boolean isRetryable(IGraphResultSetDecoder gremlinSingleResultDecoder) {
        List<Long> errorCodes = gremlinSingleResultDecoder.getErrorCodes();
        if (errorCodes.size() == 1 && gremlinSingleResultDecoder.size() == 0
            && Arrays.stream(GremlinRetryableErrorType.values())
            .anyMatch(gremlinRetryableErrorType -> gremlinRetryableErrorType.getValue().equals(errorCodes.get(0)))) {
            return true;
        }
        return false;
    }
}
