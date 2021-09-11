package com.hardcoded.mc.objects;

import java.util.List;

import com.hardcoded.mc.reflection.ReflectionUtil;
import com.hardcoded.util.Resource;

public class MinecraftBlockBuilder {
	private MinecraftBlock block;
	
	public MinecraftBlockBuilder() {
		block = new MinecraftBlock();
	}
	
	public MinecraftBlockBuilder setResource(Resource resource) {
		block.id = resource;
		return this;
	}
	
	public MinecraftBlockBuilder setResistance(float resistance) {
		block.block_resistance = resistance;
		return this;
	}
	
	public MinecraftBlockBuilder setHardness(float hardness) {
		block.block_hardness = hardness;
		return this;
	}
	
	public MinecraftBlockBuilder setIsAir(boolean is_air) {
		block.is_air = is_air;
		return this;
	}
	
	public MinecraftBlockBuilder setIsOpaque(boolean is_opaque) {
		block.is_opaque = is_opaque;
		return this;
	}
	
	public MinecraftBlockBuilder setMapColor(int map_color) {
		block.map_color = map_color;
		return this;
	}
	
	public MinecraftBlockBuilder setStates(List<MinecraftBlockState> states) {
		block.states = states;
		return this;
	}
	
	public MinecraftBlockBuilder setLightEmission(int light_emission) {
		block.light_emission = light_emission;
		return this;
	}
	
	public MinecraftBlock build() {
		MinecraftBlock old = block;
		block = null;
		return old;
	}
	
	@FunctionalInterface
	public interface FieldFunction<T> {
		T get(Object obj) throws Exception;
	}
	
	public static MinecraftBlock generate(
		Object blockObject,
		String mapColor_path,
		String hardness_path,
		String resistance_path,
		String isOpaque_path,
		String isAir_path,
		FieldFunction<Resource> resource_func,
		FieldFunction<List<MinecraftBlockState>> get_states_func
	) {
		try {
			return new MinecraftBlockBuilder()
				.setResource(resource_func.get(blockObject))
				.setStates(get_states_func.get(blockObject))
				.setMapColor(ReflectionUtil.getFieldFromPathCast(blockObject, mapColor_path, 0x000000))
				.setHardness(ReflectionUtil.getFieldFromPathCast(blockObject, hardness_path, 0.0f))
				.setResistance(ReflectionUtil.getFieldFromPathCast(blockObject, resistance_path, 0.0f))
				.setIsOpaque(ReflectionUtil.getFieldFromPathCast(blockObject, isOpaque_path, true))
				.setIsAir(ReflectionUtil.getFieldFromPathCast(blockObject, isAir_path, false))
				.build();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
