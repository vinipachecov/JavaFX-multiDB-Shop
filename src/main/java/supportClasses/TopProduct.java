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
public class TopProduct {


    public String name;
    public Integer numsales;
    public Float cashgenerated;

    public TopProduct(String name, Integer numsales, Float cashgenerated) {
        this.name = name;
        this.numsales = numsales;
        this.cashgenerated = cashgenerated;
    }
    
    public String getName() {
        return name;
    }

    public Integer getNumsales() {
        return numsales;
    }

    public Float getCashgenerated() {
        return cashgenerated;
    }
    
}
