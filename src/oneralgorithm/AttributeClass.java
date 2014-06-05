/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oneralgorithm;

/**
 *
 * @author Aloysius
 */
public class AttributeClass {
    public String name;
    public int count;
    
    public AttributeClass(String name){
        this.name = name;
        count = 0;
    }
    
    public String toString(){
        return "Name: " + name + " Count : " + count;
    }
}
