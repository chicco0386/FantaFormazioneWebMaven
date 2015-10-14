package it.zeze.servlet;

import it.zeze.util.ConfigurationUtil;

import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

@Name("fileConfigurationServlet")
public class FileConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = -219613647151336985L;

	@Logger
	static Log log;

	private static final String NOME_FILE_PROPS = "application.properties";

	@Override
	public void init() {
		String tipoConfigurazione = getServletConfig().getInitParameter("tipoConfigurazione");
		log.info("Inizializzo il file di properties [" + NOME_FILE_PROPS + "] per tipo [" + tipoConfigurazione + "]");
		try {
			ConfigurationUtil.initializeConfiguration(tipoConfigurazione + "." + NOME_FILE_PROPS);
		} catch (ConfigurationException e) {
			log.error("errore durante l'inizializzazione del file di properties [" + tipoConfigurazione + "." + NOME_FILE_PROPS + "]", e);
		}
	}

	@Override
	public void destroy() {
		log.info("Chiudo il file di properties");
		ConfigurationUtil.clearConfiguration();
	}

}
