package de.topicmapslab.aranuka.codegen.core.layer.dao;

import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.codegen.core.cached.generated.Country;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.layer.base.TopicMapDAO;

public class CountryTopicDAO extends TopicMapDAO<Country> {

	public CountryTopicDAO(TopicMap topicMap) throws TopicMap2JavaMapperException {
		super(topicMap);
	}

	@Override
	public String getType() {
		return "http://en.wikipedia.org/wiki/Country";
	}

}
