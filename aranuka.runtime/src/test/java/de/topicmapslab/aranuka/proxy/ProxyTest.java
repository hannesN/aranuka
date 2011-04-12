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
package de.topicmapslab.aranuka.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

import de.topicmapslab.aranuka.proxy.model.Person;

/**
 * @author Hannes Niederhausen
 *
 */
public class ProxyTest {

	@Test
	public void testLoading() {
		Class<Person> pc = Person.class;
		String className = pc.getName().replaceAll("\\.", "/");
		System.out.println(className);
		assertEquals(className, "de/topicmapslab/aranuka/proxy/model/Person");
		InputStream is = pc.getClassLoader().getResourceAsStream(className+".class");
		assertNotNull(is);
	}
	
	
	@Test
	public void testCreate() {
		Object obj = ProxyFactory.create(Person.class, new IMethodInterceptor() {
			
			@Override
			public Object methodCalled(Object instance, String name, Object... param) {
				System.out.println("Method called: "+name);
				System.out.println("Instance: "+instance);
				System.out.printf("Params ("+param.length+"):");
				for (Object o : param) {
					System.out.println("\t"+o);
				}
				return null;
			}
		});
		
		assertNotNull(obj);
		assertTrue(obj instanceof Person);
		assertEquals(Person.class.getDeclaredMethods().length+1, obj.getClass().getDeclaredMethods().length);
		assertEquals(1, obj.getClass().getDeclaredFields().length);
		
		Person p=(Person) obj;
		
		// check String setter
		assertNull(p.getName());
		p.setName("Hannes");
		assertEquals("Hannes", p.getName());
		
		assertEquals(0, p.getAge());
		p.setAge(29);
		assertEquals(29, p.getAge());
		p.setDoubleValue(3.143);
		p.setLongValue(3143);
		p.hello("Hallo", 23, 3.145d, (byte) 34, (long)13242, 'd');
	}
}
