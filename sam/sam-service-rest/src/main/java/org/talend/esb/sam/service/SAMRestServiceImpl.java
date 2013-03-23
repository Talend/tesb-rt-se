package org.talend.esb.sam.service;

import java.net.MalformedURLException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.server.ui.CriteriaAdapter;
import org.talend.esb.sam.service.exception.IllegalParameterException;
import org.talend.esb.sam.service.exception.ResourceNotFoundException;


public class SAMRestServiceImpl implements SAMRestService {

    SAMProvider provider;

    @Context
    protected UriInfo uriInfo;

    public void setProvider(SAMProvider provider) {
        this.provider = provider;
    }

    @Override
    public Response getEvent(String arg0) {
        Integer eventId;
        try {
            eventId = Integer.parseInt(arg0);
        }
        catch(NumberFormatException ex) {
            throw new IllegalParameterException("Error during converting " + arg0 + " parameter to Integer", ex);
        }
        Event event = provider.getEventDetails(eventId);
        if(event == null) throw new ResourceNotFoundException("There no event with "+ arg0 + " ID can be found");
        return Response.ok(event).build();
    }

    @Override
    public Response getFlow(String flowID) {
        List<FlowEvent> flowEvents = provider.getFlowDetails(flowID);
        if (flowEvents.size() == 0)
            throw new ResourceNotFoundException("There no flow with "+ flowID + " ID can be found");
        return Response.ok(aggregateFlowDetails(flowEvents)).build();
    }

    @Override
    public Response getFlows(Integer offset, Integer limit, List<String> params) {
        CriteriaAdapter adapter = new CriteriaAdapter(offset, limit, convertParams(params));
        List<Flow> flows = provider.getFlows(adapter);
        return Response.ok(aggregateRawData(flows)).build();
    }

    private Map<String, String[]> convertParams(List<String> params) {
        Map<String, String[]> paramsMap = new HashMap<String, String[]>();
        for (String param : params) {
            String[] p = param.split(",");
            if (p.length == 2) {
                paramsMap.put(p[0], new String[] { p[1] });
            }
        }
        return paramsMap;
    }
    
    public FlowDetails aggregateFlowDetails(List<FlowEvent> flowEvents) {
        FlowDetails flowDetails = new FlowDetails();
        Map<Long, Map<String, String>> customInfo = new HashMap<Long, Map<String, String>>();
        Set<Long> allEvents = new HashSet<Long>();
        for (FlowEvent flowEvent : flowEvents) {
            allEvents.add(flowEvent.getId());
            String custKey = flowEvent.getCustomKey();
            String custValue = flowEvent.getCustomValue();
            if (custKey != null) {
                if (!customInfo.containsKey(flowEvent.getId())) {
                    customInfo.put(flowEvent.getId(), new HashMap<String, String>());
                }
                customInfo.get(flowEvent.getId()).put(custKey, custValue);
            }
        }
        List<AggregatedFlowEvent> aggregatedFlowEventList = new ArrayList<AggregatedFlowEvent>();
        for (FlowEvent flowEvent : flowEvents) {
            AggregatedFlowEvent aggregatedFlowEvent = new AggregatedFlowEvent();
            if (allEvents.contains(flowEvent.getId())) {
                allEvents.remove(flowEvent.getId());
                aggregatedFlowEvent.setContentCut(flowEvent.isContentCut());
                aggregatedFlowEvent.setCustomId(flowEvent.getCustomId());
                try {
                    aggregatedFlowEvent.setDetails(
                            new URL(uriInfo.getBaseUri().toString().concat("/event/")
                            .concat(String.valueOf(flowEvent.getId()))));
                } catch (MalformedURLException e) {
                    throw new IllegalParameterException("cannot create URI for: " + flowEvent.getFlowID());
                }
                aggregatedFlowEvent.setEventType(flowEvent.getEventType());
                aggregatedFlowEvent.setFlowID(flowEvent.getFlowID());
                aggregatedFlowEvent.setHost(flowEvent.getHost());
                aggregatedFlowEvent.setId(flowEvent.getId());
                aggregatedFlowEvent.setIp(flowEvent.getIp());
                aggregatedFlowEvent.setMessageID(flowEvent.getMessageID());
                aggregatedFlowEvent.setOperation(flowEvent.getOperation());
                aggregatedFlowEvent.setPort(flowEvent.getPort());
                aggregatedFlowEvent.setPrincipal(flowEvent.getPrincipal());
                aggregatedFlowEvent.setProcess(flowEvent.getProcess());
                aggregatedFlowEvent.setTimestamp(flowEvent.getTimestamp());
                aggregatedFlowEvent.setTransport(flowEvent.getTransport());
                
                if (customInfo.containsKey(flowEvent.getId())) {
                    aggregatedFlowEvent.setCustomInfo(customInfo.get(flowEvent.getId()));
                }
                aggregatedFlowEventList.add(aggregatedFlowEvent);
            }
        }
        flowDetails.setEvents(aggregatedFlowEventList);
        return flowDetails;
    }

    public FlowCollection aggregateRawData(List<Flow> objects) {
        // Render RAW data
        Map<String, Long> flowLastTimestamp = new HashMap<String, Long>();
        Map<String, String> flowProviderIP = new HashMap<String, String>();
        Map<String, String> flowProviderHost = new HashMap<String, String>();
        Map<String, String> flowConsumerIP = new HashMap<String, String>();
        Map<String, String> flowConsumerHost = new HashMap<String, String>();
        Map<String, Set<String>> flowTypes = new HashMap<String, Set<String>>();

        for (Flow obj : objects) {
            if (null == obj.getflowID() || obj.getflowID().isEmpty()) {
                continue;
            }
            String flowID = obj.getflowID();
            long timestamp = obj.getTimeStamp();
            flowLastTimestamp.put(flowID, timestamp);
            if (!flowTypes.containsKey(flowID)) {
                flowTypes.put(flowID, new HashSet<String>());
            }
            EventTypeEnum typeEnum = obj.getEventType();
            flowTypes.get(flowID).add(typeEnum.toString());

            boolean isConsumer = typeEnum == EventTypeEnum.REQ_OUT || typeEnum == EventTypeEnum.RESP_IN;
            boolean isProvider = typeEnum == EventTypeEnum.REQ_IN || typeEnum == EventTypeEnum.RESP_OUT;
            String host = obj.getHost();
            String ip = obj.getIp();
            if (isConsumer) {
                flowConsumerIP.put(flowID, ip);
                flowConsumerHost.put(flowID, host);
            }
            if (isProvider) {
                flowProviderIP.put(flowID, ip);
                flowProviderHost.put(flowID, host);
            }
        }
        List<AggregatedFlow> result = new ArrayList<AggregatedFlow>();
        for (Flow obj : objects) {
            if (null == obj.getflowID() || obj.getflowID().isEmpty()) {
                continue;
            }

            AggregatedFlow aggregatedFlow = new AggregatedFlow();

            String flowID = obj.getflowID();
            long timestamp = obj.getTimeStamp();
            Long endTime = flowLastTimestamp.get(flowID);
            if (endTime != null) {
                flowLastTimestamp.remove(flowID);
                aggregatedFlow.setElapsed(timestamp - endTime);
                aggregatedFlow.setTypes(flowTypes.get(flowID));
                try {
                    aggregatedFlow.setDetails(new URL(uriInfo.getBaseUri().toString().concat("/flow/")
                            .concat(String.valueOf(obj.getflowID()))));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (flowConsumerHost.containsKey(flowID)) {
                    aggregatedFlow.setConsumerHost(flowConsumerHost.get(flowID));
                    aggregatedFlow.setConsumerIP(flowConsumerIP.get(flowID));
                }
                if (flowProviderHost.containsKey(flowID)) {
                    aggregatedFlow.setProviderHost(flowProviderHost.get(flowID));
                    aggregatedFlow.setProviderIP(flowProviderIP.get(flowID));
                }
                aggregatedFlow.setflowID(flowID);
                aggregatedFlow.setTimeStamp(timestamp);
                aggregatedFlow.setPort(obj.getPort());
                aggregatedFlow.setOperation(obj.getOperation());
                aggregatedFlow.setTransport(obj.getTransport());

                result.add(aggregatedFlow);
            }
        }
        FlowCollection fc = new FlowCollection();
        fc.setFlows(result);
        fc.setCount(result.size());
        return fc;
    }
}
