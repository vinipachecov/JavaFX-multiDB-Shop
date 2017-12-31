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
public class HistoryItem {
    public String type;
    public String invoice;
    public String product;
    public Integer quantity;
    public Float total;
    public Float unitvalue;
    public String date;

    public Float getUnitvalue() {
        return unitvalue;
    }

    public HistoryItem(String type, String invoice, String product, Integer quantity, Float total, Float unitvalue, String date) {
        this.type = type;
        this.invoice = invoice;
        this.product = product;
        this.quantity = quantity;
        this.total = total;
        this.unitvalue = unitvalue;
        this.date = date;
    }
    

    public String getType() {
        return type;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Float getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }
    
    
    
}
