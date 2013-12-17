/*
 * (C) Copyright 2013 DataGenerator Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.finra.datagenerator.disklist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.Vector;
import org.apache.log4j.Logger;

public class DiskList<T> extends AbstractSequentialList<T> {

    private static final Logger log = Logger.getLogger(DiskList.class);
    private static int DEFAULT_BLOCK_SIZE = 1000;
    private static int counter = 0;

    private Vector<T> internalBlock;
    private Vector<String> externalBlockHandles;
    private int currentBlock;
    private int blockSize;
    private int size;

    public DiskList() {
        this.setBlockSize(DEFAULT_BLOCK_SIZE);
        this.externalBlockHandles = new Vector<String>();
        this.setSize(0);
        startNewBlock();
    }

    public DiskList(int blockSize) {
        this.setBlockSize(blockSize);
        this.externalBlockHandles = new Vector<String>();
        this.setSize(0);
        startNewBlock();
    }

    public DiskList(T o) {
        this.setBlockSize(DEFAULT_BLOCK_SIZE);
        this.externalBlockHandles = new Vector<String>();
        this.setSize(0);
        startNewBlock();
        this.add(o);
    }

    public DiskList(Iterable<T> os) {
        this.setBlockSize(DEFAULT_BLOCK_SIZE);
        this.externalBlockHandles = new Vector<String>();
        this.setSize(0);
        startNewBlock();
        for(T o : os){
            this.add(o);
        }
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new DiskListIterator<T>(this, index);
    }

    @Override
    public int size() {
        return getSize();
    }

    public Vector<T> getInternalBlock() {
        return internalBlock;
    }

    public void setInternalBlock(Vector<T> internalBlock) {
        this.internalBlock = internalBlock;
    }

    public Vector<String> getExternalBlockHandles() {
        return externalBlockHandles;
    }

    public void setExternalBlockHandles(Vector<String> externalBlockHandles) {
        this.externalBlockHandles = externalBlockHandles;
    }

    public int getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(int currentBlock) {
        this.currentBlock = currentBlock;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    @SuppressWarnings("unchecked")
    public void loadBlock(int block_index) {
        try {
            FileInputStream filein = new FileInputStream(externalBlockHandles.get(block_index));
            ObjectInputStream in = new ObjectInputStream(filein);
            Vector<T> obj = (Vector<T>) in.readObject();
            in.close();

            setCurrentBlock(block_index);
            setInternalBlock(obj);
        }
        catch (Exception e) {
            log.error("There was a problem loading a block", e);
        }
    }

    public void startNewBlock() {
        // Initialize block
        setInternalBlock(new Vector<T>());

        // Create handle
        String filename = getNewFilename();
        getExternalBlockHandles().add(filename);

        // Get index
        setCurrentBlock(getExternalBlockHandles().size()-1);
    }

    public synchronized String getNewFilename() {
        counter++;
        String filename = counter+".dat";

        File f = new File(filename);
        f.deleteOnExit();

        return filename;
    }

    public void saveBlock() {
        log.info("Saving block "+currentBlock);

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(externalBlockHandles.get(currentBlock)));
            out.writeObject(internalBlock);
            out.close();
        }
        catch (Exception e) {
            log.error("Something went wrong when saving a block.", e);
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
