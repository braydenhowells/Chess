# My notes

+--------------------------+--------------------------------------------+--------------------------------------------+
| Command                  | Purpose                                    | Example                                    |
+--------------------------+--------------------------------------------+--------------------------------------------+
| show databases           | Lists all of the databases                 | show databases                             |
| use name                 | Open database                              | use student                                |
| show tables              | Lists all tables for the selected database | show tables                                |
| describe name            | List fields for a table                    | describe student                           |
| show index from name     | List indexes for a table                   | show index from student                    |
| show full processlist    | List currently executing queries           | show full processlist                      |
| create database name     | Create a new database                      | create database student                    |
| drop database name       | Delete a database                          | drop database student                      |
| create table name        | Create a new table                         | create table pet (name varchar(128), age int) |
| insert into name         | Insert data into a table                   | insert into pet values ("zoe", 3)          |
| select * from name       | Query a table                              | select * from pet                          |
| drop table name          | Delete a table                             | drop table pet                             |
+--------------------------+--------------------------------------------+--------------------------------------------+

paste to get into MySQL in the command prompt:
mysqlsh -u root -pNewSecurePassword --sql




