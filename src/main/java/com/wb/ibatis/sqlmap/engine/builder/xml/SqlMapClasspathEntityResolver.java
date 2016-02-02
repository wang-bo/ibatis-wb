package com.wb.ibatis.sqlmap.engine.builder.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年1月31日
 * 
 * XML文档解析时的实体解析器，将公共DTD声明替换为本地DTD文件
 * 
 */

public class SqlMapClasspathEntityResolver implements EntityResolver {

	// 本地DTD文件的位置
	private static final String SQL_MAP_CONFIG_DTD = "com/wb/ibatis/sqlmap/engine/builder/xml/sql-map-config-2.dtd";
	private static final String SQL_MAP_DTD = "com/wb/ibatis/sqlmap/engine/builder/xml/sql-map-2.dtd";
	
	// 默认替换为本地DTD文件的公共DTD声明
	private static final Map<String, String> doctypeMap = new HashMap<>();
	
	static {
		doctypeMap.put("http://www.ibatis.com/dtd/sql-map-config-2.dtd".toUpperCase(), SQL_MAP_CONFIG_DTD);
	    doctypeMap.put("http://ibatis.apache.org/dtd/sql-map-config-2.dtd".toUpperCase(), SQL_MAP_CONFIG_DTD);
	    doctypeMap.put("-//iBATIS.com//DTD SQL Map Config 2.0//EN".toUpperCase(), SQL_MAP_CONFIG_DTD);
	    doctypeMap.put("-//ibatis.apache.org//DTD SQL Map Config 2.0//EN".toUpperCase(), SQL_MAP_CONFIG_DTD);

	    doctypeMap.put("http://www.ibatis.com/dtd/sql-map-2.dtd".toUpperCase(), SQL_MAP_DTD);
	    doctypeMap.put("http://ibatis.apache.org/dtd/sql-map-2.dtd".toUpperCase(), SQL_MAP_DTD);
	    doctypeMap.put("-//iBATIS.com//DTD SQL Map 2.0//EN".toUpperCase(), SQL_MAP_DTD);
	    doctypeMap.put("-//ibatis.apache.org//DTD SQL Map 2.0//EN".toUpperCase(), SQL_MAP_DTD);
	}
	
	/**
	 * 替换一个公共DTD声明为本地DTD文件
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		if (publicId != null) {
			publicId = publicId.toUpperCase();
		}
		if (systemId != null) {
			systemId = systemId.toUpperCase();
		}
		
		InputSource source = null;
		try {
			String path = doctypeMap.get(publicId);
			source = getInputSource(path);
			if (source == null) {
				path = doctypeMap.get(systemId);
				source = getInputSource(path);
			}
		} catch (Exception e) {
			throw new SAXException(e.toString());
		}
		return source;
	}
	
	/**
	 * 从path所指的文件中生成InputSource
	 */
	private InputSource getInputSource(String path) {
		InputSource source = null;
		if (path != null) {
			InputStream in = null;
			try {
				in = Resources.getResourceAsStream(path);
				source = new InputSource(in);
			} catch (IOException e) {
				// 不处理
			}
		}
		return source;
	}

}































