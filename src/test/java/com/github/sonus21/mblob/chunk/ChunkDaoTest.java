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

package com.github.sonus21.mblob.chunk;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.sonus21.mblob.utils.MFindIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ChunkDaoTest {
  private MongoDatabase mongoDatabase = mock(MongoDatabase.class);
  private MongoCollection mongoCollection = mock(MongoCollection.class);
  private String collectionName = "test_chunk";
  private ChunkDao chunkDao = new ChunkDao(mongoDatabase, collectionName);
  private Chunk chunk, chunk2;
  private Document document, document2;

  private Chunk createObject(int order) {
    Chunk c = new Chunk();
    c.setData(UUID.randomUUID().toString());
    c.setId(UUID.randomUUID().toString());
    c.setOrder(order);
    return c;
  }

  private Document convertToDoc(Chunk c) {
    Document d = new Document();
    d.put("data", c.getData());
    d.put("_id", c.getId());
    d.put("order", c.getOrder());
    return d;
  }

  @Before
  public void init() {
    this.chunk = createObject(0);
    this.chunk2 = createObject(1);
    this.document = convertToDoc(this.chunk);
    this.document2 = convertToDoc(this.chunk2);
  }

  @Test
  public void deleteChunks() {
    doReturn(mongoCollection).when(mongoDatabase).getCollection(collectionName);
    chunkDao.deleteChunks(Collections.singleton("1234"));
    verify(mongoCollection, times(1)).deleteMany(any());
  }

  @Test
  public void saveChunks() {
    doReturn(mongoCollection).when(mongoDatabase).getCollection(collectionName);
    chunkDao.saveChunks(Collections.singleton(chunk));
    verify(mongoCollection, times(1)).bulkWrite(anyList());
  }

  @Test
  public void getChunks() {
    List<Document> chunks = new ArrayList<>();
    chunks.add(document);
    chunks.add(document2);
    FindIterable<Document> results = new MFindIterable(chunks);
    List<String> ids = new ArrayList<>();
    ids.add(chunk.getId());
    ids.add(chunk2.getId());
    Map<String, Chunk> m = new HashMap<>();
    m.put(chunk.getId(), chunk);
    m.put(chunk2.getId(), chunk2);

    doReturn(mongoCollection).when(mongoDatabase).getCollection(collectionName);
    doReturn(results).when(mongoCollection).find(any(Bson.class));
    assertEquals(m, chunkDao.getChunks(ids));
  }
}
