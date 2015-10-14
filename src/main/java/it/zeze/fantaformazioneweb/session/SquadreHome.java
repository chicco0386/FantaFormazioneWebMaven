package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.*;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("squadreHome")
public class SquadreHome extends EntityHome<Squadre> {

	public void setSquadreId(Integer id) {
		setId(id);
	}

	public Integer getSquadreId() {
		return (Integer) getId();
	}

	@Override
	protected Squadre createInstance() {
		Squadre squadre = new Squadre();
		return squadre;
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

	public Squadre getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Giocatori> getGiocatoris() {
		return getInstance() == null ? null : new ArrayList<Giocatori>(getInstance().getGiocatoris());
	}

	public List<Calendario> getCalendariosForIdSquadraCasa() {
		return getInstance() == null ? null : new ArrayList<Calendario>(getInstance().getCalendariosForIdSquadraCasa());
	}

	public List<Calendario> getCalendariosForIdSquadraFuoriCasa() {
		return getInstance() == null ? null : new ArrayList<Calendario>(getInstance().getCalendariosForIdSquadraFuoriCasa());
	}

}
