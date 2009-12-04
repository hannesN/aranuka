package de.topicmapslab.aranuka.codegen.core.layer.dao;

import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.codegen.core.cached.generated.City;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.layer.base.TopicMapDAO;

public class CityTopicDAO extends TopicMapDAO<City> {

	public CityTopicDAO(TopicMap topicMap) throws TopicMap2JavaMapperException{
		super(topicMap);
	}

	@Override
	public String getType() {
		return "http://en.wikipedia.org/wiki/City";
	}

}
