package it.zeze.fantaformazioneweb.bean.util;

import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.Squadre;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;
import it.zeze.fantaformazioneweb.session.GiornateList;
import it.zeze.fantaformazioneweb.session.SquadreList;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

@Name("comboBoxUtil")
@Scope(ScopeType.PAGE)
public class ComboBoxUtil {

	@Logger
	static Log log;

	private int idGiornataDaCalcolare;
	private String stagioneDaCalcolare;
	private int numeroGiornataDaCalcolare;

	@In(create = true)
	SquadreList squadreList;

	@In(create = true)
	GiornateList giornateList;

	private final static String SELECT_GIORNATE_CALCOLO_FORMAZIONI = "select probFormFg.id.idGiornata from ProbabiliFormazioniFg probFormFg group by probFormFg.id.idGiornata";
	private final static String SELECT_GIORNATE_STAT_CALCOLO_FORMAZIONI = "select stat.id.idGiornata from Statistiche stat group by stat.giornate.numeroGiornata";
	private final static String SELECT_STAGIONI = "select g.stagione from Giornate g group by g.stagione order by g.stagione desc";
	private final static String SELECT_GIORNATE_BY_STAGIONE_CALCOLO_FORMAZIONI = "select g.numeroGiornata from Giornate g, ProbabiliFormazioniFg probFormFg where g.stagione = :stagione and g.id = probFormFg.id.idGiornata group by probFormFg.id.idGiornata order by g.numeroGiornata desc";
	private final static String SELECT_NOME_FORMAZIONI_BY_ID_UTENTE = "select utentiForm from UtentiFormazioni utentiForm where utenti.id=:idUtente";

	public List<SelectItem> squadre() {
		List<SelectItem> toReturn = new ArrayList<SelectItem>();
		List<Squadre> listSquadre = squadreList.getResultList();
		for (int i = 0; i < listSquadre.size(); i++) {
			toReturn.add(new SelectItem(listSquadre.get(i).getNome(), listSquadre.get(i).getNome()));
		}
		return toReturn;
	}

	public List<SelectItem> giornate() {
		List<SelectItem> toReturn = new ArrayList<SelectItem>();
		Query query = squadreList.getEntityManager().createQuery(SELECT_GIORNATE_CALCOLO_FORMAZIONI);
		List<Integer> resultList = query.getResultList();
		toReturn.add(new SelectItem(-1, "Seleziona..."));
		for (int i = 0; i < resultList.size(); i++) {
			toReturn.add(new SelectItem(resultList.get(i), String.valueOf(resultList.get(i)).concat("a giornata")));
		}
		return toReturn;
	}

	public List<SelectItem> giornateStatistiche() {
		List<SelectItem> toReturn = new ArrayList<SelectItem>();
		Query query = squadreList.getEntityManager().createQuery(SELECT_GIORNATE_STAT_CALCOLO_FORMAZIONI);
		List<Integer> resultList = query.getResultList();
		toReturn.add(new SelectItem(-1, "Seleziona..."));
		int currentIdGiornata;
		int numeroGiornata;
		for (int i = 0; i < resultList.size(); i++) {
			currentIdGiornata = resultList.get(i);
			numeroGiornata = giornateList.getGiornataById(currentIdGiornata).getNumeroGiornata();
			toReturn.add(new SelectItem(resultList.get(i), String.valueOf(numeroGiornata).concat("a giornata")));
		}
		return toReturn;
	}

	public List<SelectItem> stagioniStatistiche() {
		List<SelectItem> toReturn = new ArrayList<SelectItem>();
		Query query = squadreList.getEntityManager().createQuery(SELECT_STAGIONI);
		List<String> resultList = query.getResultList();
		String currentStagione;
		for (int i = 0; i < resultList.size(); i++) {
			currentStagione = resultList.get(i);
			toReturn.add(new SelectItem(currentStagione, currentStagione));
		}
		return toReturn;
	}

	public List<SelectItem> giornateStagioneCalcoloFormazioni(String stagione) {
		log.info("Stagione [" + stagione + "]");
		String stagioneFormat = giornateList.getStagione(stagione);
		List<SelectItem> toReturn = new ArrayList<SelectItem>();
		Query query = squadreList.getEntityManager().createQuery(SELECT_GIORNATE_BY_STAGIONE_CALCOLO_FORMAZIONI);
		query.setParameter("stagione", stagioneFormat);
		List<Integer> resultList = query.getResultList();
		int currentNumGiornata;
		for (int i = 0; i < resultList.size(); i++) {
			currentNumGiornata = resultList.get(i);
			toReturn.add(new SelectItem(currentNumGiornata, String.valueOf(currentNumGiornata).concat("a giornata")));
		}
		return toReturn;
	}

	public List<SelectItem> formazioniUtente(int idUtente) {
		List<SelectItem> toReturn = new ArrayList<SelectItem>();
		Query query = squadreList.getEntityManager().createQuery(SELECT_NOME_FORMAZIONI_BY_ID_UTENTE);
		query.setParameter("idUtente", idUtente);
		List<UtentiFormazioni> resultList = query.getResultList();
		toReturn.add(new SelectItem(-1, "Seleziona..."));
		for (int i = 0; i < resultList.size(); i++) {
			toReturn.add(new SelectItem(resultList.get(i).getId(), String.valueOf(resultList.get(i).getNomeFormazione())));
		}
		return toReturn;
	}

	public void setLastStagione() {
		Giornate lastGiornata = giornateList.getLastGiornata();
		if (lastGiornata != null) {
			if (this.stagioneDaCalcolare == null || this.stagioneDaCalcolare.isEmpty()) {
				this.stagioneDaCalcolare = lastGiornata.getStagione();
			}
		}
	}

	public void setLastNumeroGiornataProbFormazioni() {
		List<SelectItem> listGiornate = giornateStagioneCalcoloFormazioni(this.stagioneDaCalcolare);
		if (listGiornate != null && !listGiornate.isEmpty()) {
			if (this.numeroGiornataDaCalcolare < 1) {
				this.numeroGiornataDaCalcolare = (Integer) listGiornate.get(0).getValue();
			}
		}
	}

	public int getIdGiornataDaCalcolare() {
		return idGiornataDaCalcolare;
	}

	public void setIdGiornataDaCalcolare(int idGiornataDaCalcolare) {
		this.idGiornataDaCalcolare = idGiornataDaCalcolare;
	}

	public String getStagioneDaCalcolare() {
		return stagioneDaCalcolare;
	}

	public void setStagioneDaCalcolare(String stagioneDaCalcolare) {
		this.stagioneDaCalcolare = stagioneDaCalcolare;
	}

	public int getNumeroGiornataDaCalcolare() {
		return numeroGiornataDaCalcolare;
	}

	public void setNumeroGiornataDaCalcolare(int numeroGiornataDaCalcolare) {
		this.numeroGiornataDaCalcolare = numeroGiornataDaCalcolare;
	}
}
