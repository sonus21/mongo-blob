package test.blob.mongo;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import test.blob.mongo.entity.Notification;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Controller {
  @NonNull private NotificationService notificationService;

  @ResponseBody
  @GetMapping
  public Notification create(@RequestParam Integer count) {
    return notificationService.create(count);
  }
}
