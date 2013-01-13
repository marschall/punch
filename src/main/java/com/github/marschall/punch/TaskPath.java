package com.github.marschall.punch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TaskPath {
  
  private final List<Integer> elements;

  private TaskPath(List<Integer> elements) {
    this.elements = elements;
  }
  
  public TaskPath add(int i) {
    if (this.elements.isEmpty()) {
      return new TaskPath(Collections.singletonList(i));
    } else {
      List<Integer> newElements = new ArrayList<>(this.elements.size() + 1);
      newElements.addAll(this.elements);
      newElements.add(i);
      return new TaskPath(newElements);
    }
  }
  
  public static TaskPath root() {
    return new TaskPath(Collections.<Integer>emptyList());
  }

}
