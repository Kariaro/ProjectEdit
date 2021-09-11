package com.hardcoded.mc.objects;

import java.util.List;

import com.fasterxml.jackson.annotation.*;
import com.hardcoded.util.Resource;

@JsonPropertyOrder({
   "id"
})
public class MinecraftBlock {
	@JsonIgnore
	Resource id;

	@JsonProperty("map_color")
	int map_color;
	
	@JsonProperty("is_opaque")
	boolean is_opaque;

	@JsonProperty("is_air")
	boolean is_air;
	
	@JsonProperty("luminosity")
	int light_emission;
	
	@JsonProperty("hardness")
	float block_hardness;
	
	@JsonProperty("resistance")
	float block_resistance;
	
	@JsonProperty("states")
	List<MinecraftBlockState> states;
	
	@JsonSetter("id")
	private void set_resource(String value) {
		id = Resource.of(value);
	}

	@JsonGetter("id")
	private String get_resource() {
		return id.toString();
	}
	
	@JsonIgnore
	public Resource getId() {
		return id;
	}

	@JsonIgnore
	public boolean isAir() {
		return is_air;
	}

	@JsonIgnore
	public boolean isOpaque() {
		return is_opaque;
	}

	@JsonIgnore
	public int getLightEmission() {
		return light_emission;
	}

	@JsonIgnore
	public int getMapColor() {
		return map_color;
	}

	@JsonIgnore
	public float getHardness() {
		return block_hardness;
	}

	@JsonIgnore
	public float getResistance() {
		return block_resistance;
	}

	@JsonIgnore
	public List<MinecraftBlockState> getStates() {
		return List.copyOf(states);
	}
	
	@Override
	public String toString() {
		return "Block{%s}".formatted(id);
	}
}
