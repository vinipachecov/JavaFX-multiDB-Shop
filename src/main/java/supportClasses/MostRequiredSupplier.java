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
public class MostRequiredSupplier {
    public String name;
    public Integer totalitems;
    public Float totalpercent;

    public MostRequiredSupplier(String name, Integer totalitems, Float totalpercent) {
        this.name = name;
        this.totalitems = totalitems;
        this.totalpercent = totalpercent;
    }

    public String getName() {
        return name;
    }

    public Integer getTotalitems() {
        return totalitems;
    }

    public Float getTotalpercent() {
        return totalpercent;
    }
    
    
}
