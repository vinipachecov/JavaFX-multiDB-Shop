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
public class Sale {
    
    public String client;
    public Float total;
    public Float subtotal;
    public String invoice;
    public Float discount;

    
    public String saleDate;    

    
    
    public Sale(String client, String invoice, Float subtotal, Float discount, Float total, String saleDate){
        this.client = client;
        this.invoice = invoice;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.saleDate = saleDate;        
    }

    
    
    public Float getDiscount() {
        return discount;
    }    

    public String getClient() {
        return client;
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

    public String getSaleDate() {
        return saleDate;
    }
    
}
