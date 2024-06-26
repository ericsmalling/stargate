/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.stargate.transport.internal.messages;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import org.apache.cassandra.stargate.transport.ProtocolVersion;
import org.apache.cassandra.stargate.transport.internal.CBUtil;
import org.apache.cassandra.stargate.transport.internal.Message;

/**
 * Indicates to the client that authentication has succeeded.
 *
 * <p>Optionally ships some final informations from the server (as mandated by SASL).
 */
public class AuthSuccess extends Message.Response {
  public static final Message.Codec<AuthSuccess> codec =
      new Message.Codec<AuthSuccess>() {
        @Override
        public AuthSuccess decode(ByteBuf body, ProtocolVersion version) {
          ByteBuffer b = CBUtil.readValue(body);
          byte[] token = null;
          if (b != null) {
            token = new byte[b.remaining()];
            b.get(token);
          }
          return new AuthSuccess(token);
        }

        @Override
        public void encode(AuthSuccess success, ByteBuf dest, ProtocolVersion version) {
          CBUtil.writeValue(success.token, dest);
        }

        @Override
        public int encodedSize(AuthSuccess success, ProtocolVersion version) {
          return CBUtil.sizeOfValue(success.token);
        }
      };

  private final byte[] token;

  public AuthSuccess(byte[] token) {
    super(Message.Type.AUTH_SUCCESS);
    this.token = token;
  }

  public byte[] getToken() {
    return token;
  }
}
