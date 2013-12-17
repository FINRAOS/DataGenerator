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

import java.util.ListIterator;
import org.apache.log4j.Logger;

public class DiskListIterator<T> implements ListIterator<T> {

    private static final Logger log = Logger.getLogger(DiskListIterator.class);
    private final DiskList<T> instance;
    private int globalIndex;

    public DiskListIterator(DiskList<T> instance, int index) {
        this.instance = instance;
        this.globalIndex = index-1;
    }

    public synchronized void loadBlockContainingIndex(int globalIndex) {
        int block_index = globalIndex/instance.getBlockSize();
        if (block_index==instance.getCurrentBlock()) {
            return;
        }
        instance.saveBlock();
        if (block_index>instance.getExternalBlockHandles().size()-1) {
            instance.startNewBlock();
            return;
        }
        instance.loadBlock(block_index);
    }

    public int getLocalIndex() {
        int blockIndex = globalIndex/instance.getBlockSize();
        return globalIndex-(blockIndex*instance.getBlockSize());
    }

    @Override
    public void add(T e) {
        globalIndex++;
        loadBlockContainingIndex(globalIndex);
        int local_index = getLocalIndex();
        instance.getInternalBlock().insertElementAt(e, local_index);

        // Need to keep the last element as we iterate
        T last = null;

        // Need to enforce block size now (current block may be over-full after the insert)
        while (instance.getCurrentBlock()<(instance.getExternalBlockHandles().size()-1)){
            // Place last element from previous block as first in this block, if necessary
            if (last!=null) {
                instance.getInternalBlock().add(0, last);
            }

            // If the block is full, we need to push its last element to the next block
            // Should always occur until we are in the last block
            if (instance.getInternalBlock().size()>instance.getBlockSize()) {
                // Remove the last element from the current block
                last = instance.getInternalBlock().remove(instance.getInternalBlock().size()-1);

                // Save current block
                instance.saveBlock();

                if (instance.getCurrentBlock()!=instance.getExternalBlockHandles().size()-1) {
                    // If there is a next block, load it
                    int block_index = instance.getCurrentBlock()+1;
                    instance.loadBlock(block_index);
                } else {
                    // If not, create a new block and load that block
                    instance.startNewBlock();
                }
            } else {
                // We are in the last block and need to hop out
                break;
            }
            // Repeat until we get to the last block
        }

        instance.setSize(instance.getSize()+1);
    }

    @Override
    public boolean hasNext() {

        if (instance.getCurrentBlock()<instance.getExternalBlockHandles().size()-1) {
            // We are not affected by the last block, so we are ok
            return true;
        } else if (instance.getCurrentBlock()==instance.getExternalBlockHandles().size()-2) {
            // We are the next-to-last block, so make sure we are not the last element
            if (getLocalIndex()<instance.getInternalBlock().size()-1) {
                return true;
            } else {
                // If we are the last element, make sure there is at least one element
                // in the next block (the last block)
                instance.saveBlock();
                instance.loadBlock(instance.getCurrentBlock()+1);
                boolean decision = instance.getInternalBlock().size()>0;
                loadBlockContainingIndex(globalIndex);
                return decision;
            }
        }

        // We are in the last block, so make sure the current block has another element
        log.info("Returning from last block decision");
        return (getLocalIndex()<(instance.getInternalBlock().size()-1));
    }

    @Override
    public boolean hasPrevious() {
        if (instance.getCurrentBlock()>0) {
            // We are not in the first block, so we are ok
            return true;
        }

        // We are in the first block, so make sure the current block has a prior element
        return (this.getLocalIndex()>1);
    }

    @Override
    public T next() {
        if (globalIndex+1>instance.getSize()) {
            return null;
        }
        globalIndex++;
        loadBlockContainingIndex(globalIndex);
        int local_index = getLocalIndex();
        return instance.getInternalBlock().get(local_index);
    }

    @Override
    public int nextIndex() {
        return globalIndex+1;
    }

    @Override
    public T previous() {
        if (!hasPrevious()) {
            return null;
        }
        globalIndex--;
        loadBlockContainingIndex(globalIndex);
        int local_index = getLocalIndex();
        return instance.getInternalBlock().get(local_index);
    }

    @Override
    public int previousIndex() {
        return globalIndex-1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("You cannot remove elements from this list. I forbid it.");
    }

    @Override
    public void set(T e) {
        globalIndex++;
        loadBlockContainingIndex(globalIndex);
        int local_index = getLocalIndex();
        instance.getInternalBlock().remove(local_index);
        instance.getInternalBlock().insertElementAt(e, local_index);
    }
}
