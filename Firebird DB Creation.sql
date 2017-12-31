-------------------------------------------------------------------
-- CREATE TYPES

create table types(
id D_PK PRIMARY KEY,
name D_NAME
)

DROP TABLE TYPES;

CREATE generator g_inc_type;

-- Trigger tipo

CREATE TRIGGER t_inc_type FOR types
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_type,1);
END




-------------------------------------------------------------------
-- CREATE BRANDS


create table brands(
id D_PK PRIMARY key,
name D_NAME
)


CREATE generator g_inc_brands;

-- Trigger tipo

CREATE TRIGGER t_inc_brands FOR brands
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_brands,1);
END


-------------------------------------------------------------------
-- CREATE PRODUCTS

create table PRODUCTS(
	id D_PK PRIMARY KEY,
	name D_NAME NOT NULL,
	brandid D_INT,
	typeid D_INT,
	minimumquantity D_INT NOT NULL,
	currentquantity D_INT NOT NULL,
	unitvalue D_DECIMAL NOT NULL,
	unittype D_UNIT NOT NULL,
	FOREIGN KEY (brandid) REFERENCES BRANDS(id)
	ON UPDATE CASCADE ON DELETE SET NULL,
	FOREIGN KEY (typeid) REFERENCES TYPES(id)
	ON UPDATE CASCADE ON DELETE SET NULL
);


CREATE generator g_inc_products;

-- Trigger tipo

CREATE TRIGGER t_inc_products FOR PRODUCTS
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_products,1);
END

-------------------------------------------------------------------
-- CREATE CLIENTS

CREATE TABLE clients(
id D_PK PRIMARY KEY,
name D_NAME,
numsales D_INT,
telephone D_TELEPHONE,
email D_EMAIL,
govid D_GOVID,
birthdate D_DATE,
JOINDATE D_DATE);


CREATE generator g_inc_clients;

-- Trigger tipo

CREATE TRIGGER t_inc_clients FOR clients
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_clients,1);
END

-------------------------------------------------------------------
-- CREATE USERS

create table users(
	id D_PK primary key,
	username D_USERNAME,
	password D_PASSWORD,
	usertype D_USERTYPE
)

CREATE generator g_inc_users;

-- Trigger tipo

CREATE TRIGGER t_inc_users FOR users
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_users,1);
END

-------------------------------------------------------------------
-- CREATE SUPPLIERS


CREATE TABLE suppliers(
id d_pk PRIMARY KEY,
socialreason D_SR,
email D_EMAIL,
cnpj D_CNPJ,
telephone TELEPHONE,
fantasyname FANTASY_NAME,
numberpurchases D_INT
);

CREATE generator g_inc_supplier;

-- Trigger tipo

CREATE OR ALTER TRIGGER t_inc_supplier FOR suppliers
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_suppliers,1);
END

-------------------------------------------------------------------
-- CREATE SALES

CREATE TABLE sales(
id D_PK PRIMARY KEY,
saledate D_DATE,
invoice D_INVOICE,
subtotal D_DECIMAL,
discount D_DECIMAL,
total D_DECIMAL,
clientid D_INT,
finalized D_FINALIZED,
FOREIGN KEY (clientid) REFERENCES CLIENTS(id)
ON UPDATE CASCADE ON DELETE SET NULL
);

DROP TABLE SALES;

SELECT * FROM sales;

INSERT INTO sales (SALEDATE)
VALUES(CURRENT_TIMESTAMP);

CREATE generator g_inc_sales;



CREATE OR ALTER TRIGGER t_inc_sales FOR sales
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_sales,1);
END

-------------------------------------------------------------------
-- CREATE SALEITEMS


CREATE TABLE saleitems(
saleid D_PK,
prodid D_PK,
unitvalue D_DECIMAL,
quantity D_INT NOT NULL,
total D_DECIMAL,
FOREIGN KEY(prodid) REFERENCES PRODUCTS(id)
ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(saleid) REFERENCES SALES(id)
ON UPDATE CASCADE ON DELETE CASCADE,
PRIMARY KEY(saleid,prodid)
);

DROP TABLE SALEITEMS;


-------------------------------------------------------------------
-- CREATE PURCHASES


CREATE TABLE PURCHASES(
id D_PK PRIMARY KEY,
purchasedate D_DATE,
invoice D_INVOICE,
subtotal D_DECIMAL,
discount D_DECIMAL,
total D_DECIMAL,
supplierid D_INT,
finalized D_FINALIZED
FOREIGN KEY(supplierid) REFERENCES SUPPLIERS(id)
ON UPDATE CASCADE ON DELETE SET null);

ALTER TABLE PURCHASES
DROP purchasedate;

ALTER TABLE PURCHASES
ADD purchasedate D_DATE;



CREATE generator g_inc_purchases;

-- Trigger tipo

CREATE OR ALTER TRIGGER t_inc_purchases FOR purchases
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_purchases,1);
END


-------------------------------------------------------------------
-- CREATE PURCHASEITEMS


CREATE TABLE PURCHASEITEMS(
purchaseid D_PK NOT NULL,
prodid D_PK NOT NULL,
unitvalue D_DECIMAL,
quantity D_INT NOT NULL,
total D_DECIMAL,
FOREIGN KEY(prodid) REFERENCES PRODUCTS(id)
ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(purchaseid) REFERENCES PURCHASES(id)
ON UPDATE CASCADE ON DELETE CASCADE,
PRIMARY KEY(purchaseid,prodid)
);


DROP TABLE PURCHASEITEMS;

INSERT INTO USERS(USERNAME,PASSWORD, USERTYPE)
VALUES('admin','admin','admin')




-------------------------------------------------------------------
-- CREATE HISTORY

CREATE TABLE ACTIONHISTORY(
id D_PK PRIMARY KEY,
prodid D_PK,
purchaseid D_INT,
saleid D_INT,
unitvalue D_DECIMAL,
total D_DECIMAL,
quantity D_INT,
actiondate D_DATE
)




CREATE generator g_inc_actionhistory;


CREATE OR ALTER TRIGGER t_inc_actionhistory FOR actionhistory
active BEFORE INSERT POSITION 5
AS BEGIN
	NEW.id=gen_id(g_inc_purchases,1);
END


