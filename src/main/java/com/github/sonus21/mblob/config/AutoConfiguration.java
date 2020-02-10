package com.github.sonus21.mblob.config;

import com.github.sonus21.mblob.aspect.BlobAspectHandler;
import com.github.sonus21.mblob.chunk.ChunkManager;
import com.github.sonus21.mblob.op.BlobOpHandler;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

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
    return new ChunkManager(
        mongoBlobConfig.getBlobSize(),
        mappingMongoConverter,
        mongoClient.getDatabase(mongoProperties.getDatabase()));
  }
}
