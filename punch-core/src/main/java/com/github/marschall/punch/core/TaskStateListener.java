package com.github.marschall.punch.core;


public interface TaskStateListener {
  //eg. open transaction
  void taskStarted(TaskPath path);

  //eg. commit transaction
  void taskFinished(TaskPath path);

  //eg. rollback transaction
  void taskFailed(TaskPath path);

}
