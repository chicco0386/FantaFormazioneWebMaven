package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioni;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniId;
import it.zeze.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

@Name("probabiliFormazioniList")
public class ProbabiliFormazioniList extends EntityQuery<ProbabiliFormazioni> {

	private static final long serialVersionUID = 4485353378796659672L;

	@Logger
	static Log log;

	@In(create = true)
	StatisticheList statisticheList;

	@In(create = true)
	GiornateList giornateList;

	private static final String EJBQL = "select probabiliFormazioni from ProbabiliFormazioni probabiliFormazioni";

	private static final String[] RESTRICTIONS = { "probabiliFormazioni.id.idGiornate = #{probabiliFormazioniList.probabiliFormazioni.id.idGiornate}", "probabiliFormazioni.id.idUtentiFormazioni = #{probabiliFormazioniList.probabiliFormazioni.id.idUtentiFormazioni}" };

	private static final String SELECT_BY_ID_GIORNATA_ID_UTENTIFORMAZIONE = "select probabiliFormazioni from ProbabiliFormazioni probabiliFormazioni where probabiliFormazioni.id.idGiornate=:idGiornata and probabiliFormazioni.id.idUtentiFormazioni=:idUtentiFormazione order by probabiliFormazioni.giocatori.ruolo desc";
	private static final String DELETE_BY_ID_GIORNATA_ID_UTENTIFORMAZIONE = "delete from ProbabiliFormazioni probabiliFormazioni where probabiliFormazioni.id.idGiornate=:idGiornata and probabiliFormazioni.id.idUtentiFormazioni=:idUtentiFormazione";
	private static final String DELETE_BY_ID_UTENTI_FORMAZIONI = "DELETE FROM ProbabiliFormazioni WHERE id.idUtentiFormazioni=:idUtentiFormazioni";

	private ProbabiliFormazioni probabiliFormazioni;

	private List<ProbabiliFormazioni> resultList;

	public ProbabiliFormazioniList() {
		probabiliFormazioni = new ProbabiliFormazioni();
		probabiliFormazioni.setGiornate(new Giornate());
		probabiliFormazioni.setId(new ProbabiliFormazioniId());
		setEjbql(EJBQL);
		// probabiliFormazioni.getId().setIdGiornate(giornateList.getIdGiornata(probabiliFormazioni.getGiornate().getNumeroGiornata(),
		// probabiliFormazioni.getGiornate().getStagione()));
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		refresh();
		// setMaxResults(25);
//		setOrderColumn("giocatori.ruolo");
//		setOrderDirection("desc");
	}

	public List<ProbabiliFormazioni> getRisultati(int idUtentiFormazione, String stagione, int numeroGiornata) {
		List<ProbabiliFormazioni> toReturn = new ArrayList<ProbabiliFormazioni>();
		if (idUtentiFormazione > -1 && (stagione != null && !stagione.isEmpty()) && numeroGiornata > -1) {
			int idGiornata = giornateList.getIdGiornata(numeroGiornata, stagione);
			probabiliFormazioni.getId().setIdGiornate(idGiornata);
			toReturn = getProbFormazioniByGiornataUtentiFormazione(idGiornata, idUtentiFormazione);
		}
		this.resultList = toReturn;
		return toReturn;
	}

	public ProbabiliFormazioni getProbabiliFormazioni() {
		return probabiliFormazioni;
	}

	public List<ProbabiliFormazioni> getProbFormazioniByGiornataUtentiFormazione(int idGiornata, int idUtentiFormazione) {
		Query query = getEntityManager().createQuery(SELECT_BY_ID_GIORNATA_ID_UTENTIFORMAZIONE);
		query.setParameter("idGiornata", idGiornata);
		query.setParameter("idUtentiFormazione", idUtentiFormazione);
		List<ProbabiliFormazioni> toReturn = query.getResultList();
		log.info("------ " + toReturn.size());
		return toReturn;
	}

	public void deleteProbFormazioniByGiornataUtentiFormazione(int idGiornata, int idUtentiFormazione) {
		Query query = getEntityManager().createQuery(DELETE_BY_ID_GIORNATA_ID_UTENTIFORMAZIONE);
		query.setParameter("idGiornata", idGiornata);
		query.setParameter("idUtentiFormazione", idUtentiFormazione);
		log.info("Cancellati [" + query.executeUpdate() + "] record da ProbabiliFormazioni");
	}
	
	public void deleteProbFormazioniByUtentiFormazione(int idUtentiFormazione) {
		Query query = getEntityManager().createQuery(DELETE_BY_ID_UTENTI_FORMAZIONI);
		query.setParameter("idUtentiFormazioni", idUtentiFormazione);
		log.info("Cancellati [" + query.executeUpdate() + "] record da ProbabiliFormazioni");
	}

	public void insertProbFormazione(int idGiornata, int idUtentiFormazione, int idGiocatore, int probTit, int probPanc, String note) {
		log.debug("Inserisco probabile formazione idGiornata [" + idGiornata + "] idUtentiFormazione [" + idUtentiFormazione + "] idGiocatore [" + idGiocatore + "] probTit [" + probTit + "] probPanc [" + probPanc + "] note [" + note + "]");
		ProbabiliFormazioniId probabiliFormazioniId = null;
		if (note != null) {
			probabiliFormazioniId = new ProbabiliFormazioniId(idGiornata, idUtentiFormazione, idGiocatore, probTit, probPanc, note);
		} else {
			probabiliFormazioniId = new ProbabiliFormazioniId(idGiornata, idUtentiFormazione, idGiocatore, probTit, probPanc);
		}
		ProbabiliFormazioni probabiliFormazioni = new ProbabiliFormazioni();
		probabiliFormazioni.setId(probabiliFormazioniId);
		getEntityManager().persist(probabiliFormazioni);
	}

	public List<ProbabiliFormazioni> getResultList() {
		return resultList;
	}

	public void setResultList(List<ProbabiliFormazioni> resultList) {
		this.resultList = resultList;
	}

	/**
	 * Per le probabilita' di fantagazzetta hanno valore 3
	 * 
	 * @param probabilita
	 * @return
	 */
	public boolean isFantaGazzettaSource(int probabilita) {
		boolean fantaGazzetta = false;
		if (probabilita != 0) {
			if (probabilita % Constants.PROB_FANTA_GAZZETTA == 0) {
				fantaGazzetta = true;
			} else {
				int probTemp = probabilita - Constants.PROB_GAZZETTA;
				if (probTemp != 0 && probTemp % Constants.PROB_FANTA_GAZZETTA == 0) {
					fantaGazzetta = true;
				}
			}
		}
		return fantaGazzetta;
	}

	/**
	 * Per le probabilita' di fantagazzetta hanno valore 5
	 * 
	 * @param probabilita
	 * @return
	 */
	public boolean isGazzettaSource(int probabilita) {
		boolean gazzetta = false;
		if (probabilita != 0) {
			if (probabilita % Constants.PROB_GAZZETTA == 0) {
				gazzetta = true;
			} else {
				int probTemp = probabilita - Constants.PROB_FANTA_GAZZETTA;
				if (probTemp != 0 && probTemp % Constants.PROB_GAZZETTA == 0) {
					gazzetta = true;
				}
			}
		}
		return gazzetta;
	}
}
