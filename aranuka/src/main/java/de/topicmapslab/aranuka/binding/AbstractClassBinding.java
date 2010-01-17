package de.topicmapslab.aranuka.binding;

import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.Construct;

public class AbstractClassBinding{
	
private static Set<Construct> updated;
	
	protected static void setToUpdated(Construct topicMapsConstruct){
		
		if(updated == null)
			updated = new HashSet<Construct>();
		
		updated.add(topicMapsConstruct);
		
	}
	
	protected boolean isUpdated(Construct topicMapsConstruct){
		
		if(updated == null)
			return false;
		
		return updated.contains(topicMapsConstruct);
		
	}
	
	public void clearUpdatedObjects(){
		
		updated.clear();
		
	}
	
}
