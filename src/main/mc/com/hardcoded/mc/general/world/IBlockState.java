package com.hardcoded.mc.general.world;

import java.util.*;

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
				if(value == null || !val.equals(value.toString())) return false;
			}
			
			return true;
		}
		
		public boolean matches(String state) {
			for(String part : state.split(",")) {
				String[] obj = part.split("=");
				Object value = map.get(obj[0]);
				if(value == null || !value.toString().equals(obj[1])) return false;
			}
			
			return true;
		}
	}
	
	static class States {
		public static final IBlockState a = new StateBoolean("attached", List.of(true, false));
		public static final IBlockState b = new StateBoolean("bottom", List.of(true, false));
		public static final IBlockState c = new StateBoolean("conditional", List.of(true, false));
		public static final IBlockState d = new StateBoolean("disarmed", List.of(true, false));
		public static final IBlockState e = new StateBoolean("drag", List.of(true, false));
		public static final IBlockState f = new StateBoolean("enabled", List.of(true, false));
		public static final IBlockState g = new StateBoolean("extended", List.of(true, false));
		public static final IBlockState h = new StateBoolean("eye", List.of(true, false));
		public static final IBlockState i = new StateBoolean("falling", List.of(true, false));
		public static final IBlockState j = new StateBoolean("hanging", List.of(true, false));
		public static final IBlockState k = new StateBoolean("has_bottle_0", List.of(true, false));
		public static final IBlockState l = new StateBoolean("has_bottle_1", List.of(true, false));
		public static final IBlockState m = new StateBoolean("has_bottle_2", List.of(true, false));
		public static final IBlockState n = new StateBoolean("has_record", List.of(true, false));
		public static final IBlockState o = new StateBoolean("has_book", List.of(true, false));
		public static final IBlockState p = new StateBoolean("inverted", List.of(true, false));
		public static final IBlockState q = new StateBoolean("in_wall", List.of(true, false));
		public static final IBlockState r = new StateBoolean("lit", List.of(true, false));
		public static final IBlockState s = new StateBoolean("locked", List.of(true, false));
		public static final IBlockState t = new StateBoolean("occupied", List.of(true, false));
		public static final IBlockState u = new StateBoolean("open", List.of(true, false));
		public static final IBlockState v = new StateBoolean("persistent", List.of(true, false));
		public static final IBlockState w = new StateBoolean("powered", List.of(true, false));
		public static final IBlockState x = new StateBoolean("short", List.of(true, false));
		public static final IBlockState y = new StateBoolean("signal_fire", List.of(true, false));
		public static final IBlockState z = new StateBoolean("snowy", List.of(true, false));
		public static final IBlockState A = new StateBoolean("triggered", List.of(true, false));
		public static final IBlockState B = new StateBoolean("unstable", List.of(true, false));
		public static final IBlockState C = new StateBoolean("waterlogged", List.of(true, false));
		public static final IBlockState D = new StateBoolean("vine_end", List.of(true, false));
		public static final IBlockState E = new StateList("axis", List.of("x", "z"));
		public static final IBlockState F = new StateList("axis", List.of("x", "y", "z"));
		public static final IBlockState G = new StateBoolean("up", List.of(true, false));
		public static final IBlockState H = new StateBoolean("down", List.of(true, false));
		public static final IBlockState I = new StateBoolean("north", List.of(true, false));
		public static final IBlockState J = new StateBoolean("east", List.of(true, false));
		public static final IBlockState K = new StateBoolean("south", List.of(true, false));
		public static final IBlockState L = new StateBoolean("west", List.of(true, false));
		public static final IBlockState M = new StateList("facing", List.of("north", "east", "south", "west", "up", "down"));
		public static final IBlockState N = new StateList("facing", List.of("down", "north", "south", "west", "east"));
		public static final IBlockState O = new StateList("facing", List.of("north", "south", "west", "east"));
		public static final IBlockState P = new StateList("orientation", List.of("DOWN_EAST", "DOWN_NORTH", "DOWN_SOUTH", "DOWN_WEST", "UP_EAST", "UP_NORTH", "UP_SOUTH", "UP_WEST", "WEST_UP", "EAST_UP", "NORTH_UP", "SOUTH_UP"));
		public static final IBlockState Q = new StateList("face", List.of("FLOOR", "WALL", "CEILING"));
		public static final IBlockState R = new StateList("attachment", List.of("FLOOR", "CEILING", "SINGLE_WALL", "DOUBLE_WALL"));
		public static final IBlockState S = new StateList("east", List.of("none", "low", "tall"));
		public static final IBlockState T = new StateList("north", List.of("none", "low", "tall"));
		public static final IBlockState U = new StateList("south", List.of("none", "low", "tall"));
		public static final IBlockState V = new StateList("west", List.of("none", "low", "tall"));
		public static final IBlockState W = new StateList("east", List.of("up", "side", "none"));
		public static final IBlockState X = new StateList("north", List.of("up", "side", "none"));
		public static final IBlockState Y = new StateList("south", List.of("up", "side", "none"));
		public static final IBlockState Z = new StateList("west", List.of("up", "side", "none"));
		public static final IBlockState aa = new StateList("half", List.of("upper", "lower"));
		public static final IBlockState ab = new StateList("half", List.of("top", "bottom"));
		public static final IBlockState ac = new StateList("shape", List.of("north_south", "east_west", "ascending_east", "ascending_west", "ascending_north", "ascending_south", "south_east", "south_west", "north_west", "north_east"));
		public static final IBlockState ad = new StateList("shape", List.of("north_south", "east_west", "ascending_east", "ascending_west", "ascending_north", "ascending_south"));
		public static final IBlockState ae = new StateInteger("age", List.of(0, 1));
		public static final IBlockState af = new StateInteger("age", List.of(0, 1, 2));
		public static final IBlockState ag = new StateInteger("age", List.of(0, 1, 2, 3));
		public static final IBlockState ah = new StateInteger("age", List.of(0, 1, 2, 3, 4, 5));
		public static final IBlockState ai = new StateInteger("age", List.of(0, 1, 2, 3, 4, 5, 6, 7));
		public static final IBlockState aj = new StateInteger("age", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		public static final IBlockState ak = new StateInteger("age", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25));
		public static final IBlockState al = new StateInteger("bites", List.of(0, 1, 2, 3, 4, 5, 6));
		public static final IBlockState am = new StateInteger("delay", List.of(1, 2, 3, 4));
		public static final IBlockState an = new StateInteger("distance", List.of(1, 2, 3, 4, 5, 6, 7));
		public static final IBlockState ao = new StateInteger("eggs", List.of(1, 2, 3, 4));
		public static final IBlockState ap = new StateInteger("hatch", List.of(0, 1, 2));
		public static final IBlockState aq = new StateInteger("layers", List.of(1, 2, 3, 4, 5, 6, 7, 8));
		public static final IBlockState ar = new StateInteger("level", List.of(0, 1, 2, 3));
		public static final IBlockState as = new StateInteger("level", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8));
		public static final IBlockState at = new StateInteger("level", List.of(1, 2, 3, 4, 5, 6, 7, 8));
		public static final IBlockState au = new StateInteger("honey_level", List.of(0, 1, 2, 3, 4, 5));
		public static final IBlockState av = new StateInteger("level", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		public static final IBlockState aw = new StateInteger("moisture", List.of(0, 1, 2, 3, 4, 5, 6, 7));
		public static final IBlockState ax = new StateInteger("note", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
		public static final IBlockState ay = new StateInteger("pickles", List.of(1, 2, 3, 4));
		public static final IBlockState az = new StateInteger("power", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		public static final IBlockState aA = new StateInteger("stage", List.of(0, 1));
		public static final IBlockState aB = new StateInteger("distance", List.of(0, 1, 2, 3, 4, 5, 6, 7));
		public static final IBlockState aC = new StateInteger("charges", List.of(0, 1, 2, 3, 4));
		public static final IBlockState aD = new StateInteger("rotation", List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		public static final IBlockState aE = new StateList("part", List.of("head", "foot"));
		public static final IBlockState aF = new StateList("type", List.of("SINGLE", "LEFT", "RIGHT"));
		public static final IBlockState aG = new StateList("mode", List.of("compare", "subtract"));
		public static final IBlockState aH = new StateList("hinge", List.of("left", "right"));
		public static final IBlockState aI = new StateList("instrument", List.of("HARP", "BASEDRUM", "SNARE", "HAT", "BASS", "FLUTE", "BELL", "GUITAR", "CHIME", "XYLOPHONE", "IRON_XYLOPHONE", "COW_BELL", "DIDGERIDOO", "BIT", "BANJO", "PLING"));
		public static final IBlockState aJ = new StateList("type", List.of("normal", "sticky"));
		public static final IBlockState aK = new StateList("type", List.of("top", "bottom", "double"));
		public static final IBlockState aL = new StateList("shape", List.of("straight", "inner_left", "inner_right", "outer_left", "outer_right"));
		public static final IBlockState aM = new StateList("mode", List.of("SAVE", "LOAD", "CORNER", "DATA"));
		public static final IBlockState aN = new StateList("leaves", List.of("none", "small", "large"));
	}
}
