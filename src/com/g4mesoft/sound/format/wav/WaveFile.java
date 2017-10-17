package com.g4mesoft.sound.format.wav;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;

import com.g4mesoft.sound.format.AlawDecoder;
import com.g4mesoft.sound.format.AudioFile;
import com.g4mesoft.sound.format.AudioHelper;
import com.g4mesoft.sound.format.AudioParsingException;
import com.g4mesoft.sound.format.UlawDecoder;
import com.g4mesoft.util.MemoryUtil;

public class WaveFile extends AudioFile {

	/**
	 * The maximum number of bytes able to get reset
	 * by the InputStream.
	 */
	private static final int MAX_TOLERANCE_DEPTH = 1024;
	
	/*
	 * The wave audio format contains multiple headers
	 * with string indicators. Ex. "RIFF", "WAVE" etc.
	 */
	
	/**
	 * RIFF chunk indicator at the beginning of a wave
	 * file.
	 */
	private static final int RIFF_DEC = 0x52494646;
	// private static final String RIFF = "RIFF";
	
	/**
	 * WAVE chunk indicator at the beginning after file
	 * size.
	 */
	private static final int WAVE_DEC = 0x57415645;
	// private static final String WAVE = "WAVE";
	
	/**
	 * FMT chunk indicator
	 */
	private static final int FMT_DEC = 0x666D7420;
	// private static final String FMT  = "fmt ";
	
	/**
	 * DATA (main audio data) chunk indicator
	 */
	private static final int DATA_DEC = 0x64617461;
	// private static final String DATA = "data";
	
	/*
	 * The audio data can be stored in different encodings.
	 * The following are the encodings specified in the
	 * definition of the wave format: PCM, A-LAW & U-LAW.
	 */
	
	/**
	 * PCM encoded audio data.
	 */
	private static final int PCM_ENCODING  = 0x1;
	/**
	 * ALAW encoded audio data.
	 */
	private static final int ALAW_ENCODING = 0x6;
	/**
	 * ULAW encoded audio data.
	 */
	private static final int ULAW_ENCODING = 0x7;
	
	private final byte[] data;
	private final AudioFormat format;
	
	private WaveFile(byte[] data, AudioFormat format) {
		this.data = data;
		this.format = format;
	}

	/**
	 * Loads wave header and audio data from an {@code InputStream}.
	 * If the {@code InputStream} does not supply a valid wave header, 
	 * the returned result will be null and the {@code InputStream} 
	 * will be reset. If {@link java.io.InputStream#markSupported() 
	 * is.markSupported} returns false, and marking is not supported,
	 * the returned result will be null. If the header is valid and
	 * the WaveFile is loaded successfully, the WaveFile will be
	 * returned.
	 * <br><br>
	 * The default wave file header is a total of 44 bytes or more 
	 * if storing extra parameters. This WaveFile loader will however 
	 * skip over these custom parameters, as they are currently not 
	 * supported. The following table shows how a typical header might 
	 * be formatted.
	 * <br><br>
	 * <center><table WIDTH=500 BORDER=1>
	 *   <tr>
	 *     <th>Byte range</th>
	 *     <th>Sample value</th>
	 *     <th>Description</th>
	 *   </tr>
	 *   <tr>
	 *     <td>1-4</td>
	 *     <td>"RIFF"</td>
	 *     <td>A riff file format marker (ASCII)</td>
	 *   </tr>
	 *   <tr>
	 *     <td>5-8</td>
	 *     <td>File size (Integer)</td>
	 *     <td>Size of overall file (minus 8) in bytes. Stored 
	 *         as a 4 byte integer with little endian byte-order</td>
	 *   </tr>
	 *   <tr>
	 *     <td>9-12</td>
	 *     <td>"WAVE"</td>
	 *     <td>Wave file format marker (ASCII)</td>
	 *   </tr>
	 *   <tr>
	 *     <td>13-16</td>
	 *     <td>"fmt "</td>
	 *     <td>Format chunk marker, including trailing space (ASCII)</td>
	 *   </tr>
	 *   <tr>
	 *     <td>17-20</td>
	 *     <td>16</td>
	 *     <td>Length of following format data in bytes</td>
	 *   </tr>
	 *   <tr>
	 *     <td>21-22</td>
	 *     <td>1</td>
	 *     <td>Type of format stored in 2 byte integer. 1=PCM, 6=ALAW, 
	 *         7=ULAW.</td>
	 *   </tr>
	 *   <tr>
	 *     <td>23-24</td>
	 *     <td>2</td>
	 *     <td>Number of channels stored in 2 byte integer. 1=mono, 
	 *         2=stereo etc.</td>
	 *   </tr>
	 *   <tr>
	 *     <td>25-28</td>
	 *     <td>44100</td>
	 *     <td>Sample rate (samples per second) stored in 4 byte integer. 
	 *         Commonly 44100, 48000 etc.</td>
	 *   </tr>
	 *   <tr>
	 *     <td>29-32</td>
	 *     <td>176400</td>
	 *     <td>Byte rate (sample_rate * bitsPerSample * channels / 8) stored 
	 *         in 4 byte integer.</td>
	 *   </tr>
	 *   <tr>
	 *     <td>33-34</td>
	 *     <td>4</td>
	 *     <td>Size of each block (aligned samples for both channels) in 
	 *         bytes.</td>
	 *   </tr>
	 *   <tr>
	 *     <td>35-36</td>
	 *     <td>16</td>
	 *     <td>Bits per sample.</td>
	 *   </tr>
	 *   <tr>
	 *     <td>37-?</td>
	 *     <td>?</td>
	 *     <td>Custom parameters. Usually these are not included in a wave 
	 *         header. The size is determined by the size of the fmt chunk.</td>
	 *   </tr>
	 *   <tr>
	 *     <td>37-40</td>
	 *     <td>"data"</td>
	 *     <td>Data chunk marker (ASCII)</td>
	 *   </tr>
	 *   <tr>
	 *     <td>41-44</td>
	 *     <td>Data chunk size</td>
	 *     <td>Size of data chunk (raw audio data)</td>
	 *   </tr>
	 *   <tr>
	 *     <td>45-?</td>
	 *     <td>Data</td>
	 *     <td>The raw audio data</td>
	 *   </tr>
	 * </table></center>
	 * <br>
	 * <i>Note: WAV format uses little-endian byte order to store values,
	 * so they have to be converted in code to make sense. Strings are
	 * stored in big-endian byte order (1 byte per character)</i>
	 * <br><br>
	 * @param is	-	The {@code InputStream} streaming the wave data.
	 * @return  null, if the {@code InputStream} does not contain a valid 
	 * 			wave format, or if marking/resetting is not supported. 
	 * 			Otherwise this method will return a WaveFile containing 
	 * 			all information needed for playback.
	 * 
	 * @throws IOException if an I/O error occurs.
	 * @throws AudioParsingException if header is valid, but audio data 
	 * 								 is corrupted or nonexistent. Note that
	 * 								 the stream cannot be reset when this
	 * 								 exception is thrown.
	 * 
	 * @see java.io.InputStream
	 */
	public static WaveFile loadWave(InputStream is) throws IOException, AudioParsingException {
		// We need to be able to reset the 
		// InputStream if header is not a 
		// wave file format.
		if (!is.markSupported())
			return null;
		is.mark(MAX_TOLERANCE_DEPTH);
		
		byte[] buffer = new byte[4];
		
		int br = AudioHelper.readBytes(is, buffer, 4, 0);
		int beginCode = MemoryUtil.bigEndianToInt(buffer, 0);
		
		// Search for RIFF beginCode
		while (beginCode != RIFF_DEC) {
			if (br++ >= MAX_TOLERANCE_DEPTH) {
				is.reset();
				return null;
			}
			
			int b = AudioHelper.readByte(is);
			if (b == -1) {
				is.reset();
				return null;
			}
			beginCode = (beginCode << 8) | b;
		}
		
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		// long fileSize = MemoryUtil.littleEndianToInt(buffer, 0);
		
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		if (MemoryUtil.bigEndianToInt(buffer, 0) != WAVE_DEC) { // wave marker
			is.reset();
			return null;
		}
		
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		if (MemoryUtil.bigEndianToInt(buffer, 0) != FMT_DEC) { // fmt chunk marker
			is.reset();
			return null;
		}
		
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		int fmtEnd = MemoryUtil.littleEndianToInt(buffer, 0) + br;
		
		br += AudioHelper.readBytes(is, buffer, 2, 0);
		int formatType = MemoryUtil.littleEndianToShortUnsignedInt(buffer, 0);
		br += AudioHelper.readBytes(is, buffer, 2, 0);
		int channels = MemoryUtil.littleEndianToShortUnsignedInt(buffer, 0);
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		int sampleRate = MemoryUtil.littleEndianToInt(buffer, 0);
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		int byteRate = MemoryUtil.littleEndianToInt(buffer, 0);
		br += AudioHelper.readBytes(is, buffer, 2, 0);
		int blockAlign = MemoryUtil.littleEndianToShortUnsignedInt(buffer, 0);
		br += AudioHelper.readBytes(is, buffer, 2, 0);
		int bitsPerSample = MemoryUtil.littleEndianToShortUnsignedInt(buffer, 0);
		
		AudioFormat.Encoding encoding;
		switch(formatType) {
		case PCM_ENCODING:
			encoding = AudioFormat.Encoding.PCM_SIGNED;
			break;
		case ALAW_ENCODING:
			encoding = AudioFormat.Encoding.ALAW;
			break;
		case ULAW_ENCODING:
			encoding = AudioFormat.Encoding.ULAW;
			break;
		default:
			// Format is not supported (or end of stream)
			is.reset();
			return null;
		}
		
		// Skip over unsupported parameters
		while (fmtEnd > br) {
			if (is.read() == -1) {
				is.reset();
				return null;
			}
			
			br++;
		}
		
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		if (MemoryUtil.bigEndianToInt(buffer, 0) != DATA_DEC) { // data chunk marker
			is.reset();
			return null;
		}
		
		// If we are working with a BufferedInputStream, 
		// the buffer will allocate extra memory if it has 
		// a valid mark. As the header was valid, we assume, 
		// that the user doesn't want to read from the stream
		// afterwards.
		is.mark(-1);
		
		br += AudioHelper.readBytes(is, buffer, 4, 0);
		int dataChunkSize = MemoryUtil.littleEndianToInt(buffer, 0);

		// Read main audio data
		buffer = new byte[dataChunkSize];
		if (dataChunkSize > 0) {
			br = 0;
			while (br < dataChunkSize) {
				int nbr = is.read(buffer, br, dataChunkSize - br);
				if (nbr == -1)
					break;
				br += nbr;
			}
			
			if (br <= 0)
				throw new AudioParsingException("Audio file is corrupted");
			
			// Make sure we have a valid amount of frames
			br -= br % blockAlign;
			
			if (br != dataChunkSize) {
				// Audio file might be corrupted
				// but header seems fine. Continue.
				buffer = Arrays.copyOf(buffer, br);
			}
		}

		// If data is not formatted in PCM, we
		// need to decode it. Note that both ULAW
		// and ALAW decoding will be little endian
		// byte order, so no need to change the
		// format accordingly.
		if (encoding == AudioFormat.Encoding.ULAW) {
			buffer = UlawDecoder.decode(buffer);
			encoding = UlawDecoder.getDecodedEncoding();
		}
		if (encoding == AudioFormat.Encoding.ALAW) {
			buffer = AlawDecoder.decode(buffer);
			encoding = AlawDecoder.getDecodedEncoding();
		}

		// The format is read from the header.
		// NOTE: all data is stored in 
		// little_endian byte order in wave files.
		AudioFormat format = new AudioFormat(encoding,
		                                     sampleRate, 
		                                     bitsPerSample, 
		                                     channels, 
		                                     blockAlign, 
		                                     byteRate / blockAlign, 
		                                     false);

		return new WaveFile(buffer, format);
	}
	
	/**
	 * @return  The audio format of the audio data.
	 * @see #getData()
	 */
	@Override
	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * Copies the raw PCM audio data in this WaveFile into
	 * the destination array.
	 * 
	 * @param dst		-	The destination array
	 * @param srcPos	-	The starting position in the
	 * 						raw audio data array.
	 * @param dstPos	-	The destination start position
	 * @param len		-	The number of bytes to be copied.
	 * 
	 * @return The amount of bytes actually copied to
	 * 	       the destination.
	 * @throws IndexOutOfBoundsException If copying would cause 
	 * 									 access of data outside 
	 * 									 array bounds.
	 * 
	 * @see #getData()
	 */
	@Override
	public int getData(byte[] dst, int srcPos, int dstPos, int len) {
		if (len > data.length - srcPos)
			len = data.length - srcPos;
		System.arraycopy(data, srcPos, dst, dstPos, len);
		return len;
	}
	
	/**
	 * Calculates the number of frames in this wave file.
	 * 
	 * @return The number of playable frames stored in this
	 *         wave file.
	 */
	@Override
	public long getLengthInFrames() {
		return data.length / format.getFrameSize();
	}
}
