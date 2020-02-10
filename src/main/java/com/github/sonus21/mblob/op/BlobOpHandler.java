package com.github.sonus21.mblob.op;

import static com.github.sonus21.mblob.aspect.AspectUtil.collectArguments;
import static com.github.sonus21.mblob.aspect.AspectUtil.getRepositoryMetaData;
import static com.github.sonus21.mblob.aspect.AspectUtil.typeCastReturnType;

import com.github.sonus21.mblob.chunk.Chunk;
import com.github.sonus21.mblob.chunk.ChunkManager;
import com.github.sonus21.mblob.document.BlobDocument;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.CollectionUtils;

/**
 * This handles all the crud related operations, those are forwarded from {@link
 * com.github.sonus21.mblob.aspect.BlobAspectHandler}, currently only save, saveAll, findById,
 * findAllById, findAll, delete, deleteById are handled.
 */
public class BlobOpHandler {
  private final ChunkManager chunkManager;

  enum OpType {
    DELETE,
    FIND,
    SAVE
  }

  public BlobOpHandler(ChunkManager chunkManager) {
    this.chunkManager = chunkManager;
  }

  public Object onDeleteEntity(ProceedingJoinPoint pjp) throws Throwable {
    return handleOperation(pjp, OpType.DELETE);
  }

  public Object onSaveEntity(ProceedingJoinPoint pjp) throws Throwable {
    return handleOperation(pjp, OpType.SAVE);
  }

  public Object onFindBy(ProceedingJoinPoint pjp) throws Throwable {
    return handleOperation(pjp, OpType.FIND);
  }

  private Object handleOperation(ProceedingJoinPoint pjp, OpType opType) throws Throwable {
    Optional<RepositoryMetadata> metadataOptional = getRepositoryMetaData(pjp);
    if (metadataOptional.isPresent()) {
      RepositoryMetadata metadata = metadataOptional.get();
      if (isBlobType(metadata)) {
        switch (opType) {
          case SAVE:
            return handleSave(pjp);
          case DELETE:
            return handleDelete(pjp, metadata);
          case FIND:
            return handleFind(pjp);
        }
      }
    }
    return pjp.proceed();
  }

  private Map<Integer, Map<Field, Object>> saveRecords(List<Object> args) {
    Map<Integer, Map<Field, Object>> modifiedRecordDetail = new HashMap<>();
    for (int i = 0; i < args.size(); i++) {
      Object o = args.get(i);
      if (o instanceof BlobDocument) {
        Collection<Chunk> chunks = chunkManager.createChunks((BlobDocument) o);
        if (!chunks.isEmpty()) {
          Map<Field, Object> modifiedFields = ReflectionUtility.getObjectModificationDetails(o);
          // error case
          if (modifiedFields == null) {
            ReflectionUtility.reverseObjectModification(modifiedRecordDetail, args, true);
            return new HashMap<>();
          }
          modifiedRecordDetail.put(i, modifiedFields);
          ((BlobDocument) o)
              .setChunkIds(chunks.stream().map(Chunk::getId).collect(Collectors.toList()));
        }
      }
    }
    return modifiedRecordDetail;
  }

  private Object handleFind(ProceedingJoinPoint pjp) throws Throwable {
    Object result = pjp.proceed();
    List<Object> entities = new ArrayList<>();
    boolean findById = false;
    List<Object> args = collectArguments(pjp);
    if (args.size() == 0) {
      // findAll
      for (Object o : (Iterable) result) {
        entities.add(o);
      }
    } else {
      // findById
      if (result instanceof Optional) {
        Optional opResult = (Optional) result;
        if (opResult.isPresent()) {
          entities.add(opResult.get());
          findById = true;
        } else {
          return result;
        }
        // findAllById
      } else if (result instanceof Iterable) {
        for (Object o : (Iterable) result) {
          entities.add(o);
        }
      }
    }
    entities = chunkManager.getUpdatedEntities(entities);
    if (findById) {
      return Optional.of(entities.get(0));
    }
    return entities;
  }

  private Object handleSave(ProceedingJoinPoint pjp) throws Throwable {
    List<Object> args = collectArguments(pjp);
    ReflectionUtility.maybeSetId(args);
    Map<Integer, Map<Field, Object>> objectModificationDetail = saveRecords(args);
    pjp.proceed();
    ReflectionUtility.reverseObjectModification(objectModificationDetail, args, false);
    return typeCastReturnType(pjp, args);
  }

  private Object handleDelete(ProceedingJoinPoint pjp, RepositoryMetadata metadata)
      throws Throwable {
    List<Object> args = collectArguments(pjp);
    if (args.isEmpty()) {
      // deleteAll
      return pjp.proceed();
    }
    if (isIdClass(metadata, args.get(0))) {
      // deleteById
      Optional<?> o = ((CrudRepository) pjp.getTarget()).findById(args.get(0));
      o.ifPresent(value -> deleteObjects(Collections.singletonList(value)));
    } else {
      // delete or deleteAll
      deleteObjects(args);
    }
    return pjp.proceed();
  }

  private void deleteObjects(List<Object> args) {
    List<String> chunkIds = new ArrayList<>();
    for (Object o : args) {
      if (o instanceof BlobDocument) {
        Collection<String> ids = ((BlobDocument) o).getChunkIds();
        if (!CollectionUtils.isEmpty(ids)) {
          chunkIds.addAll(ids);
        }
      }
    }
    chunkManager.deleteChunks(chunkIds);
  }

  private boolean isBlobType(RepositoryMetadata metadata) {
    return BlobDocument.class.isAssignableFrom(metadata.getDomainType());
  }

  private boolean isIdClass(RepositoryMetadata metadata, Object o) {
    return metadata.getIdType().isAssignableFrom(o.getClass());
  }
}
