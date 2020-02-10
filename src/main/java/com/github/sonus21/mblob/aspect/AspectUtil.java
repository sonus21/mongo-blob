package com.github.sonus21.mblob.aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

public abstract class AspectUtil {
  public static List<Object> collectArguments(ProceedingJoinPoint proceedingJoinPoint) {
    Object[] args = proceedingJoinPoint.getArgs();
    List<Object> params = new ArrayList<>();
    if (args.length > 0) {
      if (args[0] instanceof Iterable) {
        for (Object o : (Iterable) args[0]) {
          params.add(o);
        }
      } else {
        params.add(args[0]);
      }
    }
    return params;
  }

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

  public static Optional<RepositoryMetadata> getRepositoryMetaData(JoinPoint pjp) {
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
