/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.wizard;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xtend.ide.wizards.AbstractNewXtendElementWizardPage;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;

@SuppressWarnings("restriction")
public class NewScriptWizardNamePage extends AbstractNewXtendElementWizardPage
        implements
            CreateResourceInteractionCallback
{
    private final static String[] SCRIPT_API_CLASS_NAMES = new String[]{"ScriptBase", "ScriptContext", "BeforeScript",
            "AfterScript", "ExecutionContext", "Filter", "RuntimeEvent", "RuntimeEventChannel", "TimeMarker",
            "RuntimeEventTag", "DecodedNode", "DecodedTree", "DecodedRuntimeEvent", "STimeSegmentChannel",
            "STimeSegment", "STable", "SChart", "SSnapshot", "SHtmlView", "CHART_TYPE"};
    private boolean inMethodCreateType = false;
    private CreateResourceInteractionUseCase createResourceUseCase;

    public NewScriptWizardNamePage()
    {
        super( CLASS_TYPE, ScriptConstants.TITLE );
        this.setTitle( ScriptConstants.TITLE );
        this.setDescription( ScriptConstants.DESCRIPTION );
    }

    @Override
    public void createControl(Composite parent)
    {
        Composite composite = createCommonControls( parent );
        setControl( composite );
        setPageComplete( false );
        createResourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );
    }

    @Override
    protected Composite createCommonControls(Composite parent)
    {
        initializeDialogUnits( parent );
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setFont( parent.getFont() );
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        layout.marginTop = 20;
        layout.marginLeft = 10;
        layout.marginRight = 10;

        composite.setLayout( layout );
        GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
        composite.setLayoutData( layoutData );
        createTypeNameControls( composite, layout.numColumns );

        Text nameTextField = getNameTextField( composite );
        GridData gridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
        nameTextField.setLayoutData( gridData );

        return composite;
    }

    private Text getNameTextField(Composite composite)
    {
        for (Control control : composite.getChildren())
        {
            if (control instanceof Text)
            {
                return (Text)control;
            }
        } ;
        return null;
    }

    @Override
    protected int createType()
    {
        inMethodCreateType = true;
        final int[] size = {0};
        final String scriptName = getTypeName();

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( ScriptConstants.SCRIPTS_PROJECT_NAME );
        IJavaProject javaProject = JavaCore.create( project );

        initContainerPage( javaProject );
        initTypePage( javaProject );
        setTypeName( scriptName, false );
        javaProject.getProject().getFolder( ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME );

        IFile raceScriptXtendFile = javaProject.getProject().getFolder( ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME )
                .getFile( getTypeName() + ScriptConstants.SCRIPT_EXTENSION );

        size[0] = createXtendElement( new NullProgressMonitor(),
                                      raceScriptXtendFile,
                                      "    ",
                                      System.getProperty( "line.separator" ) );

        createResourceUseCase.createAndOpenUserScript( raceScriptXtendFile.getLocation().toFile(), getTypeName() );

        inMethodCreateType = false;
        return size[0];
    }

    @Override
    protected void doStatusUpdate()
    {
        IStatus[] status = new IStatus[]{fContainerStatus, fPackageStatus, fTypeNameStatus, fSuperClassStatus,
                fSuperInterfacesStatus};
        updateStatus( status );
    }

    @Override
    protected IStatus typeNameChanged()
    {
        if (!inMethodCreateType)
        {

            String enteredScriptName = getTypeName();
            if (isNameColliding( enteredScriptName ))
            {
                return new StatusInfo( IStatus.ERROR,
                                       "Script name collides with one of EB solys script API class names, please choose another one." );
            }
            if (!isScriptNameFormatValid())
            {
                return new StatusInfo( IStatus.ERROR,
                                       "Script name '" + enteredScriptName
                                               + "' not valid. It has to start with a capital letter and must not contain any special characters. " );
            }
            if (scriptExists( enteredScriptName ))
            {
                return new StatusInfo( IStatus.ERROR, "Script name already exists" );
            }
        }
        return new StatusInfo();
    }

    private boolean isNameColliding(String scriptName)
    {
        return Arrays.asList( SCRIPT_API_CLASS_NAMES ).contains( scriptName );
    }

    private boolean scriptExists(String enteredScriptName)
    {
        return createResourceUseCase.isUserScriptAvailable( enteredScriptName );
    }

    @Override
    protected IStatus packageChanged()
    {
        super.packageChanged();
        return new StatusInfo();
    }

    @Override
    protected IStatus containerChanged()
    {
        super.containerChanged();
        return new StatusInfo();
    }

    @Override
    public boolean canFlipToNextPage()
    {
        return isScriptNameFormatValid() && !scriptExists( getTypeName() ) && !isNameColliding( getTypeName() );
    }

    private boolean isScriptNameFormatValid()
    {
        return Pattern.matches( "^[A-Z][a-zA-Z0-9_]*", getTypeName() );
    }

    @Override
    protected String getPackageDeclaration(String lineSperator)
    {
        return "";
    }

    @Override
    protected String getTypeContent(String indentation, String lineSperator)
    {
        IWizard wizard = getWizard();
        return ((NewScriptWizard)wizard).getScriptContent();
    }

    @Override
    protected String getElementCreationErrorMessage()
    {
        return "Error creating class";
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }

    @Override
    public void dispose()
    {
        createResourceUseCase.unregister();
        super.dispose();
    }
}
