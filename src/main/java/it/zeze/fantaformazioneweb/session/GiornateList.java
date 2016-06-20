package it.zeze.fantaformazioneweb.session;

import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.GiornateSeamRemote;
import it.zeze.fantaformazioneweb.entity.Giornate;

@Name("giornateList")
public class GiornateList extends EntityQuery<Giornate> {

	private static final long serialVersionUID = 8967335917888183720L;

	@Logger
	static Log log;

	@In(create = true)
	SessionInfo sessionInfo;
	
	private static final String EJBQL = "select giornate from Giornate giornate";

	private static final String[] RESTRICTIONS = {};
	
	private static GiornateSeamRemote giornateEJB;
	
	static {
		try {
			giornateEJB = JNDIUtils.getGiornateEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Giornate giornate = new Giornate();

	public GiornateList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public Giornate getGiornate() {
		return giornate;
	}

	public void unmarshallAndSaveFromHtmlFile() {
		String stagione = sessionInfo.getStagione();
		giornateEJB.unmarshallAndSaveFromHtmlFile(stagione);
	}

	public String getStagione(String stagioneInput) {
		return giornateEJB.getStagione(stagioneInput);
	}

	public int getIdGiornata(int numeroGiornata, String stagione) {
		return giornateEJB.getIdGiornata(numeroGiornata, stagione);
	}

	public Giornate getGiornataById(int idGiornata) {
		return giornateEJB.getGiornataById(idGiornata);
	}

	public int getIdGiornata(String dataString) {
		return giornateEJB.getIdGiornata(dataString);
	}

	public Giornate getGiornata(String dataString) {
		return giornateEJB.getGiornata(dataString);
	}

	public Giornate getLastGiornata() {
		return giornateEJB.getLastGiornata();
	}

	public List<Giornate> getGiornateByStagione(String stagione) {
		return giornateEJB.getGiornateByStagione(stagione);
	}
}
