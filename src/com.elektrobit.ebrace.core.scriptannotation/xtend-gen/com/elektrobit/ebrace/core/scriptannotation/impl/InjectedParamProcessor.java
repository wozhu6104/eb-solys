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

import org.eclipse.xtend.lib.macro.AbstractFieldProcessor;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy;
import org.eclipse.xtend.lib.macro.declaration.MutableConstructorDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableParameterDeclaration;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend.lib.macro.expression.Expression;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class InjectedParamProcessor extends AbstractFieldProcessor {
  private String constructorCode = new Function0<String>() {
    public String apply() {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("initScriptContext(scriptContext);");
      _builder.newLine();
      return _builder.toString();
    }
  }.apply();
  
  private boolean delegatedConstructor = false;
  
  @Override
  public void doTransform(final MutableFieldDeclaration annotatedField, @Extension final TransformationContext context) {
    boolean hasError = false;
    boolean _not = (!(annotatedField.getType().isPrimitive() || annotatedField.getType().getSimpleName().equals("String")));
    if (_not) {
      hasError = true;
      context.addError(annotatedField, "Only primitive types can be injected!");
    }
    Expression _initializer = annotatedField.getInitializer();
    boolean _tripleEquals = (_initializer == null);
    if (_tripleEquals) {
      hasError = true;
      String _simpleName = annotatedField.getSimpleName();
      String _plus = ("You must initialize the field " + _simpleName);
      String _plus_1 = (_plus + "!");
      context.addError(annotatedField, _plus_1);
    }
    boolean _isArray = annotatedField.getType().isArray();
    if (_isArray) {
      hasError = true;
      context.addError(annotatedField, "Arrays cannot be injected!");
    }
    if ((!hasError)) {
      if ((!this.delegatedConstructor)) {
        final MutableConstructorDeclaration constr = IterableExtensions.head(annotatedField.getDeclaringType().getDeclaredConstructors());
        final Procedure1<MutableMethodDeclaration> _function = (MutableMethodDeclaration it) -> {
          it.setVisibility(Visibility.PRIVATE);
          Iterable<? extends MutableParameterDeclaration> _parameters = constr.getParameters();
          for (final MutableParameterDeclaration p : _parameters) {
            it.addParameter(p.getSimpleName(), p.getType());
          }
          it.setBody(constr.getBody());
        };
        annotatedField.getDeclaringType().addMethod("initScriptContext", _function);
        this.delegatedConstructor = true;
      }
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(this.constructorCode);
      _builder.newLineIfNotEmpty();
      String _simpleName_1 = annotatedField.getSimpleName();
      _builder.append(_simpleName_1);
      _builder.append(" = ");
      String _injectedName = this.getInjectedName(annotatedField);
      _builder.append(_injectedName);
      _builder.append("();");
      _builder.newLineIfNotEmpty();
      this.constructorCode = _builder.toString();
      MutableConstructorDeclaration _head = IterableExtensions.head(annotatedField.getDeclaringType().getDeclaredConstructors());
      final CompilationStrategy _function_1 = (CompilationStrategy.CompilationContext it) -> {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append(this.constructorCode);
        _builder_1.newLineIfNotEmpty();
        return _builder_1;
      };
      _head.setBody(_function_1);
      final Procedure1<MutableMethodDeclaration> _function_2 = (MutableMethodDeclaration it) -> {
        it.setVisibility(Visibility.PRIVATE);
        it.setReturnType(annotatedField.getType());
        final CompilationStrategy _function_3 = (CompilationStrategy.CompilationContext it_1) -> {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("if(this._scriptContext.hasParameter(\"");
          String _simpleName_2 = annotatedField.getSimpleName();
          _builder_1.append(_simpleName_2);
          _builder_1.append("\")) { ");
          _builder_1.newLineIfNotEmpty();
          _builder_1.append("\t");
          _builder_1.append("return ");
          String _wrapReturnValue = this.wrapReturnValue(annotatedField);
          _builder_1.append(_wrapReturnValue, "\t");
          _builder_1.append(";");
          _builder_1.newLineIfNotEmpty();
          _builder_1.append("} ");
          _builder_1.newLine();
          _builder_1.append("else {");
          _builder_1.newLine();
          _builder_1.append("\t");
          _builder_1.append("return ");
          String _defaultName = this.getDefaultName(annotatedField);
          _builder_1.append(_defaultName, "\t");
          _builder_1.append("();");
          _builder_1.newLineIfNotEmpty();
          _builder_1.append("}");
          _builder_1.newLine();
          return _builder_1;
        };
        it.setBody(_function_3);
      };
      annotatedField.getDeclaringType().addMethod(this.getInjectedName(annotatedField), _function_2);
      final Procedure1<MutableMethodDeclaration> _function_3 = (MutableMethodDeclaration it) -> {
        it.setVisibility(Visibility.PRIVATE);
        it.setReturnType(annotatedField.getType());
        it.setBody(annotatedField.getInitializer());
      };
      annotatedField.getDeclaringType().addMethod(this.getDefaultName(annotatedField), _function_3);
    }
  }
  
  private String getInjectedName(final MutableFieldDeclaration annotatedField) {
    String _firstUpper = StringExtensions.toFirstUpper(annotatedField.getSimpleName());
    return ("inject" + _firstUpper);
  }
  
  private String getDefaultName(final MutableFieldDeclaration annotatedField) {
    String _firstUpper = StringExtensions.toFirstUpper(annotatedField.getSimpleName());
    return ("default" + _firstUpper);
  }
  
  private String wrapReturnValue(final MutableFieldDeclaration annotatedField) {
    String _switchResult = null;
    String _simpleName = annotatedField.getType().getSimpleName();
    if (_simpleName != null) {
      switch (_simpleName) {
        case "boolean":
          String _simpleName_1 = annotatedField.getSimpleName();
          String _plus = ("Boolean.parseBoolean(this._scriptContext.getParameter(\"" + _simpleName_1);
          _switchResult = (_plus + "\"))");
          break;
        case "float":
          String _simpleName_2 = annotatedField.getSimpleName();
          String _plus_1 = ("Float.parseFloat(this._scriptContext.getParameter(\"" + _simpleName_2);
          _switchResult = (_plus_1 + "\"))");
          break;
        case "double":
          String _simpleName_3 = annotatedField.getSimpleName();
          String _plus_2 = ("Double.parseDouble(this._scriptContext.getParameter(\"" + _simpleName_3);
          _switchResult = (_plus_2 + "\"))");
          break;
        case "byte":
          String _simpleName_4 = annotatedField.getSimpleName();
          String _plus_3 = ("Byte.parseByte(this._scriptContext.getParameter(\"" + _simpleName_4);
          _switchResult = (_plus_3 + "\"))");
          break;
        case "short":
          String _simpleName_5 = annotatedField.getSimpleName();
          String _plus_4 = ("Short.parseShort(this._scriptContext.getParameter(\"" + _simpleName_5);
          _switchResult = (_plus_4 + "\"))");
          break;
        case "int":
          String _simpleName_6 = annotatedField.getSimpleName();
          String _plus_5 = ("Integer.parseInt(this._scriptContext.getParameter(\"" + _simpleName_6);
          _switchResult = (_plus_5 + "\"))");
          break;
        case "long":
          String _simpleName_7 = annotatedField.getSimpleName();
          String _plus_6 = ("Long.parseLong(this._scriptContext.getParameter(\"" + _simpleName_7);
          _switchResult = (_plus_6 + "\"))");
          break;
        case "char":
          String _simpleName_8 = annotatedField.getSimpleName();
          String _plus_7 = ("this._scriptContext.getParameter(\"" + _simpleName_8);
          _switchResult = (_plus_7 + "\").charAt(0)");
          break;
        case "String":
          String _simpleName_9 = annotatedField.getSimpleName();
          String _plus_8 = ("this._scriptContext.getParameter(\"" + _simpleName_9);
          _switchResult = (_plus_8 + "\")");
          break;
        default:
          _switchResult = "error";
          break;
      }
    } else {
      _switchResult = "error";
    }
    return _switchResult;
  }
}
