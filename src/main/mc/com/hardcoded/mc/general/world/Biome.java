package com.hardcoded.mc.general.world;

public class Biome {
	private final String name;
	private final double temperature;
	private final double downfall;
	
	protected Biome(String name, double temperature, double downfall) {
		this.name = name;
		this.temperature = temperature;
		this.downfall = downfall;
	}
	
	public String getName() {
		return name;
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	public double getDownfall() {
		return downfall;
	}
}
