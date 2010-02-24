package de.topicmapslab.aranuka.engine;

import java.io.File;
import java.io.FileOutputStream;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.XTM20TopicMapReader;
import org.tmapix.io.XTM20TopicMapWriter;

public class OntopiaMemoryDriver implements IEngineDriver {

	private String filename;
	private String baseLocator;
	
	//private TopicMapIF topicMap;
	private TopicMap tmapiTopicMap;
	
	
	// interface methods
	
	public TopicMap getTopicMap() {
	
		if(this.tmapiTopicMap != null)
			return tmapiTopicMap;
		
		try{
			
			File f = new File(this.filename);
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			TopicMapSystem sys = factory.newTopicMapSystem();
			this.tmapiTopicMap = sys.createTopicMap(sys.createLocator(this.baseLocator));
			
			if(f.exists()){
				
				// load
				XTM20TopicMapReader reader = new XTM20TopicMapReader(this.tmapiTopicMap, f);
				reader.read();
			}
			
			return this.tmapiTopicMap;


		}catch(Exception e){
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean flushTopicMap() {

		if(this.tmapiTopicMap == null)
			return false;
		
		File f = new File(this.filename);

		try{
			
			XTM20TopicMapWriter writer = new XTM20TopicMapWriter(new FileOutputStream(f), this.baseLocator);
			writer.write(this.tmapiTopicMap);
			
		}catch(Exception e){

			e.printStackTrace();
			return false;
		}

		return true;
		
	}
	
	// other methods
		
	public OntopiaMemoryDriver(String baseLocator, String filename) {

		if(filename == null)
			throw new RuntimeException("The filename must not be null.");

		if(baseLocator == null)
			throw new RuntimeException("The base locator must not be null.");
		
		this.filename = filename;
		this.baseLocator = baseLocator;
		
	}
	
	
}
