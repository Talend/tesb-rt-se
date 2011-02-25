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

import java.io.Serializable;

/** 
 * Helper class for {@link com.gaborcselle.persistent.PersistentQueue}.
 * Instances represent that the first element of a list has been deleted.
 * 
 * @author Gabor Cselle
 * @version 1.0
 */  
public class PersistentQueueDeleteMarker implements Serializable {
    public final static long serialVersionUID = 1;
    
    public PersistentQueueDeleteMarker() {
        super();
    }
}
