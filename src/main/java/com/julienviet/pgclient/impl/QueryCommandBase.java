/*
 * Copyright (C) 2017 Julien Viet
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
 *
 */

package com.julienviet.pgclient.impl;

import com.julienviet.pgclient.PgException;
import com.julienviet.pgclient.PgRow;
import com.julienviet.pgclient.codec.decoder.InboundMessage;
import com.julienviet.pgclient.codec.decoder.message.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */

abstract class QueryCommandBase extends CommandBase {

  protected final QueryResultHandler<PgRow> handler;

  public QueryCommandBase(QueryResultHandler<PgRow> handler) {
    this.handler = handler;
  }

  @Override
  public void handleMessage(InboundMessage msg) {
    if (msg.getClass() == ReadyForQuery.class) {
      super.handleMessage(msg);
      handler.end();
    } else if (msg.getClass() == CommandComplete.class) {
      handler.result(false);
    } else if (msg.getClass() == ErrorResponse.class) {
      ErrorResponse error = (ErrorResponse) msg;
      fail(new PgException(error));
    } else {
      super.handleMessage(msg);
    }
  }

  @Override
  void fail(Throwable cause) {
    handler.fail(cause);
  }
}
