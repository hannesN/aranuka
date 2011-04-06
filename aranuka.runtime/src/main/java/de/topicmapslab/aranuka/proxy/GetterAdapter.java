/**
 * 
 */
package de.topicmapslab.aranuka.proxy;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

/**
 * Adapter to add code to the
 * 
 * @author Hannes Niederhausen
 * 
 */
public class GetterAdapter extends MethodAdapter {

	private Type returnType;
	private Type[] params;
	private String clazzName;

	private int localIdx;

	public GetterAdapter(MethodVisitor arg0, String desc, String clazzName, String methodName) {
		super(arg0);
		returnType = Type.getReturnType(desc);
		params = Type.getArgumentTypes(desc);
		localIdx = params.length + 1;
		this.clazzName = clazzName;
		System.out.println(methodName);
	}

	@Override
	public void visitCode() {
		super.visitCode();
		try {

			Label normalLabel = new Label();

			visitVarInsn(ALOAD, 0);
			visitVarInsn(ALOAD, 0);
			Type miType = Type.getType(IMethodInterceptor.class);

			Method methodCalled = Method.getMethod(IMethodInterceptor.class.getDeclaredMethod("methodCalled"));

			visitFieldInsn(GETFIELD, clazzName, "methodInterceptor",
					"Lde/topicmapslab/aranuka/proxy/IMethodInterceptor;");
			
//			for (int i = 0; i < localIdx-1; i++) {
//				visitVarInsn(params[i].getOpcode(ILOAD), i + 1);
//			}
			
			visitMethodInsn(INVOKEINTERFACE, miType.getInternalName(), methodCalled.getName(),
					methodCalled.getDescriptor());
			visitVarInsn(ASTORE, localIdx);

			visitVarInsn(ALOAD, localIdx);
			visitJumpInsn(IFNULL, normalLabel);

			if (returnType.getSort()==Type.OBJECT) {
				visitTypeInsn(CHECKCAST, returnType.getInternalName());
				visitVarInsn(ALOAD, 1);
				visitInsn(ARETURN);
			}

			visitLabel(normalLabel);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(4, maxLocals+1);
	}
}
