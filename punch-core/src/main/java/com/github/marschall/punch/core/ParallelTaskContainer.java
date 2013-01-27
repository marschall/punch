package com.github.marschall.punch.core;

import java.util.Collection;

public class ParallelTaskContainer extends CompositeTask {

  public ParallelTaskContainer(Collection<RecoverableTask> tasks) {
    super(tasks);
  }

  @Override
  protected void safeCompute() {
    invokeAll(this.tasks);
  }

}
