/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.scriptcompiler.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.xtend.core.XtendInjectorSingleton;
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.dev.debug.annotations.api.EnterExitPrinter;
import com.elektrobit.ebrace.dev.debug.annotations.api.InterceptMethod;
import com.google.inject.Injector;

import lombok.extern.log4j.Log4j;

@Log4j
@SuppressWarnings("restriction")
public class ScriptGenerator implements BundleActivator, IResourceChangeListener
{

    @Override
    public void start(BundleContext context) throws Exception
    {
        ResourcesPlugin.getWorkspace().addResourceChangeListener( this, IResourceChangeEvent.POST_BUILD );
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        List<File> changedClassFiles = new ArrayList<>();
        try
        {
            event.getDelta().accept( new IResourceDeltaVisitor()
            {

                @Override
                public boolean visit(IResourceDelta delta) throws CoreException
                {
                    File changedFile = delta.getResource().getFullPath().toFile();
                    // FIXME rage2903: src contains is unsafe.
                    if (changedFile.getAbsolutePath().contains( "src" ))
                    {
                        changedClassFiles.add( changedFile );
                    }
                    return true;
                }
            } );
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }

        if (!changedClassFiles.isEmpty())
        {
            generateJavaFilesHeadless();

        }
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    private void generateJavaFilesHeadless()
    {
        if (getProject().exists())
        {
            String cp = "";
            IClasspathEntry[] resolvedClasspath;
            try
            {

                resolvedClasspath = JavaCore.create( getProject() ).getResolvedClasspath( true );
                for (IClasspathEntry classpathEntry : resolvedClasspath)
                {
                    // No source directories
                    if (classpathEntry.getContentKind() == IPackageFragmentRoot.K_BINARY)
                    {
                        cp += classpathEntry.getPath().toOSString() + File.pathSeparator;
                    }
                }
            }
            catch (JavaModelException e)
            {
                e.printStackTrace();
            }

            String srcPath = getProject().getFolder( ScriptConstants.USER_SCRIPT_SOURCE_FOLDER_NAME ).getLocation()
                    .toFile().getAbsolutePath();

            Injector injector = XtendInjectorSingleton.INJECTOR;
            XtendBatchCompiler compiler = injector.getInstance( XtendBatchCompiler.class );

            compiler.setUseCurrentClassLoaderAsParent( true );
            compiler.setClassPath( cp );
            compiler.setJavaSourceVersion( "1.8" );
            compiler.setFileEncoding( "UTF-8" );
            String outputPath = getProject().getFolder( ScriptConstants.XTENDGEN_FOLDER ).getLocation().toFile()
                    .getAbsolutePath();
            compiler.setOutputPath( outputPath );
            compiler.setSourcePath( srcPath );

            boolean compile = compiler.compile();

            if (compile)
            {
                log.info( "Xtend files generated!" );
            }
            else
            {
                log.warn( "Xtend files NOT generated!" );
            }

            try
            {
                IFolder folder = getProject().getFolder( ScriptConstants.XTENDGEN_FOLDER );
                folder.refreshLocal( IProject.DEPTH_INFINITE, null );
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }

    }

    private IProject getProject()
    {
        return ResourcesPlugin.getWorkspace().getRoot().getProject( "RaceScripts" );
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener( this );
    }
}
