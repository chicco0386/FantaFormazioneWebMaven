package it.zeze.fantaformazioneweb.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.bean.FormazioneBeanCommon;
import it.zeze.fanta.service.bean.GiocatoriMercato;
import it.zeze.fanta.service.definition.ejb.FormazioneMercatoRemote;
import it.zeze.fantaformazioneweb.entity.Giocatori;
import it.zeze.fantaformazioneweb.entity.wrapper.GiocatoriWrap;

@Name("formazioneBean")
@Scope(ScopeType.PAGE)
public class FormazioneBean {

	@Logger
	static Log log;
	
	private static FormazioneMercatoRemote formazioneMercatoBean;
	
	static {
		try {
			formazioneMercatoBean = JNDIUtils.getFormazioniMercatoEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@DataModel
	private List<Giocatori> listaGiocatori = new ArrayList<Giocatori>();
	
	@DataModel
	private List<GiocatoriMercato> listaGiocatoriMercato = new ArrayList<GiocatoriMercato>();

	private FormazioneBeanCommon common = new FormazioneBeanCommon();

	public void initListaGiocatoriMercato(int idUtente) {
		common = formazioneMercatoBean.initListaGiocatoriMercato(common, idUtente);
		listaGiocatoriMercato = common.getListaGiocatoriMercato();
	}

	public void initListaGiocatori(int idUtente) {
		common = formazioneMercatoBean.initListaGiocatori(common, idUtente);
		listaGiocatori = common.convertListaGiocatori();
	}
	
	public void initCreditiResidui(int idUtentiFormazioni, int idUtente) {
		common = formazioneMercatoBean.initCreditiResidui(common, idUtentiFormazioni, idUtente);
	}

	public void addGiocatoreMercato(Giocatori giocatoreToInsert, BigDecimal prezzoAcquisto) {
		common.addGiocatoreMercato(new GiocatoriWrap(giocatoreToInsert), prezzoAcquisto);
		listaGiocatoriMercato = common.getListaGiocatoriMercato();
	}

	public void add(Giocatori giocatoreToInsert) {
		common.add(new GiocatoriWrap(giocatoreToInsert));
		listaGiocatori = common.convertListaGiocatori();
	}

	public List<Giocatori> getListaGiocatori() {
		return listaGiocatori;
	}

	public void setListaGiocatori(List<Giocatori> listaGiocatori) {
		this.listaGiocatori = listaGiocatori;
	}

	public List<GiocatoriMercato> getListaGiocatoriMercato() {
		return listaGiocatoriMercato;
	}

	public void setListaGiocatoriMercato(List<GiocatoriMercato> listaGiocatoriMercato) {
		this.listaGiocatoriMercato = listaGiocatoriMercato;
	}

	public void removeMercato(GiocatoriMercato giocatoreToRemove, BigDecimal prezzo) {
		common.removeMercato(giocatoreToRemove, prezzo);
		listaGiocatoriMercato = common.getListaGiocatoriMercato();
	}

	public void remove(Giocatori giocatoreToRemove) {
		common.remove(new GiocatoriWrap(giocatoreToRemove));
		listaGiocatori = common.convertListaGiocatori();
	}

	public FormazioneBeanCommon getCommon() {
		return common;
	}

	public void setCommon(FormazioneBeanCommon common) {
		this.common = common;
	}
}
