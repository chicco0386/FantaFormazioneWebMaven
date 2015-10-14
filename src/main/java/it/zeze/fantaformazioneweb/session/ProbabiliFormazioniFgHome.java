package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("probabiliFormazioniFgHome")
public class ProbabiliFormazioniFgHome extends EntityHome<ProbabiliFormazioniFg> {

	@In(create = true)
	GiocatoriHome giocatoriHome;
	@In(create = true)
	GiornateHome giornateHome;

	public void setProbabiliFormazioniFgId(ProbabiliFormazioniFgId id) {
		setId(id);
	}

	public ProbabiliFormazioniFgId getProbabiliFormazioniFgId() {
		return (ProbabiliFormazioniFgId) getId();
	}

	public ProbabiliFormazioniFgHome() {
		setProbabiliFormazioniFgId(new ProbabiliFormazioniFgId());
	}

	@Override
	public boolean isIdDefined() {
		if (getProbabiliFormazioniFgId().getIdGiocatore() == 0)
			return false;
		if (getProbabiliFormazioniFgId().getIdGiornata() == 0)
			return false;
		if (getProbabiliFormazioniFgId().isTitolare())
			return false;
		if (getProbabiliFormazioniFgId().isPanchina())
			return false;
		return true;
	}

	@Override
	protected ProbabiliFormazioniFg createInstance() {
		ProbabiliFormazioniFg probabiliFormazioniFg = new ProbabiliFormazioniFg();
		probabiliFormazioniFg.setId(new ProbabiliFormazioniFgId());
		return probabiliFormazioniFg;
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

	public ProbabiliFormazioniFg getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
