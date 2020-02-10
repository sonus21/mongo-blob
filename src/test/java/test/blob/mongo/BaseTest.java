package test.blob.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bson.Document;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import test.blob.mongo.repository.NotificationRepository;

public class BaseTest {
  @Autowired NotificationService notificationService;
  @Autowired NotificationRepository notificationRepository;
  @Autowired MongoProperties mongoProperties;
  @Autowired MongoClient mongoClient;
  MongoDatabase mongoDatabase;

  @Value("${notification.users.count}")
  int objectCount;

  @Before
  public void init() {
    this.mongoDatabase = mongoClient.getDatabase(mongoProperties.getDatabase());
  }

  List<Document> readChunkDocuments(Collection<String> chunkIds) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").in(chunkIds));
    Document queryObj = new Document();
    queryObj.putAll(query.getQueryObject());
    FindIterable<Document> documents = mongoDatabase.getCollection("blob_chunk").find(queryObj);
    List<Document> documentList = new ArrayList<>();
    for (Document d : documents) {
      documentList.add(d);
    }
    return documentList;
  }
}
