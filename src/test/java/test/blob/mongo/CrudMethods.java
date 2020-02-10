package test.blob.mongo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import test.blob.mongo.entity.Notification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@Slf4j
public class CrudMethods extends BaseTest {

  @Test
  public void findTest() {
    notificationRepository.deleteAll();
    Notification notification = notificationService.create(objectCount);
    Iterable<Notification> findAll = notificationRepository.findAll();
    List<Notification> allInList = new ArrayList<>();
    findAll.forEach(allInList::add);

    Iterable<Notification> findAllById =
        notificationRepository.findAllById(Collections.singletonList(notification.getId()));
    List<Notification> allById = new ArrayList<>();
    findAllById.forEach(allById::add);

    Notification n = notificationRepository.findById(notification.getId()).get();

    assertEquals("Not equal", allInList, allById);
    assertEquals("Not equal", allInList, Collections.singletonList(n));
    assertEquals("Not equal", notification, n);
  }

  @Test
  public void deleteEntity() {
    notificationRepository.deleteAll();
    Notification notification = notificationService.create(objectCount);
    notificationRepository.delete(notification);
    Collection<String> chunkIds = notification.getChunkIds();
    List<Document> documentList = readChunkDocuments(chunkIds);
    assertEquals(0, documentList.size());
  }

  @Test
  public void deleteById() {
    notificationRepository.deleteAll();
    Notification notification = notificationService.create(objectCount);
    notificationRepository.deleteById(notification.getId());
    Collection<String> chunkIds = notification.getChunkIds();
    List<Document> documentList = readChunkDocuments(chunkIds);
    assertEquals(0, documentList.size());
  }

  @Test
  public void deleteAll() {
    notificationRepository.deleteAll();
    Notification notification = notificationService.create(objectCount);
    notificationRepository.deleteAll();
    Collection<String> chunkIds = notification.getChunkIds();
    List<Document> documentList = readChunkDocuments(chunkIds);
    assertEquals(2, documentList.size());
  }

  @Test
  public void deleteAllEntities() {
    notificationRepository.deleteAll();
    Notification notification = notificationService.create(objectCount);
    notificationRepository.deleteAll(Collections.singleton(notification));
    Collection<String> chunkIds = notification.getChunkIds();
    List<Document> documentList = readChunkDocuments(chunkIds);
    assertEquals(0, documentList.size());
  }
}
