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
		
		public static final IBlockState a = add(new StateBoolean("attached", List.of(true, false)));
		public static final IBlockState b = add(new StateBoolean("bottom", List.of(true, false)));
		public static final IBlockState c = add(new StateBoolean("conditional", List.of(true, false)));
		public static final IBlockState d = add(new StateBoolean("disarmed", List.of(true, false)));
		public static final IBlockState e = add(new StateBoolean("drag", List.of(true, false)));
		public static final IBlockState f = add(new StateBoolean("enabled", List.of(true, false)));
		public static final IBlockState g = add(new StateBoolean("extended", List.of(true, false)));
		public static final IBlockState h = add(new StateBoolean("eye", List.of(true, false)));
		public static final IBlockState i = add(new StateBoolean("falling", List.of(true, false)));
		public static final IBlockState j = add(new StateBoolean("hanging", List.of(true, false)));
		public static final IBlockState k = add(new StateBoolean("has_bottle_0", List.of(true, false)));
		public static final IBlockState l = add(new StateBoolean("has_bottle_1", List.of(true, false)));
		public static final IBlockState m = add(new StateBoolean("has_bottle_2", List.of(true, false)));
		public static final IBlockState n = add(new StateBoolean("has_record", List.of(true, false)));
		public static final IBlockState o = add(new StateBoolean("has_book", List.of(true, false)));
		public static final IBlockState p = add(new StateBoolean("inverted", List.of(true, false)));
		public static final IBlockState q = add(new StateBoolean("in_wall", List.of(true, false)));
		public static final IBlockState r = add(new StateBoolean("lit", List.of(true, false)));
		public static final IBlockState s = add(new StateBoolean("locked", List.of(true, false)));
		public static final IBlockState t = add(new StateBoolean("occupied", List.of(true, false)));
		public static final IBlockState u = add(new StateBoolean("open", List.of(true, false)));
		public static final IBlockState v = add(new StateBoolean("persistent", List.of(true, false)));
		public static final IBlockState w = add(new StateBoolean("powered", List.of(true, false)));
		public static final IBlockState x = add(new StateBoolean("short", List.of(true, false)));
		public static final IBlockState y = add(new StateBoolean("signal_fire", List.of(true, false)));
		public static final IBlockState z = add(new StateBoolean("snowy", List.of(true, false)));
		public static final IBlockState A = add(new StateBoolean("triggered", List.of(true, false)));
		public static final IBlockState B = add(new StateBoolean("unstable", List.of(true, false)));
		public static final IBlockState C = add(new StateBoolean("waterlogged", List.of(true, false)));
		public static final IBlockState D = add(new StateBoolean("vine_end", List.of(true, false)));
		public static final IBlockState E = add(new StateList("axis", List.of("x", "z")));
		public static final IBlockState F = add(new StateList("axis", List.of("x", "y", "z")));
		public static final IBlockState G = add(new StateBoolean("up", List.of(true, false)));
		public static final IBlockState H = add(new StateBoolean("down", List.of(true, false)));
		public static final IBlockState I = add(new StateBoolean("north", List.of(true, false)));
		public static final IBlockState J = add(new StateBoolean("east", List.of(true, false)));
		public static final IBlockState K = add(new StateBoolean("south", List.of(true, false)));
		public static final IBlockState L = add(new StateBoolean("west", List.of(true, false)));
		public static final IBlockState M = add(new StateList("facing", List.of("north", "east", "south", "west", "up", "down")));
		public static final IBlockState N = add(new StateList("facing", List.of("down", "north", "south", "west", "east")));
		public static final IBlockState O = add(new StateList("facing", List.of("north", "south", "west", "east")));
		public static final IBlockState P = add(new StateList("orientation", List.of("DOWN_EAST", "DOWN_NORTH", "DOWN_SOUTH", "DOWN_WEST", "UP_EAST", "UP_NORTH", "UP_SOUTH", "UP_WEST", "WEST_UP", "EAST_UP", "NORTH_UP", "SOUTH_UP")));
		public static final IBlockState Q = add(new StateList("face", List.of("FLOOR", "WALL", "CEILING")));
		public static final IBlockState R = add(new StateList("attachment", List.of("FLOOR", "CEILING", "SINGLE_WALL", "DOUBLE_WALL")));
		public static final IBlockState S = add(new StateList("east", List.of("none", "low", "tall")));
		public static final IBlockState T = add(new StateList("north", List.of("none", "low", "tall")));
		public static final IBlockState U = add(new StateList("south", List.of("none", "low", "tall")));
		public static final IBlockState V = add(new StateList("west", List.of("none", "low", "tall")));
		public static final IBlockState W = add(new StateList("east", List.of("up", "side", "none")));
		public static final IBlockState X = add(new StateList("north", List.of("up", "side", "none")));
		public static final IBlockState Y = add(new StateList("south", List.of("up", "side", "none")));
		public static final IBlockState Z = add(new StateList("west", List.of("up", "side", "none")));
		public static final IBlockState aa = add(new StateList("half", List.of("upper", "lower")));
		public static final IBlockState ab = add(new StateList("half", List.of("top", "bottom")));
		public static final IBlockState ac = add(new StateList("shape", List.of("north_south", "east_west", "ascending_east", "ascending_west", "ascending_north", "ascending_south", "south_east", "south_west", "north_west", "north_east")));
		public static final IBlockState ad = add(new StateList("shape", List.of("north_south", "east_west", "ascending_east", "ascending_west", "ascending_north", "ascending_south")));
		public static final IBlockState ae = add(new StateInteger("age", List.of(0, 1)));
		public static final IBlockState af = add(new StateInteger("age", List.of(0, 1, 2)));
		public static final IBlockState ag = add(new StateInteger("age", List.of(0, 1, 2, 3)));
		public static final IBlockState ah = add(new StateInteger("age", List.of(0, 1, 2, 3, 4, 5)));
		public static final IBlockState ai = add(new StateInteger("age", List.of(0, 1, 2, 3, 4, 5, 6, 7)));
		public static final IBlockState aj = add(new StateInteger("age", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
		public static final IBlockState ak = add(new StateInteger("age", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)));
		public static final IBlockState al = add(new StateInteger("bites", List.of(0, 1, 2, 3, 4, 5, 6)));
		public static final IBlockState am = add(new StateInteger("delay", List.of(1, 2, 3, 4)));
		public static final IBlockState an = add(new StateInteger("distance", List.of(1, 2, 3, 4, 5, 6, 7)));
		public static final IBlockState ao = add(new StateInteger("eggs", List.of(1, 2, 3, 4)));
		public static final IBlockState ap = add(new StateInteger("hatch", List.of(0, 1, 2)));
		public static final IBlockState aq = add(new StateInteger("layers", List.of(1, 2, 3, 4, 5, 6, 7, 8)));
		public static final IBlockState ar = add(new StateInteger("level", List.of(0, 1, 2, 3)));
		public static final IBlockState as = add(new StateInteger("level", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)));
		public static final IBlockState at = add(new StateInteger("level", List.of(1, 2, 3, 4, 5, 6, 7, 8)));
		public static final IBlockState au = add(new StateInteger("honey_level", List.of(0, 1, 2, 3, 4, 5)));
		public static final IBlockState av = add(new StateInteger("level", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
		public static final IBlockState aw = add(new StateInteger("moisture", List.of(0, 1, 2, 3, 4, 5, 6, 7)));
		public static final IBlockState ax = add(new StateInteger("note", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)));
		public static final IBlockState ay = add(new StateInteger("pickles", List.of(1, 2, 3, 4)));
		public static final IBlockState az = add(new StateInteger("power", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
		public static final IBlockState aA = add(new StateInteger("stage", List.of(0, 1)));
		public static final IBlockState aB = add(new StateInteger("distance", List.of(0, 1, 2, 3, 4, 5, 6, 7)));
		public static final IBlockState aC = add(new StateInteger("charges", List.of(0, 1, 2, 3, 4)));
		public static final IBlockState aD = add(new StateInteger("rotation", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)));
		public static final IBlockState aE = add(new StateList("part", List.of("head", "foot")));
		public static final IBlockState aF = add(new StateList("type", List.of("SINGLE", "LEFT", "RIGHT")));
		public static final IBlockState aG = add(new StateList("mode", List.of("compare", "subtract")));
		public static final IBlockState aH = add(new StateList("hinge", List.of("left", "right")));
		public static final IBlockState aI = add(new StateList("instrument", List.of("HARP", "BASEDRUM", "SNARE", "HAT", "BASS", "FLUTE", "BELL", "GUITAR", "CHIME", "XYLOPHONE", "IRON_XYLOPHONE", "COW_BELL", "DIDGERIDOO", "BIT", "BANJO", "PLING")));
		public static final IBlockState aJ = add(new StateList("type", List.of("normal", "sticky")));
		public static final IBlockState aK = add(new StateList("type", List.of("top", "bottom", "double")));
		public static final IBlockState aL = add(new StateList("shape", List.of("straight", "inner_left", "inner_right", "outer_left", "outer_right")));
		public static final IBlockState aM = add(new StateList("mode", List.of("SAVE", "LOAD", "CORNER", "DATA")));
		public static final IBlockState aN = add(new StateList("leaves", List.of("none", "small", "large")));
		
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
	}
}
