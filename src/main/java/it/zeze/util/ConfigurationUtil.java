package it.zeze.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.jboss.seam.annotations.Name;

@Name("configurationUtil")
public class ConfigurationUtil {

	private static PropertiesConfiguration propertiesConfiguration;

	public static void initializeConfiguration(String fileConfName) throws ConfigurationException {
		propertiesConfiguration = new PropertiesConfiguration(fileConfName);
		propertiesConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
		propertiesConfiguration.load();
	}

	public static void clearConfiguration() {
		propertiesConfiguration.clear();
	}

	public static String getValue(String key) {
		return propertiesConfiguration.getString(key);
	}

	public static String[] getValue(String key, char delimiter) {
		propertiesConfiguration.setListDelimiter(delimiter);
		return propertiesConfiguration.getStringArray(key);
	}

}
