/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oneralgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aloysius
 */
public class Bin {
    public double lowerRange;
    public double upperRange;
    public int errors;
    public int noOfRecord;
    public List<AttributeClass> classes;
    public String majority;
    
    public Bin(){
        lowerRange = 0;
        upperRange = 0;
        errors = 0;
        noOfRecord = 0;
        classes = new ArrayList<>();
    }
}
