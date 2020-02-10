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

public class BsonMarshaler {

  private final MappingMongoConverter mappingMongoConverter;

  public BsonMarshaler(MappingMongoConverter mappingMongoConverter) {
    this.mappingMongoConverter = mappingMongoConverter;
  }

  String write(BlobDocument o) {
    Document bson = new Document();
    mappingMongoConverter.write(o, bson);
    return bson.toJson();
  }

  BlobDocument read(BlobDocument src, String data) {
    Document document = Document.parse(data);
    return mappingMongoConverter.read(src.getClass(), document);
  }
}
