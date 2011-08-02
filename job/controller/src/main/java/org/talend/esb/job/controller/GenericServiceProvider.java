package org.talend.esb.job.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@javax.xml.ws.WebServiceProvider()
public class GenericServiceProvider
	implements javax.xml.ws.Provider<javax.xml.transform.Source> {

	private Map<String, String> operations;
    private Controller controller;

    @Resource
    private WebServiceContext context;
	
	public void setOperations(Map<String, String> operations) {
		this.operations = operations;
	}

    public void setController(Controller controller) {
        this.controller = controller;
    }

	@Override
    public Source invoke(Source request) {
        QName operationQName = (QName)context.getMessageContext().get(MessageContext.WSDL_OPERATION);
        System.out.println("operationQName="+operationQName);
		final String jobName = operations.get(operationQName.getLocalPart());
		if (jobName == null) {
			throw new RuntimeException("Job for operation '" + operationQName + "' not found");
		}
		try {
//			controller.run(jobName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return null;
    }

}
