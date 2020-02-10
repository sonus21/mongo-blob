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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

/**
 * AspectUtil helps in handling AOP, it has some utility methods that helps in type casting the
 * return type, parameter collections.
 */
@UtilityClass
public class AspectUtil {

  /**
   * Given a join point, this method collects all the parameter of the target method in a list. It
   * flattens the parameter as well, for example if one of the argument is the subtype of {@link
   * Iterable}, then individual element would be collected in the returned list.
   *
   * @param proceedingJoinPoint a object of proceeding joint point.
   * @return list of objects.
   */
  public static List<Object> collectArguments(ProceedingJoinPoint proceedingJoinPoint) {
    Object[] args = proceedingJoinPoint.getArgs();
    List<Object> params = new ArrayList<>();
    if (args != null) {
      if (args.length > 0) {
        if (args[0] instanceof Iterable) {
          for (Object o : (Iterable) args[0]) {
            params.add(o);
          }
        } else {
          params.add(args[0]);
        }
      }
    }
    return params;
  }

  /**
   * This method converts the return values of processing joint point handler based on the
   * proecessing join point arguments.
   *
   * @param pjp a proceeding joing point object
   * @param returnValues list of objects that needs to be type casted
   * @return type casted object
   */
  public static Object typeCastReturnType(ProceedingJoinPoint pjp, List<Object> returnValues) {
    Object[] args = pjp.getArgs();
    if (args.length > 0) {
      if (args[0] instanceof Iterable) {
        return new ArrayList<>(returnValues);
      } else {
        return returnValues.get(0);
      }
    }
    return null;
  }

  /**
   * Find repository metadata based on the joint point, each joint point has some target from the
   * target we try to find the actual metadata.
   *
   * @param pjp joint point
   * @return optional repository metadata
   */
  public static Optional<RepositoryMetadata>  getRepositoryMetaData(JoinPoint pjp) {
    for (Class i : pjp.getTarget().getClass().getInterfaces()) {
      try {
        RepositoryMetadata metadata = DefaultRepositoryMetadata.getMetadata(i);
        return Optional.of(metadata);
      } catch (IllegalArgumentException e) {
      }
    }
    return Optional.empty();
  }
}
