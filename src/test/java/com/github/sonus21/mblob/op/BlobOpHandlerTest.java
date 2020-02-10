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

package com.github.sonus21.mblob.op;

import static com.github.sonus21.mblob.utils.ObjectFactory.getUpdatedBlobs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.sonus21.mblob.aspect.AspectUtil;
import com.github.sonus21.mblob.chunk.ChunkManager;
import com.github.sonus21.mblob.utils.ObjectFactory;
import com.github.sonus21.mblob.utils.TestObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import test.blob.mongo.entity.Notification;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
    fullyQualifiedNames = {
      "com.github.sonus21.mblob.op.ReflectionUtility",
      "com.github.sonus21.mblob.aspect.AspectUtil"
    })
public class BlobOpHandlerTest {
  private ChunkManager chunkManager = mock(ChunkManager.class);
  private BlobOpHandler blobOpHandler = new BlobOpHandler(chunkManager);
  private MethodInvocationProceedingJoinPoint pjp = mock(MethodInvocationProceedingJoinPoint.class);
  private RepositoryMetadata metadata = mock(RepositoryMetadata.class);
  private CrudRepository crudRepository = mock(CrudRepository.class);

  @Test
  public void onDeleteAll() throws Throwable {
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.empty());
    doReturn(new Object[] {}).when(pjp).getArgs();
    blobOpHandler.onDeleteEntity(pjp);
    verify(chunkManager, times(0)).deleteChunks(anyCollection());
  }

  @Test
  public void onDeleteNonBlob() throws Throwable {
    doReturn(new Object[] {ObjectFactory.createNonBlobObject()}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.empty());
    blobOpHandler.onDeleteEntity(pjp);
    verify(chunkManager, times(0)).deleteChunks(anyCollection());
  }

  @Test
  public void onDeleteEntityBlob() throws Throwable {
    Notification notification = ObjectFactory.createBlobObject(true, 1);
    doReturn(new Object[] {notification}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    blobOpHandler.onDeleteEntity(pjp);
    verify(chunkManager, times(1)).deleteChunks(notification.getChunkIds());
  }

  @Test
  public void onDeleteMultipleEntities() throws Throwable {
    List<Object> notifications = new ArrayList<>();
    List<String> ids = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Notification notification = ObjectFactory.createBlobObject(true, 1);
      notifications.add(notification);
      ids.addAll(notification.getChunkIds());
      if (ObjectFactory.nonBlobObjectPermitted()) {
        notifications.add(ObjectFactory.createNonBlobObject());
      }
    }
    doReturn(new Object[] {notifications}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    blobOpHandler.onDeleteEntity(pjp);
    verify(chunkManager, times(1)).deleteChunks(ids);
  }

  @Test
  public void onDeleteById() throws Throwable {
    String id = UUID.randomUUID().toString();
    doReturn(new Object[] {id}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    doReturn(crudRepository).when(pjp).getTarget();
    doReturn(Optional.empty()).when(crudRepository).findById(id);
    blobOpHandler.onDeleteEntity(pjp);
    verify(chunkManager, times(0)).deleteChunks(anyCollection());
  }

  @Test
  public void onDeleteByIdObjectFound() throws Throwable {
    String id = UUID.randomUUID().toString();
    Notification notification = ObjectFactory.createBlobObject(true, 10);
    doReturn(new Object[] {id}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    doReturn(crudRepository).when(pjp).getTarget();
    doReturn(Optional.of(notification)).when(crudRepository).findById(id);
    blobOpHandler.onDeleteEntity(pjp);
    verify(chunkManager, times(1)).deleteChunks(notification.getChunkIds());
  }

  @Test
  public void onSaveNonBlobEntity() throws Throwable {
    doReturn(new Object[] {ObjectFactory.createNonBlobObject()}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.empty());
    blobOpHandler.onSaveEntity(pjp);
    verify(chunkManager, times(0)).createChunks(any());
  }

  @Test
  public void onSaveBlobEntity() throws Throwable {
    Notification notification = ObjectFactory.createBlobObject(true, 10);
    doReturn(new Object[] {notification}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(ObjectFactory.createChunks(notification))
        .when(chunkManager)
        .createChunks(notification);
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    Notification newNotification = (Notification) blobOpHandler.onSaveEntity(pjp);
    assertEquals(notification.getChunkIds(), newNotification.getChunkIds());
    assertNotNull(notification.getUsers());
    verify(pjp, times(1)).proceed();
  }

  @Test
  public void onSaveMultipleEntities() throws Throwable {
    List<Notification> notifications = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      notifications.add(ObjectFactory.createBlobObject(true, 10));
    }
    doReturn(new Object[] {notifications}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();

    doReturn(ObjectFactory.createChunks(notifications.get(0)))
        .when(chunkManager)
        .createChunks(notifications.get(0));
    doReturn(ObjectFactory.createChunks(notifications.get(1)))
        .when(chunkManager)
        .createChunks(notifications.get(1));

    List<Notification> newNotifications = (List<Notification>) blobOpHandler.onSaveEntity(pjp);
    assertEquals(notifications.get(0).getChunkIds(), newNotifications.get(0).getChunkIds());
    assertEquals(notifications.get(1).getChunkIds(), newNotifications.get(1).getChunkIds());
    assertNotNull(notifications.get(0).getUsers());
    assertNotNull(notifications.get(1).getUsers());
    verify(pjp, times(1)).proceed();
  }

  @Test
  public void onFindByNonBlob() throws Throwable {
    doReturn(new Object[] {ObjectFactory.createNonBlobObject()}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(TestObject.class).when(metadata).getDomainType();
    blobOpHandler.onFindBy(pjp);
    verify(chunkManager, times(0)).getUpdatedEntities(anyList());
    verify(pjp, times(1)).proceed();
  }

  @Test
  public void onFindByIdNotFound() throws Throwable {
    String id = UUID.randomUUID().toString();
    doReturn(new Object[] {id}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    doReturn(Optional.empty()).when(pjp).proceed();
    assertEquals(Optional.empty(), blobOpHandler.onFindBy(pjp));
    verify(chunkManager, times(0)).getUpdatedEntities(anyList());
  }

  @Test
  public void onFindById() throws Throwable {
    Notification notification = ObjectFactory.createBlobObject(false, 0);
    String id = UUID.randomUUID().toString();
    doReturn(new Object[] {id}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    doReturn(Optional.of(notification)).when(pjp).proceed();
    Notification updateNotification =
        getUpdatedBlobs(Collections.singletonList(notification)).get(0);
    doReturn(Collections.singletonList(updateNotification))
        .when(chunkManager)
        .getUpdatedEntities(Collections.singletonList(notification));
    assertEquals(Optional.of(updateNotification), blobOpHandler.onFindBy(pjp));
  }

  @Test
  public void onFindAllById() throws Throwable {
    List<Notification> notifications = new ArrayList<>();
    List<Object> objNotifications = new ArrayList<>();
    List<String> ids = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Notification notification = ObjectFactory.createBlobObject(false, 0);
      notifications.add(notification);
      objNotifications.add(notification);
      ids.add(notification.getId());
    }
    List<Notification> updateNotifications = getUpdatedBlobs(notifications);

    doReturn(new Object[] {ids}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    doReturn(notifications).when(pjp).proceed();
    doReturn(updateNotifications).when(chunkManager).getUpdatedEntities(objNotifications);
    assertEquals(updateNotifications, blobOpHandler.onFindBy(pjp));
  }

  @Test
  public void onFindAll() throws Throwable {
    List<Notification> notifications = new ArrayList<>();
    List<Object> objNotifications = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Notification notification = ObjectFactory.createBlobObject(false, 0);
      notifications.add(notification);
      objNotifications.add(notification);
    }
    List<Notification> updateNotifications = getUpdatedBlobs(notifications);

    doReturn(new Object[] {}).when(pjp).getArgs();
    PowerMockito.stub(PowerMockito.method(AspectUtil.class, "getRepositoryMetaData"))
        .toReturn(Optional.of(metadata));
    doReturn(Notification.class).when(metadata).getDomainType();
    doReturn(String.class).when(metadata).getIdType();
    doReturn(notifications).when(pjp).proceed();
    doReturn(updateNotifications).when(chunkManager).getUpdatedEntities(objNotifications);
    assertEquals(updateNotifications, blobOpHandler.onFindBy(pjp));
  }
}
