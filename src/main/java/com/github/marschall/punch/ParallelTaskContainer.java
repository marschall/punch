package com.github.marschall.punch;

import java.util.Collection;

public class ParallelTaskContainer extends CompositeTask {

  public ParallelTaskContainer(Collection<RecoverableTask> tasks) {
    super(tasks);
  }

  @Override
  protected void safeCompute() {
    this.ensureTaskPathSet();
    invokeAll(this.tasks);
  }

}
