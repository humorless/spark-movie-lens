CREATE TABLE users
(id INT PRIMARY KEY auto_increment,
 email VARCHAR(40),
 pass VARCHAR(300),
 sess UUID);
--;;
INSERT INTO PUBLIC.USERS(ID, EMAIL, PASS, SESS) VALUES
(1000, 'dummy@example.com', 'bcrypt+sha512$2e2c9e9e74c2242f02721247b78544ee$12$956dfdf3dad4eade193141b2c4e090d06a4c18a53736490c', '44e6b1b8-95ff-47c9-a9fd-4c4dabcfa1ce');              
