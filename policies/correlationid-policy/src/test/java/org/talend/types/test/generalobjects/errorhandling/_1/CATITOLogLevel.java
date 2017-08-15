
package org.talend.types.test.generalobjects.errorhandling._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CAT_ITOLogLevel.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CAT_ITOLogLevel">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NORM"/>
 *     &lt;enumeration value="WARN"/>
 *     &lt;enumeration value="MINO"/>
 *     &lt;enumeration value="MAJO"/>
 *     &lt;enumeration value="CRIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CAT_ITOLogLevel")
@XmlEnum
public enum CATITOLogLevel {

    NORM,
    WARN,
    MINO,
    MAJO,
    CRIT;

    public String value() {
        return name();
    }

    public static CATITOLogLevel fromValue(String v) {
        return valueOf(v);
    }

}
