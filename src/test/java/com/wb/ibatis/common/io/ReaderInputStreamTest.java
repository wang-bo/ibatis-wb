package com.wb.ibatis.common.io;

import java.io.IOException;
import java.io.Reader;

import org.junit.Test;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月18日
 */

public class ReaderInputStreamTest {

	@Test
	public void testReaderInputStream() throws IOException {
		Reader reader = Resources.getResourceAsReader("sql/correct.sql");
		ReaderInputStream inputStream = new ReaderInputStream(reader, "UTF-8");
		while (inputStream.available() > 0) {
			System.out.print((char)inputStream.read());
		}
		inputStream.close();
	}
}
