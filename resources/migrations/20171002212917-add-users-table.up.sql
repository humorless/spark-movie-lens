CREATE TABLE users
(id VARCHAR(20) PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 last_login TIME,
 is_active BOOLEAN,
 pass VARCHAR(300));

CREATE TABLE ratings 
(user_id INT,
 item_id INT,
 rating  INT,
 time    LONG);

ALTER TABLE ratings ADD PRIMARY KEY (user_id, item_id)
