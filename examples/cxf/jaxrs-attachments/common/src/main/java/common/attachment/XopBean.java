/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package common.attachment;

import java.awt.Image;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 * Sample bean which will be represented on the wire according 
 * to the <a href="http://www.w3.org/TR/xop10/">XOP Specification</a>.
 */
@XmlRootElement(name = "xopBean", namespace = "http://xop/jaxrs")
@XmlType(name = "XopBean", propOrder = {
        "name",
        "datahandler",
        "bytes",
        "image" })
public class XopBean {

    private String name;
    private DataHandler datahandler;
    private byte[] bytes;
    private Image image;

    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    
    @XmlElement(required = true)
    @XmlMimeType("application/octet-stream")
    public byte[] getBytes() {
        return bytes;
    }

    
    public void setBytes(byte[] value) {
        this.bytes = value;
    }
    
    
    @XmlElement(required = true)
    @XmlMimeType("application/octet-stream")
    public DataHandler getDatahandler() {
        return datahandler;
    }

    
    public void setDatahandler(DataHandler value) {
        this.datahandler = value;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

}
