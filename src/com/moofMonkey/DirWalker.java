package com.moofMonkey;

import java.io.File;

public class DirWalker {
	public DirWalker(File file, Main main) throws Throwable {
		String path = file.getAbsolutePath();
		if(file.getName().equals("maps") || file.getName().equals("materials"))
			return;
		if (file.isDirectory()) {
			if (file.listFiles() != null)
				for (File f : file.listFiles())
					new DirWalker(f, main);
		} else {
			boolean found = false;
			for(String s : Main.compileableExtensionsList)
				if(path.endsWith('.' + s)) {
					System.out.println(path);
					main.compile(path);
					found = true;
					break;
				}
			if(!found)
				main.copy(path);
		}
	}
}
