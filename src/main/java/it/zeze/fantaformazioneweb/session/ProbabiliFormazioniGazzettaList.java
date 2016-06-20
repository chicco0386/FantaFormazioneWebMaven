package it.zeze.fantaformazioneweb.session;

import java.util.Arrays;

import javax.naming.NamingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.FormazioniGazzettaSeamRemote;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniGazzetta;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniGazzettaId;

@Name("probabiliFormazioniGazzettaList")
public class ProbabiliFormazioniGazzettaList extends EntityQuery<ProbabiliFormazioniGazzetta> {

	private static final long serialVersionUID = 1953738776469264168L;

	@Logger
	static Log log;

	private static final String EJBQL = "select probabiliFormazioniGazzetta from ProbabiliFormazioniGazzetta probabiliFormazioniGazzetta";

	private static final String[] RESTRICTIONS = {};

	private ProbabiliFormazioniGazzetta probabiliFormazioniGazzetta;
	
	@In(create = true)
	SessionInfo sessionInfo;
	
	private static FormazioniGazzettaSeamRemote formazioniGazzettaEJB;
	
	static {
		try {
			formazioniGazzettaEJB = JNDIUtils.getFormazioniGazzettaEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ProbabiliFormazioniGazzettaList() {
		probabiliFormazioniGazzetta = new ProbabiliFormazioniGazzetta();
		probabiliFormazioniGazzetta.setId(new ProbabiliFormazioniGazzettaId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public ProbabiliFormazioniGazzetta getProbabiliFormazioniGazzetta() {
		return probabiliFormazioniGazzetta;
	}

	public void unmarshallAndSaveFromHtmlFile() {
		String currentStagione = sessionInfo.getStagione();
		formazioniGazzettaEJB.unmarshallAndSaveFromHtmlFile(currentStagione);
	}

	public ProbabiliFormazioniGazzetta selectByIdGiocatoreIdGiornata(int idGiocatore, int idGiornata) {
		return formazioniGazzettaEJB.selectByIdGiocatoreIdGiornata(idGiocatore, idGiornata);
	}
}
