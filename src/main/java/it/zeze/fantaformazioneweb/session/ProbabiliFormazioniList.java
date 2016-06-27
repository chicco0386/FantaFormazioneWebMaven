package it.zeze.fantaformazioneweb.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.ProbabiliFormazioniSeamRemote;
import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioni;
import it.zeze.fantaformazioneweb.entity.ProbabiliFormazioniId;
import it.zeze.fantaformazioneweb.entity.wrapper.ProbabiliFormazioniWrap;

@Name("probabiliFormazioniList")
public class ProbabiliFormazioniList extends EntityQuery<ProbabiliFormazioni> {

	private static final long serialVersionUID = 4485353378796659672L;

	@Logger
	static Log log;

	@In(create = true)
	StatisticheList statisticheList;

	@In(create = true)
	GiornateList giornateList;
	
	private static ProbabiliFormazioniSeamRemote probabiliFormazioniEJB;
	
	static {
		try {
			probabiliFormazioniEJB = JNDIUtils.getProbabiliFormazioniEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String EJBQL = "select probabiliFormazioni from ProbabiliFormazioni probabiliFormazioni";

	private static final String[] RESTRICTIONS = { "probabiliFormazioni.id.idGiornate = #{probabiliFormazioniList.probabiliFormazioni.id.idGiornate}", "probabiliFormazioni.id.idUtentiFormazioni = #{probabiliFormazioniList.probabiliFormazioni.id.idUtentiFormazioni}" };

	private ProbabiliFormazioni probabiliFormazioni;

	private List<ProbabiliFormazioni> resultList;

	public ProbabiliFormazioniList() {
		probabiliFormazioni = new ProbabiliFormazioni();
		probabiliFormazioni.setGiornate(new Giornate());
		probabiliFormazioni.setId(new ProbabiliFormazioniId());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		refresh();
	}

	public List<ProbabiliFormazioni> getRisultati(int idUtentiFormazione, String stagione, int numeroGiornata) {
		List<ProbabiliFormazioni> toReturn = new ArrayList<ProbabiliFormazioni>();
		List<ProbabiliFormazioniWrap> ejbResp = probabiliFormazioniEJB.getRisultati(idUtentiFormazione, stagione, numeroGiornata);
		toReturn = unwrapEJBResp(ejbResp);
		if (toReturn != null && !toReturn.isEmpty()){
			int idGiornata = toReturn.get(0).getGiornate().getId();
			probabiliFormazioni.getId().setIdGiornate(idGiornata);
		}
		resultList = toReturn;
		return toReturn;
	}

	public ProbabiliFormazioni getProbabiliFormazioni() {
		return probabiliFormazioni;
	}

	public List<ProbabiliFormazioni> getProbFormazioniByGiornataUtentiFormazione(int idGiornata, int idUtentiFormazione) {
		List<ProbabiliFormazioni> toReturn = new ArrayList<ProbabiliFormazioni>();
		List<ProbabiliFormazioniWrap> ejbResp = probabiliFormazioniEJB.getProbFormazioniByGiornataUtentiFormazione(idGiornata, idUtentiFormazione);
		toReturn = unwrapEJBResp(ejbResp);
		return toReturn;
	}

	public void deleteProbFormazioniByGiornataUtentiFormazione(int idGiornata, int idUtentiFormazione) {
		probabiliFormazioniEJB.deleteProbFormazioniByGiornataUtentiFormazione(idGiornata, idUtentiFormazione);
	}
	
	public void deleteProbFormazioniByUtentiFormazione(int idUtentiFormazione) {
		probabiliFormazioniEJB.deleteProbFormazioniByUtentiFormazione(idUtentiFormazione);
	}

	public void insertProbFormazione(int idGiornata, int idUtentiFormazione, int idGiocatore, int probTit, int probPanc, String note) {
		probabiliFormazioniEJB.insertProbFormazione(idGiornata, idUtentiFormazione, idGiocatore, probTit, probPanc, note);
	}

	public List<ProbabiliFormazioni> getResultList() {
		return resultList;
	}

	public void setResultList(List<ProbabiliFormazioni> resultList) {
		this.resultList = resultList;
	}

	/**
	 * Per le probabilita' di fantagazzetta hanno valore 3
	 * 
	 * @param probabilita
	 * @return
	 */
	public boolean isFantaGazzettaSource(int probabilita) {
		return probabiliFormazioniEJB.isFantaGazzettaSource(probabilita);
	}

	/**
	 * Per le probabilita' di fantagazzetta hanno valore 5
	 * 
	 * @param probabilita
	 * @return
	 */
	public boolean isGazzettaSource(int probabilita) {
		return probabiliFormazioniEJB.isGazzettaSource(probabilita);
	}
	
	private List<ProbabiliFormazioni> unwrapEJBResp(List<ProbabiliFormazioniWrap> ejbResp){
		List<ProbabiliFormazioni> toReturn = new ArrayList<ProbabiliFormazioni>();
		for (ProbabiliFormazioniWrap current : ejbResp){
			toReturn.add(current.unwrap());
		}
		return toReturn;
	}
}
