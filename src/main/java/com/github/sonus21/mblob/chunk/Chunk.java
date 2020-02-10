package com.github.sonus21.mblob.chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chunk {
  @Id private String id;
  private Integer order;
  private String data;

  public static Chunk getInstance(Document document) {
    Chunk chunk = new Chunk();
    chunk.setId(document.getString("_id"));
    chunk.setData(document.getString("data"));
    chunk.setOrder(document.getInteger("order"));
    return chunk;
  }
}
