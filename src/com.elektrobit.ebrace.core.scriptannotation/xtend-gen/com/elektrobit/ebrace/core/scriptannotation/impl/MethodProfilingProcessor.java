/**
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package com.elektrobit.ebrace.core.scriptannotation.impl;

import org.apache.log4j.Logger;
import org.eclipse.xtend.lib.macro.AbstractMethodProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy;
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableParameterDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class MethodProfilingProcessor extends AbstractMethodProcessor {
  private boolean memberDefined = false;
  
  @Override
  public void doTransform(final MutableMethodDeclaration annotatedMethod, @Extension final TransformationContext context) {
    if ((!this.memberDefined)) {
      final Procedure1<MutableFieldDeclaration> _function = (MutableFieldDeclaration it) -> {
        it.setStatic(true);
        it.setFinal(true);
        it.setType(context.newTypeReference(Logger.class));
        final CompilationStrategy _function_1 = (CompilationStrategy.CompilationContext it_1) -> {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Logger.getLogger(\"kpiLogger\")");
          return _builder;
        };
        it.setInitializer(_function_1);
      };
      annotatedMethod.getDeclaringType().addField("log", _function);
      this.memberDefined = true;
    }
    final Procedure1<MutableMethodDeclaration> _function_1 = (MutableMethodDeclaration it) -> {
      it.setVisibility(Visibility.PRIVATE);
      it.setReturnType(annotatedMethod.getReturnType());
      it.setDocComment(annotatedMethod.getDocComment());
      it.setStatic(annotatedMethod.isStatic());
      it.setBody(annotatedMethod.getBody());
      Iterable<? extends MutableParameterDeclaration> _parameters = annotatedMethod.getParameters();
      for (final MutableParameterDeclaration p : _parameters) {
        it.addParameter(p.getSimpleName(), p.getType());
      }
      it.setExceptions(((TypeReference[])Conversions.unwrapArray(annotatedMethod.getExceptions(), TypeReference.class)));
      context.setPrimarySourceElement(it, annotatedMethod);
    };
    annotatedMethod.getDeclaringType().addMethod(this.delegate(annotatedMethod), _function_1);
    final CompilationStrategy _function_2 = (CompilationStrategy.CompilationContext it) -> {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("this.log.info(\"Enter ");
      String _simpleName = annotatedMethod.getDeclaringType().getSimpleName();
      _builder.append(_simpleName);
      _builder.append(".");
      String _simpleName_1 = annotatedMethod.getSimpleName();
      _builder.append(_simpleName_1);
      _builder.append("\");");
      _builder.newLineIfNotEmpty();
      String _delegate = this.delegate(annotatedMethod);
      _builder.append(_delegate);
      _builder.append("(");
      final Function1<MutableParameterDeclaration, String> _function_3 = (MutableParameterDeclaration it_1) -> {
        return it_1.getSimpleName();
      };
      String _join = IterableExtensions.join(IterableExtensions.map(annotatedMethod.getParameters(), _function_3), ", ");
      _builder.append(_join);
      _builder.append(");");
      _builder.newLineIfNotEmpty();
      _builder.append("this.log.info(\"Exit ");
      String _simpleName_2 = annotatedMethod.getDeclaringType().getSimpleName();
      _builder.append(_simpleName_2);
      _builder.append(".");
      String _simpleName_3 = annotatedMethod.getSimpleName();
      _builder.append(_simpleName_3);
      _builder.append("\");");
      _builder.newLineIfNotEmpty();
      return _builder;
    };
    annotatedMethod.setBody(_function_2);
    annotatedMethod.setReturnType(context.getPrimitiveVoid());
  }
  
  public String delegate(final MutableMethodDeclaration m) {
    String _simpleName = m.getSimpleName();
    return (_simpleName + "Delegate");
  }
}
