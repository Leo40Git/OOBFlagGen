package com.leo.oobfg;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Config {

	private Config() {
	}

	public static final long BUILD = 1;

	public static final String KEY_CONFIG_BUILD = "config_build";
	public static final String KEY_SKIP_UPDATE_CHECK = "skip_update_check";

	private static Preferences config;

	public static void init() {
		if (config == null)
			config = Preferences.userNodeForPackage(OOBFlagGen.class);
		config.putLong(KEY_CONFIG_BUILD, BUILD);
	}

	public static void wipe() {
		try {
			config.removeNode();
			config = null;
		} catch (BackingStoreException e) {
			System.err.println("Error while wiping configuration!");
			e.printStackTrace();
		}
	}

	public static String get(String key, String def) {
		return config.get(key, def);
	}

	public static void set(String key, String value) {
		config.put(key, value);
	}

	public static boolean getBoolean(String key, boolean def) {
		return config.getBoolean(key, def);
	}

	public static void setBoolean(String key, boolean value) {
		config.putBoolean(key, value);
	}

}
