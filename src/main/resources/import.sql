/* Create users with roles */
INSERT INTO `users` (username, password, enabled, name, last_name, email, create_at, photo) VALUES ('mhpbuyer','$2a$10$O9wxmH/AeyZZzIS09Wp8YOEMvFnbRVJ8B4dmAMVSGloR62lj.yqXG',1,'John', 'Doe', 'johndoe@buyer.com', '2017-08-01', '' );
INSERT INTO `users` (username, password, enabled, name, last_name, email, create_at, photo) VALUES ('mhpadmin','$2a$10$DOMDxjYyfZ/e7RcBfUpzqeaCs8pLgcizuiQWXPkU35nOhZlFcE9MS',1,'Mary', 'Doe', 'marydoe@admin.com', '2017-11-01', '');

INSERT INTO `authorities` (user_id, authority) VALUES (1,'ROLE_USER');
INSERT INTO `authorities` (user_id, authority) VALUES (2,'ROLE_ADMIN');
INSERT INTO `authorities` (user_id, authority) VALUES (2,'ROLE_USER');

/* Create Products */
INSERT INTO products (name, price, create_at) VALUES('LG Pantalla IPS', 259990, NOW());
INSERT INTO products (name, price, create_at) VALUES('SSD EVO 500GB', 123490, NOW());
INSERT INTO products (name, price, create_at) VALUES('AMD RIZEN 7 3700X', 1499990, NOW());
INSERT INTO products (name, price, create_at) VALUES('GForce RTX 2070X', 37990, NOW());
INSERT INTO products (name, price, create_at) VALUES('Mars Gaming keyboard', 69990, NOW());
INSERT INTO products (name, price, create_at) VALUES('Aorus Ultra Motherboard', 69990, NOW());
INSERT INTO products (name, price, create_at) VALUES('Mouse Corsair', 299990, NOW());

/* Create Carts */
INSERT INTO carts (cart_name, observation, user_id, create_at) VALUES('Equipos de oficina', 'Nota', 1, NOW());

INSERT INTO cart_items (quantity, cart_id, product_id) VALUES(1, 1, 1);
INSERT INTO cart_items (quantity, cart_id, product_id) VALUES(2, 1, 4);
INSERT INTO cart_items (quantity, cart_id, product_id) VALUES(1, 1, 5);
INSERT INTO cart_items (quantity, cart_id, product_id) VALUES(1, 1, 7);

INSERT INTO carts (cart_name, observation, user_id, create_at) VALUES('Bicicleta', 'Nota', 1, NOW());

INSERT INTO cart_items (quantity, cart_id, product_id) VALUES(3, 2, 6);

