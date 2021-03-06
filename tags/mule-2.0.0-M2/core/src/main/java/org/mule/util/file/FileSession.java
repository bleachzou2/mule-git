/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.util.file;

import org.mule.util.xa.ResourceManagerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public interface FileSession
{

    void begin() throws ResourceManagerException;

    void commit() throws ResourceManagerException;

    void rollback() throws ResourceManagerException;

    FileInputStream openInputStream(File f) throws IOException;

    FileOutputStream openOutputStream(File f, boolean append) throws IOException;

    FileOutputStream openOutputStream(File f) throws IOException;

    boolean mkdir(File f) throws IOException;

    RandomAccessFile openRandomAccess(File f, String mode) throws IOException;

    /**
     * Delete the given file.
     * 
     * @throw IllegalStateException if this transaction has already been committed or
     *        rolled back
     * @throw FileNotFoundException if the file does not exist
     * @throw DeleteException if the deletion fails
     * @throw TransactionException if there is a problem maintaining transaction
     *        information
     * @throw InconsistentStateException if this transaction cannot be restored to a
     *        consistent state (either no effect or all effects); failure of
     *        atomicity
     */
    void delete(File f) throws IOException;

    void copy(File source, File dest) throws IOException;

    void rename(File source, File dest) throws IOException;

}
