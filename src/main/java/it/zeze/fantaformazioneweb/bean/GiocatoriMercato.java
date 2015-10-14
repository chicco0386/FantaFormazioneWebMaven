package it.zeze.fantaformazioneweb.bean;

import java.math.BigDecimal;

import it.zeze.fantaformazioneweb.entity.Giocatori;

public class GiocatoriMercato extends Giocatori {

	private BigDecimal prezzoAcquisto;

	public GiocatoriMercato() {
	}

	public GiocatoriMercato(Giocatori giocatore) {
		super(giocatore.getId(), giocatore.getSquadre(), giocatore.getNome(), giocatore.getRuolo(), giocatore.getStagione(), giocatore.getQuotazIniziale(), giocatore.getQuotazAttuale());
	}

	public BigDecimal getPrezzoAcquisto() {
		return prezzoAcquisto;
	}

	public void setPrezzoAcquisto(BigDecimal prezzoAcquisto) {
		this.prezzoAcquisto = prezzoAcquisto;
	}

	@Override
	public boolean equals(Object o) {
		boolean equals = false;
		if (o instanceof GiocatoriMercato) {
			GiocatoriMercato giocatoreToCompare = (GiocatoriMercato) o;
			if (giocatoreToCompare.getId() == getId()) {
				equals = true;
			}
		}
		return equals;
	}

}
