/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.importerregistry.api;

import java.io.File;
import java.io.IOException;

public interface Importer
{
    public void importFile(File file) throws IOException;

    public void importFrom(long startTimestamp, Long desiredChunkLengthTime, File file) throws IOException;

    public void setLoadFileProgressListener(ImporterProgressListener loadFileProgressListener);

    public boolean isFileTooBig(File file);

    public void cancelImport();

    /**
     * @return The file extension that is supported by the importer, e.g. "csv"
     */
    public String getSupportedFileExtension();

    /**
     * @return Name of file type that is supported by the importer, e.g. "CSV File"
     */
    public String getSupportedFileTypeName();

    public Long getFileStartTime();

    public Long getFileEndTime();

    public Long getChunkStartTime();

    public Long getChunkEndTime();

    public boolean isChunkLoadingSupported();

    /**
     * After the import method returns, this method is called to check the status. If loading was not at least partially
     * successful, the file resource model will not be visible in UI.
     * 
     * @return True if loading was at least partially successful, otherwise false;
     */
    public boolean isLoadingAtLeastPartiallySuccessful();
}
