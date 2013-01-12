package com.github.marschall.punch;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ParallelTaskContainer extends RecursiveAction {

  private final Collection<ForkJoinTask<?>> tasks;

  public ParallelTaskContainer(Collection<ForkJoinTask<?>> tasks) {
    this.tasks = tasks;
  }

  @Override
  protected void compute() {
    invokeAll(this.tasks);
  }

}
