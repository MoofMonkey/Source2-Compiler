package com.moofMonkey;

public class OSInfo {
	public enum OS {
		WINDOWS, UNIX, POSIX_UNIX, MAC, OTHER
	}

	private static OS os = OS.OTHER;
	static {
		try {
			String osName = System.getProperty("os.name");
			if (osName == null)
				throw new Throwable("os.name not found");
			osName = osName.toLowerCase();
			if (osName.indexOf("windows") != -1)
				os = OS.WINDOWS;
			else if (osName.indexOf("linux") != -1 || osName.indexOf("mpe/ix") != -1 || osName.indexOf("freebsd") != -1
					|| osName.indexOf("irix") != -1 || osName.indexOf("digital unix") != -1
					|| osName.indexOf("unix") != -1 || osName.indexOf("ubuntu") != -1)
				os = OS.UNIX;
			else if (osName.indexOf("mac os x") != -1)
				os = OS.MAC;
			else if (osName.indexOf("sun os") != -1 || osName.indexOf("sunos") != -1 || osName.indexOf("solaris") != -1
					|| osName.indexOf("hp-ux") != -1 || osName.indexOf("aix") != -1)
				os = OS.POSIX_UNIX;
			else
				os = OS.OTHER;
		} catch (Throwable t) {
			os = OS.OTHER;
		}
	}

	public static OS getOs() {
		return os;
	}
}
