/**
 * 
 */
package de.topicmapslab.aranuka.proxy;

/**
 * Classloader which has a public defineClass method.
 * 
 * @author Hannes Niederhausen
 *
 */
public class AranukaClassLoader extends ClassLoader {

	
	
	public AranukaClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * Defines a class 
	 * 
	 * @param name the name of the class
	 * @param b the byte array for the class
	 * @return
	 */
	public Class<?> defineClass(String name, byte[] b) {
		return defineClass(name, b, 0, b.length);
	}
}
