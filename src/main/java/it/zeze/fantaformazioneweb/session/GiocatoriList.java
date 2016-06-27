package it.zeze.fantaformazioneweb.session;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.naming.NamingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.GiocatoriSeamRemote;
import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.wrapper.GiocatoriWrap;

@Name("giocatoriList")
public class GiocatoriList extends EntityQuery<Giocatori> {

	private static final long serialVersionUID = -1966301305576124912L;

	@Logger
	static Log log;

	@In(create = true)
	SessionInfo sessionInfo;
	
	private static GiocatoriSeamRemote giocatoriEJB;
	
	static {
		try {
			giocatoriEJB = JNDIUtils.getGiocatoriEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String EJBQL = "select giocatori from Giocatori giocatori";

	private static final String[] RESTRICTIONS = { "lower(giocatori.nome) like lower(concat('%', concat(#{giocatoriList.giocatori.nome},'%')))", "lower(giocatori.ruolo) like lower(concat('%', concat(#{giocatoriList.giocatori.ruolo},'%')))", "lower(giocatori.squadre.nome) like lower(concat('%', concat(#{giocatoriList.giocatori.squadre.nome},'%')))", "giocatori.quotazAttuale <= #{giocatoriList.creditiResiduiDaFiltrare}", "giocatori.stagione = #{giocatoriList.giocatori.stagione}" };

	private Giocatori giocatori = new Giocatori();
	private BigDecimal creditiResiduiDaFiltrare;

	public GiocatoriList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public Giocatori getGiocatori() {
		return giocatori;
	}

	public BigDecimal getCreditiResiduiDaFiltrare() {
		return creditiResiduiDaFiltrare;
	}

	public void setCreditiResiduiDaFiltrare(BigDecimal creditiResiduiDaFiltrare) {
		this.creditiResiduiDaFiltrare = creditiResiduiDaFiltrare;
	}

	public void initCreditiResiduiDaFiltrare(BigDecimal crediti) {
		if (crediti != null && crediti.compareTo(BigDecimal.ZERO) > 0) {
			this.creditiResiduiDaFiltrare = crediti;
		}
	}

	public Giocatori getGiocatoreById(int idGiocatore) {
		return unwrapEJBResp(giocatoriEJB.getGiocatoreById(idGiocatore));
	}

	public Giocatori getGiocatoreByNomeSquadraRuolo(String nomeGiocatore, String squadra, String ruolo, String stagione, boolean noLike) {
		return unwrapEJBResp(giocatoriEJB.getGiocatoreByNomeSquadraRuolo(nomeGiocatore, squadra, ruolo, stagione, noLike));
	}

	public Giocatori getGiocatoreByNomeSquadra(String nomeGiocatore, String squadra, String stagione, boolean noLike) {
		return unwrapEJBResp(giocatoriEJB.getGiocatoreByNomeSquadra(nomeGiocatore, squadra, stagione, noLike));
	}

	public void insertOrUpdateGiocatore(String nomeSquadra, String nomeGiocatore, String ruolo, String stagione, boolean noLike) {
		giocatoriEJB.insertOrUpdateGiocatore(nomeSquadra, nomeGiocatore, ruolo, stagione, noLike);
	}

	public void unmarshallAndSaveFromHtmlFile(boolean noLike) {
		String stagione = sessionInfo.getStagione();
		giocatoriEJB.unmarshallAndSaveFromHtmlFile(stagione, noLike);
	}

	public void unmarshallAndSaveFromHtmlFileForUpdateStagione(boolean noLike) {
		giocatoriEJB.unmarshallAndSaveFromHtmlFileForUpdateStagione(noLike);
	}
	
	private Giocatori unwrapEJBResp(GiocatoriWrap ejbResp){
		if (ejbResp != null){
			return ejbResp.unwrap();
		}
		return null;
	}
}
