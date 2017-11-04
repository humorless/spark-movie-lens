-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(email, pass, sess)
VALUES (:email, :pass, :sess)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET email = :email
WHERE id = :id

-- :name get-user-by-email :? :1
-- :doc retrieve a user given the email
SELECT * FROM users
WHERE email = :email

-- :name get-user-by-sess :? :1
-- :doc retrieve a user given the session id
SELECT * FROM users
WHERE sess = :sess

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id

-- :name create-ratings! :! :n
-- :doc create multiple rating records with :tuple* parameter type
INSERT INTO ratings (user_id, item_id, rating)
VALUES :tuple*:ratings

-- :name create-movies! :! :n
-- :doc create a movie record
INSERT INTO movies (id, name)
VALUES (:id, :name)

-- :name get-movies :? :*
-- :doc retrieve all the movie id and name
SELECT * FROM MOVIES;

-- :name get-movie-rating-by-id :? :*
-- :doc retrieve ratings of movie of certain user id
SELECT item_id, rating AS r, name  FROM RATINGS join MOVIES on item_id = id WHERE user_id = :id

-- :name get-movie-average :? :*
-- :doc retrieve average rating of movie
SELECT item_id, r, name FROM (SELECT item_id, ROUND(avg(rating * 1.0), 2) as r FROM ratings GROUP BY
item_id ORDER BY r DESC) as temp JOIN movies on item_id = id;

-- :name get-movie-ratings :? :*
-- :doc retrieve all rating of movie
SELECT user_id, item_id, rating FROM RATINGS;
