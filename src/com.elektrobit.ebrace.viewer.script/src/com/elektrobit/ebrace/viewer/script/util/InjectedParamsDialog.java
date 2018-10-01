/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.script.InjectedParamsCallback;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;

public class InjectedParamsDialog extends TitleAreaDialog implements InjectedParamsCallback
{

    private static final String TITLE = "Change Script Parameters";
    private static final String MESSAGE = "You can set the values of all parameters annotated with @InjectedParam here. Please make sure the values match the declared datatype and are valid. Otherwise your script may fail.";

    private RaceScriptInfo scriptInfo;
    private Object loadedInstance;
    private final Map<String, Text> values = new HashMap<>();
    private String paramsOutput = "";

    public InjectedParamsDialog()
    {
        super( PlatformUI.getWorkbench().getDisplay().getActiveShell() );
    }

    public void initialize(RaceScriptInfo scriptInfo, Object loadedInstance)
    {
        this.scriptInfo = scriptInfo;
        this.loadedInstance = loadedInstance;
        values.clear();
        paramsOutput = "";
    }

    @Override
    public void create()
    {
        super.create();
        setTitle( TITLE );
        setMessage( MESSAGE, IMessageProvider.INFORMATION );
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite area = (Composite)super.createDialogArea( parent );
        Composite container = new Composite( area, SWT.NONE );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        GridLayout layout = new GridLayout( 3, false );
        container.setLayout( layout );

        createParameterInputFields( container );

        return area;
    }

    private void createParameterInputFields(Composite container)
    {
        for (Field field : loadedInstance.getClass().getDeclaredFields())
        {
            for (String injectedParam : scriptInfo.getInjectedParameterNames())
            {
                if (injectedParam.equals( field.getName() ))
                {
                    createInputFieldAccordingToDataType( container, field );
                }
            }
        }
    }

    private void createInputFieldAccordingToDataType(Composite container, Field field)
    {
        String fieldType = field.getType().getSimpleName();
        field.setAccessible( true );
        try
        {
            switch (fieldType)
            {
                case "String" :
                    createFieldEntry( container,
                                      field.getName(),
                                      (String)field.get( loadedInstance ),
                                      field.getType() );
                    break;
                case "boolean" :
                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getBoolean( loadedInstance ) ),
                                      field.getType() );
                    break;
                case "char" :
                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getChar( loadedInstance ) ),
                                      field.getType() );
                    break;
                case "byte" :
                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getByte( loadedInstance ) ),
                                      field.getType() );
                    break;
                case "short" :
                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getShort( loadedInstance ) ),
                                      field.getType() );
                    break;
                case "int" :

                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getInt( loadedInstance ) ),
                                      field.getType() );
                    break;
                case "long" :

                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getLong( loadedInstance ) ),
                                      field.getType() );
                    break;
                case "float" :

                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getFloat( loadedInstance ) ),
                                      field.getType() );
                    break;
                case "double" :

                    createFieldEntry( container,
                                      field.getName(),
                                      String.valueOf( field.getDouble( loadedInstance ) ),
                                      field.getType() );
                    break;

                // TODO add remaining types
            }
        }
        catch (IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            field.setAccessible( false );
        }
    }

    private void createFieldEntry(Composite container, String fieldName, String fieldValue, Class<?> fieldType)
    {
        Label fieldNameLabel = new Label( container, SWT.NONE );
        fieldNameLabel.setText( fieldName );

        Label fieldTypeLabel = new Label( container, SWT.NONE );
        fieldTypeLabel.setText( fieldType.getSimpleName() );

        GridData fieldData = new GridData();
        fieldData.grabExcessHorizontalSpace = true;
        fieldData.horizontalAlignment = GridData.FILL;

        Text fieldValueInput = new Text( container, SWT.BORDER );
        fieldValueInput.setLayoutData( fieldData );
        fieldValueInput.setText( fieldValue );
        values.put( fieldName, fieldValueInput );
    }

    @Override
    protected boolean isResizable()
    {
        return true;
    }

    @Override
    protected void okPressed()
    {
        saveInput();
        super.okPressed();
    }

    private void saveInput()
    {
        for (Entry<String, Text> newField : values.entrySet())
        {
            try
            {
                Field field = loadedInstance.getClass().getDeclaredField( newField.getKey() );
                String type = field.getType().getSimpleName();
                Object value = null;
                switch (type)
                {
                    case "String" :
                        value = newField.getValue().getText();
                        break;
                    case "boolean" :
                        value = Boolean.parseBoolean( newField.getValue().getText() );
                        break;
                    case "char" :
                        value = newField.getValue().getText().charAt( 0 );
                        break;
                    case "byte" :
                        value = (byte)newField.getValue().getText().charAt( 0 );
                        break;
                    case "short" :
                        value = Short.parseShort( newField.getValue().getText() );
                        break;
                    case "int" :
                        value = Integer.parseInt( newField.getValue().getText() );
                        break;
                    case "long" :
                        value = Long.parseLong( newField.getValue().getText() );
                        break;
                    case "float" :
                        value = Float.parseFloat( newField.getValue().getText() );
                        break;
                    case "double" :
                        value = Double.parseDouble( newField.getValue().getText() );
                        break;
                }

                field.setAccessible( true );
                field.set( loadedInstance, value );
                field.setAccessible( false );
                paramsOutput += "  " + field.getName() + ":" + value + "\n";
            }
            catch (NoSuchFieldException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (SecurityException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public String onParamsRequested(RaceScriptInfo scriptInfo, Object runningInstance)
    {
        InjectedParamsDialog current = this;
        initialize( scriptInfo, runningInstance );

        Display.getDefault().syncExec( new Runnable()
        {
            @Override
            public void run()
            {
                current.open();
            }
        } );

        return paramsOutput;
    }

}
