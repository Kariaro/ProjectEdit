package com.hardcoded.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.BlockDataManager;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;

/**
 * @author HardCoded
 */
public class VersionResourceReader {
	private final ZipFile file;
	
	public VersionResourceReader(File file) throws IOException {
		this.file = new ZipFile(file);
		FastModelJsonLoader.resource = this;
	}
	
	public JSONObject getBlockState(String name) {
		byte[] bytes = readEntry("assets/minecraft/blockstates/" + name + ".json");
		return new JSONObject(new String(bytes));
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

		List<JSONObject> model_data_list = null;
		if(json.has("variants")) {
			model_data_list = resolve_variants(state, json.getJSONObject("variants"));
		} else if(json.has("multipart")) {
			model_data_list = resolve_multipart(state, json.getJSONArray("multipart"));
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
			
			if(model_data.has("y")) {
				matrix.translateLocal(-8, -8, -8)
					.rotateLocalY(-(float)Math.toRadians(model_data.getNumber("y").floatValue()))
					.translateLocal(8, 8, 8);
			}
			
			if(model_data.has("x")) {
				matrix.translateLocal(-8, -8, -8)
					.rotateLocalX(-(float)Math.toRadians(model_data.getNumber("x").floatValue()))
					.translateLocal(8, 8, 8);
			}
			
			String path = model_data.getString("model");
			// 0.5 ms: 400 ms
			((BlockData)state).model_transform.add(matrix);
			((BlockData)state).model_objects.add(FastModelJsonLoader.loadModel(path));
			
			if(isWeighted) {
				// TODO: Figure out how to render weighted block picking
				break;
			}
			
			break;
		}
	}
	
	private List<JSONObject> resolve_multipart(IBlockData state, JSONArray array) {
//		System.out.println("################################################################");
//		System.out.println("Multipart: " + array);
//		System.out.println("--------------------------------------------\n");
		
		List<JSONObject> model_data = null;
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
						if(state.getStateList().matches(convertMultipartWhenToString(or_list.getJSONObject(0)))) {
							model_data = models;
							break;
						}
					}
				} else if(state.getStateList().matches(convertMultipartWhenToString(when))) {
					model_data = models;
				}
			} else {
				model_data = models;
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
	public void loadBlocks() {
		ConcurrentLinkedQueue<IBlockData> queue = new ConcurrentLinkedQueue<>(BlockDataManager.getStates());
		
		final AtomicInteger count = new AtomicInteger();
		final int size = queue.size();
		
		int threads = Runtime.getRuntime().availableProcessors() - 1;
		// 1: 2262,1858
		// 2: 1597,3296
		// 3: 1370,0111
		// 4: 1312,8602
		threads = 2;
		List<Thread> workers = new ArrayList<>();
		
		Runnable loader = () -> {
			while(!queue.isEmpty()) {
				IBlockData data = queue.poll();
				if(data == null) break;
				
				BlockData dta = (BlockData)data;
				resolveState(data);
				
				System.out.printf("\rLoading: (%d) / (%d)", count.getAndIncrement(), size);
				for(IBlockData child : dta.getChildren()) {
					resolveState(child);
				}
			}
		};
		
		TimerUtils.begin();

		TimerUtils.beginAverage();
		try {
			for(int i = 0; i < threads; i++) {
				Thread thread = new Thread(loader, "Resource-Loader-Worker#" + i);
				thread.start();
				workers.add(thread);
			}
			
			for(int i = 0; i < threads; i++) {
				Thread worker = workers.get(i);
				worker.join();
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		double average = TimerUtils.endAverage() / 1000000.0;
		double time = TimerUtils.end() / 1000000.0;
		
		System.out.printf("\nTook: %.4f ms, per item (%.4f)\n", time, time / (size + 0.0));
		System.out.printf("\nAverage: %.4f ms per item. Items %d\n", average, TimerUtils.getTimes());
	}
}
