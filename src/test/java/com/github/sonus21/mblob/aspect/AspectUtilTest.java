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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.sonus21.mblob.utils.MethodInvocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

public class AspectUtilTest {

  @Test
  public void collectArgumentsNull() {
    ProceedingJoinPoint point = new MethodInvocationProceedingJoinPoint(new MethodInvocation(null));
    assertEquals(0, AspectUtil.collectArguments(point).size());
  }

  @Test
  public void collectArgumentsSingleObject() {
    ProceedingJoinPoint point =
        new MethodInvocationProceedingJoinPoint(new MethodInvocation(new Object[] {new Object()}));
    assertEquals(1, AspectUtil.collectArguments(point).size());
  }

  @Test
  public void collectArgumentsIterable() {
    List<Object> l = new ArrayList<>();
    l.add("1234");
    l.add("12345");
    ProceedingJoinPoint point =
        new MethodInvocationProceedingJoinPoint(new MethodInvocation(new Object[] {l}));
    assertEquals(2, AspectUtil.collectArguments(point).size());
    assertEquals(l, AspectUtil.collectArguments(point));
  }

  @Test
  public void typeCastReturnTypeNull() {
    ProceedingJoinPoint point =
        new MethodInvocationProceedingJoinPoint(new MethodInvocation(new Object[] {}));
    List<Object> val = Collections.singletonList("1234");
    assertNull(AspectUtil.typeCastReturnType(point, val));
  }

  @Test
  public void typeCastReturnTypeSingleValue() {
    ProceedingJoinPoint point =
        new MethodInvocationProceedingJoinPoint(new MethodInvocation(new Object[] {new Object()}));
    List<Object> val = Collections.singletonList("1234");

    assertEquals("1234", AspectUtil.typeCastReturnType(point, val));
  }

  @Test
  public void typeCastReturnTypeIterable() {
    Set<Object> pjpData = new HashSet<>();
    pjpData.add("1234");
    pjpData.add("3456");
    ProceedingJoinPoint point =
        new MethodInvocationProceedingJoinPoint(new MethodInvocation(new Object[] {pjpData}));
    List<Object> val = new ArrayList<>();
    val.add("1234");
    val.add("3456");

    assertEquals(val, AspectUtil.typeCastReturnType(point, val));
  }
}
