package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniFg;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniFgId;
import it.zeze.html.cleaner.HtmlCleanerUtil;
import it.zeze.util.ConfigurationUtil;
import it.zeze.util.Constants;
import it.zeze.util.FileFormazioneFGComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

@Name("probabiliFormazioniFgList")
public class ProbabiliFormazioniFgList extends EntityQuery<ProbabiliFormazioniFg> {

	private static final long serialVersionUID = 3908423851085890461L;

	@Logger
	static Log log;

	@In(create = true)
	GiocatoriList giocatoriList;

	@In(create = true)
	ProbabiliFormazioniFgHome probabiliFormazioniFgHome;

	@In(create = true)
	GiornateList giornateList;

	private static final String EJBQL = "select probabiliFormazioniFg from ProbabiliFormazioniFg probabiliFormazioniFg";

	private static final String[] RESTRICTIONS = {};

	private static final String SELECT_BY_ID_GIOCATORE_ID_GIORNATA = "select probabiliFormazioniFg from ProbabiliFormazioniFg probabiliFormazioniFg where probabiliFormazioniFg.id.idGiocatore=:idGiocatore and probabiliFormazioniFg.id.idGiornata=:idGiornata";
	private static final String SELECT_COUNT_ID_GIORNATA = "select count(probabiliFormazioniFg.id.idGiornata) from ProbabiliFormazioniFg probabiliFormazioniFg where probabiliFormazioniFg.id.idGiornata=:idGiornata";

	private static final String DELETE_BY_ID_GIORNATA = "delete from ProbabiliFormazioniFg probabiliFormazioniFg where probabiliFormazioniFg.id.idGiornata=:idGiornata";

	private ProbabiliFormazioniFg probabiliFormazioniFg;

	public ProbabiliFormazioniFgList() {
		probabiliFormazioniFg = new ProbabiliFormazioniFg();
		probabiliFormazioniFg.setId(new ProbabiliFormazioniFgId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public ProbabiliFormazioniFg getProbabiliFormazioniFg() {
		return probabiliFormazioniFg;
	}

	public void unmarshallAndSaveFromHtmlFile() {
		log.info("unmashallAndSaveFromHtmlFile, entrato");
		String rootHTMLFiles = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_ROOT);
		String nomeFileFormazioneFG = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_PROB_FORMAZIONI_FG);
		String filtroFile = StringUtils.substringBefore(nomeFileFormazioneFG, "{giornata}");
		filtroFile = StringUtils.substringAfter(filtroFile, "/");
		Collection<File> collFiles = FileUtils.listFiles(new File(rootHTMLFiles), FileFilterUtils.prefixFileFilter(filtroFile), FileFilterUtils.falseFileFilter());
		List<File> listFile = new ArrayList<File>(collFiles);
		Collections.sort(listFile, new FileFormazioneFGComparator());
		Iterator<File> itFile = listFile.iterator();
		File currentFile;
		while (itFile.hasNext()) {
			currentFile = itFile.next();
			unmarshallAndSaveSingleHtmlFile(currentFile);
		}
		log.info("unmashallAndSaveFromHtmlFile, uscito");
	}

	public void unmarshallAndSaveSingleHtmlFile(File fileToElaborate) {
		log.info("unmarshallAndSaveSingleHtmlFile, entrato per elaborare il file [" + fileToElaborate.getAbsolutePath() + "]");
		List<TagNode> listMatchsNode;
		try {
			listMatchsNode = HtmlCleanerUtil.getListOfElementsByAttributeFromFile(fileToElaborate.getAbsolutePath(), "class", "score-probabili");
			List<TagNode> listPlayersNode = HtmlCleanerUtil.getListOfElementsByAttributeFromFile(fileToElaborate.getAbsolutePath(), "class", "player");
			String currentGiornataFromFile = HtmlCleanerUtil.getListOfElementsByAttributeFromFile(fileToElaborate.getAbsolutePath(), "id", "ContentPlaceHolderElle_Labelgiornata").get(0).getText().toString();
			String currentGiornata = StringUtils.remove(currentGiornataFromFile.trim().toLowerCase(), " giornata");
			String currentStagione = HtmlCleanerUtil.getListOfElementsByXPathFromFile(fileToElaborate.getAbsolutePath(), "//div[@id='article']/h2").get(0).getText().toString();
			currentStagione = StringUtils.substringAfter(currentStagione.trim(), "Probabili Formazioni Serie A - ").trim();
			currentStagione = StringUtils.substringBefore(currentStagione, currentGiornataFromFile);
			currentStagione = giornateList.getStagione(currentStagione.trim());
			log.info("Probabili formazioni FG per la " + "[" + currentGiornata + "]a giornata e stagione [" + currentStagione + "]");
			TagNode currentMatchNode = null;
			String squadraIn = "";
			String squadraOut = "";
			List<TagNode> listPlayersNameInNode = null;
			List<TagNode> listPlayersNameOutNode = null;
			TagNode currentPlayerNode = null;
			TagNode currentSinglePlayerNode = null;
			TagNode currentPlayerNomeNode;
			TagNode currentPlayerRuoloNode;
			String currentGiocatoreNome;
			String currentGiocatoreRuolo;
			// Cancello vecchie formazioni
			int idGiornata = giornateList.getIdGiornata(Integer.valueOf(currentGiornata), currentStagione);
			deleteByIdGiornata(idGiornata);
			for (int i = 0; i < listMatchsNode.size(); i++) {
				currentMatchNode = listMatchsNode.get(i);
				squadraIn = currentMatchNode.findElementByAttValue("class", "team-in-p", false, true).getText().toString();
				squadraOut = currentMatchNode.findElementByAttValue("class", "team-out-p", false, true).getText().toString();

				currentPlayerNode = listPlayersNode.get(i);
				// Prendo i giocatori in casa titolari
				listPlayersNameInNode = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPlayerNode, "//div[@class='in']//div[@class='name']");
				for (int y = 0; y < listPlayersNameInNode.size(); y++) {
					currentSinglePlayerNode = listPlayersNameInNode.get(y);
					currentPlayerNomeNode = currentSinglePlayerNode.findElementByName("a", false);
					currentGiocatoreNome = currentPlayerNomeNode.getText().toString();
					log.info("----  giocatore casa tit " + currentGiocatoreNome);
					currentPlayerRuoloNode = currentSinglePlayerNode.findElementByName("span", false);
					currentGiocatoreRuolo = currentPlayerRuoloNode.getText().toString();
					Giocatori giocatoriFormazione = getGiocatoreFormazione(currentGiocatoreNome, squadraIn, currentGiocatoreRuolo, currentStagione);
					probabiliFormazioniFgHome.clearInstance();
					ProbabiliFormazioniFgId instanceId = new ProbabiliFormazioniFgId(giocatoriFormazione.getId(), idGiornata, true, false);
					ProbabiliFormazioniFg instance = new ProbabiliFormazioniFg();
					instance.setId(instanceId);
					probabiliFormazioniFgHome.setInstance(instance);
					probabiliFormazioniFgHome.persist();
				}
				// Prendo i giocatori in casa panchina
				listPlayersNameInNode = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPlayerNode, "//div[@class='in']//div[@class='namesub']");
				for (int y = 0; y < listPlayersNameInNode.size(); y++) {
					currentSinglePlayerNode = listPlayersNameInNode.get(y);
					currentPlayerNomeNode = currentSinglePlayerNode.findElementByName("a", false);
					currentGiocatoreNome = currentPlayerNomeNode.getText().toString();
					log.info("----  giocatore casa panc " + currentGiocatoreNome);
					currentPlayerRuoloNode = currentSinglePlayerNode.findElementByName("span", false);
					currentGiocatoreRuolo = currentPlayerRuoloNode.getText().toString();
					Giocatori giocatoriFormazione = getGiocatoreFormazione(currentGiocatoreNome, squadraIn, currentGiocatoreRuolo, currentStagione);
					probabiliFormazioniFgHome.clearInstance();
					ProbabiliFormazioniFgId instanceId = new ProbabiliFormazioniFgId(giocatoriFormazione.getId(), idGiornata, false, true);
					ProbabiliFormazioniFg instance = new ProbabiliFormazioniFg();
					instance.setId(instanceId);
					probabiliFormazioniFgHome.setInstance(instance);
					probabiliFormazioniFgHome.persist();
				}

				// Prendo i giocatori fuori casa
				listPlayersNameOutNode = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPlayerNode, "//div[@class='out']//div[@class='name']");
				for (int y = 0; y < listPlayersNameOutNode.size(); y++) {
					currentSinglePlayerNode = listPlayersNameOutNode.get(y);
					currentPlayerNomeNode = currentSinglePlayerNode.findElementByName("a", false);
					currentGiocatoreNome = currentPlayerNomeNode.getText().toString();
					log.info("----  giocatore fuori tit " + currentGiocatoreNome);
					currentPlayerRuoloNode = currentSinglePlayerNode.findElementByName("span", false);
					currentGiocatoreRuolo = currentPlayerRuoloNode.getText().toString();
					Giocatori giocatoriFormazione = getGiocatoreFormazione(currentGiocatoreNome, squadraOut, currentGiocatoreRuolo, currentStagione);
					probabiliFormazioniFgHome.clearInstance();
					ProbabiliFormazioniFgId instanceId = new ProbabiliFormazioniFgId(giocatoriFormazione.getId(), idGiornata, true, false);
					ProbabiliFormazioniFg instance = new ProbabiliFormazioniFg();
					instance.setId(instanceId);
					probabiliFormazioniFgHome.setInstance(instance);
					probabiliFormazioniFgHome.persist();
				}
				// Prendo i giocatori in casa pachina
				listPlayersNameOutNode = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentPlayerNode, "//div[@class='out']//div[@class='namesub']");
				for (int y = 0; y < listPlayersNameOutNode.size(); y++) {
					currentSinglePlayerNode = listPlayersNameOutNode.get(y);
					currentPlayerNomeNode = currentSinglePlayerNode.findElementByName("a", false);
					currentGiocatoreNome = currentPlayerNomeNode.getText().toString();
					log.info("----  giocatore fuori panc " + currentGiocatoreNome);
					currentPlayerRuoloNode = currentSinglePlayerNode.findElementByName("span", false);
					currentGiocatoreRuolo = currentPlayerRuoloNode.getText().toString();
					Giocatori giocatoriFormazione = getGiocatoreFormazione(currentGiocatoreNome, squadraOut, currentGiocatoreRuolo, currentStagione);
					probabiliFormazioniFgHome.clearInstance();
					ProbabiliFormazioniFgId instanceId = new ProbabiliFormazioniFgId(giocatoriFormazione.getId(), idGiornata, false, true);
					ProbabiliFormazioniFg instance = new ProbabiliFormazioniFg();
					instance.setId(instanceId);
					probabiliFormazioniFgHome.setInstance(instance);
					probabiliFormazioniFgHome.persist();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("unmarshallAndSaveSingleHtmlFile, uscito");
	}

	private Giocatori getGiocatoreFormazione(String giocatoreNome, String squadra, String ruolo, String stagione) {
		boolean noLike = false;
		Giocatori giocatoriFormazione = giocatoriList.getGiocatoreByNomeSquadra(giocatoreNome, squadra, stagione, noLike);
		if (giocatoriFormazione == null) {
			giocatoriFormazione = giocatoriList.getGiocatoreByNomeSquadraRuolo(giocatoreNome, squadra, ruolo, stagione, noLike);
			if (giocatoriFormazione == null) {
				log.warn("Giocatore [" + giocatoreNome + "] squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "] NON trovato. Procedo con il suo inserimento");
				giocatoriList.insertOrUpdateGiocatore(squadra, giocatoreNome, ruolo, stagione, noLike);
				giocatoriFormazione = giocatoriList.getGiocatoreByNomeSquadraRuolo(giocatoreNome, squadra, ruolo, stagione, noLike);
			}
		}
		return giocatoriFormazione;
	}

	public ProbabiliFormazioniFg selectByIdGiocatoreIdGiornata(int idGiocatore, int idGiornata) {
		ProbabiliFormazioniFg toReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_ID_GIOCATORE_ID_GIORNATA);
		query.setParameter("idGiocatore", idGiocatore);
		query.setParameter("idGiornata", idGiornata);
		try {
			toReturn = (ProbabiliFormazioniFg) query.getSingleResult();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato con idGiocatore [" + idGiocatore + "] e idGiornata [" + idGiornata + "]");
		}
		return toReturn;
	}

	private boolean probFormGiaInserita(int idGiornata) {
		boolean giaInserita = false;
		Query query = getEntityManager().createQuery(SELECT_COUNT_ID_GIORNATA);
		query.setParameter("idGiornata", idGiornata);
		long count = (Long) query.getSingleResult();
		if (count > 0) {
			giaInserita = true;
		}
		return giaInserita;
	}

	public int deleteByIdGiornata(int idGiornata) {
		int rowDeleted = 0;
		Query query = getEntityManager().createQuery(DELETE_BY_ID_GIORNATA);
		query.setParameter("idGiornata", idGiornata);
		rowDeleted = (Integer) query.executeUpdate();
		log.info("Cancellate [" + rowDeleted + "] righe per la giornata [" + idGiornata + "]");
		return rowDeleted;
	}
}
