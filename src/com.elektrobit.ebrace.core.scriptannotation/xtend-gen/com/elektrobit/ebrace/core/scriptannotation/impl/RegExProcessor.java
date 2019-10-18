/**
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package com.elektrobit.ebrace.core.scriptannotation.impl;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.xtend.lib.macro.AbstractFieldProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy;
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableTypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class RegExProcessor extends AbstractFieldProcessor {
  @Override
  public void doTransform(final MutableFieldDeclaration annotatedField, @Extension final TransformationContext context) {
    boolean hasError = false;
    boolean _equals = annotatedField.getType().getSimpleName().equals("String");
    boolean _not = (!_equals);
    if (_not) {
      hasError = true;
      context.addError(annotatedField, "Only Strings can be annotated with @RegEx");
    }
    if ((!hasError)) {
      final Procedure1<MutableFieldDeclaration> _function = (MutableFieldDeclaration it) -> {
        it.setVisibility(Visibility.PRIVATE);
        it.setFinal(true);
        it.setType(context.newTypeReference(Pattern.class));
        final CompilationStrategy _function_1 = (CompilationStrategy.CompilationContext it_1) -> {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Pattern.compile(this.");
          String _simpleName = annotatedField.getSimpleName();
          _builder.append(_simpleName);
          _builder.append(")");
          return _builder;
        };
        it.setInitializer(_function_1);
      };
      annotatedField.getDeclaringType().addField(RegExProcessor.patternName(annotatedField.getSimpleName()), _function);
      MutableTypeDeclaration _declaringType = annotatedField.getDeclaringType();
      String _firstUpper = StringExtensions.toFirstUpper(annotatedField.getSimpleName());
      String _plus = ("get" + _firstUpper);
      String _plus_1 = (_plus + "Matcher");
      final Procedure1<MutableMethodDeclaration> _function_1 = (MutableMethodDeclaration it) -> {
        it.setVisibility(Visibility.PRIVATE);
        it.addParameter("stringToMatch", context.getString());
        final CompilationStrategy _function_2 = (CompilationStrategy.CompilationContext it_1) -> {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("return this.");
          String _patternName = RegExProcessor.patternName(annotatedField.getSimpleName());
          _builder.append(_patternName);
          _builder.append(".matcher(stringToMatch);");
          return _builder;
        };
        it.setBody(_function_2);
        it.setReturnType(context.newTypeReference(Matcher.class));
      };
      _declaringType.addMethod(_plus_1, _function_1);
      final Pattern p = Pattern.compile("\\(?<\\w+>");
      final Matcher m = p.matcher(annotatedField.getInitializer().toString());
      final ArrayList<String> groupNames = CollectionLiterals.<String>newArrayList();
      while (m.find()) {
        String _string = annotatedField.getInitializer().toString();
        int _start = m.start();
        int _plus_2 = (_start + 1);
        int _end = m.end();
        int _minus = (_end - 1);
        groupNames.add(_string.substring(_plus_2, _minus));
      }
      int _size = groupNames.size();
      boolean _equals_1 = (_size == 0);
      if (_equals_1) {
        context.addWarning(annotatedField, "Your RegEx does not contain any named groups, e.g. ?<name>");
      } else {
        final Consumer<String> _function_2 = (String e) -> {
          MutableTypeDeclaration _declaringType_1 = annotatedField.getDeclaringType();
          String _firstUpper_1 = StringExtensions.toFirstUpper(e);
          String _plus_2 = ("get" + _firstUpper_1);
          final Procedure1<MutableMethodDeclaration> _function_3 = (MutableMethodDeclaration it) -> {
            it.addParameter("matcher", context.newTypeReference(Matcher.class));
            final CompilationStrategy _function_4 = (CompilationStrategy.CompilationContext it_1) -> {
              StringConcatenation _builder = new StringConcatenation();
              _builder.append("return matcher.group(\"");
              _builder.append(e);
              _builder.append("\");");
              return _builder;
            };
            it.setBody(_function_4);
            it.setReturnType(context.getString());
          };
          _declaringType_1.addMethod(_plus_2, _function_3);
        };
        groupNames.forEach(_function_2);
      }
    }
  }
  
  private static String patternName(final String regExName) {
    return (regExName + "Pattern");
  }
}
