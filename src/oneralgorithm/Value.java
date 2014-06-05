/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oneralgorithm;

/**
 *
 * @author Aloysius
 */
public class Value implements Comparable<Value>{
    public String classAtt;
    public double value;
    
    public Value(String classAtt, double value){
        this.classAtt = classAtt;
        this.value = value;
    }

    @Override
    public int compareTo(Value o) {
        return new Double(this.value).compareTo(o.value);
    }
    
    @Override
    public String toString(){
        return "Value: " + this.value + " Class: " + this.classAtt;
    }
}
