package com.hardcoded.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.BlockDataManager;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelDelegate;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelObject;

/**
 * @author HardCoded
 */
public class VersionResourceReader {
	private static final Logger LOGGER = LogManager.getLogger(VersionResourceReader.class);
	
	private final ZipFile file;
	
	public VersionResourceReader(File file) throws IOException {
		this.file = new ZipFile(file);
		FastModelJsonLoader.resource = this;
	}
	
	public JSONObject getBlockState(String name) {
		byte[] bytes = readEntry("assets/minecraft/blockstates/" + name + ".json");
		
		try {
			return new JSONObject(new String(bytes));
		} catch(Exception e) {
			return null;
		}
	}
	
	public JSONObject getBlockModel(String key) {
		{
			int index = key.indexOf(':');
			if(index != -1) {
				key = key.substring(index + 1);
			}
		}
		
		byte[] bytes = readEntry("assets/minecraft/models/" + key + ".json");
		JSONObject json = new JSONObject(new String(bytes));
		return json;
	}
	
	public byte[] getBlockModelBytes(String key) {
		int index = key.indexOf(':');
		if(index != -1) key = key.substring(index + 1);
		return readEntry("assets/minecraft/models/" + key + ".json");
	}
	
	public BufferedImage readBufferedImage(String key) {
		int index = key.indexOf(':');
		if(index != -1) key = key.substring(index + 1);
		
		BufferedImage bi = null;
		
		try {
			bi = ImageIO.read(new ByteArrayInputStream(readEntry("assets/minecraft/textures/" + key + ".png")));
		} catch(Exception e) {
			
		}
		
		return bi;
	}
	
	public Texture readTexture(String key) {
		{
			int index = key.indexOf(':');
			if(index != -1) {
				key = key.substring(index + 1);
			}
		}
		
		BufferedImage bi = null;
		
		try {
			bi = ImageIO.read(new ByteArrayInputStream(readEntry("assets/minecraft/textures/" + key + ".png")));
		} catch(Exception e) {
			
		}
		
		if(bi == null) return null;
		return Texture.loadBufferedImageTexture(bi, key, GL11.GL_NEAREST);
	}
	
	public void resolveState(IBlockData state) {
		JSONObject json = getBlockState(state.getName());
		
		if(json == null) {
			return;
		}
		
		boolean is_multipart = false;
		List<JSONObject> model_data_list = null;
		if(json.has("variants")) {
			model_data_list = resolve_variants(state, json.getJSONObject("variants"));
		} else if(json.has("multipart")) {
			model_data_list = resolve_multipart(state, json.getJSONArray("multipart"));
			is_multipart = true;
		} else {
			System.out.println("Other: " + json);
			throw new UnsupportedOperationException();
		}
		
		if(model_data_list == null || model_data_list.isEmpty()) {
			return;
		}
		
		boolean isWeighted = false;
		for(JSONObject model_data : model_data_list) {
			if(model_data.has("weight")) {
				isWeighted = true;
				break;
			}
		}
		
		for(JSONObject model_data : model_data_list) {
			Matrix4f matrix = new Matrix4f();
			
			int x = 0;
			int y = 0;
			if(model_data.has("x")) {
				x = model_data.getNumber("x").intValue();
			}
			
			if(model_data.has("y")) {
				y = model_data.getNumber("y").intValue();
			}
			
			{
				matrix.translateLocal(-8, -8, -8)
					.rotateLocalX(MathUtils.toRadians(-x))
					.rotateLocalY(MathUtils.toRadians(-y))
					.translateLocal(8, 8, 8);
			}
			
			boolean uvlock = false;
			if(model_data.has("uvlock")) {
				uvlock = model_data.getBoolean("uvlock");
			}
			
			if(!model_data.has("model")) {
				LOGGER.error("Failed to find model of resource: {}", model_data);
			} else {
				String path = model_data.getString("model");
				
				ModelObject model = FastModelJsonLoader.loadModel(path);
				ModelDelegate delegate = new ModelDelegate(matrix, uvlock, model);
				
				((BlockData)state).model_objects.add(delegate);
			}
			
			if(isWeighted) {
				// TODO: Figure out how to render weighted block picking
				break;
			}
			
			if(!is_multipart) {
				break;
			}
		}
	}
	
	private List<JSONObject> resolve_multipart(IBlockData state, JSONArray array) {
		IBlockStateList stateList = state.getStateList();
		
		List<JSONObject> model_data = new ArrayList<>();
		for(int i = 0; i < array.length(); i++) {
			JSONObject model_case = array.getJSONObject(i);
			
			List<JSONObject> models = new ArrayList<>();
			{
				Object model_obj = model_case.get("apply");
				if(model_obj instanceof JSONObject) {
					models.add((JSONObject)model_obj);
				} else {
					JSONArray model_array = (JSONArray)model_obj;
					for(int j = 0; j < model_array.length(); j++) {
						models.add(model_array.getJSONObject(j));
					}
				}
			}
			
			if(model_case.has("when")) {
				JSONObject when = model_case.getJSONObject("when");
				
				if(when.has("OR")) {
					JSONArray or_list = when.getJSONArray("OR");
					
					for(int j = 0; j < or_list.length(); j++) {
						if(stateList.matchesAllowOr(convertMultipartWhenToString(or_list.getJSONObject(j)))) {
							model_data.addAll(models);
							break;
						}
					}
				} else if(when.has("AND")) {
					JSONArray and_list = when.getJSONArray("AND");
					
					boolean match_all = true;
					for(int j = 0; j < and_list.length(); j++) {
						if(!stateList.matchesAllowOr(convertMultipartWhenToString(and_list.getJSONObject(j)))) {
							match_all = false;
							break;
						}
					}
					
					if(match_all) {
						model_data.addAll(models);
					}
				} else if(stateList.matchesAllowOr(convertMultipartWhenToString(when))) {
					model_data.addAll(models);
				}
			} else {
				model_data.addAll(models);
			}
		}
		
		return model_data;
	}
	
	private Map<String, String> convertMultipartWhenToString(JSONObject when) {
		Map<String, String> map = new HashMap<>();
		
		for(String key : when.keySet()) {
			map.put(key, when.getString(key));
		}
		
		return map;
	}
	
	private List<JSONObject> resolve_variants(IBlockData state, JSONObject variants) {
		IBlockStateList stateList = state.getStateList();
		
		String key = null;
		for(String str : variants.keySet()) {
			if(key == null) key = str;
			
			if(!str.isBlank()) {
				if(stateList.matches(str)) {
					key = str;
				}
			}
		}
		
		if(key == null) return null;
		Object obj = variants.get(key);
		
		List<JSONObject> model_data = null;
		if(obj instanceof JSONArray) {
			JSONArray array = (JSONArray)obj;
			
			if(array.length() > 0) {
				model_data = new ArrayList<>();
				for(int i = 0; i < array.length(); i++) {
					model_data.add(array.getJSONObject(i));
				}
			}
		} else if(obj instanceof JSONObject) {
			model_data = new ArrayList<>();
			model_data.add((JSONObject)obj);
		}
		
		return model_data;
	}
	
	private byte[] readEntry(String name) {
		ZipEntry entry = file.getEntry(name);
		if(entry != null) {
			try {
				InputStream stream = file.getInputStream(entry);
				return stream.readAllBytes();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return new byte[0];
	}
	
	/**
	 * This method will load all blocks using multiple threads.
	 * 
	 * <p>Before:
	 * <pre>
	 * Took: 10764,4312 ms, per item (14,1080)
	 * Average: 7,94011591 ms
	 * </pre>
	 * 
	 * Now:
	 * <pre>
	 * Took: 5786,3481 ms, per item (7,5837)
	 * </pre>
	 */
	public void loadBlocks(BiConsumer<Integer, Integer> callback) {
		Set<IBlockData> states = BlockDataManager.getStates();
		final int size = states.size();
		
		int count = 0;
		try {
			for(IBlockData data : states) {
				BlockData dta = (BlockData)data;
				dta.model_objects.clear();
				resolveState(data);
				
				for(IBlockData child : dta.getChildren()) {
					BlockData dta2 = (BlockData)child;
					dta2.model_objects.clear();
					resolveState(child);
				}
				
				callback.accept(++count, size);
			}
		} catch(Exception e) {
			LOGGER.fatal("Failed to load all states: {}", e);
			e.printStackTrace();
		}
	}
}
