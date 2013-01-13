package com.github.marschall.punch;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class SerialTaskContainer extends CompositeTask {

  public SerialTaskContainer(Collection<ForkJoinTask<?>> tasks) {
    super(tasks);
  }

  @Override
  protected void compute() {
    for (ForkJoinTask<?> task : this.tasks) {
      task.invoke();
    }
  }

}
