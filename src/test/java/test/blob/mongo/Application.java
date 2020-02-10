package test.blob.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableMongoRepositories("test.blob.mongo.repository")
@PropertySource("classpath:application.properties")
@EnableWebMvc
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
