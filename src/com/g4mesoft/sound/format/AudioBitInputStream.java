package com.g4mesoft.sound.format;

import java.io.IOException;
import java.io.InputStream;

import com.g4mesoft.math.MathUtils;

public class AudioBitInputStream extends InputStream {

	/**
	 * The default buffer size for the {@code AudioBitStream} (4 Kilobytes).
	 */
	private static final int DEFAULT_BUFFER_CAPACITY = 4096;

	/**
	 * The size of a single byte in bits.
	 */
	private static final int BYTE_SIZE = 8;

	/**
	 * The size of an integer in bits (4 bytes).
	 */
	private static final int INTEGER_SIZE = 4 * BYTE_SIZE;
	
	/**
	 * A mask table used for reading bits. When masking using MASK_TABLE[i],
	 * only the least {@code i} significant bits will be ones and the rest will
	 * be zeroes.
	 */
	private static final int[] MASK_TABLE = new int[] {
		0, 0x00000001, 0x00000003, 0x00000007, 0x0000000F,
		   0x0000001F, 0x0000003F, 0x0000007F, 0x000000FF,
		   0x000001FF, 0x000003FF, 0x000007FF, 0x00000FFF,
		   0x00001FFF, 0x00003FFF, 0x00007FFF, 0x0000FFFF,
		   0x0001FFFF, 0x0003FFFF, 0x0007FFFF, 0x000FFFFF,
		   0x001FFFFF, 0x003FFFFF, 0x007FFFFF, 0x00FFFFFF,
		   0x01FFFFFF, 0x03FFFFFF, 0x07FFFFFF, 0x0FFFFFFF,
		   0x1FFFFFFF, 0x3FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF
	};
	
	/**
	 * The buffer used to store the bit stream data temporarily. The buffer will
	 * never increase in size, instead the buffer will have a fixed size. When
	 * the buffer is full the older data will be overwritten by the newer data.
	 * This ensures an efficient write-time to the buffer when reading new data.
	 * The current position in the buffer and the number of bytes stored is
	 * given by {@link #pos} and {@link #count}.
	 */
	private byte[] buffer;

	/**
	 * The position of the next byte to be written / read from the byte-buffer.
	 * It will always be the case that {@code 0 <= pos < buffer.length}.
	 */
	private int pos;

	/**
	 * The amount of bytes that are currently stored in the buffer. This is also
	 * the number of bytes that can be restored from the bit stream at any point
	 * in time. The default value is zero, and will be increased by 1 for every
	 * byte that is read from the InputStream. Count will always be less than
	 * the length of the buffer. {@code count <= buffer.length}.
	 */
	private int count;
	
	/**
	 * The amount of bytes we've currently restored from the buffer. This 
	 * indicates the number of bytes that have to be read from the buffer before
	 * reading from the {@code InputStream}.
	 */
	private int avail;

	/**
	 * The last read byte from this bit stream or -1 if no byte has been read.
	 */
	private int bufferedByte;
	
	/**
	 * The amount of bits to be read remaining in the buffered byte. Note: this
	 * is always zero unless {@link #readBits(int)} was called.
	 */
	private int bitsRemaining;
	
	/**
	 * The number of bytes that have been read from the InputStream. If any
	 * number of bytes have been restored, this value will be changed to the
	 * amount of bytes that were read minus the amount of bytes that have been
	 * restored.
	 */
	private long bytesRead;
	
	/**
	 * The exact location, in bytes, at which the bit stream will crash if an
	 * attempt is made to read beyond this point. Or Long.MAX_VALUE if no limit
	 * is set.
	 */
	private long readLimitLocation;
	
	/**
	 * A boolean indicating whether we've hit the end of the InputStream.
	 */
	private boolean endOfStream;
	
	/**
	 * The {@code InputStream} given in the constructor.
	 */
	private InputStream is;
	
	public AudioBitInputStream(InputStream is) {
		this(is, DEFAULT_BUFFER_CAPACITY);
	}
	
	public AudioBitInputStream(InputStream is, int bufferCapacity) {
		if (is == null)
			throw new NullPointerException("InputStream is null!");
		if (bufferCapacity <= 0)
			throw new IllegalArgumentException("bufferCapacity must be positive!");
			
		this.is = is;
		bytesRead = 0L;

		// Initialize buffer.
		this.buffer = new byte[bufferCapacity];
		count = avail = pos = 0;
	
		// We have not read any bytes yet. Invalidate
		// the bufferedByte
		bufferedByte = -1;
		bitsRemaining = 0;
		
		endOfStream = false;
		
		invalidateBufferedBits();
		invalidateReadLimit();
	}
	
	/**
	 * @return The InputStream if it is currently possible to read data from it.
	 *         Cases where it will throw an exception include when it's the end
	 *         of the file and if the bit stream has been closed by a call to
	 *         the {@link #close()} method.
	 *         
	 * @throws IOException if the bit stream was closed.
	 * @see #close()
	 */
	private InputStream getStreamIfOpen() throws IOException {
		InputStream input = is;
		if (input == null)
			throw new IOException("Stream is closed");

		return input;
	}
	
	/**
	 * Increments the buffer position by the specified amount. This ensures that
	 * the position will wrap back around if it passes the capacity of the
	 * buffer, so {@code 0 <= pos < buffer.length}.
	 * 
	 * @param amount - The amount of bytes to increment the buffer-position by.
	 */
	private void incrementBufferPos(int amount) {
		// Increment the buffer position modulo
		// capacity. This will ensure that the
		// position will always wrap back around
		// so 0 <= pos < buffer.length.
		pos = (pos + amount) % buffer.length;
	}

	@Override
	public int read() throws IOException, ReadLimitReachedException {
		// This will automatically check if the
		// #close() method has been called and
		// throw an IOException accordingly.
		InputStream is = getStreamIfOpen();

		// Check read limit
		if (bytesRead >= readLimitLocation)
			throw new ReadLimitReachedException("Read limit reached");
		
		// Check if we've restored any bytes
		if (avail > 0) {
			bufferedByte = buffer[pos] & 0xFF;
			bitsRemaining = 0;
			avail--;
		} else {
			bufferedByte = is.read();
			bitsRemaining = 0;
			
			if (bufferedByte != -1) {
				buffer[pos] = (byte)bufferedByte;
				
				if (count < buffer.length)
					count++;
			} else {
				// We've hit the end of the file
				endOfStream = true;
				return -1;
			}
		}

		bytesRead++;
		incrementBufferPos(1);

		return bufferedByte;
	}

	@Override
	public int read(byte[] buf, int off, int len) throws IOException, ReadLimitReachedException {
		// Make sure we don't read beyond the read
		// limit.
		if (bytesRead > readLimitLocation - len) {
			int readLimit = (int)(readLimitLocation - bytesRead);
			if (readLimit <= 0)
				throw new ReadLimitReachedException("Read limit reached");
			
			len = readLimit;
		}
		
		InputStream is = getStreamIfOpen();
		
		if (buf == null)
			throw new NullPointerException("buf is null");
		if (len < 0)
			throw new IllegalArgumentException("len < 0");
		if (buf.length < off + len)
			throw new ArrayIndexOutOfBoundsException(buf.length);
		if (off < 0)
			throw new ArrayIndexOutOfBoundsException(off);

		// Make sure we return, if no bytes
		// should be read.
		if (len == 0) 
			return 0;
		
		// Check for bytes in the buffer
		int br = 0;
		if (avail > 0) {
			br = readBuffer(buf, off, len);
			if (br >= len)
				return br;
			
			len -= br;
			off += br;
		}
		
		int isbr = is.read(buf, off, len);
		bitsRemaining = 0;
		
		if (isbr == -1) {
			// We've hit the end of the
			// stream, but we already read
			// bytes from the buffer.
			if (br != 0)
				return br;

			// We've hit the end of the stream
			bufferedByte = -1;
			endOfStream = true;
			
			return -1;
		}
		
		bufferedByte = buf[off + isbr - 1] & 0xFF;

		// There are a total of three cases where
		// we need to copy data from the buf array.
		if (isbr >= buffer.length) {
			// 1. We've read more bytes than we can fit
			//    into the buffer. We can overwrite the
			//    entire buffer.
			pos = 0;
			count = buffer.length;

			System.arraycopy(buf, off + isbr - count, buffer, 0, count);
		} else {
			if (pos + isbr <= buffer.length) {
				// 2. The new data fits at the end of the
				//    buffer without splitting it, and can
				//    be copied using a single arraycopy.
				System.arraycopy(buf, off, buffer, pos, isbr);
				incrementBufferPos(isbr);
			} else {
				// 3. The new data has to be split into two
				//    sections because it will wrap around
				//    to the beginning of the buffer.
				int bytesAtEnd = buffer.length - pos;
				System.arraycopy(buf, off, buffer, pos, bytesAtEnd);
				pos = isbr - bytesAtEnd;
				System.arraycopy(buf, off + bytesAtEnd, buffer, 0, pos);
			}
			
			count = MathUtils.min(buffer.length, count + isbr);
		}
		
		bytesRead += isbr;

		return br + isbr;
	}
	
	/**
	 * TODO: Documentation
	 * 
	 * @param buf
	 * @param off
	 * @param len
	 * @return
	 */
	private int readBuffer(byte[] buf, int off, int len) {
		if (len > avail)
			len = avail;
		
		// If no bytes were requested or if we
		// have no bytes available just return 0;
		if (len <= 0)
			return 0;
		
		if (pos + len <= buffer.length) {
			// The entire buffer can be copied
			// using a single arraycopy call.
			System.arraycopy(buffer, pos, buf, off, len);

			incrementBufferPos(len);
		} else {
			int bytesAtEnd = buffer.length - pos;
			System.arraycopy(buffer, pos, buf, off, bytesAtEnd);
			
			pos = len - bytesAtEnd;
			System.arraycopy(buffer, 0, buf, off, pos);
		}
		
		// Store the last read byte in the
		// buffered byte.
		bufferedByte = buf[off + len - 1] & 0xFF;
		bitsRemaining = 0;

		avail -= len;
		bytesRead += len;
		
		return len;
	}
	
	/**
	 * TODO: Documentation.
	 * 
	 * @param bitsToRead
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public int readBits(int bitsToRead) throws IOException, ReadLimitReachedException {
		if (bitsToRead <= 0)
			return 0;
		
		if (bitsRemaining <= 0) {
			if (read() == -1)
				return -1;
			bitsRemaining = BYTE_SIZE;
		}
		
		if (bitsRemaining >= bitsToRead) {
			bitsRemaining -= bitsToRead;
			return (bufferedByte >>> bitsRemaining) & MASK_TABLE[bitsToRead];
		}

		bitsToRead -= bitsRemaining;
		int res = (bufferedByte & MASK_TABLE[bitsRemaining]) << bitsToRead;

		while (true) {
			if (read() == -1) {
				// bitsRemaining is already zero at this
				// point, from the read() call above.
				// bitsRemaining = 0;
				return -1;
			}
			
			if (BYTE_SIZE >= bitsToRead) {
				bitsRemaining = BYTE_SIZE - bitsToRead;
				return res | ((bufferedByte >>> bitsRemaining) & MASK_TABLE[bitsToRead]);
			}
			
			bitsToRead -= BYTE_SIZE;
			res |= bufferedByte << bitsToRead;
		}
	}
	
	/**
	 * TODO: Documentation
	 * 
	 * @param n
	 * @throws IOException
	 */
	public void prereadBytes(int n) throws IOException {
		// Make sure we dont read more
		// bytes than we can actually
		// fit into the buffer.
		if (n > buffer.length)
			n = buffer.length;

		restoreBytes((int)skip(n));
	}
	
	/**
	 * Restores the amount of bytes provided by the parameter. If there are not
	 * enough bytes in the buffer to restore that amount, the buffer will try to
	 * restore all the bytes currently available.
	 * 
	 * @param bytesToRestore - The amount of bytes to restore
	 * 
	 * @return The actual amount of bytes restored.
	 */
	public int restoreBytes(int bytesToRestore) {
		if (bytesToRestore <= 0)
			return 0;
		if (bytesToRestore > count - avail)
			bytesToRestore = count - avail;
		
		pos -= bytesToRestore;
		if (pos < 0)
			pos += buffer.length;
		
		avail += bytesToRestore;
		bytesRead -= bytesToRestore;
		
		endOfStream = false;
		
		return bytesToRestore;
	}

	@Override
	public long skip(long n) throws IOException {
		if (avail >= n) {
			// avail is gte to n, so it must be
			// able to fit into an integer.
			incrementBufferPos((int)n);

			bytesRead += n;
			avail -= n;
			return n;
		}
		
		int skipped = 0;
		if (avail > 0) {
			incrementBufferPos(avail);
			bytesRead += avail;

			skipped = avail;
			avail = 0;
		}
		
		long remaining = n - skipped;
		if (bytesRead > readLimitLocation - remaining)
			remaining = readLimitLocation - bytesRead;
		
		return skipped + super.skip(remaining);
	}

	@Override
	public int available() throws IOException {
		int isa = getStreamIfOpen().available();
		if (isa > Integer.MAX_VALUE - avail)
			return Integer.MAX_VALUE;
		return isa + avail;
	}
	
	/**
	 * TODO: Documentation
	 * 
	 * @param expectedPattern
	 * @param numBits
	 * @param bufferOnly
	 * 
	 * @throws IOException
	 * @throws ReadLimitReachedException
	 */
	public boolean findBitPattern(int expectedPattern, int numBits) throws IOException, ReadLimitReachedException {
		return findBitPattern(expectedPattern, numBits, Long.MAX_VALUE);
	}

	/**
	 * TODO: Documentation
	 * 
	 * @param expectedPattern
	 * @param numBits
	 * @param bufferOnly
	 * @param searchLimit
	 * 
	 * @throws IOException
	 * @throws ReadLimitReachedException
	 */
	public boolean findBitPattern(int expectedPattern, int numBits, long searchLimit) throws IOException, ReadLimitReachedException {
		if (numBits < 0)
			throw new IllegalArgumentException("numBits < 0");
		if (searchLimit < 0)
			throw new IllegalArgumentException("searchLimit < 0");
			
		if (numBits > INTEGER_SIZE)
			numBits = INTEGER_SIZE;
		
		// Make sure we're byte-aligned.
		invalidateBufferedBits();
		
		searchLimit -= (numBits + BYTE_SIZE - 1) / BYTE_SIZE;
		if (searchLimit < 0)
			return false;
		
		int pattern = readBits(numBits);
		
		// Make sure the expected pattern is
		// correctly masked.
		expectedPattern &= MASK_TABLE[numBits];
		
		while (true) {
			if (isEndOfStream())
				return false;
				
			if (pattern == expectedPattern)
				break;
			
			searchLimit--;
			if (searchLimit < 0)
				return false;
			
			pattern <<= BYTE_SIZE;
			pattern |= readBits(BYTE_SIZE);
			pattern &= MASK_TABLE[numBits];
		}
		
		return true;
	}
	
	/**
	 * TODO: documentation
	 * 
	 * @param location
	 * 
	 * @return The number of bytes that ....
	 */
	public long seekByteLocation(long location) throws IOException, ReadLimitReachedException {
		if (location > bytesRead)
			return skip(location - bytesRead);
		
		return restoreBytes((int)(bytesRead - location));
	}

	/**
	 * TODO: documentation
	 * 
	 * @param readLimit
	 */
	public void setReadLimit(long readLimit) {
		if (readLimit < 0)
			throw new IllegalArgumentException("readLimit < 0");
		
		if (bytesRead >= Long.MAX_VALUE - readLimit) {
			readLimitLocation = Long.MAX_VALUE;
		} else {
			readLimitLocation = bytesRead + readLimit;
		}
	}
	
	/**
	 * Invalidates the current read limit. Doing this will no longer throw an
	 * exception when is is attempted to read beyond the limit. If there was no
	 * read limit set prior to calling this function, no action will occur.
	 */
	public void invalidateReadLimit() {
		readLimitLocation = Long.MAX_VALUE;
	}
	
	/**
	 * @return The current location at which the read limit is located, or
	 *         {@code Long.MAX_VALUE} if there is no read limit.
	 */
	public long getReadLimitLocation() {
		return readLimitLocation;
	}
	
	/**
	 * TODO: documentation
	 * 
	 * @return
	 */
	public boolean hasReadLimit() {
		return readLimitLocation != Long.MAX_VALUE;
	}
	
	/**
	 * Invalidates the currently buffered bits. This will force readBits(int) to
	 * read a new byte when invoked.
	 * 
	 * @return The previously read byte. This byte is used for locating the end
	 *         of the stream and for reading bits.
	 * 
	 * @see #readBits(int)
	 */
	public int invalidateBufferedBits() {
		bitsRemaining = 0;
		return bufferedByte;
	}
	
	/**
	 * @return The number of bits remaining in the currently bufferedByte. If
	 *         this value is inadequate from zero, we're not on a byte-border.
	 */
	public int getBitsRemaining() {
		return bitsRemaining;
	}
	
	/**
	 * @return True, if we've reached the end of the InputStream. If this is the
	 *         case, it is possible to restore to the beginning of the buffer.
	 * @see #restoreBytes(int)
	 */
	public boolean isEndOfStream() {
		return endOfStream;
	}
	
	/**
	 * @return The number of bytes read from this bitStream. This can be used to
	 *         determine how many bytes you need to restore to get back to a
	 *         certain point.
	 *         <br><br>
	 *         <i>NOTE: restoreBytes will decrement this value with the amount
	 *         of restored bytes.</i>
	 */
	public long getBytesRead() {
		return bytesRead;
	}
	
	/**
	 * @return The number of bytes that can fit into the buffer without having
	 *         to overwrite any of the stored bytes.
	 */
	public int getCapacity() {
		return buffer.length;
	}
	
	/**
	 * @return The number of bytes currently stored in the buffer
	 */
	public int getBufferSize() {
		return count;
	}
	
	/**
	 * @return The number of currently restored bytes.
	 */
	public int getNumberOfRestoredBytes() {
		return avail;
	}
	
	@Override
	public void close() throws IOException {
		is.close();
	
		is = null;
		buffer = null;
	}
}
