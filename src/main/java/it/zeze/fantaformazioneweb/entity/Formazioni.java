package it.zeze.fantaformazioneweb.entity;

// Generated 19-gen-2012 10.57.55 by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

/**
 * Formazioni generated by hbm2java
 */
@Entity
@Table(name = "formazioni", catalog = "fanta_formazione")
public class Formazioni implements java.io.Serializable {

	private FormazioniId id;
	private BigDecimal prezzoAcquisto;
	private UtentiFormazioni utentiFormazioni;
	private Giocatori giocatori;

	public Formazioni() {
	}

	public Formazioni(FormazioniId id, UtentiFormazioni utentiFormazioni, Giocatori giocatori) {
		this.id = id;
		this.utentiFormazioni = utentiFormazioni;
		this.giocatori = giocatori;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "idGiocatore", column = @Column(name = "id_giocatore", nullable = false)), @AttributeOverride(name = "idUtentiFormazioni", column = @Column(name = "id_utenti_formazioni", nullable = false)) })
	@NotNull
	public FormazioniId getId() {
		return this.id;
	}

	public void setId(FormazioniId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utenti_formazioni", nullable = false, insertable = false, updatable = false)
	@NotNull
	public UtentiFormazioni getUtentiFormazioni() {
		return this.utentiFormazioni;
	}

	public void setUtentiFormazioni(UtentiFormazioni utentiFormazioni) {
		this.utentiFormazioni = utentiFormazioni;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_giocatore", nullable = false, insertable = false, updatable = false)
	@NotNull
	public Giocatori getGiocatori() {
		return this.giocatori;
	}

	public void setGiocatori(Giocatori giocatori) {
		this.giocatori = giocatori;
	}

	@Column(name = "prezzo_acquisto", nullable = true, precision = 2, scale = 2)
	public BigDecimal getPrezzoAcquisto() {
		return prezzoAcquisto;
	}

	public void setPrezzoAcquisto(BigDecimal prezzoAcquisto) {
		this.prezzoAcquisto = prezzoAcquisto;
	}

}
