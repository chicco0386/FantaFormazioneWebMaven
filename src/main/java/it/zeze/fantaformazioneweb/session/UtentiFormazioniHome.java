package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Formazioni;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioni;
import it.zeze.fantaformazioneweb.entity.Utenti;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("utentiFormazioniHome")
public class UtentiFormazioniHome extends EntityHome<UtentiFormazioni> {

	@In(create = true)
	UtentiHome utentiHome;

	public void setUtentiFormazioniId(Integer id) {
		setId(id);
	}

	public Integer getUtentiFormazioniId() {
		return (Integer) getId();
	}

	@Override
	protected UtentiFormazioni createInstance() {
		UtentiFormazioni utentiFormazioni = new UtentiFormazioni();
		return utentiFormazioni;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Utenti utenti = utentiHome.getDefinedInstance();
		if (utenti != null) {
			getInstance().setUtenti(utenti);
		}
	}

	public boolean isWired() {
		if (getInstance().getUtenti() == null)
			return false;
		return true;
	}

	public UtentiFormazioni getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProbabiliFormazioni> getProbabiliFormazionis() {
		return getInstance() == null ? null : new ArrayList<ProbabiliFormazioni>(getInstance().getProbabiliFormazionis());
	}

	public List<Formazioni> getFormazionis() {
		return getInstance() == null ? null : new ArrayList<Formazioni>(getInstance().getFormazionis());
	}

}
