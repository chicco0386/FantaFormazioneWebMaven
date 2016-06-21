package it.zeze.fantaformazioneweb.session;

import java.util.Arrays;

import javax.naming.NamingException;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fanta.ejb.util.JNDIUtils;
import it.zeze.fanta.service.definition.ejb.proxy.seam.SquadreSeamRemote;
import it.zeze.fantaformazioneweb.entity.Squadre;

@Name("squadreList")
public class SquadreList extends EntityQuery<Squadre> {

	private static final long serialVersionUID = -6400129066025374893L;

	@Logger
	static Log log;

	private static final String EJBQL = "select squadre from Squadre squadre";	

	private static final String[] RESTRICTIONS = { "lower(squadre.nome) like lower(concat(#{squadreList.squadre.nome},'%'))", };
	
	private Squadre squadre = new Squadre();
	
	public Squadre getSquadre() {
		return squadre;
	}
	
	private static SquadreSeamRemote squadreEJB;
		
	static {
		try {
			squadreEJB = JNDIUtils.getSquadreEJB();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SquadreList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrderColumn("squadre.nome");
		setOrderDirection("asc");
		// setMaxResults(25);
	}

	public void unmarshallAndSaveFromHtmlFile() {
		squadreEJB.unmarshallAndSaveFromHtmlFile();
	}

	public void initMappaSquadre() {
		squadreEJB.initMappaSquadre();
	}

	public Squadre getSquadraFromMapByNome(String nomeSquadraToSearch) {
		return squadreEJB.getSquadraFromMapByNome(nomeSquadraToSearch);
	}

	public Squadre getSquadraByNome(String nomeSquadraToSearch) {
		return squadreEJB.getSquadraByNome(nomeSquadraToSearch);
	}
	
	public Squadre getSquadraById(int idSquadra) {
		return squadreEJB.getSquadraById(idSquadra);
	}

}
