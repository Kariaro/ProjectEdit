package com.hardcoded.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;

/**
 * @author HardCoded
 */
public class VersionResourceReader {
	private final ZipFile file;
	
	public VersionResourceReader(File file) throws IOException {
		this.file = new ZipFile(file);
	}
	
	public JSONObject getBlockState(String name) {
		byte[] bytes = readEntry("assets/minecraft/blockstates/" + name + ".json");
		
		JSONObject json = new JSONObject(new String(bytes));
		return json;
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
	
	public void resolveState_TEST(BlockData data, IBlockData state) {
		JSONObject json = getBlockState(state.getName());
		System.out.println("READING JSON FOR: " + state);
		
		JSONObject model_data = null;
		// JSONObject compiled_model = null;
		if(json.has("variants")) {
			model_data = resolve_variants(state, json.getJSONObject("variants"));
		} else if(json.has("multipart")) {
			// Read multipart
			System.out.println("Multipart: " + json);
		} else {
			System.out.println("Other: " + json);
		}
		
		if(model_data != null) {
			String path = model_data.getString("model");
			data.model2 = FastModelJsonLoader.test(this, path);
		}
		
		System.out.println("--------------------------------------------\n");
	}
	
	private JSONObject resolve_variants(IBlockData state, JSONObject variants) {
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
		
		JSONObject model_data = null;
		if(obj instanceof JSONArray) {
			JSONArray array = (JSONArray)obj;
			
			if(array.length() > 0) {
				model_data = array.getJSONObject(0);
			}
		} else if(obj instanceof JSONObject) {
			JSONObject object = (JSONObject)obj;
			
			model_data = object;
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
}
