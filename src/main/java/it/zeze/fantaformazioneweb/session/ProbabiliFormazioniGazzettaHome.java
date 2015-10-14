package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("probabiliFormazioniGazzettaHome")
public class ProbabiliFormazioniGazzettaHome extends EntityHome<ProbabiliFormazioniGazzetta> {

	@In(create = true)
	GiocatoriHome giocatoriHome;
	@In(create = true)
	GiornateHome giornateHome;

	public void setProbabiliFormazioniGazzettaId(ProbabiliFormazioniGazzettaId id) {
		setId(id);
	}

	public ProbabiliFormazioniGazzettaId getProbabiliFormazioniGazzettaId() {
		return (ProbabiliFormazioniGazzettaId) getId();
	}

	public ProbabiliFormazioniGazzettaHome() {
		setProbabiliFormazioniGazzettaId(new ProbabiliFormazioniGazzettaId());
	}

	@Override
	public boolean isIdDefined() {
		if (getProbabiliFormazioniGazzettaId().getIdGiocatore() == 0)
			return false;
		if (getProbabiliFormazioniGazzettaId().getIdGiornata() == 0)
			return false;
		if (getProbabiliFormazioniGazzettaId().isTitolare())
			return false;
		if (getProbabiliFormazioniGazzettaId().isPanchina())
			return false;
		return true;
	}

	@Override
	protected ProbabiliFormazioniGazzetta createInstance() {
		ProbabiliFormazioniGazzetta probabiliFormazioniGazzetta = new ProbabiliFormazioniGazzetta();
		probabiliFormazioniGazzetta.setId(new ProbabiliFormazioniGazzettaId());
		return probabiliFormazioniGazzetta;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Giocatori giocatori = giocatoriHome.getDefinedInstance();
		if (giocatori != null) {
			getInstance().setGiocatori(giocatori);
		}
		Giornate giornate = giornateHome.getDefinedInstance();
		if (giornate != null) {
			getInstance().setGiornate(giornate);
		}
	}

	public boolean isWired() {
		if (getInstance().getGiocatori() == null)
			return false;
		if (getInstance().getGiornate() == null)
			return false;
		return true;
	}

	public ProbabiliFormazioniGazzetta getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
