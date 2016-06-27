package it.zeze.fantaformazioneweb.session;

import java.math.BigDecimal;
import java.util.List;

import javax.naming.NamingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.bean.GiocatoriMercato;
import it.zeze.fanta.service.bean.ServiceResponse;
import it.zeze.fanta.service.definition.ejb.FormazioniRemote;
import it.zeze.fantaformazioneweb.bean.FormazioneBean;
import it.zeze.fantaformazioneweb.bean.util.Utility;
import it.zeze.fantaformazioneweb.entity.Formazioni;
import it.zeze.fantaformazioneweb.entity.FormazioniId;
import it.zeze.fantaformazioneweb.entity.Giocatori;

@Name("formazioniList")
public class FormazioniList extends EntityQuery<Formazioni> {

	private static final long serialVersionUID = -5475364340053573535L;

	@Logger
	static Log log;

	@In(create = true)
	FormazioneBean formazioneBean;
	
	private static FormazioniRemote formazioniEJB;
	
	static {
		try {
			formazioniEJB = JNDIUtils.getFormazioniEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String EJBQL = "select formazioni from Formazioni formazioni";
	
	private Formazioni formazioni;

	public FormazioniList() {
		formazioni = new Formazioni();
		formazioni.setId(new FormazioniId());
		setEjbql(EJBQL);
		// setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}
	
	public boolean insertFormazioneMercato(String nomeFormazione, List<GiocatoriMercato> listaGiocatori, int idUtente, int idUtenteFormazioneToUpdate, BigDecimal crediti) {
		boolean toReturn = false;
		ServiceResponse response = formazioniEJB.insertFormazioneMercato(nomeFormazione, listaGiocatori, idUtente, idUtenteFormazioneToUpdate, crediti);
		toReturn = (Boolean) response.getObjectResponse();
		Utility.convertServiceResponseToStatusMessage(StatusMessages.instance(), response);
		return toReturn;
	}

	public boolean insertFormazione(String nomeFormazione, List<Giocatori> listaGiocatori, int idUtente, int idUtenteFormazioneToUpdate) {
		boolean toReturn = false;
		ServiceResponse response = formazioniEJB.insertFormazione(nomeFormazione, listaGiocatori, idUtente, idUtenteFormazioneToUpdate);
		toReturn = (Boolean) response.getObjectResponse();
		Utility.convertServiceResponseToStatusMessage(StatusMessages.instance(), response);
		return toReturn;
	}

	public boolean calcolaFormazione(int idUtentiFormazioni, String stagioneDaCalcolare, int numeroGiornataDaCalcolare) {
		boolean toReturn = false;
		ServiceResponse response = formazioniEJB.calcolaFormazione(idUtentiFormazioni, stagioneDaCalcolare, numeroGiornataDaCalcolare);
		toReturn = (Boolean) response.getObjectResponse();
		Utility.convertServiceResponseToStatusMessage(StatusMessages.instance(), response);
		return toReturn;
	}

	public List<Formazioni> selectFormazioniByIdUtenteFormazioni(int idUtentiFormazioni) {
		List<Formazioni> toReturn = formazioniEJB.selectFormazioniByIdUtenteFormazioni(idUtentiFormazioni);
		return toReturn;
	}
	
	public void deleteGiocatoreByIdFormazione(int idUtentiFormazioni) {
		formazioniEJB.deleteGiocatoreByIdFormazione(idUtentiFormazioni);
	}

	public Formazioni getFormazioni() {
		return formazioni;
	}
}
