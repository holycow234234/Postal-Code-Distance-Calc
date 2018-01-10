// Uses google maps api to check the distance between two postal codes either by road, or failing that, the distance as the crow flies


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import javax.swing.*;
import java.util.Scanner;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
public class postalcodedistancechecker {

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  public static void main(String[] args) throws IOException, JSONException {
    
String codeb;
    // reads codes from codes.txt
    Scanner inputFile = new Scanner(new File("codes.txt")); 
     try{
      //  puts distances in km in distances.txt
    PrintWriter writer = new PrintWriter("distances.txt", "UTF-8");
     while(inputFile.hasNextLine()) {
    codeb = inputFile.nextLine();

   //change H0H+0H0 to the postal code you want to check, could also be modified to check multiple postal codes
   String distance = calcDistance("H0H+0H0",validator(codeb));
    System.out.println(distance);
    
     writer.println(distance);
     }
     writer.close();
     }
     catch(UnsupportedEncodingException ioe){
      }
  
}
   
    

  

  public static String calcDistance(String beg, String end) {
    int dist = 0;
    String distance ="";
  JSONObject json=null;
  try {
            //change APIKEYGOESHERE to your google api key
              json = readJsonFromUrl("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+beg+"&destinations="+end+"&mode=driving&sensor=false&key=APIKEYGOESHERE");
              // json processing  
              json.get("rows");
              JSONArray arr=null;
              arr = json.getJSONArray("rows");
              dist=(Integer)arr.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value");
              dist=dist/1000;
              distance=Integer.toString(dist);
      }catch (JSONException e) {
              e.printStackTrace();
              // if distance by road comes back null, we calculate the distance as the crow flies instead
              distance = crowflies(beg,end);
      } 
      catch (IOException e)
        {
          e.printStackTrace();
            }
              return distance;
}//end calc distance
//checks to make sure that postal code is in valid format (A1A 1A1)
public static String validator (String crap) {
   String fixed ="";
    String working ="";
    if (crap.length()==6||crap.length()==7){

    if (crap.matches ("...\\s..."))
    {working= crap.substring(0,3)+"+"+crap.substring(4);
    }
     if (crap.matches ("......"))
       {working= crap.substring(0,3)+"+"+crap.substring(3);
       
     if(working.charAt(1) =='O')
     {working = working.substring(0,1)+"0"+working.substring(2);
     }
       if(working.charAt(4) =='O')
     {working = working.substring(0,4)+"0"+working.substring(5);
       }
         if(working.charAt(6) =='O')
     {working = working.substring(0,6)+"0";
    }

       }
    }
    if(working.matches("[A-Z][0-9][A-Z]\\u002b[0-9][A-Z][0-9]"))
         {fixed=working;
    }
    else {fixed = "000+000";
    }
    return fixed;
    
}
  public static String crowflies (String codea, String codeb) {
    JSONObject json=null;
    double lat1;
    double lat2;
    double lng1;
    double lng2;
    String distance="";
    double dist=0;
try 
{
//get lat and long of both postal codes and calculate the distance between them using the haversine formula
json = readJsonFromUrl("https://maps.googleapis.com/maps/api/geocode/json?components=postal_code:"+codea+"&sensor=false&key=APIKEYGOESHERE");
json.get("results");
JSONArray arr=null;
arr = json.getJSONArray("results");
lat1=(Double)arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
lng1=(Double)arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

json = readJsonFromUrl("https://maps.googleapis.com/maps/api/geocode/json?components=postal_code:"+codeb+"&sensor=false&key=APIKEYGOESHERE");
json.get("results");
arr=null;
arr = json.getJSONArray("results");
lat2=(Double)arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
lng2=(Double)arr.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");


dist= haversine(lat1,lng1,lat2,lng2);
distance=Integer.toString((int)dist);


}
catch (JSONException e) 
{
e.printStackTrace();
distance ="*";
} 
catch (IOException e)
{
    e.printStackTrace();
}
return distance;
  }
  public static final double R = 6372.8; // In kilometers
  //use the haversine formula to calculate the distance between the 2 postal codes
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}

  
