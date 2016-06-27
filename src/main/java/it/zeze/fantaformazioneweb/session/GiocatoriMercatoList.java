package it.zeze.fantaformazioneweb.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.service.bean.GiocatoriMercato;
import it.zeze.fantaformazioneweb.entity.Giocatori;

@Name("giocatoriMercatoList")
public class GiocatoriMercatoList extends EntityQuery<GiocatoriMercato> {
	
	private static final String EJBQL = "select giocatori from Giocatori giocatori";
	private static final String[] RESTRICTIONS = { "lower(giocatori.nome) like lower(concat('%', concat(#{giocatoriMercatoList.giocatori.nome},'%')))", "lower(giocatori.ruolo) like lower(concat('%', concat(#{giocatoriMercatoList.giocatori.ruolo},'%')))", "lower(giocatori.squadre.nome) like lower(concat('%', concat(#{giocatoriMercatoList.giocatori.squadre.nome},'%')))", };

	@Logger
	static Log log;

	@In(create = true)
	GiocatoriList giocatoriList;
	
	private Giocatori giocatori = new Giocatori();

	public GiocatoriMercatoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		// setMaxResults(25);
	}

	@Override
	public List<GiocatoriMercato> getResultList() {
		List<GiocatoriMercato> toReturn = new ArrayList<GiocatoriMercato>();
		List<Giocatori> resultGiocatori = giocatoriList.getResultList();
		for (Giocatori currentGiocatore : resultGiocatori) {
			GiocatoriMercato toInsert = new GiocatoriMercato(currentGiocatore);
			toReturn.add(toInsert);
		}
		return toReturn;
	}
	
	public Giocatori getGiocatori() {
		return giocatori;
	}

}
