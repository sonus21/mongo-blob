/*
 * Copyright 2020 Sonu Kumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sonus21.mblob.utils;

import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MFindIterable implements FindIterable<Document> {
  private final List<Document> documentList;

  public MFindIterable(List<Document> documentList) {
    this.documentList = documentList;
  }

  @Override
  public FindIterable<Document> filter(Bson filter) {
    return null;
  }

  @Override
  public FindIterable<Document> limit(int limit) {
    return null;
  }

  @Override
  public FindIterable<Document> skip(int skip) {
    return null;
  }

  @Override
  public FindIterable<Document> maxTime(long maxTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  public FindIterable<Document> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
    return null;
  }

  @Override
  public FindIterable<Document> modifiers(Bson modifiers) {
    return null;
  }

  @Override
  public FindIterable<Document> projection(Bson projection) {
    return null;
  }

  @Override
  public FindIterable<Document> sort(Bson sort) {
    return null;
  }

  @Override
  public FindIterable<Document> noCursorTimeout(boolean noCursorTimeout) {
    return null;
  }

  @Override
  public FindIterable<Document> oplogReplay(boolean oplogReplay) {
    return null;
  }

  @Override
  public FindIterable<Document> partial(boolean partial) {
    return null;
  }

  @Override
  public FindIterable<Document> cursorType(CursorType cursorType) {
    return null;
  }

  @Override
  public FindIterable<Document> batchSize(int batchSize) {
    return null;
  }

  @Override
  public FindIterable<Document> collation(Collation collation) {
    return null;
  }

  @Override
  public FindIterable<Document> comment(String comment) {
    return null;
  }

  @Override
  public FindIterable<Document> hint(Bson hint) {
    return null;
  }

  @Override
  public FindIterable<Document> max(Bson max) {
    return null;
  }

  @Override
  public FindIterable<Document> min(Bson min) {
    return null;
  }

  @Override
  public FindIterable<Document> maxScan(long maxScan) {
    return null;
  }

  @Override
  public FindIterable<Document> returnKey(boolean returnKey) {
    return null;
  }

  @Override
  public FindIterable<Document> showRecordId(boolean showRecordId) {
    return null;
  }

  @Override
  public FindIterable<Document> snapshot(boolean snapshot) {
    return null;
  }

  @Override
  public MongoCursor<Document> iterator() {
    return new MCursor(documentList);
  }

  @Override
  public MongoCursor<Document> cursor() {
    return null;
  }

  @Override
  public Document first() {
    return null;
  }

  @Override
  public <U> MongoIterable<U> map(Function<Document, U> mapper) {
    return null;
  }

  @Override
  public void forEach(Block<? super Document> block) {
    for (Document d : documentList) {
      block.apply(d);
    }
  }

  @Override
  public <A extends Collection<? super Document>> A into(A target) {
    return null;
  }
}
