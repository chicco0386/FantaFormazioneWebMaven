package it.zeze.fantaformazioneweb.session;

import java.util.List;

import javax.faces.model.SelectItem;

import it.zeze.fantaformazioneweb.bean.util.ComboBoxUtil;
import it.zeze.fantaformazioneweb.entity.Utenti;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("sessionInfo")
@Scope(ScopeType.SESSION)
public class SessionInfo {

	private String stagione = null;
	private Utenti authUser = null;

	@In(create = true)
	ComboBoxUtil comboBoxUtil;

	public Utenti getAuthUser() {
		return authUser;
	}

	public void setAuthUser(Utenti authUser) {
		this.authUser = authUser;
	}

	public String getStagione() {
		return stagione;
	}

	public void setStagione(String stagione) {
		this.stagione = stagione;
	}

	public void initStagione() {
		List<SelectItem> listStagioni = comboBoxUtil.stagioniStatistiche();
		if (!listStagioni.isEmpty()) {
			stagione = listStagioni.get(0).getLabel();
		}
	}
}
