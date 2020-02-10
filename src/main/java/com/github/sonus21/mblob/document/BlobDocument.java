package com.github.sonus21.mblob.document;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = "chunkIds")
public abstract class BlobDocument {
  @Field("_chunkIds")
  protected Collection<String> chunkIds;
}
