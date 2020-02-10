package com.github.sonus21.mblob.reflect;

import com.github.sonus21.mblob.document.BlobDocument;
import com.github.sonus21.mblob.document.BlobField;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;

@UtilityClass
public class ReflectionUtility {
  public static void maybeSetId(List<Object> args) {
    Field idField = null;
    for (Field field : args.get(0).getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Id.class)) {
        if (field.getType().isAssignableFrom(String.class)) {
          idField = field;
        }
        break;
      }
    }
    if (idField != null) {
      for (Object o : args) {
        try {
          Object val = FieldUtils.readField(idField, o, true);
          if (val == null) {
            FieldUtils.writeField(idField, o, UUID.randomUUID().toString(), true);
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void reverseObjectModification(
      Map<Integer, Map<Field, Object>> modifiedRecordDetail,
      List<Object> objects,
      boolean resetId) {
    for (Entry<Integer, Map<Field, Object>> e : modifiedRecordDetail.entrySet()) {
      Object o = objects.get(e.getKey());
      for (Entry<Field, Object> entry : e.getValue().entrySet()) {
        try {
          FieldUtils.writeField(entry.getKey(), o, entry.getValue(), true);
        } catch (IllegalAccessException ex) {
          ex.printStackTrace();
        }
      }
      if (resetId) {
        ((BlobDocument) o).setChunkIds(null);
      }
    }
  }

  public static Map<Field, Object> getObjectModificationDetails(Object o) {
    Map<Field, Object> modifiedFields = new HashMap<>();
    for (Field field : o.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(BlobField.class)) {
        try {
          Object val = FieldUtils.readField(field, o, true);
          modifiedFields.put(field, val);
          FieldUtils.writeField(field, o, null, true);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
          return null;
        }
      }
    }
    return modifiedFields;
  }
}
