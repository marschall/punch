package com.github.marschall.punch;

import java.util.Collection;
import java.util.concurrent.ForkJoinTask;

public class ParallelTaskContainer extends CompositeTask {

  public ParallelTaskContainer(Collection<RecoverableTask> tasks) {
    super(tasks);
  }

  @Override
  protected void compute() {
    this.ensureTaskPathSet();
    invokeAll(this.tasks);
  }

}
