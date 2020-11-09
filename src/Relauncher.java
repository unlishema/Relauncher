import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;

public class Relauncher extends PApplet {
	private Config old_config;
	private Config new_config;

	private PImage bg;
	private File old_launcher, new_launcher, dl_launcher;

	public void settings() {
		this.size(125, 125);
	}

	public void setup() {
		this.bg = this.loadImage("Relauncher.png");

		// Get Old Config & Setup Default Value
		final File old_config_file = new File(this.sketchPath() + "\\launcher.config");
		if (!old_config_file.exists()) {
			println("Old Launcher Config is Missing!");
			this.exit();
			return;
		}
		this.old_config = new Config(old_config_file.getAbsolutePath());
		this.old_config.setDefault("launcher", "GameLauncher.exe");

		// Get New Config & Setup Default Value
		final File new_config_file = new File(this.sketchPath() + "\\launcher.config_dl");
		if (!new_config_file.exists()) {
			println("New Launcher Config is Missing!");
			this.exit();
			return;
		}
		this.new_config = new Config(new_config_file.getAbsolutePath());
		this.new_config.setDefault("launcher", "GameLauncher.exe");

		// Setup Old Launcher File
		this.old_launcher = new File(this.sketchPath() + "\\" + this.old_config.getProperty("launcher"));
		if (!this.old_launcher.exists()) {
			println("Old Launcher is Missing!");
			this.exit();
			return;
		}

		// Setup New Launcher File
		this.new_launcher = new File(this.sketchPath() + "\\" + this.new_config.getProperty("launcher"));

		// Setup Downloaded Launcher File
		this.dl_launcher = new File(this.sketchPath() + "\\" + this.new_config.getProperty("launcher") + "_dl");
		if (!this.dl_launcher.exists()) {
			println("New Launcher is Missing!");
			this.exit();
			return;
		}

		// If Old & New Launcher have same name then Wait until the launcher exits
		if (this.old_launcher.getName().equals(this.new_launcher.getName()))
			while (this.isProcessRunning(this.old_launcher.getName())) this.delay(1000);

		// Remove Old Launcher & Rename New Launcher
		this.old_launcher.delete();
		this.dl_launcher.renameTo(this.new_launcher);

		// Update launcher config with new version
		this.old_config.file.delete();
		this.new_config.file.renameTo(this.old_config.file);

		// Run New Launcher
		try {
			new ProcessBuilder(this.new_launcher.getAbsolutePath()).start();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		this.exit();
	}

	private boolean isProcessRunning(final String processName) {
		try {
			final Process process = new ProcessBuilder("tasklist.exe").start();
			String pidInfo = this.toString(process.getInputStream());
			return pidInfo.contains(processName);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private String toString(final InputStream inputStream) {
		final Scanner scanner = new Scanner(inputStream, "UTF-8");
		scanner.useDelimiter("\\A");
		final String string = scanner.hasNext() ? scanner.next() : "";
		scanner.close();

		return string;
	}

	// Set Frame to Not Decorated
	public processing.core.PSurface initSurface() {
		final processing.core.PSurface pSurface = super.initSurface();
		((processing.awt.PSurfaceAWT.SmoothCanvas) ((processing.awt.PSurfaceAWT) surface).getNative()).getFrame()
				.setUndecorated(true);
		return pSurface;
	}

	public void draw() {
		this.background(0);
		this.image(this.bg, 0, 0);
	}

	public static void main(final String[] args) {
		PApplet.main("Relauncher");
	}
}