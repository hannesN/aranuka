/**
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
 */
package de.topicmapslab.aranuka.tinytim.test;

import java.util.Properties;

import org.junit.BeforeClass;

import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.test.AranukaTestSuite;

/**
 * @author Hannes Niederhausen
 *
 */
public class TinyTiMMemoryTest extends AranukaTestSuite {

	@BeforeClass
	public static void setUp() {
		Properties properties = new Properties();
		properties.setProperty(IProperties.CONNECTOR_CLASS, "de.topicmapslab.aranuka.tinytim.connectors.TinyTiMConnector");
		properties.setProperty(IProperties.BACKEND, "memory");
		setProperties(properties);
	}
	
}
