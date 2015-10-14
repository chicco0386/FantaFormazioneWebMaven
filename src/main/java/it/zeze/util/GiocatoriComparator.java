package it.zeze.util;

import it.zeze.fantaformazioneweb.entity.Giocatori;

import java.util.Comparator;

public class GiocatoriComparator implements Comparator<Giocatori> {

	public int compare(Giocatori o1, Giocatori o2) {
		String nomeG1 = o1.getNome();
		String nomeG2 = o2.getNome();
		String ruoloG1 = o1.getRuolo();
		String ruoloG2 = o2.getRuolo();
		return ruoloG1.compareToIgnoreCase(ruoloG2);
	}
}
