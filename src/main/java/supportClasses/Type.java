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
public class Type {
    
    public Integer id;
    public String name;
    
    
    public Type(Integer id, String name){
        this.id = id;
        this.name = name;
    }
    
    public Integer getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public Type(String name){
        this.name = name;
    }
    
}
