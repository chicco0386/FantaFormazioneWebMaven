package it.zeze.fantaformazioneweb.session;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.service.definition.ejb.CalendarioRemote;
import it.zeze.fantaformazioneweb.entity.Calendario;
import it.zeze.fantaformazioneweb.entity.CalendarioId;
import it.zeze.fantaformazioneweb.entity.Squadre;
import it.zeze.html.cleaner.HtmlCleanerUtil;

@Name("calendarioList")
public class CalendarioList extends EntityQuery<Calendario> {

	private static final long serialVersionUID = -6277662181191081023L;

	@Logger
	static Log log;

	@In(create = true)
	SquadreList squadreList;

	@In(create = true)
	GiornateList giornateList;

	@In(create = true)
	CalendarioHome calendarioHome;

	private static final String EJBQL = "select calendario from Calendario calendario";

	private static final String GET_CALENDARIO_BY_ID_GIOR_ID_SQUADRA = "select calendario from Calendario calendario where calendario.id.idGiornata = :idGiornata AND (calendario.id.idSquadraCasa = :idSquadra OR calendario.id.idSquadraFuoriCasa = :idSquadra)";

	private static final String[] RESTRICTIONS = {};

	private Calendario calendario;

	public CalendarioList() {
		calendario = new Calendario();
		calendario.setId(new CalendarioId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public Calendario getCalendario() {
		return calendario;
	}

	public void inizializzaCalendario() {
		squadreList.unmarshallAndSaveFromHtmlFile();
		giornateList.unmarshallAndSaveFromHtmlFile();
		
		try {
			//Get the Initial Context for the JNDI lookup for a local EJB
			InitialContext ic = new InitialContext();
			//Retrieve the Home interface using JNDI lookup
			
			CalendarioRemote calendarioEJB = (CalendarioRemote) ic.lookup("java:global/FantaFormazioneEAR/FantaWebService-0.0.1-SNAPSHOT/CalendarioEJB!it.zeze.fanta.service.definition.ejb.CalendarioRemote");
			log.info(calendarioEJB.getNomeSquadraAvversaria(-1, -1));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void unmarshallAndSaveFromNodeCalendario(int idGiornata, TagNode calendarNode) {
		log.info("unmarshallAndSaveFromHtmlFile, entrato");
		try {
			squadreList.initMappaSquadre();
			List<TagNode> listNodeGiornate = HtmlCleanerUtil.getListOfElementsByXPathFromElement(calendarNode, "/tbody/tr/td");
			TagNode currentNodePartita;
			List<TagNode> listSquadrePartita;
			String squadraCasa;
			int idSquadraCasa;
			String squadraFuori;
			int idSquadraFuori;
			for (int i = 0; i < listNodeGiornate.size(); i++) {
				currentNodePartita = listNodeGiornate.get(i);
				listSquadrePartita = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodePartita, "/a");
				if (!listSquadrePartita.isEmpty()) {
					squadraCasa = listSquadrePartita.get(0).getText().toString();
					squadraFuori = listSquadrePartita.get(1).getText().toString();
					idSquadraCasa = squadreList.getSquadraFromMapByNome(squadraCasa).getId();
					idSquadraFuori = squadreList.getSquadraFromMapByNome(squadraFuori).getId();
					calendarioHome.clearInstance();
					Calendario calendarioToInsert = new Calendario();
					calendarioToInsert.setId(new CalendarioId(idGiornata, idSquadraCasa, idSquadraFuori));
					calendarioHome.setInstance(calendarioToInsert);
					calendarioHome.persist();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("unmarshallAndSaveFromHtmlFile, uscito");
	}

	public Calendario getCalendarioByIdGiornataIdSquadra(int idGiornata, int idSquadra) {
		Calendario toReturn = null;
		EntityManager em = getEntityManager();
		Query query = em.createQuery(GET_CALENDARIO_BY_ID_GIOR_ID_SQUADRA);
		query.setParameter("idGiornata", idGiornata);
		query.setParameter("idSquadra", idSquadra);
		try {
			toReturn = (Calendario) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		return toReturn;
	}

	public String getNomeSquadraAvversaria(int idGiornata, int idSquadra) {
		String nomeSquadraAvv = "";
		try {
			Calendario calendarioReturn = getCalendarioByIdGiornataIdSquadra(idGiornata, idSquadra);
			if (calendarioReturn != null) {
				int idSquadraToSearch = 0;
				if (calendarioReturn.getId().getIdSquadraCasa() != idSquadra) {
					idSquadraToSearch = calendarioReturn.getId().getIdSquadraCasa();
				} else if (calendarioReturn.getId().getIdSquadraFuoriCasa() != idSquadra) {
					idSquadraToSearch = calendarioReturn.getId().getIdSquadraFuoriCasa();
				}
				if (idSquadraToSearch > 0) {
					Squadre squadra = squadreList.getSquadraById(idSquadraToSearch);
					nomeSquadraAvv = squadra.getNome();
				} else {
					log.warn("idSquadra NON trovato");
				}
			}
		} catch (NoResultException e) {
			return null;
		}
		return nomeSquadraAvv;
	}

	public boolean isSquadraFuoriCasa(int idGiornata, int idSquadra) {
		boolean isCasa = true;
		Calendario calendarioReturn = getCalendarioByIdGiornataIdSquadra(idGiornata, idSquadra);
		if (calendarioReturn != null) {
			if (calendarioReturn.getId().getIdSquadraCasa() == idSquadra) {
				isCasa = true;
			} else if (calendarioReturn.getId().getIdSquadraFuoriCasa() == idSquadra) {
				isCasa = false;
			}
		}
		return isCasa;
	}
}
