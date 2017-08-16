package org.talend.esb.sam.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.server.persistence.AggregatedFlow;
import org.talend.esb.sam.server.persistence.AggregatedFlowCollection;
import org.talend.esb.sam.server.persistence.AggregatedFlowEvent;
import org.talend.esb.sam.server.persistence.CustomInfo;
import org.talend.esb.sam.server.persistence.Flow;
import org.talend.esb.sam.server.persistence.FlowCollection;
import org.talend.esb.sam.server.persistence.FlowDetails;
import org.talend.esb.sam.server.persistence.FlowEvent;
import org.talend.esb.sam.server.persistence.SAMProvider;
import org.talend.esb.sam.server.persistence.criterias.CriteriaAdapter;
import org.talend.esb.sam.service.exception.IllegalParameterException;
import org.talend.esb.sam.service.exception.ResourceNotFoundException;


public class SAMRestServiceImpl implements SAMRestService {

    SAMProvider provider;

    @Context
    protected UriInfo uriInfo;

    public void setProvider(SAMProvider provider) {
        this.provider = provider;
    }

    private final String ALIVE_CHECK_TEXT = "Talend Service Activity Monitoring Server :: REST API - ";

    @Override
    public Response checkAlive() {
        String startUrl = uriInfo.getBaseUriBuilder().path("list").build().toString();
        return Response.ok(ALIVE_CHECK_TEXT + startUrl).type(MediaType.TEXT_PLAIN).build();
    }

    @Override
    public Response getEvent(String id) {
        Integer eventId;
        try {
            eventId = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            throw new IllegalParameterException("Error during converting " + id + " parameter to Integer", ex);
        }
        FlowEvent event = provider.getEventDetails(eventId);
        if (null == event) {
            throw new ResourceNotFoundException("There no event with " + id + " ID can be found");
        }
        return Response.ok(event).build();
    }

    @Override
    public Response getFlow(String flowID) {
        List<FlowEvent> flowEvents = provider.getFlowDetails(flowID);
        if (0 == flowEvents.size()) {
            throw new ResourceNotFoundException("There no flow with " + flowID + " ID can be found");
        }
        return Response.ok(aggregateFlowDetails(flowEvents)).build();
    }

    @Override
    public Response getFlows(Integer offset, Integer limit) {
        Map<String, String[]> params = new HashMap<String, String[]>();
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        for (Entry<String, List<String>> entry : queryParams.entrySet()) {
            params.put(entry.getKey(), entry.getValue().toArray(new String[] {}));
        }
        CriteriaAdapter adapter = new CriteriaAdapter(offset, limit, params);
        FlowCollection flowCollection = provider.getFlows(adapter);
        return Response.ok(aggregateRawData(flowCollection)).build();
    }

    public FlowDetails aggregateFlowDetails(List<FlowEvent> flowEvents) {
        Map<Long, List<CustomInfo>> customInfo = new HashMap<Long, List<CustomInfo>>();
        Set<Long> allEvents = new HashSet<Long>();
        for (FlowEvent flowEvent : flowEvents) {
            long flowEventId = flowEvent.getId();
            allEvents.add(flowEventId);
            String custKey = flowEvent.getCustomKey();
            String custValue = flowEvent.getCustomValue();
            if (null != custKey) {
                if (!customInfo.containsKey(flowEventId)) {
                    customInfo.put(flowEventId, new ArrayList<CustomInfo>());
                }
                CustomInfo custom = new CustomInfo();
                custom.setKey(custKey);
                custom.setValue(custValue);
                customInfo.get(flowEventId).add(custom);
            }
        }

        List<AggregatedFlowEvent> aggregatedFlowEventList = new ArrayList<AggregatedFlowEvent>();
        for (FlowEvent flowEvent : flowEvents) {
            long flowEventId = flowEvent.getId();
            if (allEvents.contains(flowEventId)) {
                allEvents.remove(flowEventId);

                AggregatedFlowEvent aggregatedFlowEvent = new AggregatedFlowEvent();

                aggregatedFlowEvent.setContentCut(flowEvent.isContentCut());
                aggregatedFlowEvent.setCustomId(flowEvent.getCustomId());
                aggregatedFlowEvent.setDetails(uriInfo.getBaseUriBuilder().path("event")
                        .path(String.valueOf(flowEventId)).build());
                aggregatedFlowEvent.setType(flowEvent.getType());
                aggregatedFlowEvent.setFlowID(flowEvent.getFlowID());
                aggregatedFlowEvent.setHost(flowEvent.getHost());
                aggregatedFlowEvent.setId(flowEventId);
                aggregatedFlowEvent.setIp(flowEvent.getIp());
                aggregatedFlowEvent.setMessageID(flowEvent.getMessageID());
                aggregatedFlowEvent.setOperation(flowEvent.getOperation());
                aggregatedFlowEvent.setPort(flowEvent.getPort());
                aggregatedFlowEvent.setPrincipal(flowEvent.getPrincipal());
                aggregatedFlowEvent.setProcess(flowEvent.getProcess());
                aggregatedFlowEvent.setTimestamp(flowEvent.getTimestamp());
                aggregatedFlowEvent.setTransport(flowEvent.getTransport());

                if (customInfo.containsKey(flowEventId)) {
                    aggregatedFlowEvent.setCustomInfo(customInfo.get(flowEventId));
                }
                aggregatedFlowEventList.add(aggregatedFlowEvent);
            }
        }

        FlowDetails flowDetails = new FlowDetails();
        flowDetails.setEvents(aggregatedFlowEventList);
        return flowDetails;
    }

    public AggregatedFlowCollection aggregateRawData(FlowCollection collection) {
        // Render RAW data
        Map<String, Long> flowLastTimestamp = new HashMap<String, Long>();
        Map<String, String> flowProviderIP = new HashMap<String, String>();
        Map<String, String> flowProviderHost = new HashMap<String, String>();
        Map<String, String> flowConsumerIP = new HashMap<String, String>();
        Map<String, String> flowConsumerHost = new HashMap<String, String>();
        Map<String, Set<String>> flowTypes = new HashMap<String, Set<String>>();

        Map<String, String> flowConsumerPort = new HashMap<String, String>();
        Map<String, String> flowConsumerOperation = new HashMap<String, String>();
        Map<String, String> flowProviderPort = new HashMap<String, String>();
        Map<String, String> flowProviderOperation = new HashMap<String, String>();

        for (Flow obj : collection.getFlows()) {
            if (null == obj.getflowID() || obj.getflowID().isEmpty()) {
                continue;
            }

            String flowID = obj.getflowID();

            flowLastTimestamp.put(flowID, obj.getTimestamp());

            if (!flowTypes.containsKey(flowID)) {
                flowTypes.put(flowID, new HashSet<String>());
            }
            EventTypeEnum typeEnum = obj.getEventType();
            flowTypes.get(flowID).add(typeEnum.toString());

            boolean isConsumer = typeEnum == EventTypeEnum.REQ_OUT || typeEnum == EventTypeEnum.RESP_IN
                    || typeEnum == EventTypeEnum.FAULT_IN;
            boolean isProvider = typeEnum == EventTypeEnum.REQ_IN || typeEnum == EventTypeEnum.RESP_OUT
                    || typeEnum == EventTypeEnum.FAULT_OUT;
            String host = obj.getHost();
            String ip = obj.getIp();
            String port = obj.getPort();
            String operation = obj.getOperation();
            if (isConsumer) {
                flowConsumerIP.put(flowID, ip);
                flowConsumerHost.put(flowID, host);
                flowConsumerPort.put(flowID, port);
                flowConsumerOperation.put(flowID, operation);
            }
            if (isProvider) {
                flowProviderIP.put(flowID, ip);
                flowProviderHost.put(flowID, host);
                flowProviderPort.put(flowID, port);
                flowProviderOperation.put(flowID, operation);
            }
        }

        List<AggregatedFlow> result = new ArrayList<AggregatedFlow>();
        for (Flow obj : collection.getFlows()) {
            String flowID = obj.getflowID();
            if (null == flowID || flowID.isEmpty()) {
                continue;
            }

            Long startTime = flowLastTimestamp.remove(flowID);
            if (null != startTime) {
                AggregatedFlow aggregatedFlow = new AggregatedFlow();

                aggregatedFlow.setFlowID(flowID);

                if (flowProviderPort.containsKey(flowID)) {
                    aggregatedFlow.setPort(flowProviderPort.get(flowID));
                    aggregatedFlow.setOperation(flowProviderOperation.get(flowID));
                } else {
                    aggregatedFlow.setPort(flowConsumerPort.get(flowID));
                    aggregatedFlow.setOperation(flowConsumerOperation.get(flowID));
                }

                aggregatedFlow.setTransport(obj.getTransport());
                aggregatedFlow.setTypes(flowTypes.get(flowID));

                long timestamp = obj.getTimestamp();
                aggregatedFlow.setTimestamp(timestamp);
                aggregatedFlow.setElapsed(timestamp - startTime);

                aggregatedFlow.setDetails(uriInfo.getBaseUriBuilder().path("flow").path(flowID).build());

                if (flowConsumerHost.containsKey(flowID)) {
                    aggregatedFlow.setConsumerHost(flowConsumerHost.get(flowID));
                    aggregatedFlow.setConsumerIP(flowConsumerIP.get(flowID));
                }
                if (flowProviderHost.containsKey(flowID)) {
                    aggregatedFlow.setProviderHost(flowProviderHost.get(flowID));
                    aggregatedFlow.setProviderIP(flowProviderIP.get(flowID));
                }

                result.add(aggregatedFlow);
            }
        }

        AggregatedFlowCollection fc = new AggregatedFlowCollection();
        fc.setAggregated(result);
        fc.setCount(collection.getCount());
        return fc;
    }
}
