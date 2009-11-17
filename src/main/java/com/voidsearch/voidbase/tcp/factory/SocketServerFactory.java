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

package com.voidsearch.voidbase.tcp.factory;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServerFactory {
    protected static final Logger logger = LoggerFactory.getLogger(SocketServerFactory.class.getName());

    public static ChannelPipelineFactory getSocketServer(String codec, ChannelHandler handler) throws SocketServerFactoryException {
        String name;

        // sanity check
        if (codec == null) {
            throw new SocketServerFactoryException("Empty codec name");
        }
        name = codec.toLowerCase();

        // get channel
        if (name.equals("line")) {
            logger.info("Initializing line-delimited codec channel.");
            return new LineDelimitedSocketServerPipelineFactory(handler);
        }

        throw new SocketServerFactoryException("Unknown codec: " + codec);
    }
}
