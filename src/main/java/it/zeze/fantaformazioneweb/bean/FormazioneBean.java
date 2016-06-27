package it.zeze.fantaformazioneweb.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;

import it.zeze.fanta.service.bean.GiocatoriMercato;
import it.zeze.fantaformazioneweb.entity.Formazioni;
import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;
import it.zeze.fantaformazioneweb.session.FormazioniList;
import it.zeze.fantaformazioneweb.session.GiocatoriList;
import it.zeze.fantaformazioneweb.session.UtentiFormazioniList;
import it.zeze.util.GiocatoriComparator;

@Name("formazioneBean")
@Scope(ScopeType.PAGE)
public class FormazioneBean {

	@Logger
	static Log log;

	@In(create = true)
	UtentiFormazioniList utentiFormazioniList;

	@In(create = true)
	GiocatoriList giocatoriList;

	@In(create = true)
	FormazioniList formazioniList;

	@DataModel
	private List<Giocatori> listaGiocatori = new ArrayList<Giocatori>();

	private boolean doInit = true;
	private boolean doInitCrediti = true;

	private int idUtenteFormazioneToInit = -1;

	@DataModel
	private List<GiocatoriMercato> listaGiocatoriMercato = new ArrayList<GiocatoriMercato>();
	private BigDecimal prezzoAcquisto = BigDecimal.ZERO;
	private BigDecimal creditiResidui = BigDecimal.ZERO;

	public void initListaGiocatoriMercato(int idUtente) {
		log.info("Init listaGiocatoriMercato doInit [" + doInit + "]");
		if (idUtenteFormazioneToInit != -1) {
			if (doInit) {
				if (utentiFormazioniList.esisteUtentiFormazioni(idUtenteFormazioneToInit, idUtente)) {
					listaGiocatoriMercato.clear();
					List<Formazioni> formazioni = formazioniList.selectFormazioniByIdUtenteFormazioni(idUtenteFormazioneToInit);
					for (int i = 0; i < formazioni.size(); i++) {
						GiocatoriMercato giocatoreMercato = new GiocatoriMercato(formazioni.get(i).getGiocatori());
						if (formazioni.get(i).getPrezzoAcquisto() == null) {
							giocatoreMercato.setPrezzoAcquisto(BigDecimal.ZERO);
						} else {
							giocatoreMercato.setPrezzoAcquisto(formazioni.get(i).getPrezzoAcquisto());
						}
						listaGiocatoriMercato.add(giocatoreMercato);
					}
					// lo setto in modo da nn rifare l'init se eseguo un action
					// ajax sulla pagina
					doInit = false;
				}
			}
		}
		Collections.sort(listaGiocatoriMercato, Collections.reverseOrder(new GiocatoriComparator()));
	}

	public void initListaGiocatori(int idUtente) {
		log.info("Init listaGiocatori doInit [" + doInit + "]");
		if (idUtenteFormazioneToInit != -1) {
			if (doInit) {
				if (utentiFormazioniList.esisteUtentiFormazioni(idUtenteFormazioneToInit, idUtente)) {
					listaGiocatori.clear();
					List<Formazioni> formazioni = formazioniList.selectFormazioniByIdUtenteFormazioni(idUtenteFormazioneToInit);
					for (int i = 0; i < formazioni.size(); i++) {
						listaGiocatori.add(formazioni.get(i).getGiocatori());
					}
					// lo setto in modo da nn rifare l'init se eseguo un action
					// ajax sulla pagina
					doInit = false;
				}
			}
		}
		Collections.sort(listaGiocatori, Collections.reverseOrder(new GiocatoriComparator()));
	}

	public void addGiocatoreMercato(Giocatori giocatoreToInsert, BigDecimal prezzoAcquisto) {
		if (!listaGiocatoriMercato.contains(giocatoreToInsert)) {
			log.info("Prezzo acquisto: [" + prezzoAcquisto.toPlainString() + "]");
			GiocatoriMercato mercato = new GiocatoriMercato(giocatoreToInsert);
			mercato.setPrezzoAcquisto(prezzoAcquisto);
			listaGiocatoriMercato.add(mercato);
			creditiResidui = creditiResidui.subtract(mercato.getPrezzoAcquisto());
			this.prezzoAcquisto = BigDecimal.ZERO;
		}
	}

	public void add(Giocatori giocatoreToInsert) {
		if (!listaGiocatori.contains(giocatoreToInsert)) {
			listaGiocatori.add(giocatoreToInsert);
		}
	}

	public List<Giocatori> getListaGiocatori() {
		return listaGiocatori;
	}

	public void setListaGiocatori(List<Giocatori> listaGiocatori) {
		this.listaGiocatori = listaGiocatori;
	}

	public int getIdUtenteFormazioneToInit() {
		return idUtenteFormazioneToInit;
	}

	public boolean isDoInit() {
		return doInit;
	}

	public void setDoInit(boolean doInit) {
		this.doInit = doInit;
	}

	public boolean isDoInitCrediti() {
		return doInitCrediti;
	}

	public void setDoInitCrediti(boolean doInitCrediti) {
		this.doInitCrediti = doInitCrediti;
	}

	public void setIdUtenteFormazioneToInit(int idUtenteFormazioneToInit) {
		this.idUtenteFormazioneToInit = idUtenteFormazioneToInit;
	}

	public List<GiocatoriMercato> getListaGiocatoriMercato() {
		return listaGiocatoriMercato;
	}

	public void setListaGiocatoriMercato(List<GiocatoriMercato> listaGiocatoriMercato) {
		this.listaGiocatoriMercato = listaGiocatoriMercato;
	}

	public BigDecimal getPrezzoAcquisto() {
		return prezzoAcquisto;
	}

	public void setPrezzoAcquisto(BigDecimal prezzoAcquisto) {
		this.prezzoAcquisto = prezzoAcquisto;
	}

	public BigDecimal getCreditiResidui() {
		return creditiResidui;
	}

	public void setCreditiResidui(BigDecimal creditiResidui) {
		this.creditiResidui = creditiResidui;
	}

	public void removeMercato(GiocatoriMercato giocatoreToRemove, BigDecimal prezzo) {
		listaGiocatoriMercato.remove(giocatoreToRemove);
		creditiResidui = creditiResidui.add(prezzo);
	}

	public void remove(Giocatori giocatoreToRemove) {
		listaGiocatori.remove(giocatoreToRemove);
	}

	public void initCreditiResidui(int idUtentiFormazioni, int idUtente) {
		log.info("initCreditiResidui doInitCrediti [" + doInitCrediti + "]");
		UtentiFormazioni utForm = utentiFormazioniList.getUtentiFormazioniByIdAndIdUtente(idUtentiFormazioni, idUtente);
		if (utForm.getCrediti() != null) {
			this.creditiResidui = utForm.getCrediti();
		} else {
			this.creditiResidui = BigDecimal.ZERO;
		}
		this.doInitCrediti = false;
	}
}
