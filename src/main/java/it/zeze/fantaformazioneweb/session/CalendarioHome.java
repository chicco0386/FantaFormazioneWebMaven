package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Calendario;
import it.zeze.fantaformazioneweb.entity.CalendarioId;
import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.Squadre;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("calendarioHome")
public class CalendarioHome extends EntityHome<Calendario> {

	private static final long serialVersionUID = 562508963505415705L;

	@In(create = true)
	SquadreHome squadreHome;
	@In(create = true)
	GiornateHome giornateHome;

	public void setCalendarioId(CalendarioId id) {
		setId(id);
	}

	public CalendarioId getCalendarioId() {
		return (CalendarioId) getId();
	}

	public CalendarioHome() {
		setCalendarioId(new CalendarioId());
	}

	@Override
	public boolean isIdDefined() {
		if (getCalendarioId().getIdGiornata() == 0)
			return false;
		if (getCalendarioId().getIdSquadraCasa() == 0)
			return false;
		if (getCalendarioId().getIdSquadraFuoriCasa() == 0)
			return false;
		return true;
	}

	@Override
	protected Calendario createInstance() {
		Calendario calendario = new Calendario();
		calendario.setId(new CalendarioId());
		return calendario;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Squadre squadreByIdSquadraFuoriCasa = squadreHome.getDefinedInstance();
		if (squadreByIdSquadraFuoriCasa != null) {
			getInstance().setSquadreByIdSquadraFuoriCasa(squadreByIdSquadraFuoriCasa);
		}
		Giornate giornate = giornateHome.getDefinedInstance();
		if (giornate != null) {
			getInstance().setGiornate(giornate);
		}
		Squadre squadreByIdSquadraCasa = squadreHome.getDefinedInstance();
		if (squadreByIdSquadraCasa != null) {
			getInstance().setSquadreByIdSquadraCasa(squadreByIdSquadraCasa);
		}
	}

	public boolean isWired() {
		if (getInstance().getSquadreByIdSquadraFuoriCasa() == null)
			return false;
		if (getInstance().getGiornate() == null)
			return false;
		if (getInstance().getSquadreByIdSquadraCasa() == null)
			return false;
		return true;
	}

	public Calendario getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
