package it.zeze.fantaformazioneweb.session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.StatisticheSeamRemote;
import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.Statistiche;
import it.zeze.fantaformazioneweb.entity.StatisticheId;
import it.zeze.fantaformazioneweb.entity.wrapper.StatisticheWrap;

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
	
	private static StatisticheSeamRemote statisticheEJB;
	
	static {
		try {
			statisticheEJB = JNDIUtils.getStatisticheEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
		String stagione = sessionInfo.getStagione();
		statisticheEJB.unmarshallAndSaveFromHtmlFile(stagione);
	}

	public Statistiche getStatisticheIdGiocatoreIdGiornata(int idGiocatore, int idGiornata) {
		StatisticheWrap ejbResponse = statisticheEJB.getStatisticheIdGiocatoreIdGiornata(idGiocatore, idGiornata); 
		return ejbResponse.unwarp();
	}

	private List<Statistiche> getStatisticheIdGiocatoreAndStagione(int idGiocatore, String stagione) {
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

	public void setLastStagione() {
		Giornate lastGiornata = giornateList.getLastGiornata();
		if (lastGiornata != null) {
			if (this.giornate.getStagione() == null || this.giornate.getStagione().isEmpty()) {
				this.giornate.setStagione(lastGiornata.getStagione());
			}
		}
	}
}
