SELECT * FROM products;

SELECT name, category FROM products;

SELECT * FROM products
WHERE quantity > 10;

SELECT * FROM products
WHERE price < 5000;

SELECT * FROM products
WHERE category = 'Electronics';

SELECT * FROM products
ORDER BY price DESC;

SELECT * FROM products
ORDER BY price DESC
LIMIT 3;

SELECT SUM(quantity) FROM products;

SELECT AVG(price) FROM products;

SELECT * FROM products
ORDER BY price DESC
LIMIT 1;
