package com.voidsearch.voidbase.apps.feedq.connector.fetcher;

/**
 * @author Aleksandar Bradic
 */
public interface FeedFetcher {

    /**
     * fetch content of given resource
     *
     * @param resource  resource to fetch
     * @return
     */

    public byte[] fetch(String resource) throws Exception;


    /**
     * fetch last size bytes from given resource
     *
     * @param resource resource to fetch
     * @param size buffer size to fetch
     * @return
     */

    public byte[] fetch(String resource, int size) throws Exception;

}
