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

import com.github.sonus21.mblob.document.BlobDocument;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * ChunkManager handles the operations related to chunk(s). It supports the chunk management in the
 * database, that stores the chunk in database and find the set of chunks from the database.
 */
@Slf4j
public class ChunkManager {
  private final ChunkDao chunkDao;
  private final BsonMarshaller bsonMarshaller;
  private final ChunkPartitionManager chunkPartitionManager;
  private static final int FIND_BATCH_SIZE = 100;

  public ChunkManager(
      BsonMarshaller bsonMarshaller,
      ChunkPartitionManager chunkPartitionManager,
      ChunkDao chunkDao) {
    this.chunkDao = chunkDao;
    Assert.notNull(bsonMarshaller, "bsonMarshaller cannot be null");
    Assert.notNull(chunkPartitionManager, "chunkPartitionManager cannot be null");
    Assert.notNull(chunkDao, "chunkDao cannot be null");
    this.bsonMarshaller = bsonMarshaller;
    this.chunkPartitionManager = chunkPartitionManager;
  }

  /**
   * Create requires chunks and store them into the database, each chunk is stored in individual
   * record. Bulk write operation is used in the case of large document leads to multiple chunks.
   *
   * @param entity entity that may require split.
   * @return collection of chunks.
   */
  public Collection<Chunk> createChunks(BlobDocument entity) {
    Assert.notNull(entity, "entity cannot be null");
    List<Chunk> chunks = chunkPartitionManager.chunks(entity);
    if (!chunks.isEmpty()) {
      chunkDao.saveChunks(chunks);
    }
    return chunks;
  }

  /**
   * Delete list of chunks from the database
   *
   * @param ids list of ids of those chunks
   */
  public void deleteChunks(Collection<String> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return;
    }
    chunkDao.deleteChunks(ids);
  }

  private void setChunkDetail(
      List<String> chunkIds, List<BlobDocument> updateRequiredDocs, List<Object> updatedObjects) {
    if (chunkIds.isEmpty()) {
      return;
    }
    Map<String, Chunk> chunkIdToChunk = chunkDao.getChunks(chunkIds);
    for (BlobDocument blobDocument : updateRequiredDocs) {
      List<Chunk> chunks = new ArrayList<>();
      for (String id : blobDocument.getChunkIds()) {
        Chunk chunk = chunkIdToChunk.get(id);
        if (chunk == null) {
          log.error("Chunk data is not found in DB for id: {}", id);
        } else if (!chunk.isValid()) {
          log.error("Chunk data is not valid in DB for id: {}, data: {}", id, chunk);
        } else {
          chunks.add(chunkIdToChunk.get(id));
        }
      }
      if (!chunks.isEmpty()) {
        chunks.sort(Comparator.comparingInt(Chunk::getOrder));
        StringBuilder sb = new StringBuilder();
        for (Chunk chunk : chunks) {
          sb.append(chunk.getData());
        }
        BlobDocument newDocument = bsonMarshaller.read(blobDocument, sb.toString());
        updatedObjects.add(newDocument);
      } else {
        updatedObjects.add(blobDocument);
      }
    }
  }

  /**
   * Update set of entities, this will bring the document from the chunk collection, and mapped it
   * back to the original document. Batching is used to avoid the large query execution and large
   * data download in one shot.
   *
   * @param entities list of entities those require update
   * @return list of updated entities.
   */
  public List<Object> getUpdatedEntities(List<Object> entities) {
    if (entities.isEmpty()) {
      return entities;
    }
    List<String> chunkIds = new ArrayList<>();
    List<BlobDocument> updateRequiredDocs = new ArrayList<>();
    List<Object> updatedObjects = new ArrayList<>();
    for (Object o : entities) {
      if (o instanceof BlobDocument) {
        Collection<String> docChunkIds = ((BlobDocument) (o)).getChunkIds();
        if (!CollectionUtils.isEmpty(docChunkIds)) {
          chunkIds.addAll(docChunkIds);
          updateRequiredDocs.add((BlobDocument) o);
          if (chunkIds.size() >= FIND_BATCH_SIZE) {
            setChunkDetail(chunkIds, updateRequiredDocs, updatedObjects);
            updateRequiredDocs.clear();
            chunkIds.clear();
          }
        } else {
          updatedObjects.add(o);
        }
      } else {
        updatedObjects.add(o);
      }
    }
    setChunkDetail(chunkIds, updateRequiredDocs, updatedObjects);
    return updatedObjects;
  }
}
