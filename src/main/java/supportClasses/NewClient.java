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
public class NewClient {
    
    public String name;
    public String contact;
    public String joinDate;

    public NewClient(String name, String contact, String joinDate) {
        this.name = name;
        this.contact = contact;
        this.joinDate = joinDate;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getJoinDate() {
        return joinDate;
    }
    
    
}
