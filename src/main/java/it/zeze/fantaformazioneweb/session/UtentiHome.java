package it.zeze.fantaformazioneweb.session;

import it.zeze.fantaformazioneweb.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("utentiHome")
public class UtentiHome extends EntityHome<Utenti> {

	public void setUtentiId(Integer id) {
		setId(id);
	}

	public Integer getUtentiId() {
		return (Integer) getId();
	}

	@Override
	protected Utenti createInstance() {
		Utenti utenti = new Utenti();
		return utenti;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Utenti getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<UtentiFormazioni> getUtentiFormazionis() {
		return getInstance() == null ? null : new ArrayList<UtentiFormazioni>(
				getInstance().getUtentiFormazionis());
	}

	public boolean controllaUguaglianzaPassword() {
		boolean ok = true;
		String password = this.instance.getPassword();
		String passwordRepeat = this.instance.getPasswordRepeat();
		if ((password == null && passwordRepeat == null)
				|| (password.isEmpty() && passwordRepeat.isEmpty())
				|| !password.equals(passwordRepeat)) {
			StatusMessages.instance().add(Severity.ERROR,
					"Le password non corrispondo");
			ok = false;
		}
		return ok;
	}

	public boolean salvaUtente() {
		boolean saved = false;
		if (this.instance.getUsername() != null
				&& !this.instance.getUsername().isEmpty()) {
			if (controllaUguaglianzaPassword()) {
				if (this.instance.getMail() != null
						&& !this.instance.getMail().isEmpty()) {
					this.instance.setDataRegistrazione(new Date(System
							.currentTimeMillis()));
					try {
						this.persist();
						saved = true;
					} catch (Exception e) {
					}
				}
			}
		}
		return saved;
	}
}
