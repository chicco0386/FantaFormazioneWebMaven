package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.bean.FormazioneBean;
import it.zeze.fantaformazioneweb.bean.GiocatoriMercato;
import it.zeze.fantaformazioneweb.entity.Formazioni;
import it.zeze.fantaformazioneweb.entity.FormazioniId;
import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniFg;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniGazzetta;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;
import it.zeze.util.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

@Name("formazioniList")
public class FormazioniList extends EntityQuery<Formazioni> {

	private static final long serialVersionUID = -5475364340053573535L;

	@Logger
	static Log log;

	@In(create = true)
	UtentiFormazioniList utentiFormazioniList;

	@In(create = true)
	UtentiFormazioniHome utentiFormazioniHome;

	@In(create = true)
	ProbabiliFormazioniFgList probabiliFormazioniFgList;

	@In(create = true)
	ProbabiliFormazioniGazzettaList probabiliFormazioniGazzettaList;

	@In(create = true)
	ProbabiliFormazioniList probabiliFormazioniList;

	@In(create = true)
	FormazioneBean formazioneBean;

	@In(create = true)
	GiornateList giornateList;

	private static final String EJBQL = "select formazioni from Formazioni formazioni";
	private static final String SELECT_BY_ID_UTENTI_FORMAZIONI = "select formazioni from Formazioni formazioni where formazioni.id.idUtentiFormazioni=:idUtentiFormazioni";
	private static final String UPDATE_PROB_TIT_BY_ID_UTENTI_FORMAZIONI_ID_GIOCATORE = "update Formazioni formazioni set formazioni.id.probTitolare=:probTitolare where formazioni.id.idUtentiFormazioni=:idUtentiFormazioni and formazioni.id.idGiocatore=:idGiocatore";
	private static final String UPDATE_PROB_PANC_BY_ID_UTENTI_FORMAZIONI_ID_GIOCATORE = "update Formazioni formazioni set formazioni.id.probPanchina=:probPanchina where formazioni.id.idUtentiFormazioni=:idUtentiFormazioni and formazioni.id.idGiocatore=:idGiocatore";
	private static final String DELETE_GIOCATORE_BY_ID_UTENTI_FORMAZIONI_ID_GIOCATORE = "DELETE FROM Formazioni formazioni WHERE formazioni.id.idUtentiFormazioni=:idUtentiFormazioni and formazioni.id.idGiocatore=:idGiocatore";
	private static final String DELETE_GIOCATORE_BY_ID_UTENTI_FORMAZIONI = "DELETE FROM Formazioni formazioni WHERE formazioni.id.idUtentiFormazioni=:idUtentiFormazioni";

	private Formazioni formazioni;

	public FormazioniList() {
		formazioni = new Formazioni();
		formazioni.setId(new FormazioniId());
		setEjbql(EJBQL);
		// setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}
	
	public boolean insertFormazioneMercato(String nomeFormazione, List<GiocatoriMercato> listaGiocatori, int idUtente, int idUtenteFormazioneToUpdate, BigDecimal crediti) {
		log.info("Start inserimento [" + listaGiocatori.size() + "] giocatori per la formazione [" + nomeFormazione + "] per ID utente [" + idUtente + "] idFormazioneToUpdate [" + idUtenteFormazioneToUpdate + "]");
		boolean result = false;
		if (listaGiocatori == null || listaGiocatori.isEmpty()) {
			StatusMessages.instance().add(Severity.ERROR, "Devi inserire almeno un giocatore per creare la tua formazione");
		} else if (controllaGiocatoriDuplicatiMercato(listaGiocatori)) {
			StatusMessages.instance().add(Severity.ERROR, "Hai inserito dei giocatori duplicati");
		} else {
			GiocatoriMercato currentGiocatore;
			UtentiFormazioni utenteFormazione;
			List<GiocatoriMercato> listaGiocatoriMod = new ArrayList<GiocatoriMercato>(listaGiocatori);
			formazioneBean.setDoInit(true);
			formazioneBean.initListaGiocatoriMercato(idUtente);
			List<GiocatoriMercato> listGiocatoriAttualiInFormazione = formazioneBean.getListaGiocatoriMercato();
			log.info("Init [" + listGiocatoriAttualiInFormazione.size() + "]");
			if (idUtenteFormazioneToUpdate == -1) {
				utenteFormazione = utentiFormazioniList.insertUtenteFormazione(nomeFormazione, idUtente, crediti);
				listGiocatoriAttualiInFormazione.clear();
			} else {
				utenteFormazione = utentiFormazioniList.updateUtenteFormazione(idUtenteFormazioneToUpdate, nomeFormazione, idUtente,crediti);
			}
			if (utenteFormazione != null) {
				// Cancellazione utenti
				List<GiocatoriMercato> listGiocToRemove = (List<GiocatoriMercato>) CollectionUtils.subtract(listGiocatoriAttualiInFormazione, listaGiocatoriMod);
				for (int i = 0; i < listGiocToRemove.size(); i++) {
					currentGiocatore = listGiocToRemove.get(i);
					log.info("currentGiocatoreToRemove: " + currentGiocatore.getNome());
					deleteGiocatoreByIdFormazioneAndIdGiocatore(utenteFormazione.getId(), currentGiocatore.getId());
				}
				// Inserisco eventuali nuovi giocatori
				listaGiocatoriMod = (List<GiocatoriMercato>) CollectionUtils.subtract(listaGiocatoriMod, listGiocToRemove);
				for (int i = 0; i < listaGiocatoriMod.size(); i++) {
					currentGiocatore = listaGiocatoriMod.get(i);
					if (!listGiocatoriAttualiInFormazione.contains(currentGiocatore)) {
						Formazioni toInsert = new Formazioni();
						toInsert.setId(new FormazioniId(currentGiocatore.getId(), utenteFormazione.getId()));
						toInsert.setPrezzoAcquisto(currentGiocatore.getPrezzoAcquisto());
						getEntityManager().persist(toInsert);
					}
				}
				result = true;
				log.info("End inserimento [" + listaGiocatoriMod.size() + "] e cancellazione [" + listGiocToRemove.size() + "] giocatori per la formazione [" + nomeFormazione + "] per ID utente [" + idUtente + "]");
			}
		}
		if (result) {
			if (idUtenteFormazioneToUpdate == -1) {
				StatusMessages.instance().add(Severity.INFO, "Formazione [" + nomeFormazione + "] inserita correttamente");
			} else {
				StatusMessages.instance().add(Severity.INFO, "Formazione [" + nomeFormazione + "] aggiornata correttamente");
			}
		}
		return result;
	}

	public boolean insertFormazione(String nomeFormazione, List<Giocatori> listaGiocatori, int idUtente, int idUtenteFormazioneToUpdate) {
		log.info("Start inserimento [" + listaGiocatori.size() + "] giocatori per la formazione [" + nomeFormazione + "] per ID utente [" + idUtente + "] idFormazioneToUpdate [" + idUtenteFormazioneToUpdate + "]");
		boolean result = false;
		if (listaGiocatori == null || listaGiocatori.isEmpty()) {
			StatusMessages.instance().add(Severity.ERROR, "Devi inserire almeno un giocatore per creare la tua formazione");
		} else if (controllaGiocatoriDuplicati(listaGiocatori)) {
			StatusMessages.instance().add(Severity.ERROR, "Hai inserito dei giocatori duplicati");
		} else {
			Giocatori currentGiocatore;
			UtentiFormazioni utenteFormazione;
			List<Giocatori> listaGiocatoriMod = new ArrayList<Giocatori>(listaGiocatori);
			formazioneBean.setDoInit(true);
			formazioneBean.initListaGiocatori(idUtente);
			List<Giocatori> listGiocatoriAttualiInFormazione = formazioneBean.getListaGiocatori();
			log.info("Init [" + listGiocatoriAttualiInFormazione.size() + "]");
			if (idUtenteFormazioneToUpdate == -1) {
				utenteFormazione = utentiFormazioniList.insertUtenteFormazione(nomeFormazione, idUtente, null);
				listGiocatoriAttualiInFormazione.clear();
			} else {
				utenteFormazione = utentiFormazioniList.updateUtenteFormazione(idUtenteFormazioneToUpdate, nomeFormazione, idUtente,null);
			}
			if (utenteFormazione != null) {
				// Cancellazione utenti
				List<Giocatori> listGiocToRemove = (List<Giocatori>) CollectionUtils.subtract(listGiocatoriAttualiInFormazione, listaGiocatoriMod);
				for (int i = 0; i < listGiocToRemove.size(); i++) {
					currentGiocatore = listGiocToRemove.get(i);
					log.info("currentGiocatoreToRemove: " + currentGiocatore.getNome());
					deleteGiocatoreByIdFormazioneAndIdGiocatore(utenteFormazione.getId(), currentGiocatore.getId());
				}
				// Inserisco eventuali nuovi giocatori
				listaGiocatoriMod = (List<Giocatori>) CollectionUtils.subtract(listaGiocatoriMod, listGiocToRemove);
				for (int i = 0; i < listaGiocatoriMod.size(); i++) {
					currentGiocatore = listaGiocatoriMod.get(i);
					if (!listGiocatoriAttualiInFormazione.contains(currentGiocatore)) {
						Formazioni toInsert = new Formazioni();
						toInsert.setId(new FormazioniId(currentGiocatore.getId(), utenteFormazione.getId()));
						getEntityManager().persist(toInsert);
					}
				}
				result = true;
				log.info("End inserimento [" + listaGiocatoriMod.size() + "] e cancellazione [" + listGiocToRemove.size() + "] giocatori per la formazione [" + nomeFormazione + "] per ID utente [" + idUtente + "]");
			}
		}
		if (result) {
			if (idUtenteFormazioneToUpdate == -1) {
				StatusMessages.instance().add(Severity.INFO, "Formazione [" + nomeFormazione + "] inserita correttamente");
			} else {
				StatusMessages.instance().add(Severity.INFO, "Formazione [" + nomeFormazione + "] aggiornata correttamente");
			}
		}
		return result;
	}

	public boolean calcolaFormazione(int idUtentiFormazioni, String stagioneDaCalcolare, int numeroGiornataDaCalcolare) {
		boolean result = false;
		log.info("Calcolo probabili formazioni per IdUtentiFormazioe [" + idUtentiFormazioni + "] stagione [" + stagioneDaCalcolare + "] e giornata [" + numeroGiornataDaCalcolare + "]");
		if (stagioneDaCalcolare == null || stagioneDaCalcolare.trim().isEmpty()) {
			StatusMessages.instance().add(Severity.ERROR, "Selezionare la stagione e la giornata relativa");
			log.error("Selezionare la stagione e la giornata relativa");
		} else if (numeroGiornataDaCalcolare < 1) {
			StatusMessages.instance().add(Severity.ERROR, "Selezionare la stagione e la giornata relativa");
			log.error("Selezionare la stagione e la giornata relativa");
		} else {
			int idGiornata = giornateList.getIdGiornata(numeroGiornataDaCalcolare, stagioneDaCalcolare);
			result = calcolaFormazione(idUtentiFormazioni, idGiornata);
		}
		return result;
	}

	public boolean calcolaFormazione(int idUtentiFormazioni, int idGiornata) {
		log.info("Calcolo probabili formazioni per IdUtentiFormazioe [" + idUtentiFormazioni + "] e idGiornata [" + idGiornata + "]");
		boolean result = false;
		List<Formazioni> listFormazioni = selectFormazioniByIdUtenteFormazioni(idUtentiFormazioni);
		if (listFormazioni != null && !listFormazioni.isEmpty()) {
			Formazioni currentFormazioni;
			int currentIdGiocatore;
			ProbabiliFormazioniFg currentProbFormazioniFg;
			ProbabiliFormazioniGazzetta currentProbFormazioniGazzetta;
			int currentProbTit = 0;
			int currentProbPanc = 0;
			// Cancello eventuali formazioni calcolate in precedenza per la
			// stessa giornata e stesso idUtentiFormazioni
			probabiliFormazioniList.deleteProbFormazioniByGiornataUtentiFormazione(idGiornata, idUtentiFormazioni);
			for (int i = 0; i < listFormazioni.size(); i++) {
				currentProbTit = 0;
				currentProbPanc = 0;
				currentFormazioni = listFormazioni.get(i);
				currentIdGiocatore = currentFormazioni.getId().getIdGiocatore();
				// Prendo probabilit� FG
				currentProbFormazioniFg = probabiliFormazioniFgList.selectByIdGiocatoreIdGiornata(currentIdGiocatore, idGiornata);
				if (currentProbFormazioniFg != null) {
					if (currentProbFormazioniFg.getId().isTitolare()) {
						currentProbTit = currentProbTit + Constants.PROB_FANTA_GAZZETTA;
					} else if (currentProbFormazioniFg.getId().isPanchina()) {
						currentProbPanc = currentProbPanc + + Constants.PROB_FANTA_GAZZETTA;
					}
				}
				// Prendo probabilita' Gazzetta
				currentProbFormazioniGazzetta = probabiliFormazioniGazzettaList.selectByIdGiocatoreIdGiornata(currentIdGiocatore, idGiornata);
				if (currentProbFormazioniGazzetta != null) {
					if (currentProbFormazioniGazzetta.getId().isTitolare()) {
						currentProbTit = currentProbTit + + Constants.PROB_GAZZETTA;
					} else if (currentProbFormazioniGazzetta.getId().isPanchina()) {
						currentProbPanc = currentProbPanc + + Constants.PROB_GAZZETTA;
					}
				}
				probabiliFormazioniList.insertProbFormazione(idGiornata, idUtentiFormazioni, currentIdGiocatore, currentProbTit, currentProbPanc, null);
			}
			result = true;
			StatusMessages.instance().add(Severity.INFO, "Formazione calcata correttamente");
			log.info("Fine calcolo probabili formazioni per IdUtentiFormazione [" + idUtentiFormazioni + "] e idGiornata [" + idGiornata + "]");
		} else {
			log.error("Nessuna formazione trovata per IdUtentiFormazione [" + idUtentiFormazioni + "] e idGiornata [" + idGiornata + "]");
		}
		return result;
	}

	public List<Formazioni> selectFormazioniByIdUtenteFormazioni(int idUtentiFormazioni) {
		Query query = getEntityManager().createQuery(SELECT_BY_ID_UTENTI_FORMAZIONI);
		query.setParameter("idUtentiFormazioni", idUtentiFormazioni);
		List<Formazioni> toReturn = query.getResultList();
		return toReturn;
	}

	public void updateFormazioniProbTitByUIdUtenteFormazioneIdGiocatore(int idUtentiFormazioni, int idGiornata) {
		Query query = getEntityManager().createQuery(UPDATE_PROB_TIT_BY_ID_UTENTI_FORMAZIONI_ID_GIOCATORE);
		query.setParameter("idUtentiFormazioni", idUtentiFormazioni);
		query.setParameter("idUtentiFormazioni", idGiornata);
	}

	public void deleteGiocatoreByIdFormazioneAndIdGiocatore(int idUtentiFormazioni, int idGiocatore) {
		Query query = getEntityManager().createQuery(DELETE_GIOCATORE_BY_ID_UTENTI_FORMAZIONI_ID_GIOCATORE);
		query.setParameter("idUtentiFormazioni", idUtentiFormazioni);
		query.setParameter("idGiocatore", idGiocatore);
		query.executeUpdate();
	}
	
	public void deleteGiocatoreByIdFormazione(int idUtentiFormazioni) {
		Query query = getEntityManager().createQuery(DELETE_GIOCATORE_BY_ID_UTENTI_FORMAZIONI);
		query.setParameter("idUtentiFormazioni", idUtentiFormazioni);
		query.executeUpdate();
	}

	private boolean controllaGiocatoriDuplicati(List<Giocatori> listaGiocatori) {
		boolean trovatoDuplicati = false;
		Giocatori currentGiocatori;
		for (int i = 0; i < listaGiocatori.size() && !trovatoDuplicati; i++) {
			currentGiocatori = listaGiocatori.get(i);
			int frequency = Collections.frequency(listaGiocatori, currentGiocatori);
			if (frequency > 1) {
				trovatoDuplicati = true;
			}
		}
		return trovatoDuplicati;
	}
	
	private boolean controllaGiocatoriDuplicatiMercato(List<GiocatoriMercato> listaGiocatori) {
		boolean trovatoDuplicati = false;
		Giocatori currentGiocatori;
		for (int i = 0; i < listaGiocatori.size() && !trovatoDuplicati; i++) {
			currentGiocatori = listaGiocatori.get(i);
			int frequency = Collections.frequency(listaGiocatori, currentGiocatori);
			if (frequency > 1) {
				trovatoDuplicati = true;
			}
		}
		return trovatoDuplicati;
	}

	public Formazioni getFormazioni() {
		return formazioni;
	}
}
