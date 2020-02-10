package com.github.sonus21.mblob.chunk;

import static com.mongodb.client.model.Filters.eq;

import com.github.sonus21.mblob.Pair;
import com.github.sonus21.mblob.document.BlobDocument;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

public class ChunkManager {
  private final Integer maxDocumentSizeInByte;
  private final MappingMongoConverter mappingMongoConverter;
  private final MongoDatabase mongoDatabase;
  private static final String CHUNK_COLLECTION_NAME = "blob_chunk";
  private static final int FIND_BATCH_SIZE = 500;

  private Pair<Integer, String> getChunkDetail(BlobDocument o) {
    Document bson = new Document();
    mappingMongoConverter.write(o, bson);
    String data = bson.toJson();
    int size = data.length();
    int count = size / maxDocumentSizeInByte;
    if (size % maxDocumentSizeInByte != 0) {
      count += 1;
    }
    return new Pair<>(count, data);
  }

  public ChunkManager(
      Integer maxDocumentSizeInByte,
      MappingMongoConverter mappingMongoConverter,
      MongoDatabase mongoDatabase) {
    this.maxDocumentSizeInByte = maxDocumentSizeInByte;
    this.mappingMongoConverter = mappingMongoConverter;
    this.mongoDatabase = mongoDatabase;
  }

  public Collection<Chunk> createChunks(BlobDocument entity) {
    List<Chunk> chunks = chunks(entity);
    if (!chunks.isEmpty()) {
      List<UpdateOneModel<Document>> updateOneModels = new ArrayList<>();
      UpdateOptions updateOptions = new UpdateOptions();
      updateOptions.upsert(true);
      for (Chunk chunk : chunks) {
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("data", chunk.getData());
        updateFields.append("order", chunk.getOrder());
        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);
        updateOneModels.add(
            new UpdateOneModel<>(eq("_id", chunk.getId()), setQuery, updateOptions));
      }
      mongoDatabase.getCollection(CHUNK_COLLECTION_NAME).bulkWrite(updateOneModels);
    }
    return chunks;
  }

  private List<Chunk> chunks(BlobDocument o) {
    Pair<Integer, String> chunkDetail = getChunkDetail(o);
    List<Chunk> chunks = new ArrayList<>();
    String data = chunkDetail.getSecond();
    int length = data.length();
    if (chunkDetail.getFirst() > 1) {
      for (int i = 0; i < chunkDetail.getFirst(); i++) {
        int start = i * maxDocumentSizeInByte;
        Chunk chunk =
            new Chunk(
                UUID.randomUUID().toString(),
                i,
                data.substring(start, Math.min(length, start + maxDocumentSizeInByte)));
        chunks.add(chunk);
      }
    }
    return chunks;
  }

  public void deleteChunks(Collection<String> ids) {
    if (ids.isEmpty()) {
      return;
    }
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").in(ids));
    Document queryObj = new Document();
    queryObj.putAll(query.getQueryObject());
    mongoDatabase.getCollection(CHUNK_COLLECTION_NAME).deleteMany(queryObj);
  }

  private Map<String, Chunk> getChunks(List<String> chunkIds) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").in(chunkIds));
    Document queryObj = new Document();
    queryObj.putAll(query.getQueryObject());
    FindIterable<Document> results =
        mongoDatabase.getCollection(CHUNK_COLLECTION_NAME).find(queryObj);
    Map<String, Chunk> chunkIdToChunk = new HashMap<>();
    for (Document doc : results) {
      Chunk chunk = Chunk.getInstance(doc);
      chunkIdToChunk.put(chunk.getId(), chunk);
    }
    return chunkIdToChunk;
  }

  private void setChunkDetail(
      List<String> chunkIds, List<BlobDocument> updateRequiredDocs, List<Object> updatedObjects) {
    if (chunkIds.isEmpty()) {
      return;
    }
    Map<String, Chunk> chunkIdToChunk = getChunks(chunkIds);
    for (BlobDocument blobDocument : updateRequiredDocs) {
      List<Chunk> chunks = new ArrayList<>();
      for (String id : blobDocument.getChunkIds()) {
        chunks.add(chunkIdToChunk.get(id));
      }
      chunks.sort(Comparator.comparingInt(Chunk::getOrder));
      StringBuilder sb = new StringBuilder();
      for (Chunk chunk : chunks) {
        sb.append(chunk.getData());
      }
      Document document = Document.parse(sb.toString());
      BlobDocument newDocument = mappingMongoConverter.read(blobDocument.getClass(), document);
      updatedObjects.add(newDocument);
    }
  }

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
