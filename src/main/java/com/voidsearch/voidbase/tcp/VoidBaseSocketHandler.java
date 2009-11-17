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

package com.voidsearch.voidbase.tcp;

import com.voidsearch.voidbase.tcp.message.TCPMessage;
import com.voidsearch.voidbase.util.GenericUtil;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelPipelineCoverage("all")
public class VoidBaseSocketHandler extends SimpleChannelUpstreamHandler {
    protected TCPMessage messageHandler = null;
    protected static final Logger logger = LoggerFactory.getLogger(VoidBaseSocketHandler.class.getName());

    public VoidBaseSocketHandler() {
    }

    public VoidBaseSocketHandler(String name) throws TCPServerException {
        initializeMessageHandler(name);
    }

    public void initializeMessageHandler(String name) throws TCPServerException {
        logger.info("Initializing message handler: " + name);

        try {
            messageHandler = (TCPMessage) Class.forName(name).newInstance();
        } catch (ClassNotFoundException e) {
            GenericUtil.logException(e);
            throw new TCPServerException("Class not found: " + name);
        } catch (IllegalAccessException e) {
            GenericUtil.logException(e);
            throw new TCPServerException("Illegal access to constructor for: " + name);
        } catch (InstantiationException e) {
            GenericUtil.logException(e);
            throw new TCPServerException("Failed to instantiate: " + name);
        }
    }

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            logger.debug(e.toString());
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
        boolean close = false;

        System.out.println("WAAAA!");
        System.out.println(event.getMessage());

        ChannelFuture future = event.getChannel().write((String)event.getMessage());
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.error("Unexpected exception from downstream.", e.getCause());
        e.getChannel().close();
    }
}
