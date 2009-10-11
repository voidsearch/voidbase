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

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import static org.jboss.netty.channel.Channels.pipeline;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

public class HttpRequestPipelineFactory implements ChannelPipelineFactory {
  public boolean chunked = true;
  public Integer chunkSize = 1048576;

  private final ChannelHandler handler;

  public HttpRequestPipelineFactory(ChannelHandler handler) {
    this.handler = handler;
  }

  public ChannelPipeline getPipeline() throws Exception {
    ChannelPipeline pipeline = pipeline();

    pipeline.addLast("decoder", new HttpRequestDecoder());

    // aggregate if not chunked http
    if (chunked == false) {
      pipeline.addLast("aggregator", new HttpChunkAggregator(chunkSize));
    }

    pipeline.addLast("encoder", new HttpResponseEncoder());
    pipeline.addLast("handler", handler);

    return pipeline;
  }
}
