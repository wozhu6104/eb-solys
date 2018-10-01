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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.app.racescriptexecutor.caller.RaceScriptCaller;
import com.elektrobit.ebrace.app.racescriptexecutor.caller.impl.CallbackScriptCaller;
import com.elektrobit.ebrace.app.racescriptexecutor.caller.impl.DefaultRaceScriptCaller;
import com.elektrobit.ebrace.app.racescriptexecutor.caller.impl.FilterScriptCaller;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.FilterScriptCallerListener;
import com.elektrobit.ebrace.core.interactor.api.script.InjectedParamsCallback;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptInfoChangedListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutionListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptMethodLoader;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.script.external.ScriptContext;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class ScriptExecutorServiceImpl implements ScriptExecutorService
{

    private final GenericListenerCaller<ScriptExecutionListener> listeners = new GenericListenerCaller<ScriptExecutionListener>();
    private ScriptMethodLoader scriptLoader;
    private InjectedParamsCallback callback;

    @Reference
    public void bindScriptLoader(ScriptMethodLoader scriptLoader)
    {
        this.scriptLoader = scriptLoader;
    }

    public void unbindScriptLoader(ScriptMethodLoader scriptLoader)
    {
        this.scriptLoader = null;
    }

    @Override
    public void runScriptWithGlobalMethod(RaceScriptInfo script, String methodName)
    {
        runScriptInNewThread( (RaceScriptImpl)script, new DefaultRaceScriptCaller(), methodName );
    }

    @Override
    public void runScriptWithGlobalMethodAndParams(RaceScriptInfo script, String methodName, Object... params)
    {
        runScriptInNewThread( (RaceScriptImpl)script, new DefaultRaceScriptCaller(), methodName, params );
    }

    @Override
    public void runScriptWithRuntimeEventPreselection(RaceScriptInfo script, String methodName, RuntimeEvent<?> event)
    {
        runScriptInNewThread( (RaceScriptImpl)script, new DefaultRaceScriptCaller(), methodName, event );
    }

    @Override
    public void runScriptWithTimeMarkerPreselection(RaceScriptInfo script, String timeMarkerMethodName,
            TimeMarker timeMarker)
    {
        runScriptInNewThread( (RaceScriptImpl)script, new DefaultRaceScriptCaller(), timeMarkerMethodName, timeMarker );
    }

    @Override
    public void runScriptWithChannelPreselection(RaceScriptInfo script, String methodName,
            RuntimeEventChannel<?> channel)
    {
        runScriptInNewThread( (RaceScriptImpl)script, new DefaultRaceScriptCaller(), methodName, channel );
    }

    @Override
    public void runScriptWithChannelListPreselection(RaceScriptInfo script, String methodName,
            List<RuntimeEventChannel<?>> channels)
    {
        runScriptInNewThread( (RaceScriptImpl)script, new DefaultRaceScriptCaller(), methodName, channels );
    }

    @Override
    public void runScriptWithTimeMarkerListPreselection(RaceScriptInfo script, String methodName,
            List<TimeMarker> timeMarkers)
    {
        runScriptInNewThread( (RaceScriptImpl)script, new DefaultRaceScriptCaller(), methodName, timeMarkers );
    }

    @Override
    public void runScriptWithRuntimeEventsPreselection(RaceScriptInfo script, String methodName,
            List<RuntimeEvent<?>> events)
    {
        runScriptInNewThread( (RaceScriptImpl)script,
                              new DefaultRaceScriptCaller(),
                              methodName,
                              new ArrayList<RuntimeEvent<?>>( events ) );
    }

    @Override
    public void runCallbackScript(RaceScriptInfo script, String methodName)
    {
        CallbackScriptCaller callbackScriptCaller = new CallbackScriptCaller();
        RaceScriptImpl scriptImpl = (RaceScriptImpl)script;
        scriptImpl.setCallbackScriptCaller( callbackScriptCaller );
        runScriptInNewThread( scriptImpl, callbackScriptCaller, methodName, new ArrayList<RuntimeEvent<?>>() );
    }

    @Override
    public void runFilterScript(RaceScriptInfo script, RaceScriptMethod method, List<RuntimeEvent<?>> eventsToFilter,
            FilterScriptCallerListener resultListener, SEARCH_MODE searchMode, List<RowFormatter> allRowFormatters)
    {
        FilterScriptCaller filterScriptCaller = new FilterScriptCaller( eventsToFilter,
                                                                        resultListener,
                                                                        searchMode,
                                                                        allRowFormatters );
        runScriptInNewThread( (RaceScriptImpl)script, filterScriptCaller, method.getMethodName() );
    }

    @Override
    public void stopScript(RaceScriptInfo script)
    {
        RaceScriptImpl scriptImpl = (RaceScriptImpl)script;
        while (scriptImpl.runtimeEventsWaitingToBeProcessedByCallback())
        {
            try
            {
                Thread.sleep( 100 );
            }
            catch (InterruptedException e)
            {
            }
        }

        Thread scriptExecutionThread = scriptImpl.getScriptExecutionThread();
        if (scriptExecutionThread != null && scriptExecutionThread.isAlive())
        {
            scriptExecutionThread.stop();
        }
    }

    @UseStatLog(UseStatLogTypes.SCRIPT_EXECUTION_STARTED)
    private void runScriptInNewThread(RaceScriptImpl raceScript, final RaceScriptCaller raceScriptCaller,
            final String executeMethod, final Object... params)
    {
        if (!raceScript.isRunning())
        {
            final Class<?> raceScriptClass = loadScriptClass( raceScript, true );
            if (raceScriptClass != null)
            {
                final Object raceScriptObject = instantiateRaceScriptObject( raceScript, raceScriptClass );
                Thread scriptExecutionThread = new Thread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            raceScript.getConsole().clear();
                            if (callback == null)
                            {
                                System.out.println( "no callback" );
                            }
                            if (raceScript.hasInjectedParameters() && callback != null)
                            {
                                System.out.println( "start executing: " + raceScriptObject.getClass().getName() );
                                String calledWithParams = callback.onParamsRequested( raceScript, raceScriptObject );
                                raceScript.getConsole()
                                        .println( "Running script with parameters:\n" + calledWithParams );
                            }
                            listeners.notifyListeners( (listener) -> listener.onScriptStarted( raceScript ) );
                            scriptStarted( raceScript, raceScriptCaller );
                            raceScriptCaller.callBeforeMethod( raceScript.getName(), raceScriptObject );
                            raceScriptCaller
                                    .callScript( raceScript.getName(), raceScriptObject, executeMethod, params );
                        }
                        catch (RuntimeException e)
                        {
                            if (mustBeLogged( e ))
                            {
                                logExceptionInScriptConsole( raceScript, executeMethod, e );
                            }
                        }
                        finally
                        {
                            callAfterMethod( raceScript, raceScriptCaller, executeMethod, raceScriptObject );
                            scriptStopped( raceScript );
                            listeners.notifyListeners( (listener) -> listener.onScriptStopped( raceScript ) );
                            raceScript.setScriptExecutionThread( null );
                            closeClassloaderToUnlockJar( raceScriptClass.getClassLoader() );

                            if (raceScript.getCallbackScriptCaller() != null)
                            {
                                raceScript.getCallbackScriptCaller().dispose();
                                raceScript.setCallbackScriptCaller( null );
                            }
                        }
                    }
                } );

                if (raceScriptObject == null)
                {
                    return;
                }

                raceScript.setScriptExecutionThread( scriptExecutionThread );
                scriptExecutionThread.start();
            }
        }
    }

    private Class<?> loadScriptClass(RaceScriptImpl raceScript, boolean copyJar)
    {
        File runScriptJar = new File( raceScript.getPathToBinJarFile() );

        if (copyJar)
        {
            runScriptJar = copyScriptRunJar( runScriptJar );
        }

        if (runScriptJar != null)
        {
            URL[] urlsToBinFolders = new URL[1];
            try
            {
                urlsToBinFolders[0] = runScriptJar.toURI().toURL();
            }
            catch (MalformedURLException e1)
            {
                log.error( "Incorrect URL for class for script " + raceScript.getName() + ". URL was "
                        + urlsToBinFolders[0] );
            }

            URLClassLoader classLoader = new URLClassLoader( urlsToBinFolders, this.getClass().getClassLoader() );

            Class<?> raceScriptToLoad = loadScriptFromClassLoader( raceScript, classLoader );
            if (raceScriptToLoad == null)
            {
                log.error( "Couldn't find class for script " + raceScript.getName() + ". Path "
                        + raceScript.getPathToBinJarFile() );
            }
            return raceScriptToLoad;
        }

        return null;
    }

    private File copyScriptRunJar(File orgScriptJar)
    {
        String runScriptJarPath = RaceScriptHelper.createRunJarFilePath( orgScriptJar.getAbsolutePath() );
        File runScriptJar = new File( runScriptJarPath );
        try
        {
            FileUtils.copyFile( orgScriptJar, runScriptJar );
        }
        catch (IOException e)
        {
            log.warn( "Couldn't create run script jar. Error: " + e.getMessage() );
            return null;
        }
        return runScriptJar;
    }

    private Class<?> loadScriptFromClassLoader(RaceScriptImpl raceScript, URLClassLoader child)
    {
        Class<?> raceScriptToLoad = tryToLoadScriptClassFromUserScripts( raceScript, child );

        if (raceScriptToLoad == null)
        {
            raceScriptToLoad = tryToLoadScriptFromPreinstalledScripts( raceScript, child, raceScriptToLoad );
        }
        return raceScriptToLoad;
    }

    private Class<?> tryToLoadScriptClassFromUserScripts(RaceScriptImpl raceScript, URLClassLoader child)
    {
        Class<?> raceScriptToLoad = null;
        try
        {
            raceScriptToLoad = Class.forName( raceScript.getName(), true, child );
        }
        catch (ClassNotFoundException e)
        {
            log.info( "Couldn't find class for script " + raceScript.getName()
                    + " in preinstalled scripts folder. Path " + raceScript.getPathToBinJarFile() );
        }
        return raceScriptToLoad;
    }

    private Class<?> tryToLoadScriptFromPreinstalledScripts(RaceScriptImpl raceScript, URLClassLoader child,
            Class<?> raceScriptToLoad)
    {
        try
        {
            raceScriptToLoad = Class.forName( "api." + raceScript.getName(), true, child );
        }
        catch (ClassNotFoundException e)
        {
            log.info( "Couldn't find class for script " + raceScript.getName() + " in user scripts folder. Path "
                    + raceScript.getPathToBinJarFile() );
        }
        return raceScriptToLoad;
    }

    private Object instantiateRaceScriptObject(RaceScriptImpl raceScript, Class<?> raceScriptToLoad)
    {
        Object raceScriptObject = null;
        Constructor<?> cons = null;
        try
        {
            cons = raceScriptToLoad.getConstructor( ScriptContext.class );
            raceScriptObject = cons.newInstance( raceScript.getRaceScriptContext() );
        }
        catch (Exception e)
        {
            log.error( "Couldn't instantiate script " + raceScript.getName() + ". Exception was " + e.getMessage() );
            if (cons == null)
            {
                logExceptionInScriptConsole( raceScript, "Constructor", e );
            }
            else
            {
                logExceptionInScriptConsole( raceScript, cons.getName(), e );
            }
        }

        return raceScriptObject;
    }

    private void logExceptionInScriptConsole(RaceScriptImpl raceScript, String nameOfMethod, Exception e)
    {
        if (e.getCause() == null)
        {
            logDirectException( raceScript, e );
            return;
        }

        // ThreadDeath exception caused by force stopping script should not be displayed to user
        if (e.getCause().getClass().equals( ThreadDeath.class ))
        {
            return;
        }

        raceScript.getConsole().println( "Exception in script execution in method '" + nameOfMethod + "': " );

        if (e.getCause() instanceof InvocationTargetException)
        {
            raceScript.getConsole().println( getStackTraceAsString( (InvocationTargetException)e.getCause() ) );
        }
        else
        {
            for (StackTraceElement nextStackTraceElement : e.getCause().getStackTrace())
            {
                raceScript.getConsole().println( "\t" + nextStackTraceElement.toString() );
            }
        }
    }

    private void logDirectException(RaceScriptImpl raceScript, Exception e)
    {
        String result = ExceptionUtils.getStackTrace( e );
        raceScript.getConsole().println( result );
    }

    private String getStackTraceAsString(InvocationTargetException e)
    {
        Throwable targetException = e.getTargetException();
        String result = ExceptionUtils.getStackTrace( targetException );
        return result;
    }

    private void scriptStarted(RaceScriptImpl raceScript, RaceScriptCaller raceScriptCaller)
    {
        raceScript.setScriptRunning( true );
        if (raceScriptCaller instanceof CallbackScriptCaller)
        {
            raceScript.setRunningAsCallbackScript( true );
        }
        notifyScriptStatusChanged( raceScript );
    }

    private void scriptStopped(RaceScriptImpl raceScript)
    {
        raceScript.setScriptRunning( false );
        raceScript.setRunningAsCallbackScript( false );
        scriptLoader.reloadScriptMethods( Arrays.asList( raceScript ) );
        notifyScriptStatusChanged( raceScript );
    }

    private void notifyScriptStatusChanged(RaceScriptImpl raceScript)
    {
        new OSGIWhiteBoardPatternCaller<RaceScriptInfoChangedListener>( RaceScriptInfoChangedListener.class )
                .callOSGIService( new OSGIWhiteBoardPatternCommand<RaceScriptInfoChangedListener>()
                {
                    @Override
                    public void callOSGIService(RaceScriptInfoChangedListener listener)
                    {
                        listener.scriptInfoChanged( raceScript );
                    }
                } );
    }

    private boolean mustBeLogged(RuntimeException e)
    {
        if (e.getCause() instanceof InvocationTargetException)
        {
            InvocationTargetException i = (InvocationTargetException)e.getCause();
            if (i.getTargetException() instanceof ThreadDeath)
            {
                return false;
            }
        }
        return true;
    }

    private void callAfterMethod(RaceScriptImpl raceScript, final RaceScriptCaller raceScriptCaller,
            final String executeMethod, final Object raceScriptObject)
    {
        try
        {
            raceScriptCaller.callAfterMethod( raceScript.getName(), raceScriptObject );
        }
        catch (RuntimeException e)
        {
            if (mustBeLogged( e ))
            {
                logExceptionInScriptConsole( raceScript, "@AfterRaceScript", e );
            }
        }
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

    @Override
    public void addScriptExecutionListener(ScriptExecutionListener scriptExecutionListener)
    {
        listeners.add( scriptExecutionListener );
    }

    @Override
    public void removeScriptExecutionListener(ScriptExecutionListener scriptExecutionListener)
    {
        listeners.remove( scriptExecutionListener );
    }

    @Override
    public void setInjectedParamsCallback(InjectedParamsCallback callback)
    {
        this.callback = callback;
    }

}
