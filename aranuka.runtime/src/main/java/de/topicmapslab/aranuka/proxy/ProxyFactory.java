/**
 * 
 */
package de.topicmapslab.aranuka.proxy;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;


/**
 * The Proxyfactory generates a proxy for methods adding a given interceptor which is called before the method is called.
 * 
 * @author Hannes Niederhausen
 *
 */
public class ProxyFactory {

	public static Object create(Class<?> clazz, IMethodInterceptor methodInterceptor) {
		try {
			String className = clazz.getName().replaceAll("\\.", "/");
			
			String qname = clazz.getName();
			String name = qname.substring(qname.lastIndexOf('.')+1);
			String targetName = "de/topicmapslab/aranuka/proxy/"+name+"_ARANUKA_PROXY";
			
			InputStream is = clazz.getClassLoader().getResourceAsStream(className+".class");
			ClassReader reader = new ClassReader(is);
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//			TraceClassVisitor tv = new TraceClassVisitor(writer, new PrintWriter(System.out));
//			CheckClassAdapter cca = new CheckClassAdapter(tv);
			
			ProxyAdapter pa = new ProxyAdapter(writer, className, targetName);
			reader.accept(pa, 0);
			
			is.close();
			
			FileOutputStream fis = new FileOutputStream("/tmp/test.class");
			fis.write(writer.toByteArray());
			fis.close();
			
			AranukaClassLoader classLoader = new AranukaClassLoader(clazz.getClassLoader());
			Class<?> result = classLoader.defineClass("de.topicmapslab.aranuka.proxy."+name+"_ARANUKA_PROXY", writer.toByteArray());

			Object obj = result.newInstance();
			
			Method m = result.getDeclaredMethod("setMethodInterceptor", IMethodInterceptor.class);
			m.setAccessible(true);
			m.invoke(obj, methodInterceptor);
			
			return obj;
			
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
}
