/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportClasses;

/**
 *
 * @author vinicius
 */
public class Product {
    
    public Integer ID;
    public String brandID;
    public String typeID;
    public String name;
    public Integer minimumQuantity;
    public Integer currentQuantity;
    public Float unitValue;
    public String unit;    
    public String brandname;
    public String typename;

    public Product(String brandID, String typeID, String name, Integer minimumQuantity, Integer currentQuantity, Float unitValue, String unit) {
        this.brandID = brandID;
        this.typeID = typeID;
        this.name = name;
        this.minimumQuantity = minimumQuantity;
        this.currentQuantity = currentQuantity;
        this.unitValue = unitValue;
        this.unit = unit;
    }

    public Product(String productname, Integer minimumQuantity, Integer currentQuantity, Float unitValue, String unit, String brandname, String typename) {
        this.name = productname;
        this.minimumQuantity = minimumQuantity;
        this.currentQuantity = currentQuantity;
        this.unitValue = unitValue;
        this.unit = unit;
        this.brandname = brandname;
        this.typename = typename;
    }

    public String getBrandname() {
        return brandname;
    }

    public String getTypename() {
        return typename;
    }

    
    public String getBrandID() {
        return brandID;
    }

    public String getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public Float getUnitValue() {
        return unitValue;
    }

    public String getUnit() {
        return unit;
    }
    
    
}
