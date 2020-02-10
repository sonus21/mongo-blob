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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.github.sonus21.mblob.utils.ObjectFactory;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import test.blob.mongo.entity.Notification;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class BsonMarshallerTest {
  private MappingMongoConverter mappingMongoConverter = mock(MappingMongoConverter.class);
  private BsonMarshaller bsonMarshaller = new BsonMarshaller(mappingMongoConverter);

  @Test
  public void write() {
    Notification notification = ObjectFactory.createBlobObject(false, 1);
    Document d = new Document();
    d.put("foo", "bar");
    doAnswer(
            invocation -> {
              ((Document) invocation.getArguments()[1]).put("foo", "bar");
              return null;
            })
        .when(mappingMongoConverter)
        .write(eq(notification), any(Document.class));
    assertEquals(d.toJson(), bsonMarshaller.write(notification));
  }

  @Test
  public void read() {
    Notification notification = ObjectFactory.createBlobObject(false, 1);
    Document d = new Document();
    d.put("foo", "bar");
    doReturn(notification).when(mappingMongoConverter).read(notification.getClass(), d);
    assertEquals(notification, bsonMarshaller.read(notification, d.toJson()));
  }
}
