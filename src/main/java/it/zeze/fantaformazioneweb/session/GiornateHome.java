package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.*;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("giornateHome")
public class GiornateHome extends EntityHome<Giornate> {

	public void setGiornateId(Integer id) {
		setId(id);
	}

	public Integer getGiornateId() {
		return (Integer) getId();
	}

	@Override
	protected Giornate createInstance() {
		Giornate giornate = new Giornate();
		return giornate;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Giornate getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Statistiche> getStatistiches() {
		return getInstance() == null ? null : new ArrayList<Statistiche>(getInstance().getStatistiches());
	}

	public List<ProbabiliFormazioniFg> getProbabiliFormazioniFgs() {
		return getInstance() == null ? null : new ArrayList<ProbabiliFormazioniFg>(getInstance().getProbabiliFormazioniFgs());
	}

	public List<ProbabiliFormazioniGazzetta> getProbabiliFormazioniGazzettas() {
		return getInstance() == null ? null : new ArrayList<ProbabiliFormazioniGazzetta>(getInstance().getProbabiliFormazioniGazzettas());
	}

	public List<Calendario> getCalendarios() {
		return getInstance() == null ? null : new ArrayList<Calendario>(getInstance().getCalendarios());
	}

}
