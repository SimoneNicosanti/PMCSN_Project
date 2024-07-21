package it.uniroma2.pmcsn.parks.engineering.factory;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import it.uniroma2.pmcsn.parks.model.server.Attraction;

public class AttractionBuilder {

    public List<Attraction> buildFromFile(String fileName) {
        
        List<Attraction> attractionList = new ArrayList<>() ;

        try (
            FileReader filereader = new FileReader(fileName) ;
            CSVReader csvReader = new CSVReader(filereader) ; 
        ) { 
  
            String[] nextRecord; 
    
            // Skip header
            csvReader.readNext() ;

            // Read line by line
            while ((nextRecord = csvReader.readNext()) != null) { 
                String attractionName = nextRecord[0] ;
                double attractionPopularity = Double.valueOf(nextRecord[1]) ;
                double attractionAvgDuration = Double.valueOf(nextRecord[2]) ;
                
                // TODO Build Attraction class
            } 
        } 
        catch (Exception e) { 
            e.printStackTrace(); 
        } 

        return attractionList ;
    }

    public Attraction buildAttraction(String name, double popularity, double avgDuration) {
        // TODO Fix this method --> Create constructor acepting these params
        return new Attraction(1) ;
    }

}
