package org.talend.esb.job.console;

/**
 * Util class to provide HTML headers/footers.
 */
public class Template {

    public static String header() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
        buffer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n");
        buffer.append("<head>\n");
        buffer.append("<title>Talend Job Console</title>\n");
        buffer.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"talend.css\" title=\"Talend\"/>\n");
        buffer.append("</head>\n");
        buffer.append("<body>\n");
        buffer.append("<div class=\"top\">\n");
        buffer.append("<img src=\"images/talend.jpg\" alt=\"Talend\"/>\n");
        buffer.append("<div class=\"searchbox\">\n");
        buffer.append("<form action=\"search.do\">\n");
        buffer.append("<input class=\"searchform\" type=\"text\" name=\"query\" size=\"15\"/>\n");
        buffer.append("<input class=\"searchform\" type=\"submit\" name=\"ok\" value=\"Search\"/><br>\n");
        buffer.append("</form>\n");
        buffer.append("</div>\n");
        buffer.append("<div class=\"splitter\">\n");
        buffer.append("<b>ESB Job Console</b>\n");
        buffer.append("</div>\n");
        buffer.append("</div>\n");
        return buffer.toString();
    }

    public static String footer() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("</body>\n");
        buffer.append("</html>\n");
        return buffer.toString();
    }

}
