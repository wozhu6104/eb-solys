/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.elektrobit.ebrace.targetdata.importer.internal.racefile.RaceFileImporter;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;

public class RaceFileImporterTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void fileTooBig()
    {
        File mockedFile = Mockito.mock( File.class );

        long maxFileSizeMB = 123;
        long tooBigFileSizeB = (maxFileSizeMB + 2) * RaceFileImporter.BYTES_TO_MB;

        Mockito.when( mockedFile.length() ).thenReturn( tooBigFileSizeB );

        RaceFileImporter sut = new RaceFileImporter();
        FileSizeLimitService mockedMaxSizeService = Mockito.mock( FileSizeLimitService.class );
        Mockito.when( mockedMaxSizeService.getMaxSolysFileSizeMB() ).thenReturn( maxFileSizeMB );
        sut.bind( mockedMaxSizeService );
        Assert.assertTrue( sut.isFileTooBig( mockedFile ) );
    }

    @Test
    public void fileNotToBig()
    {
        File mockedFile = Mockito.mock( File.class );

        long maxFileSizeMB = 123;
        long tooBigFileSizeB = (maxFileSizeMB - 2) * RaceFileImporter.BYTES_TO_MB;

        Mockito.when( mockedFile.length() ).thenReturn( tooBigFileSizeB );

        RaceFileImporter sut = new RaceFileImporter();
        FileSizeLimitService mockedMaxSizeService = Mockito.mock( FileSizeLimitService.class );
        Mockito.when( mockedMaxSizeService.getMaxSolysFileSizeMB() ).thenReturn( maxFileSizeMB );
        sut.bind( mockedMaxSizeService );
        Assert.assertFalse( sut.isFileTooBig( mockedFile ) );
    }

}
