package test.blob.mongo.entity;

import com.github.sonus21.mblob.document.BlobDocument;
import com.github.sonus21.mblob.document.BlobField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import test.blob.mongo.dto.User;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Notification extends BlobDocument {
  private @Id String id;
  @BlobField private Collection<User> users;

  public static Notification create(int objectCount) {
    List<User> users = new ArrayList<>();
    for (int i = 0; i < objectCount; i++) {
      users.add(User.getInstance());
    }
    Notification notification = new Notification();
    notification.setUsers(users);
    notification.setId(UUID.randomUUID().toString());
    return notification;
  }
}
