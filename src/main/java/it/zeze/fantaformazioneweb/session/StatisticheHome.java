package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("statisticheHome")
public class StatisticheHome extends EntityHome<Statistiche> {

	@In(create = true)
	GiocatoriHome giocatoriHome;
	@In(create = true)
	GiornateHome giornateHome;

	public void setStatisticheId(StatisticheId id) {
		setId(id);
	}

	public StatisticheId getStatisticheId() {
		return (StatisticheId) getId();
	}

	public StatisticheHome() {
		setStatisticheId(new StatisticheId());
	}

	@Override
	public boolean isIdDefined() {
		if (getStatisticheId().getIdGiocatore() == 0)
			return false;
		if (getStatisticheId().getIdGiornata() == 0)
			return false;
		if (getStatisticheId().getMediaVoto().intValue() == 0)
			return false;
		if (getStatisticheId().getMediaVotoFm().intValue() == 0)
			return false;
		if (getStatisticheId().getGoalFatti() == 0)
			return false;
		if (getStatisticheId().getGoalRigore() == 0)
			return false;
		if (getStatisticheId().getGoalSubiti() == 0)
			return false;
		if (getStatisticheId().getRigoriParati() == 0)
			return false;
		if (getStatisticheId().getRigoriSbagliati() == 0)
			return false;
		if (getStatisticheId().getAutoreti() == 0)
			return false;
		if (getStatisticheId().getAssist() == 0)
			return false;
		if (getStatisticheId().getAmmonizioni() == 0)
			return false;
		if (getStatisticheId().getEspulsioni() == 0)
			return false;
		return true;
	}

	@Override
	protected Statistiche createInstance() {
		Statistiche statistiche = new Statistiche();
		statistiche.setId(new StatisticheId());
		return statistiche;
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

	public Statistiche getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
