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
/**
 * 
 */
package de.topicmapslab.aranuka.proxy;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Hannes Niederhausen
 *
 */
public class ProxyAdapter extends ClassAdapter {

	private String oldName;
	private String newName;
	
	public ProxyAdapter(ClassVisitor cv, String oldName, String newName) {
		super(cv);
		this.oldName = oldName;
		this.newName = newName;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

		if (oldName.equals(name)) {
			super.visit(version, access, newName, signature, oldName, new String[0]);
		}
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}
	
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (name.equals("<init>"))
			return null;
		
		return new GetterAdapter(super.visitMethod(access, name, desc, signature, exceptions), desc, newName, name);
//		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	@Override
	public void visitEnd() {
		MethodVisitor visitMethod = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		visitMethod.visitCode();
		visitMethod.visitVarInsn(ALOAD, 0);
		visitMethod.visitMethodInsn(INVOKESPECIAL, oldName, "<init>", "()V");
		visitMethod.visitInsn(RETURN);
		visitMethod.visitMaxs(1, 1);
		visitMethod.visitEnd();
		
		super.visitField(ACC_PRIVATE, "methodInterceptor", "Lde/topicmapslab/aranuka/proxy/IMethodInterceptor;", null, null).visitEnd();
		
		visitMethod = super.visitMethod(ACC_PRIVATE, "setMethodInterceptor", "(Lde/topicmapslab/aranuka/proxy/IMethodInterceptor;)V", null, null);
		visitMethod.visitCode();
		visitMethod.visitVarInsn(ALOAD, 0);
		visitMethod.visitVarInsn(ALOAD, 1);
		visitMethod.visitFieldInsn(PUTFIELD, newName, "methodInterceptor", "Lde/topicmapslab/aranuka/proxy/IMethodInterceptor;");
		visitMethod.visitInsn(RETURN);
		visitMethod.visitMaxs(2, 2);
		visitMethod.visitEnd();
		
		super.visitEnd();
	}
}
