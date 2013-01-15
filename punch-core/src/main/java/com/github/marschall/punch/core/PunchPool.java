package com.github.marschall.punch.core;

import java.util.concurrent.ForkJoinPool;

public final class PunchPool extends ForkJoinPool {

  final TaskStateListener listener;
  final RecoveryService recoveryService;

  public PunchPool(TaskStateListener listener, RecoveryService recoveryService) {
    if (listener == null) {
      throw new NullPointerException("A task state listener is mandatory.");
    }

    if (recoveryService == null) {
      throw new NullPointerException("A recovery service is mandatory.");
    }

    this.listener = listener;
    this.recoveryService = recoveryService;
  }
}

