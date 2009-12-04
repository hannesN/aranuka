package de.topicmapslab.aranuka.codegen.properties;

import java.util.Properties;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;

import de.topicmapslab.aranuka.codegen.core.exception.InitializationException;
import de.topicmapslab.aranuka.codegen.model.databrdige.IDataBridge;

/**
 * 
 * @author Sven Krosse
 *
 */
public class PropertyLoader extends Properties {

	private static final long serialVersionUID = 1L;
	private static PropertyLoader propertyLoader;
	private final IDataBridge databridge;

	private final TopicMapSystem topicMapSystem;
	private final TopicMap topicMap;

	public static final PropertyLoader initialize(
			final TopicMapSystem topicMapSystem, final TopicMap topicMap)
			throws InitializationException {
		if (propertyLoader != null) {
			throw new InitializationException("Property loader is already initilazied.");
		}
		propertyLoader = new PropertyLoader(topicMapSystem, topicMap);
		return propertyLoader;
	}
	
	public static final PropertyLoader getInstance()
			throws InitializationException {
		if (propertyLoader == null) {
			throw new InitializationException("Property loader is not initilazied.");
		}
		return propertyLoader;
	}

	private PropertyLoader(final TopicMapSystem topicMapSystem,
			final TopicMap topicMap) throws InitializationException {
		try {
			load(getClass().getResourceAsStream("codegen.properties"));
			this.topicMapSystem = topicMapSystem;
			this.topicMap = topicMap;

			this.databridge = (IDataBridge) Class
					.forName(
							getProperty(PropertyConstants.PROPERTY_IDATABRIDGE_IMPLEMENTAION))
					.getConstructor(PropertyLoader.class).newInstance(this);

		} catch (Exception ex) {
			throw new InitializationException("Cannot load orm.properties");
		}
	}

	public IDataBridge getDataBridge() {
		return databridge;
	}

	public TopicMap getTopicMap() {
		return topicMap;
	}

	public TopicMapSystem getTopicMapSystem() {
		return topicMapSystem;
	}
	
	
}
