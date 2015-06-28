package database.main;

import java.io.IOException;

public class Launcher {
	public void pull() throws IOException {
		Runtime.getRuntime().exec("C:/WINDOWS/system32/cmd.exe /c start Z:/pull.bat");
	}

	public void push() throws IOException {
		Runtime.getRuntime().exec("C:/WINDOWS/system32/cmd.exe /c start Z:/push.bat");
	}

	public void connect() throws IOException, InterruptedException {
		Process connection = Runtime.getRuntime().exec("cmd.exe /c net use Z: https://webdav.hidrive.strato.com/users/maxptrs/Server /user:maxptrs ***REMOVED*** /persistent:no");
		connection.waitFor();
	}
}