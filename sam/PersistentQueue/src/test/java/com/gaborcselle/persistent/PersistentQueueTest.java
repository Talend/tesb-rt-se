/*******************************************************************************
 * Copyright (c) 2005 Gabor Cselle.
 * 
 * The author can be reached at:
 * Gabor Cselle (mail at gaborcselle dot com)
 * 
 * Redistribution and use in source and binary forms with or without
 * modification are permitted provided that source distributions retain this
 * entire copyright notice and comment.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.gaborcselle.persistent;

import java.io.File;

import junit.framework.TestCase;

/**
 * Unit test for {@link com.gaborcselle.persistent.PersistentQueue}.
 * 
 * @author Gabor Cselle
 * @version 1.0
 */
public class PersistentQueueTest extends TestCase {

    private PersistentQueue<PersistentQueueTestEntry> pqueue;
	private String fileName;

    /** 
     * Set up unit test - delete the test file if it already exists,
     * instantiate PersistentQueue.
     */
    protected void setUp() throws Exception {
        super.setUp();
        File tempFile = File.createTempFile("persistentTest", "temp");
        fileName = tempFile.getAbsolutePath();
        tempFile.delete();
        
        pqueue = new PersistentQueue<PersistentQueueTestEntry>(fileName);
    }
    
    /** Test adding and removing a single element. */
    public void testOneElement() throws Exception {        
        pqueue.clear();
        pqueue.add(new PersistentQueueTestEntry ("one"));
        PersistentQueueTestEntry entry = pqueue.remove();
        if (!entry.content.equals("one")) {
            fail("Added string != string removed");
        }        
    }
    
    /** Test adding and removing a lot of elements. */
    public void testALotOfElements() throws Exception {
        pqueue.clear();
        
        // add a hundred elements
        for (int i = 0; i < 100; i++) {
            pqueue.add(new PersistentQueueTestEntry(String.valueOf(i)));
        }
        
        // and remove a hundred elements
        for (int i = 0; i < 100; i++) {
            PersistentQueueTestEntry entry = pqueue.remove();
            assertEquals(entry.content, String.valueOf(i)); 
        }
    }
    
    /** Test the method {@link PersistentQueue#size()} */
    public void testSize() throws Exception {
        pqueue.clear();
        
        // queue is now empty - isEmpty should return true
        assertTrue(pqueue.isEmpty());

        // add three elements
        for (int i = 0; i < 3; i++) {
            pqueue.add(new PersistentQueueTestEntry(String.valueOf(i)));
        }
        
        // check that element number is correct
        assertEquals(pqueue.size(),3);
        
        // queue is now filled - isEmpty should return false
        assertFalse(pqueue.isEmpty());
    }
    
    /** Test what happens when removing from an empty queue. */
    public void testRemoveFromEmptyQueue() throws Exception {
        pqueue.clear();
        
        assertEquals(pqueue.remove(), null);
    }
    
    /** Simulate a crash and reload of the PersistentQueue. */
    public void testRecoverFromLost() throws Exception {
        pqueue.clear();
        pqueue.add(new PersistentQueueTestEntry ("one"));
        pqueue.add(new PersistentQueueTestEntry ("two"));
        
        // lose pqueue now (e.g. because of system crash) and create a new one
        pqueue = new PersistentQueue<PersistentQueueTestEntry>(fileName);
    }
    
    /** 
     * Write 20 elements, remove 10, then lose PersistentQueue.
     * Also forces a defragment because it sets deframentInterval to 9. 
     * */
    public void testWriteList() throws Exception {
        pqueue.clear();
        
        pqueue = new PersistentQueue<PersistentQueueTestEntry>(fileName, 9);
        // add 20, remove 10
        for (int i = 0; i < 10; i++) {
            pqueue.add(new PersistentQueueTestEntry(String.valueOf(i)));
            pqueue.add(new PersistentQueueTestEntry(String.valueOf(i)));
            pqueue.remove();
        }
        
        // lose pqueue now (e.g. because of system crash) and create a new one
        pqueue = new PersistentQueue<PersistentQueueTestEntry>(fileName, 10);
        
        // and check ten elements
        for (int i = 0; i < 10; i++) {
            PersistentQueueTestEntry entry = pqueue.remove();
            assert(entry.content != null); 
        }
    }
    
    /** Tear down unit test: delete the file created by PersistentQueue. */
    protected void tearDown() throws Exception {
        super.tearDown();
        
        // delete the file created by pqueue
        File deleteFile = new File(fileName);
        deleteFile.delete();
    }
}
