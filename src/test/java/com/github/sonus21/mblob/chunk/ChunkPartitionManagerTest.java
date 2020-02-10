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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.github.sonus21.mblob.utils.ObjectFactory;
import java.util.Collections;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import test.blob.mongo.entity.Notification;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ChunkPartitionManagerTest {
  private int blobSize = 100;
  private BsonMarshaller bsonMarshaller = mock(BsonMarshaller.class);
  private ChunkPartitionManager chunkPartitionManager =
      new ChunkPartitionManager(blobSize, bsonMarshaller);

  @Test
  public void chunksBelowThreshold() {
    Notification notification = ObjectFactory.createBlobObject(false, 1);
    doReturn("bsonMarshaller").when(bsonMarshaller).write(notification);
    assertEquals(Collections.emptyList(), chunkPartitionManager.chunks(notification));
  }

  @Test
  public void chunksEqualToThreshold() {
    Notification notification = ObjectFactory.createBlobObject(false, 1);
    doReturn(RandomStringUtils.random(blobSize)).when(bsonMarshaller).write(notification);
    assertEquals(Collections.emptyList(), chunkPartitionManager.chunks(notification));
  }

  @Test
  public void divisibleToThreshold() {
    Notification notification = ObjectFactory.createBlobObject(false, 1);
    doReturn(RandomStringUtils.random(blobSize * 10)).when(bsonMarshaller).write(notification);
    assertEquals(10, chunkPartitionManager.chunks(notification).size());
  }

  @Test
  public void nonDivisibleToThreshold() {
    Notification notification = ObjectFactory.createBlobObject(false, 1);
    doReturn(RandomStringUtils.random(blobSize * 10 + blobSize / 2))
        .when(bsonMarshaller)
        .write(notification);
    assertEquals(11, chunkPartitionManager.chunks(notification).size());
  }
}
