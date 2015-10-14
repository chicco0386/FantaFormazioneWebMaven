package it.zeze.fantaformazioneweb.entity.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.Name;

@Name("parametriAuthFG")
public class ParametriAuthFG implements Serializable {

	private static final long serialVersionUID = 4915516688565618664L;

	private String userName = "";
	private String password = "";
	private String tipologiaFG = "";

	public List<SelectItem> comboBoxTipiFG() {
		List<SelectItem> toReturn = new ArrayList<SelectItem>();
		toReturn.add(new SelectItem("FG", "FantaGazzetta"));
		toReturn.add(new SelectItem("FG_Sisal", "FantaGazzetta Sisal"));
		return toReturn;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTipologiaFG() {
		return tipologiaFG;
	}

	public void setTipologiaFG(String tipologiaFG) {
		this.tipologiaFG = tipologiaFG;
	}

}
