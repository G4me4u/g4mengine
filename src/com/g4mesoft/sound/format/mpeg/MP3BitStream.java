package com.g4mesoft.sound.format.mpeg;

import java.io.IOException;
import java.io.InputStream;

public class MP3BitStream {

	/**
	 * The maximum amount of bytes to read, when skipping.
	 */
    private static final int MAX_SKIP_BUFFER_SIZE = 2048;
	
	/**
	 * The size of a single byte in bits.
	 */
	private static final int BYTE_SIZE = 8;
	
	/**
	 * A mask table used for reading bits. When masking
	 * using MASK_TABLE[i], only i number of the least
	 * significant bits will be present and the rest set
	 * to zero.
	 */
	private static final int[] MASK_TABLE = new int[] {
			0x0, 0x1, 0x3, 0x7, 0xF, 
			0x1F, 0x3F, 0x7F, 0xFF
	};
	
	/** 
	 * A buffer of this size should for sure be able 
	 * to hold at least a single frame of an mpeg file. 
	 * Most frames will be much smaller than this.<br>
	 * This buffer size is provided in bytes. (4 kB)
	 */
	private static final int MAX_BUFFER_SIZE = 1024 * 4;
	
	/**
	 * The InputStream provided in the constructor
	 */
	private InputStream in;

	/**
	 * The buffer storing the previously read bytes.
	 */
	private byte[] buffer;
	
	/**
	 * Current number of bytes stored in buffer.<br>
	 * Note that this can be greater than buffer.length - pos,
	 * and will wrap around back to the beginning of 
	 * the buffer.
	 */
	private int count;

	/**
	 * Describes the number of bytes in the buffer,
	 * which have been "unread". This value will in
	 * other words store how many bytes have to be
	 * read directly from the buffer, before we 
	 * continue reading from the InputStream.
	 */
	private int avail;
	
	/**
	 * Current position in buffer
	 */
	private int pos;
	
	/**
	 * The last read byte
	 */
	private int bufferedByte;
	
	/**
	 * The amount of bits left in the last read byte.
	 * Note: this is always zero unless readBits(int)
	 * is called.
	 * @see #readBits(int)
	 */
	private int bitsLeft;
	
	/**
	 * A value indicating how many bytes have been read
	 * from the InputStream.
	 * NOTE: restoreBytes will modify this value.
	 */
	private long bytesRead;
	
	MP3BitStream(InputStream in) {
		this.in = in;
		if (in == null) {
			throw new NullPointerException("in is null");
		}
		
		buffer = new byte[MAX_BUFFER_SIZE];
		count = avail = pos = 0;
		
		// Set bufferedByte to -2, so
		// we don't get false results
		// when checking if it's the
		// end of the stream.
		bufferedByte = -2;

		bytesRead = 0;
	}
	
	private InputStream getIfOpen() throws IOException {
		InputStream input = in;
		if (input == null)
			throw new IOException("stream closed");
		return input;
	}
	
	/**
	 * Reads a single byte from either the InputStream
	 * provided in the constructor or the internal buffer. 
	 * <br>
	 * The byte received from the InputStream will be
	 * stored in the internal buffer for later use.
	 * <br>
	 * NOTE: previously stored data in the buffer might 
	 * be overwritten during this process.
	 * 
	 * @return The byte which was read from the internal
	 * 		   buffer or from the InputStream. If no bytes 
	 * 		   were available, -1 will be returned.
	 * 
	 * @throws IOException - if an I/O error occurs.
	 */
	public int read() throws IOException {
		InputStream in = getIfOpen();

		if (avail > 0) {
			bufferedByte = (int)(buffer[pos]) & 0xFF;
			bitsLeft = 0;
		
			avail--;
			if (++pos >= buffer.length)
				pos = 0;
			bytesRead++;
			
			return bufferedByte;
		}
		
		bufferedByte = in.read();
		bitsLeft = 0;

		if (bufferedByte != -1) {
			buffer[pos] = (byte)bufferedByte;
			if (++pos >= buffer.length)
				pos = 0;
			if (count < buffer.length)
				count++;
			bytesRead++;
		}
		
		return bufferedByte;
	}
	
	/**
	 * Reads the specified amount ({@code len}) of bytes
	 * from the InputStream, or from the buffer if available.
	 * When reading from the InputStream, the data will
	 * be buffered in the buffer, and overwrite any previously
	 * stored data, if needed.
	 * <br><br>
	 * If there are no more bytes in the InputStream and
	 * the buffer is empty, -1 will be returned.
	 * 
	 * @param buf	-	The destination of the data.
	 * @param off	-	The offset in the destination array.
	 * @param len	-	The amount of bytes to read.
	 * 
	 * @return The amount of bytes actually read, or -1 if we hit
	 * 		   the end of the stream.
	 * 
	 * @throws IOException
	 * @throws IndexOutOfBoundsException if off + len > buf.length or if off < 0
	 */
	public int read(byte[] buf, int off, int len) throws IOException {
		InputStream in = getIfOpen();

		if (buf == null)
			throw new NullPointerException("buf is null");
		if (len < 0)
			throw new IllegalArgumentException("len < 0");
		if (buf.length < off + len)
			throw new IndexOutOfBoundsException(Integer.toString(buf.length));
		if (off < 0)
			throw new IndexOutOfBoundsException(Integer.toString(off));

		// Make sure we return, if
		// no bytes should be read.
		if (len == 0) 
			return 0;
		
		int br = 0;
		if (avail > 0) {
			br = readBuffer(buf, off, len);
			len -= br;
			off += br;
			
			if (len <= 0) {
				bufferedByte = (int)(buf[off + len - 1]) & 0xFF;
				bitsLeft = 0;
				// readBuffer already modified bytesRead
				// bytesRead += br;
				return br;
			}
		}
		
		// avail is zero at this point
		
		int rfis = in.read(buf, off, len);
		
		// No matter what, bufferedByte will be
		// overwritten at this point in time.
		// There will be no bits available at that
		// point.
		bitsLeft = 0;
		
		if (rfis > 0) {
			bufferedByte = (int)(buf[off + rfis - 1]) & 0xFF;

			if (rfis > buffer.length) {
				// Overwrite entire buffer with new data
				System.arraycopy(buf, off + rfis - buffer.length, buffer, 0, buffer.length);
				pos = 0;
				count = buffer.length;
			} else {
				if (rfis + pos > buffer.length) {
					int btc = buffer.length - pos;
					System.arraycopy(buf, off, buffer, pos, btc);
					pos = rfis - btc;
					System.arraycopy(buf, off + btc, buffer, 0, pos);
				} else {
					System.arraycopy(buf, off, buffer, pos, rfis);
					pos += rfis;

					if (pos >= buffer.length)
						pos = 0;
				}
				
				count += rfis;
				if (count > buffer.length)
					count = buffer.length;
			}
			
			bytesRead += rfis;
			
			return br + rfis;
		}
		
		if (br > 0) {
			// We didn't read anything from
			// the InputStream, but there were
			// available bytes in the buffer.
			// So bufferedByte is still the last
			// read byte at this point.
			bufferedByte = (int)(buf[off + len - 1]) & 0xFF;
			// readBuffer already modified bytesRead
			// bytesRead += br;
			return br;
		}

		// End of stream
		bufferedByte = -1;
		return -1;
	}
	
	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}
	
	public int readBuffer(byte[] dst, int off, int len) {
		if (len > avail)
			len = avail;
		if (len <= 0)
			return 0;

		// We know we have len available
		// bytes in the buffer, so we assume
		// we'll read that number of bytes.
		bytesRead += len;
		
		if (pos + len > buffer.length) {
			int btr = buffer.length - pos;
			System.arraycopy(buffer, pos, dst, off, btr);
			avail -= btr;
			pos = len - btr;
			System.arraycopy(buffer, 0, dst, off + btr, pos);
			avail -= pos;
		} else {
			System.arraycopy(buffer, pos, dst, off, len);
			avail -= len;
			pos += len;
			if (pos >= buffer.length)
				pos = 0;
		}
		
		return len;
	}
	
	public int readBits(int bitsToRead) throws IOException {
		if (bitsToRead <= 0) return 0;
		
		if (bitsLeft <= 0) {
			read();
			if (bufferedByte == -1)
				return -1;
			bitsLeft = BYTE_SIZE;
		}
		
		if (bitsLeft >= bitsToRead) {
			bitsLeft -= bitsToRead;
			return (bufferedByte >> bitsLeft) & MASK_TABLE[bitsToRead];
		}
		

		bitsToRead -= bitsLeft;
		int res = (bufferedByte & MASK_TABLE[bitsLeft]) << bitsToRead;

		while (true) {
			read();
			if (bufferedByte == -1) {
				// bitsLeft is already zero at 
				// this point, from the check above.
				// bitsLeft = 0;
				return -1;
			}
			
			if (BYTE_SIZE >= bitsToRead) {
				bitsLeft = BYTE_SIZE - bitsToRead;
				return res | ((bufferedByte >> bitsLeft) & MASK_TABLE[bitsToRead]);
			}
			
			bitsToRead -= BYTE_SIZE;
			res |= bufferedByte << bitsToRead;
		}
	}

	public void prereadBytes(int n) throws IOException {
		if (n > MAX_BUFFER_SIZE)
			n = MAX_BUFFER_SIZE;

		restoreBytes((int)skip(n));
	}
	
	public long skip(long n) throws IOException {
		long remaining = n;
		
		if (avail > 0) {
			if (avail >= remaining) {
				pos += remaining;
				if (pos >= buffer.length)
					pos -= buffer.length;
				avail -= remaining;
				bytesRead += remaining;
				return remaining;
			} else {
				pos += avail;
				if (pos >= buffer.length)
					pos -= buffer.length;
				bytesRead += avail;
				remaining -= avail;
				avail = 0;
			}
		}

		long size = Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
		byte[] skipBuffer = new byte[(int)size];
		while (remaining > 0) {
			int br = read(skipBuffer, 0, (int)Math.min(size, remaining));
			if (br < 0)
				break;
			remaining -= br;
		}
		
		return n - remaining;
	}
	
	public int available() throws IOException {
		int n = in.available();
		return n >= Integer.MAX_VALUE - avail ?
				Integer.MAX_VALUE :
				avail + n;
	}
	
	/**
	 * Invalidates the currently buffered bits.
	 * This will force readBits(int) to read
	 * a new byte when invoked.
	 * 
	 * @return The previously read byte. This
	 * 		   byte is used for locating the
	 * 		   end of the stream and for reading
	 * 		   bits.
	 * 
	 * @see #readBits(int)
	 */
	public int invalidateBufferedBits() {
		bitsLeft = 0;
		return bufferedByte;
	}
	
	/**
	 * @return The number of bits left in the
	 *         currently bufferedByte. If this
	 *         value is inadequate from zero,
	 *         we're not on a byte-border.
	 */
	public int getBitsLeft() {
		return bitsLeft;
	}
	
	/**
	 * @return True, if we've reached the end of the
	 * 		   InputStream. If this is the case, it
	 * 		   is possible to restore to the beginning
	 *		   of the buffer.
	 */
	public boolean isEndOfStream() {
		return bufferedByte == -1;
	}
	
	/**
	 * Restores the amount of bytes provided by
	 * the parameter. If there are not enough bytes
	 * in the buffer to restore that amount, the
	 * buffer will try to restore all the bytes currently
	 * available.
	 * 
	 * @param bytesToRestore	-	The amount of bytes 
	 * 								to restore
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
		
		return bytesToRestore;
	}
	
	/**
	 * @return The number of bytes read from this
	 *         bitStream. This can be used to determine
	 *         how many bytes you need to restore to
	 *         get back to a certain point.<br><br>
	 *         <i>NOTE: restoreBytes will decrement
	 *         this value with the amount of restored
	 *         bytes.</i>
	 */
	public long getBytesRead() {
		return bytesRead;
	}
	
	/**
	 * Closes this bitstream.<br><br>
	 * <b>NOTE:</b> This will not close the 
	 * InputStream provided in the constructor!
	 */
	public void close() {
		in = null;
		buffer = null;
	}
}
