

create new database

CREATE DATABASE userbase;

use userbase;
--------------------------------------------
@create user table using below command 

CREATE  TABLE users (
  username VARCHAR(45) NOT NULL ,
  password VARCHAR(45) NOT NULL ,
  enabled TINYINT NOT NULL DEFAULT 1 ,
  PRIMARY KEY (username));
  -------------------------------------------------------
  
  
 @create user role table whether user is admin or normal user 
CREATE TABLE user_roles (
  user_role_id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(45) NOT NULL,
  role varchar(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  UNIQUE KEY uni_username_role (role,username),
  KEY fk_username_idx (username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username));
 ---------------------------------------------------------------------------------
@Add data using below commands 
INSERT INTO users(username,password,enabled) VALUES ('savitri','savitri', true);
INSERT INTO users(username,password,enabled) VALUES ('mannu','mannu', true);
INSERT INTO user_roles (username, role) VALUES ('savitri', 'ROLE_USER');
INSERT INTO user_roles (username, role) VALUES ('savitri', 'ROLE_ADMIN');
INSERT INTO user_roles (username, role) VALUES ('mannu', 'ROLE_USER');