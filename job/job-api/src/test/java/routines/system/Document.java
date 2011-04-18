// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package routines.system;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class Document {

    private org.dom4j.Document doc = null;

    public void setDocument(org.dom4j.Document doc) {
        this.doc = doc;
    }

    public org.dom4j.Document getDocument() {
        return this.doc;
    }
	
	public String toString() {
		if(this.doc==null)
			return null;
			
		return this.doc.asXML();
	}
}
