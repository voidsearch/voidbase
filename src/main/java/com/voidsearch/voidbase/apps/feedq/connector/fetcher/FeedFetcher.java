package com.voidsearch.voidbase.apps.feedq.connector.fetcher;

import com.voidsearch.voidbase.apps.feedq.resource.FeedResource;

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

    public byte[] fetchRaw(String resource) throws Exception;


    /**
     * fetch last size bytes from given resource
     *
     * @param resource resource to fetch
     * @param size buffer size to fetch
     * @return
     */

    public byte[] fetchRaw(String resource, int size) throws Exception;


    /**
     * fetch the entire content of resource and factor a appropriate FeedResource
     * 
     * @param resource
     * @return
     * @throws Exception
     */
    public FeedResource fetch(String resource) throws Exception;

}
