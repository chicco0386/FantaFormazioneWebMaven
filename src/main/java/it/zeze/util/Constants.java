package it.zeze.util;

import org.apache.commons.lang.StringUtils;

public class Constants {

	public static final String CONF_KEY_HTML_ROOT = "pathHTMLRoot";

	public static final String CONF_KEY_HTML_FILE_SQUADRE = "nomeFileHTMLSquadre";

	public static final String CONF_KEY_HTML_FILE_GIOCATORI_PORTIERI = "nomeFileHTMLGiocatoriP";
	public static final String CONF_KEY_HTML_FILE_GIOCATORI_DIFENSORI = "nomeFileHTMLGiocatoriD";
	public static final String CONF_KEY_HTML_FILE_GIOCATORI_CENTROCAMPISTI = "nomeFileHTMLGiocatoriC";
	public static final String CONF_KEY_HTML_FILE_GIOCATORI_ATTACCANTI = "nomeFileHTMLGiocatoriA";

	public static final String CONF_KEY_HTML_FILE_CALENDARIO = "nomeFileHTMLCalendario";
	public static final String CONF_KEY_HTML_FILE_CALENDARIO_NEW = "nomeFileHTMLCalendarioNew";
	public static final String STRING_TO_REPLACE_NOME_FILE_CALENDARIO_NEW = "{giornata}";

	public static final String CONF_KEY_HTML_FILE_STATISTICHE_G = "nomeFileHTMLStatisticheG";
	public static final String STRING_TO_REPLACE_NOME_FILE_GIORNATE = "{giornata}";

	public static final String CONF_KEY_HTML_FILE_PROB_FORMAZIONI_FG = "nomeFileHTMLProbFormazioniFG";
	public static final String CONF_KEY_HTML_FILE_PROB_FORMAZIONI_GAZZETTA = "nomeFileHTMLProbFormazioniGazzetta";

	public static final String GIOCATORI_RUOLO_PORTIERE = "P";
	public static final String GIOCATORI_RUOLO_DIFENSORE = "D";
	public static final String GIOCATORI_RUOLO_CENTROCAMPISTA = "C";
	public static final String GIOCATORI_RUOLO_ATTACCANTE = "A";

	public static final int PROB_FANTA_GAZZETTA = 3;
	public static final int PROB_GAZZETTA = 5;

	public static String getStagione(String stagioneInput) {
		String toReturn = stagioneInput;
		if (stagioneInput.length() > 7) {
			String temp = stagioneInput;
			temp = StringUtils.deleteWhitespace(stagioneInput);
			String toReplace = StringUtils.substringAfterLast(temp, "/");
			if (toReplace.length() > 2) {
				temp = StringUtils.substring(toReplace, 2);
			}
			toReturn = StringUtils.replace(toReturn, toReplace, temp);
		}
		return toReturn;
	}

	public static void main(String args[]) {
		int prob = 0;
		System.out.println(prob % PROB_FANTA_GAZZETTA);
	}

}
