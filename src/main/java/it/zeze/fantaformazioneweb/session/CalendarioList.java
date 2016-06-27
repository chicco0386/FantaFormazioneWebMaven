package it.zeze.fantaformazioneweb.session;

import java.util.Arrays;

import javax.naming.NamingException;

import org.htmlcleaner.TagNode;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.CalendarioSeamRemote;
import it.zeze.fantaformazioneweb.entity.Calendario;
import it.zeze.fantaformazioneweb.entity.CalendarioId;

@Name("calendarioList")
public class CalendarioList extends EntityQuery<Calendario> {

	private static final long serialVersionUID = -6277662181191081023L;

	@Logger
	static Log log;
	
	@In(create = true)
	SessionInfo sessionInfo;

	private static CalendarioSeamRemote calendarioEJB;

	private static final String EJBQL = "select calendario from Calendario calendario";

	private static final String[] RESTRICTIONS = {};

	static {
		try {
			calendarioEJB = JNDIUtils.getCalendarioEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Calendario calendario;

	public CalendarioList() {
		calendario = new Calendario();
		calendario.setId(new CalendarioId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public Calendario getCalendario() {
		return calendario;
	}

	public void inizializzaCalendario() {
		String stagione = sessionInfo.getStagione();
		calendarioEJB.inizializzaCalendario(stagione);
	}

	public void unmarshallAndSaveFromNodeCalendario(int idGiornata, TagNode calendarNode) {
		calendarioEJB.unmarshallAndSaveFromNodeCalendario(idGiornata, calendarNode);
	}

	public Calendario getCalendarioByIdGiornataIdSquadra(int idGiornata, int idSquadra) {
		return calendarioEJB.getCalendarioByIdGiornataIdSquadra(idGiornata, idSquadra);
	}

	public String getNomeSquadraAvversaria(int idGiornata, int idSquadra) {
		return calendarioEJB.getNomeSquadraAvversaria(idGiornata, idSquadra);
	}

	public boolean isSquadraFuoriCasa(int idGiornata, int idSquadra) {
		return calendarioEJB.isSquadraFuoriCasa(idGiornata, idSquadra);
	}
}
