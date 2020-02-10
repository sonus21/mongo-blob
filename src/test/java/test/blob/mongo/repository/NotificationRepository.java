package test.blob.mongo.repository;

import org.springframework.data.repository.CrudRepository;
import test.blob.mongo.entity.Notification;

public interface NotificationRepository extends CrudRepository<Notification, String> {}
