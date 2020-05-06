/*
 * Copyright (C) 2012-2016 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.nifty.core;

import org.jboss.netty.buffer.ChannelBuffer;

public class ThriftMessage
{
    private final ChannelBuffer buffer;
    private final ThriftTransportType transportType;
    private long processStartTimeMillis;

    public ThriftMessage(ChannelBuffer buffer, ThriftTransportType transportType)
    {
        this.buffer = buffer;
        this.transportType = transportType;
    }

    public ChannelBuffer getBuffer()
    {
        return buffer;
    }

    public ThriftTransportType getTransportType()
    {
        return transportType;
    }

    /**
     * Gets a {@link Factory} for creating messages similar to this one. Used by {@link
     * NiftyDispatcher} to create response messages that are similar to their corresponding
     * request messages.
     *
     * @return The {@link Factory}
     */
    public Factory getMessageFactory()
    {
        return new Factory()
        {
            @Override
            public ThriftMessage create(ChannelBuffer messageBuffer)
            {
                return new ThriftMessage(messageBuffer, getTransportType());
            }
        };
    }

    /**
     * Standard Thrift clients require ordered responses, so even though Nifty can run multiple
     * requests from the same client at the same time, the responses have to be held until all
     * previous responses are ready and have been written. However, through the use of extended
     * protocols and codecs, a request can indicate that the client understands
     * out-of-order responses.
     *
     * 标准的thrift客户端是要求有顺的响应，即使nifty可以同时接受同一个客户端的多个请求，某个响应还是要等到之前的响应已经响应了才能响应给客户端。
     * 然而，通过使用扩展的协议和编码，一个请求可以标明客户端是否可以接受无序的响应
     *
     *
     * @return {@code true} if ordered responses are required
     * 这里只返回true，即nifty按照标准的thrift客户端处理
     */
    public boolean isOrderedResponsesRequired()
    {
        return true;
    }

    public long getProcessStartTimeMillis() { return processStartTimeMillis; }

    public void setProcessStartTimeMillis(long processStartTimeMillis)
    {
        this.processStartTimeMillis = processStartTimeMillis;
    }

    public static interface Factory
    {
        public ThriftMessage create(ChannelBuffer messageBuffer);
    }
}
