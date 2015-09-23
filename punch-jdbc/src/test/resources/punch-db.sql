CREATE TABLE t_task_state (
   task_path VARCHAR2(4000),
   task_state VARCHAR2(32)
);

CREATE SEQUENCE seq_task_state START WITH 1;
ALTER TABLE t_task_state ADD CONSTRAINT uc_executionid_taskpath UNIQUE(task_path);
