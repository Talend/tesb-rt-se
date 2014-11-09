package org.talend.esb.policy.compression.impl.internal;

public class StreamPosition {
	
	public static final int DEFAULT_POSITION = -1;
	
	private int start = DEFAULT_POSITION;
	private int end = DEFAULT_POSITION;
	
	public StreamPosition(){
		
	}
	
	public StreamPosition(int start, int end){
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getSize() {
		if(end == DEFAULT_POSITION
				|| start == DEFAULT_POSITION){
			return 0;
		}
		return end - start;
	}
}
