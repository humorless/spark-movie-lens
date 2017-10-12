CREATE TABLE users
(id INT PRIMARY KEY,
 email VARCHAR(40),
 pass VARCHAR(300));

CREATE TABLE movies
(id INT PRIMARY KEY,
 name VARCHAR(255));

CREATE TABLE ratings 
(user_id INT,
 item_id INT,
 rating  INT,
 PRIMARY KEY(user_id, item_id));
