/**
 * 
 */
package fdi.ucm.server.importparser.medpix;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fdi.ucm.server.modelComplete.ImportExportPair;
import fdi.ucm.server.modelComplete.LoadCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionAndLog;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteLinkElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementURL;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteLinkElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteResourceElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;

/**
 * @author Joaquin Gayoso Cabada
 *
 */
public class LoadCollectionMedPix extends LoadCollection{

	
	private CompleteCollection CC;
	private ArrayList<String> Logs;
	private CompleteLinkElementType encounterIDL;
	private HashMap<String,CompleteDocuments> encounterID;
	private CompleteLinkElementType encounterIDLC;
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadCollectionMedPix LC=new LoadCollectionMedPix();
		CompleteCollectionAndLog Salida=LC.processCollecccion(new ArrayList<String>());
		if (Salida!=null)
			{
			
			System.out.println("Correcto");
			System.exit(0);
			
			}
		else
			{
			System.err.println("Error");
			System.exit(-1);
			}
	}

	

	@Override
	public CompleteCollectionAndLog processCollecccion(ArrayList<String> dateEntrada) {
		try {
			CompleteCollectionAndLog Salida=new CompleteCollectionAndLog();
			CC=new CompleteCollection("MedPix", new Date()+"");
			Salida.setCollection(CC);
			Logs=new ArrayList<String>();
			encounterID=new HashMap<String,CompleteDocuments>();
			
			
			ProcesaCasos();
			ProcesaCasoID();
			//AQUI se puede trabajar
			
			
			return Salida;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	private void ProcesaCasoID() {
		CompleteGrammar CG=new CompleteGrammar("CasosCompleto", "CasosCompleto", CC);
		CC.getMetamodelGrammar().add(CG);
		
		HashMap<String,CompleteElementType> tabla= ProcesaGramaticaCasoID(CG);
		
	}



	



	private void ProcesaCasos() {

		CompleteGrammar CG=new CompleteGrammar("CasosSimple", "CasosSimple", CC);
		CC.getMetamodelGrammar().add(CG);
		
		HashMap<String,CompleteElementType> tabla= ProcesaGramaticaCasos(CG);
		
		ProcesaValores(tabla);
		
	}



	private void ProcesaValores(HashMap<String, CompleteElementType> tabla) {
		
        try {
        	URL F=new URL("https://medpix.nlm.nih.gov/rest/caseofweek/list?count=2000");
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
      				
      				CompleteDocuments cd=new CompleteDocuments(CC, "", "");
      				CC.getEstructuras().add(cd);
      				
      				for (Entry<String, CompleteElementType> entryTabla : tabla.entrySet()) {
      					String Valor = eElement.getElementsByTagName(entryTabla.getKey()).item(0).getTextContent();
      					if (Valor!=null&&!Valor.isEmpty())
      					{
      					if (entryTabla.getValue() instanceof CompleteTextElementType)
      					{
      						CompleteTextElement TE=new CompleteTextElement((CompleteTextElementType) entryTabla.getValue(), Valor);
      						cd.getDescription().add(TE);
      						TE.setDocumentsFather(cd);
      						
      						if (entryTabla.getKey().equals("history"))
      							cd.setDescriptionText(Valor);
      						
      						if (entryTabla.getKey().equals("encounterID"))
      							{
      							encounterID.put(Valor, cd);
      							}
      						
      					}else if (entryTabla.getValue() instanceof CompleteResourceElementType)
      					{
      						CompleteResourceElementURL TE=new CompleteResourceElementURL((CompleteResourceElementType) entryTabla.getValue(), "https://medpix.nlm.nih.gov"+Valor);
      						cd.getDescription().add(TE);
      						TE.setDocumentsFather(cd);
      						
      						if (entryTabla.getKey().equals("imageThumbURL"))
      							cd.setIcon("https://medpix.nlm.nih.gov"+Valor);
      						
      					}
      						
      						
      					}
      					else
      						Logs.add("Error por falta de datos en casos para parametro "+entryTabla.getValue() );
					}
      				
      				
      		  }
      		  }
      	//	  ListaSer.add(fstNode.getFirstChild().getNodeValue());
         	  }
       	  
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("No tiene editor o los elementos son incorrectos");
		}
        
        
	}



	private HashMap<String, CompleteElementType> ProcesaGramaticaCasos(CompleteGrammar cG) {
		HashMap<String, CompleteElementType> Salida=new HashMap<String, CompleteElementType>();
		
		CompleteElementType encounterID=new CompleteElementType("encounterID", cG);
		cG.getSons().add(encounterID);

		
		CompleteTextElementType encounterIDT=new CompleteTextElementType("encounterID", encounterID, cG);
		encounterID.getSons().add(encounterIDT);
		Salida.put("encounterID", encounterIDT);
		
		encounterIDL=new CompleteLinkElementType("encounterID", encounterID, cG);
		encounterID.getSons().add(encounterIDL);

		
		CompleteTextElementType imageID=new CompleteTextElementType("imageID", cG);
		cG.getSons().add(imageID);
		Salida.put("imageID", imageID);
		
		CompleteResourceElementType imageURL=new CompleteResourceElementType("imageURL", cG);
		cG.getSons().add(imageURL);
		Salida.put("imageURL", imageURL);
		
		CompleteResourceElementType imageThumbURL=new CompleteResourceElementType("imageThumbURL", cG);
		cG.getSons().add(imageThumbURL);
		Salida.put("imageThumbURL", imageThumbURL);
		
		CompleteTextElementType history=new CompleteTextElementType("history", cG);
		cG.getSons().add(history);
		Salida.put("history", history);
		
		CompleteTextElementType age=new CompleteTextElementType("age", cG);
		cG.getSons().add(age);
		Salida.put("age", age);
		
		CompleteTextElementType sex=new CompleteTextElementType("sex", cG);
		cG.getSons().add(sex);
		Salida.put("sex", sex);
		
		CompleteTextElementType quiz=new CompleteTextElementType("quiz", cG);
		cG.getSons().add(quiz);
		Salida.put("quiz", quiz);
		
		CompleteTextElementType offset=new CompleteTextElementType("offset", cG);
		cG.getSons().add(offset);
		Salida.put("offset", offset);
		
		CompleteTextElementType caseNumber=new CompleteTextElementType("caseNumber", cG);
		cG.getSons().add(caseNumber);
		Salida.put("caseNumber", caseNumber);
		
		return Salida;
	}

	private HashMap<String, CompleteElementType> ProcesaGramaticaCasoID(CompleteGrammar cG) {
		HashMap<String, CompleteElementType> Salida=new HashMap<String, CompleteElementType>();
		
		CompleteElementType encounterID=new CompleteElementType("encounterID", cG);
		cG.getSons().add(encounterID);

		
		CompleteTextElementType encounterIDT=new CompleteTextElementType("encounterID", encounterID, cG);
		encounterID.getSons().add(encounterIDT);
		Salida.put("encounterID", encounterIDT);
		
		encounterIDLC=new CompleteLinkElementType("encounterID", encounterID, cG);
		encounterID.getSons().add(encounterIDLC);

		
		CompleteTextElementType dxHow=new CompleteTextElementType("dxHow", cG);
		cG.getSons().add(dxHow);
		Salida.put("dxHow", dxHow);
		
		CompleteTextElementType age=new CompleteTextElementType("age", cG);
		cG.getSons().add(age);
		Salida.put("age", age);
		
		CompleteTextElementType sex=new CompleteTextElementType("sex", cG);
		cG.getSons().add(sex);
		Salida.put("sex", sex);
		
		CompleteTextElementType diagnosis=new CompleteTextElementType("diagnosis", cG);
		cG.getSons().add(diagnosis);
		Salida.put("diagnosis", diagnosis);
		
		CompleteTextElementType exam=new CompleteTextElementType("exam", cG);
		cG.getSons().add(exam);
		Salida.put("exam", exam);
		
		CompleteTextElementType authorID=new CompleteTextElementType("authorID", cG);
		cG.getSons().add(authorID);
		Salida.put("authorID", authorID);
		
		CompleteTextElementType authorName=new CompleteTextElementType("authorName", cG);
		cG.getSons().add(authorName);
		Salida.put("authorName", authorName);
		
		CompleteTextElementType authorAffiliation=new CompleteTextElementType("authorAffiliation", cG);
		cG.getSons().add(authorAffiliation);
		Salida.put("authorAffiliation", authorAffiliation);
		
		CompleteResourceElementType authorImage=new CompleteResourceElementType("authorImage", cG);
		cG.getSons().add(authorImage);
		Salida.put("authorImage", authorImage);
		
		
		///authorEmail
		
		return Salida;
	}

	@Override
	public ArrayList<ImportExportPair> getConfiguracion() {
		return new ArrayList<ImportExportPair>();
	}

	@Override
	public String getName() {
		return "MedPix";
	}

	@Override
	public boolean getCloneLocalFiles() {
		return false;
	}

}
