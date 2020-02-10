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

import org.springframework.context.annotation.Configuration;
/**
 * The required configuration to handle blob document
 *
 * <p>A blob handler stores the data in multiple documents, so it requires some collectio name that
 * can be provided using {@link MongoBlobConfig#blobChunkCollectionName}
 *
 * <p>Also some of the mongodb might have lower or higher BSON document size, by default it assumes
 * 16MB, but it can be configured using {@link MongoBlobConfig#blobSize}
 */
@Configuration
public class MongoBlobConfig {
  private Integer blobSize = 16770000;
  private String blobChunkCollectionName = "blob_chunk";

  public void setBlobSize(Integer blobSize) {
    this.blobSize = blobSize;
  }

  public Integer getBlobSize() {
    return blobSize;
  }

  public String getBlobChunkCollectionName() {
    return blobChunkCollectionName;
  }

  public void setBlobChunkCollectionName(String blobChunkCollectionName) {
    this.blobChunkCollectionName = blobChunkCollectionName;
  }
}
