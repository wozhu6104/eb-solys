/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.memory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.SnapshotFactory;
import org.eclipse.mat.util.VoidProgressListener;

public class MemorySnapshotHelper
{
    private final String pathToSnapshot;
    private ISnapshot snapshot;
    private File tempDirectory;

    public MemorySnapshotHelper(String pathToSnapshot)
    {
        createTempFolder();
        this.pathToSnapshot = tempDirectory.getAbsolutePath() + "/" + pathToSnapshot;
    }

    private void createTempFolder()
    {
        tempDirectory = null;
        try
        {
            tempDirectory = createTempDirectory();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private File createTempDirectory() throws IOException
    {
        final File temp;

        temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );

        System.out.println( temp.getAbsolutePath() );

        if (!(temp.delete()))
        {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }

        if (!(temp.mkdir()))
        {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }

        return (temp);
    }

    public void makeMemorySnapshot() throws RuntimeException
    {
        HeapDumper.dumpLiveHeapObjectToFile( pathToSnapshot );
        try
        {
            loadSnapshot();
        }
        catch (SnapshotException e)
        {
            // It's ok to print here only stacktrace,
            // because it's only used in test code.
            e.printStackTrace();
        }
    }

    private void loadSnapshot() throws SnapshotException
    {
        File snapshotAsFile = new File( pathToSnapshot );
        snapshot = SnapshotFactory.openSnapshot( snapshotAsFile,
                                                 new HashMap<String, String>(),
                                                 new VoidProgressListener() );
    }

    public long getHeapSize()
    {
        long heapSize = snapshot.getSnapshotInfo().getUsedHeapSize();
        return heapSize;
    }

    public void closeAndDeleteSnapshot()
    {
        closeSnapshot( snapshot );
        deleteSnapshot( pathToSnapshot );
    }

    private void closeSnapshot(ISnapshot snapshot)
    {
        SnapshotFactory.dispose( snapshot );
    }

    private void deleteSnapshot(String pathToSnapshot)
    {
        if (tempDirectory.exists())
        {
            String[] entries = tempDirectory.list();
            for (String s : entries)
            {
                File currentFile = new File( tempDirectory.getPath(), s );
                currentFile.delete();
            }
        }

    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        if (tempDirectory.exists())
            tempDirectory.delete();
    }

}
