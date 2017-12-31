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
public class ProductItem {
    
    
    public String name;    
    public Float unitValue;    
    public String brandname;
    public String typename;
    public Integer quantity;    
    public float total;

    public ProductItem(String name, Float unitValue, String brandname, String typename, Integer quantity, float total) {
        this.name = name;
        this.unitValue = unitValue;
        this.brandname = brandname;
        this.typename = typename;
        this.quantity = quantity;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public Float getUnitValue() {
        return unitValue;
    }

    public String getBrandname() {
        return brandname;
    }

    public String getTypename() {
        return typename;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public float getTotal() {
        return total;
    }
    
    
    
    
    
}
