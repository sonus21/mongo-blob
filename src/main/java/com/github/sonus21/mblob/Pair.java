package com.github.sonus21.mblob;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Pair<E, V> {
  private E first;
  private V second;
}
