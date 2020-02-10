/*
 * Copyright 2020 Sonu Kumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sonus21.mblob.aspect;

import com.github.sonus21.mblob.op.BlobOpHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * AOP handler that catches all the method calls going to {@link
 * org.springframework.data.repository.CrudRepository}. This handles all CRUD operations. Aspect
 * handler forwards the intercepted method requests to the {@link BlobOpHandler} class's methods.
 */
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
