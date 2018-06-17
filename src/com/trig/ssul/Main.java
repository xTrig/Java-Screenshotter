package com.trig.ssul;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import it.sauronsoftware.junique.MessageHandler;

public class Main {
	
	private static GUI gui;
	public static final String HOME = System.getProperty("user.home") + "/ssul/"; //The home working directory for this application
	private static SiteConfig config;
	private static final String appId = "ssul"; //appId to lock application

	public static void main(String[] args) {
		if(checkForInstance()) { //Check if there is already an instance of this application running
			System.out.println("SESSION LOCKED");
			return;
		}
		createSystemTray(); //Create the system tray icon
		setup(); //Setup before we launch
		gui = new GUI(); //Show the GUI
	}
	
	public static ScreenshotUploader createUploader() {
		return new ScreenshotUploader(config);
	}
	
	
	private static void setup() {
		Gson gson = new Gson();
		//Create the directories
		
		try {
			File dir = new File(HOME);
			dir.mkdirs();
			File screenShotDir = new File(HOME + "/screenshots/");
			screenShotDir.mkdirs();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		File cfg = new File(HOME + "/config.json");
		if(!cfg.exists()) {
			try {
				cfg.createNewFile();
				BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("resources/config.json")));
				BufferedWriter writer = new BufferedWriter(new FileWriter(cfg));
				
				String data = reader.readLine();
				writer.write(data);
				writer.close();
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		loadConfig();
	}
	
	private static void createSystemTray() {
		if(SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = null;
			try {
				image = ImageIO.read(Main.class.getClassLoader().getResource("resources/logo.png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			PopupMenu trayPopupMenu = new PopupMenu();
			
			MenuItem action = new MenuItem("View");
		    action.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		            if(gui.isDisplayable()) {
		            	gui.show();
		            	gui.requestFocus();
		            } else {
		            	gui.dispose();
		            	gui = new GUI();
		            }
		        }
		    });     
		    trayPopupMenu.add(action);
		    
		    MenuItem close = new MenuItem("Close");
		    close.addActionListener(new ActionListener() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		            System.exit(0);             
		        }
		    });
		    trayPopupMenu.add(close);
		    
		    TrayIcon trayIcon = new TrayIcon(image, "Screenshot Uploader", trayPopupMenu);
		    trayIcon.setImageAutoSize(true);
		    
		    trayIcon.addMouseListener(new MouseListener() { //When the user left clicks the tray icon

				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("Starting screen capture...");
					gui.startRegionSelection();
				}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}
		    	
		    });
		    
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException awtException){
		        awtException.printStackTrace();
		    }
		}
	}
	
	public static SiteConfig getSiteConfig() {
		return config;
	}
	
	public static void saveSiteConfig() {
		Gson gson = new Gson();
		String json = gson.toJson(config);
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(HOME + "/config.json")));
			writer.write(json);
			writer.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public static void loadConfig() {
		Gson gson = new Gson();
		try {
			config = gson.fromJson(new FileReader(HOME + "/config.json"), SiteConfig.class);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
	}
	
	private static boolean checkForInstance() {
		boolean alreadyRunning;
	    try {
	        JUnique.acquireLock(appId, new MessageHandler() {
	            public String handle(String message) {
	                // A brand new argument received! Handle it!
	                return null;
	            }
	        });
	        alreadyRunning = false;
	    } catch (AlreadyLockedException e) {
	        alreadyRunning = true;
	    }
	    
	    return alreadyRunning;
	}

}
