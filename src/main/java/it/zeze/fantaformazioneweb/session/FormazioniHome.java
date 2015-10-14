package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Formazioni;
import it.zeze.fantaformazioneweb.entity.FormazioniId;
import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("formazioniHome")
public class FormazioniHome extends EntityHome<Formazioni> {

	@In(create = true)
	UtentiFormazioniHome utentiFormazioniHome;
	@In(create = true)
	GiocatoriHome giocatoriHome;

	public void setFormazioniId(FormazioniId id) {
		setId(id);
	}

	public FormazioniId getFormazioniId() {
		return (FormazioniId) getId();
	}

	public FormazioniHome() {
		setFormazioniId(new FormazioniId());
	}

	@Override
	public boolean isIdDefined() {
		if (getFormazioniId().getIdGiocatore() == 0)
			return false;
		if (getFormazioniId().getIdUtentiFormazioni() == 0)
			return false;
		return true;
	}

	@Override
	protected Formazioni createInstance() {
		Formazioni formazioni = new Formazioni();
		formazioni.setId(new FormazioniId());
		return formazioni;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
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
		if (getInstance().getUtentiFormazioni() == null)
			return false;
		if (getInstance().getGiocatori() == null)
			return false;
		return true;
	}

	public Formazioni getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
