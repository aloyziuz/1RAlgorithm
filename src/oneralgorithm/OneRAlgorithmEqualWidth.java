/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oneralgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aloysius
 * the OneR algorithm uses unsupervised equal width discretization
 * 
 * 1. read the file
 * 2. identify the attribute names (for identification purpose)
 * 3. store the rest of the file as records
 * 4. for every attribute, 
 *  4.1. determine the values from the record
 *  4.2. determine min and max value for the attribute
 *  4.3. calculate the interval and bin range using (min - max)/# of bins
 * 5. for every attribute,
 *  5.1. for every record of the attribute,
 *      5.1.1. determine which bin it belongs to using the bin range
 *      5.1.2. determine which class it belongs to 
 *      5.1.3. increment the respective counter
 * 6. determine which class is majority for every bin
 * 7. calculate total error rate of the attribute for every attribute
 * 8. determine which attribute has the lowest error rate
 */
public class OneRAlgorithmEqualWidth {
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            System.out.println("-------Use Training Data as Test-------");
            int NoOfBins = 7;
            //(1) read the data set file
            BufferedReader br = new BufferedReader(new FileReader(new File("wine.csv")));
            String line = br.readLine();
            
            //(2) read first line as attribute names
            String[] attributesString = line.split(",");
            System.out.println("No. of attributes: " + attributesString.length);
            
            //(3) store records so no rescanning is required
            List<String> recordsString = new ArrayList<>();
            while((line = br.readLine()) != null){
                recordsString.add(line);
            }
            
            System.out.println("No. Of Records: " + recordsString.size());
            br.close();
            
            //list to store attributes and class value
            List<Attribute> attributes = new ArrayList<>();
            List<String> classList = new ArrayList<>();
            List<AttributeClass> classType = new ArrayList<>();
            for(String record: recordsString){
                String[] a = record.split(",");
                classList.add(a[0]);
                boolean duplicate = false;
                for(int i = 0; i < classType.size(); i++){
                    if(a[0].equals(classType.get(i).name)){
                        duplicate = true;
                    }
                }
                if(duplicate == false){
                    AttributeClass b = new AttributeClass(a[0]);
                    classType.add(b);
                }
            }
            
            //(4) pre-processing stage
            //for every attribute (except class), fill the bins/class
            for(int i = 1; i < attributesString.length; i++){
                List<Double> values = new ArrayList<>();
                //populate list with values related to the respective attributes
                for(String record: recordsString){
                    String[] a = record.split(",");
                    float b = Float.parseFloat(a[i]);
                    double c = (double) Math.round(b*100)/100;
                    values.add(c);
                }
                //determine max, min, interval values
                double minValue = getMinValue(values);
                double maxValue = getMaxValue(values);
                double interval = (maxValue - minValue)/NoOfBins;
                
                Attribute att = new Attribute();
                att.attributeName = attributesString[i];
                att.values = values;
                
                //determine bin range for each bin
                double num = minValue;
                for(int b = 0; b < NoOfBins; b++){
                    Bin bin = new Bin();
                    bin.lowerRange = (double) Math.round(num*100)/100;
                    num += interval;
                    bin.upperRange = (double) Math.round(num*100)/100;
                    for(AttributeClass clss : classType){
                        AttributeClass a = new AttributeClass(clss.name);
                        bin.classes.add(a);
                    }
                    att.bins.add(bin);
                }
                attributes.add(att);
            }
            
            //(5) count # of occurences
            //for each record, determine which bin it is placed and increment the appropriate class counter
            for(Attribute att: attributes){
                for(int i = 0; i < att.values.size(); i++){
                    double value = att.values.get(i);
                    for(int b = 0; b < att.bins.size(); b++){
                        //all values that is lower than the upper range of first bin is included
                        if(b == 0){
                            if(value < att.bins.get(b).upperRange){
                                att.bins.get(b).noOfRecord++;
                                for(AttributeClass clss : att.bins.get(b).classes){
                                    if(classList.get(i).toString().equals(clss.name)) {
                                        clss.count+=1;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        //all values that is higher than the lower bin of last bin is included
                        else if(b == (att.bins.size()-1)){
                            if(value >= att.bins.get(b).lowerRange){
                                att.bins.get(b).noOfRecord++;
                                for(AttributeClass clss:att.bins.get(b).classes){
                                    if(classList.get(i).toString().equals(clss.name)){
                                        clss.count+=1;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        else{
                            if(value >= att.bins.get(b).lowerRange && value < att.bins.get(b).upperRange){
                                att.bins.get(b).noOfRecord++;
                                for(AttributeClass clss : att.bins.get(b).classes){
                                    if(classList.get(i).toString().equals(clss.name)){
                                        clss.count+=1;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            
            //(6) determine majority and 3 of errors
            //for every attribute, for every bin, determine majority and calculate bin error
            for(Attribute att: attributes){
                for(Bin bin: att.bins){
                    int highest = bin.classes.get(0).count;
                    bin.majority = bin.classes.get(0).name;
                    for(AttributeClass a : bin.classes){
                        if(a.count > highest){
                            highest = a.count;
                            bin.majority = a.name;
                        }
                    }
                    bin.errors = bin.noOfRecord - highest;
                }
            }
            
            //(7) calculate attribute error rate
            for(Attribute att: attributes){
                int attributeErrorRate = 0;
                for(Bin bin: att.bins){
                    attributeErrorRate += bin.errors;
                }
                att.attributeErrorRate = attributeErrorRate;
            }
            
            //(8) determine the attribute which has lowest error rate
            Attribute lowestErrorAttribute = attributes.get(0);
            for(int i = 1; i < attributes.size(); i++){
                if(attributes.get(i).attributeErrorRate < lowestErrorAttribute.attributeErrorRate){
                    lowestErrorAttribute = attributes.get(i);
                }
            }
            
            System.out.println("Best Rule: " + lowestErrorAttribute.attributeName);
            System.out.println("Error Rate: " + lowestErrorAttribute.attributeErrorRate + " out of " + recordsString.size() + " records. ");
            System.out.println("Rule: ");
            for(int i = 0; i < lowestErrorAttribute.bins.size(); i++){
                if(i == 0){
                    System.out.println("\t< "+lowestErrorAttribute.bins.get(i).upperRange + " --> " + lowestErrorAttribute.bins.get(i).majority);
                }
                else if(i == (lowestErrorAttribute.bins.size()-1)){
                    System.out.println("\t>= "+lowestErrorAttribute.bins.get(i).lowerRange + " --> " + lowestErrorAttribute.bins.get(i).majority);
                }
                else{
                    System.out.println("\t"+lowestErrorAttribute.bins.get(i).lowerRange + " <= x < " + lowestErrorAttribute.bins.get(i).upperRange + " --> " + lowestErrorAttribute.bins.get(i).majority);
                }
            }
            
            //use training data as test data
            int correct = 0;
            int wrong = 0;
            for(int i = 0; i < lowestErrorAttribute.values.size(); i++){
                double value = lowestErrorAttribute.values.get(i);
                for(int b = 0; b < lowestErrorAttribute.bins.size(); b++){
                    if(b == 0){
                        if(value < lowestErrorAttribute.bins.get(b).upperRange){
                            if(classList.get(i).toString().equals(lowestErrorAttribute.bins.get(b).majority)){
                                correct++;
                                break;
                            }
                            else{
                                wrong++;
                                break;
                            }
                        }
                    }
                    else if(b == (lowestErrorAttribute.bins.size()-1)){
                        if(value >= lowestErrorAttribute.bins.get(b).lowerRange){
                            if(classList.get(i).toString().equals(lowestErrorAttribute.bins.get(b).majority)){
                                correct++;
                                break;
                            }
                            else{
                                wrong++;
                                break;
                            }
                        }
                    }
                    else{
                        if(value >= lowestErrorAttribute.bins.get(b).lowerRange && value < lowestErrorAttribute.bins.get(b).upperRange){
                            if(classList.get(i).toString().equals(lowestErrorAttribute.bins.get(b).majority)){
                                correct++;
                                break;
                            }
                            else{
                                wrong++;
                                break;
                            }
                        }
                    }
                }
            }
            System.out.println("Correctly classified instance: " + correct + "\t" + (double) (correct*100)/recordsString.size() + "%");
            System.out.println("Incorrectly classified instance: " + wrong + "\t" + (double) (wrong*100)/recordsString.size() + "%");
            long end = System.currentTimeMillis();
            System.out.println("Time taken: " + (double) (end - start)/1000 + "s");
        } catch (FileNotFoundException ex) {
            System.out.println("File not Found. " + ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    private static double getMaxValue(List<Double> a){
        double max = a.get(0);
        for (int i = 1; i < a.size(); i++){
            if(a.get(i) > max){
                max = a.get(i);
            }
        }
        return max;
    }
    
    private static double getMinValue(List<Double> a){
        double min = a.get(0);
        for(int i = 1; i < a.size(); i++){
            if(a.get(i) < min){
                min = a.get(i);
            }
        }
        return min;
    }
}

