package edu.stanford.slac.pinger.etl.pre_extractor.geographic.country;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// the arquive who has all the countries geonames list was generated by http://peric.github.io/GetCountries/
// this class extract all the countries and put on a multidimensional string

public class GetCountriesGeonames {
	
	public String[][] getGeonameCountries() {

        JSONParser parser = new JSONParser();

        try {     
            Object obj = parser.parse(new FileReader("data/json/allcountriesgeonames.json"));
            
    		JSONObject jsonObject =  (JSONObject) obj;
            
            JSONObject countries =  (JSONObject) jsonObject.get("countries"); 
            
            JSONArray country =  (JSONArray) countries.get("country"); 
            
            //System.out.println(country.size());
            
            int i = 0;
            String[][] singlecountries = new String[250][5];
            int j = 0;                  
                     	                   
            for (i = 0; i < 250; i++) { 
            	
            	JSONObject single =  (JSONObject) country.get(i);
            	
            	for (j = 0; j < 5; j++) {
            		
            		if (j == 0 ){ singlecountries[i][j] = single.get("countryCode").toString(); }  
            	    if (j == 1 ){ singlecountries[i][j] = single.get("countryName").toString(); } 
            	    if (j == 2 ){ singlecountries[i][j] = single.get("population").toString(); }
            	    if (j == 3 ){ singlecountries[i][j] = single.get("continent").toString();}
            	    if (j == 4 ){ singlecountries[i][j] = single.get("areaInSqKm").toString(); }               	   
            	}
            	
            }     
            
            return singlecountries;
            
            
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return null;  
        
        
        
	}


}
