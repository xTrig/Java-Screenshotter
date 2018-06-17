# Java-Screenshotter
Couldn't find a "gyazo" type program for linux that allowed me to upload to my own web server. So I made this program to fix this issue.

## How to use
Run jar file anyway you want. The main config GUI should open. Once configured, you may close the GUI (Note that clicking the exit button will CLOSE the entire application).

To screenshot, left click the icon in the system tray. This will bring up a screenshot preview. You may click OK to save the entire screenshot, or select a region of the screen.

## Configuration

- Site: The link to the site you will be uploading to. Must contain a trailing '/'
- Script: The name of the PHP script you will be sending the file to
- Secret: The API key for authentication
