package saves;

import util.MergeArray;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.zip.DataFormatException;

public class Saves {

	public enum FileSegment {
		MAP
	}
	
	// holds the list of all saved class instances after loading/saving a file
	List<Saveable> savedObjects;
	// all of the constructor methods for each Saveable in savedObjects must be mapped in here bc Interfaces dont let you specify constructors which is annoying asf cause i dont want to have to use an abstract class cause it will be redundant
	HashMap<FileSegment, Function<byte[], Saveable>> savedObjectsLoadMethods = new HashMap<>();


	
	public static final Version DEFUALT_VERSION = new Version((byte) 0, (byte) 1, (byte) 0);
	public static final String WORLDS_FOLDER = ".\\worlds";
	private String worldPath;
	private String fileName;
	private Version version;
	private Map map;
	
	private static final byte[] ID_BYTES = {6, 15, 'G', 'M', 'A', (byte) 153};
	
	private static final int BUFFER_SIZE = 32; // kinda arbitrary, but should definitely make bigger, just want to keep small rn in case it breaks anything
	
	
//	decided to use 2 bytes just cause then when checking a file, if it is broken, there is a lower chance of NEW_FILE_SEGMENT bytes randomly being in the correct spot
	public static final byte[] NEW_FILE_SEGMENT = {(byte) 234, 10};
	
	
	
	
	//	this ones for loading an existing world
	public Saves(String fileName) throws DataFormatException, IOException {
		updateHashMap();
		updatePath(fileName);
		
		loadFile();
		
	}
	
	
	
//	these 2 are for generating a new world file
	public Saves(String fileName, Map map) throws IOException {
		updateHashMap();
		updatePath(fileName);
		this.map = map;
		version = DEFUALT_VERSION;
		saveNewWorld();
	}

	
	
	public Saves(String fileName, Map map, Version version) throws IOException {
		updateHashMap();
		updatePath(fileName);
		this.map = map;
		this.version = version;
		saveNewWorld();
	}
	
	
	
	private void updateHashMap() {
		savedObjectsLoadMethods.put(FileSegment.MAP, Map::new);
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
//		bulk of the data, dont affect how following data is processed
		List<Saveable> fileSegments = new ArrayList<>();
		fileSegments.add(map);
		
		
		for (Saveable segment : fileSegments) {
			
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


//		should probably make into its own function
		savedObjects = new ArrayList<>();
		FileSegment[] fileSegments = FileSegment.values();
		
		for (int i = 0; i < binarySegments.size(); i++) {
			Function<byte[], Saveable> instantiationFunction = savedObjectsLoadMethods.get(fileSegments[i]);
			
			Saveable classInstance = instantiationFunction.apply(binarySegments.get(i));
			savedObjects.add(classInstance);
		}
		
//		if there was multiple segments being loaded i would define them to their respective attributes here
		map = (Map) savedObjects.get(0);
	}
	
	
	
	private List<byte[]> loadFileSegments() throws IOException, DataFormatException {
		
		InputStream stream = new FileInputStream(worldPath);
		
//		these are read outside of the loop because they affect how the rest of the data is processed
		byte[] header = getFileSegment(stream, ID_BYTES.length);
		checkIdBytes(header); // throws error if fails
		
		byte[] versionBytes = getFileSegment(stream, Version.BYTE_LENGTH);
		version = new Version(versionBytes);
		
		
		List<byte[]> fileSegments = new ArrayList<>();
		
		for (int i = 0; i < FileSegment.values().length; i++) {

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
	
	public Map getMap() {
		return map;
	}
	
}