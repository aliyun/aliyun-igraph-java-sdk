package org.apache.tinkerpop.gremlin.driver;

import org.apache.tinkerpop.gremlin.driver.message.RequestMessage;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * @author alibaba
 */
public interface ResultSet extends Iterable<Result> {
    public RequestMessage getOriginalRequestMessage();

    public Host getHost();

    /**
     * Returns a future that will complete when {@link #allItemsAvailable()} is {@code true} and will contain the
     * attributes from the response.
     *
     * @return CompletableFuture
     */
    public CompletableFuture<Map<String, Object>> statusAttributes();

    /**
     * Determines if all items have been returned to the client.
     *
     * @return boolean
     */
    public boolean allItemsAvailable();

    /**
     * Returns a future that will complete when all items have been returned from the server.
     *
     * @return CompletableFuture
     */
    public CompletableFuture<Void> allItemsAvailableAsync();

    /**
     * Gets the number of items available on the client.
     *
     * @return int
     */
    public int getAvailableItemCount();

    /**
     * Get the next {@link Result} from the stream, blocking until one is available.
     *
     * @return Result
     */
    public Result one();

    /**
     * The returned {@link CompletableFuture} completes when the number of items specified are available.  The
     * number returned will be equal to or less than that number.  They will only be less if the stream is
     * completed and there are less than that number specified available.
     *
     * @param items int
     * @return CompletableFuture
     */
    public CompletableFuture<List<Result>> some(final int items);

    /**
     * The returned {@link CompletableFuture} completes when all reads are complete for this request and the
     * entire result has been accounted for on the client. While this method is named "all" it really refers to
     * retrieving all remaining items in the set.  For large result sets it is preferred to use
     * {@link Iterator} or {@link Stream} options, as the results will be held in memory at once.
     *
     * @return CompletableFuture
     */
    public CompletableFuture<List<Result>> all();

    /**
     * Stream items with a blocking iterator.
     *
     * @return Stream
     */
    public Stream<Result> stream();

    /**
     * Returns a blocking iterator of the items streaming from the server to the client. This {@link Iterator} will
     * consume results as they arrive and leaving the {@code ResultSet} empty when complete.
     * The returned {@link Iterator} does not support the {@link Iterator#remove} method.
     *
     * @return Iterator
     */
    @Override
    public Iterator<Result> iterator();

    public Object getJson();

}
