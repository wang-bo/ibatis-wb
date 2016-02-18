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
	
	private Reader correctReader;
	private Reader wrongReader;
	
	@Before
	public void instantiate() throws IOException {
		correctReader = Resources.getResourceAsReader("sql/correct.sql");
		wrongReader = Resources.getResourceAsReader("sql/wrong.sql");
	}
	
	@Test
	public void testRunWrongScript() throws IOException, SQLException {
		runner = new ScriptRunner(driver, url, username, password, true, true);
		runner.runScript(wrongReader);
	}
	
	@Test
	public void testRunCorrectScript() throws IOException, SQLException {
		runner = new ScriptRunner(driver, url, username, password, false, false);
		runner.runScript(correctReader);
	}
	
}
