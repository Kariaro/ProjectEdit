package com.hardcoded.render.generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.hardcoded.api.IResource;
import com.hardcoded.api.ResourceException;
import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.mc.general.world.*;
import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelObject;
import com.hardcoded.util.Resource;

/**
 * @author HardCoded
 */
public class VersionResourceReader extends IResource {
	private static final Logger LOGGER = LogManager.getLogger(VersionResourceReader.class);
	
	private final ZipFile file;
	private final ZipFile[] resourcePacks;
	
	public VersionResourceReader(File file, File... packs) throws IOException {
		this.file = new ZipFile(file);
		this.resourcePacks = new ZipFile[packs.length];
		
		for(int i = 0; i < packs.length; i++) {
			this.resourcePacks[i] = new ZipFile(packs[i]);
		}
		
		// XXX: Totaly bogus solution but works
		FastModelJsonLoader.setResourceReader(this);
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
		key = Resource.removeNamespace(key);
		byte[] bytes = readEntry("assets/minecraft/models/" + key + ".json");
		JSONObject json = new JSONObject(new String(bytes));
		return json;
	}
	
	public byte[] getBlockModelBytes(String key) {
		key = Resource.removeNamespace(key);
		return readEntry("assets/minecraft/models/" + key + ".json");
	}
	
	public BufferedImage readBufferedImage(String key) {
		key = Resource.removeNamespace(key);
		
		try {
			InputStream stream = findFirstInputStream("assets/minecraft/textures/" + key + ".png");
			if(stream == null) return null;
			
			return ImageIO.read(stream);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Texture readTexture(String key) {
		BufferedImage bi = readBufferedImage(key);
		if(bi == null) return null;
		return Texture.loadBufferedImageTexture(bi, key, GL11.GL_NEAREST);
	}
	
	public boolean resolveState(IBlockData state) {
		JSONObject json = getBlockState(state.getName());
		
		if(json == null) {
			return false;
		}
		
		List<JSONObject> model_data_list = null;
		if(json.has("variants")) {
			model_data_list = resolve_variants(state, json.getJSONObject("variants"));
		} else if(json.has("multipart")) {
			model_data_list = resolve_multipart(state, json.getJSONArray("multipart"));
		} else {
			LOGGER.info("Other: {}", json);
			throw new UnsupportedOperationException();
		}
		
		if(model_data_list == null || model_data_list.isEmpty()) {
			return false;
		}
		
		for(JSONObject model_data : model_data_list) {
			int x = 0;
			int y = 0;
			if(model_data.has("x")) {
				x = model_data.getNumber("x").intValue();
			}
			
			if(model_data.has("y")) {
				y = model_data.getNumber("y").intValue();
			}
			
			boolean uvlock = false;
			if(model_data.has("uvlock")) {
				uvlock = model_data.getBoolean("uvlock");
			}
			
			if(!model_data.has("model")) {
				LOGGER.error("Failed to find model of resource: {}", model_data);
			} else {
				String path = model_data.getString("model");
				ModelObject model = FastModelJsonLoader.loadModelDelegate(path, x, y, uvlock);
				FastModelRenderer.addModel(state, model);
			}
		}
		
		return true;
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
					models.add(model_array.getJSONObject(0));
//					for(int j = 0; j < model_array.length(); j++) {
//					}
				}
			}
			
			if(model_case.has("when")) {
				JSONObject when = model_case.getJSONObject("when");
				
				if(when.has("OR")) {
					JSONArray or_list = when.getJSONArray("OR");
					
					for(int j = 0; j < or_list.length(); j++) {
						Map<String,String> map = convertMultipartWhenToString(or_list.getJSONObject(j));
						
						if(stateList.matchesAllowOr(map)) {
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
			// XXX: Make sure this works for all values
			// when.getString(key));
			map.put(key, when.get(key).toString());
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
				model_data.add(array.getJSONObject(0));
				
				// TODO: Implemented weighted random picking
//				for(int i = 0; i < array.length(); i++) {
//					model_data.add(array.getJSONObject(i));
//				}
			}
		} else if(obj instanceof JSONObject) {
			model_data = new ArrayList<>();
			model_data.add((JSONObject)obj);
		}
		
		return model_data;
	}
	
	private InputStream findFirstInputStream(String name) throws IOException {
		for(ZipFile pack : resourcePacks) {
			ZipEntry entry = pack.getEntry(name);
			if(entry != null) return pack.getInputStream(entry);
		}
		
		ZipEntry entry = file.getEntry(name);
		return entry == null ? null:file.getInputStream(entry);
	}
	
	private byte[] readEntry(String name) {
		InputStream stream = null;
		try {
			stream = findFirstInputStream(name);
			if(stream != null) {
				return stream.readAllBytes();
			}
		} catch(IOException e) {
			LOGGER.throwing(e);
			e.printStackTrace();
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {
					LOGGER.throwing(e);
					e.printStackTrace();
				}
			}
		}
		
		return new byte[0];
		
//		ZipEntry entry = file.getEntry(name);
//		if(entry != null) {
//			try {
//				InputStream stream = file.getInputStream(entry);
//				return stream.readAllBytes();
//			} catch(IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		return new byte[0];
	}
	
	@Override
	public void cleanup() throws ResourceException {
		try {
			file.close();
			
			for(int i = 0; i < resourcePacks.length; i++) {
				resourcePacks[i].close();
				resourcePacks[i] = null;
			}
		} catch(IOException e) {
			throw new ResourceException(e);
		}
	}
	
	public void loadBlocks(BiConsumer<Integer, Integer> callback) {
		Set<IBlockData> states = BlockDataManager.getStates();
		final int size = states.size();
		
		int count = 0;
		try {
			for(IBlockData data : states) {
				BlockData dta = (BlockData)data;
				if(!resolveState(data)) {
					FastModelRenderer.addModel(data, FastModelJsonLoader.getMissingBlockModel());
				}
				
				for(IBlockData child : dta.getChildren()) {
					if(!resolveState(child)) {
						FastModelRenderer.addModel(child, FastModelJsonLoader.getMissingBlockModel());
					}
				}
				
				callback.accept(++count, size);
			}
		} catch(Exception e) {
			LOGGER.fatal("Failed to load all states: {}", e);
			e.printStackTrace();
		}
	}
}
