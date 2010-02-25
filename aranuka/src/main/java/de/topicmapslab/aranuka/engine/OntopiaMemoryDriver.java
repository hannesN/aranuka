package de.topicmapslab.aranuka.engine;

import java.io.File;
import java.io.FileOutputStream;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.XTM20TopicMapReader;
import org.tmapix.io.XTM20TopicMapWriter;

public class OntopiaMemoryDriver extends AbstractEngineDriver{

	public static final String FILENAME = "filename";
	public static final String BASE_LOCATOR = "base_locator";

	private TopicMap topicMap;
	
	// interface methods
	
	public TopicMap getTopicMap() {
	
		if(this.topicMap != null)
			return topicMap;
		
		if(getProperty(BASE_LOCATOR) == null)
			throw new RuntimeException("Base locator property not specified.");
		
		try{
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			TopicMapSystem sys = factory.newTopicMapSystem();
			this.topicMap = sys.createTopicMap(sys.createLocator(getProperty(BASE_LOCATOR)));
			
			if(getProperty(FILENAME) != null){
			
				File f = new File(getProperty(FILENAME));
				
				if(f.exists()){
					
					// load
					XTM20TopicMapReader reader = new XTM20TopicMapReader(this.topicMap, f);
					reader.read();
				}
			}
			
			return this.topicMap;


		}catch(Exception e){
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean flushTopicMap() {

		if(this.topicMap == null)
			return false;
		
		if(getProperty(FILENAME) == null)
			throw new RuntimeException("Filename property not specified.");
		
		File f = new File(getProperty(FILENAME));

		try{
			
			XTM20TopicMapWriter writer = new XTM20TopicMapWriter(new FileOutputStream(f), getProperty(BASE_LOCATOR));
			writer.write(this.topicMap);
			
		}catch(Exception e){

			e.printStackTrace();
			return false;
		}

		return true;
		
	}

	
}
