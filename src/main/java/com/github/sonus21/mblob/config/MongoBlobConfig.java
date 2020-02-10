package com.github.sonus21.mblob.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoBlobConfig {
  private Integer blobSize = 16770000;

  public void setBlobSize(Integer blobSize) {
    this.blobSize = blobSize;
  }

  public Integer getBlobSize() {
    return blobSize;
  }
}
