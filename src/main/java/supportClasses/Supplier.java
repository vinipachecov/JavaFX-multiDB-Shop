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
public class Supplier {
    
    public String socialReason;
    public String email;
    public String cnpj;
    public String telephone;
    public String fantasyName;
    public Integer purchases;

    public String getSocialReason() {
        return socialReason;
    }

    public String getEmail() {
        return email;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getTelephone() {
        return telephone;
    }

    public Supplier(String socialReason, String email, String cnpj, String telephone, String fantasyName, Integer purchases) {
        this.socialReason = socialReason;
        this.email = email;
        this.cnpj = cnpj;
        this.telephone = telephone;
        this.fantasyName = fantasyName;
        this.purchases = purchases;
    }

    public String getFantasyName() {
        return fantasyName;
    }

    public Integer getPurchases() {
        return purchases;
    }
    
    
}
