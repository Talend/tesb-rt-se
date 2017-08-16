package org.talend.esb.sam.server.persistence;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "flowCollection")
public class AggregatedFlowCollection {

    private int count;

    private List<AggregatedFlow> aggregated;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<AggregatedFlow> getAggregated() {
        return aggregated;
    }

    public void setAggregated(List<AggregatedFlow> flows) {
        this.aggregated = flows;
    }

}
