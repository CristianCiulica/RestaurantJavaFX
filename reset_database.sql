DELETE FROM pizza_toppinguri;
DELETE FROM produs;
ALTER SEQUENCE produs_id_seq RESTART WITH 1;
SELECT COUNT(*) as "Products remaining" FROM produs;
SELECT COUNT(*) as "Toppinguri remaining" FROM pizza_toppinguri;

