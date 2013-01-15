package com.github.marschall.punch.util;

import com.github.marschall.punch.core.TaskPath;
import com.github.marschall.punch.core.TaskStateListener;

public enum NullListener implements TaskStateListener {

  INSTANCE;

  @Override
  public void taskStarted(TaskPath path) {
    print(path, "started");
  }
  @Override
  public void taskFinished(TaskPath path) {
    print(path, "finished");
  }

  @Override
  public void taskFailed(TaskPath path) {
    print(path, "failed");
  }

  private void print(TaskPath path, String whatHappened) {
    System.out.println("task " + path + " " + whatHappened);
  }

}