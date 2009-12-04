package de.topicmapslab.aranuka.codegen.core.layer.dao;

import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.codegen.core.cached.generated.GrandDuchy;
import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;
import de.topicmapslab.aranuka.codegen.layer.base.TopicMapDAO;

public class GrandDuchyTopicDAO extends TopicMapDAO<GrandDuchy> {

	public GrandDuchyTopicDAO(TopicMap topicMap)  throws TopicMap2JavaMapperException{
		super(topicMap);
	}

	@Override
	public String getType() {
		return "http://en.wikipedia.org/wiki/Grand_duchy";
	}

}
