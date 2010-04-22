package de.topicmapslab.aranuka.ontopiy.memory.connectors;

import java.io.File;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapSystemIF;
import net.ontopia.topicmaps.utils.ctm.CTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTM2TopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.connectors.AbstractEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;

public class OntopiaMemoryConnector extends AbstractEngineConnector{



	private TopicMap topicMap;
	private TopicMapSystem topicMapSystem;
	
	// interface methods
	
	public TopicMap getTopicMap() {
	
		if(this.topicMap != null)
			return topicMap;
		
		if(getProperty(IProperties.BASE_LOCATOR) == null)
			throw new RuntimeException("Base locator property not specified.");
		
		try{
			
			TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
			topicMapSystem = factory.newTopicMapSystem();
			this.topicMap = topicMapSystem.createTopicMap(topicMapSystem.createLocator(getProperty(IProperties.BASE_LOCATOR)));
			
			String filename = getProperty(IProperties.FILENAME);
			if(filename != null){
			
				File f = new File(filename);
				
				if(f.exists()){
					
					// load
					if (filename.endsWith(".xtm")) {
						XTMTopicMapReader  reader = new XTMTopicMapReader(f);
						TopicMapIF tm = reader.read();
						this.topicMap = new TopicMapImpl((TopicMapSystemIF) topicMapSystem, tm.getStore());
					} else if (filename.endsWith(".ctm")) {
						CTMTopicMapReader reader = new CTMTopicMapReader(f);
						TopicMapIF tm = reader.read();
						this.topicMap = new TopicMapImpl((TopicMapSystemIF) topicMapSystem, tm.getStore());
					}
				}
			}
			
			return this.topicMap;


		}catch(Exception e){
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	public TopicMapSystem getTopicMapSystem() {
		return topicMapSystem;
	}
	
	public boolean flushTopicMap() {

		if(this.topicMap == null)
			return false;
		
		String filename = getProperty(IProperties.FILENAME);
		if(filename == null)
			throw new RuntimeException("Filename property not specified.");
		
		File f = new File(filename);

		try{
			
			if (filename.endsWith(".xtm")) {
				XTM2TopicMapWriter writer = new XTM2TopicMapWriter(f);
				writer.write(((TopicMapImpl) topicMap).getWrapped());
			} else if (filename.endsWith(".ctm")) {
				// TODO add CTMWriter
//				CTMTopicMapWriter writer = new CTMTopicMapWriter(new FileOutputStream(f), topicMap.getBaseLocator());
//				writer.write(((TopicMapImpl) topicMap).getWrapped());
			} 
			
			
		}catch(Exception e){

			e.printStackTrace();
			return false;
		}

		return true;
		
	}

	
}
