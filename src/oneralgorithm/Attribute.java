/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oneralgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Aloysius
 */
public class Attribute {
    public String attributeName;
    public List<Bin> bins;
    public List<Double> values;
    public List<Value> valuesList;
    public List<Integer> classList;
    public int attributeErrorRate;
    public Map<Double, ArrayList<String>> valueToClassMap;
    
    public Attribute(){
        bins = new ArrayList<>();
        values = new ArrayList<>();
        classList = new ArrayList<>();
        valueToClassMap = new HashMap<Double, ArrayList<String>>();
    }
}
