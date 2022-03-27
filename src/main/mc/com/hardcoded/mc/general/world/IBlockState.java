package com.hardcoded.mc.general.world;

import java.util.*;
import java.util.stream.Collectors;

public interface IBlockState {
	String getName();
	List<?> getValues();
	int size();
	
	static abstract class BasicBlockState implements IBlockState {
		private final String name;
		
		private BasicBlockState(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public int size() {
			return getValues().size();
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return name.equals(obj);
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	static class StateBoolean extends BasicBlockState {
		private final List<Boolean> list;
		
		public StateBoolean(String name, List<Boolean> list) {
			super(name);
			this.list = list;
		}
		
		@Override
		public List<Boolean> getValues() {
			return list;
		}
	}
	
	static class StateInteger extends BasicBlockState {
		private final List<Integer> list;
		public StateInteger(String name, List<Integer> list) {
			super(name);
			this.list = list;
		}
		
		@Override
		public List<Integer> getValues() {
			return list;
		}
	}
	
	static class StateList extends BasicBlockState {
		private final List<?> list;
		
		public StateList(String name, List<?> list) {
			super(name);
			this.list = list;
		}
		
		@Override
		public List<?> getValues() {
			return list;
		}
	}
	
	// Container of states
	static class IBlockStateList {
		private Set<IBlockState> states;
		private Map<String, Object> map;
		
		public IBlockStateList() {
			states = new HashSet<>();
			map = new LinkedHashMap<>();
		}
		
		public IBlockStateList setState(IBlockState state, Object value) {
			List<?> values = state.getValues();
			if(values.contains(value)) {
				map.put(state.getName(), value);
				states.add(state);
			} else {
				// Failed to set state
			}
			
			return this;
		}
		
		public boolean isEmpty() {
			return map.isEmpty();
		}
		
		public Object getState(IBlockState state) {
			return map.get(state.getName());
		}
		
		public Set<IBlockState> values() {
			return states;
		}
		
		@Override
		public String toString() {
			String string = map.toString();
			return string.substring(1, string.length() - 1).replace(" ", "");
		}

		public boolean matches(Map<String, String> states) {
			for(String key : states.keySet()) {
				String val = states.get(key);
				Object value = map.get(key);
				if(value == null || !val.equalsIgnoreCase(value.toString())) return false;
			}
			
			return true;
		}
		
		public boolean matches(String state) {
			for(String part : state.split(",")) {
				String[] obj = part.split("=");
				Object value = map.get(obj[0]);
				if(value == null || !value.toString().equalsIgnoreCase(obj[1])) return false;
			}
			
			return true;
		}

		public boolean matchesAllowOr(Map<String, String> states) {
			for(String key : states.keySet()) {
				// This is the value this state holds
				String value = Objects.toString(map.get(key), null);
				if(value == null) return false;
				
				String val = states.get(key);
				
				// The first character in the value must never be '|'
				if(val.indexOf('|') < 0) {
					if(!val.equalsIgnoreCase(value)) return false;
				} else {
					boolean match = false;
					String[] array = val.split("\\|");
					for(String str : array) {
						if(str.equalsIgnoreCase(value)) {
							match = true;
							break;
						}
					}
					
					if(!match) return false;
				}
			}
			
			return true;
		}
	}
	
	static class States {
		private static final List<IBlockState> states = new ArrayList<>();
		
		public static final IBlockState _level = add(new StateInteger("level", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
		public static final IBlockState _power = add(new StateInteger("power", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
		
		private static final IBlockState add(IBlockState state) {
			states.add(state);
			return state;
		}
		
		public static boolean contains(IBlockState state, List<String> content) {
			// If content contains dublicates this could give false positives.
			// Make sure that all the items in the set is seen once to fix this
			// problem.
			
			List<?> list = state.getValues();
			if(list.size() != content.size()) return false;
			
			Set<String> set = list.stream().map(i -> i.toString()).collect(Collectors.toSet());
			for(String value : content) {
				if(!set.contains(value)) return false;
			}
			
			return true;
		}
		
		public static IBlockState find(String name, List<String> content) {
			for(IBlockState state : states) {
				if(state.getName().equals(name)) {
					if(contains(state, content)) {
						return state;
					}
				}
			}
			
			// The state was not defined so we define it here.
			{
				IBlockState new_state = add(new StateList(name, content));
				// TODO: Use logger
				System.out.printf("New state was added: [%s]\n", new_state);
				
				return new_state;
			}
		}
		
		public static IBlockState findByName(String name) {
			for(IBlockState state : states) {
				if(state.getName().equals(name)) {
					return state;
				}
			}
			
			return null;
		}
		
		public static void unloadStates() {
			states.clear();
		}
	}
}
