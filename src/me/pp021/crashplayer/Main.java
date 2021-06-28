package me.pp021.crashplayer;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		getCommand("crash").setExecutor(new Crash());
	}
}
