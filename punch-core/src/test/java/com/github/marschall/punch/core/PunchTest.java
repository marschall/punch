package com.github.marschall.punch.core;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.marschall.punch.util.NullListener;

import static org.junit.Assert.assertTrue;


public class PunchTest {

  private ForkJoinPool pool;

  @Before
  public void before() {
    this.pool = new PunchPool(NullListener.INSTANCE, AlwaysFinishedRecoveryService.INSTANCE);
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

  enum AlwaysFinishedRecoveryService implements RecoveryService {
    INSTANCE;

    @Override
    public boolean isFinished(TaskPath path) {
      return true;
    }

  }

}
