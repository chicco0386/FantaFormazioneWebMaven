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
import it.zeze.fanta.service.definition.ejb.proxy.seam.GiocatoriSeamRemote;
import it.zeze.fantaformazioneweb.entity.Calendario;
import it.zeze.fantaformazioneweb.entity.CalendarioId;
import it.zeze.fantaformazioneweb.entity.wrapper.GiocatoriWrap;

@Name("calendarioList")
public class CalendarioList extends EntityQuery<Calendario> {

	private static final long serialVersionUID = -6277662181191081023L;

	@Logger
	static Log log;

	private static GiocatoriSeamRemote giocatoriEJB;
	private static CalendarioSeamRemote calendarioEJB;

	@In(create = true)
	SquadreList squadreList;

	@In(create = true)
	GiornateList giornateList;

	@In(create = true)
	CalendarioHome calendarioHome;

	private static final String EJBQL = "select calendario from Calendario calendario";

	private static final String GET_CALENDARIO_BY_ID_GIOR_ID_SQUADRA = "select calendario from Calendario calendario where calendario.id.idGiornata = :idGiornata AND (calendario.id.idSquadraCasa = :idSquadra OR calendario.id.idSquadraFuoriCasa = :idSquadra)";

	private static final String[] RESTRICTIONS = {};

	static {
		try {
			giocatoriEJB = JNDIUtils.getGiocatoriSeamEJB();
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
		GiocatoriWrap giocatore = giocatoriEJB.getGiocatoreById(34);
		log.info(giocatore.getNome());
		log.info(giocatore.getSquadre().getNome());
		squadreList.unmarshallAndSaveFromHtmlFile();
		giornateList.unmarshallAndSaveFromHtmlFile();
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
