/*******************************************************************************
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.aranuka.osgi;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import de.topicmapslab.aranuka.connectors.IEngineConnector;

/**
 * @author Hannes Niederhausen
 *
 */
public class RuntimeActivator implements BundleActivator, BundleListener {

	private static RuntimeActivator plugin = null;
	private BundleContext context = null;
	
	// caching bundles2classname
	Map<Bundle, String> bundleMap;
	
	// caching classname2implementation
	Map<String, IEngineConnector> engineMap;
	
	public void start(BundleContext context) throws Exception {
		RuntimeActivator.plugin = this;
		this.context = context;
		context.addBundleListener(this);
	}

	public void stop(BundleContext context) throws Exception {
		RuntimeActivator.plugin = null;
		this.context = null;
		context.removeBundleListener(this);
	}
	
	public static RuntimeActivator getDefault() {
		return plugin;
	}

	public IEngineConnector getConnector(String name) {
		if (bundleMap==null)
			init();
		
		return engineMap.get(name);
	}

	private void init() {
		try {
			bundleMap = new HashMap<Bundle, String>();
			engineMap = new HashMap<String, IEngineConnector>();
			// Warning right now only one implementation per bundle possible - fix this some time
			ServiceReference[] servRefs = context.getAllServiceReferences(IEngineConnector.class.getName(), null);
			for (ServiceReference sr : servRefs) {
				IEngineConnector tmp = (IEngineConnector) context.getService(sr);
				String className = tmp.getClass().getName();
				bundleMap.put(sr.getBundle(), className);
				engineMap.put(className, tmp);
			}
			
		} catch (InvalidSyntaxException e) {
		}
	}
	
	public void bundleChanged(BundleEvent event) {
		if (event.getType()==Bundle.STARTING) {
			System.out.println("Starting: "+event.getBundle());
			for (ServiceReference sr : event.getBundle().getRegisteredServices()) {
				System.out.println(sr);
				// TODO check service properties to filter for IEngineConnector services
			}
			return;
		}
		
		
		if (event.getType()==Bundle.STOPPING) {
			String className = bundleMap.get(event.getBundle());
			if (className!=null) {
				bundleMap.remove(event.getBundle());
				engineMap.remove(className);
			}
		}
	}
}
