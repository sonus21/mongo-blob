package test.blob.mongo.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
  private String name;
  private String email;

  public static User getInstance() {
    return new User(
        RandomStringUtils.randomAlphabetic(1000), RandomStringUtils.randomAlphabetic(1000));
  }
}
