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

package com.voidsearch.voidbase.http;

import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voidsearch.voidbase.core.VoidBaseCore;
import com.voidsearch.voidbase.core.VoidBaseRequestQueue;
import com.voidsearch.voidbase.core.VoidBaseResourceRegister;
import com.voidsearch.voidbase.core.HandlerNotRegisteredException;
import com.voidsearch.voidbase.module.VoidBaseModuleResponse;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.module.VoidBaseModule;
import com.voidsearch.voidbase.module.VoidBaseResponseType;
import com.voidsearch.voidbase.util.GenericUtil;

@ChannelPipelineCoverage("one")
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    protected volatile HttpRequest request;
    protected volatile boolean readingChunks;
    protected VoidBaseCore core = VoidBaseCore.getInstance();
    protected static final Logger logger = LoggerFactory.getLogger(SimpleChannelUpstreamHandler.class.getClass());

    public HttpRequestHandler() { }
    private ChannelBuffer buf= null;
    
    private static VoidBaseRequestQueue requestQueue = VoidBaseRequestQueue.getInstance();
    private static VoidBaseResourceRegister resourceRegister = VoidBaseResourceRegister.getInstance();

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!readingChunks) {

            HttpRequest request = this.request = (HttpRequest) e.getMessage();
            
            if (request.isChunked()) {
                readingChunks = true;
            } else {
                ChannelBuffer content = request.getContent();

                if (content.readable()) {
                    // TODO: handle content
                }

                try {
                    VoidBaseModuleRequest moduleReq = new VoidBaseModuleRequest(request, content);
                    VoidBaseModule handler = resourceRegister.getHandler(moduleReq.getResource());

                    if (requestQueue.slotAvailable(handler,moduleReq)) {
                        writeResponse(e, handler.handle(moduleReq));
                        requestQueue.releaseSlot(handler,moduleReq);
                    } else {
                        requestQueue.enqueue(handler,moduleReq);
                        writeResponse(e, handler.handle(moduleReq));
                        requestQueue.releaseSlot(handler,moduleReq);
                    }

                } catch (HandlerNotRegisteredException ex) {
                    writeResponse(e, new VoidBaseModuleResponse("HandlerNotRegistered"));
                }

            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();

            ChannelBuffer content = request.getContent();

            if (chunk.isLast()) {
                readingChunks = false;
                writeResponse(e, new VoidBaseModuleResponse("HandlerNotRegistered"));
            }
        }
    }

    private void writeResponse(MessageEvent e, VoidBaseModuleResponse result) {

        if(result.isBinary){
            buf = ChannelBuffers.copiedBuffer(result.buffer);
        }else{
            buf = ChannelBuffers.copiedBuffer(result.getMessage() + "\n", "UTF-8");
        }

        // decide whether to close the connection or not
        boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(request.getHeader(HttpHeaders.Names.CONNECTION)) ||
                                                                  request.getProtocolVersion().equals(HttpVersion.HTTP_1_0) &&
                                                                  !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(request.getHeader(HttpHeaders.Names.CONNECTION));

        // create response object
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                                                    result.status.serializeToHttpResponseStatus());

        response.setContent(buf);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE, result.type.serializeToHttpContentType());
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buf.readableBytes()));

        // write the response
        ChannelFuture future = e.getChannel().write(response);

        // close the connection after the write operation is done if necessary
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.error("Unexpected exception from downstream: " + e.getCause());
        GenericUtil.logException(e.getCause().getMessage(), e.getCause().getStackTrace());

        e.getChannel().close();
    }

    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        readingChunks = false;
        ctx.sendUpstream(e);
    }

    public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        readingChunks = false;
        ctx.sendUpstream(e);
    }

}
