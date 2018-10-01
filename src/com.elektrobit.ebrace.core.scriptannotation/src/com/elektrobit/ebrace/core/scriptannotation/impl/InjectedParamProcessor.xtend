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

import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.AbstractFieldProcessor
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import static org.eclipse.xtend.lib.macro.declaration.Visibility.PRIVATE
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Active(InjectedParamProcessor)
@Retention(RetentionPolicy.RUNTIME)
annotation InjectedParam {
}

class InjectedParamProcessor extends AbstractFieldProcessor {

	var constructorCode = '''
		initScriptContext(scriptContext);
	'''

	var delegatedConstructor = false

	override doTransform(MutableFieldDeclaration annotatedField, @Extension TransformationContext context) {

		// The annotation @InjectedParams can only be applied to primitive types (incl. Strings)
		// The field must be set with a default value
		// If these rules are not fulfilled, then an Error is added to the Editor and no code is generated				
		var hasError = false

		if (!(annotatedField.type.primitive || annotatedField.type.simpleName.equals('String'))) {
			hasError = true
			annotatedField.addError('Only primitive types can be injected!')
		}

		if (annotatedField.initializer === null) {
			hasError = true
			annotatedField.addError('You must initialize the field ' + annotatedField.simpleName + '!')
		}

		if (annotatedField.type.array) {
			hasError = true
			annotatedField.addError('Arrays cannot be injected!')
		}

		if (!hasError) {

			if (!delegatedConstructor) {
				val constr = annotatedField.declaringType.declaredConstructors.head
				annotatedField.declaringType.addMethod('initScriptContext') [
					visibility = PRIVATE
					for (p : constr.parameters) {
						addParameter(p.simpleName, p.type)
					}
					body = constr.body
				]
				delegatedConstructor = true
			}

			constructorCode = '''
				«constructorCode»
				«annotatedField.simpleName» = «annotatedField.injectedName»();
			'''

			annotatedField.declaringType.declaredConstructors.head.body = [
				'''
					«constructorCode»
				'''
			]

			annotatedField.declaringType.addMethod(annotatedField.injectedName) [
				visibility = PRIVATE
				returnType = annotatedField.type
				body = [
					'''
						if(this._scriptContext.hasParameter("«annotatedField.simpleName»")) { 
							return «annotatedField.wrapReturnValue»;
						} 
						else {
							return «annotatedField.defaultName»();
						}
					'''
				]
			]

			// Sets the
			annotatedField.declaringType.addMethod(annotatedField.defaultName) [
				visibility = PRIVATE
				returnType = annotatedField.type
				body = annotatedField.initializer
			]

		}

	}

	def private getInjectedName(MutableFieldDeclaration annotatedField) {
		'inject' + annotatedField.simpleName.toFirstUpper
	}

	def private getDefaultName(MutableFieldDeclaration annotatedField) {
		'default' + annotatedField.simpleName.toFirstUpper
	}

	def private wrapReturnValue(MutableFieldDeclaration annotatedField) {

		switch annotatedField.type.simpleName {
			case 'boolean':
				'Boolean.parseBoolean(this._scriptContext.getParameter("' + annotatedField.simpleName + '"))'
			case 'float':
				'Float.parseFloat(this._scriptContext.getParameter("' + annotatedField.simpleName + '"))'
			case 'double':
				'Double.parseDouble(this._scriptContext.getParameter("' + annotatedField.simpleName + '"))'
			case 'byte':
				'Byte.parseByte(this._scriptContext.getParameter("' + annotatedField.simpleName + '"))'
			case 'short':
				'Short.parseShort(this._scriptContext.getParameter("' + annotatedField.simpleName + '"))'
			case 'int':
				'Integer.parseInt(this._scriptContext.getParameter("' + annotatedField.simpleName + '"))'
			case 'long':
				'Long.parseLong(this._scriptContext.getParameter("' + annotatedField.simpleName + '"))'
			case 'char':
				'this._scriptContext.getParameter("' + annotatedField.simpleName + '").charAt(0)'
			case 'String':
				'this._scriptContext.getParameter("' + annotatedField.simpleName + '")'
			default:
				'error'
		}
	}

}
