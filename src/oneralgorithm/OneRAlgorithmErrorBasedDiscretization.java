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
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Aloysius
 * How the program works:
 * 
 * 1. read the file
 * 2. assign first line as attribute names/headers
 * 3. read the rest as records and hold them in memory
 * 4. determine how many class category is there
 * 5. preprocess the data using error-based discretization
 *  for every attribute,
 *      assign attributes its respective values
 *      sort the list of values in ascending order
 *      fill the bucket with values until it reaches minimum bucket size
 *      if it reaches minimum bucket size,
 *          determine majority class category of the bucket
 *          check the next value
 *          if class category of the next value is same with bucket majority or same value with the last value in the bucket,
 *              add the value into the bucket
 *              if last value of the list
 *                  create bucket using the left over values
 *                  take last value of the bucket as upper range
 *                  take average of first value of the bucket and last value of previous bucket as lower range
 *          else
 *              close the bucket
 *              determine upper and lower range of the bucket.
 *              if first bucket,
 *                  take first value of the bucket as lower range
 *                  take average of last value of the bucket and the next value as upper range
 *              if last bin
 *                  take last value of the bucket as upper range
 *                  take average of first value of the bucket and last value of previous bucket as lower range
 *              if it is somewhere in the middle
 *                  take last value of previous bucket as lower range
 *                  take average of last value of the bucket and the next value as upper range
 * 6. count class category occurences
 *      for every attribute,
 *          for every value,
 *              check which bucket the value belongs to
 *                  increment the appropriate counter according to class category
 * 7. determine majority class category
 * 8. determine error rates
 *      for every attribute,
 *          for every bucket,
 *              calculate # of errors
 * 9. calculate error rate of each attribute
 *      for every attribute
 *          add up the # of errors of every bucket
 * 10. determine attribute with lowest error rate
 * 11. print results
 * 12. use training data as test data
 * 13. print results
 *
 */
public class OneRAlgorithmErrorBasedDiscretization {
    public static void main(String[] args) {
        try{
            long start = System.currentTimeMillis();
            System.out.println("-------Use Training Data as Test-------");
            int minBucketSize = 6;
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
            
            //(4) determine how many class category is there
            //list to store attributes and class value
            List<Attribute> attributes = new ArrayList<>();
            List<String> classList = new ArrayList<>();
            List<AttributeClass> classType = new ArrayList<>();
            for(String record: recordsString){
                String[] a = record.split(",");
                classList.add(a[0]);
                //determine how many class category is there
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
            
            //(5) preprocessing stage
            //for every attribute (except class), fill the bins/class
            for(int i = 1; i < attributesString.length; i++){
                Attribute att = new Attribute();
                att.attributeName = attributesString[i];
                
                List<Value> values = new ArrayList<>();
                //populate list with values related to the respective attributes
                for(int r = 0; r < recordsString.size(); r++){
                    String[] a = recordsString.get(r).split(",");
                    float b = Float.parseFloat(a[i]);
                    double c = (double) Math.round(b*100)/100;
                    Value v = new Value(classList.get(r), c);
                    values.add(v);
                }
                att.valuesList = values;
                
                //sort the values
                Collections.sort(att.valuesList);
                
                ArrayList<Value> list = new ArrayList<>();
                for(int v = 0; v < att.valuesList.size(); v++){
                    if(list.size() < minBucketSize){
                        list.add(att.valuesList.get(v));
                        if(v == att.valuesList.size()-1){
                            Bin bin = new Bin();
                            bin.lowerRange = att.bins.get(att.bins.size()-1).upperRange;
                            bin.upperRange = list.get(list.size()-1).value;
                            for(AttributeClass clss : classType){
                                AttributeClass ac = new AttributeClass(clss.name);
                                bin.classes.add(ac);
                            }
                            att.bins.add(bin);
                        }
                    }
                    else{
                        ArrayList<AttributeClass> classes = createBunshin(classType);
                        //count class occurences
                        for(Value val : list){
                            for(AttributeClass ac : classes){
                                if(val.classAtt.equals(ac.name)){
                                    ac.count++;
                                    break;
                                }
                            }
                        }
                        
                        //determine majority
                        AttributeClass majority = classes.get(0);
                        for(AttributeClass ac : classes){
                            if(ac.count > majority.count){
                                majority = ac;
                            }
                        }
                        
                        //check if the next value is same or is majority class
                        boolean different = false;
                        while(different == false){
                            if(v+1 < att.valuesList.size()){
                                double a = att.valuesList.get(v+1).value;
                                double b = list.get(list.size()-1).value;
                                if(att.valuesList.get(v+1).classAtt.equals(majority.name) || a == b){
                                    list.add(att.valuesList.get(v+1));
                                    v++;
                                }
                                else{
                                    different = true;
                                }
                            }
                            else{
                                different = true;
                            }
                        }

                        //if first bin
                        if(att.bins.size() == 0){
                            Bin bin = new Bin();
                            bin.lowerRange = list.get(0).value;
                            double a = list.get(list.size()-1).value;
                            double b = att.valuesList.get(v+1).value;
                            double avg = (a+b)/2;
                            bin.upperRange = (double) Math.round(avg*1000)/1000;
                            for(AttributeClass clss : classType){
                                AttributeClass ac = new AttributeClass(clss.name);
                                bin.classes.add(ac);
                            }
                            att.bins.add(bin);
                        }
                        //if last bin
                        else if(v == att.valuesList.size()-1){
                            Bin bin = new Bin();
                            bin.lowerRange = att.bins.get(att.bins.size()-1).upperRange;
                            bin.upperRange = list.get(list.size()-1).value;
                            for(AttributeClass clss : classType){
                                AttributeClass ac = new AttributeClass(clss.name);
                                bin.classes.add(ac);
                            }
                            att.bins.add(bin);
                        }
                        else{
                            Bin bin = new Bin();
                            bin.lowerRange = att.bins.get(att.bins.size()-1).upperRange;
                            double a = list.get(list.size()-1).value;
                            double b = att.valuesList.get(v+1).value;
                            double avg = (a+b)/2;
                            bin.upperRange = (double) Math.round(avg*1000)/1000;
                            for(AttributeClass clss : classType){
                                AttributeClass ac = new AttributeClass(clss.name);
                                bin.classes.add(ac);
                            }
                            att.bins.add(bin);
                            
                        }
                        list.clear();
                    }
                }
                attributes.add(att);
            }
            
            //(6) count occurences
            //count class category occurences
            for(Attribute att: attributes){
                for(int i = 0; i < att.valuesList.size(); i++){
                    Value value = att.valuesList.get(i);
                    for(int b = 0; b < att.bins.size(); b++){
                        //all values that is lower than the upper range of first bin is included
                        if(b == 0){
                            if(value.value < att.bins.get(b).upperRange){
                                att.bins.get(b).noOfRecord++;
                                for(AttributeClass clss : att.bins.get(b).classes){
                                    if(value.classAtt.equals(clss.name)) {
                                        clss.count+=1;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        else if(b == (att.bins.size()-1)){
                            if(value.value >= att.bins.get(b).lowerRange){
                                att.bins.get(b).noOfRecord++;
                                for(AttributeClass clss : att.bins.get(b).classes){
                                    if(value.classAtt.equals(clss.name)) {
                                        clss.count+=1;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        else{
                            if(value.value >= att.bins.get(b).lowerRange && value.value < att.bins.get(b).upperRange){
                                att.bins.get(b).noOfRecord++;
                                for(AttributeClass clss : att.bins.get(b).classes){
                                    if(value.classAtt.equals(clss.name)) {
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
            
            //(7) determine majority
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
                    //(8) calculate number of errors
                    bin.errors = bin.noOfRecord - highest;
                }
            }
            
            //(9) calculate error rate
            //calculate attribute error rate
            for(Attribute att: attributes){
                int attributeErrorRate = 0;
                for(Bin bin: att.bins){
                    attributeErrorRate += bin.errors;
                }
                att.attributeErrorRate = attributeErrorRate;
            }
            
            //(10) determine the attribute which has lowest error rate
            Attribute lowestErrorAttribute = attributes.get(0);
            for(int i = 1; i < attributes.size(); i++){
                if(attributes.get(i).attributeErrorRate < lowestErrorAttribute.attributeErrorRate){
                    lowestErrorAttribute = attributes.get(i);
                }
            }
            //(11) printing informations
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
            //(12) use training data as test data
            int correct = 0;
            int wrong = 0;
            for(int i = 0; i < lowestErrorAttribute.valuesList.size(); i++){
                Value value = lowestErrorAttribute.valuesList.get(i);
                for(int b = 0; b < lowestErrorAttribute.bins.size(); b++){
                    if(b == 0){
                        if(value.value < lowestErrorAttribute.bins.get(b).upperRange){
                            if(value.classAtt.equals(lowestErrorAttribute.bins.get(b).majority)){
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
                        if(value.value >= lowestErrorAttribute.bins.get(b).lowerRange){
                            if(value.classAtt.toString().equals(lowestErrorAttribute.bins.get(b).majority)){
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
                        if(value.value >= lowestErrorAttribute.bins.get(b).lowerRange && value.value < lowestErrorAttribute.bins.get(b).upperRange){
                            if(value.classAtt.toString().equals(lowestErrorAttribute.bins.get(b).majority)){
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
    
    public static ArrayList<AttributeClass> createBunshin(List<AttributeClass> a){
        ArrayList<AttributeClass> list = new ArrayList();
        for(AttributeClass obj : a){
            list.add(new AttributeClass(obj.name));
        }
        return list;
    }
}
