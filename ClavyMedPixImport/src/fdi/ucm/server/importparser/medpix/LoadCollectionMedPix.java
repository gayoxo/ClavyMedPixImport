/**
 * 
 */
package fdi.ucm.server.importparser.medpix;

import java.util.ArrayList;

import fdi.ucm.server.modelComplete.ImportExportPair;
import fdi.ucm.server.modelComplete.LoadCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionAndLog;

/**
 * @author Joaquin Gayoso Cabada
 *
 */
public class LoadCollectionMedPix extends LoadCollection{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadCollectionMedPix LC=new LoadCollectionMedPix();
		LC.processCollecccion(new ArrayList<String>());
	}

	@Override
	public CompleteCollectionAndLog processCollecccion(ArrayList<String> dateEntrada) {
		// TODO Auto-generated method stub
		return null;
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
