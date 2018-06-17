package com.trig.ssul;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author steven
 *
 */
public class GUI extends JFrame {

	private JPanel contentPane;

	private Robot robot = null;
	private ScreenCaptureRectangle scr;
	private JTextField siteField;
	private JTextField scriptField;
	private JTextField secretField;
	

	/**
	 * Create the frame.
	 */
	public GUI() {
		setResizable(false);
		setTitle("Screenshot Uploader v0.0.1");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblApp = new JLabel(getTitle());
		lblApp.setBounds(118, 32, 212, 15);
		contentPane.add(lblApp);
		
		JLabel lblSite = new JLabel("Site:");
		lblSite.setBounds(12, 93, 87, 15);
		contentPane.add(lblSite);
		
		JLabel lblScript = new JLabel("Script:");
		lblScript.setBounds(12, 120, 70, 15);
		contentPane.add(lblScript);
		
		JLabel lblSecret = new JLabel("Secret:");
		lblSecret.setBounds(12, 147, 70, 15);
		contentPane.add(lblSecret);
		
		siteField = new JTextField(Main.getSiteConfig().getBaseURL());
		siteField.setBounds(78, 91, 184, 19);
		contentPane.add(siteField);
		siteField.setColumns(10);
		
		scriptField = new JTextField(Main.getSiteConfig().getUploadScript());
		scriptField.setBounds(78, 118, 184, 19);
		contentPane.add(scriptField);
		scriptField.setColumns(10);
		
		secretField = new JTextField(Main.getSiteConfig().getSecret());
		secretField.setBounds(78, 147, 184, 19);
		contentPane.add(secretField);
		secretField.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSiteConfig();
			}
		});
		btnSave.setBounds(12, 211, 117, 25);
		contentPane.add(btnSave);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshSiteConfig();
			}
		});
		btnRefresh.setBounds(145, 211, 117, 25);
		contentPane.add(btnRefresh);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setBounds(321, 263, 117, 25);
		contentPane.add(btnExit);
		
		JLabel lblYyuMayClose = new JLabel("You may close this screen.");
		lblYyuMayClose.setBounds(250, 178, 212, 15);
		contentPane.add(lblYyuMayClose);
		setVisible(true);
		
		//Create the robot needed to screenshot
		try {
			robot = new Robot();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	

	/**
	 * This function is called from the Main class when a user left clicks the system tray icon.
	 * This will open the ScreenCapture screen, and return the selected area.
	 */
	public void startRegionSelection() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		BufferedImage screen = robot.createScreenCapture(new Rectangle(screenSize));
		scr = new ScreenCaptureRectangle(screen, this);
	}
	
	
	/**
	 * This method is called whenever the ScreenCapture selection is complete.
	 * @param region The region of the original screenshot to save.
	 */
	public void processSelection(Rectangle region) {
		if(region == null || region.isEmpty()) { //If the region is invalid, we assume they want the whole screenshot
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			region = new Rectangle(0, 0, screen.width, screen.height);
		}
		BufferedImage img = robot.createScreenCapture(region); //Create a screenshot from the region specified.
		playSound("screenshot.wav");
		try {
			System.out.print("Saving screenshot... ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss"); //We'll give the screenshot a name with the current time
			File screenshot = new File(Main.HOME + "/screenshots/" + sdf.format(new Date(System.currentTimeMillis())) + ".png"); //Create the File object
			ImageIO.write(img, "png", screenshot); //Write the image to the disk
			System.out.println(screenshot.getName());
			Main.createUploader().upload(screenshot); //Request an image uploader, and pass the screenshot file
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Saves the config from the main panel
	 */
	private void saveSiteConfig() {
		
		if(!siteField.getText().endsWith("/")) {
			siteField.setText(siteField.getText() + "/");
		}
		Main.getSiteConfig().setBaseURL(siteField.getText().trim());
		Main.getSiteConfig().setUploadScript(scriptField.getText().trim());
		Main.getSiteConfig().setSecret(secretField.getText().trim());
		Main.saveSiteConfig();
	}
	
	/**
	 * Reloads the config file into the main panel
	 */
	private void refreshSiteConfig() {
		Main.loadConfig();
		siteField.setText(Main.getSiteConfig().getBaseURL());
		scriptField.setText(Main.getSiteConfig().getUploadScript());
		secretField.setText(Main.getSiteConfig().getSecret());
	}
	
	private void playSound(String soundEffect) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(ClassLoader.getSystemResourceAsStream(soundEffect));
			
			Clip clip = AudioSystem.getClip();
			
			clip.open(audioIn);
	        clip.start();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
