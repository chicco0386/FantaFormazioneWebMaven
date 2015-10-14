package it.zeze.fantaformazioneweb.session;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import it.zeze.fantaformazioneweb.entity.*;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

@Name("utentiList")
public class UtentiList extends EntityQuery<Utenti> {

	private static final long serialVersionUID = -3927090848753528506L;

	@Logger
	private Log log;

	private static final String EJBQL = "select utenti from Utenti utenti";
	private static final String SELECT_BY_USERNAME_PASS = "select utenti from Utenti utenti where utenti.username=:username and utenti.password=:pass";

	private static final String[] RESTRICTIONS = { "lower(utenti.username) like lower(concat(#{utentiList.utenti.username},'%'))", "lower(utenti.password) like lower(concat(#{utentiList.utenti.password},'%'))", };

	private Utenti utenti = new Utenti();

	public UtentiList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
//		setMaxResults(25);
	}

	public Utenti getUtenti() {
		return utenti;
	}

	public Utenti selectUtentiByUsernamePass(String username, String pass) {
		Utenti utenteToReturn = null;
		Query query = getEntityManager().createQuery(SELECT_BY_USERNAME_PASS);
		query.setParameter("username", username);
		query.setParameter("pass", pass);
		List<Utenti> resultSet = query.getResultList();
		if (resultSet != null && !resultSet.isEmpty()) {
			utenteToReturn = resultSet.get(0);
			log.info("Utente [" + username + "] trovato.");
		} else {
			log.info("Utente [" + username + "] NON trovato.");
		}
		return utenteToReturn;
	}
}
