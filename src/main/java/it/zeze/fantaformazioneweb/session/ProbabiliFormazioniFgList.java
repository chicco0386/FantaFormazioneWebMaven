package it.zeze.fantaformazioneweb.session;

import java.util.Arrays;

import javax.naming.NamingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.FormazioniFGSeamRemote;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniFg;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniFgId;

@Name("probabiliFormazioniFgList")
public class ProbabiliFormazioniFgList extends EntityQuery<ProbabiliFormazioniFg> {

	private static final long serialVersionUID = 3908423851085890461L;

	@Logger
	static Log log;
	
	@In(create = true)
	SessionInfo sessionInfo;
	
	private static FormazioniFGSeamRemote formazioniFGEJB;

	private static final String EJBQL = "select probabiliFormazioniFg from ProbabiliFormazioniFg probabiliFormazioniFg";

	private static final String[] RESTRICTIONS = {};
	
	static {
		try {
			formazioniFGEJB = JNDIUtils.getFormazioniFGEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ProbabiliFormazioniFg probabiliFormazioniFg;

	public ProbabiliFormazioniFgList() {
		probabiliFormazioniFg = new ProbabiliFormazioniFg();
		probabiliFormazioniFg.setId(new ProbabiliFormazioniFgId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	public ProbabiliFormazioniFg getProbabiliFormazioniFg() {
		return probabiliFormazioniFg;
	}

	public void unmarshallAndSaveFromHtmlFile() {
		String currentStagione = sessionInfo.getStagione();
		formazioniFGEJB.unmarshallAndSaveFromHtmlFile(currentStagione);
	}

	public ProbabiliFormazioniFg selectByIdGiocatoreIdGiornata(int idGiocatore, int idGiornata) {
		return formazioniFGEJB.selectByIdGiocatoreIdGiornata(idGiocatore, idGiornata);
	}
}
