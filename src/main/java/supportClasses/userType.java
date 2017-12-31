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
public enum userType{
    admin,
    salesman;
    
    public static userType ParseUserType(String usertype){        
        switch(usertype){
            case "admin":                
                return userType.admin;                
            case "salesman":
                return userType.salesman;                            
        }
        return null;
    }
};
