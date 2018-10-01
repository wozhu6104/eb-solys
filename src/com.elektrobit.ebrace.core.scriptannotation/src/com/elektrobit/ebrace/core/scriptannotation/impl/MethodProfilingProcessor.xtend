/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptannotation.impl

import org.eclipse.xtend.lib.macro.AbstractMethodProcessor
import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration
import static org.eclipse.xtend.lib.macro.declaration.Visibility.PRIVATE
import org.apache.log4j.Logger

@Active(MethodProfilingProcessor)
annotation Profiling {
}

class MethodProfilingProcessor extends AbstractMethodProcessor {

	var memberDefined = false

	override doTransform(MutableMethodDeclaration annotatedMethod, @Extension TransformationContext context) {

		// Add the member variable 'log' to the class
		if (!memberDefined) {
			annotatedMethod.declaringType.addField('log') [
				static = true
				final = true
				 type = Logger.newTypeReference
				initializer = ['''Logger.getLogger("kpiLogger")''']
			]
			memberDefined = true
		}

		//
		annotatedMethod.declaringType.addMethod(annotatedMethod.delegate) [
			visibility = PRIVATE
			returnType = annotatedMethod.returnType
			docComment = annotatedMethod.docComment
			static = annotatedMethod.static
			body = annotatedMethod.body
			for (p : annotatedMethod.parameters) {
				addParameter(p.simpleName, p.type)
			}
			exceptions = annotatedMethod.exceptions
			primarySourceElement = annotatedMethod
		]

		annotatedMethod.body = [
			'''
				this.log.info("Enter «annotatedMethod.declaringType.simpleName».«annotatedMethod.simpleName»");
				«annotatedMethod.delegate»(«annotatedMethod.parameters.map[simpleName].join(", ")»);
				this.log.info("Exit «annotatedMethod.declaringType.simpleName».«annotatedMethod.simpleName»");
			         '''
		]

		annotatedMethod.returnType = primitiveVoid

	}

	def delegate(MutableMethodDeclaration m) {
		m.simpleName + 'Delegate'
	}

}
