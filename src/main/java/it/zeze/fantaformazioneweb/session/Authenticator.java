package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.Utenti;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator {
	@Logger
	private Log log;

	@In
	Identity identity;
	@In
	Credentials credentials;

	@In(create = true)
	UtentiList utentiList;

	@In(create = true)
	@Out
	SessionInfo sessionInfo;

	public boolean authenticate() {
		String username = credentials.getUsername();
		String pass = credentials.getPassword();
		log.info("authenticating {0}", username);
		Utenti authUser = utentiList.selectUtentiByUsernamePass(username, pass);
		if (authUser != null) {
			log.info("Utente [" + username + "] autenticato.");
			// Setto le informazioni dell'utente in sessione
			sessionInfo.setAuthUser(authUser);
			return true;
		} else {
			log.info("Utente [" + username + "] NON autenticato.");
			return false;
		}
		// if ("admin".equals(credentials.getUsername())) {
		// identity.addRole("admin");
		// return true;
		// }
	}
}
