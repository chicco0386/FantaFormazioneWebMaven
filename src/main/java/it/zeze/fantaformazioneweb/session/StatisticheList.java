package it.zeze.fantaformazioneweb.session;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.StatisticheSeamRemote;
import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.Giornate;
import it.zeze.fantaformazioneweb.entity.Statistiche;
import it.zeze.fantaformazioneweb.entity.StatisticheId;
import it.zeze.fantaformazioneweb.entity.wrapper.StatisticheWrap;

@Name("statisticheList")
public class StatisticheList extends EntityQuery<Statistiche> {

	private static final long serialVersionUID = 1685766221228868935L;

	@Logger
	static Log log;

	@In(create = true)
	GiornateList giornateList;

	@In(create = true)
	GiocatoriList giocatoriList;

	@In(create = true)
	StatisticheHome statisticheHome;

	@In(create = true)
	SessionInfo sessionInfo;

	private static final String EJBQL = "select statistiche from Statistiche statistiche";

	private Statistiche statistiche = new Statistiche();
	private Giocatori giocatori = new Giocatori();
	private Giornate giornate = new Giornate();

	private List<Statistiche> resultList = new ArrayList<Statistiche>();
	private List<Statistiche> resumeStatistiche = new ArrayList<Statistiche>();
	
	private static StatisticheSeamRemote statisticheEJB;
	
	static {
		try {
			statisticheEJB = JNDIUtils.getStatisticheEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initResultList() {
		List<StatisticheWrap> ejbResp = statisticheEJB.initResultList(giornate, giocatori, getOrderColumn(), getOrderDirection());
		this.resultList = unwrapEjbResp(ejbResp);
	}

	@Override
	public List<Statistiche> getResultList() {
		return this.resultList;
	}

	public void resetResumeStatistiche() {
		List<StatisticheWrap> ejbResp = statisticheEJB.resetResumeStatistiche(resultList, giornate, giocatori, getOrderColumn(), getOrderDirection());
		this.resumeStatistiche = unwrapEjbResp(ejbResp);
	}

	public List<Statistiche> getResumeStatistiche() {
		return this.resumeStatistiche;
	}

	public StatisticheList() {
		statistiche = new Statistiche();
		statistiche.setId(new StatisticheId());
		setEjbql(EJBQL);
	}

	public Statistiche getStatistiche() {
		return statistiche;
	}

	public Giocatori getGiocatori() {
		return giocatori;
	}

	public void setGiocatori(Giocatori giocatori) {
		this.giocatori = giocatori;
	}

	public Giornate getGiornate() {
		return giornate;
	}

	public void setGiornate(Giornate giornate) {
		this.giornate = giornate;
	}

	public void unmarshallAndSaveFromHtmlFile() {
		String stagione = sessionInfo.getStagione();
		statisticheEJB.unmarshallAndSaveFromHtmlFile(stagione);
	}

	public Statistiche getStatisticheIdGiocatoreIdGiornata(int idGiocatore, int idGiornata) {
		StatisticheWrap ejbResponse = statisticheEJB.getStatisticheIdGiocatoreIdGiornata(idGiocatore, idGiornata); 
		return ejbResponse.unwarp();
	}

	public void setLastStagione() {
		Giornate lastGiornata = giornateList.getLastGiornata();
		if (lastGiornata != null) {
			if (this.giornate.getStagione() == null || this.giornate.getStagione().isEmpty()) {
				this.giornate.setStagione(lastGiornata.getStagione());
			}
		}
	}
	
	private List<Statistiche> unwrapEjbResp(List<StatisticheWrap> ejbResp){
		List<Statistiche> toReturn = new ArrayList<Statistiche>();
		for (StatisticheWrap current : ejbResp){
			toReturn.add(current.unwarp());
		}
		return toReturn;
	}
}
