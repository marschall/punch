package com.github.marschall.punch.jdbc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.github.marschall.punch.core.TaskPath;
import com.github.marschall.punch.core.TaskStateListener;

public class PersistingTaskStateListener implements TaskStateListener {

  private static final String INSERT_TASK_SQL = "INSERT INTO t_task_state(task_path, task_state) VALUES (?, ?)";
  private static final String UPDATE_TASK_SQL = "UPDATE t_task_state SET task_state = ? WHERE task_path = ?";

  private final JdbcTemplate jdbcTemplate;
  private final PlatformTransactionManager transactionManager;
  private final ConcurrentMap<TaskPath, TransactionStatus> transactionStates;
  private final TransactionDefinition transactionDefinition;

  PersistingTaskStateListener(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {
    this.jdbcTemplate = jdbcTemplate;
    this.transactionManager = transactionManager;
    this.transactionStates = new ConcurrentHashMap<>();
    this.transactionDefinition = new DefaultTransactionDefinition();
  }

  @Override
  public void taskStarted(TaskPath path) {
    TransactionStatus status = this.transactionManager.getTransaction(this.transactionDefinition);
    this.transactionStates.put(path, status);
    this.jdbcTemplate.update(INSERT_TASK_SQL, path.toString(), "RUNNING");
  }

  @Override
  public void taskFinished(TaskPath path) {
    this.jdbcTemplate.update(UPDATE_TASK_SQL, "FINISHED", path.toString());
    TransactionStatus status = this.transactionStates.remove(path);
    this.transactionManager.commit(status);
  }

  @Override
  public void taskFailed(TaskPath path) {
    TransactionStatus status = this.transactionStates.remove(path);
    this.transactionManager.rollback(status);
  }
}