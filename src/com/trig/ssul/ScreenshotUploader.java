package com.trig.ssul;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

public class ScreenshotUploader {

	//Loads the configuration required to upload images to web server
	private SiteConfig config;
	
	public ScreenshotUploader(SiteConfig config) {
		this.config = config;
	}
	
	public void upload(File screenshot) {
		HttpPost post = new HttpPost(config.getBaseURL() + "/" + config.getUploadScript()); //Create the POST request to the specified web server and script
		if(screenshot.exists()) { //Ensure that the screenshot we want to upload exists.
			HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("sharex", screenshot).addTextBody("secret", config.getSecret()).build(); //Create our POST request
			HttpClient client = HttpClientBuilder.create().build(); //Create our HTTP client
			post.setEntity(entity); //Add our request entity to the POST request
			System.out.println("Created upload entity...");
			try {
				System.out.println("Uploading image...");
				HttpResponse response = client.execute(post); //Execute the request
				System.out.println("Upload complete!");
				
				//The upload was complete. Now we'll start processing the response.
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); //Read the response
				String url = reader.readLine(); //We know the server is only going to send back one line, and it will contain the image URL.
				System.out.println("Link: " + url); //Print the link to the console
				
				//Copy the link to the clipboard
				StringSelection selection = new StringSelection(url);
				Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
				cb.setContents(selection, null);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} else {
			System.out.println("Image does not exist! Path: " + screenshot.getAbsolutePath());
		}
	}
}
