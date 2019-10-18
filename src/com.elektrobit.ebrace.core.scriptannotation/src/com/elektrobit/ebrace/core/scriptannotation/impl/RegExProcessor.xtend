/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptannotation.impl

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.regex.Pattern
import org.eclipse.xtend.lib.macro.AbstractFieldProcessor
import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration

import static org.eclipse.xtend.lib.macro.declaration.Visibility.PRIVATE
import java.util.regex.Matcher

@Active(RegExProcessor)
@Retention(RetentionPolicy.RUNTIME)
annotation RegEx {
}

class RegExProcessor extends AbstractFieldProcessor {
		
	override doTransform(MutableFieldDeclaration annotatedField, @Extension TransformationContext context) {
		
		var hasError = false

		if (!annotatedField.type.simpleName.equals('String')) {
			hasError = true
			annotatedField.addError('Only Strings can be annotated with @RegEx')
		}

		if(!hasError)
		{			
			//   @RegEx
			//   val myRegEx = "(?<firstName>\\w+>)\\s(?<lastName>\\w+)$"
			
			//   private final Pattern myRegExPattern = Pattern.compile(this.myRegEx);
			annotatedField.declaringType.addField(annotatedField.simpleName.patternName) [
				visibility = PRIVATE
				final = true
				type = newTypeReference(typeof(Pattern))
				initializer = ['''Pattern.compile(this.«annotatedField.simpleName»)''']
			]
			
//			private Matcher getMyRegExMatcher(final String input) {
//    			return this.myRegExPattern.matcher(input);
//  		}
			annotatedField.declaringType.addMethod('get'+annotatedField.simpleName.toFirstUpper+'Matcher') [
				visibility = PRIVATE
				addParameter('stringToMatch', string)
				body = [
					'''return this.«annotatedField.simpleName.patternName».matcher(stringToMatch);'''
				]
				returnType = newTypeReference(typeof(Matcher))				
			]
			
			 
			 val p = Pattern.compile('\\(?<\\w+>')
			 val m = p.matcher(annotatedField.initializer.toString)
			 val groupNames = newArrayList
	
			 while (m.find()) {
				groupNames.add(annotatedField.initializer.toString.substring(m.start()+1, m.end()-1));
			 }
			 
			 if(groupNames.size==0) {
			 	annotatedField.addWarning('Your RegEx does not contain any named groups, e.g. ?<name>')
			 }
			 else {
			
//			    private String getFirstNameId(final Matcher matcher) {
//    				return matcher.group("firstName");
//  				}

//			    private String getLastNameId(final Matcher matcher) {
//    				return matcher.group("lastName");
//  				}
			 
			 	groupNames.forEach[e|
			 		annotatedField.declaringType.addMethod('get'+e.toFirstUpper) [
			 			addParameter('matcher', newTypeReference(typeof(Matcher)))
			 			body = [
			 				'''return matcher.group("«e»");'''
			 			]
			 			returnType = string
			 		]
			 	]
			 }
			 			 			
		}

	}
	
	private static def patternName(String regExName) {
		regExName+'Pattern'
	}	
	
}