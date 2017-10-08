-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id

-- :name create-ratings! :! :n
-- :doc create a rating record
INSERT INTO ratings (user_id, item_id, rating)
VALUES (:user_id, :item_id, :rating)

-- :name create-ratings! :! :n
-- :doc create multiple rating records with :tuple* parameter type
INSERT INTO ratings (user_id, item_id, rating)
VALUES :tuple*:ratings

-- :name create-movies! :! :n
-- :doc create a movie record
INSERT INTO movies (id, name)
VALUES (:id, :name)

-- :name get-movie-average :? :*
-- :doc retrieve average rating of movie
SELECT item_id, ROUND(avg(rating * 1.0), 2) AS r, name FROM RATINGS join MOVIES on item_id = id GROUP BY item_id ORDER BY r DESC 
