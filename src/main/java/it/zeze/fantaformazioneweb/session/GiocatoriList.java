package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.Squadre;
import it.zeze.html.cleaner.HtmlCleanerUtil;
import it.zeze.util.ConfigurationUtil;
import it.zeze.util.Constants;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

@Name("giocatoriList")
public class GiocatoriList extends EntityQuery<Giocatori> {

	private static final long serialVersionUID = -1966301305576124912L;

	@Logger
	static Log log;

	@In(create = true)
	SquadreList squadreList;

	@In(create = true)
	GiocatoriHome giocatoriHome;

	private static final String EJBQL = "select giocatori from Giocatori giocatori";

	private static final String[] RESTRICTIONS = { "lower(giocatori.nome) like lower(concat('%', concat(#{giocatoriList.giocatori.nome},'%')))", "lower(giocatori.ruolo) like lower(concat('%', concat(#{giocatoriList.giocatori.ruolo},'%')))", "lower(giocatori.squadre.nome) like lower(concat('%', concat(#{giocatoriList.giocatori.squadre.nome},'%')))", "giocatori.quotazAttuale <= #{giocatoriList.creditiResiduiDaFiltrare}", "giocatori.stagione = #{giocatoriList.giocatori.stagione}" };

	private static final String SELECT_BY_ID = "select giocatori from Giocatori giocatori where giocatori.id=:idGiocatore";
	private static final String SELECT_ID_BY_STAGIONE = "select giocatori.id from Giocatori giocatori where giocatori.stagione=:stagione";
	private static final String SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.ruolo=:ruolo and giocatori.nome=:nomeGiocatore";
	private static final String SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_AND_STAGIONE = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.ruolo=:ruolo and giocatori.nome=:nomeGiocatore and giocatori.stagione=:stagione";
	private static final String SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_LIKE = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.ruolo=:ruolo and giocatori.nome like ':nomeGiocatore'";
	private static final String SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_LIKE_AND_STAGIONE = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.ruolo=:ruolo and giocatori.nome like ':nomeGiocatore' and giocatori.stagione=:stagione";
	private static final String SELECT_BY_NOME_AND_SQUADRA = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.nome=:nomeGiocatore";
	private static final String SELECT_BY_NOME_AND_SQUADRA_AND_STAGIONE = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.nome=:nomeGiocatore and giocatori.stagione=:stagione";
	private static final String SELECT_BY_NOME_AND_SQUADRA_LIKE = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.nome like ':nomeGiocatore'";
	private static final String SELECT_BY_NOME_AND_SQUADRA_LIKE_AND_STAGIONE = "select giocatori from Giocatori giocatori where giocatori.squadre.nome=:squadra and giocatori.nome like ':nomeGiocatore' and giocatori.stagione=:stagione";

	private static final String SELECT_BY_NOME_AND_RUOLO = "select giocatori from Giocatori giocatori where giocatori.ruolo=:ruolo and giocatori.nome=:nomeGiocatore";
	private static final String SELECT_BY_NOME_AND_RUOLO_AND_STAGIONE = "select giocatori from Giocatori giocatori where giocatori.ruolo=:ruolo and giocatori.nome=:nomeGiocatore and giocatori.stagione=:stagione";

	private static final String UPDATE_STAGIONE_GIOCATORE = "update Giocatori giocatori set giocatori.stagione=:stagione where giocatori.id=:idGiocatore";
	private static final String UPDATE_SQUADRA_GIOCATORE = "update Giocatori giocatori set giocatori.squadre.id=:idSquadra where giocatori.id=:idGiocatore";
	private static final String UPDATE_SQUADRA_QUOTAZ_GIOCATORE = "update Giocatori giocatori set giocatori.squadre.id=:idSquadra, giocatori.quotazIniziale=:quotazIniziale, giocatori.quotazAttuale=:quotazAttuale where giocatori.id=:idGiocatore";
	private static final String UPDATE_SQUADRA_QUOTAZ_GIOCATORE_AND_STAGIONE = "update Giocatori giocatori set giocatori.squadre.id=:idSquadra, giocatori.quotazIniziale=:quotazIniziale, giocatori.quotazAttuale=:quotazAttuale, giocatori.stagione=:stagione where giocatori.id=:idGiocatore";

	private Giocatori giocatori = new Giocatori();
	private BigDecimal creditiResiduiDaFiltrare;

	public GiocatoriList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public Giocatori getGiocatori() {
		return giocatori;
	}

	public BigDecimal getCreditiResiduiDaFiltrare() {
		return creditiResiduiDaFiltrare;
	}

	public void setCreditiResiduiDaFiltrare(BigDecimal creditiResiduiDaFiltrare) {
		this.creditiResiduiDaFiltrare = creditiResiduiDaFiltrare;
	}

	public void initCreditiResiduiDaFiltrare(BigDecimal crediti) {
		if (crediti != null && crediti.compareTo(BigDecimal.ZERO) > 0) {
			this.creditiResiduiDaFiltrare = crediti;
		}
	}

	public Giocatori getGiocatoreById(int idGiocatore) {
		Giocatori giocatoreToReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_ID);
		query.setParameter("idGiocatore", idGiocatore);
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
		}
		return giocatoreToReturn;
	}

	public List<Integer> getGiocatoreIdByStagione(String stagione) {
		Query query = getEntityManager().createQuery(SELECT_ID_BY_STAGIONE);
		query.setParameter("stagione", Constants.getStagione(stagione));
		List<Integer> resultSet = query.getResultList();
		return resultSet;
	}

	/*
	 * Non uso il like perch� i nome dei giocatori dovrebbero rimanere
	 * invariati, NON siamo nel caso che vado a confrontare giocatori di
	 * FantaGazzetta con quelli di Gazzetta
	 */
	private Giocatori getGiocatoreByNomeRuolo(String nomeGiocatore, String ruolo) {
		Giocatori giocatoreToReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_NOME_AND_RUOLO);
		query.setParameter("ruolo", ruolo.trim());
		query.setParameter("nomeGiocatore", nomeGiocatore.trim());
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
		}
		return giocatoreToReturn;
	}

	/*
	 * Non uso il like perch� i nome dei giocatori dovrebbero rimanere
	 * invariati, NON siamo nel caso che vado a confrontare giocatori di
	 * FantaGazzetta con quelli di Gazzetta
	 */
	private Giocatori getGiocatoreByNomeRuolo(String nomeGiocatore, String ruolo, String stagione) {
		Giocatori giocatoreToReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_NOME_AND_RUOLO_AND_STAGIONE);
		query.setParameter("ruolo", ruolo.trim());
		query.setParameter("nomeGiocatore", nomeGiocatore.trim());
		query.setParameter("stagione", Constants.getStagione(stagione));
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
		}
		return giocatoreToReturn;
	}

	public Giocatori getGiocatoreByNomeSquadraRuolo(String nomeGiocatore, String squadra, String ruolo, boolean noLike) {
		Giocatori giocatoreToReturn = null;
		// Prima cerco solo nome giocatore e squadra
		Giocatori existingGiocatore = getGiocatoreByNomeSquadra(nomeGiocatore, squadra, noLike);
		if (existingGiocatore == null) {
			Query query = getEntityManager().createQuery(SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO);
			query.setParameter("squadra", squadra.trim());
			query.setParameter("ruolo", ruolo.trim());
			query.setParameter("nomeGiocatore", nomeGiocatore.trim());
			List<Giocatori> resultSet = query.getResultList();
			if (resultSet.size() == 1) {
				giocatoreToReturn = resultSet.get(0);
			} else if (resultSet.size() > 1 && !noLike) {
				log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "].");
				giocatoreToReturn = this.getGiocatoreByNomeSquadraRuoloLike(nomeGiocatore, squadra, ruolo);
			} else if (resultSet.size() == 0 && !noLike) {
				log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "]");
				giocatoreToReturn = this.getGiocatoreByNomeSquadraRuoloLike(nomeGiocatore, squadra, ruolo);
			}
		} else {
			giocatoreToReturn = existingGiocatore;
		}
		return giocatoreToReturn;
	}

	public Giocatori getGiocatoreByNomeSquadraRuolo(String nomeGiocatore, String squadra, String ruolo, String stagione, boolean noLike) {
		Giocatori giocatoreToReturn = null;
		List<Giocatori> resultSet = null;
		Giocatori existingGiocatore = getGiocatoreByNomeSquadra(nomeGiocatore, squadra, stagione, noLike);
		if (existingGiocatore == null) {
			Query query = getEntityManager().createQuery(SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_AND_STAGIONE);
			query.setParameter("squadra", squadra.trim());
			query.setParameter("ruolo", ruolo.trim());
			query.setParameter("nomeGiocatore", nomeGiocatore.trim());
			query.setParameter("stagione", Constants.getStagione(stagione));
			resultSet = query.getResultList();
			if (resultSet.size() == 1) {
				giocatoreToReturn = resultSet.get(0);
			} else if (resultSet.size() > 1 && !noLike) {
				log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
				giocatoreToReturn = this.getGiocatoreByNomeSquadraRuoloLike(nomeGiocatore, squadra, ruolo, stagione);
			} else if (resultSet.size() == 0 && !noLike) {
				log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
				giocatoreToReturn = this.getGiocatoreByNomeSquadraRuoloLike(nomeGiocatore, squadra, ruolo, stagione);
			}
		} else {
			giocatoreToReturn = existingGiocatore;
		}
		return giocatoreToReturn;
	}

	private Giocatori getGiocatoreByNomeSquadraRuoloLike(String nomeGiocatore, String squadra, String ruolo) {
		Giocatori giocatoreToReturn = null;
		String nomeGiocatoreToSearch = this.preparaStringaRicercaLike(nomeGiocatore);
		String queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_LIKE, ":nomeGiocatore", nomeGiocatoreToSearch.trim());
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("squadra", squadra.trim());
		query.setParameter("ruolo", ruolo.trim());
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
			log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "].");
		} else if (resultSet.size() > 1) {
			log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "].");
		} else if (resultSet.size() == 0) {
			log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "]");
			// Richiamo la stessa funzione passando nomeGiocatore con LIKE da
			// entrambe le parti
			queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_LIKE, ":nomeGiocatore", "%" + nomeGiocatoreToSearch.trim() + "%");
			query = getEntityManager().createQuery(queryString);
			query.setParameter("squadra", squadra.trim());
			query.setParameter("ruolo", ruolo.trim());
			resultSet = query.getResultList();
			if (resultSet.size() == 1) {
				giocatoreToReturn = resultSet.get(0);
				log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "]");
			} else if (resultSet.size() > 1) {
				log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "]");
			} else if (resultSet.size() == 0) {
				log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "]");
			}
		}
		return giocatoreToReturn;
	}

	private Giocatori getGiocatoreByNomeSquadraRuoloLike(String nomeGiocatore, String squadra, String ruolo, String stagione) {
		Giocatori giocatoreToReturn = null;
		String nomeGiocatoreToSearch = this.preparaStringaRicercaLike(nomeGiocatore);
		String queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_LIKE_AND_STAGIONE, ":nomeGiocatore", nomeGiocatoreToSearch.trim());
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("squadra", squadra.trim());
		query.setParameter("ruolo", ruolo.trim());
		query.setParameter("stagione", Constants.getStagione(stagione));
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
			log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
		} else if (resultSet.size() > 1) {
			log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
		} else if (resultSet.size() == 0) {
			log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
			// Richiamo la stessa funzione passando nomeGiocatore con LIKE da
			// entrambe le parti
			queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_AND_RUOLO_LIKE_AND_STAGIONE, ":nomeGiocatore", "%" + nomeGiocatoreToSearch.trim() + "%");
			query = getEntityManager().createQuery(queryString);
			query.setParameter("squadra", squadra.trim());
			query.setParameter("ruolo", ruolo.trim());
			query.setParameter("stagione", Constants.getStagione(stagione));
			resultSet = query.getResultList();
			if (resultSet.size() == 1) {
				giocatoreToReturn = resultSet.get(0);
				log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
			} else if (resultSet.size() > 1) {
				log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
			} else if (resultSet.size() == 0) {
				log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] ruolo [" + ruolo + "] stagione [" + stagione + "].");
			}
		}
		return giocatoreToReturn;
	}

	public Giocatori getGiocatoreByNomeSquadra(String nomeGiocatore, String squadra, boolean noLike) {
		Giocatori giocatoreToReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_NOME_AND_SQUADRA);
		query.setParameter("squadra", squadra.trim());
		query.setParameter("nomeGiocatore", nomeGiocatore.trim());
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
		} else if (resultSet.size() > 1 && !noLike) {
			log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "].");
			giocatoreToReturn = this.getGiocatoreByNomeSquadraLike(nomeGiocatore, squadra);
		} else if (resultSet.size() == 0 && !noLike) {
			log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			giocatoreToReturn = this.getGiocatoreByNomeSquadraLike(nomeGiocatore, squadra);
		}
		return giocatoreToReturn;
	}

	public Giocatori getGiocatoreByNomeSquadra(String nomeGiocatore, String squadra, String stagione, boolean noLike) {
		Giocatori giocatoreToReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_NOME_AND_SQUADRA_AND_STAGIONE);
		query.setParameter("squadra", squadra.trim());
		query.setParameter("nomeGiocatore", nomeGiocatore.trim());
		query.setParameter("stagione", Constants.getStagione(stagione));
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
		} else if (resultSet.size() > 1 && !noLike) {
			log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] stagione [" + stagione + "].");
			giocatoreToReturn = this.getGiocatoreByNomeSquadraLike(nomeGiocatore, squadra, stagione);
		} else if (resultSet.size() == 0 && !noLike) {
			log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "] stagione [" + stagione + "].");
			giocatoreToReturn = this.getGiocatoreByNomeSquadraLike(nomeGiocatore, squadra, stagione);
		}
		return giocatoreToReturn;
	}

	private Giocatori getGiocatoreByNomeSquadraLike(String nomeGiocatore, String squadra) {
		Giocatori giocatoreToReturn = null;
		String nomeGiocatoreToSearch = this.preparaStringaRicercaLike(nomeGiocatore);
		String queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_LIKE, ":nomeGiocatore", nomeGiocatoreToSearch.trim());
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("squadra", squadra.trim());
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
			log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
		} else if (resultSet.size() > 1) {
			log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
		} else if (resultSet.size() == 0) {
			log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			// Richiamo la stessa funzione passando nomeGiocatore con LIKE da
			// entrambe le parti
			queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_LIKE, ":nomeGiocatore", "%" + nomeGiocatoreToSearch.trim() + "%");
			query = getEntityManager().createQuery(queryString);
			query.setParameter("squadra", squadra.trim());
			resultSet = query.getResultList();
			if (resultSet.size() == 1) {
				giocatoreToReturn = resultSet.get(0);
				log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			} else if (resultSet.size() > 1) {
				log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			} else if (resultSet.size() == 0) {
				log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			}
		}
		return giocatoreToReturn;
	}

	private Giocatori getGiocatoreByNomeSquadraLike(String nomeGiocatore, String squadra, String stagione) {
		Giocatori giocatoreToReturn = null;
		String nomeGiocatoreToSearch = this.preparaStringaRicercaLike(nomeGiocatore);
		String queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_LIKE_AND_STAGIONE, ":nomeGiocatore", nomeGiocatoreToSearch.trim());
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("squadra", squadra.trim());
		query.setParameter("stagione", Constants.getStagione(stagione));
		List<Giocatori> resultSet = query.getResultList();
		if (resultSet.size() == 1) {
			giocatoreToReturn = resultSet.get(0);
			log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
		} else if (resultSet.size() > 1) {
			log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
		} else if (resultSet.size() == 0) {
			log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			// Richiamo la stessa funzione passando nomeGiocatore con LIKE da
			// entrambe le parti
			queryString = StringUtils.replace(SELECT_BY_NOME_AND_SQUADRA_LIKE_AND_STAGIONE, ":nomeGiocatore", "%" + nomeGiocatoreToSearch.trim() + "%");
			query = getEntityManager().createQuery(queryString);
			query.setParameter("squadra", squadra.trim());
			query.setParameter("stagione", Constants.getStagione(stagione));
			resultSet = query.getResultList();
			if (resultSet.size() == 1) {
				giocatoreToReturn = resultSet.get(0);
				log.info("Ricerca per LIKE trovato nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			} else if (resultSet.size() > 1) {
				log.info("Trovati pi� giocatori con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			} else if (resultSet.size() == 0) {
				log.info("Nessun giocatore trovato con nome [" + nomeGiocatore + "] e squadra [" + squadra + "]");
			}
		}
		return giocatoreToReturn;
	}

	public void insertGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo) {
		Squadre squadraGiocatore = squadreList.getSquadraFromMapByNome(nomeSquadra);
		giocatoriHome.clearInstance();
		Squadre squadra = new Squadre();
		squadra.setId(squadraGiocatore.getId());
		giocatoriHome.getInstance().setSquadre(squadra);
		giocatoriHome.getInstance().setNome(nomeGiocatore);
		giocatoriHome.getInstance().setRuolo(ruolo);
		giocatoriHome.persist();
	}

	public void insertGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo, String stagione) {
		Squadre squadraGiocatore = squadreList.getSquadraFromMapByNome(nomeSquadra);
		giocatoriHome.clearInstance();
		Squadre squadra = new Squadre();
		squadra.setId(squadraGiocatore.getId());
		giocatoriHome.getInstance().setSquadre(squadra);
		giocatoriHome.getInstance().setNome(nomeGiocatore);
		giocatoriHome.getInstance().setRuolo(ruolo);
		giocatoriHome.getInstance().setStagione(Constants.getStagione(stagione));
		giocatoriHome.persist();
	}

	public int insertGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo, String quotazIniziale, String quotazAttuale) {
		Squadre squadraGiocatore = squadreList.getSquadraFromMapByNome(nomeSquadra);
		giocatoriHome.clearInstance();
		Squadre squadra = new Squadre();
		squadra.setId(squadraGiocatore.getId());
		giocatoriHome.getInstance().setSquadre(squadra);
		giocatoriHome.getInstance().setNome(nomeGiocatore);
		giocatoriHome.getInstance().setRuolo(ruolo);
		giocatoriHome.getInstance().setQuotazIniziale(getQuotazioneFromString(quotazIniziale));
		giocatoriHome.getInstance().setQuotazAttuale(getQuotazioneFromString(quotazAttuale));
		giocatoriHome.persist();

		return giocatoriHome.find().getId();
	}

	public int insertGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo, String quotazIniziale, String quotazAttuale, String stagione) {
		Squadre squadraGiocatore = squadreList.getSquadraFromMapByNome(nomeSquadra);
		giocatoriHome.clearInstance();
		Squadre squadra = new Squadre();
		squadra.setId(squadraGiocatore.getId());
		giocatoriHome.getInstance().setSquadre(squadra);
		giocatoriHome.getInstance().setNome(nomeGiocatore);
		giocatoriHome.getInstance().setRuolo(ruolo);
		giocatoriHome.getInstance().setQuotazIniziale(getQuotazioneFromString(quotazIniziale));
		giocatoriHome.getInstance().setQuotazAttuale(getQuotazioneFromString(quotazAttuale));
		giocatoriHome.getInstance().setStagione(Constants.getStagione(stagione));
		giocatoriHome.persist();

		return giocatoriHome.find().getId();
	}

	public void insertOrUpdateGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo, boolean noLike) {
		/*
		 * Se il giocatore lo trovo gi� con la select per squadra,nome,ruolo
		 * allora nn faccio niente
		 */
		// Prima cerco solo nome giocatore e squadra
		Giocatori existingGiocatore = getGiocatoreByNomeSquadra(nomeGiocatore, nomeSquadra, noLike);
		if (existingGiocatore == null) {
			existingGiocatore = getGiocatoreByNomeSquadraRuolo(nomeGiocatore, nomeSquadra, ruolo, noLike);
			if (existingGiocatore == null) {
				/*
				 * Se non ho trovato il giocatore con quella select allora lo
				 * cerco per nome e ruolo, perch� potrebbe aver cambiato
				 * squadra, cos� aggiorno la squadra
				 */
				existingGiocatore = getGiocatoreByNomeRuolo(nomeGiocatore, ruolo);
				if (existingGiocatore != null) {
					updateSquadraGiocatore(existingGiocatore, nomeSquadra);
				} else {
					insertGiocatore(nomeSquadra, nomeGiocatore, ruolo);
				}
			}
		}
	}

	public void insertOrUpdateGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo, String stagione, boolean noLike) {
		/*
		 * Se il giocatore lo trovo gi� con la select per squadra,nome,ruolo
		 * allora nn faccio niente
		 */
		// Prima cerco solo nome giocatore e squadra
		Giocatori existingGiocatore = getGiocatoreByNomeSquadra(nomeGiocatore, nomeSquadra, stagione, noLike);
		if (existingGiocatore == null) {
			existingGiocatore = getGiocatoreByNomeSquadraRuolo(nomeGiocatore, nomeSquadra, ruolo, stagione, noLike);
			if (existingGiocatore == null) {
				/*
				 * Se non ho trovato il giocatore con quella select allora lo
				 * cerco per nome e ruolo, perch� potrebbe aver cambiato
				 * squadra, cos� aggiorno la squadra
				 */
				existingGiocatore = getGiocatoreByNomeRuolo(nomeGiocatore, ruolo, stagione);
				if (existingGiocatore != null) {
					updateSquadraGiocatore(existingGiocatore, nomeSquadra);
				} else {
					insertGiocatore(nomeSquadra, nomeGiocatore, ruolo, stagione);
				}
			}
		}
	}

	public int insertOrUpdateGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo, String stagione, String quotazIniziale, String quotazAttuale, boolean noLike) {
		/*
		 * Se il giocatore lo trovo gi� con la select per squadra,nome,ruolo
		 * allora nn faccio niente
		 */
		int idGiocatore = -1;

		if (stagione == null) {
			// Prima cerco solo nome giocatore e squadra
			Giocatori existingGiocatore = getGiocatoreByNomeSquadra(nomeGiocatore, nomeSquadra, noLike);
			if (existingGiocatore == null) {
				existingGiocatore = getGiocatoreByNomeSquadraRuolo(nomeGiocatore, nomeSquadra, ruolo, noLike);
				if (existingGiocatore == null) {
					/*
					 * Se non ho trovato il giocatore con quella select allora
					 * lo cerco per nome e ruolo, perch� potrebbe aver cambiato
					 * squadra, cos� aggiorno la squadra
					 */
					existingGiocatore = getGiocatoreByNomeRuolo(nomeGiocatore, ruolo);
					if (existingGiocatore != null) {
						idGiocatore = updateSquadraGiocatore(existingGiocatore, nomeSquadra, quotazIniziale, quotazAttuale);
					} else {
						idGiocatore = insertGiocatore(nomeSquadra, nomeGiocatore, ruolo, quotazIniziale, quotazAttuale);
					}
				}
				else {
					// Aggiorno solo se ci sono le quotazioni diverse
					if (existingGiocatore.getQuotazIniziale().compareTo(getQuotazioneFromString(quotazIniziale)) != 0 || existingGiocatore.getQuotazAttuale().compareTo(getQuotazioneFromString(quotazAttuale)) != 0) {
						idGiocatore = updateSquadraGiocatore(existingGiocatore, nomeSquadra, quotazIniziale, quotazAttuale);
					} else {
						idGiocatore = existingGiocatore.getId();
					}
				}
			}
			if (existingGiocatore != null) {
				// Aggiorno solo se ci sono le quotazioni diverse
				if (existingGiocatore.getQuotazIniziale().compareTo(getQuotazioneFromString(quotazIniziale)) != 0 || existingGiocatore.getQuotazAttuale().compareTo(getQuotazioneFromString(quotazAttuale)) != 0) {
					idGiocatore = updateSquadraGiocatore(existingGiocatore, nomeSquadra, quotazIniziale, quotazAttuale);
				} else {
					idGiocatore = existingGiocatore.getId();
				}
			}
		} else {
			// Prima cerco solo nome giocatore e squadra
			Giocatori existingGiocatore = getGiocatoreByNomeSquadra(nomeGiocatore, nomeSquadra, stagione, noLike);
			if (existingGiocatore == null) {
				existingGiocatore = getGiocatoreByNomeSquadraRuolo(nomeGiocatore, nomeSquadra, ruolo, stagione, noLike);
				if (existingGiocatore == null) {
					/*
					 * Se non ho trovato il giocatore con quella select allora
					 * lo cerco per nome e ruolo, perch� potrebbe aver cambiato
					 * squadra, cos� aggiorno la squadra
					 */
					existingGiocatore = getGiocatoreByNomeRuolo(nomeGiocatore, ruolo, stagione);
					if (existingGiocatore != null) {
						idGiocatore = updateSquadraGiocatore(existingGiocatore, nomeSquadra, quotazIniziale, quotazAttuale, stagione);
					} else {
						idGiocatore = insertGiocatore(nomeSquadra, nomeGiocatore, ruolo, quotazIniziale, quotazAttuale, stagione);
					}
				}
				else {
					// Aggiorno solo se ci sono le quotazioni diverse
					if (existingGiocatore.getQuotazIniziale().compareTo(getQuotazioneFromString(quotazIniziale)) != 0 || existingGiocatore.getQuotazAttuale().compareTo(getQuotazioneFromString(quotazAttuale)) != 0) {
						idGiocatore = updateSquadraGiocatore(existingGiocatore, nomeSquadra, quotazIniziale, quotazAttuale, stagione);
					} else {
						idGiocatore = existingGiocatore.getId();
					}
				}
			}
			if (existingGiocatore != null) {
				// Aggiorno solo se ci sono le quotazioni diverse
				if ((existingGiocatore.getQuotazIniziale() == null || existingGiocatore.getQuotazAttuale() == null) 
						|| (existingGiocatore.getQuotazIniziale().compareTo(getQuotazioneFromString(quotazIniziale)) != 0 || existingGiocatore.getQuotazAttuale().compareTo(getQuotazioneFromString(quotazAttuale)) != 0)) {
					idGiocatore = updateSquadraGiocatore(existingGiocatore, nomeSquadra, quotazIniziale, quotazAttuale, stagione);
				} else {
					idGiocatore = existingGiocatore.getId();
				}
			}
		}
		return idGiocatore;
	}

	private void updateStagioneGiocatore(int idGiocatore, String stagione) {
		log.info("updateStagioneGiocatore, aggiorno il giocatore ID [" + idGiocatore + "] alla stagione [" + stagione + "]");
		Query query = getEntityManager().createQuery(UPDATE_STAGIONE_GIOCATORE);
		query.setParameter("stagione", stagione);
		query.setParameter("idGiocatore", idGiocatore);
		query.executeUpdate();
		this.getEntityManager().flush();
	}

	private void updateSquadraGiocatore(Giocatori giocatoreToUpdate, String nomeSquadra) {
		log.info("updateSquadraGiocatore, aggiorno il giocatore ID [" + giocatoreToUpdate.getId() + "] alla squadra [" + nomeSquadra + "]");
		int idSquadra = squadreList.getSquadraByNome(nomeSquadra).getId();
		Query query = getEntityManager().createQuery(UPDATE_SQUADRA_GIOCATORE);
		query.setParameter("idSquadra", idSquadra);
		query.setParameter("idGiocatore", giocatoreToUpdate.getId());
		query.executeUpdate();
		// giocatoreToUpdate.getSquadre().setId(idSquadra);
		// giocatoreToUpdate = this.getEntityManager().merge(giocatoreToUpdate);
		this.getEntityManager().flush();
	}

	private int updateSquadraGiocatore(Giocatori giocatoreToUpdate, String nomeSquadra, String quotazIniziale, String quotazAttuale) {
		log.info("updateSquadraGiocatore, aggiorno il giocatore ID [" + giocatoreToUpdate.getId() + "] alla squadra [" + nomeSquadra + "]");
		int idSquadra = squadreList.getSquadraByNome(nomeSquadra).getId();
		Query query = getEntityManager().createQuery(UPDATE_SQUADRA_QUOTAZ_GIOCATORE);
		query.setParameter("idSquadra", idSquadra);
		query.setParameter("quotazIniziale", getQuotazioneFromString(quotazIniziale));
		query.setParameter("quotazAttuale", getQuotazioneFromString(quotazAttuale));
		query.setParameter("idGiocatore", giocatoreToUpdate.getId());
		query.executeUpdate();
		// giocatoreToUpdate = this.getEntityManager().merge(giocatoreToUpdate);
		// giocatoreToUpdate.getSquadre().setId(idSquadra);
		this.getEntityManager().flush();
		return giocatoreToUpdate.getId();
	}

	private int updateSquadraGiocatore(Giocatori giocatoreToUpdate, String nomeSquadra, String quotazIniziale, String quotazAttuale, String stagione) {
		log.info("updateSquadraGiocatore, aggiorno il giocatore ID [" + giocatoreToUpdate.getId() + "] alla squadra [" + nomeSquadra + "]");
		int idSquadra = squadreList.getSquadraByNome(nomeSquadra).getId();
		Query query = getEntityManager().createQuery(UPDATE_SQUADRA_QUOTAZ_GIOCATORE_AND_STAGIONE);
		query.setParameter("idSquadra", idSquadra);
		query.setParameter("quotazIniziale", getQuotazioneFromString(quotazIniziale));
		query.setParameter("quotazAttuale", getQuotazioneFromString(quotazAttuale));
		query.setParameter("idGiocatore", giocatoreToUpdate.getId());
		query.setParameter("stagione", Constants.getStagione(stagione));
		query.executeUpdate();
		// giocatoreToUpdate = this.getEntityManager().merge(giocatoreToUpdate);
		// giocatoreToUpdate.getSquadre().setId(idSquadra);
		this.getEntityManager().flush();
		return giocatoreToUpdate.getId();
	}

	public void unmarshallAndSaveFromHtmlFile(boolean noLike) {
		log.info("unmarshallAndSaveFromHtmlFile, entrato");
		String rootHTMLFiles = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_ROOT);
		// Portieri
		String nomeFileGiocatoriP = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_PORTIERI);
		String nomeFileGiocatoriD = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_DIFENSORI);
		String nomeFileGiocatoriC = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_CENTROCAMPISTI);
		String nomeFileGiocatoriA = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_ATTACCANTI);
		String pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriP;
		try {
			squadreList.initMappaSquadre();
			TagNode stagioneTagNode = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//*[@id='stats']/h1").get(0);
			String stagione = stagioneTagNode.getText().toString().trim();
			stagione = StringUtils.substringBetween(stagione.toLowerCase(), "Quotazioni Stagione ".toLowerCase(), " Fantagazzetta.com".toLowerCase());
			stagione = Constants.getStagione(stagione);
			log.info("Leggo i file HTML giocatori per la stagione [" + stagione + "]");
			// TODO Setto a null per non gestire la stagione dei giocatori, da
			// gestire in futuro
			// stagione = null;
			List<Integer> listIdGiocatoriStagione = getGiocatoreIdByStagione(stagione);
			List<TagNode> listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			TagNode currentNodeGiocatore;
			List<TagNode> listNodeSingoloGiocatore;
			List<TagNode> listNodeSingoloGiocatoreQuotaz;
			String currentNomeGiocatore;
			String currentSquadraGiocatore;
			String currentGiocatoreQuotazAttuale;
			String currentGiocatoreQuotazIniziale;
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			int idGiocatoreDb;
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();
				idGiocatoreDb = insertOrUpdateGiocatore(currentSquadraGiocatore, currentNomeGiocatore, Constants.GIOCATORI_RUOLO_PORTIERE, stagione, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, noLike);
				listIdGiocatoriStagione.remove(new Integer(idGiocatoreDb));
			}
			// Difensori
			pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriD;
			listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();
				idGiocatoreDb = insertOrUpdateGiocatore(currentSquadraGiocatore, currentNomeGiocatore, Constants.GIOCATORI_RUOLO_DIFENSORE, stagione, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, noLike);
				listIdGiocatoriStagione.remove(new Integer(idGiocatoreDb));
			}
			// Centrocampisti
			pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriC;
			listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();
				idGiocatoreDb = insertOrUpdateGiocatore(currentSquadraGiocatore, currentNomeGiocatore, Constants.GIOCATORI_RUOLO_CENTROCAMPISTA, stagione, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, noLike);
				listIdGiocatoriStagione.remove(new Integer(idGiocatoreDb));
			}
			// Attaccanti
			pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriA;
			listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();
				idGiocatoreDb = insertOrUpdateGiocatore(currentSquadraGiocatore, currentNomeGiocatore, Constants.GIOCATORI_RUOLO_ATTACCANTE, stagione, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, noLike);
				listIdGiocatoriStagione.remove(new Integer(idGiocatoreDb));
			}
			// Aggiorno la stagione del giocatore a null se il giocatore NON e'
			// piu' in seri A per la stagione che mi interessa
			for (Integer currentIdGiocatoreToRemove : listIdGiocatoriStagione) {
				updateStagioneGiocatore(currentIdGiocatoreToRemove, null);
			}
			log.info("Giocatori rimuossi perche' trasferiti all'estero [" + listIdGiocatoriStagione.size() + "]");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("unmarshallAndSaveFromHtmlFile, uscito");
	}

	public void unmarshallAndSaveFromHtmlFileForUpdateStagione(boolean noLike) {
		log.info("unmarshallAndSaveFromHtmlFile, entrato");
		String rootHTMLFiles = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_ROOT);
		// Portieri
		String nomeFileGiocatoriP = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_PORTIERI);
		String nomeFileGiocatoriD = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_DIFENSORI);
		String nomeFileGiocatoriC = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_CENTROCAMPISTI);
		String nomeFileGiocatoriA = ConfigurationUtil.getValue(Constants.CONF_KEY_HTML_FILE_GIOCATORI_ATTACCANTI);
		String pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriP;
		try {
			squadreList.initMappaSquadre();
			TagNode stagioneTagNode = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//*[@id='stats']/h1").get(0);
			String stagione = stagioneTagNode.getText().toString().trim();
			stagione = StringUtils.substringBetween(stagione.toLowerCase(), "Quotazioni Stagione ".toLowerCase(), " Fantagazzetta.com".toLowerCase());
			stagione = Constants.getStagione(stagione);
			log.info("Leggo i file HTML giocatori per la stagione [" + stagione + "]");
			// TODO Setto a null per non gestire la stagione dei giocatori, da
			// gestire in futuro
			// stagione = null;
			List<TagNode> listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			TagNode currentNodeGiocatore;
			List<TagNode> listNodeSingoloGiocatore;
			List<TagNode> listNodeSingoloGiocatoreQuotaz;
			String currentNomeGiocatore;
			String currentSquadraGiocatore;
			String currentGiocatoreQuotazAttuale;
			String currentGiocatoreQuotazIniziale;
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();

				Giocatori existingGiocatore = getGiocatoreByNomeRuolo(currentNomeGiocatore, Constants.GIOCATORI_RUOLO_PORTIERE);
				if (existingGiocatore != null)
					updateSquadraGiocatore(existingGiocatore, currentSquadraGiocatore, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, stagione);
				else
					log.info("Giocatore [" + currentNomeGiocatore + "] squadra [" + currentSquadraGiocatore + "]");
			}
			// Difensori
			pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriD;
			listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();

				Giocatori existingGiocatore = getGiocatoreByNomeRuolo(currentNomeGiocatore, Constants.GIOCATORI_RUOLO_DIFENSORE);
				if (existingGiocatore != null)
					updateSquadraGiocatore(existingGiocatore, currentSquadraGiocatore, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, stagione);
				else
					log.info("Giocatore [" + currentNomeGiocatore + "] squadra [" + currentSquadraGiocatore + "]");
			}
			// Centrocampisti
			pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriC;
			listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();

				Giocatori existingGiocatore = getGiocatoreByNomeRuolo(currentNomeGiocatore, Constants.GIOCATORI_RUOLO_CENTROCAMPISTA);
				if (existingGiocatore != null)
					updateSquadraGiocatore(existingGiocatore, currentSquadraGiocatore, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, stagione);
				else
					log.info("Giocatore [" + currentNomeGiocatore + "] squadra [" + currentSquadraGiocatore + "]");
			}
			// Attaccanti
			pathCompletoFile = rootHTMLFiles + nomeFileGiocatoriA;
			listNodeGiocatori = HtmlCleanerUtil.getListOfElementsByXPathFromFile(pathCompletoFile, "//div[@class='content']/table/tbody/tr");
			log.info("Leggo il file HTML [" + pathCompletoFile + "]");
			for (int i = 0; i < listNodeGiocatori.size(); i++) {
				currentNodeGiocatore = listNodeGiocatori.get(i);
				listNodeSingoloGiocatore = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td/a");
				currentNomeGiocatore = listNodeSingoloGiocatore.get(0).getText().toString().trim();
				currentSquadraGiocatore = listNodeSingoloGiocatore.get(2).getText().toString().trim();
				listNodeSingoloGiocatoreQuotaz = HtmlCleanerUtil.getListOfElementsByXPathFromElement(currentNodeGiocatore, "//td");
				currentGiocatoreQuotazAttuale = listNodeSingoloGiocatoreQuotaz.get(1).getText().toString().trim();
				currentGiocatoreQuotazIniziale = listNodeSingoloGiocatoreQuotaz.get(2).getText().toString().trim();

				Giocatori existingGiocatore = getGiocatoreByNomeRuolo(currentNomeGiocatore, Constants.GIOCATORI_RUOLO_ATTACCANTE);
				if (existingGiocatore != null)
					updateSquadraGiocatore(existingGiocatore, currentSquadraGiocatore, currentGiocatoreQuotazIniziale, currentGiocatoreQuotazAttuale, stagione);
				else
					log.info("Giocatore [" + currentNomeGiocatore + "] squadra [" + currentSquadraGiocatore + "]");
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

	private String preparaStringaRicercaLike(String stringaRicerca) {
		String stringaLikeToReturn = stringaRicerca.trim();
		// Qui vado a controllare se il nome del giocatore contiene spazi, se li
		// contiene prendo la parte pi� lunga prima o dopo lo spazio
		if (StringUtils.contains(stringaRicerca.trim(), " ")) {
			String nomeGiocatoreBefore = StringUtils.substringBefore(stringaRicerca.trim(), " ");
			String nomeGiocatoreAfter = StringUtils.substringAfter(stringaRicerca.trim(), " ");
			if (nomeGiocatoreBefore.length() > nomeGiocatoreAfter.length()) {
				stringaLikeToReturn = (nomeGiocatoreBefore).concat("%");
			} else {
				stringaLikeToReturn = "%".concat(nomeGiocatoreAfter);
			}
		} else {
			stringaLikeToReturn = "%".concat(stringaLikeToReturn).concat("%");
		}
		// Controllo che non di siano apostrofi all'interno del nome giocatore,
		// perch� ci sono problemi con il LIKE
		if (StringUtils.contains(stringaLikeToReturn.trim(), "'")) {
			if (StringUtils.substringAfter(stringaLikeToReturn, "'").length() > 0) {
				stringaLikeToReturn = StringUtils.substringAfter(stringaLikeToReturn, "'").concat("%");
			} else {
				stringaLikeToReturn = "%".concat(StringUtils.substringBefore(stringaLikeToReturn, "'"));
			}
		}
		return stringaLikeToReturn;
	}

	private BigDecimal getQuotazioneFromString(String quotazione) {
		return BigDecimal.valueOf(Double.parseDouble(StringUtils.substringBefore(quotazione, ",")));
	}
}
