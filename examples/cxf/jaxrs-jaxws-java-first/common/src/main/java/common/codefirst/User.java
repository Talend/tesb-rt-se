/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.codefirst;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(UserAdapter.class)
public interface User {

    String getName();
}
