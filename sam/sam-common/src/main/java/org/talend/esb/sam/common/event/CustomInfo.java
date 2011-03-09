package org.talend.esb.sam.common.event;

/**
 * this Bean is used for store custom key/value info into Database
 * @author Xilai Dai
 *
 */
public class CustomInfo {

	private Long persistedId;
	
	private String custKey;
	
	private Object custValue;
	
	public CustomInfo(){
		super();
	}

	public Long getPersistedId() {
		return persistedId;
	}

	public void setPersistedId(Long persistedId) {
		this.persistedId = persistedId;
	}

    public String getCustKey() {
		return custKey;
	}

	public void setCustKey(String custKey) {
		this.custKey = custKey;
	}

	public Object getCustValue() {
		return custValue;
	}

	public void setCustValue(Object custValue) {
		this.custValue = custValue;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((persistedId == null) ? 0 : persistedId.hashCode());
        result = prime * result + ((custKey == null) ? 0 : custKey.hashCode());
        result = prime * result + ((custValue == null) ? 0 : custValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomInfo other = (CustomInfo)obj;
        if (persistedId == null) {
            if (other.persistedId != null)
                return false;
        } else if (!persistedId.equals(other.persistedId))
            return false;
        if (custKey == null) {
            if (other.custKey != null)
                return false;
        } else if (!custKey.equals(other.custKey))
            return false;
        if (custValue == null) {
            if (other.custValue != null)
                return false;
        } else if (!custValue.equals(other.custValue))
            return false;        
        return true;
    }
	
	
}
