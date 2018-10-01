/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.validation.dialog;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.localstore.SafeChunkyInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elektrobit.ebrace.viewer.common.dialog.BaseSaveSettingsDialog;

@SuppressWarnings("restriction")
public class DANAValidationDialog extends BaseSaveSettingsDialog
{
    private static final String WORKSPACE_IMPORTED_PROJECTS_PATH = "/.metadata/.plugins/org.eclipse.core.resources/.projects";
    List<IProject> projects;
    List<String> projectPaths;
    private String danaModel;
    private String stateMachineStringId;
    private String resultString;
    private Text stateMachineId;
    private String logfilePathString;
    private String workspaceString;

    public DANAValidationDialog(Shell parentShell)
    {
        super( parentShell );
        projects = new ArrayList<IProject>();
        projectPaths = new ArrayList<String>();
    }

    @Override
    protected Control createContents(Composite parent)
    {
        parent.setLayout( new FillLayout() );
        Composite container = new Composite( parent, SWT.BORDER );

        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        super.createContents( container );
        Composite content = new Composite( (Composite)getDialogArea(), SWT.NONE );
        content.setLayout( new GridLayout( 3, false ) );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        createFilePart( content );
        setMessages();
        return container;
    }

    private void createFilePart(Composite parent)
    {
        Label l = new Label( parent, SWT.NONE );
        l.setText( "Workspace path: " );
        final Text workspacePath = new Text( parent, SWT.BORDER | SWT.READ_ONLY );
        workspacePath.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );
        Button browseButton = new Button( parent, SWT.PUSH );
        browseButton.setText( "Browse..." );

        browseButton.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                DirectoryDialog d = new DirectoryDialog( getShell() );
                String selectedString = d.open();
                if (selectedString != null)
                {
                    workspacePath.setText( selectedString );
                    workspaceString = selectedString;
                }
                getProjects( selectedString );

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }
        } );

        l = new Label( parent, SWT.NONE );
        l.setText( "Model path: " );
        final Text stateMachinePath = new Text( parent, SWT.BORDER | SWT.READ_ONLY );
        stateMachinePath.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );
        Button browseStateMachineButton = new Button( parent, SWT.PUSH );
        browseStateMachineButton.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                IProjectDescription description;
                try
                {
                    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                    for (String path : projectPaths)
                    {
                        description = ResourcesPlugin.getWorkspace()
                                .loadProjectDescription( new Path( path + "/.project" ) );
                        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( description.getName() );
                        if (!project.exists())
                        {
                            project.create( description, null );
                        }
                        if (!project.isOpen())
                        {
                            project.open( null );
                        }
                        projects.add( project );

                        workspaceRoot.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                        System.out.println();
                    }

                    FilterModelSelectionDialog d = new FilterModelSelectionDialog( getShell(),
                                                                                   false,
                                                                                   workspaceRoot,
                                                                                   IResource.FILE );

                    d.setInitialPattern( "?" );
                    if (Dialog.OK == d.open())
                    {
                        final Object firstResult = d.getFirstResult();
                        if (firstResult instanceof IFile)
                        {
                            final IFile selectedFile = (IFile)firstResult;
                            final URI fileURI = URI.createPlatformResourceURI( selectedFile.getFullPath().toString(),
                                                                               true );
                            danaModel = fileURI.toString();
                            stateMachinePath.setText( danaModel );
                        }
                    }
                }
                catch (CoreException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();

                }
                finally
                {
                    deleteprojects();
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }
        } );
        l = new Label( parent, SWT.NONE );
        l.setText( "State machine id: " );
        stateMachineId = new Text( parent, SWT.BORDER );
        GridData data = new GridData( SWT.FILL, SWT.BEGINNING, true, false );
        data.horizontalSpan = 2;
        stateMachineId.setLayoutData( data );
        stateMachineId.addModifyListener( new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                stateMachineStringId = stateMachineId.getText();

            }
        } );
        browseStateMachineButton.setText( "Browse..." );
        l = new Label( parent, SWT.NONE );
        l.setText( "Log file path: " );
        final Text logFilePath = new Text( parent, SWT.BORDER | SWT.READ_ONLY );
        logFilePath.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );
        Button browseLogFileButton = new Button( parent, SWT.PUSH );
        browseLogFileButton.setText( "Browse..." );
        browseLogFileButton.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                FileDialog d = new FileDialog( getShell() );
                String logFilePathString = d.open();
                if (logFilePathString != null)
                {
                    logFilePath.setText( logFilePathString );
                    logfilePathString = logFilePathString;
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // TODO Auto-generated method stub

            }
        } );
    }

    private void getProjects(String workspacePath)
    {
        StringBuilder pathToProjects = new StringBuilder();
        pathToProjects.append( workspacePath );
        pathToProjects.append( WORKSPACE_IMPORTED_PROJECTS_PATH );
        File projectsFolder = new File( pathToProjects.toString() );
        if (projectsFolder.exists() && projectsFolder.isDirectory())
        {
            for (File f : projectsFolder.listFiles())
            {
                if (f.isDirectory())
                {
                    for (File child : f.listFiles())
                    {
                        if (child.getName().equals( ".location" ))
                        {// TODO:

                            try
                            {
                                SafeChunkyInputStream fileInputStream = new SafeChunkyInputStream( child );
                                DataInputStream dataInputStream = new DataInputStream( fileInputStream );
                                String file;
                                file = dataInputStream.readUTF().trim();
                                if (file.length() > 0)
                                {
                                    if (!file.startsWith( "URI//" ))
                                    {
                                        // throw new IOException( child.getAbsolutePath() +
                                        // " contains unexpected data: "
                                        // + file );
                                    }
                                    file = file.substring( "URI//file:/".length() );
                                    file = file.replace( "%20", " " );
                                    projectPaths.add( file );
                                }
                                dataInputStream.close();
                            }
                            catch (IOException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        }
    }

    // private ResourceSet resourceSet;
    // private List<Diagram> stateMachineDiagrams;
    //
    // private void updateStateMachines()
    // {
    // if (resourceSet == null)
    // {
    // resourceSet = new ResourceSetImpl();
    // }
    //
    // final String resourcePath = danaModel;
    // final Resource diResource = resourceSet.getResource( URI.createURI( resourcePath ), true );
    //
    // if (diResource != null)
    // {
    // final SashWindowsMngr sashWindowMngr = DiUtils.lookupSashWindowsMngr( diResource );
    // final PageList pageList = sashWindowMngr.getPageList();
    //
    // stateMachineDiagrams.clear();
    // for (final PageRef pageRef : pageList.getAvailablePage())
    // {
    // final EObject identifier = pageRef.getEmfPageIdentifier();
    // if (identifier instanceof Diagram)
    // {
    // final Diagram diagram = (Diagram)identifier;
    // if (diagram.getElement() instanceof StateMachine)
    // {
    // stateMachineDiagrams.add( diagram );
    // }
    // }
    // }
    // }
    // }
    public String[] getResultString()

    {
        int index = danaModel.indexOf( "di" );
        danaModel = danaModel.substring( 0, index );
        resultString = danaModel + "notation#" + stateMachineStringId;
        String[] result = new String[3];
        result[0] = resultString;
        result[1] = logfilePathString;
        result[2] = workspaceString;
        return result;
    }

    private void deleteprojects()
    {
        for (IProject p : projects)
        {
            try
            {
                p.delete( false, true, new NullProgressMonitor() );
                ResourcesPlugin.getWorkspace().getRoot().refreshLocal( IWorkspaceRoot.DEPTH_INFINITE,
                                                                       new NullProgressMonitor() );
            }
            catch (CoreException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void setMessages()
    {
        setTitle( "Model Validation" );
        setMessage( "Please select a file to store the selected runtime events and a model which have to validate the selected runtime event sequence." );
        getShell().setText( "Model Validation" );
    }
}
