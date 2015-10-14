package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.*;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("giocatoriHome")
public class GiocatoriHome extends EntityHome<Giocatori> {

	@In(create = true)
	SquadreHome squadreHome;

	public void setGiocatoriId(Integer id) {
		setId(id);
	}

	public Integer getGiocatoriId() {
		return (Integer) getId();
	}

	@Override
	protected Giocatori createInstance() {
		Giocatori giocatori = new Giocatori();
		return giocatori;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Squadre squadre = squadreHome.getDefinedInstance();
		if (squadre != null) {
			getInstance().setSquadre(squadre);
		}
	}

	public boolean isWired() {
		if (getInstance().getSquadre() == null)
			return false;
		return true;
	}

	public Giocatori getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProbabiliFormazioniFg> getProbabiliFormazioniFgs() {
		return getInstance() == null ? null : new ArrayList<ProbabiliFormazioniFg>(getInstance().getProbabiliFormazioniFgs());
	}

	public List<ProbabiliFormazioniGazzetta> getProbabiliFormazioniGazzettas() {
		return getInstance() == null ? null : new ArrayList<ProbabiliFormazioniGazzetta>(getInstance().getProbabiliFormazioniGazzettas());
	}

	public List<Formazioni> getFormazionis() {
		return getInstance() == null ? null : new ArrayList<Formazioni>(getInstance().getFormazionis());
	}

	public List<Statistiche> getStatistiches() {
		return getInstance() == null ? null : new ArrayList<Statistiche>(getInstance().getStatistiches());
	}

}
