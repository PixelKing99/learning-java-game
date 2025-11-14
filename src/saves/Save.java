package saves;

import util.MergeArray;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.DataFormatException;

public class Save {

//	new structure:
//	i dont think i really need this cause rn everything is kinda simple and i havent decided the functionality i want so i would probably do all this and then be fine with just one file

//	Save: manages files which are stored in a folder
//		SaveFile: a file inside Save which can have multiple segments
//			Saveable: interface which means it can be a file segment

//	idk how ima do this, definitely wrong syntax here
//	SaveFile[] files = {new SaveFile("path", {Map::new, Entities::new}),
//						new SaveFile("path", {Config::new})}
//
//	public class Save {
//		Save(String path) {
//			for (SaveFile saveFile : files) {
//				saveFile.load()
//				// smthn with enums to store the file in a way that has nice syntax?
//
//			}
//		}
//	}
	
	
	public static final Version DEFUALT_VERSION = new Version((byte) 0, (byte) 1, (byte) 0);
	public static final String WORLDS_FOLDER = ".\\worlds";
	private String worldPath;
	private String fileName;
	private Version version;
	
	private static final byte[] ID_BYTES = {6, 15, 'G', 'M', 'A', (byte) 153};
	
	private static final int BUFFER_SIZE = 32; // kinda arbitrary, but should definitely make bigger, just want to keep small rn in case it breaks anything
	
	
	//	decided to use 2 bytes just cause then when checking a file, if it is broken, there is a lower chance of NEW_FILE_SEGMENT bytes randomly being in the correct spot
	public static final byte[] NEW_FILE_SEGMENT = {(byte) 234, 10};
	
	final HashMap<Class<? extends Saveable>, Saveable> segments = new LinkedHashMap<>(1); // Linked to preserve order (otherwise the order segments are saved in the file could be random or smthn)
	
	
	
	//	this ones for loading an existing world
	public Save(String fileName) throws DataFormatException, IOException {
		updateSegmentMap();
		updatePath(fileName);
		
		loadFile();
		
	}
	
	
	//	these 2 are for generating a new world file
	public Save(String fileName, Map map) throws IOException {
		updateSegmentMap();
		updatePath(fileName);
		segments.put(map.getClass(), map);
		version = DEFUALT_VERSION;
		saveNewWorld();
	}
	
	
	public Save(String fileName, Map map, Version version) throws IOException {
		updateSegmentMap();
		updatePath(fileName);
		segments.put(map.getClass(), map);
		this.version = version;
		saveNewWorld();
	}
	
//	this is the only place new file segments need to be added
	private void updateSegmentMap() {
		segments.put(Map.class, null);
	}
	
	
	private void updatePath(String fileName) {
		this.fileName = fileName;
		this.worldPath = WORLDS_FOLDER + "\\" + fileName + ".idk";
	}
	
	
	private void saveNewWorld() throws IOException {
		
		File newWorld = new File(worldPath);

//		could make it do the thing world(1) but i can be bothered rn
		if (!newWorld.createNewFile()) {
			throw new FileAlreadyExistsException("file '" + worldPath + "' already exists");
		}
		
		byte[] binary = generateBinary();
		writeToFile(binary);
	}
	
	
	private byte[] intToBytes(int x) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(x);
		return buffer.array();
	}
	
	
	private byte[] generateBinary() {
		
		MergeArray<byte[]> binary = new MergeArray<>(byte[]::new);


//		header
//		fixed length, store data required for processing the file segments
		binary.addArray(ID_BYTES);
		binary.addArray(NEW_FILE_SEGMENT);
		
		binary.addArray(version.getBinary());
		binary.addArray(NEW_FILE_SEGMENT);


//		file segments
//		bulk of the data, doesnt affect how following data is processed
		for (Saveable segment : segments.values()) {
		
//			debug
			if (segment == null) {
				throw new IllegalStateException("a Saveable segment has not been initialized, this is a problem with the way your code is written\ncurrent state:\n"+ getStringOfSegmentMap());
			}
			
			binary.addArray(intToBytes(segment.getBinary().length));
			binary.addArray(segment.getBinary());
			binary.addArray(NEW_FILE_SEGMENT);
		}
		
		return binary.getMergedArray();
	}
	
	
	private void writeToFile(byte[] data) throws IOException {
		OutputStream outStream = new FileOutputStream(worldPath);
		byte[] dataBuffer = new byte[BUFFER_SIZE];
		
		for (int i = 0; i < data.length; i += BUFFER_SIZE) {
			
			int writeAmount = Math.min(BUFFER_SIZE, data.length - i);
			
			System.arraycopy(data, i, dataBuffer, 0, writeAmount);
			
			outStream.write(dataBuffer, 0, writeAmount);
		}
	}
	
	
	public static String[] getWorlds() {
		
		String[] worlds = new File(WORLDS_FOLDER).list();
		for (String world : worlds) {
			System.out.println(world);
		}
		
		return worlds;
	}
	
	
	private void loadFile() throws DataFormatException, IOException {
		
		List<byte[]> binarySegments = loadFileSegments();
		
		Class<? extends Saveable>[] c = new Class[segments.size()];
		
		segments.keySet().toArray(c);
		for (int i = 0; i < binarySegments.size(); i++) {
			
			try {
				Constructor<? extends Saveable> segmentConstructor = c[i].getConstructor(byte[].class);
				segments.put(c[i], segmentConstructor.newInstance(binarySegments.get(i)));
				
//				this shouldnt happen as long as Saveable classes are implemented correctly(?)
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException("retrieving the constructor from the Saveable class '"+c[i].getName()+"' went wrong somehow\n" + e);
			}
			
		}

//		if there was multiple segments being loaded i would define them to their respective attributes here
//		map = (Map) savedObjects.get(0);
	}
	
	
	private List<byte[]> loadFileSegments() throws IOException, DataFormatException {
		
		InputStream stream = new FileInputStream(worldPath);

//		these are read outside of the loop because they affect how the rest of the data is processed
		byte[] header = getFileSegment(stream, ID_BYTES.length);
		checkIdBytes(header); // throws error if fails
		
		byte[] versionBytes = getFileSegment(stream, Version.BYTE_LENGTH);
		version = new Version(versionBytes);
		
		
		List<byte[]> fileSegments = new ArrayList<>();
		
		for (int i = 0; i < segments.size(); i++) {
			
			int size = readSegmentSize(stream);
			
			fileSegments.add(getFileSegment(stream, size));
		}
		
		return fileSegments;
	}
	
	
	//	at the beginning of new fileSegment the first 4 bytes make up an int which is the size of the fileSegment so this just returns the value of those bytes
	private int readSegmentSize(InputStream stream) throws IOException {
		
		byte[] buffer = new byte[4];
		int bytesRead = stream.read(buffer);
		
		if (bytesRead != 4) {
			throw new EOFException("less than the expected amount of data is present\nreceived: " + bytesRead + " bytes\nexpected: 4 bytes\n");
		}
		
		ByteBuffer intBuffer = ByteBuffer.wrap(buffer);
		return intBuffer.getInt();
	}
	
	
	private byte[] getFileSegment(InputStream stream, int length) throws IOException, DataFormatException {
		
		byte[] buffer;
		MergeArray<byte[]> mergeFileSegment = new MergeArray<>(byte[]::new);
		int bytesRead;


//		i dont think adding a buffer here really does anything since all of the data is going into memory and staying there
//		ig it shouldnt make things too much worse and if i come back and improve this or change/expand the functionality it might be useful to have it like this already
		for (int i = 0; i < length; i += BUFFER_SIZE) {
			
			buffer = new byte[Math.min(BUFFER_SIZE, length - i)];
			
			bytesRead = stream.read(buffer);
			
			if (bytesRead < buffer.length) {
				throw new EOFException("less than the expected amount of data is present\nreceived: " + (i - BUFFER_SIZE + bytesRead) + " bytes\nexpected: " + length + " bytes");
			}
			
			mergeFileSegment.addArray(buffer);
		}
		
		
		byte[] newSegmentBytes = new byte[NEW_FILE_SEGMENT.length];
		bytesRead = stream.read(newSegmentBytes);
		
		if (bytesRead != NEW_FILE_SEGMENT.length) {
			throw new EOFException("the NEW_FILE_SEGMENT bytes were missing\nreceived: " + bytesRead + " bytes\nexpected: " + NEW_FILE_SEGMENT.length + " bytes");
		}
		if (newSegmentBytes[0] != NEW_FILE_SEGMENT[0] || newSegmentBytes[1] != NEW_FILE_SEGMENT[1]) {
			throw new DataFormatException("the next bytes are not NEW_FILE_SEGMENT\nreceived: " + newSegmentBytes[0] + " " + newSegmentBytes[1] + "\nexpected: " + NEW_FILE_SEGMENT[0] + " " + NEW_FILE_SEGMENT[1]);
		}
		
		return mergeFileSegment.getMergedArray();
	}
	
	
	private static void checkIdBytes(byte[] header) throws DataFormatException {
		if (header.length != ID_BYTES.length) {
			throwIdBytesError("the ID bytes is the wrong length\nexpected: " + ID_BYTES.length + " bytes\nreceived: " + header.length + " bytes", header);
		}
		
		for (int i = 0; i < ID_BYTES.length; i++) {
			if (header[i] != ID_BYTES[i]) {
				throwIdBytesError("the ID bytes are invalid\nat index: " + i + "\nexpected: " + ID_BYTES[i] + "\nreceived: " + header[i], header);
			}
		}
	}
	
	private static void throwIdBytesError(String message, byte[] receivedBytes) throws DataFormatException {
		StringBuilder output = new StringBuilder();
		
		output.append("expected: ");
		for (byte value : ID_BYTES) {
			output.append(value).append(" ");
		}
		
		output.append("\nreceived: ");
		for (byte value : receivedBytes) {
			output.append(value).append(" ");
		}
		
		output.append("\n");
		output.append(message);
		throw new DataFormatException(output.toString());
	}
	
	
	public Version getVersion() {
		return version;
	}
	
	/**	returns the instance of a Saveable class that was loaded from the file
	 * @param type the class that extends Saveable is stored in the file
	 * @param <T>
	 * @return the instance of the class
	 */
	public <T extends Saveable> T get(Class<T> type) {
		T x = (T) segments.get(type);
		if (x == null) {
			throw new RuntimeException("error getting " + type.getName() + "\n" + getStringOfSegmentMap());
		}
		return x;
	}
	
	private String getStringOfSegmentMap() {
		StringBuilder s = new StringBuilder();
		
		Class[] keys = new Class[segments.size()];
		segments.keySet().toArray(keys);
		
		for (int i = 0; i < segments.size(); i++) {
			s.append("k: ").append(keys[i]).append("   v: ").append(segments.get(keys[i])).append("\n");
		}
		
		return s.toString();
	}
	
}