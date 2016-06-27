package it.zeze.fantaformazioneweb.session;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.naming.NamingException;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.bean.ServiceResponse;
import it.zeze.fanta.service.definition.ejb.proxy.seam.UtentiFormazioniSeamRemote;
import it.zeze.fantaformazioneweb.bean.util.Utility;
import it.zeze.fantaformazioneweb.entity.UtentiFormazioni;

@Name("utentiFormazioniList")
public class UtentiFormazioniList extends EntityQuery<UtentiFormazioni> {

	private static final long serialVersionUID = 3366976626785833304L;

	@Logger
	static Log log;
	
	private static UtentiFormazioniSeamRemote utentiFormazioniEJB;
	
	static{
		try {
			utentiFormazioniEJB = JNDIUtils.getUtentiFormazioniEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String EJBQL = "select utentiFormazioni from UtentiFormazioni utentiFormazioni";

	private static final String[] RESTRICTIONS = { "lower(utentiFormazioni.nomeFormazione) like lower(concat(#{utentiFormazioniList.utentiFormazioni.nomeFormazione},'%'))", };

	private UtentiFormazioni utentiFormazioni = new UtentiFormazioni();

	public UtentiFormazioniList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public UtentiFormazioni insertUtenteFormazione(String nomeFormazione, int idUtente, BigDecimal crediti) {
		ServiceResponse ejbResp = utentiFormazioniEJB.insertUtenteFormazione(nomeFormazione, idUtente, crediti);
		Utility.convertServiceResponseToStatusMessage(StatusMessages.instance(), ejbResp);
		UtentiFormazioni toReturn = (UtentiFormazioni) ejbResp.getObjectResponse();
		return toReturn;
	}

	public UtentiFormazioni updateUtenteFormazione(int idUtentiFormazioni, String nomeFormazione, int idUtente, BigDecimal crediti) {
		ServiceResponse ejbResp = utentiFormazioniEJB.updateUtenteFormazione(idUtentiFormazioni, nomeFormazione, idUtente, crediti);
		Utility.convertServiceResponseToStatusMessage(StatusMessages.instance(), ejbResp);
		UtentiFormazioni toReturn = (UtentiFormazioni) ejbResp.getObjectResponse();
		return toReturn;
	}

	public boolean esisteUtentiFormazioni(String nomeFormazione, int idUtente) {
		return utentiFormazioniEJB.esisteUtentiFormazioni(nomeFormazione, idUtente);
	}

	public boolean esisteUtentiFormazioni(int idUtenteFormazione, int idUtente) {
		return esisteUtentiFormazioni(idUtenteFormazione, idUtente);
	}

	public UtentiFormazioni getUtentiFormazioniByIdAndIdUtente(int idUtenteFormazione, int idUtente) {
		return utentiFormazioniEJB.getUtentiFormazioniByIdAndIdUtente(idUtenteFormazione, idUtente).unwrap();
	}

	public UtentiFormazioni getUtentiFormazioni() {
		return utentiFormazioni;
	}

	public void delete(int idUtentiFormazioni, int idUtente) {
		utentiFormazioniEJB.delete(idUtentiFormazioni, idUtente);
	}
}
