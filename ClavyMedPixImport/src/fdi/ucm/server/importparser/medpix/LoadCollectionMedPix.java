/**
 * 
 */
package fdi.ucm.server.importparser.medpix;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

	
	private List<CompleteElementTypeencounterIDImage> ListImageEncounter;
	private CompleteCollection CC;
	private ArrayList<String> Logs;
	private CompleteLinkElementType encounterIDL;
	private HashMap<String,CompleteDocuments> encounterID;
	private CompleteLinkElementType encounterIDLC;
	private CompleteLinkElementType topicIDTC;
	public static boolean consoleDebug=false;
	private int querryMax=2000;
	private CompleteLinkElementType topicIDIDLC;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadCollectionMedPix LC=new LoadCollectionMedPix();
		LoadCollectionMedPix.consoleDebug=true;
		
		
		
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
	public CompleteCollectionAndLog processCollecccion(ArrayList<String> dateEntrada) {
		try {
			CompleteCollectionAndLog Salida=new CompleteCollectionAndLog();
			CC=new CompleteCollection("MedPix", new Date()+"");
			Salida.setCollection(CC);
			Logs=new ArrayList<String>();
			Salida.setLogLines(Logs);
			encounterID=new HashMap<String,CompleteDocuments>();
			ListImageEncounter=new ArrayList<CompleteElementTypeencounterIDImage>();
			
			ProcesaCasos();
			ProcesaCasoID();
			ProcesaTopics();
			//AQUI se puede trabajar
			
			
			return Salida;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	private void ProcesaTopics() {
		CompleteGrammar CG=new CompleteGrammar("Topics", "Topics", CC);
		CC.getMetamodelGrammar().add(CG);
		
		HashMap<String,CompleteElementType> tabla= ProcesaGramaticaTopics(CG);
//		ProcesaValoresCasoID(tabla);
		
	}



	private void ProcesaCasoID() {
		CompleteGrammar CG=new CompleteGrammar("CasosCompleto", "CasosCompleto", CC);
		CC.getMetamodelGrammar().add(CG);
		
		HashMap<String,CompleteElementType> tabla= ProcesaGramaticaCasoID(CG);
		ProcesaValoresCasoID(tabla);
	}



	



	private void ProcesaValoresCasoID(HashMap<String, CompleteElementType> tabla) {
		
		for (Entry<String, CompleteDocuments> Entryvalues : encounterID.entrySet()) {
			String IDvalues=Entryvalues.getKey();
			CompleteDocuments IDDoc=Entryvalues.getValue();
			try {
	        	URL F=new URL("https://medpix.nlm.nih.gov/rest/encounter?encounterID="+IDvalues);
	       	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	      	  DocumentBuilder db = dbf.newDocumentBuilder();
	      	  Document doc = db.parse(F.openStream());
	      	  doc.getDocumentElement().normalize();
	      	     	
	      	  
	      	
	      	  
	      	  NodeList nodeLstT = doc.getElementsByTagName("EncounterRest");
	      		  Node fstNode = doc.getElementsByTagName("EncounterRest").item(0);
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
	      							
	      							CompleteLinkElement CLE=new CompleteLinkElement(encounterIDLC, IDDoc);
	      							cd.getDescription().add(CLE);
	      							CLE.setDocumentsFather(cd);	
	      							
	      							CompleteLinkElement CLEC=new CompleteLinkElement(encounterIDL, cd);
	      							IDDoc.getDescription().add(CLEC);
	      							CLEC.setDocumentsFather(IDDoc);
	      							
	      							
	      							}
	      						
	      					}else if (entryTabla.getValue() instanceof CompleteResourceElementType)
	      					{
	      						CompleteResourceElementURL TE=new CompleteResourceElementURL((CompleteResourceElementType) entryTabla.getValue(), "https://medpix.nlm.nih.gov"+Valor);
	      						cd.getDescription().add(TE);
	      						TE.setDocumentsFather(cd);
	      						
//	      						if (entryTabla.getKey().equals("imageThumbURL"))
//	      							cd.setIcon("https://medpix.nlm.nih.gov"+Valor);
	      						
	      					}
	      						
	      						
	      					}
	      					else
	      						if (consoleDebug)
	      						System.out.println("Documento (encounterID: "+IDvalues+") : Error por falta de datos para parametro "+entryTabla.getKey() );
						
	      				}
	      				
	      				
	      				try {
	      					NodeList ListaImagenes=((Element) eElement.getElementsByTagName("imageList").item(0)).getElementsByTagName("imageList");
	      					for (int i = 0; i < ListaImagenes.getLength(); i++) {
	      						 Node imagenNode = nodeLstT.item(i);
	      			      		  if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
	      			      			  
	      			      			Element imagenNodeElem = (Element) imagenNode;
	      			      			  if (imagenNodeElem!=null)
	      			      			  {
	      			      			  while (ListImageEncounter.size()<=i)
	      			      			  	{
	      			      				CompleteElementTypeencounterIDImage cona = ListImageEncounter.get(0);
	      			      				CompleteElementTypeencounterIDImage nuevo = new CompleteElementTypeencounterIDImage(cona);
	      			      				ArrayList<CompleteElementType> nueva=new ArrayList<>();
	      			      				
	      			      				
	      			      				boolean found=false;
	      			      				for (CompleteElementType completeElementType : cona.getElement().getCollectionFather().getSons()) {
	      			      					
	      			      					if (completeElementType.getClassOfIterator()==null&&completeElementType==cona.getElement())
	      			      						found=true;
	      			      					else if (completeElementType.getClassOfIterator()!=null&&completeElementType.getClassOfIterator().equals(cona.getElement()))
	      			      						found=true;
											else
												if (found)
													nueva.add(nuevo.getElement());
											
											nueva.add(completeElementType);
											
										}
	      			      				
	      			      				cona.getElement().getCollectionFather().setSons(nueva);
	      			      				
	      			      				ListImageEncounter.add(nuevo);
	      			      				
	      			      			  	}
	      			      			  
	      			      			  CompleteElementTypeencounterIDImage ImageMio = ListImageEncounter.get(i);
	      			      			  
	      			      			for (Entry<String, CompleteElementType> entryTabla : ImageMio.getTablaHijos().entrySet()) {
	      			      
	      			      	
	      		      					String Valor = imagenNodeElem.getElementsByTagName(entryTabla.getKey()).item(0).getTextContent();
	      		      					if (Valor!=null&&!Valor.isEmpty())
	      		      					{
	      		      					if (entryTabla.getValue() instanceof CompleteTextElementType)
	      		      					{
	      		      						CompleteTextElement TE=new CompleteTextElement((CompleteTextElementType) entryTabla.getValue(), Valor);
	      		      						cd.getDescription().add(TE);
	      		      						TE.setDocumentsFather(cd);
	      		      						

	      		      						
	      		      					}else if (entryTabla.getValue() instanceof CompleteResourceElementType)
	      		      					{
	      		      						CompleteResourceElementURL TE=new CompleteResourceElementURL((CompleteResourceElementType) entryTabla.getValue(), "https://medpix.nlm.nih.gov"+Valor);
	      		      						cd.getDescription().add(TE);
	      		      						TE.setDocumentsFather(cd);
	      		      						
	      		      					if (entryTabla.getKey().equals("thumbImageURL")&&i==0)
	    	      							cd.setIcon("https://medpix.nlm.nih.gov"+Valor);
	      		      						
	      		      						
	      		      					}
	      		      						
	      		      						
	      		      					
	      		      					
	      		      					}else
	      		      					if (consoleDebug)
	      		      						System.out.println("Documento (encounterID: "+IDvalues+") : Error por falta de datos (imagenes) para parametro "+entryTabla.getKey() );

	      			      				
	      		      					
	      							
	      		      				}
	      			      			  
	      			      		  }
	      			      		  }
							}
						} catch (Exception e) {
							if (consoleDebug)
								e.printStackTrace();
							Logs.add("Error con la carga imagenes del documento->encounterID: "+IDvalues);
						}
	
	      				
	      				
	      				
	      		  
	      		  }
	      	//	  ListaSer.add(fstNode.getFirstChild().getNodeValue());
	         	  }
	       	  
			} catch (Exception e) {
				e.printStackTrace();
				Logs.add("Error con la carga de documento->encounterID: "+IDvalues);
//				throw new RuntimeException("No tiene editor o los elementos son incorrectos");
			}
		}
		
		
		
	}



	private void ProcesaCasos() {

		CompleteGrammar CG=new CompleteGrammar("CasosSimple", "CasosSimple", CC);
		CC.getMetamodelGrammar().add(CG);
		
		HashMap<String,CompleteElementType> tabla= ProcesaGramaticaCasos(CG);
		
		ProcesaValores(tabla);
		
	}



	private void ProcesaValores(HashMap<String, CompleteElementType> tabla) {
		
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
      						if (consoleDebug)
      						System.out.println("Lista de Casos (encounterID: "+encounterIDS+"): Error por falta de datos en casos para parametro "+entryTabla.getKey() );
					}
      				
      				
      		  }
      		  }
      	//	  ListaSer.add(fstNode.getFirstChild().getNodeValue());
         	  }
       	  
		} catch (Exception e) {
			if (consoleDebug)
			e.printStackTrace();
			Logs.add("Error con la carga de listas de documento");
//			throw new RuntimeException("No tiene editor o los elementos son incorrectos");
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

	private HashMap<String, CompleteElementType> ProcesaGramaticaTopics(CompleteGrammar cG) {
		HashMap<String, CompleteElementType> Salida=new HashMap<String, CompleteElementType>();
		
		CompleteElementType topicID=new CompleteElementType("topicID", cG);
		cG.getSons().add(topicID);

		
		CompleteTextElementType topicIDT=new CompleteTextElementType("topicID", topicID, cG);
		topicID.getSons().add(topicIDT);
		Salida.put("topicID", topicIDT);
		
		topicIDIDLC=new CompleteLinkElementType("topicID", topicID, cG);
		topicID.getSons().add(topicIDIDLC);

		
		CompleteTextElementType factoid=new CompleteTextElementType("factoid", cG);
		cG.getSons().add(factoid);
		Salida.put("factoid", factoid);
		
		CompleteTextElementType preacr=new CompleteTextElementType("preacr", cG);
		cG.getSons().add(preacr);
		Salida.put("preacr", preacr);
		
		CompleteTextElementType postacr=new CompleteTextElementType("postacr", cG);
		cG.getSons().add(postacr);
		Salida.put("age", postacr);
		
		CompleteTextElementType acrCode=new CompleteTextElementType("acrCode", cG);
		cG.getSons().add(acrCode);
		Salida.put("sex", acrCode);
		
		CompleteTextElementType reference=new CompleteTextElementType("reference", cG);
		cG.getSons().add(reference);
		Salida.put("reference", reference);
		
		CompleteTextElementType location=new CompleteTextElementType("location", cG);
		cG.getSons().add(location);
		Salida.put("location", location);
		
		CompleteTextElementType subLocation=new CompleteTextElementType("subLocation", cG);
		cG.getSons().add(subLocation);
		Salida.put("subLocation", subLocation);
		
		CompleteTextElementType categoryID=new CompleteTextElementType("categoryID", cG);
		cG.getSons().add(categoryID);
		Salida.put("categoryID", categoryID);
		
		CompleteTextElementType subCategory=new CompleteTextElementType("subCategory", cG);
		cG.getSons().add(subCategory);
		Salida.put("subCategory", subCategory);
		
		CompleteTextElementType subCategoryID=new CompleteTextElementType("subCategoryID", cG);
		cG.getSons().add(subCategoryID);
		Salida.put("subCategoryID", subCategoryID);
		
		CompleteResourceElementType author=new CompleteResourceElementType("author", cG);
		cG.getSons().add(author);
		Salida.put("author", author);
				
		CompleteResourceElementType submitName=new CompleteResourceElementType("submitName", cG);
		cG.getSons().add(submitName);
		Salida.put("submitName", submitName);
		
		
		//AQUI ME QUEDE ABURRIENDOME
		
		
		CompleteTextElementType approverID=new CompleteTextElementType("approverID", cG);
		cG.getSons().add(approverID);
		Salida.put("approverID", approverID);
		
		CompleteResourceElementType approverEmail=new CompleteResourceElementType("approverEmail", cG);
		cG.getSons().add(approverEmail);
		Salida.put("approverEmail", approverEmail);
		
		CompleteTextElementType approverName=new CompleteTextElementType("approverName", cG);
		cG.getSons().add(approverName);
		Salida.put("approverName", approverName);
		
		CompleteTextElementType approverAffiliation=new CompleteTextElementType("approverAffiliation", cG);
		cG.getSons().add(approverAffiliation);
		Salida.put("approverAffiliation", approverAffiliation);
		
		CompleteResourceElementType approverImage=new CompleteResourceElementType("approverImage", cG);
		cG.getSons().add(approverImage);
		Salida.put("approverImage", approverImage);
		
		CompleteTextElementType findings=new CompleteTextElementType("findings", cG);
		cG.getSons().add(findings);
		Salida.put("findings", findings);
		
		CompleteTextElementType ddx=new CompleteTextElementType("ddx", cG);
		cG.getSons().add(ddx);
		Salida.put("ddx", ddx);
		
		CompleteTextElementType txFollowup=new CompleteTextElementType("txFollowup", cG);
		cG.getSons().add(txFollowup);
		Salida.put("txFollowup", txFollowup);
		
		CompleteTextElementType discussion=new CompleteTextElementType("discussion", cG);
		cG.getSons().add(discussion);
		Salida.put("discussion", discussion);
		
		
		CompleteElementType topicID=new CompleteElementType("topicID", cG);
		cG.getSons().add(topicID);

		
		CompleteTextElementType topicIDT=new CompleteTextElementType("topicIDT", topicID, cG);
		encounterID.getSons().add(topicIDT);
		Salida.put("topicID", topicIDT);
		
		topicIDTC=new CompleteLinkElementType("topicIDT", topicID, cG);
		encounterID.getSons().add(topicIDTC);
	
		CompleteTextElementType mCaseID=new CompleteTextElementType("mCaseID", cG);
		cG.getSons().add(mCaseID);
		Salida.put("mCaseID", mCaseID);
		
		CompleteElementTypeencounterIDImage imageList=new CompleteElementTypeencounterIDImage("imageList", cG);
		cG.getSons().add(imageList.getElement());
		
		ListImageEncounter.add(imageList);
			
		CompleteTextElementType error=new CompleteTextElementType("error", cG);
		cG.getSons().add(error);
		Salida.put("error", error);
		
		CompleteTextElementType contributorsCSV=new CompleteTextElementType("contributorsCSV", cG);
		cG.getSons().add(contributorsCSV);
		Salida.put("contributorsCSV", contributorsCSV);
		
		CompleteTextElementType affiliation=new CompleteTextElementType("affiliation", cG);
		cG.getSons().add(affiliation);
		Salida.put("affiliation", affiliation);
		
		CompleteTextElementType affiliationID=new CompleteTextElementType("affiliationID", cG);
		cG.getSons().add(affiliationID);
		Salida.put("affiliationID", affiliationID);
		
		CompleteResourceElementType affiliationLogo=new CompleteResourceElementType("affiliationLogo", cG);
		cG.getSons().add(affiliationLogo);
		Salida.put("affiliationLogo", affiliationLogo);
		
		CompleteResourceElementType mediaList=new CompleteResourceElementType("mediaList", cG);
		cG.getSons().add(mediaList);
		Salida.put("mediaList", mediaList);
		
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
		
		CompleteTextElementType history=new CompleteTextElementType("history", cG);
		cG.getSons().add(history);
		Salida.put("history", history);
		
		CompleteTextElementType age=new CompleteTextElementType("age", cG);
		cG.getSons().add(age);
		Salida.put("age", age);
		
		CompleteTextElementType sex=new CompleteTextElementType("sex", cG);
		cG.getSons().add(sex);
		Salida.put("sex", sex);
		
		CompleteTextElementType race=new CompleteTextElementType("race", cG);
		cG.getSons().add(race);
		Salida.put("race", race);
		
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
				
		CompleteResourceElementType authorEmail=new CompleteResourceElementType("authorEmail", cG);
		cG.getSons().add(authorEmail);
		Salida.put("authorEmail", authorEmail);
		
		CompleteTextElementType approverID=new CompleteTextElementType("approverID", cG);
		cG.getSons().add(approverID);
		Salida.put("approverID", approverID);
		
		CompleteResourceElementType approverEmail=new CompleteResourceElementType("approverEmail", cG);
		cG.getSons().add(approverEmail);
		Salida.put("approverEmail", approverEmail);
		
		CompleteTextElementType approverName=new CompleteTextElementType("approverName", cG);
		cG.getSons().add(approverName);
		Salida.put("approverName", approverName);
		
		CompleteTextElementType approverAffiliation=new CompleteTextElementType("approverAffiliation", cG);
		cG.getSons().add(approverAffiliation);
		Salida.put("approverAffiliation", approverAffiliation);
		
		CompleteResourceElementType approverImage=new CompleteResourceElementType("approverImage", cG);
		cG.getSons().add(approverImage);
		Salida.put("approverImage", approverImage);
		
		CompleteTextElementType findings=new CompleteTextElementType("findings", cG);
		cG.getSons().add(findings);
		Salida.put("findings", findings);
		
		CompleteTextElementType ddx=new CompleteTextElementType("ddx", cG);
		cG.getSons().add(ddx);
		Salida.put("ddx", ddx);
		
		CompleteTextElementType txFollowup=new CompleteTextElementType("txFollowup", cG);
		cG.getSons().add(txFollowup);
		Salida.put("txFollowup", txFollowup);
		
		CompleteTextElementType discussion=new CompleteTextElementType("discussion", cG);
		cG.getSons().add(discussion);
		Salida.put("discussion", discussion);
		
		
		CompleteElementType topicID=new CompleteElementType("topicID", cG);
		cG.getSons().add(topicID);

		
		CompleteTextElementType topicIDT=new CompleteTextElementType("topicIDT", topicID, cG);
		encounterID.getSons().add(topicIDT);
		Salida.put("topicID", topicIDT);
		
		topicIDTC=new CompleteLinkElementType("topicIDT", topicID, cG);
		encounterID.getSons().add(topicIDTC);
	
		CompleteTextElementType mCaseID=new CompleteTextElementType("mCaseID", cG);
		cG.getSons().add(mCaseID);
		Salida.put("mCaseID", mCaseID);
		
		CompleteElementTypeencounterIDImage imageList=new CompleteElementTypeencounterIDImage("imageList", cG);
		cG.getSons().add(imageList.getElement());
		
		ListImageEncounter.add(imageList);
			
		CompleteTextElementType error=new CompleteTextElementType("error", cG);
		cG.getSons().add(error);
		Salida.put("error", error);
		
		CompleteTextElementType contributorsCSV=new CompleteTextElementType("contributorsCSV", cG);
		cG.getSons().add(contributorsCSV);
		Salida.put("contributorsCSV", contributorsCSV);
		
		CompleteTextElementType affiliation=new CompleteTextElementType("affiliation", cG);
		cG.getSons().add(affiliation);
		Salida.put("affiliation", affiliation);
		
		CompleteTextElementType affiliationID=new CompleteTextElementType("affiliationID", cG);
		cG.getSons().add(affiliationID);
		Salida.put("affiliationID", affiliationID);
		
		CompleteResourceElementType affiliationLogo=new CompleteResourceElementType("affiliationLogo", cG);
		cG.getSons().add(affiliationLogo);
		Salida.put("affiliationLogo", affiliationLogo);
		
		CompleteResourceElementType mediaList=new CompleteResourceElementType("mediaList", cG);
		cG.getSons().add(mediaList);
		Salida.put("mediaList", mediaList);
		
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
