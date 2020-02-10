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

import com.github.sonus21.mblob.Pair;
import com.github.sonus21.mblob.document.BlobDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Handle partitioning of blob document. Creates list of chunks for the given blob document.
 * Partitioning is handled using bson marshaller, if the size of marshalled string is more than the
 * document size then it will split into multiple chunks otherwise no split is required.
 */
public class ChunkPartitionManager {
  private final int maxDocumentSizeInByte;
  private final BsonMarshaller bsonMarshaller;

  public ChunkPartitionManager(int maxDocumentSizeInByte, BsonMarshaller bsonMarshaller) {
    this.maxDocumentSizeInByte = maxDocumentSizeInByte;
    this.bsonMarshaller = bsonMarshaller;
  }

  /**
   * Create chunks for the entity, it finds number of possible chunks, if length is zero then
   * nothing to do as this can be stored as it as. It splits json string into multiple substrings
   * and each substring is stored in individual record. Each record, has an order that's created
   * based on the split order. First chunk is given order 0.
   *
   * @param o entity
   * @return list of chunks.
   */
  List<Chunk> chunks(BlobDocument o) {
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

  /**
   * Creates a json string with the help of the {@link MappingMongoConverter} object. If the size of
   * json string is larger than blobSize then we divide the json string length by blob size to find
   * the number of chunks.
   *
   * @param o a blob document that may require chunk
   * @return pair of chunk count and json string.
   */
  private Pair<Integer, String> getChunkDetail(BlobDocument o) {
    String data = bsonMarshaller.write(o);
    int size = data.length();
    int count = size / maxDocumentSizeInByte;
    if (size % maxDocumentSizeInByte != 0) {
      count += 1;
    }
    return new Pair<>(count, data);
  }
}
