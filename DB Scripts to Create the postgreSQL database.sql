alter table usuario
add column tipousuario varchar(14)

alter table usuario
drop column usertype;

alter table usuario
add column password varchar(20);

alter table usuario
add column usertype varchar(14)


select * from usuario;

--------------------------------------------------------
-- 			TYPES TABLE
create table types(
id serial primary key,
name varchar(30)
)

--------------------------------------------------------
-- 			BRANDS TABLE
create table brands(
id serial primary key,
name varchar(30)
)

--------------------------------------------------------
-- 			PRODUCTS TABLE
create table products(
id serial primary key,
name varchar(50),
brandid integer references brands(id),
typeid integer references types(id),
minimumquantity integer,
currentquantity integer,
unitvalue decimal(15,2),
unittype varchar(30)
)

--------------------------------------------------------
-- 			SALES TABLE
create table sales(
id serial primary key,
saledate timestamp,
invoice char(9),
subtotal decimal(15,2),
total decimal(15,2),
clientid int references clients(id),
discout decimal(15,2),
finalized char(1)
)

alter table sales
add column  invoice char(9);

select * from sales;


drop  table sales;

--------------------------------------------------------
-- 			SALEITEMS  TABLE
create table saleitems(
saleid int references sales(id),
prodid int references products(id),
unitvalue decimal(15,2),
quantity integer,
total decimal(15,2),
primary key(saleid,prodid)
)

drop table products;

select * from products;


select * from brands

--------------------------------------------------------
-- 			SALEITEMS  TABLE
create table clients(
id serial primary key,
name varchar(60),
numsales int,
email varchar(50),
telephone varchar(20),
govid varchar(20),
birthdate date
)

alter table CLIENTS
add column joindate timestamp

drop table client;


create table users(
id serial primary key,
username varchar(20),
password varchar(20),
usertype varchar(20)
)

ALTER TABLE users
ADD COLUMN joindate TIMESTAMP

CREATE TABLE suppliers(
id serial PRIMARY KEY,
socialreason varchar(40),
email varchar(40),
cnpj varchar(11),
telephone varchar(20),
fantasyname varchar(40),
numberpurchases int
)

alter table suppliers
add column numberpurchases int

insert into suppliers(socialreason, email, cnpj, telephone, fantasyname, numberpurchases)
values('', '' ,' ',' ', '', 0)

delete from suppliers
where id = 2;

select * from suppliers;

select * from types;

delete from types
where id = 1;


insert into users(username,password,usertype)
values('admin','admin','admin')


insert into usuario(username,password,usertype)
values('admin','admin','admin')

insert into clients(name,numsales,email,telephone,govid,birthdate)
values('vinicius',0,'vini@gmail.com','5532213615','12345678912','2010-02-20')

select * from clients;

select exists(select from client where name ='vinicius' and email = 'vini@gmail.com' and govid ='12345678912')

select *
from client
where name ='vinicius' and email = 'vini@.com' and govid ='12345678912'
limit 1

--------------------------------------------
--					CREATE PURCHASE AND PURCHASE ITEMS

create table purchases(
id serial primary key,
purchasedate timestamp,
invoice char(9),
subtotal decimal(15,2),
total decimal(15,2),
supplierid int references suppliers(id),
discout decimal(15,2),
finalized char(1) 
)


create table purchaseitems(
purchaseid int references purchases(id),
prodid int references products(id),
unitvalue decimal(15,2),
quantity integer,
total decimal(15,2),
primary key(purchaseid,prodid)
)


