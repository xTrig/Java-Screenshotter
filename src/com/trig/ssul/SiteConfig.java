package com.trig.ssul;

public class SiteConfig {

	private String secret = ""; //The API key to upload screenshots
	private String baseURL = ""; //The webserver to upload to.
	private String uploadScript = "sharex.php"; //The name of the script that will accept the screenshot
	
	public String getSecret() {
		return secret;
	}
	
	public String getBaseURL() {
		return baseURL;
	}
	
	public String getUploadScript() {
		return uploadScript;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	
	public void setUploadScript(String uploadScript) {
		this.uploadScript = uploadScript;
	}
}
