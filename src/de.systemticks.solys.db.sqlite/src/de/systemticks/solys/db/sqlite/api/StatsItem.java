package de.systemticks.solys.db.sqlite.api;

public class StatsItem <T> {

	long timestamp;
	T minimum;
	T maximum;
	T average;	
	int channelId;
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public T getMinimum() {
		return minimum;
	}
	public void setMinimum(T minimum) {
		this.minimum = minimum;
	}
	public T getMaximum() {
		return maximum;
	}
	public void setMaximum(T maximum) {
		this.maximum = maximum;
	}
	public T getAverage() {
		return average;
	}
	public void setAverage(T average) {
		this.average = average;
	}
	public int getChannelId() {
		return channelId;
	}
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}	
	
	
}
