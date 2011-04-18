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
package de.topicmapslab.aranuka.proxy.model;


public class Person {

	private String name;
	
	private int age;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public void setDoubleValue(double value) {
		System.out.println("Double value set to: "+value);
	}
	
	public void hello(String test, int age, double dnum, byte bnum, long lNum, char character) {
		System.out.println("Hallo: "+test+"; I'm "+age+" years old; numbers are: "+dnum+";"+bnum+";"+lNum+";"+character);
	}

	/**
	 * @param i
	 */
	public void setLongValue(long value) {
		System.out.println("Long value set to: "+value);
	}
}
