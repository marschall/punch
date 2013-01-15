package com.github.marschall.punch.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RecursiveAction;

public final class JobTrees {
  
  private JobTrees() {
    throw new AssertionError("not instantiable");
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
  static RecursiveAction buildTree() {
    RecoverableTask serial1 = buildSerialTasks(1, 2);
    RecoverableTask singleTask = new StringTask("singleTask-1");
    RecoverableTask serial2 = buildSerialTasks(3, 2);

    Collection<RecoverableTask> tasks = new ArrayList<>(3);
    tasks.add(serial1);
    tasks.add(singleTask);
    tasks.add(serial2);
    return new ParallelTaskContainer(tasks);
  }

  static RecoverableTask buildSerialTasks(int start, int numberOfTasks) {
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
  public static RecoverableTask buildFileOutRoot() {
    Collection<RecoverableTask> tasks = new ArrayList<>(2);
    tasks.add(buildTenantJobs(1, 10));
    tasks.add(buildTenantJobs(2, 5));
    return new ParallelTaskContainer(tasks);
  }

  private static RecoverableTask buildTenantJobs(int tenant, int numberOfStagingJobs) {
    Collection<RecoverableTask> tasks = new ArrayList<>(2);
    tasks.add(buildStagingJobs(numberOfStagingJobs, tenant));
    tasks.add(new StringTask("writing-job tenant-" + tenant));
    return new SerialTaskContainer(tasks);
  }

  private static RecoverableTask buildStagingJobs(int numberOfTasks, int tenant) {
    Collection<RecoverableTask> tasks = new ArrayList<>(numberOfTasks);
    for (int i = 0; i < numberOfTasks; i++) {
      tasks.add(new StringTask("staging-job-" + i + " tenant-" + tenant));
    }
    return new ParallelTaskContainer(tasks);
  }

  static final class StringTask extends RecoverableTask {

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
