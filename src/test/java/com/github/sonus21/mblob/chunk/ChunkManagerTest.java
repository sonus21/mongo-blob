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

import static com.github.sonus21.mblob.utils.ObjectFactory.createBlobObject;
import static com.github.sonus21.mblob.utils.ObjectFactory.createNonBlobObject;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.sonus21.mblob.utils.ObjectFactory;
import com.github.sonus21.mblob.utils.TestObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner.StrictStubs;
import test.blob.mongo.entity.Notification;

@RunWith(StrictStubs.class)
@Slf4j
public class ChunkManagerTest {
  private BsonMarshaller marshaller = mock(BsonMarshaller.class);
  private ChunkPartitionManager manager = mock(ChunkPartitionManager.class);
  private ChunkDao chunkDao = mock(ChunkDao.class);
  private ChunkManager chunkManager = new ChunkManager(marshaller, manager, chunkDao);

  @Test(expected = IllegalArgumentException.class)
  public void createChunksNull() {
    chunkManager.createChunks(null);
  }

  @Test
  public void createChunksBelowThresholdSize() {
    doReturn(Collections.emptyList()).when(manager).chunks(any(Notification.class));
    chunkManager.createChunks(ObjectFactory.createBlobObject(false, 1));
    verify(chunkDao, times(0)).saveChunks(anyList());
  }

  @Test
  public void createChunks() {
    Chunk chunk = new Chunk(UUID.randomUUID().toString(), 0, "This is test");
    Chunk chunk2 = new Chunk(UUID.randomUUID().toString(), 1, "data");
    List<Chunk> chunks = new ArrayList<>();
    chunks.add(chunk);
    chunks.add(chunk2);
    doReturn(chunks).when(manager).chunks(any(Notification.class));
    chunkManager.createChunks(ObjectFactory.createBlobObject(false, 1));
    verify(chunkDao, times(1)).saveChunks(anyList());
  }

  @Test
  public void deleteChunksEmptyCollection() {
    chunkManager.deleteChunks(null);
    chunkManager.deleteChunks(Collections.emptyList());
    verify(chunkDao, times(0)).deleteChunks(any());
  }

  @Test
  public void deleteChunks() {
    List<String> ids = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      ids.add(UUID.randomUUID().toString());
    }
    chunkManager.deleteChunks(ids);
    verify(chunkDao, times(1)).deleteChunks(ids);
  }

  @Test
  public void getUpdatedEntitiesEmptyCollection() {
    assertEquals(Collections.emptyList(), chunkManager.getUpdatedEntities(Collections.emptyList()));
  }

  @Test
  public void getUpdatedEntitiesNonBlobDocument() {
    List<Object> foos = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      foos.add(new TestObject());
    }
    assertEquals(foos, chunkManager.getUpdatedEntities(foos));
    verify(chunkDao, times(0)).getChunks(anyList());
  }

  @Test
  public void getUpdatedEntitiesNonBlobAndBlobDocument() {
    List<Object> objs = new ArrayList<>();
    List<String> chunkIds = new ArrayList<>();
    int numCalls = 0;
    int prevSize = 0;
    for (int i = 0; i < 1000; i++) {
      if (ObjectFactory.nonBlobObjectPermitted()) {
        objs.add(createNonBlobObject());
      } else {
        Notification notification = createBlobObject(true, 1);
        chunkIds.addAll(notification.getChunkIds());
        objs.add(notification);
        if (chunkIds.size() >= prevSize + 100) {
          numCalls += 1;
          prevSize = chunkIds.size();
        }
      }
    }
    if (chunkIds.size() != prevSize) {
      numCalls += 1;
    }
    List<String> chunkIdsQueries = new ArrayList<>();
    when(chunkDao.getChunks(anyList()))
        .thenAnswer(
            invocation -> {
              List<String> arg = invocation.getArgument(0);
              chunkIdsQueries.addAll(arg);
              return Collections.emptyMap();
            });
    assertEquals(objs.size(), chunkManager.getUpdatedEntities(objs).size());
    assertEquals(chunkIds, chunkIdsQueries);
    verify(chunkDao, times(numCalls)).getChunks(anyList());
  }

  @Test
  public void getUpdatedEntitiesBlobDocument() {
    List<Object> objs = new ArrayList<>();
    List<String> chunkIds = new ArrayList<>();
    int numCalls = 0;
    int prevSize = 0;
    for (int i = 0; i < 1000; i++) {
      if (ObjectFactory.nonBlobObjectPermitted()) {
        objs.add(new TestObject());
      } else {
        Notification notification = ObjectFactory.createBlobObject(true, 10);
        chunkIds.addAll(notification.getChunkIds());
        objs.add(notification);
        if (chunkIds.size() >= prevSize + 100) {
          numCalls += 1;
          prevSize = chunkIds.size();
        }
      }
    }
    if (chunkIds.size() != prevSize) {
      numCalls += 1;
    }
    List<String> chunkIdsQueries = new ArrayList<>();
    when(chunkDao.getChunks(anyList()))
        .thenAnswer(
            invocation -> {
              List<String> arg = invocation.getArgument(0);
              chunkIdsQueries.addAll(arg);
              return Collections.emptyMap();
            });
    assertEquals(objs.size(), chunkManager.getUpdatedEntities(objs).size());
    assertEquals(chunkIds, chunkIdsQueries);
    verify(chunkDao, times(numCalls)).getChunks(anyList());
  }
}
