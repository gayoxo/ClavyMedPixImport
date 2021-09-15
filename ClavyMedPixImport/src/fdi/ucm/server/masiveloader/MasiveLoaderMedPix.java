/**
 * 
 */
package fdi.ucm.server.masiveloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author Joaquin Gayoso Cabada
 *
 */
public class MasiveLoaderMedPix{

	

	private ArrayList<String> Logs;

	private HashSet<String> encounterID;

	public static boolean consoleDebug=false;

	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MasiveLoaderMedPix LC=new MasiveLoaderMedPix();
		MasiveLoaderMedPix.consoleDebug=true;
		
		String foldera="/tmp/id_"+System.nanoTime();
		
		(new File(foldera)).mkdirs();
		
		List<String> Salida=LC.processCollecccion(foldera);
		if (Salida!=null)
			{
			
			StringBuffer SB=new StringBuffer();
			
			
			
			for (String warning : Salida)
				{
				System.err.println(warning);
				SB.append(warning+"\n");
				}

			
			

			  FileWriter myWriter;
			try {
				myWriter = new FileWriter(foldera+"/notas.txt");
				myWriter.write(SB.toString());
	 	          myWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Error");
				System.exit(-2);
			}
 	          
			System.out.println("Correcto");
			System.exit(0);
			
			}
		else
			{
			System.err.println("Error");
			System.exit(-1);
			}
	}

	public MasiveLoaderMedPix() {
		super();
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
	}

	public List<String> processCollecccion(String foldera) {
		try {
			
			
			
			Logs=new ArrayList<String>();
			encounterID=new  HashSet<String>();
			
			Logs.add("New Data import on"+new Date());
			
			
			ProcesaCasos();
			ProcesaCasoID(foldera);

			
			
			return Logs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}





	
	
	

	
	
	
	

	private void ProcesaCasoID(String folder) {
		
		ProcesaValoresCasoIDJson(folder);
		
	}



	
private void ProcesaValoresCasoIDJson(String folder) {

		int ite=1;
		for (String Entryvalues : encounterID) {
		
			
			System.out.println(ite++ + "/"+encounterID.size());
			
			String IDvalues=Entryvalues;
			

			try {
			URL F=new URL("https://medpix.nlm.nih.gov/rest/encounter.json?encounterID="+IDvalues);
			
			InputStream is = F.openStream();
    	    
    	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
    	      String jsonText = readAll(rd);
    	      
    	      JsonParser parser = new JsonParser();
    	      Gson gson = new GsonBuilder().setPrettyPrinting().create();

    	      JsonElement el = parser.parse(jsonText);
    	      jsonText = gson.toJson(el);
    	      
    	      
    	          FileWriter myWriter = new FileWriter(folder+"/"+IDvalues+".json");
    	          myWriter.write(jsonText);
    	          myWriter.close();
    	      
    	     
    	      
    	      
    	      Logs.add("case:"+ IDvalues + " loaded in "+ IDvalues+".json" );
    	      
    	      
    	      
	       	  
			} catch (Exception e) {
				e.printStackTrace();
				Logs.add("Error con la carga de documento->encounterID: "+IDvalues);
//				throw new RuntimeException("No tiene editor o los elementos son incorrectos");
			}
			
			
			
			
		}

		
		
		
	}
	





	private void ProcesaCasos() {


		ProcesaValores();
		
	}



	private void ProcesaValores() {
		
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		
		for (char c : alphabet) {
			 try {
		        	URL F=new URL("https://medpix.nlm.nih.gov/rest/encounter/diagnosis.json?diagnosis="+c);
		        	
		        	System.out.println(F.toString());
		        	
		        	 InputStream is = F.openStream();
		        	    try {
		        	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		        	      String jsonText = readAll(rd);
//		        	      System.out.println(jsonText);
		        	      JSONArray json = new JSONArray(jsonText);
//		        	      System.out.println(json.toString());
//		        	      System.out.println(json.get("encounterID"));
		        	      for (int i = 0; i < json.length(); i++) {
		        	    	  JSONObject JO=json.getJSONObject(i);
		        	    	  if (JO.get("encounterID")!=null)
		        	    		  encounterID.add(JO.get("encounterID").toString());
						}
		        	      
		        	    } finally {
		        	      is.close();
		        	    }
		       	  
				} catch (Exception e) {
					if (consoleDebug)
					e.printStackTrace();
					Logs.add("Error con la carga de listas de documento");
//					throw new RuntimeException("No tiene editor o los elementos son incorrectos");
				}
		}
		
       
        
	}

	 private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }



	
	
	
	



}
