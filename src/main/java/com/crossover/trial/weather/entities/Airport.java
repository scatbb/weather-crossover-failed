package com.crossover.trial.weather.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "Airport")
@Table(name = "airports_weather")
public class Airport {
	
	@JsonIgnore
	private Long id;	
	private String aname;	
	private String city;
	private String country;
	private String iata;
	private String code4;
	private String latitude;
	private String longitude;
	private String value1;
	private String value2;
	private String value3;
	
    @Id
    @SequenceGenerator(name = "AID_GENERATOR", sequenceName = "airports_weather_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AID_GENERATOR")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "aname")
	public String getAname() {
		return aname;
	}
	public void setAname(String aname) {
		this.aname = aname;
	}
	
	@Column(name = "city")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "country")
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Column(name = "iata")
	public String getIata() {
		return iata;
	}
	public void setIata(String iata) {
		this.iata = iata;
	}

	@Column(name = "code4")
	public String getCode4() {
		return code4;
	}
	public void setCode4(String code4) {
		this.code4 = code4;
	}

	@Column(name = "latitude")
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Column(name = "longitude")
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	@Column(name = "value1")
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	@Column(name = "value2")
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	
	@Column(name = "value3")
	public String getValue3() {
		return value3;
	}
	public void setValue3(String value3) {
		this.value3 = value3;
	}
	
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer("Airport is: ");
		result.append("Name: ").append(this.aname).append("; ");
		result.append("City: ").append(this.city).append("; ");
		result.append("Country: ").append(this.country).append("; ");
		result.append("IATA: ").append(this.iata).append("; ");
		result.append("Code4: ").append(this.code4).append("; ");
		result.append("Latitude: ").append(this.latitude).append("; ");
		result.append("Longitude: ").append(this.longitude).append("; ");
		result.append("Value1: ").append(this.value1).append("; ");
		result.append("Value2: ").append(this.value2).append("; ");
		result.append("Value3: ").append(this.value3).append("; ");
		return result.toString();
	}
}
