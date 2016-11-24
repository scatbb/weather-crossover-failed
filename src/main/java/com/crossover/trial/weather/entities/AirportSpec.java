package com.crossover.trial.weather.entities;

public class AirportSpec {
	private Airport airport;	
	private AtmosphericInformation ai = new AtmosphericInformation();
	private long reqCount = 0;
	
	public Airport getAirport() {
		return airport;
	}
	public void setAirport(Airport airport) {
		this.airport = airport;
	}
	public AtmosphericInformation getAi() {
		return ai;
	}
	public void setAi(AtmosphericInformation ai) {
		this.ai = ai;
	}
	public long getReqCount() {
		return reqCount;
	}
	public void incReqCount() {
		++reqCount;
	}
	public void setReqCount(long reqCount) {
		this.reqCount = reqCount;
	}

}
