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

package com.github.sonus21.mblob.config;

import com.github.sonus21.mblob.aspect.BlobAspectHandler;
import com.github.sonus21.mblob.chunk.BsonMarshaller;
import com.github.sonus21.mblob.chunk.ChunkDao;
import com.github.sonus21.mblob.chunk.ChunkManager;
import com.github.sonus21.mblob.chunk.ChunkPartitionManager;
import com.github.sonus21.mblob.op.BlobOpHandler;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * This class enables the auto-configuration of spring boot app
 *
 * <p>Some configurations can be provided using {@link MongoBlobConfig}
 */
@Configuration
@ConditionalOnProperty(name = "mongo.blob.enabled", matchIfMissing = true)
public class AutoConfiguration {
  @Autowired(required = false)
  private MongoBlobConfig mongoBlobConfig = new MongoBlobConfig();

  @Bean
  @ConditionalOnMissingBean
  public BlobAspectHandler mongoBlobHandler(BlobOpHandler blobOpHandler) {
    return new BlobAspectHandler(blobOpHandler);
  }

  @Bean
  @ConditionalOnMissingBean
  public BlobOpHandler blobOpHandler(ChunkManager blobChunkManager) {
    return new BlobOpHandler(blobChunkManager);
  }

  @Bean
  @ConditionalOnMissingBean
  public ChunkManager blobChunkManager(
      MappingMongoConverter mappingMongoConverter,
      MongoClient mongoClient,
      MongoProperties mongoProperties) {
    BsonMarshaller marshaller = new BsonMarshaller(mappingMongoConverter);
    return new ChunkManager(
        marshaller,
        new ChunkPartitionManager(mongoBlobConfig.getBlobSize(), marshaller),
        new ChunkDao(
            mongoClient.getDatabase(mongoProperties.getDatabase()),
            mongoBlobConfig.getBlobChunkCollectionName()));
  }
}
