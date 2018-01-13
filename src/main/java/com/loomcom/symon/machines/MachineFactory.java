package com.loomcom.symon.machines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineFactory {
	// not to be constructed
	private MachineFactory() {
	}

	public enum MachineEnum {
		SYMON("Symon", "symon") {
			public Machine create() throws Exception {
				return new SymonMachine();
			}
		},
		MULTICOMP("Multicomp", "multicomp") {
			public Machine create() throws Exception {
				return new MulticompMachine();
			}
		},
		SIMPLE("Simple", "simple") {
			public Machine create() throws Exception {
				return new SimpleMachine();
			}
		},
		CNP1("CNP-1", "cnp1") {
			public Machine create() throws Exception {
				return new CNP1Machine();
			}
		};

		private final String name;
		private final String cmdLine;

		public abstract Machine create() throws Exception;

		private static final Map<String, MachineEnum> map = new HashMap<String, MachineEnum>();

		static {
			for (MachineEnum type : MachineEnum.values()) {
				map.put(type.name, type);
				map.put(type.cmdLine, type);
			}
		}

		private MachineEnum(String name, String cmdLine) {
			this.name = name;
			this.cmdLine = cmdLine;
		}

		public String getName() {
			return name;
		}

		public String getCmdLine() {
			return cmdLine;
		}

		public static MachineEnum fromString(String name) {
			if (map.containsKey(name)) {
				return map.get(name);
			} else {
				return null;
			}
		}

	}

	public static Machine createMachine(String machineName) throws Exception {
		return createMachine(MachineEnum.fromString(machineName));
	}
	
	public static Machine createMachine(MachineEnum mach) throws Exception {
		return mach.create();
	}

	public static String[] getFriendlyNames() {
		List<String> names = new ArrayList<String>();
		for (MachineEnum mach : MachineEnum.values())
			names.add(mach.getName());
		return names.toArray(new String[0]);
	}

	public static String[] getCmdLineNames() {
		List<String> names = new ArrayList<String>();
		for (MachineEnum mach : MachineEnum.values())
			names.add(mach.getCmdLine());
		return names.toArray(new String[0]);
	}
}
