/**
 * 
 */
package de.topicmapslab.aranuka.proxy;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.IRETURN;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

/**
 * Adapter to add code to the POJOs for lazy loading of getter values
 * 
 * @author Hannes Niederhausen
 * 
 */
public class GetterAdapter extends MethodAdapter {

	private Type returnType;
	private Type[] params;
	private String clazzName;

	private int localIdx;
	private String methodName;
	private String origClass;
	private String desc;

	public GetterAdapter(MethodVisitor arg0, String desc, String clazzName, String methodName, String origClass) {
		super(arg0);
		returnType = Type.getReturnType(desc);
		params = Type.getArgumentTypes(desc);
		this.clazzName = clazzName;
		this.origClass = origClass;
		this.methodName = methodName;
		this.desc = desc;
		this.localIdx = 0;
	}

	@Override
	public void visitCode() {
		super.visitCode();
		
		for (Type t : params) {
			this.localIdx+=t.getSize();
		}
		this.localIdx++;
		int localIdx = this.localIdx;
		try {

			Label normalLabel = new Label();

			super.visitVarInsn(ALOAD, 0);
			Type miType = Type.getType(IMethodInterceptor.class);

			Method methodCalled = Method.getMethod(IMethodInterceptor.class.getDeclaredMethod("methodCalled", Object.class, String.class, Object[].class));

			super.visitFieldInsn(GETFIELD, clazzName, "methodInterceptor",
					"Lde/topicmapslab/aranuka/proxy/IMethodInterceptor;");

			super.visitVarInsn(ALOAD, 0);
			super.visitLdcInsn(methodName);
			
			storeConst(params.length);
			super.visitTypeInsn(ANEWARRAY, "java/lang/Object");
			
			int paramWord = 1;
			for (int i = 0; i < params.length; i++) {
				Type type = params[i];
				
				super.visitInsn(DUP);
				storeConst(i);
				
				
				super.visitVarInsn(type.getOpcode(ILOAD), paramWord);
				paramWord+=type.getSize();
				switch (type.getSort()) {
				case Type.INT:
					invokeBoxing("Integer", "I");
					break;
				case Type.CHAR:
					invokeBoxing("Character", "C");
					break;
				case Type.LONG:
					invokeBoxing("Long", "J");
					break;
				case Type.DOUBLE:
					invokeBoxing("Double", "D");
					break;
				case Type.BYTE:
					invokeBoxing("Byte", "B");
					break;
				case Type.FLOAT:
					invokeBoxing("Float", "F");
					break;
				case Type.SHORT:
					invokeBoxing("Short", "S");
					break;
				case Type.BOOLEAN:
					invokeBoxing("Boolean", "Z");
					break;
				}
				super.visitInsn(AASTORE);
			}

			super.visitMethodInsn(INVOKEINTERFACE, miType.getInternalName(), methodCalled.getName(),
					methodCalled.getDescriptor());
			super.visitVarInsn(ASTORE, localIdx);

			
			

			if (returnType.getSort() == Type.OBJECT) {
				super.visitVarInsn(ALOAD, localIdx);
				super.visitJumpInsn(IFNULL, normalLabel);
				
				super.visitVarInsn(ALOAD, localIdx);
				super.visitTypeInsn(CHECKCAST, returnType.getInternalName());
				super.visitInsn(returnType.getOpcode(IRETURN));

				super.visitLabel(normalLabel);
			}



			super.visitVarInsn(ALOAD, 0);
			paramWord = 1;
			for (int i = 0; i < params.length ; i++) {
				Type type = params[i];
				super.visitVarInsn(type.getOpcode(ILOAD), paramWord);
				paramWord += type.getSize();
			}

			super.visitMethodInsn(INVOKESPECIAL, origClass, methodName, desc);
			super.visitInsn(returnType.getOpcode(IRETURN));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param i
	 */
	private void storeConst(int i) {
		if (i<5)
			super.visitInsn(ICONST_0+i);
		else
			super.visitLdcInsn(i);
		
	}

	void invokeBoxing(String typeClass, String paramSig) {
		super.visitMethodInsn(INVOKESTATIC, "java/lang/"+typeClass, "valueOf", "("+paramSig+")Ljava/lang/"+typeClass+";");
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(10, maxLocals+1);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
	}

	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		// if (opcode==GETFIELD)
		// super.visitFieldInsn(opcode, clazzName, name, desc);
		// else
		// super.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {

	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		return null;
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
	}

	@Override
	public void visitInsn(int opcode) {
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
	}

	@Override
	public void visitLabel(Label label) {
	}

	@Override
	public void visitLdcInsn(Object cst) {
	}

	@Override
	public void visitIincInsn(int var, int increment) {
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
	}

}
