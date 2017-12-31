

-----------------------------------------------------------------
--					TRIGGERS


-----------------------------------------------------------------
--				 INSERT SALEITEMS INTO HISTORY TRIGGER

CREATE TRIGGER H_SALES FOR SALEITEMS
ACTIVE AFTER INSERT OR UPDATE POSITION 5
AS
BEGIN
	INSERT INTO ACTIONHISTORY(prodid ,purchaseid , saleid , unitvalue , total , quantity, actiondate)
	VALUES(NEW.prodid,NULL,NEW.saleid,NEW.unitvalue,NEW.total,NEW.quantity, CURRENT_TIMESTAMP);	
END


------------------------------------------------------------------
--					UPDATE PRODUCT QUANTITY SALES

CREATE OR ALTER TRIGGER U_PRODQUANT FOR SALES
ACTIVE AFTER UPDATE POSITION 4
AS
DECLARE VARIABLE prodid D_INT;
DECLARE VARIABLE prodquant D_INT;
DECLARE VARIABLE clientnumsales D_INT;
BEGIN
	IF(NEW.FINALIZED = 'Y')
	THEN
		BEGIN
			FOR SELECT sit.PRODID, sit.QUANTITY
			FROM SALEITEMS sit
			WHERE sit.SALEID = NEW.id
			INTO :prodid, :prodquant
			DO
				BEGIN 
					UPDATE PRODUCTS P
					SET P.CURRENTQUANTITY = (P.CURRENTQUANTITY - :prodquant)
					WHERE P.ID = :prodid;					
				END
			
			SELECT COUNT(ID)
			FROM SALES s
			WHERE s.CLIENTID = NEW.CLIENTID AND s.FINALIZED = 'Y'
			INTO :clientnumsales;
			
			
			UPDATE CLIENTS
			SET NUMSALES = :clientnumsales
			WHERE ID = NEW.CLIENTID;
		END 	
END 

------------------------------------------------------------------
--					UPDATE PRODUCT QUANTITY PURCHASES

CREATE OR ALTER TRIGGER U_PRODQUANTPURC FOR PURCHASES
ACTIVE AFTER UPDATE POSITION 4
AS
DECLARE VARIABLE prodid D_INT;
DECLARE VARIABLE prodquant D_INT;
DECLARE VARIABLE suppliernumpurchases D_INT;
BEGIN
	IF(NEW.FINALIZED = 'Y')
	THEN
		BEGIN
			FOR SELECT pit.PRODID, pit.QUANTITY
			FROM PURCHASEITEMS pit
			WHERE pit.PURCHASEID = NEW.id
			INTO :prodid, :prodquant
			DO
				BEGIN 
					UPDATE PRODUCTS P
					SET P.CURRENTQUANTITY = (P.CURRENTQUANTITY + :prodquant)
					WHERE P.ID = :prodid;					
				END
			
			SELECT COUNT(ID)
			FROM PURCHASES p
			WHERE p.SUPPLIERID = NEW.SUPPLIERID AND p.FINALIZED = 'Y'
			INTO :suppliernumpurchases;
			
			
			UPDATE SUPPLIERS
			SET NUMBERPURCHASES = :suppliernumpurchases
			WHERE ID = NEW.SUPPLIERID;
		END 	
END 

-----------------------------------------------------------------
--				 INSERT PURCHASE INTO HISTORY TRIGGER


CREATE TRIGGER H_PURCHASES FOR PURCHASEITEMS
ACTIVE AFTER INSERT OR UPDATE POSITION 5
AS
BEGIN
	INSERT INTO ACTIONHISTORY(prodid ,purchaseid , saleid , unitvalue , total , quantity, actiondate)
	VALUES(NEW.prodid,NEW.purchaseid,NULL,NEW.unitvalue,NEW.total,NEW.quantity, CURRENT_TIMESTAMP);
END

