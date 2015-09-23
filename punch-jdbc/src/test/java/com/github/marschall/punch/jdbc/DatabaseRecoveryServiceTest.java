package com.github.marschall.punch.jdbc;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import com.github.marschall.punch.core.RecoveryService;
import com.github.marschall.punch.core.TaskPath;

import static org.junit.Assert.assertEquals;

public class DatabaseRecoveryServiceTest {

  private static final String INSERT_INTO_TASK_STATE = "INSERT INTO t_task_state(task_path, task_state) VALUES(?, ?)";

  private JdbcTemplate jdbcTemplate;
  private EmbeddedDatabase db;
  private RecoveryService recoveryService;

  @Before
  public void before() {
    this.db = new H2DatabaseBuilder()
    .setName("punch-db")
    .addScript("punch-db.sql")
    .build();

    this.jdbcTemplate = new JdbcTemplate(this.db);
    this.recoveryService = new DatabaseRecoveryService(this.jdbcTemplate);

    this.jdbcTemplate.update(INSERT_INTO_TASK_STATE, "1/0", "FINISHED");
    this.jdbcTemplate.update(INSERT_INTO_TASK_STATE, "2/0", "FINISHED");
    this.jdbcTemplate.update(INSERT_INTO_TASK_STATE, "3/0", "RUNNING");
  }

  @After
  public void after() {
    this.db.shutdown();
  }

  @Test
  public void newTaskGroup() {
    Set<Integer> taskGroups = new HashSet<>();

    taskGroups.add(this.recoveryService.newTaskGroup());
    taskGroups.add(this.recoveryService.newTaskGroup());
    taskGroups.add(this.recoveryService.newTaskGroup());

    assertEquals(3, taskGroups.size());
  }

  @Test
  public void isFinished() {
    Assert.assertTrue(this.recoveryService.isFinished(TaskPath.fromString("1/0")));
    Assert.assertTrue(this.recoveryService.isFinished(TaskPath.fromString("2/0")));

    Assert.assertFalse(this.recoveryService.isFinished(TaskPath.fromString("3/0")));
  }

}
