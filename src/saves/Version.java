package saves;

public class Version extends Saveable {
	private final byte major;
	private final byte minor;
	private final byte patch;
	public static final int BYTE_LENGTH = 3;
	
	Version(byte major, byte minor, byte patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	Version(byte[] data) {
		if (data.length != BYTE_LENGTH) {
			throw new IllegalArgumentException("the length of the provided array must be: " + BYTE_LENGTH + ", provided: " + data.length);
		}
		
		this.major = data[0];
		this.minor = data[1];
		this.patch = data[2];
	}
	
	public String toString() {
		String string = "v" + major + "." + minor;
		if (patch > 0) {
			string += "." + patch;
		}
		return string;
	}
	
	public boolean equals(Version version) {
		return version.major == this.major && version.minor == this.minor && version.patch == this.patch;
	}
	
	
	public byte[] getBinary() {
		return new byte[]{major, minor, patch};
	}
}