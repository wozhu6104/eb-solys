/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptInfoChangedListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptMethodLoader;
import com.elektrobit.ebsolys.script.external.Execute;
import com.elektrobit.ebsolys.script.external.Filter;

import lombok.extern.log4j.Log4j;

@Component(service = {ScriptMethodLoader.class}, immediate = true)
@Log4j
public class ScriptMethodLoaderImpl implements ScriptMethodLoader
{

    @Override
    public void reloadScriptMethods(List<RaceScriptInfo> scripts)
    {
        for (RaceScriptInfo scriptData : scripts)
        {
            updateMethodsInScriptModel( scriptData );
        }
    }

    private void updateMethodsInScriptModel(RaceScriptInfo scriptInfo)
    {
        RaceScriptImpl scriptImpl = (RaceScriptImpl)scriptInfo;
        Class<?> raceScriptClass = loadScriptClass( scriptImpl, false );
        if (raceScriptClass != null)
        {
            scriptImpl.clearMethodLists();
            try
            {
                List<Method> allMethods = new ArrayList<Method>( Arrays
                        .asList( raceScriptClass.getDeclaredMethods() ) );
                List<RaceScriptMethod> allRaceScriptMethods = RaceScriptMethodFilter
                        .getAllMethods( raceScriptClass, scriptInfo.getName() );
                List<RaceScriptMethod> remainingExecuteMethods = RaceScriptMethodFilter
                        .getMethodsForAnnotationClass( allRaceScriptMethods, Execute.class );

                List<RaceScriptMethod> channelListContextMethods = RaceScriptMethodFilter
                        .filterPreselectionMethodsWithListOfChannels( remainingExecuteMethods );

                List<RaceScriptMethod> timeMarkerContextMethods = RaceScriptMethodFilter
                        .filterPreselectionMethodsWithTimeMarker( remainingExecuteMethods );

                List<RaceScriptMethod> runtimeEventContextMethods = RaceScriptMethodFilter
                        .filterPreselectionMethodsWithRuntimeEvent( remainingExecuteMethods );

                List<RaceScriptMethod> timeMarkerListContextMethods = RaceScriptMethodFilter
                        .filterPreselectionMethodsWithListOfTimeMarker( remainingExecuteMethods );

                List<RaceScriptMethod> runtimeEventListContextMethods = RaceScriptMethodFilter
                        .filterPreselectionMethodsWithRuntimeEvents( remainingExecuteMethods );

                List<RaceScriptMethod> channelContextMethods = RaceScriptMethodFilter
                        .filterPreselectionMethodsWithChannel( remainingExecuteMethods );

                List<RaceScriptMethod> globalContextMethods = RaceScriptMethodFilter
                        .filterGlobalExecutionMethods( remainingExecuteMethods );

                List<RaceScriptMethod> callbackMethods = RaceScriptMethodFilter
                        .filterCallbackMethod( remainingExecuteMethods );

                List<RaceScriptMethod> remainingFilterMethods = RaceScriptMethodFilter
                        .getMethodsForAnnotationClass( allRaceScriptMethods, Filter.class );

                List<RaceScriptMethod> filterMethods = RaceScriptMethodFilter
                        .filterFilterMethods( remainingFilterMethods );

                List<String> injectedParams = RaceScriptMethodFilter.collectInjectedParamNames( raceScriptClass );

                scriptImpl.setAllMethods( allMethods );
                scriptImpl.setGlobalContextMethods( globalContextMethods );
                scriptImpl.setCallbackMethods( callbackMethods );
                scriptImpl.setTimeMarkerContextMethods( timeMarkerContextMethods );
                scriptImpl.setTimeMarkerListContextMethods( timeMarkerListContextMethods );
                scriptImpl.setChannelContextMethods( channelContextMethods );
                scriptImpl.setChannelListContextMethods( channelListContextMethods );
                scriptImpl.setRuntimeEventContextMethods( runtimeEventContextMethods );
                scriptImpl.setRuntimeEventListContextMethods( runtimeEventListContextMethods );
                scriptImpl.setFilterMethods( filterMethods );
                scriptImpl.setInjectedParams( injectedParams );

                notifyFilterMethodsChanged( scriptImpl, filterMethods );
                logNotCategorizedMethods( scriptImpl, remainingExecuteMethods, remainingFilterMethods );
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            finally
            {
                closeClassloaderToUnlockJar( raceScriptClass.getClassLoader() );
            }
        }
    }

    private void logNotCategorizedMethods(RaceScriptImpl scriptImpl, List<RaceScriptMethod> remainingExecuteMethods,
            List<RaceScriptMethod> remainingFilterMethods)
    {
        List<RaceScriptMethod> allRemainingMethods = new ArrayList<RaceScriptMethod>();
        allRemainingMethods.addAll( remainingExecuteMethods );
        allRemainingMethods.addAll( remainingFilterMethods );

        for (RaceScriptMethod method : allRemainingMethods)
        {
            scriptImpl.getConsole()
                    .println( "WARN: Method '" + method.getMethod().getName() + "' has wrong method signature." );
        }
    }

    private void notifyFilterMethodsChanged(RaceScriptImpl scriptImpl, List<RaceScriptMethod> filterMethods)
    {
        new OSGIWhiteBoardPatternCaller<RaceScriptInfoChangedListener>( RaceScriptInfoChangedListener.class )
                .callOSGIService( new OSGIWhiteBoardPatternCommand<RaceScriptInfoChangedListener>()
                {
                    @Override
                    public void callOSGIService(RaceScriptInfoChangedListener listener)
                    {
                        listener.filterMethodsChanged( scriptImpl, filterMethods );
                    }
                } );
    }

    private Class<?> loadScriptClass(RaceScriptImpl scriptImpl, boolean copyJar)
    {
        String scriptName = scriptImpl.getName();
        File runScriptJar = new File( scriptImpl.getPathToBinJarFile() );

        URL[] urlsToBinFolders = new URL[1];
        try
        {
            urlsToBinFolders[0] = runScriptJar.toURI().toURL();
        }
        catch (MalformedURLException e1)
        {
            log.error( "Incorrect URL for class for script " + scriptName + ". URL was " + urlsToBinFolders[0] );
        }

        URLClassLoader classLoader = new URLClassLoader( urlsToBinFolders, this.getClass().getClassLoader() );

        Class<?> raceScriptToLoad = loadScriptFromClassLoader( scriptImpl, classLoader );
        if (raceScriptToLoad == null)
        {
            log.error( "Couldn't find class for script " + scriptName + ". Path " + scriptImpl.getPathToBinJarFile() );
        }
        return raceScriptToLoad;
    }

    private Class<?> loadScriptFromClassLoader(RaceScriptImpl scriptImpl, URLClassLoader child)
    {
        Class<?> raceScriptToLoad = tryToLoadScriptClassFromUserScripts( scriptImpl, child );

        if (raceScriptToLoad == null)
        {
            raceScriptToLoad = tryToLoadScriptFromPreinstalledScripts( scriptImpl, child, raceScriptToLoad );
        }
        return raceScriptToLoad;
    }

    private Class<?> tryToLoadScriptClassFromUserScripts(RaceScriptImpl scriptImpl, URLClassLoader child)
    {
        Class<?> raceScriptToLoad = null;
        try
        {
            raceScriptToLoad = Class.forName( scriptImpl.getName(), true, child );
        }
        catch (ClassNotFoundException e)
        {
            log.info( "Couldn't find class for script " + scriptImpl.getName()
                    + " in preinstalled scripts folder. Path " + scriptImpl.getPathToBinJarFile() );
        }
        return raceScriptToLoad;
    }

    private Class<?> tryToLoadScriptFromPreinstalledScripts(RaceScriptImpl scriptImpl, URLClassLoader child,
            Class<?> raceScriptToLoad)
    {
        try
        {
            raceScriptToLoad = Class.forName( "api." + scriptImpl.getName(), true, child );
        }
        catch (ClassNotFoundException e)
        {
            log.info( "Couldn't find class for script " + scriptImpl.getName() + " in user scripts folder. Path "
                    + scriptImpl.getPathToBinJarFile() );
        }
        return raceScriptToLoad;
    }

    private void closeClassloaderToUnlockJar(ClassLoader classLoaderToClose)
    {
        try
        {
            Field urlClassPath = URLClassLoader.class.getDeclaredField( "ucp" );
            urlClassPath.setAccessible( true );
            Object sunMiscURLClassPath = urlClassPath.get( classLoaderToClose );

            Field loaders = sunMiscURLClassPath.getClass().getDeclaredField( "loaders" );
            loaders.setAccessible( true );

            for (Object sunMiscURLClassPathJarLoader : ((Collection<?>)loaders.get( sunMiscURLClassPath )).toArray())
            {
                try
                {
                    Field loader = sunMiscURLClassPathJarLoader.getClass().getDeclaredField( "jar" );
                    loader.setAccessible( true );
                    Object jarFile = loader.get( sunMiscURLClassPathJarLoader );
                    ((JarFile)jarFile).close();
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

}
