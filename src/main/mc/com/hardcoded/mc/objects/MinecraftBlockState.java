package com.hardcoded.mc.objects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MinecraftBlockState {
	@JsonProperty("name")
	public final String name;
	
	@JsonProperty("values")
	public final List<String> values;
	
	@JsonCreator
	public MinecraftBlockState(
		@JsonProperty("name") String name,
		@JsonProperty("values") List<String> values
	) {
		this.name = name;
		this.values = List.copyOf(values);
	}
}
