package it.zeze.util;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ListUtil {
    
    
    private static final Log log = LogFactory.getLog(ListUtil.class);    
    
	public static List<String> parseStringToList(String stringToParse, String separatore) {
		List<String> listToReturn = new ArrayList<String>();
		stringToParse = stringToParse.trim();
		int indiceStart = 0;
		int indiceEnd = stringToParse.indexOf(separatore, indiceStart);
		
		if (indiceEnd == -1){
			listToReturn.add(stringToParse);
		}
		else {
			String currentStringItem = stringToParse.substring(indiceStart, indiceEnd);
			listToReturn.add(currentStringItem);
			indiceStart = indiceEnd+1;
			indiceEnd = stringToParse.indexOf(separatore, indiceStart);
			while (indiceEnd!=-1){
				currentStringItem = stringToParse.substring(indiceStart, indiceEnd);
				listToReturn.add(currentStringItem);
				indiceStart = indiceEnd+1;
				indiceEnd = stringToParse.indexOf(separatore, indiceStart);
			}
			indiceEnd = stringToParse.length();
			currentStringItem = stringToParse.substring(indiceStart, indiceEnd);
			listToReturn.add(currentStringItem);
		}
		if (log.isDebugEnabled()){
			log.debug("parseStringToList, lista di ritorno "+listToReturn);
		}
		return listToReturn;
	}
	
	public static String parseListToString (List<String> listToParse, String separatore){
		String stringToReturn = "";
		if (listToParse != null && !listToParse.isEmpty()){
			String currentItem;
			for (int i=0; i<listToParse.size(); i++){
				currentItem = listToParse.get(i);
				if (i==0){
					stringToReturn = stringToReturn.concat(currentItem);
				} else {
					stringToReturn = stringToReturn.concat(separatore).concat(currentItem);
				}
			}
		}
		return stringToReturn;
	}

}
