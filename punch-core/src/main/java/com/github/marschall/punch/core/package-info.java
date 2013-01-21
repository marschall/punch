package com.github.marschall.punch.core;

/**
 * The absolute minimum core classes.
 *
 * Concepts:
 *  - a VM crash is treated like a termination due to an exception
 *  - the state of a task is either
 *   - not yet run (implicit)
 *   - running
 *   - finished
 *  - in a case of a re-run all not finished tasks are run again
 *  - a task is identified by a path
 *  - in a recovery case the job tree looks exactly the same
 *
 * Things to consider:
 *  - drop "running" state
 *
 * Possible features:
 *  - idempotent tasks that don't require a transaction
 *  - #getDescription() on listanble task
 *  - pass task to listener
 *  - execution ID:
 *    - TaskPath.root() is not 0 but a per-VM unique ID identifying a task tree
 *    - this ID might be used in the task state listener to correlate a task with the task tree it belongs to
 */