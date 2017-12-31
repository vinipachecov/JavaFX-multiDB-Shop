-- DB PROCEDURES FOR FIREBIRD

-------------------------------------------------------------
--				ADD A PRODUCT


-- GET BRANDS
CREATE OR ALTER VIEW getbrands(
      NAME)
  AS
  SELECT name
  FROM BRANDS
  
  SELECT * FROM BRANDS;
  
  SELECT name FROM getbrands
  
  SELECT first 1 id
  FROM BRANDS b
  WHERE b.NAME = 'Optimum'

  
-- GET TYPES 
  CREATE OR ALTER VIEW gettypes(
      NAME)
  AS
  SELECT name
  FROM TYPES
  
  SELECT name FROM gettypes
  
 
  
  EXECUTE PROCEDURE SP_ADDPRODUCT('Whey 500%','Optimum','Whey Protein',0,0,150,'900g')
  
  
  --ADD A PRODUCT
  CREATE OR ALTER PROCEDURE SP_ADDPRODUCT(  
	PNAME D_NAME,
	brandname D_NAME,
	typename D_NAME,
	minimumquantity D_INT ,
	currentquantity D_INT ,
	unitvalue D_DECIMAL ,
	unittype D_UNIT) 
  AS  	
  DECLARE VARIABLE IDBRAND D_INT;
  DECLARE VARIABLE IDTYPE D_INT;
  	BEGIN	  	
	  	SELECT first 1 id 
	  	FROM BRANDS B
	  	WHERE B.NAME = :BRANDNAME
	  	INTO :IDBRAND;
	  	
	  	SELECT first 1 id
	  	FROM TYPES t
	  	WHERE t.NAME = :typename
	  	INTO :IDTYPE;	  	
  	
  		INSERT INTO PRODUCTS(NAME,BRANDID,TYPEID,MINIMUMQUANTITY,CURRENTQUANTITY,UNITVALUE,UNITTYPE)
  		VALUES(:PNAME, :IDBRAND, :IDTYPE, :minimumquantity, :currentquantity, :unitvalue, :unittype );  		
  	 END
  	 


	
SELECT * FROM PRODUCTS

SELECT FIRST 1 ID
FROM PRODUCTS
WHERE NAME = :PNAME

CREATE OR ALTER PROCEDURE hasproduct(PNAME D_NAME)
RETURNS (hasproduct D_INT)
AS
BEGIN 
	SELECT FIRST 1 ID
	FROM PRODUCTS P
	WHERE P.NAME = :PNAME
	INTO :HASPRODUCT;
END 


CREATE OR ALTER PROCEDURE hasproduct(PNAME D_NAME, BRANDNAME D_NAME)  
AS  	
DECLARE VARIABLE BRANDID D_INT;
BEGIN
	  
	  SELECT FIRST 1 ID 
	  FROM BRANDS B
	  WHERE B.NAME = :BRANDNAME
	  INTO :BRANDID;	  
 
	  IF ( EXISTS (SELECT FIRST 1 P.NAME
	  			   FROM PRODUCTS P	  			   	  			   
	  			   WHERE P.NAME = :PNAME AND P.BRANDID = :BRANDID ))	  			   
	  			   THEN	  			   
			   	EXCEPTION E_PRODUTO_IGUAL;
  END 

  SELECT * FROM PRODUCTS;
  
EXECUTE PROCEDURE hasproduct('Whey 100%','ProStar')   	 
  	

---------------------------------------------------------------------
--				SEARCH PRODUCTS


CREATE OR ALTER PROCEDURE get_a_product(prodname D_NAME)
RETURNS (
pname D_NAME,
brandname D_NAME,
typename D_NAME,
minimumquantity D_INT,
currentquantity D_INT,
unitvalue D_DECIMAL,
unittype D_UNIT
)
  AS  
  BEGIN
	  SELECT  p.NAME, b.NAME, t.NAME, p.MINIMUMQUANTITY, p.CURRENTQUANTITY, p.UNITVALUE, p.UNITTYPE
	  FROM PRODUCTS p
	  INNER JOIN BRANDS B ON p.BRANDID = b.ID
	  INNER JOIN TYPES T ON p.TYPEID = t.ID
	  WHERE p.NAME = :prodname
	  INTO :pname, :brandname, :typename, :minimumquantity, :currentquantity, :unitvalue, :unittype;
  END
   	
  EXECUTE PROCEDURE get_a_product('Whey 100%')

CREATE OR ALTER VIEW search_products(pname,brandname,typename,minimumquantity,currentquantity,unitvalue,unittype)
AS
SELECT  p.NAME, b.NAME, t.NAME, p.MINIMUMQUANTITY, p.CURRENTQUANTITY, p.UNITVALUE, p.UNITTYPE
FROM PRODUCTS p
INNER JOIN BRANDS B ON p.BRANDID = b.ID
INNER JOIN TYPES T ON p.TYPEID = t.ID

SELECT * FROM search_products;
 
CREATE OR ALTER VIEW getbrands(
      NAME)
  AS
  SELECT name
  FROM BRANDS
  	 
  
---------------------------------------------------------------------
--				ADD A SALE
  
  DROP VIEW get_clients;
  
  
  
CREATE OR ALTER VIEW get_clients(cname)
AS
SELECT  c.NAME 
FROM CLIENTS c
  
SELECT cname FROM get_clients


CREATE OR ALTER PROCEDURE addSale(subtotal D_DECIMAL,
	total D_DECIMAL,
	clientname D_NAME,
	discount D_DECIMAL,
	finalized D_FINALIZED)
  AS  
  DECLARE VARIABLE idclient D_INT;
  BEGIN
	  SELECT c.ID
	  FROM CLIENTS C
	  WHERE C.NAME = :clientname
	  INTO :idclient;
	  
	  
	  INSERT INTO SALES(SALEDATE,SUBTOTAL,TOTAL,CLIENTID,DISCOUNT,FINALIZED)
	  VALUES(CURRENT_TIMESTAMP, :SUBTOTAL, :total, :idclient, :discount, :finalized);
  END
  
  
  SELECT * FROM CLIENTS;
  
  EXECUTE PROCEDURE addSale(0.0,0.0,'Vinicius',0.0,'N')
  
  SELECT * FROM sales;
  
  
  SELECT max(id) FROM sales;
  
  CREATE EXCEPTION INVOICEEXISTS 'Invoice already exists!';

    
  -- CHECK IF INVOICE ALREADY EXISTS
  CREATE OR ALTER PROCEDURE checkInvoiceExists(inputinvoice D_INVOICE)  
  AS  
  BEGIN
	 IF(EXISTS (SELECT id
	 FROM sales s 
	 WHERE s.INVOICE = :inputinvoice))THEN
	 EXCEPTION INVOICEEXISTS;
  END
  
  SELECT * FROM CLIENTS;
  
  EXECUTE PROCEDURE  checkInvoiceExists('987654321');
  
  
  SELECT * FROM SALES;
  
  SELECT * FROM PURCHASES;
  
  CREATE EXCEPTION NO_SALEID 'No sale found! Access Violation.';
  
    
  -- SET INVOICE TO THE SALE
  CREATE OR ALTER PROCEDURE setInvoice(idsale D_INT, inputinvoice D_INVOICE)  
  AS  
  BEGIN
	 IF( EXISTS(SELECT id
	 			FROM SALES s
	 			WHERE s.ID = :idsale))
	 			THEN
	 			BEGIN
		 			UPDATE SALES
	 				SET INVOICE = :inputinvoice
	 				WHERE id = :idsale;
	 			END 
  				ELSE
  				EXCEPTION NO_SALEID;	 
  END
  
  SELECT * FROM sales;
  
  EXECUTE PROCEDURE setInvoice(2 , '987654321');
  
  
  
  
  CREATE OR ALTER PROCEDURE finishSale(saleid D_INT,  input_subtotal D_DECIMAL, input_total D_DECIMAL)  
  AS  
  BEGIN
	 IF( EXISTS(SELECT id
	 			FROM SALES s
	 			WHERE s.ID = :saleid))
	 			THEN
	 			BEGIN
		 			UPDATE SALES
	 				SET FINALIZED = 'Y',SUBTOTAL = :input_subtotal, TOTAL = :input_total
	 				WHERE id = :saleid;
	 			END 
  				ELSE
  				EXCEPTION NO_SALEID;	 
  END
  
  SELECT * FROM sales;
  
  EXECUTE PROCEDURE finishSale(1 , 50.0,100.0);
  
  
    
    
  CREATE OR ALTER PROCEDURE removeSale(currentSaleID D_INT)
  AS  
  BEGIN
	DELETE FROM saleitems
	WHERE SALEID = :currentSaleID;
	
	DELETE FROM SALES
	WHERE ID = :currentSaleID;	
  END 
  
  SELECT * FROM saleitems;
  
  SELECT * FROM ;
	 
  EXECUTE PROCEDURE removeSale(1);
  
  
  
---------------------------------------------------------------------
--				ADD A SALEITEM
  
  
  
-- ADD ITEM PURCHASE	
CREATE OR ALTER PROCEDURE add_saleitem( 
	saleid_input D_INT,
	prodname_input D_NAME,
	unitvalue_input D_DECIMAL,
	quantity_input D_INT,
	total_input D_DECIMAL)
  AS  
  DECLARE IDPROD D_INT;
  BEGIN	  
	 IF( EXISTS(SELECT p.ID
	 			FROM PRODUCTS p
	 			WHERE p.NAME = :prodname_input))
	 			THEN
	 			BEGIN		 			
		 			SELECT p.ID
	 				FROM PRODUCTS p
	 				WHERE p.NAME = :prodname_input
	 				INTO :IDPROD;
		 			
		 			INSERT INTO SALEITEMS(SALEID ,PRODID,UNITVALUE,QUANTITY,TOTAL)
		 			VALUES (:saleid_input, :IDPROD, :unitvalue_input, :quantity_input, :total_input );
	 			END 
  				ELSE
  				EXCEPTION NO_PRODUCT;	 
  END	
  
  SELECT * FROM sales;
  
  SELECT * FROM ;
  
  EXECUTE PROCEDURE add_saleitem(2,'Whey 100%', 150.0, 10, 1500);
  
  CREATE EXCEPTION SIT_ALREADYADDED 'Sale Item already Added!';
  
    DROP PROCEDURE saleItem_exists;
  
  -- CHECK IF SALEITEM HAS NOT BEEN ADDED
  CREATE OR ALTER PROCEDURE saleItem_exists(currentSaleID D_INT, pname D_NAME)
  AS  
  DECLARE IDPROD D_INT;
  BEGIN	  
	 IF( EXISTS(SELECT p.ID
	 			FROM PRODUCTS p
	 			WHERE p.NAME = :pname))
	 			THEN
	 			BEGIN		 			
		 			SELECT p.ID
	 				FROM PRODUCTS p
	 				WHERE p.NAME = :pname
	 				INTO :IDPROD;
		 			
		 			IF( EXISTS (SELECT UNITVALUE
		 			FROM  SALEITEMS sit
		 			WHERE sit.PRODID = :IDPROD AND sit.SALEID = :currentSaleID))
		 			THEN
		 				EXCEPTION SIT_ALREADYADDED;
	 			END 
  				ELSE
  				EXCEPTION NO_PRODUCT;	 
  END	
  
  
  EXECUTE PROCEDURE saleItem_exists(2,'Whey 100%');
  
  EXECUTE PROCEDURE saleItem_exists(10,'Whey 100%');
  
  SELECT * FROM sales;
  
  SELECT * FROM ;
---------------------------------------------------------------------
--				ADD A PURCHASE
  
  
  
  
CREATE OR ALTER VIEW get_suppliers(cname)
AS
SELECT  s.FANTASYNAME 
FROM SUPPLIERS s
  
SELECT cname FROM get_suppliers;


CREATE EXCEPTION NO_SUPPLIER 'No Supplier Found!';


--ADD A PURCHASE
CREATE OR ALTER PROCEDURE addPurchase(subtotal D_DECIMAL,
	total D_DECIMAL,
	suppliername D_NAME,
	discount D_DECIMAL,
	finalized D_FINALIZED)
  AS  
  DECLARE VARIABLE idsupplier D_INT;
  BEGIN
	  IF( EXISTS (SELECT s.ID
	  FROM SUPPLIERS s
	  WHERE s.FANTASYNAME = :suppliername ))
	  THEN
	  BEGIN
		  SELECT s.ID
		  FROM SUPPLIERS s
		  WHERE s.FANTASYNAME = :suppliername
		  INTO idsupplier;
		  		  
		  INSERT INTO PURCHASES(PURCHASEDATE,SUBTOTAL,TOTAL,SUPPLIERID,DISCOUNT,FINALIZED)
		  VALUES(CURRENT_TIMESTAMP, :SUBTOTAL, :total, :idsupplier, :discount, :finalized);
	  END 
	  ELSE
	  EXCEPTION NO_SUPPLIER;	  
  END
  
  SELECT * FROM SUPPLIERS;
  
  
  EXECUTE PROCEDURE addPurchase(0.0,0.0,'Batata Doce Suplementos',0.0,'N');
  
  SELECT * FROM PURCHASES;
  
  
  
  CREATE OR ALTER PROCEDURE checkPurchaseInvoiceExists(inputinvoice D_INVOICE)  
  AS  
  BEGIN
	 IF(EXISTS (SELECT id
	 FROM PURCHASES p 
	 WHERE p.INVOICE = :inputinvoice))THEN
	 EXCEPTION INVOICEEXISTS;
  END
  
  SELECT * FROM PURCHASES;
  
  EXECUTE PROCEDURE checkPurchaseInvoiceExists('123456799');
  
  CREATE EXCEPTION NO_PURCHASEID 'No Purchase with this id has been found! Access Violation.';
  
  CREATE OR ALTER PROCEDURE finishPurchase(purchaseID D_INT,  input_subtotal D_DECIMAL, input_total D_DECIMAL)  
  AS  
  BEGIN
	 IF( EXISTS(SELECT id
	 			FROM PURCHASES P
	 			WHERE P.ID = :purchaseID))
	 			THEN
	 			BEGIN
		 			UPDATE PURCHASES
	 				SET FINALIZED = 'Y',SUBTOTAL = :input_subtotal, TOTAL = :input_total
	 				WHERE id = :purchaseID;
	 			END 
  				ELSE
  				EXCEPTION NO_PURCHASEID;	 
  END
  
  SELECT * FROM PURCHASES;
  
  EXECUTE PROCEDURE finishPurchase(5,100,150);
  
  -- SET INVOICE
  CREATE OR ALTER PROCEDURE setInvoicePurchase(idpurchase D_INT, inputinvoice D_INVOICE)  
  AS  
  BEGIN
	 IF( EXISTS(SELECT id
	 			FROM PURCHASES p
	 			WHERE p.ID = :idpurchase))
	 			THEN
	 			BEGIN
		 			UPDATE PURCHASES
	 				SET INVOICE = :inputinvoice
	 				WHERE id = :idpurchase;
	 			END 
  				ELSE
  				EXCEPTION NO_PURCHASEID;	 
  END
  
  SELECT * FROM PURCHASES;
  
  EXECUTE PROCEDURE setInvoicePurchase(2 , '987654322');
  
  SELECT * FROM sales;
  
  DROP PROCEDURE 
  
  -- REMOVE SALE AND IT'S purchaseItems
   CREATE OR ALTER PROCEDURE removePurchase(currentPurchaseID D_INT)
  AS  
  BEGIN
	DELETE FROM PURCHASEITEMS
	WHERE PURCHASEID = :currentPurchaseID;
	
	DELETE FROM PURCHASES
	WHERE ID = :currentPurchaseID;	
  END 
  
  SELECT * FROM PURCHASES;
  
  SELECT * FROM PURCHASEITEMS;
  
  EXECUTE PROCEDURE removePurchase(2);
  
---------------------------------------------------------------------
--				ADD A PURCHASEITEM

  DROP VIEW get_products_additem;
  
-- VIEW TO GET PRODUCTS
CREATE OR ALTER VIEW get_products_additem(productname)
AS
SELECT  P.NAME 
FROM PRODUCTS P
  
SELECT productname FROM get_products_purchaseitem;


CREATE EXCEPTION NO_PRODUCT 'No product registered with this Name!';

CREATE OR ALTER PROCEDURE get_itemunitvalue(pname D_NAME)
RETURNS (unitvalue D_DECIMAL)
  AS  
  BEGIN
	 IF( EXISTS(SELECT id
	 			FROM PRODUCTS p
	 			WHERE p.NAME = :pname))
	 			THEN
	 			BEGIN
		 			SELECT p.UNITVALUE 
		 			FROM PRODUCTS p
		 			WHERE p.NAME = :pname
		 			INTO :unitvalue;
	 			END 
  				ELSE
  				EXCEPTION NO_PRODUCT;	 
  END
  
  SELECT * FROM PRODUCTS;
  
  EXECUTE PROCEDURE get_itemunitvalue('Whey 100%');
  
  
  
  

  
CREATE OR ALTER PROCEDURE get_a_typeby_product(pname D_NAME)
RETURNS (typename D_NAME)
  AS  
  DECLARE IDTYPE D_INT;
  BEGIN	  
	 IF( EXISTS(SELECT id
	 			FROM PRODUCTS p
	 			WHERE p.NAME = :pname))
	 			THEN
	 			BEGIN
		 			SELECT p.TYPEID
					FROM PRODUCTS P
					WHERE p.NAME = :pname
					INTO :IDTYPE;
		 			
		 			SELECT t.NAME
		 			FROM TYPES t
		 			WHERE t.ID = :IDTYPE
		 			INTO :typename;
	 			END 
  				ELSE
  				EXCEPTION NO_PRODUCT;	 
  END
  
  EXECUTE PROCEDURE get_a_typeby_product('Whey 100%')
  
  
CREATE OR ALTER PROCEDURE get_a_brandby_product(pname D_NAME)
RETURNS (brandname D_NAME)
  AS  
  DECLARE IDBRAND D_INT;
  BEGIN	  
	 IF( EXISTS(SELECT id
	 			FROM PRODUCTS p
	 			WHERE p.NAME = :pname))
	 			THEN
	 			BEGIN
		 			SELECT p.BRANDID
					FROM PRODUCTS P
					WHERE p.NAME = :pname
					INTO :IDBRAND;
		 			
		 			SELECT b.NAME
		 			FROM BRANDS b
		 			WHERE b.ID = :IDBRAND
		 			INTO :brandname;
	 			END 
  				ELSE
  				EXCEPTION NO_PRODUCT;	 
  END
  
  
  EXECUTE PROCEDURE get_a_brandby_product('Whey 100%'); 
 
	SELECT * FROM PURCHASES;
  
-- ADD ITEM PURCHASE	
CREATE OR ALTER PROCEDURE add_purchaseitem( 
	purchaseid_input D_INT,
	prodname_input D_NAME,
	unitvalue_input D_DECIMAL,
	quantity_input D_INT,
	total_input D_DECIMAL)
  AS  
  DECLARE IDPROD D_INT;
  BEGIN	  
	 IF( EXISTS(SELECT p.ID
	 			FROM PRODUCTS p
	 			WHERE p.NAME = :prodname_input))
	 			THEN
	 			BEGIN		 			
		 			SELECT p.ID
	 				FROM PRODUCTS p
	 				WHERE p.NAME = :prodname_input
	 				INTO :IDPROD;
		 			
		 			INSERT INTO PURCHASEITEMS(purchaseid ,PRODID,UNITVALUE,QUANTITY,TOTAL)
		 			VALUES (:purchaseid_input, :IDPROD, :unitvalue_input, :quantity_input, :total_input );
	 			END 
  				ELSE
  				EXCEPTION NO_PRODUCT;	 
  END	
  
  SELECT * FROM sales;
  
  DROP  PROCEDURE add_purchaseitem;
  
  
  
  
  EXECUTE PROCEDURE purchaseItem_exists (2,'Top Whey',150,10,1500.0);
  
  
  CREATE EXCEPTION PIT_ALREADYADDED 'PurchaseItem already Added!';
  
  -- CHECK IF PURCHASE ITEM HAS NOT BEEN ADDED	
CREATE OR ALTER PROCEDURE purchaseItem_exists(currentPurchaseID D_INT, pname D_NAME)
  AS  
  DECLARE IDPROD D_INT;
  BEGIN	  
	 IF( EXISTS(SELECT p.ID
	 			FROM PRODUCTS p
	 			WHERE p.NAME = :pname))
	 			THEN
	 			BEGIN		 			
		 			SELECT p.ID
	 				FROM PRODUCTS p
	 				WHERE p.NAME = :pname
	 				INTO :IDPROD;
		 			
		 			IF( EXISTS (SELECT UNITVALUE
		 			FROM PURCHASEITEMS pit
		 			WHERE pit.PRODID = :IDPROD AND pit.PURCHASEID = :currentPurchaseID))
		 			THEN
		 				EXCEPTION PIT_ALREADYADDED;
	 			END 
  				ELSE
  				EXCEPTION NO_PRODUCT;	 
  END	
  
  SELECT * FROM PRODUCTS;
  
  SELECT * FROM PURCHASEITEMS;
  
  SELECT * FROM PURCHASES;
  
  EXECUTE PROCEDURE purchaseItem_exists(1,'Whey 100%');
  
  CREATE OR ALTER PROCEDURE removePurchase(currentPurchaseID D_INT)
  AS  
  BEGIN
	DELETE FROM PURCHASEITEMS
	WHERE PURCHASEID = :currentPurchaseID;
	
	DELETE FROM PURCHASES
	WHERE ID = :currentPurchaseID;	
  END 
	 
  EXECUTE PROCEDURE removePurchase(1);
  
  SELECT * FROM PURCHASES;
  
  SELECT * FROM PURCHASEITEMS;
  
------------------------------------------------------------
--				SEARCH PURCHASE
  
  -- SEARCH PURCHASES
  CREATE OR ALTER VIEW searchPurchase(fantasyname,invoice,subtotal, discount , total,purchasedate   )
  AS
  SELECT s.FANTASYNAME, p.INVOICE, p.SUBTOTAL,  p.DISCOUNT, p.TOTAL ,p.PURCHASEDATE
  FROM PURCHASES p 
  INNER JOIN SUPPLIERS s ON p.SUPPLIERID = s.ID
  WHERE p.FINALIZED = 'Y'
  
  SELECT * FROM PURCHASES;
  
  SELECT FIRST 50 * FROM searchPurchase; 
  
  
  -- SEARCH a SPECIFIC PURCHASE
  CREATE OR ALTER PROCEDURE search_a_purchase(input_fantasyName D_NAME)
  RETURNS (
  fantasyname D_NAME,
  invoice D_INVOICE,
  subtotal D_DECIMAL,
  discount D_DECIMAL,
  total D_DECIMAL,    
  purchasedate D_DATE
  )
  AS  
  DECLARE VARIABLE idSupplier D_INT;
  BEGIN
	 SELECT FIRST 1 ID
	 FROM SUPPLIERS
	 WHERE FANTASYNAME = :input_fantasyName
	 INTO :idSupplier;	  
	  
	
	FOR SELECT s.FANTASYNAME, p.INVOICE, p.SUBTOTAL, p.DISCOUNT, p.TOTAL, p.PURCHASEDATE
	FROM PURCHASES P
	INNER JOIN SUPPLIERS S ON P.SUPPLIERID = S.ID
	WHERE P.SUPPLIERID = :idSupplier	AND p.FINALIZED = 'Y'	 	
	INTO :fantasyname, :invoice, :subtotal, :discount, :total, :purchasedate
	DO 
	SUSPEND;
  END
  
 SELECT * FROM SUPPLIERS;
  
  
  SELECT * FROM search_a_purchase('Batata Doce Suplementos');
  
  
------------------------------------------------------------
--				SEARCH SALE
  
  
  
  DROP VIEW searchSale;
   -- SEARCH PURCHASES
  CREATE OR ALTER VIEW searchSale(clientname,invoice,subtotal, discount , total, saledate   )
  AS
  SELECT c.NAME, s.INVOICE, s.SUBTOTAL,  s.DISCOUNT, s.TOTAL ,s.SALEDATE
  FROM SALES s 
  INNER JOIN CLIENTS c ON s.CLIENTID = c.ID
  WHERE s.FINALIZED = 'Y'
  
  SELECT * FROM SALES;
  
  SELECT FIRST 50 * FROM searchSale; 
  
  DROP PROCEDURE search_a_sale;
  
  DROP PROCEDURE search_a_sale;
  
  -- SEARCH a SPECIFIC PURCHASE
  
  CREATE OR ALTER PROCEDURE search_a_sale(input_clientname D_NAME)
  RETURNS (
  clientname D_NAME,
  invoice D_INVOICE,
  subtotal D_DECIMAL,
  discount D_DECIMAL,
  total D_DECIMAL,    
  saledate D_DATE
  )
  AS  
  DECLARE VARIABLE idClient D_INT;
  DECLARE VARIABLE idSale D_INT;
  BEGIN
	 SELECT FIRST 1 ID
	 FROM CLIENTS
	 WHERE NAME = :input_clientname
	 INTO :idClient;	  
	
	FOR SELECT c.NAME, s.INVOICE, s.SUBTOTAL, s.DISCOUNT, s.TOTAL, s.SALEDATE
	FROM SALES s
	INNER JOIN CLIENTS c ON s.CLIENTID = c.ID
	WHERE s.FINALIZED = 'Y'	AND S.CLIENTID = :idClient 	
	INTO :clientname, :invoice, :subtotal, :discount, :total, :saledate
	DO 
		SUSPEND;
  END
  
  SELECT * FROM CLIENTS;
  
  SELECT * FROM  SALES;
  
  SELECT * FROM  search_a_sale('Vinicius');
  
  
  ------------------------------------------------------------
--				CHECK HISTORY
  

  CREATE EXCEPTION NO_SALE_OR_PURCHASE 'No sale or Purchase found with given date interval';
  
 
  
  
    CREATE OR ALTER PROCEDURE checkAllHistory
  RETURNS (
  tipo D_NAME,
  productname D_NAME,
  invoice D_INVOICE,
  quantity D_INT,
  unitvalue D_DECIMAL,
  total D_DECIMAL,    
  actiondate D_DATE
  )
  AS   
  DECLARE VARIABLE idsale D_INT;
  DECLARE VARIABLE idItem D_INT; 
  DECLARE VARIABLE idpurchase D_INT;
  BEGIN
	 IF( EXISTS (SELECT ID
	 			  FROM SALES s
	 			  WHERE s.FINALIZED = 'Y' 
	 			  ) OR EXISTS(SELECT ID 
				 			  FROM PURCHASES p
				 			  WHERE p.FINALIZED = 'Y' ))
										 			  THEN
	 			  						 			  BEGIN
		 			  						 			FOR SELECT S.ID, sit.PRODID
	 			  								      	FROM SALES s
	 			  								      	INNER JOIN SALEITEMS sit ON s.ID = sit.SALEID
	 			  								      	WHERE s.FINALIZED = 'Y' 
 			  										  	INTO :idsale, :idItem
 			  										  	DO 			  										  	
 			  										  		BEGIN 
	 			  										  		FOR SELECT DISTINCT s.INVOICE, prod.NAME ,sit.QUANTITY, sit.UNITVALUE, sit.TOTAL, s.SALEDATE
 			  										  	 		FROM SALEITEMS sit
 			  										  			INNER JOIN PRODUCTS prod ON sit.PRODID = prod.ID
 			  										  	 		INNER JOIN SALES s ON sit.SALEID = s.ID		  										  			
		  										  				WHERE sit.SALEID = :idsale AND sit.PRODID = :idItem
		  										  				INTO :invoice, :productname, :quantity, :unitvalue, :total, :actiondate
		  										  				DO 
			  										  				tipo = 'Sale';			  										  			
		  										  				SUSPEND;
 			  										  		END
 			  										  		
		  										  		FOR SELECT p.ID, pit.PRODID
		  										  		FROM PURCHASES p
		  										  		INNER JOIN PURCHASEITEMS pit ON p.ID = pit.PURCHASEID
		  										  		WHERE p.FINALIZED = 'Y' 
		  										  		INTO :idpurchase, :idItem
		  										  		DO 
		  										  			BEGIN
			  										  			FOR SELECT DISTINCT p.INVOICE, prod.NAME, pit.QUANTITY, pit.UNITVALUE, pit.TOTAL, p.PURCHASEDATE 
			  										  			FROM PURCHASEITEMS pit
			  										  			INNER JOIN PRODUCTS prod ON pit.PRODID = prod.ID
			  										  			INNER JOIN PURCHASES p ON pit.PURCHASEID = p.ID
			  										  			WHERE pit.PURCHASEID = :idpurchase AND pit.PRODID = :idItem
			  										  			INTO :invoice, :productname, :quantity, :unitvalue, :total, :actiondate
			  										  			DO
			  										  				tipo = 'Purchase';
			  										  				SUSPEND;
		  										  			END 
		  										  	  END		  										  	  
	 			  						 			  ELSE
	 			  						 			  EXCEPTION NO_SALE_OR_PURCHASE;	 			  						 			  
  END  
  
  SELECT * FROM checkAllHistory;
  
  
  CREATE OR ALTER PROCEDURE checkHistoryRange(input_data1 D_DATE, input_data2 D_DATE)
  RETURNS (
  tipo D_NAME,
  productname D_NAME,
  invoice D_INVOICE,
  quantity D_INT,
  unitvalue D_DECIMAL,
  total D_DECIMAL,    
  actiondate D_DATE
  )
  AS   
  DECLARE VARIABLE idsale D_INT;
  DECLARE VARIABLE idItem D_INT; 
  DECLARE VARIABLE idpurchase D_INT;
  BEGIN
	 IF( EXISTS (SELECT ID
	 			  FROM SALES s
	 			  WHERE s.FINALIZED = 'Y' 
	 			  AND s.SALEDATE BETWEEN :input_data1 AND :input_data2) OR EXISTS(SELECT ID 
	 			  						 			  FROM PURCHASES p
	 			  						 			  WHERE p.FINALIZED = 'Y' AND p.PURCHASEDATE BETWEEN :input_data1 AND :input_data2 ))
	 			  						 			  THEN
	 			  						 			  BEGIN
		 			  						 			FOR SELECT S.ID, sit.PRODID
	 			  								      	FROM SALES s
	 			  								      	INNER JOIN SALEITEMS sit ON s.ID = sit.SALEID
	 			  									  	WHERE s.FINALIZED = 'Y' AND  s.SALEDATE BETWEEN :input_data1 AND :input_data2
 			  										  	INTO :idsale, :idItem
 			  										  	DO 			  										  	
 			  										  		BEGIN 
	 			  										  		FOR SELECT DISTINCT s.INVOICE, prod.NAME ,sit.QUANTITY, sit.UNITVALUE, sit.TOTAL, s.SALEDATE
 			  										  	 		FROM SALEITEMS sit
 			  										  			INNER JOIN PRODUCTS prod ON sit.PRODID = prod.ID
 			  										  	 		INNER JOIN SALES s ON sit.SALEID = s.ID		  										  			
		  										  				WHERE sit.SALEID = :idsale AND sit.PRODID = :idItem
		  										  				INTO :invoice, :productname, :quantity, :unitvalue, :total, :actiondate
		  										  				DO 
			  										  				tipo = 'Sale';			  										  			
		  										  				SUSPEND;
 			  										  		END
 			  										  		
		  										  		FOR SELECT p.ID, pit.PRODID
		  										  		FROM PURCHASES p
		  										  		INNER JOIN PURCHASEITEMS pit ON p.ID = pit.PURCHASEID
		  										  		WHERE p.FINALIZED = 'Y' AND p.PURCHASEDATE  BETWEEN :input_data1 AND :input_data2
		  										  		INTO :idpurchase, :idItem
		  										  		DO 
		  										  			BEGIN
			  										  			FOR SELECT DISTINCT p.INVOICE, prod.NAME, pit.QUANTITY, pit.UNITVALUE, pit.TOTAL, p.PURCHASEDATE 
			  										  			FROM PURCHASEITEMS pit
			  										  			INNER JOIN PRODUCTS prod ON pit.PRODID = prod.ID
			  										  			INNER JOIN PURCHASES p ON pit.PURCHASEID = p.ID
			  										  			WHERE pit.PURCHASEID = :idpurchase AND pit.PRODID = :idItem
			  										  			INTO :invoice, :productname, :quantity, :unitvalue, :total, :actiondate
			  										  			DO
			  										  				tipo = 'Purchase';
			  										  				SUSPEND;
		  										  			END 
		  										  	  END		  										  	  
	 			  						 			  ELSE
	 			  						 			  EXCEPTION NO_SALE_OR_PURCHASE;	 			  						 			  
  END  
----------------------------------------------------------------------------------------
--				Top Product Sales
  
  CREATE OR ALTER PROCEDURE listTopProductSales(input_date VARCHAR(4))
  RETURNS(
  prodname D_NAME,
  prodsales D_INT,
  prodsalecash D_DECIMAL,
  dateyear VARCHAR(4)
  )
  AS
  BEGIN
	  --PEGAR PRODUTOS MAIS VENDIDOS
	  	  FOR SELECT  SUM(sit.QUANTITY) NUMBERSALES, SUM(SIT.QUANTITY * SIT.UNITVALUE) TOTALCASH, p.NAME PRODUCT
		  FROM SALEITEMS sit
  		  INNER JOIN PRODUCTS p ON sit.PRODID = p.ID
  		  INNER JOIN SALES s ON sit.SALEID = s.ID
  		  WHERE EXTRACT(YEAR FROM s.SALEDATE) = :input_date
          GROUP BY sit.PRODID, p.NAME
          ORDER BY SUM(sit.QUANTITY) DESC
          INTO :prodsales, :prodsalecash, :prodname
          DO 
          BEGIN
          	dateyear = :input_date;
          	SUSPEND;
  		  END          
  END 
  
  
  SELECT * FROM  listTopProductSales('2017');
  
  SELECT * FROM SALEITEMS;
  
  SELECT * FROM PRODUCTS;
  
 
  
----------------------------------------------------------------------------------------
--				Worst Product Sales

  
  CREATE OR ALTER PROCEDURE listWorstProductSales(input_date VARCHAR(4))
  RETURNS(
  prodname D_NAME,
  prodsales D_INT,
  prodsalecash D_DECIMAL,
  dateyear VARCHAR(4)
  )
  AS
  BEGIN	  
	  	  FOR SELECT  SUM(sit.QUANTITY) NUMBERSALES, SUM(SIT.QUANTITY * SIT.UNITVALUE) TOTALCASH, p.NAME PRODUCT
		  FROM SALEITEMS sit
  		  INNER JOIN PRODUCTS p ON sit.PRODID = p.ID
  		  INNER JOIN SALES s ON sit.SALEID = s.ID
  		  WHERE EXTRACT(YEAR FROM s.SALEDATE) = :input_date
          GROUP BY sit.PRODID, p.NAME
          ORDER BY SUM(sit.QUANTITY) ASC
          INTO :prodsales, :prodsalecash, :prodname
          DO 
          BEGIN
          	dateyear = :input_date;
          	SUSPEND;
  		  END          
  END 
  
  
  SELECT * FROM  listWorstProductSales('2017');
  
  SELECT * FROM SALEITEMS;
  
  SELECT * FROM PRODUCTS;
  
 ---------------------------------------------------------------------
 --					CLIENTS THAT BUYS THE MOST
 
  
  CREATE OR ALTER PROCEDURE bestbuyers
  RETURNS(
  clientname D_NAME,
  numbuys D_INT,
  totalcash D_DECIMAL  
  )
  AS
  BEGIN	  
	  FOR SELECT c.NAME, COUNT(s.ID), SUM(s.TOTAL)
	  FROM SALES s
	  INNER JOIN CLIENTS c ON s.CLIENTID = c.ID
  	  WHERE s.FINALIZED = 'Y'
  	  GROUP BY c.NAME
  	  ORDER BY 3 DESC
  	  INTO :clientname, :numbuys, :totalcash
  	  DO
  	  	BEGIN
	  	  	SUSPEND;
  	  	END  	  	
  END 
  
  
  CREATE OR ALTER PROCEDURE client_spendings(input_clientname D_NAME)
  RETURNS(
  clientname D_NAME,
  numbuys D_INT,
  totalcash D_DECIMAL  
  )
  AS
  BEGIN	  
	  FOR SELECT c.NAME, COUNT(s.ID), SUM(s.TOTAL)
	  FROM SALES s
	  INNER JOIN CLIENTS c ON s.CLIENTID = c.ID
  	  WHERE s.FINALIZED = 'Y' AND c.NAME = :input_clientname
  	  GROUP BY c.NAME
  	  ORDER BY SUM(s.total) DESC
  	  INTO :clientname, :numbuys, :totalcash
  	  DO
  	  	BEGIN
	  	  	SUSPEND;
  	  	END  	  	
  END 
  
  SELECT * FROM client_spendings('Vinicius');
  
    SELECT * FROM bestbuyers;
    
  SELECT C.NAME, COUNT(s.ID), SUM(s.TOTAL)
  FROM SALES s
  INNER JOIN CLIENTS c ON s.CLIENTID = c.ID
  WHERE s.FINALIZED = 'Y'
  GROUP BY C.NAME
  
   	
  
  
  
-----------------------------------------------------------------------------
--					MOST REQUIRED SUPPLIERS 
 	
  
  
  CREATE OR ALTER PROCEDURE mostRequiredSupp
RETURNS(
suppliername D_NAME,
suppliertotalpurchase D_INT,
supplierpercentProductsTotal D_DECIMAL
)
AS
DECLARE VARIABLE idpurchase D_INT;
DECLARE VARIABLE idsupplier D_INT;
DECLARE VARIABLE totalproducts D_INT;
DECLARE VARIABLE totalproductsSupplier D_INT;
DECLARE VARIABLE totalproductsSupplierTemp D_INT;
BEGIN
	-- GET TOTAL PRODUCTS ITEMS	
	SELECT SUM(CURRENTQUANTITY)
	FROM PRODUCTS	
	INTO :totalproducts;	
	
	FOR SELECT S.FANTASYNAME ,SUM(pit.QUANTITY) NUMPURCHASES
	FROM SUPPLIERS s   
	INNER JOIN PURCHASES p ON p.SUPPLIERID = s.ID
	INNER JOIN PURCHASEITEMS pit ON p.ID = pit.PURCHASEID
	WHERE p.FINALIZED = 'Y'
	GROUP BY s.FANTASYNAME
	INTO :suppliername, :suppliertotalpurchase	
	DO
		BEGIN
			supplierpercentProductsTotal = (suppliertotalpurchase * 100) / :totalproducts;
			SUSPEND;
		END 
				
END 


CREATE OR ALTER PROCEDURE totalRequiredSupplier(input_suppliername D_NAME)
RETURNS(
suppliername D_NAME,
suppliertotalpurchase D_INT,
supplierpercentProductsTotal D_DECIMAL
)
AS
DECLARE VARIABLE idpurchase D_INT;
DECLARE VARIABLE idsupplier D_INT;
DECLARE VARIABLE totalproducts D_INT;
DECLARE VARIABLE totalproductsSupplier D_INT;
DECLARE VARIABLE totalproductsSupplierTemp D_INT;
BEGIN
	-- GET TOTAL PRODUCTS ITEMS	
	SELECT SUM(CURRENTQUANTITY)
	FROM PRODUCTS	
	INTO :totalproducts;	
	
	FOR SELECT S.FANTASYNAME ,SUM(pit.QUANTITY) NUMPURCHASES
	FROM SUPPLIERS s   
	INNER JOIN PURCHASES p ON p.SUPPLIERID = s.ID
	INNER JOIN PURCHASEITEMS pit ON p.ID = pit.PURCHASEID
	WHERE p.FINALIZED = 'Y' AND s.FANTASYNAME = :input_suppliername
	GROUP BY s.FANTASYNAME
	INTO :suppliername, :suppliertotalpurchase	
	DO
		BEGIN
			supplierpercentProductsTotal = (suppliertotalpurchase * 100) / :totalproducts;
			SUSPEND;
		END 
				
END 

SELECT * FROM mostRequiredSupp;


SELECT * FROM totalRequiredSupplier('VWsales');
-----------------------------------------------------------------------------
--					NEW CLIENTS THIS YEAR

CREATE OR ALTER PROCEDURE newclientsyear(input_year char(4))
RETURNS(
clientname D_NAME,
telephone D_TELEPHONE,
cJOINDATE D_DATE
)
AS
BEGIN
	FOR SELECT c.NAME,  c."TELEPHONE", c.JOINDATE 
	FROM CLIENTS c
	WHERE EXTRACT(YEAR FROM c.JOINDATE) = :input_year
	INTO :clientname, :TELEPHONE, :cjoindate
	DO
	BEGIN
		SUSPEND;
	END 
END 

SELECT * FROM newclientsyear('2017');

SELECT * FROM CLIENTS;

INSERT INTO CLIENTS(NAME,NUMSALES, "TELEPHONE",EMAIL,BIRTHDATE,GOVID,JOINDATE)
VALUES('teste',0,'55-9344203', 'liz@gmail','2015-02-03','123456789','2016-02-03')


 