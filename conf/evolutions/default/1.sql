# Tasks schema
 
# --- !Ups

CREATE SEQUENCE task_id_seq;
 
CREATE SEQUENCE user_id_seq;

CREATE TABLE tasks (
    id       integer      NOT NULL,
    status   integer      NOT NULL,
    priority integer      NOT NULL,
    text     varchar(255) NOT NULL,
    user_id  integer NOT NULL
);
 
CREATE TABLE users (
    id                  integer      NOT NULL DEFAULT nextval('user_id_seq'),
    name                varchar(255) NOT NULL,
    password_digest     varchar(255) NOT NULL, 
    CONSTRAINT 
    pk_users            PRIMARY KEY (id)
);
 
 
ALTER TABLE tasks 
	ADD CONSTRAINT fk_tasks_users_1 FOREIGN KEY (user_id) REFERENCES users (id) 
	ON DELETE RESTRICT 
	ON UPDATE RESTRICT;
	
	
CREATE INDEX ix_tasks_users_1 ON tasks (user_id);

# --- !Downs
 
DROP INDEX ix_tasks_users_1;
 
DROP TABLE users;

DROP TABLE tasks;

DROP SEQUENCE IF EXISTS user_id_seq;

DROP SEQUENCE IF EXISTS task_id_seq;

