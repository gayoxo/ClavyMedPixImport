/**
 * 
 */
package fdi.ucm.server.importparser.medpixnosimple;

import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fdi.ucm.server.modelComplete.collection.CompleteCollectionAndLog;

/**
 * @author Joaquin Gayoso Cabada
 *
 */
public class LoadCollectionMedPixNoSimple extends fdi.ucm.server.importparser.medpixbytopic.LoadCollectionMedPixNoSimple{


	private int querryMax=2000;

	public LoadCollectionMedPixNoSimple() {
		super();
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadCollectionMedPixNoSimple LC=new LoadCollectionMedPixNoSimple();
		LoadCollectionMedPixNoSimple.consoleDebug=true;
		
		
		
		CompleteCollectionAndLog Salida=LC.processCollecccion(new ArrayList<String>());
		if (Salida!=null)
			{
			
			System.out.println("Correcto");
			
			for (String warning : Salida.getLogLines())
				System.err.println(warning);

			
			System.exit(0);
			
			}
		else
			{
			System.err.println("Error");
			System.exit(-1);
			}
	}
	
	
	@Override
	protected void ProcesaValores() {
		
        try {
        	URL F=new URL("https://medpix.nlm.nih.gov/rest/caseofweek/list?count="+querryMax);
       	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      	  DocumentBuilder db = dbf.newDocumentBuilder();
      	  Document doc = db.parse(F.openStream());
      	  doc.getDocumentElement().normalize();
      	  NodeList nodeLstT = doc.getElementsByTagName("cases");
      	  for (int s = 0; s < nodeLstT.getLength(); s++) {
      		  Node fstNode = nodeLstT.item(s);
      		  if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
      			  {
      				Element eElement = (Element) fstNode;
      				String encounterIDS="nulo";
      			try {
      				encounterIDS = eElement.getElementsByTagName("encounterID").item(0).getTextContent();
				} catch (Exception e) {
					Logs.add("Documento sin encounterID");
				}
      				

      				
      			encounterID.add(encounterIDS);
      			
      				
      				
      		  }
      		  }
      	//	  ListaSer.add(fstNode.getFirstChild().getNodeValue());
         	  }
       	  
		} catch (Exception e) {
			e.printStackTrace();
			Logs.add("Error con la carga de listas de documento");
//			throw new RuntimeException("No tiene editor o los elementos son incorrectos");
		}
        
        
	}
		
	
	
	@Override
	public String getName() {
		return "MedPix WeekCases (Sin Ficha Simple)";
	}

}
