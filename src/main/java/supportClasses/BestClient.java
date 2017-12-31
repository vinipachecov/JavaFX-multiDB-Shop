package supportClasses;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vinicius
 */
public class BestClient {
    public String name;
    public Integer numBought;
    public Float numcashgenerated;

    public BestClient(String name, Integer numBought, Float numcashgenerated) {
        this.name = name;
        this.numBought = numBought;
        this.numcashgenerated = numcashgenerated;
    }

    public String getName() {
        return name;
    }

    public Integer getNumBought() {
        return numBought;
    }

    public Float getNumcashgenerated() {
        return numcashgenerated;
    }    
    
}
