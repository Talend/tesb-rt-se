/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package com.talend.tif.examples.springsecurity.common;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(UserAdapter.class)
public interface User {

    String getName();

}
