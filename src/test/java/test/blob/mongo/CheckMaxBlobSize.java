package test.blob.mongo;

import com.github.sonus21.mblob.utils.ObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonMaximumSizeExceededException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import test.blob.mongo.entity.Notification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestPropertySource(properties = {"mongo.blob.enabled=false"})
@Slf4j
public class CheckMaxBlobSize extends BaseTest {

  @Test(expected = BsonMaximumSizeExceededException.class)
  public void checkBlobSize() {
    notificationRepository.deleteAll();
    Notification notification = ObjectFactory.createBlobObject(false, objectCount);
    notificationRepository.save(notification);
  }
}
