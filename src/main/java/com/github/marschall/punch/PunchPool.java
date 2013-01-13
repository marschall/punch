package com.github.marschall.punch;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;


class PunchPool extends ForkJoinPool {

  PunchPool(TaskStateListener listener) {
    super(Runtime.getRuntime().availableProcessors(), new PunchWorkerThreadFactory(listener), null, false);
  }


  static class PunchWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

    private final TaskStateListener listener;

    public PunchWorkerThreadFactory(TaskStateListener listener) {
      this.listener = listener;
    }

    @Override
    public PunchWorkerThread newThread(ForkJoinPool pool) {
      return new PunchWorkerThread(pool, this.listener);
    }

  }

  static class PunchWorkerThread extends ForkJoinWorkerThread {

    final TaskStateListener listener;

    protected PunchWorkerThread(ForkJoinPool pool, TaskStateListener listener) {
      super(pool);
      this.listener = listener;
    }

  }
}

