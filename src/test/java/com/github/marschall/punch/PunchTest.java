package com.github.marschall.punch;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PunchTest {

  private ForkJoinPool pool;

  @Before
  public void before() {
    this.pool = new PunchPool(NullListener.INSTANCE);
  }

  @After
  public void after() throws InterruptedException {
    this.pool.shutdown();
    assertTrue(this.pool.awaitTermination(1, TimeUnit.SECONDS));
  }

  @Test
  public void batchSample() {
    this.pool.invoke(JobTrees.buildFileOutRoot());
  }

  /**
   * serial-1 has to be executed before serial-2, serial-3 has to be executed before serial-4.
   */
  @Test
  public void treeSample() {
    this.pool.invoke(JobTrees.buildTree());
  }

  enum NullListener implements TaskStateListener {

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

}
