/*
 * #%L
 * Talend :: ESB :: Job :: Web Console
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.console;

/**
 * Util class to provide HTML headers/footers.
 */
public class Template {

    public static String header() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        buffer.append("<head>");
        buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
        buffer.append("<title>Bundle Conductor</title>");
        buffer.append("<link rel=\"stylesheet\" href=\"css/core.css\" type=\"text/css\" />");
        buffer.append("<script type=\"text/javascript\" src=\"js/jquery.js\"></script>");
        buffer.append("<script type=\"text/javascript\" src=\"js/core.js\"></script>");
        buffer.append("</head>");
        buffer.append("<body>");
        buffer.append("<div id=\"header_bc\">");
        buffer.append("<div>");
        buffer.append("<div>");
        buffer.append("<h1>BUNDLE CONDUCTOR</h1>");
        buffer.append("</div>");
        buffer.append("</div>");
        buffer.append("</div>");
        return buffer.toString();
    }

    public static String footer() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("</body>\n");
        buffer.append("</html>\n");
        return buffer.toString();
    }

}
