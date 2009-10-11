/*
 * Copyright 2009 VoidSearch.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.voidsearch.voidbase.apps.queuetree.module;

import com.voidsearch.voidbase.module.*;
import com.voidsearch.voidbase.storage.queuetree.QueueTreeStorage;
import com.voidsearch.voidbase.storage.queuetree.QueueAlreadyExistsException;
import com.voidsearch.voidbase.storage.queuetree.InvalidQueueException;
import com.voidsearch.voidbase.supervision.StorageSupervisor;
import com.voidsearch.voidbase.supervision.SupervisionException;
import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeProtocol;
import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeFeed;
import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeResponse;
import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeFeedUtils;
import com.voidsearch.voidbase.protocol.InvalidRequestException;
import com.voidsearch.voidbase.protocol.VoidBaseHttpRequest;
import com.voidsearch.voidbase.protocol.VoidBaseOperationType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.LinkedList;

public class  QueueTreeModule implements VoidBaseModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    StorageSupervisor supervisor = StorageSupervisor.getInstance();

    // init

    public void initialize(String name) throws VoidBaseModuleException {

        QueueTreeStorage qTree = QueueTreeStorage.factory();
        supervisor.register(qTree);

        try {
            qTree.createQueue("common", 64);
        } catch (QueueAlreadyExistsException e) {
            e.printStackTrace();
        } catch (SupervisionException e) {
            e.printStackTrace();
        }

        logger.info("Initialized QueueTreeModule");

    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {

        return new VoidBaseModuleResponse(buildResponse(request), VoidBaseResponseStatus.OK, VoidBaseResponseType.XML) ;

    }

     // the actual protocol was moved here
     // to enable cals from within the system 
     public String buildResponse(VoidBaseModuleRequest request) throws VoidBaseModuleException {

        QueueTreeResponse response = new QueueTreeResponse();

        StringBuilder responseContent = new StringBuilder();

        String uri = request.getURI();
        responseContent.append(QueueTreeFeed.XML_HEADER);
        responseContent.append(QueueTreeFeed.FEED_START);

        try {
            VoidBaseHttpRequest req = new VoidBaseHttpRequest(uri);
            response.setRequest(req);

            responseContent.append(QueueTreeFeed.HEADER_START);
            responseContent.append(QueueTreeFeedUtils.getRequestHeader(req));

            String method = req.getParam(QueueTreeProtocol.METHOD);
            VoidBaseOperationType operation = VoidBaseOperationType.deserialize(method);

            QueueTreeStorage qTree = QueueTreeStorage.factory();

            // put key to given queue
            if (operation == VoidBaseOperationType.PUT) {

                if (req.containsParams(QueueTreeProtocol.getRequiredParams(VoidBaseOperationType.PUT))) {

                    String queue = req.getParam(QueueTreeProtocol.QUEUE);
                    String value = req.getParam(QueueTreeProtocol.VALUE);
                    qTree.putFIFO(queue, value);

                    response.setResponse(QueueTreeProtocol.ENQUEUED);
                } else {
                    throw new InvalidRequestException();
                }

            } // get queue head
            else if (operation == VoidBaseOperationType.GET) {

                if (req.containsParams(QueueTreeProtocol.getRequiredParams(VoidBaseOperationType.GET))) {

                    String queue = req.getParam(QueueTreeProtocol.QUEUE);
                    int size = Integer.parseInt(req.getParam(QueueTreeProtocol.SIZE));

                    if (req.containsParam(QueueTreeProtocol.TIME_START)) {
                        Long timestamp = Long.parseLong(req.getParam(QueueTreeProtocol.TIME_START));

                        List resultList = qTree.getFIFO(queue, size, timestamp);
                        response.setResponse(resultList);
                        response.setNumResults(resultList.size());

                    } else {

                        List resultList = qTree.getFIFO(queue, size);
                        response.setResponse(resultList);
                        response.setNumResults(resultList.size());
                    }

                    response.setMetadata(qTree.getMetadataMap(queue));
                    response.setType(QueueTreeProtocol.QUEUE_ELEMENTS);
                } else {
                    throw new InvalidRequestException();
                }

            } // create new queue
            else if (operation == VoidBaseOperationType.ADD) {

                if (req.containsParams(QueueTreeProtocol.getRequiredParams(VoidBaseOperationType.ADD))) {
                    String queue = req.getParam(QueueTreeProtocol.QUEUE);
                    int size = Integer.parseInt(req.getParam(QueueTreeProtocol.SIZE));
                    qTree.createQueue(queue, size);

                    response.setResponse(QueueTreeProtocol.CREATED);
                } else {
                    throw new InvalidRequestException();
                }

            } // delete queue
            else if (operation == VoidBaseOperationType.DELETE) {

                if (req.containsParams(QueueTreeProtocol.getRequiredParams(VoidBaseOperationType.DELETE))) {
                    String queue = req.getParam(QueueTreeProtocol.QUEUE);

                    if (queue.equals(QueueTreeProtocol.WILDCARD)) {
                        qTree.deleteAll();
                    } else {
                        qTree.deleteQueue(queue);
                    }
                    response.setResponse(QueueTreeProtocol.DELETED);
                } else {
                    throw new InvalidRequestException();
                }

            } // resize queue
            else if (operation == VoidBaseOperationType.RESIZE) {

                if (req.containsParams(QueueTreeProtocol.getRequiredParams(VoidBaseOperationType.RESIZE))) {
                    String queue = req.getParam(QueueTreeProtocol.QUEUE);
                    int size = Integer.parseInt(req.getParam(QueueTreeProtocol.SIZE));
                    qTree.resizeQueue(queue, size);
                    response.setResponse(QueueTreeProtocol.RESIZED);
                } else {
                    throw new InvalidRequestException();
                }

            } // list registered queues
            else if (operation == VoidBaseOperationType.LIST) {

                LinkedList list = qTree.listQueues();
                response.setNumResults(list.size());

                response.setType(QueueTreeProtocol.QUEUE_LIST);
                response.setResponse(list);

            } // flushAll all queues
            else if (operation == VoidBaseOperationType.FLUSH) {

                if (req.containsParams(QueueTreeProtocol.getRequiredParams(VoidBaseOperationType.FLUSH))) {

                    String queue = req.getParam(QueueTreeProtocol.QUEUE);
                    if (queue.equals(QueueTreeProtocol.WILDCARD)) {
                        qTree.flushAll();
                    } else {
                        qTree.flushQueue(queue);
                    }
                    response.setResponse(QueueTreeProtocol.FLUSHED);

                } else {
                    throw new InvalidRequestException();
                }

            } else if (operation == VoidBaseOperationType.STAT) {

                response.setType(QueueTreeProtocol.QUEUE_STAT);
                response.setResponse(supervisor.getStats());

            } // flushAll all queues

            else {
                throw new InvalidRequestException();
            }

        } catch (InvalidRequestException ex) {
            response.setResponse(QueueTreeProtocol.INVALID_REQUEST);

        } catch (UnsupportedOperationException ex) {
            response.setResponse(QueueTreeProtocol.UNSUPPORTED_OPERATION);

        } catch (InvalidQueueException ex) {
            response.setResponse(QueueTreeProtocol.INVALID_QUEUE);

        } catch (QueueAlreadyExistsException ex) {
            response.setResponse(QueueTreeProtocol.QUEUE_ALREADY_EXISTS);

        } catch (SupervisionException ex) {
            response.setResponse(QueueTreeProtocol.STORAGE_SUPERVISED);
        }

        responseContent.append(QueueTreeFeedUtils.getResultsHeader(response));
        responseContent.append(QueueTreeFeed.HEADER_END);
        responseContent.append(QueueTreeFeed.RESPONSE_START);
        responseContent.append(response.getResponse());
        responseContent.append(QueueTreeFeed.RESPONSE_END);
        responseContent.append(QueueTreeFeed.FEED_END);


        return responseContent.toString() ;

    }

    public void run() {
    }

}
