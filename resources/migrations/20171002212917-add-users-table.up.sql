CREATE TABLE users
(id INT PRIMARY KEY auto_increment,
 email VARCHAR(40),
 pass VARCHAR(300),
 sess UUID);

CREATE TABLE movies
(id INT PRIMARY KEY,
 name VARCHAR(255));

CREATE TABLE ratings 
(user_id INT,
 item_id INT,
 rating  INT,
 PRIMARY KEY(user_id, item_id));
