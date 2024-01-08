package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import pap.ptmp.game.PtmPGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setResizable(true);
		config.setTitle("Piwo to moje Paliwo");
		config.setWindowIcon("piwo16.png", "piwo32.png", "piwo64.png");
		// new Lwjgl3Application(new PtmPGame(), config);
		new Lwjgl3Application(new UI(), config);
	}
}
