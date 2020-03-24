package com.g4mesoft.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.g4mesoft.Application;

public final class FileUtil {

	private static final String COMMENT_START = "#";
	
	private FileUtil() {
	}

	private static void checkExistingFile(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("file is null!");
		if (!file.isFile())
			throw new IOException("file either doesn't exist or is a directory!");
	}

	private static void checkNotDirectory(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("file is null!");
		if (file.isDirectory())
			throw new IOException("file is a directory!");
	}
	
	public static Map<String, String> readConfigFile(File file, String splitter) throws IOException {
		checkExistingFile(file);
		
		return readConfigFile(new FileReader(file), splitter);
	}

	public static Map<String, String> readConfigFile(Reader reader, String splitter) throws IOException {
		if (reader == null)
			throw new NullPointerException("reader is null!");
		if (splitter == null)
			throw new NullPointerException("splitter is null!");

		Map<String, String> result = new HashMap<String, String>();
		
		BufferedReader br = new BufferedReader(reader);
		
		String line;
		int lineCount = 0;
		while ((line = br.readLine()) != null) {
			lineCount++;
			
			if (line.isEmpty() || line.startsWith(COMMENT_START))
				continue;
			
			String[] entry = line.split(splitter);
			
			if (entry.length != 2)
				throw new RuntimeException(String.format("Invalid entry %s, should be 'x%sy' at line %d", line, splitter, lineCount));
			if (entry[0].isEmpty())
				throw new RuntimeException(String.format("Key for value %s is undefined at line %d", entry[1], lineCount));
			if (result.put(entry[0], entry[1]) != null)
				throw new RuntimeException(String.format("Duplicate key %s at line %d", entry[0], lineCount));
		}
		
		br.close();

		return result;
	}
	
	public static void writeConfigFile(File file, Map<String, String> entryMap, String splitter) throws IOException {
		writeConfigFile(file, true, entryMap, splitter);
	}

	public static void writeConfigFile(File file, boolean genDirectory, Map<String, String> entryMap, String splitter) throws IOException {
		createFile(file, genDirectory);
		
		writeConfig(new FileWriter(file), entryMap, splitter);
	}
	
	public static void writeConfig(Writer writer, Map<String, String> entryMap, String splitter) throws IOException {
		if (writer == null)
			throw new NullPointerException("writer is null!");
		if (entryMap == null)
			throw new NullPointerException("entryMap is null!");
		if (splitter == null)
			throw new NullPointerException("splitter is null!");
		
		BufferedWriter bw = new BufferedWriter(writer);
		
		Iterator<Map.Entry<String, String>> entries = entryMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			
			String key = entry.getKey();
			if (key == null)
				continue;
			
			bw.write(key);
			bw.write(splitter);
			bw.write(entry.getValue());
			bw.newLine();
		}
		
		bw.close();
	}

	public static String readAsString(Reader reader) throws IOException {
		if (reader == null)
			throw new IllegalArgumentException("reader is null!");
		
		BufferedReader br = new BufferedReader(reader);
		StringBuilder result = new StringBuilder();
		
		String line = null;
		while ((line = br.readLine()) != null) {
			result.append(line);
			result.append('\n');
		}
			
		br.close();
		
		return result.toString();
	}
	
	public static String readAsString(File file) throws IOException {
		checkExistingFile(file);
		
		return readAsString(new FileReader(file));
	}
	
	public static String readAsString(InputStream stream) throws IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream is null!");
		
		return readAsString(stream, StandardCharsets.UTF_8);
	}
	
	public static String readAsString(InputStream stream, Charset charset) throws IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream is null!");
		if (charset == null)
			throw new IllegalArgumentException("charset is null!");
		
		return readAsString(new InputStreamReader(stream, charset));
	}
	
	public static String readAsString(URL url) throws IOException {
		if (url == null)
			throw new IllegalArgumentException("url is null!");
		
		return readAsString(url.openStream());
	}
	
	private static final int BUFFER_SIZE = 128;

	public static byte[] readAsBytes(InputStream stream) throws IOException {
		return readAsBytes(stream, true);
	}
	
	public static byte[] readAsBytes(InputStream stream, boolean closeStream) throws IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream is null!");

		List<byte[]> buffer = new ArrayList<byte[]>();
		
		int p = 0;
		int br = 0;
		
		byte[] cb;
		int btr;
		
		buffer.add(cb = new byte[BUFFER_SIZE]);

		while (true) {
			if (p >= cb.length) {
				buffer.add(cb = new byte[BUFFER_SIZE]);
				btr = BUFFER_SIZE;
				p = 0;
			} else btr = cb.length - p;
			
			int bytesRead = stream.read(cb, p, btr);
			if (bytesRead < 0)
				break;
			
			p += bytesRead;
			br += bytesRead;
		}
		
		if (closeStream)
			stream.close();

		btr = p;
		byte[] data = new byte[br];
		ListIterator<byte[]> bufferItr = buffer.listIterator(buffer.size() - 1);
		while(true) {
			br -= btr;

			System.arraycopy(cb, 0, data, br, btr);
			
			if (!bufferItr.hasPrevious()) 
				break;
			cb = bufferItr.previous();
			btr = cb.length;
		}
		
		buffer.clear();
		buffer = null;

		return data;
	}

	public static byte[] readAsBytes(URL url) throws IOException {
		if (url == null)
			throw new IllegalArgumentException("url is null!");
		
		return readAsBytes(url.openStream());
	}
	
	public static byte[] readAsBytes(File file) throws IOException {
		checkExistingFile(file);
		
		return Files.readAllBytes(file.toPath());
	}

	public static void writeAsString(Writer writer, String data) throws IOException {
		if (writer == null)
			throw new IllegalArgumentException("writer is null!");
		if (data == null)
			throw new IllegalArgumentException("data is null!");
		
		BufferedWriter bw = new BufferedWriter(writer);

		bw.write(data);
		
		bw.flush();
		bw.close();
	}
	
	public static void writeAsString(OutputStream stream, String data) throws IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream is null!");
		
		writeAsString(new OutputStreamWriter(stream), data);
	}
	
	public static void writeAsString(File file, boolean genDirectory, String data) throws IOException {
		createFile(file, genDirectory);
		
		writeAsString(new FileWriter(file), data);
	}

	public static void writeAsBytes(OutputStream stream, byte[] data) throws IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream is null!");
		if (data == null)
			throw new IllegalArgumentException("data is null!");
		
		stream.write(data, 0, data.length);

		stream.flush();
		stream.close();
	}
	
	public static void writeAsBytes(File file, boolean genDirectory, byte[] data) throws IOException {
		createFile(file, genDirectory);
		
		writeAsBytes(new FileOutputStream(file), data);
	}
	
	public static void createFile(File file, boolean genDirectory) throws IOException {
		checkNotDirectory(file);
		
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			if (!genDirectory)
				throw new IOException("The file's path doesn't exist!");
			if (!parent.mkdirs())
				throw new IOException("Unable to generate parent directories!");
		}
		
		if (!file.exists())
			file.createNewFile();
	}

	public static InputStream getInputStream(String filename, boolean internal) {
		if (internal)
			return FileUtil.class.getResourceAsStream(filename);

		try {
			return new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			Application.errorOccurred(e);
		}

		return null;
	}

	public static void closeInputStreamSilent(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}
}
