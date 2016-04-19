package it.zeze.fantaformazioneweb.session;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.Statistiche;
import it.zeze.fantaformazioneweb.entity.StatisticheId;
import it.zeze.html.cleaner.HtmlCleanerUtil;
import it.zeze.util.ConfigurationUtil;
import it.zeze.util.Constants;

@Name("statisticheList")
public class StatisticheList extends EntityQuery<Statistiche> {

	private static final long serialVersionUID = 1685766221228868935L;

	@Logger
	static Log log;

	@In(create = true)
	GiornateList giornateList;

	@In(create = true)
	GiocatoriList giocatoriList;

	@In(create = true)
	StatisticheHome statisticheHome;

	@In(create = true)
	SessionInfo sessionInfo;

	private static final String SELECT_BY_ID_GIOCATORE_ID_GIORNATE = "select statistiche from Statistiche statistiche where statistiche.id.idGiocatore=:idGiocatore and statistiche.id.idGiornata=:idGiornata";
	private static final String SELECT_BY_ID_GIOCATORE_STAGIONE = "select statistiche from Statistiche statistiche, Giornate gior where statistiche.id.idGiocatore=:idGiocatore and statistiche.id.idGiornata=gior.id AND gior.stagione = :stagione";
	private static final String SELECT_BY_ID_GIOCATORE = "select statistiche from Statistiche statistiche where statistiche.id.idGiocatore=:idGiocatore";
	private static final String SELECT_COUNT_BY_ID_GIORNATA = "select count(statistiche.id.idGiornata) from Statistiche statistiche where statistiche.id.idGiornata=:idGiornata";

	private static final String EJBQL = "select statistiche from Statistiche statistiche";

	private static final String[] RESTRICTIONS = { "id.idGiornata = (#{statisticheList.statistiche.id.idGiornata})", "lower(giocatori.nome) like lower(concat('%', concat(#{statisticheList.giocatori.nome},'%')))", "lower(giocatori.ruolo) like lower(concat('%', concat(#{statisticheList.giocatori.ruolo},'%')))", "lower(giocatori.squadre.nome) like lower(concat('%', concat(#{statisticheList.giocatori.squadre.nome},'%')))", };
	private static final String[] RESTRICTIONS_NO_GIORNATA = { "lower(giocatori.nome) like lower(concat('%', concat(#{statisticheList.giocatori.nome},'%')))", "lower(giocatori.ruolo) like lower(concat('%', concat(#{statisticheList.giocatori.ruolo},'%')))", "lower(giocatori.squadre.nome) like lower(concat('%', concat(#{statisticheList.giocatori.squadre.nome},'%')))", };

	private Statistiche statistiche = new Statistiche();
	private Giocatori giocatori = new Giocatori();
	private Giornate giornate = new Giornate();

	private List<Statistiche> resultList = new ArrayList<Statistiche>();
	private List<Statistiche> resumeStatistiche = new ArrayList<Statistiche>();

	public void initResultList() {
		log.info("initResultList");
		List<Statistiche> toReturn = new ArrayList<Statistiche>();
		boolean wherePresente = false;
		String newQuery = EJBQL;
		if (getGiornate().getStagione() != null && !getGiornate().getStagione().isEmpty()) {
			log.info("Stagione [" + getGiornate().getStagione() + "]");
			newQuery = newQuery.concat(", Giornate giornate where statistiche.id.idGiornata = giornate.id AND giornate.stagione = '" + getGiornate().getStagione() + "'");
			wherePresente = true;
		}
		if (getGiornate().getNumeroGiornata() != null && getGiornate().getNumeroGiornata() > 0) {
			if (wherePresente) {
				newQuery = newQuery.concat(" AND giornate.numeroGiornata = " + getGiornate().getNumeroGiornata());
			} else {
				newQuery = newQuery.concat(", Giornate giornate where statistiche.id.idGiornata = giornate.id");
				newQuery = newQuery.concat(" AND giornate.numeroGiornata = " + getGiornate().getNumeroGiornata());
				wherePresente = true;
			}
		}
		if (getGiocatori().getNome() != null && !getGiocatori().getNome().isEmpty()) {
			if (wherePresente) {
				newQuery = newQuery.concat(" and statistiche.giocatori.nome LIKE'%" + getGiocatori().getNome()) + "%'";
			} else {
				newQuery = newQuery.concat(" where statistiche.giocatori.nome LIKE'%" + getGiocatori().getNome()) + "%'";
				wherePresente = true;
			}
		}
		if (getGiocatori().getRuolo() != null && !getGiocatori().getRuolo().isEmpty()) {
			if (wherePresente) {
				newQuery = newQuery.concat(" and statistiche.giocatori.ruolo LIKE'%" + getGiocatori().getRuolo()) + "%'";
			} else {
				newQuery = newQuery.concat(" where statistiche.giocatori.ruolo LIKE'%" + getGiocatori().getRuolo()) + "%'";
				wherePresente = true;
			}
		}
		if (getGiocatori().getSquadre().getNome() != null && !getGiocatori().getSquadre().getNome().isEmpty()) {
			if (wherePresente) {
				newQuery = newQuery.concat(" and statistiche.giocatori.squadre.nome LIKE'%" + getGiocatori().getSquadre().getNome()) + "%'";
			} else {
				newQuery = newQuery.concat(" where statistiche.giocatori.squadre.nome LIKE'%" + getGiocatori().getSquadre().getNome()) + "%'";
				wherePresente = true;
			}
		}
		if (getGiocatori().getQuotazAttuale() != null && getGiocatori().getQuotazAttuale().compareTo(BigDecimal.ZERO) > 0) {
			if (wherePresente) {
				newQuery = newQuery.concat(" and statistiche.giocatori.quotazAttuale <= " + getGiocatori().getQuotazAttuale().toPlainString());
			} else {
				newQuery = newQuery.concat(" where statistiche.giocatori.quotazAttuale <= " + getGiocatori().getQuotazAttuale().toPlainString());
				wherePresente = true;
			}
		}
		if (StringUtils.isNotBlank(getOrderColumn()) && StringUtils.isNotBlank(getOrderDirection())) {
			newQuery = newQuery.concat(" order by " + getOrderColumn() + " " + getOrderDirection());
		}
		Query query = getEntityManager().createQuery(newQuery);
		try {
			toReturn = (List<Statistiche>) query.getResultList();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato per la query [" + newQuery + "]");
		}
		this.resultList = toReturn;
		log.info("Statistiche [" + resultList.size() + "]");
	}

	@Override
	public List<Statistiche> getResultList() {
		return this.resultList;
	}

	public void resetResumeStatistiche() {
		log.info("getResumeStatistiche");
		List<Statistiche> toReturn = new ArrayList<Statistiche>();
		List<Statistiche> resultList = getResultList();
		if (resultList.isEmpty()) {
			initResultList();
			resultList = getResultList();
		}
		// Raggruppo le statistiche di tutte le giornate per ogni giocatore
		Statistiche currentStat;
		int currentGiocatoreId;
		// Tengo traccia degli idDa rimuovere e quindi conto anche le occorrenze
		// dei match per fare la media sui voto poi
		List<Integer> idToRemove = new ArrayList<Integer>();
		for (int i = 0; i < resultList.size(); i++) {
			currentStat = resultList.get(i);
			currentGiocatoreId = currentStat.getId().getIdGiocatore();
			if (!idToRemove.contains(currentGiocatoreId)) {
				List<Statistiche> listStatGiocatore = getStatisticheIdGiocatoreAndStagione(currentGiocatoreId, this.giornate.getStagione());
				for (int y = 0; y < listStatGiocatore.size(); y++) {
					Statistiche currentStatToAdd = listStatGiocatore.get(y);
					if (currentStatToAdd.getId().getIdGiocatore() == currentGiocatoreId) {
						if (y == 0) {
							currentStat = currentStatToAdd;
						} else {
							currentStat.getId().setAmmonizioni(currentStat.getId().getAmmonizioni() + currentStatToAdd.getId().getAmmonizioni());
							currentStat.getId().setAssist(currentStat.getId().getAssist() + currentStatToAdd.getId().getAssist());
							currentStat.getId().setAutoreti(currentStat.getId().getAutoreti() + currentStatToAdd.getId().getAutoreti());
							currentStat.getId().setEspulsioni(currentStat.getId().getEspulsioni() + currentStatToAdd.getId().getEspulsioni());
							currentStat.getId().setGoalFatti(currentStat.getId().getGoalFatti() + currentStatToAdd.getId().getGoalFatti());
							currentStat.getId().setGoalRigore(currentStat.getId().getGoalRigore() + currentStatToAdd.getId().getGoalRigore());
							currentStat.getId().setGoalSubiti(currentStat.getId().getGoalSubiti() + currentStatToAdd.getId().getGoalSubiti());
							currentStat.getId().setRigoriParati(currentStat.getId().getRigoriParati() + currentStatToAdd.getId().getRigoriParati());
							currentStat.getId().setRigoriSbagliati(currentStat.getId().getRigoriSbagliati() + currentStatToAdd.getId().getRigoriSbagliati());
							currentStat.getId().setMediaVoto(currentStat.getId().getMediaVoto().add(currentStatToAdd.getId().getMediaVoto()));
							currentStat.getId().setMediaVotoFm(currentStat.getId().getMediaVotoFm().add(currentStatToAdd.getId().getMediaVotoFm()));
						}
					}
				}
				// Faccio la media sulle medie
				currentStat.getId().setMediaVoto(currentStat.getId().getMediaVoto().divide(new BigDecimal(listStatGiocatore.size()), 2, RoundingMode.CEILING));
				currentStat.getId().setMediaVotoFm(currentStat.getId().getMediaVotoFm().divide(new BigDecimal(listStatGiocatore.size()), 2, RoundingMode.CEILING));
				currentStat.getId().setPartiteGiocate(listStatGiocatore.size());

				toReturn.add(currentStat);
				idToRemove.add(currentGiocatoreId);
			}
			Collections.sort(toReturn, new Comparator<Statistiche>() {

				public int compare(Statistiche o1, Statistiche o2) {
					ComparatorChain comparatorChain = new ComparatorChain();
					comparatorChain.addComparator(new Comparator<Statistiche>() {

						public int compare(Statistiche o1, Statistiche o2) {
							int i = ((Integer) o1.getId().getPartiteGiocate()).compareTo((Integer) o2.getId().getPartiteGiocate());
							return i;
						}
					}, true);
					comparatorChain.addComparator(new Comparator<Statistiche>() {

						public int compare(Statistiche o1, Statistiche o2) {
							int i = o1.getId().getMediaVotoFm().compareTo(o2.getId().getMediaVotoFm());
							return i;
						}
					}, true);
					return comparatorChain.compare(o1, o2);
				}
			});
		}
		this.resumeStatistiche = toReturn;
	}

	public List<Statistiche> getResumeStatistiche() {
		return this.resumeStatistiche;
	}

	public StatisticheList() {
		statistiche = new Statistiche();
		statistiche.setId(new StatisticheId());
		setEjbql(EJBQL);
		// if (statistiche.getId().getIdGiornata() < 1) {
		// setRestrictionExpressionStrings(Arrays
		// .asList(RESTRICTIONS_NO_GIORNATA));
		// } else {
		// setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// }
		// setMaxResults(25);
	}

	public Statistiche getStatistiche() {
		return statistiche;
	}

	public Giocatori getGiocatori() {
		return giocatori;
	}

	public void setGiocatori(Giocatori giocatori) {
		this.giocatori = giocatori;
	}

	public Giornate getGiornate() {
		return giornate;
	}

	public void setGiornate(Giornate giornate) {
		this.giornate = giornate;
	}

	public void unmarshallAndSaveFromHtmlFile() {
		log.info("unmarshallAndSaveFromHtmlFile, entrato");
		String rootHTMLFiles = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_ROOT);
		String nomeFileSquadre = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_STATISTICHE_G);
		// Per tutte le giornate presenti su DB controllo se esiste il relativo
		// file e lo elaboro
		String pathCompletoFileSquadre;
		List<Giornate> listaGiornate = giornateList.getResultList();
		Giornate currentGiornata;
		File currentFileGiornata;
		for (int i = 0; i < listaGiornate.size(); i++) {
			currentGiornata = listaGiornate.get(i);
			pathCompletoFileSquadre = rootHTMLFiles + createNomeFileGiornata(nomeFileSquadre, String.valueOf(currentGiornata.getId()));
			currentFileGiornata = new File(pathCompletoFileSquadre);
			if (currentFileGiornata.exists()) {
				try {
					unmarshallAndSaveFromHtmlFile(currentFileGiornata, String.valueOf(currentGiornata.getId()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XPatherException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerFactoryConfigurationError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		log.info("unmarshallAndSaveFromHtmlFile, uscito");
	}

	private String createNomeFileGiornata(String nomeFile, String numGiornata) {
		String fileGiornataToReturn = "";
		fileGiornataToReturn = StringUtils.replace(nomeFile, Constants.STRING_TO_REPLACE_NOME_FILE_GIORNATE, numGiornata);
		return fileGiornataToReturn;
	}

	private void unmarshallAndSaveFromHtmlFile(File fileGiornata, String numGiornata) throws IOException, XPatherException, XPathExpressionException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		String currentStagione = sessionInfo.getStagione();
		currentStagione = giornateList.getStagione(currentStagione);
		int idGiornata = giornateList.getIdGiornata(Integer.valueOf(numGiornata), currentStagione);
		if (!statGiaInserite(idGiornata)) {
			try {
				// Vecchio HTML
				log.info("Elaboro statistiche della giornata [" + numGiornata + "] della stagione [" + currentStagione + "]");
				List<TagNode> listaTabelleVotiPerSquadra = HtmlCleanerUtil.getListOfElementsByXPathFromFile(fileGiornata.getAbsolutePath(), "//div[@id='allvotes']");
				if (listaTabelleVotiPerSquadra == null || listaTabelleVotiPerSquadra.isEmpty()){
					throw new Exception("Rilancio per fare parsing con nuovo HTML");
				}
				TagNode currentNodeSquadra;
				String currentNomeSquadra;
				List<TagNode> listaGiocatori;
				TagNode currentNodeGiocatore;
				TagNode currentNodeGiocatoreNome;
				String currentGiocatoreNome;
				TagNode currentNodeGiocatoreRuolo;
				String currentGiocatoreRuolo;
				List<TagNode> listaColonneGiocatore;
				TagNode currentColonnaGiocatore;
				int currentEspulso;
				int currentAmmonito;
				String currentGoalFatto;
				String currentGoalSuRigore;
				String currentGoalSubito;
				String currentRigoreParato;
				String currentRigoreSbagliato;
				String currentAutorete;
				String currentAssist;
				String currentMediaVoto;

				for (int i = 0; i < listaTabelleVotiPerSquadra.size(); i++) {
					currentNodeSquadra = listaTabelleVotiPerSquadra.get(i);
					// Recupero il nome della squadra
					currentNodeSquadra = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeSquadra, "/div[@id]").get(0);
					currentNomeSquadra = currentNodeSquadra.getAttributeByName("id");

					listaGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeSquadra, "/table/tbody/tr[@class='P']");
					int numColonnaPartenza = 2;
					for (int y = 0; y < listaGiocatori.size(); y++) {
						numColonnaPartenza = 2;
						currentEspulso = 0;
						currentAmmonito = 0;
						currentNodeGiocatore = listaGiocatori.get(y);
						currentNodeGiocatoreNome = currentNodeGiocatore.findElementByAttValue("class", "n", false, false);
						currentNodeGiocatoreNome = currentNodeGiocatoreNome.findElementByName("a", false);
						currentGiocatoreNome = currentNodeGiocatoreNome.getText().toString();
						currentNodeGiocatoreRuolo = currentNodeGiocatore.findElementByAttValue("class", "r", false, false);
						currentGiocatoreRuolo = currentNodeGiocatoreRuolo.getText().toString();
						// Salva solo i giocatori non gli allenatori (ruolo = M)
						if (!currentGiocatoreRuolo.equalsIgnoreCase("M")) {
							listaColonneGiocatore = currentNodeGiocatore.getElementListByName("td", true);
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentMediaVoto = currentColonnaGiocatore.getText().toString();
							if (HtmlCleanerUtil.nodeContainsAttribute(currentColonnaGiocatore, "class", "vesp")) {
								currentEspulso = currentEspulso + 1;
							} else if (HtmlCleanerUtil.nodeContainsAttribute(currentColonnaGiocatore, "class", "vamm")) {
								currentAmmonito = currentAmmonito + 1;
							}
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentGoalFatto = currentColonnaGiocatore.getText().toString();
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentGoalSuRigore = currentColonnaGiocatore.getText().toString();
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentGoalSubito = currentColonnaGiocatore.getText().toString();
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentRigoreParato = currentColonnaGiocatore.getText().toString();
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentRigoreSbagliato = currentColonnaGiocatore.getText().toString();
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentAutorete = currentColonnaGiocatore.getText().toString();
							currentColonnaGiocatore = listaColonneGiocatore.get(numColonnaPartenza++);
							currentAssist = currentColonnaGiocatore.getText().toString();

							saveVotoFromHtmlFile(idGiornata, currentStagione, currentGiocatoreNome, currentNomeSquadra, currentGiocatoreRuolo, currentAmmonito, currentEspulso, currentAssist, currentAutorete, currentGoalFatto, currentGoalSuRigore, currentGoalSubito, currentMediaVoto, currentRigoreParato, currentRigoreSbagliato);
						}

					}
				}

			} catch (Exception e) {
				// Vecchio HTML
				log.info("Nuovo HTML");
				unmarshallAndSaveFromHtmlFileNEW(fileGiornata, numGiornata, currentStagione);
			}
		} else {
			log.info("Statistiche della giornata [" + numGiornata + "] gia' inserite");
		}
	}

	private void unmarshallAndSaveFromHtmlFileNEW(File fileGiornata, String numGiornata, String stagione) throws IOException, XPatherException, XPathExpressionException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		int idGiornata = giornateList.getIdGiornata(Integer.valueOf(numGiornata), stagione);
		log.info("Elaboro statistiche della giornata [" + numGiornata + "] della stagione [" + stagione + "]");
		List<TagNode> listTabelleSquadre = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromFile(fileGiornata.getAbsolutePath(), "//div[contains(@class,'tbvoti')]//table");
		for (TagNode currentNodeVotiSquadra : listTabelleSquadre) {
			salvaVotiSquadraHtmlNEW(idGiornata, stagione, currentNodeVotiSquadra);
		}
	}

	private void salvaVotiSquadraHtmlNEW(int idGiornata, String currentStagione, TagNode currentNodeVotiSquadra) throws IOException, XPatherException, XPathExpressionException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		String currentNomeSquadra;
		String currentGiocatoreNome;
		String currentGiocatoreRuolo;
		String currentAssistString;
		String currentAutoreteString;
		String currentGoalFattoString;
		String currentGoalSuRigoreString;
		String currentGoalSubitoString;
		String currentRigoreParatoString;
		String currentRigoreSbagliatoString;
		String mediaVotoString;

		int currentEspulso;
		int currentAmmonito;

		currentNomeSquadra = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeVotiSquadra, "//table//thead//h3").get(0).getText().toString();
		log.info("Squadra [" + currentNomeSquadra + "]");
		List<TagNode> listaGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeVotiSquadra, "//tbody/tr");
		TagNode currentNodeVoto;
		List<TagNode> listaControlloCartellini;
		int tdIndexVotiMI;
		for (TagNode currentGiocatore : listaGiocatori) {
			tdIndexVotiMI = 10;
			currentAmmonito = 0;
			currentEspulso = 0;
			currentGiocatoreNome = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[@class='pname']//a").get(0).getText().toString();
			currentGiocatoreRuolo = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromElement(currentGiocatore, "//span[contains(@class,'role')]").get(0).getText().toString();
			currentNodeVoto = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0);
			listaControlloCartellini = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromElement(currentGiocatore, "//span[contains(@class,'trn-rr')]");
			if (listaControlloCartellini == null || !listaControlloCartellini.isEmpty()) {
				currentEspulso = currentEspulso + 1;
			}
			listaControlloCartellini = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromElement(currentGiocatore, "//span[contains(@class,'trn-ry')]");
			if (listaControlloCartellini == null || !listaControlloCartellini.isEmpty()) {
				currentAmmonito = currentAmmonito + 1;
			}
			mediaVotoString = currentNodeVoto.getText().toString();
			currentGoalFattoString = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0).getText().toString();
			currentGoalSuRigoreString = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0).getText().toString();
			currentGoalSubitoString = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0).getText().toString();
			currentRigoreParatoString = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0).getText().toString();
			currentRigoreSbagliatoString = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0).getText().toString();
			currentAutoreteString = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0).getText().toString();
			currentAssistString = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentGiocatore, "//td[" + tdIndexVotiMI++ + "]").get(0).getText().toString();

			if (!currentGiocatoreRuolo.equalsIgnoreCase("ALL")) {
				saveVotoFromHtmlFile(idGiornata, currentStagione, currentGiocatoreNome, currentNomeSquadra, currentGiocatoreRuolo, currentAmmonito, currentEspulso, currentAssistString, currentAutoreteString, currentGoalFattoString, currentGoalSuRigoreString, currentGoalSubitoString, mediaVotoString, currentRigoreParatoString, currentRigoreSbagliatoString);
			}

		}
	}

	private void saveVotoFromHtmlFile(int idGiornata, String currentStagione, String currentGiocatoreNome, String currentNomeSquadra, String currentGiocatoreRuolo, int currentAmmonito, int currentEspulso, String currentAssist, String currentAutorete, String currentGoalFatto, String currentGoalSuRigore, String currentGoalSubito, String currentMediaVoto, String currentRigoreParato, String currentRigoreSbagliato) {
		// Recupero il giocatore relativo su DB
		boolean noLike = true;
		Giocatori currentGiocatoreDB = giocatoriList.getGiocatoreByNomeSquadraRuolo(currentGiocatoreNome, currentNomeSquadra, currentGiocatoreRuolo, currentStagione, noLike);
		int espulso = currentEspulso;
		int ammonito = currentAmmonito;
		int goalFatto;
		int goalSuRigore;
		int goalSubito;
		int rigoreParato;
		int rigoreSbagliato;
		int autorete;
		int assist;
		BigDecimal mediaVoto;
		mediaVoto = new BigDecimal(currentMediaVoto.replace(",", "."));
		if (currentGoalFatto.contains("-")) {
			goalFatto = 0;
		} else {
			goalFatto = Integer.parseInt(currentGoalFatto);
		}
		if (currentGoalSuRigore.contains("-")) {
			goalSuRigore = 0;
		} else {
			goalSuRigore = Integer.parseInt(currentGoalSuRigore);
		}
		if (currentGoalSubito.contains("-")) {
			goalSubito = 0;
		} else {
			goalSubito = Integer.parseInt(currentGoalSubito);
		}
		if (currentRigoreParato.contains("-")) {
			rigoreParato = 0;
		} else {
			rigoreParato = Integer.parseInt(currentRigoreParato);
		}
		if (currentRigoreSbagliato.contains("-")) {
			rigoreSbagliato = 0;
		} else {
			rigoreSbagliato = Integer.parseInt(currentRigoreSbagliato);
		}
		if (currentAutorete.contains("-")) {
			autorete = 0;
		} else {
			autorete = Integer.parseInt(currentAutorete);
		}
		if (currentAssist.contains("-")) {
			assist = 0;
		} else {
			assist = Integer.parseInt(currentAssist);
		}

		if (currentGiocatoreDB == null) {
			log.warn("Giocatore [" + currentGiocatoreNome + "] [" + currentNomeSquadra + "] [" + currentGiocatoreRuolo + "] NON presente nel DB. Procedo con il suo inserimento");
			giocatoriList.insertOrUpdateGiocatore(currentNomeSquadra, currentGiocatoreNome, currentGiocatoreRuolo, currentStagione, noLike);
			currentGiocatoreDB = giocatoriList.getGiocatoreByNomeSquadraRuolo(currentGiocatoreNome, currentNomeSquadra, currentGiocatoreRuolo, currentStagione, noLike);
		}
		log.info("Inserisco statistiche giocatore [" + currentGiocatoreNome + "] [" + currentNomeSquadra + "] [" + currentGiocatoreRuolo + "]");
		StatisticheId statisticheId = new StatisticheId();
		statisticheId.setIdGiocatore(currentGiocatoreDB.getId());
		statisticheId.setIdGiornata(idGiornata);
		statisticheId.setAmmonizioni(ammonito);
		statisticheId.setEspulsioni(espulso);
		statisticheId.setAssist(assist);
		statisticheId.setAutoreti(autorete);
		statisticheId.setGoalFatti(goalFatto);
		statisticheId.setGoalRigore(goalSuRigore);
		statisticheId.setGoalSubiti(goalSubito);
		statisticheId.setMediaVoto(mediaVoto);
		BigDecimal currentMediaFantaVoto = calcolaFantaVoto(mediaVoto, ammonito, espulso, assist, autorete, goalFatto, goalSuRigore, rigoreSbagliato, goalSubito, rigoreParato);
		statisticheId.setMediaVotoFm(currentMediaFantaVoto);
		statisticheId.setRigoriParati(rigoreParato);
		statisticheId.setRigoriSbagliati(rigoreSbagliato);

		statisticheHome.clearInstance();
		Statistiche statisticheToInsert = new Statistiche();
		statisticheToInsert.setId(statisticheId);
		statisticheHome.setInstance(statisticheToInsert);
		statisticheHome.persist();
	}

	private BigDecimal calcolaFantaVoto(BigDecimal mediaVoto, int ammonito, int espulso, int assist, int autoreti, int goalFatti, int rigoreFatto, int rigoreSbagliato, int rigoreSubito, int rigoreParato) {
		BigDecimal fantaVoto = mediaVoto;
		if (ammonito > 0) {
			fantaVoto = fantaVoto.subtract(new BigDecimal(0.5));
		}
		if (espulso > 0) {
			fantaVoto = fantaVoto.subtract(BigDecimal.ONE);
		}
		fantaVoto = fantaVoto.add(new BigDecimal(assist));
		if (autoreti > 0) {
			fantaVoto = fantaVoto.subtract(new BigDecimal(autoreti * 2));
		}
		if (goalFatti > 0) {
			fantaVoto = fantaVoto.add(new BigDecimal(goalFatti * 3));
		}
		if (rigoreFatto > 0) {
			fantaVoto = fantaVoto.add(new BigDecimal(rigoreFatto * 3));
		}
		if (rigoreSbagliato > 0) {
			fantaVoto = fantaVoto.subtract(new BigDecimal(rigoreSbagliato * 3));
		}
		if (rigoreSubito > 0) {
			fantaVoto = fantaVoto.subtract(new BigDecimal(rigoreSubito * 1));
		}
		if (rigoreParato > 0) {
			fantaVoto = fantaVoto.add(new BigDecimal(rigoreParato * 3));
		}
		return fantaVoto;
	}

	public Statistiche getStatisticheIdGiocatoreIdGiornata(int idGiocatore, int idGiornata) {
		Statistiche toReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_ID_GIOCATORE_ID_GIORNATE);
		query.setParameter("idGiocatore", idGiocatore);
		if (idGiornata > 1) {
			query.setParameter("idGiornata", idGiornata - 1);
		} else if (idGiornata == 1) {
			query.setParameter("idGiornata", idGiornata);
		}
		try {
			toReturn = (Statistiche) query.getSingleResult();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato con idGiocatore [" + idGiocatore + "] e idGiornata [" + idGiornata + "]");
		}
		return toReturn;
	}

	public List<Statistiche> getStatisticheIdGiocatore(int idGiocatore) {
		List<Statistiche> toReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_ID_GIOCATORE);
		query.setParameter("idGiocatore", idGiocatore);
		try {
			toReturn = (List<Statistiche>) query.getResultList();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato con idGiocatore [" + idGiocatore + "]");
		}
		return toReturn;
	}

	public List<Statistiche> getStatisticheIdGiocatoreAndStagione(int idGiocatore, String stagione) {
		List<Statistiche> toReturn = null;
		String stagioneParse = giornateList.getStagione(stagione);
		Query query = getEntityManager().createQuery(SELECT_BY_ID_GIOCATORE_STAGIONE);
		query.setParameter("idGiocatore", idGiocatore);
		query.setParameter("stagione", stagioneParse);
		try {
			toReturn = (List<Statistiche>) query.getResultList();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato con idGiocatore [" + idGiocatore + "] e stagione [" + stagioneParse + "]");
		}
		return toReturn;
	}

	private boolean statGiaInserite(int idGiornata) {
		boolean giaInserita = false;
		Query query = getEntityManager().createQuery(SELECT_COUNT_BY_ID_GIORNATA);
		query.setParameter("idGiornata", idGiornata);
		long count = (Long) query.getSingleResult();
		if (count > 0) {
			giaInserita = true;
		}
		return giaInserita;
	}

	public void setLastStagione() {
		Giornate lastGiornata = giornateList.getLastGiornata();
		if (lastGiornata != null) {
			if (this.giornate.getStagione() == null || this.giornate.getStagione().isEmpty()) {
				this.giornate.setStagione(lastGiornata.getStagione());
			}
		}
	}
}
