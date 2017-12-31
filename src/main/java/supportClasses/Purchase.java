/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supportClasses;

import java.time.LocalDate;

/**
 *
 * @author vinicius
 */
public class Purchase {
    
    public String supplier;
    public Float total;
    public Float subtotal;
    public String invoice;
    public Float discount;

    
    public String purchasedate;    

    
    
    public Purchase(String supplier, String invoice, Float subtotal, Float discount, Float total, String purchaseDate){
        this.supplier = supplier;
        this.invoice = invoice;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.purchasedate = purchaseDate;        
    }

    
    
    public Float getDiscount() {
        return discount;
    }    

    public String getSupplier() {
        return supplier;
    }

    public Float getTotal() {
        return total;
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getPurchasedate() {
        return purchasedate;
    }
    
}
