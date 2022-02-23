DROP DATABASE IF EXISTS dep8_student_attendance;

CREATE DATABASE dep8_student_attendance;

USE dep8_student_attendance;

CREATE TABLE student
(
    id VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    picture LONGBLOB NOT NULL
);

CREATE TABLE user
(
    username VARCHAR(30) PRIMARY KEY ,
    name VARCHAR(100) NOT NULL ,
    password VARCHAR(50) NOT NULL,
    role ENUM ('ADMIN','USER' ) NOT NULL
);


CREATE TABLE attendance
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    status ENUM ('IN','OUT') NOT NULL ,
    student_id VARCHAR(30) NOT NULL,
    username VARCHAR(30) NOT NULL,
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES student(id),
    CONSTRAINT fk_attendance_user FOREIGN KEY (username) REFERENCES user(username)

);
SET GLOBAL log_bin_trust_function_creators = 1;
#
# CREATE FUNCTION sf_add(value1 INT, value2 INT) RETURNS INT
# BEGIN
#     RETURN value1+value1;
# end;
#
# SELECT sf_add(10,20);
#
#
# CREATE FUNCTION sf_print_user(username VARCHAR(30), role ENUM('admin','user')) RETURNS VARCHAR(30)
# BEGIN
#     RETURN CONCAT(username, '-', role);
# end;
#
# SELECT sf_print_user(name,role) FROM user;
#
# CREATE TABLE product(
#     id INT AUTO_INCREMENT PRIMARY KEY,
#     name VARCHAR(100) NOT NULL,
#     buying_price DECIMAL(10,2) NOT NULL,
#     selling_price DECIMAL(10,2) NOT NULL
# );
#
# INSERT INTO product (name, buying_price, selling_price) VALUES
#     ('Mouse', 500.00, 700.00),
#     ('Keyboard', 600.00, 850.00),
#     ('Monitor', 8000.00, 12500.00);
#
# CREATE FUNCTION sf_profit(buyingPrice DECIMAL,sellingPrice DECIMAL) RETURNS DECIMAL
# BEGIN
#     DECLARE diff DECIMAL(10,2);
#     DECLARE percentage DECIMAL(10,2);
#     SET diff = sellingPrice-buyingPrice;
#     SET percentage = diff/buyingPrice*100;
#
#
#     RETURN CONCAT(percentage,'%');
#
#
# end;
#
# SELECT *,sf_profit(buying_price,selling_price) AS PROFIT FROM product;
#
# CREATE PROCEDURE sp_add_product(IN name VARCHAR(100), IN buying_price DECIMAL(10,2), IN selling_price DECIMAL(10,2))
# BEGIN
#     INSERT INTO product (name, buying_price, selling_price) VALUES (name, buying_price, selling_price);
#
# end;
#
# CALL sp_add_product('Motherboard', 5000,5500);
#
#
#
# CREATE TABLE product_log(
#     log_id INT AUTO_INCREMENT PRIMARY KEY,
#     product_id INT NOT NULL ,
#     action ENUM('ADD','REMOVE', 'UPDATE') NOT NULL ,
#     date DATETIME NOT NULL
# );
#
# CREATE TRIGGER st_product_log_add AFTER INSERT ON product
#     FOR EACH ROW
#     BEGIN
#         INSERT INTO product_log (log_id, product_id, action, date) VALUES (NEW.id, 'ADD', NOW());
#     end;
#
# CREATE TRIGGER st_product_log_remove AFTER DELETE ON product
#     FOR EACH ROW
# BEGIN
#     INSERT INTO product_log (log_id, product_id, action, date) VALUES (OLD.id, 'ADD', NOW());
# end;
#
# CREATE TRIGGER st_product_log_update AFTER UPDATE ON product
#     FOR EACH ROW
# BEGIN
#     INSERT INTO product_log (log_id, product_id, action, date) VALUES (NEW.id, 'ADD', NOW());
# end;
#
# ALTER TABLE product ADD COLUMN description VARCHAR(100) NOT NULL ;
#
# ALTER TABLE product DROP COLUMN description;
#
# ALTER TABLE product RENAME COLUMN description TO descript;
# ALTER TABLE product MODIFY COLUMN description VARCHAR(700);
# ALTER TABLE product CHANGE COLUMN description descript VARCHAR(700) NOT NULL;
#
# ALTER TABLE product ADD COLUMN bar_code VARCHAR(100) NOT NULL;
# ALTER TABLE product ADD CONSTRAINT uk_product UNIQUE (bar_code);
# ALTER TABLE product DROP CONSTRAINT uk_product;


ALTER TABLE student ADD COLUMN guardian_contact VARCHAR(100) NOT NULL ;

ALTER TABLE student ADD CONSTRAINT uk_contact UNIQUE (guardian_contact);

# SELECT status FROM attendance WHERE student_id = '160320L' ORDER BY date DESC LIMIT 1;

SELECT student_id, date, status, name FROM attendance JOIN student s on s.id = attendance.student_id ORDER BY date DESC LIMIT 1;



