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

package com.github.sonus21.mblob.document;

import java.io.Serializable;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Any document that requires blob handling must extend this class. This is like a marker to
 * document, for blob handling. Field level blob should be enabled using {@link BlobField}. <br>
 * Usage example
 *
 * <pre>
 * public class Notification extends BlobDocument {
 *    @BlobField Collection<UserDetail> userDetails;
 * }
 * </pre>
 *
 * {@code} public class Notification extends BlobDocument { @BlobField Collection<UserDetail>
 * userDetails; }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = "chunkIds")
public abstract class BlobDocument implements Serializable {
  @Field("_chunkIds")
  protected Collection<String> chunkIds;
}
