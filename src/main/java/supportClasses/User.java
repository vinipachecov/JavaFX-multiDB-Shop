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


public class User {
    
    
    private String username;
    private userType usertype;    
    
    public User(String username, userType userType){
        this.username = username;
        this.usertype = userType;
    }
    
    public userType getUserType(){
        return usertype;
    }
    
    public String getUsername(){
        return username;
    }
    
    
    
}
