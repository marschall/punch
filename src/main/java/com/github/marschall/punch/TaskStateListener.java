package com.github.marschall.punch;


public interface TaskStateListener {
  
  void taskStarted(TaskPath path);
  
  void taskFinished(TaskPath path);

}
