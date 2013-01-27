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
 *  - running the same task instance twice won't work
 *
 * Possible features:
 *  - idempotent tasks that don't require a transaction
 *  - #getDescription() on listanble task
 *  - pass task to listener
 *  - execution ID:
 *    - TaskPath.root() is not 0 but a unique ID identifying a task tree.
 *      This will make the TaskPath a globally unique key.
 */