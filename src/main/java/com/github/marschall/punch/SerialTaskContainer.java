package com.github.marschall.punch;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class SerialTaskContainer extends RecursiveAction {

  private Collection<ForkJoinTask<?>> tasks;

  public SerialTaskContainer(Collection<ForkJoinTask<?>> tasks) {
    this.tasks = tasks;
  }

  @Override
  protected void compute() {
    for (ForkJoinTask<?> task : this.tasks) {
      task.invoke();
    }
  }

}
