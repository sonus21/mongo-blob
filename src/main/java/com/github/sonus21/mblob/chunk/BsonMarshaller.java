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
import org.bson.Document;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * BSON marshaller marshal and unmarshal BSON document into string and vice-versa. BSON document is
 * also LinkedHashMap in terms of object, but it requires conversion in the Domain mapped object.
 */
public class BsonMarshaller {

  private final MappingMongoConverter mappingMongoConverter;

  public BsonMarshaller(MappingMongoConverter mappingMongoConverter) {
    this.mappingMongoConverter = mappingMongoConverter;
  }

  /**
   * Convert a Domain object to BSON formatted string.
   *
   * @param o domain object
   * @return BSON converted string
   */
  String write(BlobDocument o) {
    Document bson = new Document();
    mappingMongoConverter.write(o, bson);
    return bson.toJson();
  }

  /**
   * Convert a bson string back to a class object
   *
   * @param src src domain model
   * @param data BSON formatted string
   * @return object of domain model
   */
  BlobDocument read(BlobDocument src, String data) {
    Document document = Document.parse(data);
    return mappingMongoConverter.read(src.getClass(), document);
  }
}
