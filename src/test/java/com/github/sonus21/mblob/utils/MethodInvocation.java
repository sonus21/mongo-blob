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

package com.github.sonus21.mblob.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.springframework.aop.ProxyMethodInvocation;

public class MethodInvocation implements ProxyMethodInvocation {
  private Object[] args = null;

  public MethodInvocation(Object[] args) {
    this.args = args;
  }

  @Override
  public Object getProxy() {
    return null;
  }

  @Override
  public org.aopalliance.intercept.MethodInvocation invocableClone() {
    return null;
  }

  @Override
  public org.aopalliance.intercept.MethodInvocation invocableClone(Object... arguments) {
    return null;
  }

  @Override
  public void setArguments(Object... arguments) {}

  @Override
  public void setUserAttribute(String key, Object value) {}

  @Override
  public Object getUserAttribute(String key) {
    return null;
  }

  @Override
  public Method getMethod() {
    return null;
  }

  @Override
  public Object[] getArguments() {
    if (args == null) {
      return new Object[] {};
    }
    return args;
  }

  @Override
  public Object proceed() throws Throwable {
    return null;
  }

  @Override
  public Object getThis() {
    return null;
  }

  @Override
  public AccessibleObject getStaticPart() {
    return null;
  }
}
