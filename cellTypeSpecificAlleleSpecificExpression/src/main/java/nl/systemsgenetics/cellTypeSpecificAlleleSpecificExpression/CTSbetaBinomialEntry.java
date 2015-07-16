/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.systemsgenetics.cellTypeSpecificAlleleSpecificExpression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author adriaan
 */
class CTSbetaBinomialEntry {

    public CTSbetaBinomialEntry(String asLocations, String phenoTypeLocation, String outputLocation, int minReads, int minHets) throws IOException, Exception {
        
        
        // BETABINOMIAL TEST.
        // CURRENTLY DETERMINES THE OVERDISPERSION 
        // IN THE SAME WAY AS THE BETA BINOMIAL.
        
        //PART 1: read all individuals names from the files.

        ArrayList<String> allFiles = UtilityMethods.readFileIntoStringArrayList(asLocations);
        
        //PART 2: determine the per sample overdispersion in the file.
        
        ArrayList<betaBinomOverdispInSample>  dispersionParameters = new ArrayList<betaBinomOverdispInSample>();
        
        for(String sampleName : allFiles){
           
            dispersionParameters.add(new betaBinomOverdispInSample(sampleName));
        
            /*
             * TODO: determine some way to save the overdispersion sample.
             *       But we could also do it everytime you do this, perhaps based 
             *       on the relative time compared to the full calculation.
             */
        
        }
        
        double[] dispersionVals = new double[dispersionParameters.size()];  
        int i = 0;     
        
        for(betaBinomOverdispInSample sampleDispersion : dispersionParameters){
            dispersionVals[i] = sampleDispersion.getOverdispersion()[0];
            
            
            //do a check to make sure ordering is correct.
            if(!(sampleDispersion.getSampleName().equals(allFiles.get(i)))){
                System.out.println("ERROR! ordering is not correct filenames for overdispersion");
                System.out.println(sampleDispersion.getSampleName());
                System.out.println(allFiles.get(i));
            }
            
            i++;
        }
        
        
        //PART 3: determine the cell proportions per sample, provided in the sample file.
        
        
        // filenames_file, is a file containing per line a filename, which to load.
        String filenames_file = asLocations;

        /*
            Determine cell proportion per sample
         */
        
        ArrayList<String> phenoString =UtilityMethods.readFileIntoStringArrayList(phenoTypeLocation);
        
        /*
            Right now just assuming this is a file that is 
            ordered in the same way as the asLocation file.
            With per line the cell proportion that we can determine.
        */
        i = 0;
        double[] cellProp = new double[phenoString.size()];
        for(String samplePheno : phenoString){
            cellProp[i] = Double.parseDouble(samplePheno);
            i++;
        }
        
        
        //PART 4. Read the as files one line at a time and 
        //      determine the Cell type specific tests :).
        
                PrintWriter out_writer;
        out_writer = new PrintWriter(outputLocation, "UTF-8");
        
        //open all the files we want to open.
        ReadAsLinesIntoIndividualSNPdata asReader = new ReadAsLinesIntoIndividualSNPdata(asLocations);

        while (true) {

            //read some stuff from the files.
            ArrayList<IndividualSnpData> allSnpData;
            allSnpData = asReader.getIndividualsFromNextLine();
            
            if(allSnpData.isEmpty()) break;
            
            //Add the dispersion data assuming the same ordering
            //Which was checked previously.
            for(int j = 0; j < dispersionVals.length; j++){
                allSnpData.get(j).setDispersion(dispersionVals[j]);
            }
            for(int j = 0; j < cellProp.length; j++){
                allSnpData.get(j).setCellTypeProp(cellProp[j]);
            }
            
            
            //do the beta binomial test:
            CTSBetaBinomialTest results = new CTSBetaBinomialTest(allSnpData, minReads, minHets);

            // Write the results to the out_file.
            if (results.isTestPerformed()) {
                out_writer.println(results.writeTestStatistics(true));
                i++;
            }

        }
        
        out_writer.close();
       
    
    }
    
    
}
