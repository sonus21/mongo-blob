package test.blob.mongo;

import static org.junit.Assert.assertTrue;

import com.github.sonus21.mblob.utils.ObjectFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
public class BlobSaveTest extends BaseTest {
  @Test
  public void saveAll() {
    notificationRepository.deleteAll();
    List<Notification> notifications = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      notifications.add(ObjectFactory.createBlobObject(false, objectCount));
    }

    Iterable<Notification> iterable = notificationRepository.saveAll(notifications);
    for (Notification n : iterable) {
      boolean found = false;
      for (Notification notification : notifications) {
        if (notification.equals(n)) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }

    iterable = notificationRepository.findAll();
    for (Notification n : iterable) {
      boolean found = false;
      for (Notification notification : notifications) {
        if (notification.equals(n)) {
          found = true;
          break;
        }
      }
      assertTrue(found);
    }
  }
}
