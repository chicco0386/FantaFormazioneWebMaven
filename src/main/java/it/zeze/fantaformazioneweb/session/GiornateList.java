package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.html.cleaner.HtmlCleanerUtil;
import it.zeze.util.ConfigurationUtil;
import it.zeze.util.Constants;
import it.zeze.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

@Name("giornateList")
public class GiornateList extends EntityQuery<Giornate> {

	private static final long serialVersionUID = 8967335917888183720L;

	@Logger
	static Log log;

	@In(create = true)
	GiornateHome giornateHome;

	@In(create = true)
	CalendarioList calendarioList;

	@In(create = true)
	SessionInfo sessionInfo;

	private static final String EJBQL = "select giornate from Giornate giornate";

	private static final String[] RESTRICTIONS = {};

	private static final String SELECT_BY_NUMERO_GIORNATA_STAGIONE = "SELECT g.id FROM Giornate g WHERE g.numeroGiornata = :numeroGiornata AND g.stagione = :stagione";
	private static final String SELECT_BY_DATA = "SELECT g.id FROM Giornate g WHERE g.data = :data";
	private static final String SELECT_BY_ID = "SELECT g FROM Giornate g WHERE g.id = :idGiornata";
	private static final String ORDER_BY_ID = "SELECT g FROM Giornate g ORDER BY g.id DESC";
	private static final String SELECT_BY_STAGIONE = "SELECT g FROM Giornate g WHERE g.stagione = :stagione ORDER BY g.id DESC";

	private Giornate giornate = new Giornate();

	public GiornateList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public Giornate getGiornate() {
		return giornate;
	}

	public void unmarshallAndSaveFromHtmlFile() {
		log.info("unmarshallAndSaveFromHtmlFile, entrato");
		String rootHTMLFiles = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_ROOT);
		String fileNameCalendario = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_CALENDARIO);
		String fileHTMLPath = rootHTMLFiles + fileNameCalendario;
		try {
			File fileCalendario = new File(fileHTMLPath);
			if (fileCalendario.exists()) {
				String nomeStagione = null;
				List<TagNode> listNomeStagione = HtmlCleanerUtil.getListOfElementsByXPathFromFile(fileHTMLPath, "//div[@id='stats']/h1");
				if (listNomeStagione == null || listNomeStagione.isEmpty()) {
					throw new ParseException("Nome stagione NON trovato", 0);
				} else {
					nomeStagione = listNomeStagione.get(0).getText().toString();
					nomeStagione = StringUtils.substringAfter(nomeStagione.toLowerCase(), "Calendario Serie A ".toLowerCase());
				}

				List<TagNode> listNodeGiornate = HtmlCleanerUtil.getListOfElementsByXPathFromFile(fileHTMLPath, "//div[@id='stats']/div[@class='calendar']/table");
				TagNode currentNodeGiornata;
				int currentIdGiornata;
				int currentNumeroGiornata;
				log.info("Salvo giornate per la stagione [" + nomeStagione + "]");
				for (int i = 0; i < listNodeGiornate.size(); i++) {
					currentNodeGiornata = listNodeGiornate.get(i);
					currentNumeroGiornata = i + 1;
					currentIdGiornata = getIdGiornata(currentNumeroGiornata, nomeStagione);
					if (currentIdGiornata != -1) {
						log.info("Stagione [" + currentNumeroGiornata + "] stagione [" + nomeStagione + "] gia' inserita");
					} else {
						currentIdGiornata = salvaGiornate(currentNodeGiornata, i + 1, nomeStagione);
					}
					log.info("currentIdGiornata " + currentIdGiornata);
					calendarioList.unmarshallAndSaveFromNodeCalendario(currentIdGiornata, currentNodeGiornata);
				}
			} else {
				// Nuovo HTML
				String currentStagione = sessionInfo.getStagione();
				fileNameCalendario = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_CALENDARIO_NEW);
				String wildCard = StringUtils.replace(fileNameCalendario, Constants.STRING_TO_REPLACE_NOME_FILE_CALENDARIO_NEW, "*");
				Iterator<File> itFile = FileUtils.iterateFiles(new File(rootHTMLFiles), new WildcardFileFilter(wildCard), null);
				File currentGiornataFile;
				String currentNumeroGiornata;
				TagNode currentGiornataTag;
				while (itFile.hasNext()) {
					currentGiornataFile = itFile.next();
					log.info("File [" + currentGiornataFile.getName() + "]");
					currentNumeroGiornata = StringUtils.substringBetween(currentGiornataFile.getName(), "_", "_");
					log.info("Giornata [" + currentNumeroGiornata + "]");
					currentGiornataTag = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromFile(currentGiornataFile.getAbsolutePath(), "//div[@id='artContainer']").get(0);

					salvaGiornataNew(currentGiornataTag, Integer.valueOf(currentNumeroGiornata), currentStagione);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
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
		log.info("unmarshallAndSaveFromHtmlFile, uscito");
	}

	private int salvaGiornate(TagNode calendarNode, int numeroGiornata, String nomeStagione) throws IOException, XPatherException, ParseException {
		int idGiornataInseritoToReturn = -1;
		String divisoreData = "/";
		String patternData = "dd" + divisoreData + "MM" + divisoreData + "yyyy";
		List<TagNode> listNodeGiornate = HtmlCleanerUtil.getListOfElementsByXPathFromElement(calendarNode, "/thead/tr/th/h3[@class='ra']");
		String currentStringGiornata;
		int indexOf;
		String currentStringData;
		Date currentDateParsed;
		for (int i = 0; i < listNodeGiornate.size(); i++) {
			currentStringGiornata = listNodeGiornate.get(i).getText().toString();
			indexOf = StringUtils.indexOf(currentStringGiornata, divisoreData);
			currentStringData = StringUtils.substring(currentStringGiornata, indexOf - 2, indexOf + (patternData.length() - 2));
			currentDateParsed = DateUtil.getDateWithPatternFromString(currentStringData, patternData);
			giornateHome.clearInstance();
			giornateHome.getInstance().setNumeroGiornata(numeroGiornata);
			giornateHome.getInstance().setStagione(nomeStagione);
			giornateHome.getInstance().setData(currentDateParsed);
			giornateHome.persist();
			idGiornataInseritoToReturn = giornateHome.getInstance().getId();
		}
		return idGiornataInseritoToReturn;
	}

	private int salvaGiornataNew(TagNode calendarNode, int numeroGiornata, String nomeStagione) throws IOException, XPatherException, ParseException, XPathExpressionException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		int idGiornataInseritoToReturn = -1;
		List<TagNode> listDivGiornateNode = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromElement(calendarNode, "//div[contains(@class,'row')][2]/div");
		List<TagNode> currentListDate;
		List<TagNode> currentListSquadre;
		List<TagNode> currentListsquadraCasa;
		List<TagNode> currentListsquadraFuori;
		String dataPartita;
		String patternData="dd MMM";
		Date dataParsed;
		for (TagNode currentDiv : listDivGiornateNode) {
			currentListDate = null;
			currentListSquadre = null;

			currentListDate = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentDiv, "//div/span");
			currentListSquadre = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromElement(currentDiv, "//div[contains(@class,'tablefk')]");
			if (currentListDate != null && !currentListDate.isEmpty()) {
				// Data giornata
				dataPartita = currentListDate.get(0).getText().toString();
				
				dataParsed = DateUtil.getDateWithPatternFromString(dataPartita, patternData, Locale.ITALIAN);
				log.info("Data [" + dataPartita + "] = "+ DateUtil.getDateAsString(dataParsed, "dd/MM/YYYY"));
			} 
			if (currentListSquadre != null && !currentListSquadre.isEmpty()) {
				// Partita con squadre
				// Squadra casa
				currentListsquadraCasa = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromElement(currentListSquadre.get(0), "//div[contains(@class,'ui-match-up')]//div[contains(@class,'left')]//h3[contains(@class,'team-name')]");
				currentListsquadraFuori = HtmlCleanerUtil.getListOfElementsByXPathSpecialFromElement(currentListSquadre.get(0), "//div[contains(@class,'ui-match-up')]//div[contains(@class,'right')]//h3[contains(@class,'team-name')]");
				for (int i = 0; i < currentListsquadraCasa.size(); i++) {
					log.info(currentListsquadraCasa.get(i).getText() + " - " + currentListsquadraFuori.get(i).getText());
				}
			}
		}
		return idGiornataInseritoToReturn;
	}

	public String getStagione(String stagioneInput) {
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

	public int getIdGiornata(int numeroGiornata, String stagione) {
		int idGiornata = -1;
		Query query = getEntityManager().createQuery(SELECT_BY_NUMERO_GIORNATA_STAGIONE);
		query.setParameter("numeroGiornata", numeroGiornata);
		query.setParameter("stagione", stagione);
		try {
			idGiornata = (Integer) query.getSingleResult();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato con numeroGiornata [" + numeroGiornata + "] e stagione [" + stagione + "]");
		}
		return idGiornata;
	}

	public Giornate getGiornataById(int idGiornata) {
		Giornate toReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_ID);
		query.setParameter("idGiornata", idGiornata);
		try {
			toReturn = (Giornate) query.getSingleResult();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato con idGiornata [" + idGiornata + "]");
		}
		return toReturn;
	}

	public int getIdGiornata(String dataString) {
		int idGiornata = -1;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date data = formatter.parse(dataString);
			Query query = getEntityManager().createQuery(SELECT_BY_DATA);
			query.setParameter("data", data);

			idGiornata = (Integer) query.getSingleResult();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato con data [" + dataString + "]");
		} catch (ParseException e) {
			log.error("Errore parsing [" + dataString + "]", e);
		}
		return idGiornata;
	}

	public Giornate getGiornata(String dataString) {
		Giornate toReturn = null;
		int idGiornata = getIdGiornata(dataString);
		if (idGiornata != -1) {
			toReturn = getGiornataById(idGiornata);
		}
		return toReturn;
	}

	public Giornate getLastGiornata() {
		Giornate toReturn = null;
		Query query = getEntityManager().createQuery(ORDER_BY_ID);
		try {
			List<Giornate> resultList = (List<Giornate>) query.getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				toReturn = resultList.get(0);
			}
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato per giornate");
		}
		return toReturn;
	}

	public List<Giornate> getGiornateByStagione(String stagione) {
		List<Giornate> toReturn = null;
		stagione = getStagione(stagione);
		Query query = getEntityManager().createQuery(SELECT_BY_STAGIONE);
		query.setParameter("stagione", stagione);
		try {
			toReturn = (List<Giornate>) query.getResultList();
		} catch (NoResultException e) {
			log.error("Nessun risultato tovato per giornate");
		}
		return toReturn;
	}
}
