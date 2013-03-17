package com.github.marschall.punch.core;

public interface RecoveryService {

  boolean isFinished(TaskPath path);

  int newTaskGroup();

}
