package com.moofMonkey;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Thread {
	static {
		if (OSInfo.getOs() != OSInfo.OS.WINDOWS) {
			System.err.println("Sorry, this tool doesn't works for non-windows OS, blame Valve developers");
			System.exit(1);
		}
	}
	String dota2Path, adoonName;
	File addonDir, tmpDir, outDir, rawOutDir;
	File out, outNew;
	
	public static void main(String[] args) throws Throwable {
		new Main(args).start();
	}

	public Main(String[] args) throws Throwable {
		tmpDir = new File(NameGen.get());
		while(tmpDir.exists())
			tmpDir = new File(NameGen.get());
		tmpDir.mkdir();
		fetchArgs(args);
	}
	
	public static void deleteFileOrFolder(final Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(final Path file,
					final BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(final Path file, final IOException e) {
				return handleException(e);
			}

			private FileVisitResult handleException(final IOException e) {
				e.printStackTrace();
				return FileVisitResult.TERMINATE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
					throws IOException {
				if (e != null)
					return handleException(e);
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	public static void copyFileOrFolder(Path fromPath, Path toPath) throws IOException {
		Files.walkFileTree(fromPath, new SimpleFileVisitor<Path>() {
			private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path targetPath = toPath.resolve(fromPath.relativize(dir));
				if(!Files.exists(targetPath)){
					Files.createDirectory(targetPath);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
				return FileVisitResult.CONTINUE;
			}
		});
	}


	private void fetchArgs(String[] args) throws Throwable {
		if(args.length != 3) {
			System.out.println("Usage: java -jar Source2Compiler.jar <dota2 path> <addon name> <output directory>");
			System.exit(2);
		}
		dota2Path = args[0];
		if(dota2Path.endsWith("\\") || dota2Path.endsWith("/"))
			dota2Path = dota2Path.substring(0, dota2Path.length() - 1);
		adoonName = args[1];
		addonDir = new File(dota2Path, "content/dota_addons/" + adoonName);
		outDir = new File(args[2]);
		if(outDir.exists())
			deleteFileOrFolder(outDir.toPath());
		out = new File(outDir.getParentFile(), outDir.getName() + ".vpk");
		outNew = new File(outDir.getParentFile(), "pak01_dir.vpk");
		if(out.exists())
			out.delete();
		if(outNew.exists())
			outNew.delete();
		outDir.mkdirs();
	}
	
	private void addBaseArgs(ArrayList<String> ar) {
		ar.add(dota2Path + "/game/bin/win64/resourcecompiler.exe");
		ar.add("-f");
		ar.add("-outroot");
		ar.add(tmpDir.getAbsolutePath());
		ar.add("-i");
	}

	private static final String[] compileableExtensions = new String[] { "xml", "css", "js", "png" };
	public static final List<String> compileableExtensionsList = (List<String>) Arrays.asList(compileableExtensions);
	/***
	 * So many try/catch IS NOT AN ERROR!
	 */
	@Override
	public void run() {
		try {
			new DirWalker(addonDir, this);
		} catch(Throwable t) {
			t.printStackTrace();
		}
		try {
			copyFileOrFolder(new File(tmpDir, "dota_addons/" + adoonName).toPath(), outDir.toPath());
			ArrayList<String> cmds = new ArrayList<>();
			cmds.add(new File("vpk", "vpk.exe").getAbsolutePath());
			cmds.add(outDir.getAbsolutePath());
			String[] cmdarray = new String[cmds.size()];
			cmds.toArray(cmdarray);
			Process p = Runtime.getRuntime().exec(cmdarray);
			p.waitFor();
			out.renameTo(outNew);
		} catch(Throwable t) {
			t.printStackTrace();
			System.err.println("Are you sure this addon isn't empty?");
		}
		try {
			deleteFileOrFolder(tmpDir.toPath());
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}

	public void compile(String path) throws Throwable {
		ArrayList<String> cmds = new ArrayList<>();
		addBaseArgs(cmds);
		cmds.add(path);
		String[] cmdarray = new String[cmds.size()];
		cmds.toArray(cmdarray);
		for(String s : cmds)
			System.out.print(s + " ");
		System.out.println();
		Process p = Runtime.getRuntime().exec(cmdarray);
		p.waitFor();
	}

	/// ---------------------------------------------

	public void move(String from) throws Throwable {
		String to = outDir.getAbsolutePath() + from.replace(addonDir.getAbsolutePath(), "");
		move(from, to);
	}

	public void move(String _from, String _to) throws Throwable {
		File from = new File(_from);
		File to = new File(_to);
		move(from, to);
	}
	
	public void move(File from, File to) throws Throwable {
		System.out.println("Move from: " + from.getAbsolutePath() + " to: " + to.getAbsolutePath());
		to.mkdirs();
		Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/// ---------------------------------------------
	
	public void copy(String from) throws Throwable {
		String to = outDir.getAbsolutePath() + from.replace(addonDir.getAbsolutePath(), "");
		copy(from, to);
	}

	public void copy(String _from, String _to) throws Throwable {
		File from = new File(_from);
		File to = new File(_to);
		copy(from, to);
	}
	
	public void copy(File from, File to) throws Throwable {
		to.mkdirs();
		Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
}
