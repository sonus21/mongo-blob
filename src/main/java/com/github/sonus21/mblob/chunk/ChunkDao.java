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

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Communicate the Mongo database for chunk collection operations. It communicates with mongo DB
 * using simple query instead of sophisticated crud methods like {@link
 * org.springframework.data.repository.CrudRepository} since a crud repository requires scanning.
 */
public class ChunkDao {
  private final MongoDatabase mongoDatabase;
  private final String chunkCollectionName;

  public ChunkDao(MongoDatabase mongoDatabase, String chunkCollectionName) {
    this.mongoDatabase = mongoDatabase;
    this.chunkCollectionName = chunkCollectionName;
  }

  /**
   * Delete documents from chunk collection
   *
   * @param ids document ids
   */
  void deleteChunks(Collection<String> ids) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").in(ids));
    Document queryObj = new Document();
    queryObj.putAll(query.getQueryObject());
    mongoDatabase.getCollection(chunkCollectionName).deleteMany(queryObj);
  }

  /**
   * Save set of chunks in the chunk collection.
   *
   * @param chunks set of chunks
   */
  void saveChunks(Collection<Chunk> chunks) {
    List<UpdateOneModel<Document>> updateOneModels = new ArrayList<>();
    UpdateOptions updateOptions = new UpdateOptions();
    updateOptions.upsert(true);
    for (Chunk chunk : chunks) {
      BasicDBObject updateFields = new BasicDBObject();
      updateFields.append("data", chunk.getData());
      updateFields.append("order", chunk.getOrder());
      BasicDBObject setQuery = new BasicDBObject();
      setQuery.append("$set", updateFields);
      updateOneModels.add(new UpdateOneModel<>(eq("_id", chunk.getId()), setQuery, updateOptions));
    }
    mongoDatabase.getCollection(chunkCollectionName).bulkWrite(updateOneModels);
  }

  /**
   * Build map of chunkId to chunk for set of chunkIds. Finds list of chunks from the db based on
   * the chunkIds, then each document is converted to chunk.
   *
   * @param chunkIds list of chunk ids
   * @return map of chunkId to chunk object.
   */
  Map<String, Chunk> getChunks(List<String> chunkIds) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").in(chunkIds));
    Document queryObj = new Document();
    queryObj.putAll(query.getQueryObject());
    FindIterable<Document> results =
        mongoDatabase.getCollection(chunkCollectionName).find(queryObj);
    Map<String, Chunk> chunkIdToChunk = new HashMap<>();
    for (Document doc : results) {
      Chunk chunk = Chunk.getInstance(doc);
      chunkIdToChunk.put(chunk.getId(), chunk);
    }
    return chunkIdToChunk;
  }
}
