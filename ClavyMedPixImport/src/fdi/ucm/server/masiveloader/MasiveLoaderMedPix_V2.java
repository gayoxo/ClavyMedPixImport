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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author Joaquin Gayoso Cabada
 *
 */
public class MasiveLoaderMedPix_V2 extends MasiveLoaderMedPix{

	



	private HashSet<String> topicID;

	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MasiveLoaderMedPix_V2 LC=new MasiveLoaderMedPix_V2();
		MasiveLoaderMedPix_V2.consoleDebug=true;
		
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

	public MasiveLoaderMedPix_V2() {
		super();
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
	}

	@Override
	public List<String> processCollecccion(String foldera) {
		try {
			
			
			
			Logs=new ArrayList<String>();
			encounterID=new  HashSet<String>();
			topicID=new HashSet<String>();
			
			Logs.add("New Data import on"+new Date());
			
			String folderaM=foldera+"/cases";
			
			(new File(folderaM)).mkdirs();
			
			ProcesaCasos();
			ProcesaCasoID(folderaM);
			
			String folderaT=foldera+"/topics";
			
			(new File(folderaT)).mkdirs();
			
			ProcesaTopics(folderaT);

			
			
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
	      
	      
	      JSONObject json = new JSONObject(jsonText);

	      String Valor = json.get("topicID").toString();
	      
	      if (!topicID.contains(Valor))
	    	  topicID.add(Valor);
	    	  
	      
	      
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
//			throw new RuntimeException("No tiene editor o los elementos son incorrectos");
		}
		
		
		
		
	}

	
	
	
}

	

	private void ProcesaTopics(String folder) {
		ProcesaValoresTopics(folder);
		
	}


private void ProcesaValoresTopics(String folder) {
		
		
		int Tot = topicID.size();
		int ite=0;
		for (String IDvalues : topicID) {
			
			System.out.println(ite++ + "/"+Tot);

			try {
	        	URL F=new URL("https://medpix.nlm.nih.gov/rest/topic.json?topicID="+IDvalues);
	        	
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
	    	      
	    	     
	    	      
	    	      
	    	      Logs.add("Topic:"+ IDvalues + " loaded in "+ IDvalues+".json" );
	    	      

	         	  
	       	  
			} catch (Exception e) {
				e.printStackTrace();
				Logs.add("Error con la carga de documento->encounterID: "+IDvalues);
//				throw new RuntimeException("No tiene editor o los elementos son incorrectos");
			}
		}
	}

	

	








	
	
	



}
