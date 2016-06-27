package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Utenti;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

@Name("utentiFormazioniList")
public class UtentiFormazioniList extends EntityQuery<UtentiFormazioni> {

	private static final long serialVersionUID = 3366976626785833304L;

	@Logger
	static Log log;

	@In(create = true)
	FormazioniList formazioniList;
	
	@In(create = true)
	ProbabiliFormazioniList probabiliFormazioniList;

	private static final String EJBQL = "select utentiFormazioni from UtentiFormazioni utentiFormazioni";

	private static final String[] RESTRICTIONS = { "lower(utentiFormazioni.nomeFormazione) like lower(concat(#{utentiFormazioniList.utentiFormazioni.nomeFormazione},'%'))", };

	private static final String SELECT_BY_NOME_AND_UTENTE_ID = "select utentiFormazioni from UtentiFormazioni utentiFormazioni where utentiFormazioni.nomeFormazione=:nomeFormazione and utentiFormazioni.utenti.id=:idUtente";
	private static final String SELECT_BY_ID_AND_UTENTE_ID = "select utentiFormazioni from UtentiFormazioni utentiFormazioni where utentiFormazioni.id=:idUtenteFormazione and utentiFormazioni.utenti.id=:idUtente";
	private static final String DELETE_BY_ID_AND_UTENTE_ID = "delete from UtentiFormazioni utentiFormazioni where utentiFormazioni.id=:idUtenteFormazione and utentiFormazioni.utenti.id=:idUtente";

	private UtentiFormazioni utentiFormazioni = new UtentiFormazioni();

	public UtentiFormazioniList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public UtentiFormazioni insertUtenteFormazione(String nomeFormazione, int idUtente, BigDecimal crediti) {
		log.info("Start inserimento formazione [" + nomeFormazione + "] dell'utente con id [" + idUtente + "]");
		UtentiFormazioni toReturn = null;
		if (nomeFormazione == null || StringUtils.isBlank(nomeFormazione)) {
			StatusMessages.instance().add(Severity.ERROR, "Devi inserire il nome della tua formazione");
		} else if (esisteUtentiFormazioni(nomeFormazione, idUtente)) {
			StatusMessages.instance().add(Severity.ERROR, "Hai gi� inserito una formazione con questo nome");
		} else {
			UtentiFormazioni toInsert = new UtentiFormazioni();
			Utenti utenteToInsert = new Utenti();
			utenteToInsert.setId(idUtente);
			toInsert.setUtenti(utenteToInsert);
			toInsert.setNomeFormazione(nomeFormazione);
			if (crediti != null) {
				toInsert.setCrediti(crediti);
			}
			getEntityManager().persist(toInsert);
			getEntityManager().flush();
			toReturn = getUtentiFormazioniId(nomeFormazione, idUtente);
			log.info("End inserita utente formazione con ID [" + toReturn.getId() + "]");
		}
		return toReturn;
	}

	public UtentiFormazioni updateUtenteFormazione(int idUtentiFormazioni, String nomeFormazione, int idUtente, BigDecimal crediti) {
		log.info("Start update formazione id [" + idUtentiFormazioni + "] dell'utente con id [" + idUtente + "] con [" + nomeFormazione + "]");
		UtentiFormazioni toReturn = null;
		if (nomeFormazione == null || StringUtils.isBlank(nomeFormazione)) {
			StatusMessages.instance().add(Severity.ERROR, "Devi inserire il nome della tua formazione");
		} else if (idUtentiFormazioni != -1) {
			toReturn = getUtentiFormazioniByIdAndIdUtente(idUtentiFormazioni, idUtente);
			toReturn.setNomeFormazione(nomeFormazione);
			if (crediti != null) {
				toReturn.setCrediti(crediti);
			}
			getEntityManager().persist(toReturn);
			log.info("End update utente formazione con ID [" + toReturn.getId() + "]");
		}
		return toReturn;
	}

	public boolean esisteUtentiFormazioni(String nomeFormazione, int idUtente) {
		boolean exist = false;
		UtentiFormazioni utenteFormazioneReturned = getUtentiFormazioniId(nomeFormazione, idUtente);
		if (utenteFormazioneReturned != null) {
			exist = true;
		}
		return exist;
	}

	public boolean esisteUtentiFormazioni(int idUtenteFormazione, int idUtente) {
		boolean exist = false;
		UtentiFormazioni utenteFormazioneReturned = getUtentiFormazioniByIdAndIdUtente(idUtenteFormazione, idUtente);
		if (utenteFormazioneReturned != null) {
			exist = true;
		}
		return exist;
	}

	public UtentiFormazioni getUtentiFormazioniId(String nomeFormazione, int idUtente) {
		UtentiFormazioni toReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_NOME_AND_UTENTE_ID);
		query.setParameter("nomeFormazione", nomeFormazione);
		query.setParameter("idUtente", idUtente);
		List<UtentiFormazioni> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			log.error("Nessun utentiFormazione trovato con nome [" + nomeFormazione + "] ID utente [" + idUtente + "]");
		} else {
			toReturn = resultList.get(0);
		}
		return toReturn;
	}

	public UtentiFormazioni getUtentiFormazioniByIdAndIdUtente(int idUtenteFormazione, int idUtente) {
		UtentiFormazioni toReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_ID_AND_UTENTE_ID);
		query.setParameter("idUtenteFormazione", idUtenteFormazione);
		query.setParameter("idUtente", idUtente);
		List<UtentiFormazioni> resultList = query.getResultList();
		if (resultList != null && resultList.size() == 0) {
			log.error("Nessun utentiFormazione trovato con ID [" + idUtenteFormazione + "] ID utente [" + idUtente + "]");
		} else {
			toReturn = resultList.get(0);
		}
		return toReturn;
	}

	private void copiaUtentiFormazioni(int idUtenteFormazione, int idUtente) {
		UtentiFormazioni currentUF = getUtentiFormazioniByIdAndIdUtente(idUtenteFormazione, idUtente);
		String nomeCopia = currentUF.getNomeFormazione().concat("_copy");
		log.info("TODO COPY");
	}

	public UtentiFormazioni getUtentiFormazioni() {
		return utentiFormazioni;
	}

	public void delete(int idUtentiFormazioni, int idUtente) {
		formazioniList.deleteGiocatoreByIdFormazione(idUtentiFormazioni);
		probabiliFormazioniList.deleteProbFormazioniByUtentiFormazione(idUtentiFormazioni);
		Query query = getEntityManager().createQuery(DELETE_BY_ID_AND_UTENTE_ID);
		query.setParameter("idUtenteFormazione", idUtentiFormazioni);
		query.setParameter("idUtente", idUtente);
		query.executeUpdate();
	}
}
