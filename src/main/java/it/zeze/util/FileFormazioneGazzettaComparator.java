package it.zeze.util;

import java.io.File;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

public class FileFormazioneGazzettaComparator implements Comparator<File> {

	public int compare(File o1, File o2) {
		int toReturn = 0;
		String nomeFile1 = o1.getName();
		String nomeFile2 = o2.getName();
		String nomeFileFG = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_PROB_FORMAZIONI_GAZZETTA);
		nomeFileFG = StringUtils.remove(nomeFileFG, "/");
		nomeFileFG = StringUtils.substringBefore(nomeFileFG, "{giornata}");
		String numeroGiornataFile1 = StringUtils.substringBetween(nomeFile1, nomeFileFG, ".html");
		String numeroGiornataFile2 = StringUtils.substringBetween(nomeFile2, nomeFileFG, ".html");
		if (Integer.valueOf(numeroGiornataFile1) > Integer.valueOf(numeroGiornataFile2)) {
			toReturn = 1;
		}
		if (Integer.valueOf(numeroGiornataFile1) < Integer.valueOf(numeroGiornataFile2)) {
			toReturn = -1;
		}
		if (Integer.valueOf(numeroGiornataFile1) == Integer.valueOf(numeroGiornataFile2)) {
			toReturn = 0;
		}
		return toReturn;
	}
}
