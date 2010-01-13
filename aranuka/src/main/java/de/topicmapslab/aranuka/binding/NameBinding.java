package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;


public class NameBinding extends AbstractFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(NameBinding.class);
	
	private String nameType;
	
	public NameBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}
	
	
	@SuppressWarnings("unchecked")
	public void persist(Topic topic, Object topicObject){
		
		if(this.isArray())
			
			for (Object obj:(Object[])this.getValue(topicObject))
				createName(topic, obj.toString());
			
		else if(this.isCollection())
				
			for (Object obj:(Collection<Object>)this.getValue(topicObject))
				createName(topic, obj.toString());

		else 

			if(this.getValue(topicObject) != null)
				createName(topic, this.getValue(topicObject).toString());
	}
	
	private void createName(Topic topic, String name){

		logger.info("Add name '" + name + "'.");
		
		Name n = topic.createName(name, getScope(topic.getTopicMap()));
		n.setType(topic.getTopicMap().createTopicBySubjectIdentifier(topic.getTopicMap().createLocator(this.nameType)));
		
	}
	
	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameIdentifier) {
		this.nameType = nameIdentifier;
	}

	@Override
	public String toString() {
		return "NameBinding [nameType=" + nameType + "] " + super.toString();
	}
	
}


