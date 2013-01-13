package com.github.marschall.punch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
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
    this.pool.awaitTermination(1, TimeUnit.HOURS);
  }

  @Test
  public void batchSample() {
    this.pool.invoke(this.buildFileOutRoot());
  }

  /**
   * serial-1 has to be executed before serial-2, serial-3 has to be executed before serial-4.
   */
  @Test
  public void treeSample() {
    this.pool.invoke(buildTree());
  }

  /**
   * <pre>
   * parallel
   *   - sequential
   *     - serial-1
   *     - serial-2
   *   - singleTask-1
   *   - sequential
   *     - serial-3
   *     - serial-4
   * </pre>
   */
  private RecursiveAction buildTree() {
    RecoverableTask serial1 = buildSerialTasks(1, 2);
    RecoverableTask singleTask = new StringTask("singleTask-1");
    RecoverableTask serial2 = buildSerialTasks(3, 2);

    Collection<RecoverableTask> tasks = new ArrayList<>(3);
    tasks.add(serial1);
    tasks.add(singleTask);
    tasks.add(serial2);
    return new ParallelTaskContainer(tasks);
  }

  private RecoverableTask buildSerialTasks(int start, int numberOfTasks) {
    Collection<RecoverableTask> tasks = new ArrayList<>(numberOfTasks);
    for (int i = start; i < start + numberOfTasks; i++) {
      tasks.add(new StringTask("serial-" + i));
    }
    return new SerialTaskContainer(tasks);
  }

  /**
   * <pre>
   * fileout-root
   *  - tenants (parallel)
   *    - tenant1 (sequential)
   *      - staging (parallel)
   *        - staging-job1
   *        - staging-job2
   *        - ...
   *      - writing-job
   *    - tenant2 (sequential)
   *      ...
   * </pre>
   */
  private RecoverableTask buildFileOutRoot() {
    Collection<RecoverableTask> tasks = new ArrayList<>(2);
    tasks.add(buildTenantJobs(1, 10));
    tasks.add(buildTenantJobs(2, 5));
    return new ParallelTaskContainer(tasks);
  }

  private RecoverableTask buildTenantJobs(int tenant, int numberOfStagingJobs) {
    Collection<RecoverableTask> tasks = new ArrayList<>(2);
    tasks.add(buildStagingJobs(numberOfStagingJobs, tenant));
    tasks.add(new StringTask("writing-job tenant-" + tenant));
    return new SerialTaskContainer(tasks);
  }

  private RecoverableTask buildStagingJobs(int numberOfTasks, int tenant) {
    Collection<RecoverableTask> tasks = new ArrayList<>(numberOfTasks);
    for (int i = 0; i < numberOfTasks; i++) {
      tasks.add(new StringTask("staging-job-" + i + " tenant-" + tenant));
    }
    return new ParallelTaskContainer(tasks);
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

  static final class StringTask extends ListenableTask {

    private final String s;

    StringTask(String s) {
      this.s = s;
    }

    @Override
    protected void safeCompute() {
      System.out.println(this.s);
    }

  }

}
