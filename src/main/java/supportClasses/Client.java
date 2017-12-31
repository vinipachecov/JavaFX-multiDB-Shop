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
public class Client {
    
    private int id;
    private String name;
    private int numsales;
    private String email;
    private String telephone;
    private String govid;
    private LocalDate birthdate;

    public Client(int id, String name, int numsales, String email, String telephone, String govid, LocalDate birthdate) {
        this.id = id;
        this.name = name;
        this.numsales = numsales;
        this.email = email;
        this.telephone = telephone;
        this.govid = govid;
        this.birthdate = birthdate;
    }
    
    public Client(String name, int numsales, String email, String telephone, String govid, LocalDate birthdate) {        
        this.name = name;
        this.numsales = numsales;
        this.email = email;
        this.telephone = telephone;
        this.govid = govid;
        this.birthdate = birthdate;
    }
    
    

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumsales() {
        return numsales;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getGovid() {
        return govid;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }    
    
}
