package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioni;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniId;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("probabiliFormazioniHome")
public class ProbabiliFormazioniHome extends EntityHome<ProbabiliFormazioni> {

	@In(create = true)
	GiornateHome giornateHome;
	@In(create = true)
	UtentiFormazioniHome utentiFormazioniHome;
	@In(create = true)
	GiocatoriHome giocatoriHome;

	public void setProbabiliFormazioniId(ProbabiliFormazioniId id) {
		setId(id);
	}

	public ProbabiliFormazioniId getProbabiliFormazioniId() {
		return (ProbabiliFormazioniId) getId();
	}

	public ProbabiliFormazioniHome() {
		setProbabiliFormazioniId(new ProbabiliFormazioniId());
	}

	@Override
	public boolean isIdDefined() {
		if (getProbabiliFormazioniId().getIdGiornate() == 0)
			return false;
		if (getProbabiliFormazioniId().getIdUtentiFormazioni() == 0)
			return false;
		if (getProbabiliFormazioniId().getIdGiocatore() == 0)
			return false;
		if (getProbabiliFormazioniId().getProbTitolare() == 0)
			return false;
		if (getProbabiliFormazioniId().getProbPanchina() == 0)
			return false;
		return true;
	}

	@Override
	protected ProbabiliFormazioni createInstance() {
		ProbabiliFormazioni probabiliFormazioni = new ProbabiliFormazioni();
		probabiliFormazioni.setId(new ProbabiliFormazioniId());
		return probabiliFormazioni;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Giornate giornate = giornateHome.getDefinedInstance();
		if (giornate != null) {
			getInstance().setGiornate(giornate);
		}
		UtentiFormazioni utentiFormazioni = utentiFormazioniHome.getDefinedInstance();
		if (utentiFormazioni != null) {
			getInstance().setUtentiFormazioni(utentiFormazioni);
		}
		Giocatori giocatori = giocatoriHome.getDefinedInstance();
		if (giocatori != null) {
			getInstance().setGiocatori(giocatori);
		}
	}

	public boolean isWired() {
		if (getInstance().getGiornate() == null)
			return false;
		if (getInstance().getUtentiFormazioni() == null)
			return false;
		if (getInstance().getGiocatori() == null)
			return false;
		return true;
	}

	public ProbabiliFormazioni getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
