package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.annotations.IDTYPE;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

public class IdBinding extends AbstractFieldBinding {

	private static Logger logger = LoggerFactory.getLogger(TopicBinding.class);
	
	private IDTYPE idtype;
	
	public IdBinding(TopicBinding parent) {
		super(parent);
	}

	public void persist(Topic topic, Object topicObject, Map<String,String> prefixMap) throws BadAnnotationException{
		
		String baseLocator = TopicMapsUtils.resolveURI("base_locator:", prefixMap) + topicObject.getClass().getName().replaceAll("\\.", "/") + "/";
						
		// create identifier dependent of field type
		
		if(this.isArray())
			
			for (Object obj:(Object[])this.getValue(topicObject))
				addIdentifier(topic, obj, baseLocator);
			
		else if(this.isCollection())
				
			for (Object obj:(Collection<Object>)this.getValue(topicObject))
				addIdentifier(topic, obj, baseLocator);

		else 
			if(this.getValue(topicObject) != null)
				addIdentifier(topic, this.getValue(topicObject), baseLocator);
	}
	
	private void addIdentifier(Topic topic, Object obj, String baseLocator) throws BadAnnotationException{

		String locator = null;
		if (obj instanceof String)
			locator = obj.toString();
		else
			locator = baseLocator + obj.toString();

		logger.info("Create identifier " + locator);
		
		if(this.getIdtype() == IDTYPE.ITEM_IDENTIFIER)
			topic.addItemIdentifier(topic.getTopicMap().createLocator(locator));
		
		else if(this.getIdtype() == IDTYPE.SUBJECT_IDENTIFIER)
			topic.addSubjectIdentifier(topic.getTopicMap().createLocator(locator));
		
		else if(this.getIdtype() == IDTYPE.SUBJECT_LOCATOR)
			topic.addSubjectLocator(topic.getTopicMap().createLocator(locator));
		
		else
			throw new BadAnnotationException("Unkonwn IDTYPE.");
	}

	
	// getter and setter
	
	public void setIdtype(IDTYPE idtype) {
		this.idtype = idtype;
	}
	
	public IDTYPE getIdtype() {
		return idtype;
	}

	@Override
	public String toString() {
		return "IdBinding [idtype=" + idtype + "]";
	}

}
