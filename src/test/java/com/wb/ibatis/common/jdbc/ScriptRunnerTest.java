package com.wb.ibatis.common.jdbc;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月18日
 */

public class ScriptRunnerTest {

	private ScriptRunner runner;
	
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/test";
	private String username = "root";
	private String password = "";

	private Reader wrongReader;
	private Reader correctReader;
	private Reader correctReader2;
	
	@Before
	public void instantiate() throws IOException {
		wrongReader = Resources.getResourceAsReader("sql/wrong.sql");
		correctReader = Resources.getResourceAsReader("sql/correct.sql");
		correctReader2 = Resources.getResourceAsReader("sql/correct2.sql");
	}
	
	/**
	 * 测试执行会出错的SQL脚本
	 */
	@Test
	public void testRunWrongScript() throws IOException, SQLException {
		runner = new ScriptRunner(driver, url, username, password, true, true);
		runner.runScript(wrongReader);
	}
	
	/**
	 * 执行正确的SQL脚本
	 */
	@Test
	public void testRunCorrectScript() throws IOException, SQLException {
		runner = new ScriptRunner(driver, url, username, password, false, false);
		runner.runScript(correctReader);
		runner.runScript(correctReader2);
	}
	
	/**
	 * 执行正确的SQL脚本（分行标志是单独一行）
	 */
	@Test
	public void testRunCorrectScript2() throws IOException, SQLException {
		runner = new ScriptRunner(driver, url, username, password, true, true);
		runner.setDelimiter(";", true);
		runner.runScript(correctReader2);
	}
	
}
