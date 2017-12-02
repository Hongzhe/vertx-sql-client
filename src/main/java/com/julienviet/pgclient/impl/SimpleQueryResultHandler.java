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

import com.julienviet.pgclient.PgResult;
import com.julienviet.pgclient.PgRow;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.*;

public class SimpleQueryResultHandler implements QueryResultHandler<PgRow> {

  private final Handler<AsyncResult<PgResult<PgRow>>> handler;
  private List<String> columnNames;
  private JsonPgRow head;
  private JsonPgRow tail;
  private int size;
  private Throwable failure;
  private final Queue<PgResultImpl> results = new ArrayDeque<>(1);

  public SimpleQueryResultHandler(Handler<AsyncResult<PgResult<PgRow>>> handler) {
    this.handler = handler;
  }

  @Override
  public void beginRows(List<String> columnNames) {
    this.columnNames = columnNames;
  }

  @Override
  public void addRow(PgRow row) {
    JsonPgRow jsonRow = (JsonPgRow) row;
    if (head == null) {
      head = tail = jsonRow;
    } else {
      tail.next = jsonRow;
      tail = jsonRow;
    }
    size++;
  }

  public boolean hasNext() {
    return results.size() > 0;
  }

  public PgResult<PgRow> next() {
    if (results.size() > 0) {
      return results.poll();
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public void endRows() {
    results.add(new PgResultImpl(columnNames, head, size));
    head = null;
    size = 0;
  }

  @Override
  public void result(boolean suspended) {
  }

  @Override
  public void fail(Throwable cause) {
    failure = cause;
    handler.handle(Future.failedFuture(cause));
  }

  @Override
  public void end() {
    if (failure == null) {
      handler.handle(Future.succeededFuture(results.poll()));
    }
  }
}
