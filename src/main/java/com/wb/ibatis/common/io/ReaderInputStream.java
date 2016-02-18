package com.wb.ibatis.common.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @author www
 * @date 2016年2月18日
 * 
 * 从Reader中获得InputStream
 * 
 */

public class ReaderInputStream extends InputStream {

	protected Reader reader;
	protected ByteArrayOutputStream byteArrayOutputStream;
	protected Writer writer;
	protected char[] chars;
	protected byte[] buffer;
	protected int index, length; // index:buffer里的指针， length:buffer的总长
	
	public ReaderInputStream(Reader reader) {
		this.reader = reader;
		byteArrayOutputStream = new ByteArrayOutputStream();
		writer = new OutputStreamWriter(byteArrayOutputStream);
		chars = new char[1024];
	}
	
	public ReaderInputStream(Reader reader, String encoding) throws UnsupportedEncodingException {
		this.reader = reader;
		byteArrayOutputStream = new ByteArrayOutputStream();
		writer = new OutputStreamWriter(byteArrayOutputStream, encoding);
		chars = new char[1024];
	}
	
	/**
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if (index >= length) {
			fillBuffer();
		}
		if (index >= length) {
			return -1;
		}
		return 0xff & buffer[index++];
	}
	
	/**
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] data, int off, int len) throws IOException {
		if (index >= length) {
			fillBuffer();
		}
		if (index >= length) {
			return -1;
		}
		int amount = Math.min(len, length - index);
		System.arraycopy(buffer, index, data, off, amount);
		index += amount;
		return amount;
	}

	protected void fillBuffer() throws IOException {
		if (length < 0) {
			return;
		}
		int numChars = reader.read(chars);
		if (numChars < 0) {
			length = -1;
		} else {
			byteArrayOutputStream.reset();
			writer.write(chars, 0, numChars);
			writer.flush();
			buffer = byteArrayOutputStream.toByteArray();
			length = buffer.length;
			index = 0;
		}
	}
	
	/**
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return (index < length) ? length - index :
			((length >= 0) && reader.ready()) ? 1 : 0;
	}
	
	/**
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		reader.close();
	}
}
