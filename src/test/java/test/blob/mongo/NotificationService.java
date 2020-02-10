package test.blob.mongo;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.blob.mongo.entity.Notification;
import test.blob.mongo.repository.NotificationRepository;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationService {
  @NonNull private NotificationRepository notificationRepository;

  public Notification create(int objectCount) {
    Notification notification = Notification.create(objectCount);
    notificationRepository.save(notification);
    return notification;
  }
}
