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
public enum databaseType{
    postgres,
    firebird,
    mongodb;
    
    public static databaseType ParseUserType(String db){        
        switch(db){
            case "postgres":                
                return databaseType.postgres;                
            case "firebird":
                return databaseType.firebird;                            
            case "mongodb":
                return databaseType.mongodb;                            
        }
        return null;
    }
};
