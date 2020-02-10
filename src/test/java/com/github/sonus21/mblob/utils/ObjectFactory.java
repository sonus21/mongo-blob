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

package com.github.sonus21.mblob.utils;

import com.github.sonus21.mblob.chunk.Chunk;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.SerializationUtils;
import test.blob.mongo.dto.User;
import test.blob.mongo.entity.Notification;

@UtilityClass
public class ObjectFactory {
  private static Random random = new Random(System.currentTimeMillis());

  public static boolean nonBlobObjectPermitted() {
    return random.nextBoolean();
  }

  public static TestObject createNonBlobObject() {
    return new TestObject();
  }

  public static Notification createBlobObject(boolean setChunkId, int size) {
    Notification notification = Notification.create(size);
    if (setChunkId) {
      List<String> ids = new ArrayList<>();
      for (int i = 0; i < random.nextInt(10); i++) {
        ids.add(UUID.randomUUID().toString());
      }
      notification.setChunkIds(ids);
    }
    return notification;
  }

  public static List<Chunk> createChunks(Notification notification) {
    List<Chunk> chunks = new ArrayList<>();
    for (String id : notification.getChunkIds()) {
      Chunk chunk = new Chunk();
      chunk.setId(id);
      chunks.add(chunk);
    }
    return chunks;
  }

  public static List<Notification> getUpdatedBlobs(List<Notification> notifications) {
    List<Notification> notificationList = new ArrayList<>();
    for (Notification notification : notifications) {
      Notification newObj = SerializationUtils.clone(notification);
      int c = random.nextInt(10);
      List<User> users = new ArrayList<>();
      for (int i = 0; i < c; i++) {
        users.add(User.getInstance());
      }
      if (c > 0) {
        newObj.setUsers(users);
      }
      notificationList.add(newObj);
    }
    return notificationList;
  }
}
