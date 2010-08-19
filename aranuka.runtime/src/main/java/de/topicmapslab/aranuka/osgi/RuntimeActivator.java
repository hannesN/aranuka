/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
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
