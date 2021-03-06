/*
 * Copyright (C) 2019,2020 IBM Corporation
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
package io.vertx.db2client.impl;

import java.util.Map;

import io.netty.channel.ChannelPipeline;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.net.impl.NetSocketInternal;
import io.vertx.db2client.impl.codec.DB2Codec;
import io.vertx.db2client.impl.command.InitialHandshakeCommand;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.SocketConnectionBase;

public class DB2SocketConnection extends SocketConnectionBase {

    private DB2Codec codec;
    private String dbName;
    private Handler<Void> closeHandler;

    public DB2SocketConnection(NetSocketInternal socket, boolean cachePreparedStatements,
            int preparedStatementCacheSize, int preparedStatementCacheSqlLimit, ContextInternal context) {
        super(socket, cachePreparedStatements, preparedStatementCacheSize, preparedStatementCacheSqlLimit, 1, context);
    }

    void sendStartupMessage(String username,
            String password,
            String database,
            Map<String, String> properties,
            Promise<Connection> completionHandler) {
        this.dbName = database;
        InitialHandshakeCommand cmd = new InitialHandshakeCommand(this, username, password, database, properties);
        schedule(cmd, completionHandler);
    }

    public String database() {
        return dbName;
    }

    @Override
    public void init() {
        codec = new DB2Codec(this);
        ChannelPipeline pipeline = socket.channelHandlerContext().pipeline();
        pipeline.addBefore("handler", "codec", codec);
        super.init();
    }
    
    @Override
    public void handleClose(Throwable t) {
      super.handleClose(t);
      context().runOnContext(closeHandler);
    }
    
    public DB2SocketConnection closeHandler(Handler<Void> handler) {
      closeHandler = handler;
      return this;
    }
}
