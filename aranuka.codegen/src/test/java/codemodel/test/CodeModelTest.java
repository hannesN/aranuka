/**
 * 
 */
package codemodel.test;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

/**
 * @author niederhausen
 *
 */
public class CodeModelTest extends AbstractGenerationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CodeModelTest().test();

	}

	
	private void test() {
		JCodeModel cm = new JCodeModel();
		
		try {
			String packName = "de.topicmapslab.aranuka.test";
			String className = "Hello";
			
			
			
			JPackage p = cm._package(packName);
			
			JClass a = cm.ref("de.topicmapslab.aranuka.annotations.Topic");
			
			JDefinedClass c = p._getClass(className); 
			if (c==null) {
				c = p._class(className);
				JAnnotationUse use = c.annotate(a);
				use.param("type", packName.replaceAll("\\.", "/")+"/"+className);
				
				JFieldVar var = c.field(JMod.PRIVATE, boolean.class, "dead");
				
				c.method(JMod.PUBLIC, boolean.class, "isDead").body()._return(var);
				
				JMethod method = c.method(JMod.PUBLIC, void.class, "setDead");
				JVar param = method.param(boolean.class, "dead");
				method.body().directStatement("this."+var.name()+" = "+param.name()+";");
				
				
				
				
			}
			
			
//			c._("Topic");
			
			cm.build(getDir());
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	
}
