-- Procedures

--------------------------------------------------------------------
--						ADD A SUPPLIER PROCEDURES


--------------------------------------------------------------------
--						ADD A PURCHASE PROCEDURES
-- GET CLIENTS
CREATE or replace FUNCTION get_suppliers()
RETURNS TABLE(name varchar(50)) AS $$
BEGIN
    RETURN QUERY select s.fantasyname
				  FROM suppliers as s;
END;            
$$ LANGUAGE plpgsql;

select * from get_suppliers();



-- REMOVE PURCHASE
CREATE or replace FUNCTION removePurchase(currentpurchaseid integer)
RETURNS void AS $$
BEGIN    
	delete 
	from purchaseitems pit
	where pit.purchaseid = currentpurchaseid;
	
	delete 
	from purchases p
	where p.id = currentpurchaseid;
END;            
$$ LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM removePurchase(1);
END $$;


select * from purchases;

select * from purchaseitems;

CREATE OR REPLACE FUNCTION add_purchaseitem(	
	purchaseid_input int,
	prodname_input character varying,
	unitvalue_input decimal(15,2),
	quantity_input integer,
	total_input decimal(15,2)
) 
    RETURNS void AS $$
    declare prodid integer;
    BEGIN
	    select p.id
	    from products p
	    where p."name" = prodname_input
	    into prodid;
	    
	    INSERT INTO purchaseitems(
	    purchaseid,
		prodid ,
		unitvalue,
		quantity,
		total
	   	) 
	    VALUES (
	    purchaseid_input,
		prodid,
		unitvalue_input,
		quantity_input,
		total_input	   
		);
    END;
$$ LANGUAGE plpgsql;

drop function add_saleitem;

select * from purchaseitems;

select * from purchases;

DO $$ BEGIN
    PERFORM add_purchaseitem(1,'Optimum Whey 900g - 100%',100,10,1000);
END $$;



CREATE OR REPLACE FUNCTION add_purchase(	
	subtotal decimal(15,2),
	total  decimal(15,2),
	suppliername character varying,
	discout  decimal(15,2),
	finalized char(1)
) 
    RETURNS void AS $$
    declare 
    	idsupplier integer;    	
    BEGIN
      	select s.id
      	from suppliers s
      	where s.fantasyname = suppliername
      	into idsupplier;      	
	    
	    INSERT INTO purchases(
	   	purchasedate,		
		subtotal,
		total,
		supplierid,
		discout,
		finalized) 
	    VALUES (
	    current_timestamp,	    
	    subtotal,
	    total,
	    idsupplier,
	    discout,
	    finalized
		);
    END;
    $$ LANGUAGE plpgsql;
    
    -- Execute store procedure    
DO $$ BEGIN
    PERFORM add_purchase(100.0,50.0,'batataDoceSales',0.5, 'F');
END $$;

select * from purchases;

select * from suppliers;


-- CHECK FOR INVOICE ID NOT USED YET
CREATE or replace FUNCTION checkInvoicePurchaseAlreadyExists(inputInvoice character varying)
RETURNS TABLE(id integer) AS $$
BEGIN
    RETURN QUERY select p.id 
				  FROM purchases as p
				  where p.invoice = inputInvoice;
END;            
$$ LANGUAGE plpgsql;

update purchases
set invoice = '123456789'
where id = 1;

select * from purchases;

select * from checkInvoicePurchaseAlreadyExists('123456788');


-- FINISH THE PURCHASE TO FURTHER GENERATE THE INVOICE
CREATE or replace FUNCTION finishPurchase(purchaseid integer, input_subtotal decimal(15,2), input_total decimal(15,2))
RETURNS void AS $$
BEGIN    
	update purchases 
	set finalized = 'Y', subtotal = input_subtotal, total = input_total
	where id = purchaseid;
END;            
$$ LANGUAGE plpgsql;

select * from purchases;

select * from purchase items;


DO $$ BEGIN
    PERFORM finishPurchase(5,1200,100);
END $$;


-- set the invoice value to purchase
CREATE or replace FUNCTION setPurchaseInvoice(purchaseid integer,invoiceval character varying)
RETURNS void AS $$
BEGIN    
	update purchases 
	set invoice = invoiceval
	where id = purchaseid;
END;            
$$ LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM setPurchaseInvoice(1,'123456788');
END $$;

-- CHECK IF PRODUCT HAS ALREADY BEEN ADDED
CREATE or replace FUNCTION purchaseItem_exists(currentPurchaseID integer,pname character varying)
RETURNS TABLE(hasitem boolean) AS $$
declare
	idprod integer;
BEGIN
    select id
    from products p
    where p."name" = pname
    into idprod;
	
	RETURN QUERY 
    select exists(select from purchaseitems as pit
    				where pit.purchaseid = currentPurchaseID and pit.prodid = idprod );
END;            
$$ LANGUAGE plpgsql;

drop function item_exists(currentsaleid integer,pname character varying)

select * from products;



select * from purchaseItem_exists(1,'Optimum Whey 900g - 100%');

--------------------------------------------------------------------
--						ADD A SALE PROCEDURES

CREATE OR REPLACE FUNCTION add_saleitem(	
	saleid_input int,
	prodname_input character varying,
	unitvalue_input decimal(15,2),
	quantity_input integer,
	total_input decimal(15,2)
) 
    RETURNS void AS $$
    declare prodid integer;
    BEGIN
	    select p.id
	    from products p
	    where p."name" = prodname_input
	    into prodid;
	    
	    INSERT INTO saleitems(
	    saleid,
		prodid ,
		unitvalue,
		quantity,
		total
	   	) 
	    VALUES (
	    saleid_input,
		prodid,
		unitvalue_input,
		quantity_input,
		total_input	   
		);
    END;
$$ LANGUAGE plpgsql;

drop function add_saleitem;

select * from saleitems;

DO $$ BEGIN
    PERFORM add_saleitem(1,'Optimum Whey 900g - 100%',100,10,1000);
END $$;

   
    
-- DELETE A SALE AND SALE ITEMS OF IT
CREATE or replace FUNCTION removeSale(currentsaleid integer)
RETURNS void AS $$
BEGIN    
	delete 
	from saleitems sa
	where sa.saleid = currentsaleid;
	
	delete 
	from sales sa
	where sa.id = currentsaleid;
END;            
$$ LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM removeSale(1);
END $$;
    

 -- FINISH THE SALE TO FURTHER GENERATE THE INVOICE
CREATE or replace FUNCTION finishSale(saleid integer,  input_subtotal decimal(15,2), input_total decimal(15,2))
RETURNS void AS $$
BEGIN    
	update sales 
	set finalized = 'Y',subtotal = input_subtotal, total = input_total
	where id = saleid;
END;            
$$ LANGUAGE plpgsql;

select * from sales;

select * from saleitems;


DO $$ BEGIN
    PERFORM finishSale(1,2000,1500);
END $$;

-- set the invoice value
CREATE or replace FUNCTION setInvoice(saleid integer,invoiceval character varying)
RETURNS void AS $$
BEGIN    
	update sales 
	set invoice = invoiceval
	where id = saleid;
END;            
$$ LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM setInvoice(1,'');
END $$;

-- get a typename by a product name
CREATE or replace FUNCTION get_a_typeby_product(prodname character varying)
RETURNS TABLE(typename character varying) AS $$
declare
auxtypeid integer;
BEGIN
    select p.typeid
	from products p
	where p."name" = prodname
	into auxtypeid;		
	
	RETURN QUERY select t.name
				  FROM types as t
				  where t.id = auxtypeid;
END;            
$$ LANGUAGE plpgsql;

select * from get_a_typeby_product('Optimum Whey 900g - 100%')

	
	
-- get a brand by a product name
CREATE or replace FUNCTION get_a_brandby_product(prodname character varying)
RETURNS TABLE(brandname character varying) AS $$
declare
auxbrandid integer;
BEGIN
    select p.brandid
	from products p
	where p."name" = prodname
	into auxbrandid;		
	
	RETURN QUERY select b.name
				  FROM brands as b
				  where b.id = auxbrandid;
END;            
$$ LANGUAGE plpgsql;


select * from get_a_brandby_product('Optimum Whey 900g - 100%')




-- Get a Brand by product name

DO $$ BEGIN
    PERFORM get_brandandtype('Optimum Whey 900g - 100%');
END $$;

select * from get_brandandtype('Optimum Whey 900g - 100%') as f(bradname character varying, typename character varying);

--CHECK ITEM EXISTS IN SALEITEMS

CREATE OR REPLACE FUNCTION get_itemunitvalue(pname character varying)
RETURNS decimal(15,2) AS $func$
   SELECT  p.unitvalue iditem
      FROM products p
     WHERE p."name" = pname
     limit 1;
$func$ LANGUAGE sql;

drop function get_itemunitvalue


select * from get_itemunitvalue('Optimum Whey 900g - 100%')


CREATE or replace FUNCTION item_exists(currentsaleid integer,pname character varying)
RETURNS TABLE(hasitem boolean) AS $$
declare
	idprod integer;
BEGIN
    select id
    from products p
    where p."name" = pname
    into idprod;
	
	RETURN QUERY 
    select exists(select from saleitems as si
    				where si.saleid = currentsaleid and si.prodid = idprod );
END;            
$$ LANGUAGE plpgsql;

drop function item_exists(currentsaleid integer,pname character varying)

select * from products;

select * from item_exists(1,'Optimum Whey 900g - 100%');

-- GET CLIENTS
CREATE or replace FUNCTION get_clients()
RETURNS TABLE(name varchar(50)) AS $$
BEGIN
    RETURN QUERY select c.name
				  FROM clients as c;
END;            
$$ LANGUAGE plpgsql;

select * from get_clients();



select * from sales;

-- GET PRODUCTS
CREATE or replace FUNCTION get_products()
RETURNS TABLE(name varchar(50)) AS $$
BEGIN
    RETURN QUERY select p.name
				  FROM products as p;
END;            
$$ LANGUAGE plpgsql;


-- ADD A NEW SALE
CREATE OR REPLACE FUNCTION add_sale(	
	subtotal float,
	total float,
	clientname character varying,
	discout float,
	finalized char(1)
) 
    RETURNS void AS $$
    declare 
    	idclient integer;    	
    BEGIN
      	select c.id
      	from clients c
      	where c."name" = clientname
      	into idclient;      	
	    
	    INSERT INTO sales(
	   	saledate,		
		subtotal,
		total,
		clientid,
		discout,
		finalized) 
	    VALUES (
	    current_timestamp,	    
	    subtotal,
	    total,
	    idclient,
	    discout,
	    finalized
		);
    END;
    $$ LANGUAGE plpgsql;
    
    -- Execute store procedure    
DO $$ BEGIN
    PERFORM add_sale(100.0,50.0,'vinicius',0.5, 'F');
END $$;


--RESTART SEQUENCES
ALTER SEQUENCE sales_id_seq RESTART WITH 1

select * from sales;

select * from saleitems;

CREATE or replace FUNCTION checkInvoiceIDAlreadyExists(iputFiscalNote character varying)
RETURNS TABLE(id integer) AS $$
BEGIN
    RETURN QUERY select s.id 
				  FROM sales as s
				  where s.invoice = iputFiscalNote;
END;            
$$ LANGUAGE plpgsql;

select * from checkInvoiceIDAlreadyExists('123456489');

--------------------------------------------------------------------
--						ADD A PRODUCT PROCEDURES
-- GET BRANDS
CREATE or replace FUNCTION get_brands()
RETURNS TABLE(name varchar(50)) AS $$
BEGIN
    RETURN QUERY select b.name
				  FROM brands as b;
END;            
$$ LANGUAGE plpgsql;


CREATE OR REPLACE VIEW getbrands AS
    SELECT name
    FROM brands;
    
    select * from getbrands

-- GET TYPES

CREATE or replace FUNCTION get_types()
RETURNS TABLE(name varchar(50)) AS $$
BEGIN
    RETURN QUERY select t.name
				  FROM types as t;
END;            
$$ LANGUAGE plpgsql;

CREATE OR REPLACE VIEW gettypes AS
    SELECT name
    FROM types;
    
    select * from getbrands



CREATE or replace FUNCTION get_products()
RETURNS TABLE(
productname character varying,
brandname character varying,
typename  character varying ,
minimumquantity integer,
currentquantity integer,
unitvalue decimal(15,2),
unittype varchar(30)) AS $$
BEGIN    
	RETURN QUERY select p."name",b."name",t."name",p.minimumquantity,p.currentquantity,p.unitvalue,p.unittype
				  FROM products p
				  join types t on p.typeid = t.id
				  join brands b on p.brandid = b.id
				  limit 50;				  
END;            
$$ LANGUAGE plpgsql;

select * from get_products();


CREATE or replace FUNCTION get_a_product(pname varchar(50))
RETURNS TABLE(
productname character varying,
brandname character varying,
typename  character varying ,
minimumquantity integer,
currentquantity integer,
unitvalue decimal(15,2),
unittype varchar(30)) AS $$
BEGIN    
	RETURN QUERY select p."name",b."name",t."name",p.minimumquantity,p.currentquantity,p.unitvalue,p.unittype
				  FROM products p
				  join types t on p.typeid = t.id
				  join brands b on p.brandid = b.id
				  where p."name" = pname;
END;            
$$ LANGUAGE plpgsql;


drop function get_products

select * from get_products();

select * from products;

select * from products
where "name" = 

select p."name" productname,b."name" brandname, t."name" typename ,p.minimumquantity,p.currentquantity,p.unitvalue,p.unittype  
  FROM products p
  join types t on p.typeid = t.id
  join brands b on p.brandid = b.id;


CREATE or replace FUNCTION product_exists(pname character varying, pbrand character varying)
RETURNS TABLE(hasproduct boolean) AS $$
declare 
	productbrand INTEGER;
BEGIN
    select id
    from brands b
    where b."name" = pbrand
    into productbrand;
    
	RETURN QUERY    
    select exists(select from products as p
    				where p."name" = pname and p.brandid = productbrand);
END;            
$$ LANGUAGE plpgsql;


select * from products;

select * from brands;

select * from product_exists('SuperWhey','Optimum')

CREATE or replace FUNCTION product_exists(pname character varying)
RETURNS TABLE(hasproduct boolean) AS $$
BEGIN
    RETURN QUERY 
    select exists(select from products as p
    				where p."name" = pname );
END;            
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_product(
	pname character varying,
	brandname character varying,
	typename character varying ,
	minimumquantity integer,
	currentquantity integer,
	unitvalue decimal(15,2),
	unittype character varying
) 
    RETURNS void AS $$
    declare 
    	idbrand integer;
    	idtype integer;
    BEGIN
      	select b.id
      	from brands b
      	where b."name" = brandname
      	into idbrand;
      	
      	select t.id
      	from types t
      	where t."name" = typename
      	into idtype;
	    
	    INSERT INTO products(
	    name,
		brandid,
		typeid,
		minimumquantity,
		currentquantity,
		unitvalue,
		unittype) 
	    VALUES (
	    pname,
	    idbrand,
	    idtype,
	    minimumquantity,
		currentquantity,
		unitvalue,
		unittype 
		);
    END;
    $$ LANGUAGE plpgsql;
    
-- Execute store procedure    
DO $$ BEGIN
    PERFORM add_product('Ultra Whey','ProStar','Protein Bar',20,200,150.00,'900g');
END $$;


    
select * from brands;

select * from products;
    
add_product('Ultra Whey','Prostar','Protein Bar',20,200,150.00,'900g');

select * from products;

select * from types;


select hasproduct from product_exists('Optimum Whey 900g - 100%');


select * from products;

select * from get_brands();

select name from get_types()


----------------------------------------------------------------
--				search purchases

create or replace view  searchpurchases as
select s.fantasyname, p.invoice, p.subtotal, p.discout , p.total, purchasedate
from purchases p
inner join suppliers s on p.supplierid = s.id
where p.finalized = 'Y';


select * from searchpurchases;

CREATE or replace FUNCTION search_a_purchase(inputfantasyname character varying)
RETURNS TABLE(
fantasyname character varying,
invoice char(9),
subtotal decimal(15,2),
discount decimal(15,2),
total decimal(15,2),
purchasedate timestamp 
) AS $$
declare 
	idsupplier INTEGER;
BEGIN
    select id
    from suppliers s
    where s.fantasyname = inputfantasyname
    into idsupplier;
    
	RETURN QUERY    
    select s.fantasyname, p.invoice, p.subtotal, p.discout, p.total, p.purchasedate
    from purchases p
    inner join suppliers s on p.supplierid = s.id
    where p.supplierid = idsupplier and p.finalized = 'Y';
END;            
$$ LANGUAGE plpgsql;

drop function search_a_purchase(inputfantasyname character varying);

select * from suppliers;

select * from search_a_purchase('batataDoceSales');


----------------------------------------------------------------
--				search sales

create or replace view  searchsales as
select c."name", s.invoice, s.subtotal, s.discout , s.total, s.saledate
from sales s
inner join clients c on s.clientid = c.id
where s.finalized = 'Y';

drop view searchsales;

select * from searchpurchases;

CREATE or replace FUNCTION search_a_sale(input_clientname character varying)
RETURNS TABLE(
clientname character varying,
invoice char(9),
subtotal DECIMAL(15,2),
discount DECIMAL(15,2),
total DECIMAL(15,2),
saledate timestamp 
) AS $$
declare 
	idclient INTEGER;
BEGIN
    select id
    from clients c
    where c."name" = input_clientname
    into idclient;
    
	RETURN QUERY    
    select c."name", s.invoice, s.subtotal, s.discout, s.total, s.saledate
    from sales s
    inner join clients c on s.clientid = c.id
    where s.clientid = idclient and s.finalized = 'Y';
END;            
$$ LANGUAGE plpgsql;



drop function search_a_sale(input_clientname character varying);

select * from sales;

select * from search_a_sale('vinicius');
