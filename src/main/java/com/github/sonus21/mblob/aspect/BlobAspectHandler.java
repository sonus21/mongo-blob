package com.github.sonus21.mblob.aspect;

import com.github.sonus21.mblob.op.BlobOpHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class BlobAspectHandler {
  private final BlobOpHandler blobOpHandler;

  public BlobAspectHandler(BlobOpHandler blobOpHandler) {
    this.blobOpHandler = blobOpHandler;
  }

  @Around(
      "execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onSave(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onSaveEntity(pjp);
  }

  @Around(
      "execution(public * saveAll(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onSaveAll(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onSaveEntity(pjp);
  }

  @Around(
      "execution(public * findById(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onFindById(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onFindBy(pjp);
  }

  @Around(
      "execution(public * findAll(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onFindAll(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onFindBy(pjp);
  }

  @Around(
      "execution(public * findAllById(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onFindAllById(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onFindBy(pjp);
  }

  @Around(
      "execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onDelete(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onDeleteEntity(pjp);
  }

  @Around(
      "execution(public * deleteById(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onDeleteById(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onDeleteEntity(pjp);
  }

  @Around(
      "execution(public * deleteAll(..)) && this(org.springframework.data.repository.CrudRepository)")
  public Object onDeleteAll(ProceedingJoinPoint pjp) throws Throwable {
    return blobOpHandler.onDeleteEntity(pjp);
  }
}
