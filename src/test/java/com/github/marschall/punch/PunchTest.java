package com.github.marschall.punch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PunchTest {

  private ForkJoinPool pool;

  @Before
  public void before() {
    this.pool = new ForkJoinPool();
  }

  @After
  public void after() throws InterruptedException {
    this.pool.shutdown();
    this.pool.awaitTermination(1, TimeUnit.SECONDS);
  }

  @Test
  public void sample() {
    this.pool.invoke(buildTopLevelTask());
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
    Collection<ForkJoinTask<?>> serial1 = buildSerialTasks(1, 2);
    ForkJoinTask<?> serialTaskContainer1 = new SerialTaskContainer(serial1);
    
    ForkJoinTask<?> singleTask = new StringTask("singleTask-1");
    
    Collection<ForkJoinTask<?>> serial2 = buildSerialTasks(3, 2);
    ForkJoinTask<?> serialTaskContainer2 = new SerialTaskContainer(serial2);

    Collection<ForkJoinTask<?>> tasks = new ArrayList<>(3);
    tasks.add(serialTaskContainer1);
    tasks.add(singleTask);
    tasks.add(serialTaskContainer2);
    return new ParallelTaskContainer(tasks);
  }

  private RecursiveAction buildTopLevelTask() {
    Collection<ForkJoinTask<?>> tasks = new ArrayList<>(9);
    tasks.addAll(buildSerialTasks(1, 4));
    tasks.add(buildParallelTasks(1, 10));
    tasks.addAll(buildSerialTasks(5, 4));
    return new SerialTaskContainer(tasks);
  }

  private Collection<ForkJoinTask<?>> buildSerialTasks(int start, int numberOfTasks) {
    Collection<ForkJoinTask<?>> tasks = new ArrayList<>(numberOfTasks);
    for (int i = start; i < start + numberOfTasks; i++) {
      tasks.add(new StringTask("serial-" + i));
    }
    return tasks;
  }

  private RecursiveAction buildParallelTasks(int start, int numberOfTasks) {
    Collection<ForkJoinTask<?>> tasks = new ArrayList<>(numberOfTasks);
    for (int i = start; i < start + numberOfTasks; i++) {
      tasks.add(new StringTask("parallel-" + i));
    }
    return new ParallelTaskContainer(tasks);
  }

  static final class StringTask extends RecursiveAction {

    private final String s;

    StringTask(String s) {
      this.s = s;
    }

    @Override
    protected void compute() {
      System.out.println(this.s);
    }

  }

}
