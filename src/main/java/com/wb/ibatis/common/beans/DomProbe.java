package com.wb.ibatis.common.beans;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.wb.ibatis.common.resources.Resources;

/**
 * @author www
 * @date 2016年2月4日
 * 
 * 处理DOM的Probe实现
 * 
 */

public class DomProbe extends BaseProbe {
	
	/**
	 * 获取object对象中可读的属性名称数组
	 */
	@Override
	public String[] getReadablePropertyNames(Object object) {
		List<String> props = new ArrayList<>();
		Element e = resolveElement(object);
		NodeList nodes = e.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			props.add(nodes.item(i).getNodeName());
		}
		return props.toArray(new String[props.size()]);
	}

	/**
	 * 获取object对象中可写的属性名称数组
	 */
	@Override
	public String[] getWriteablePropertyNames(Object object) {
		List<String> props = new ArrayList<>();
		Element e = resolveElement(object);
		NodeList nodes = e.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			props.add(nodes.item(i).getNodeName());
		}
		return props.toArray(new String[props.size()]);
	}

	/**
	 * 获得object对象中指定属性的getter方法的返回值类型，name可能是嵌套的结构，格式：name1[i].name2[j]，表示element中第i个name1元素中的第j个name2元素
	 */
	@Override
	public Class<?> getPropertyTypeForGetter(Object object, String name) {
		Element element = findNestedNodeByName(resolveElement(object), name, false);
		try {
			return Resources.classForName(element.getAttribute("type"));
		} catch (ClassNotFoundException e) {
			return Object.class;
		}
	}

	/**
	 * 获得object对象中指定属性的setter方法的参数类型，name可能是嵌套的结构，格式：name1[i].name2[j]，表示element中第i个name1元素中的第j个name2元素
	 */
	@Override
	public Class<?> getPropertyTypeForSetter(Object object, String name) {
		Element element = findNestedNodeByName(resolveElement(object), name, false);
		try {
			return Resources.classForName(element.getAttribute("type"));
		} catch (ClassNotFoundException e) {
			return Object.class;
		}
	}

	/**
	 * 判断object对象中指定属性是否可写，name可能是嵌套的结构，格式：name1[i].name2[j]，表示element中第i个name1元素中的第j个name2元素
	 */
	@Override
	public boolean hasWritableProperty(Object object, String name) {
		return findNestedNodeByName(resolveElement(object), name, false) != null;
	}

	/**
	 * 判断object对象中指定属性是否可读，name可能是嵌套的结构，格式：name1[i].name2[j]，表示element中第i个name1元素中的第j个name2元素
	 */
	@Override
	public boolean hasReadableProperty(Object object, String name) {
		return findNestedNodeByName(resolveElement(object), name, false) != null;
	}
	
	/**
	 * 获取object对象中指定属性的值，name可能是嵌套的结构，格式：name1[i].name2[j]，表示element中第i个name1元素中的第j个name2元素
	 */
	@Override
	public Object getObject(Object object, String name) {
		Object value = null;
		Element element = findNestedNodeByName(resolveElement(object), name, false);
		if (element != null) {
			value = getElementValue(element);
		}
		return value;
	}

	/**
	 * 设置object对象中指定属性的值，name可能是嵌套的结构，格式：name1[i].name2[j]，表示element中第i个name1元素中的第j个name2元素
	 */
	@Override
	public void setObject(Object object, String name, Object value) {
		Element element = findNestedNodeByName(resolveElement(object), name, true);
		if (element != null) {
			setElementValue(element, value);
		}
	}

	/**
	 * 获取object对象中指定属性的值
	 */
	@Override
	protected Object getProperty(Object object, String property) {
		Object value = null;
		Element element = findNodeByName(resolveElement(object), property, 0, false);
		if (element != null) {
			value = getElementValue(element);
		}
		return value;
	}

	/**
	 * 设置object对象中指定属性的值
	 */
	@Override
	protected void setProperty(Object object, String property, Object value) {
		Element element = findNodeByName(resolveElement(object), property, 0,true);
		if (element != null) {
			setElementValue(element, value);
		}
	}
	
	/**
	 * 从object中解析出Element
	 */
	private Element resolveElement(Object object) {
		Element element = null;
		if (object instanceof Document) {
			// 获取Document的根节点
			element = (Element) ((Document) object).getLastChild();
		} else if (object instanceof Element) {
			element = (Element) object;
		} else {
			throw new ProbeException("An unknow object type was passed to DomProbe. Must be a Document.");
		}
		return element;
	}
	
	/**
	 * 获取element中指定的元素，可能是嵌套结构
	 * @param element
	 * @param name 格式：name1[i].name2[j]，表示element中第i个name1元素中的第j个name2元素
	 * @param create
	 * @return
	 */
	private Element findNestedNodeByName(Element element, String name, boolean create) {
		Element child = element;
		String[] childNameArray = name.split("\\.");
		for (String childName: childNameArray) {
			if (childName.indexOf('[') > -1) {
				String propName = childName.substring(0, childName.indexOf('['));
				int i = Integer.parseInt(childName.substring(childName.indexOf('[') + 1, childName.indexOf(']')));
				child = findNodeByName(child, propName, i, create);
			} else {
				child = findNodeByName(child, childName, 0, create);
			}
			if (child == null) {
				break;
			}
		}
		return child;
	}
	
	/**
	 * 获取element中第index个名称为name的元素，如果不存在，当create为true时，则创建该元素
	 * @param element
	 * @param name
	 * @param index
	 * @param create
	 * @return
	 */
	private Element findNodeByName(Element element, String name, int index, boolean create) {
		Element prop = null;
		// 获取属性名为name的所有Node的列表
		NodeList propNodes = element.getElementsByTagName(name);
		if (propNodes.getLength() > index) {
			prop = (Element) propNodes.item(index);
		} else {
			if (create) {
				// TODO 总觉得这行代码有问题，应该是：i < index + 1 - propNodes.getLength() 吧？
				for (int i = 0; i < index + 1; i++) {
					prop = element.getOwnerDocument().createElement(name);
					element.appendChild(prop);
				}
			}
		}
		return prop;
	}
	
	/**
	 * 获取element的值(所有text子元素的值)
	 */
	private Object getElementValue(Element element) {
		StringBuilder value = null;
		Element prop = element;
		if (prop != null) {
			// 只处理text子元素
			NodeList texts = prop.getChildNodes();
			if (texts.getLength() > 0) {
				value = new StringBuilder();
				for (int i = 0; i < texts.getLength(); i++) {
					Node text = texts.item(i);
					if (text instanceof CharacterData) {
						value.append(((CharacterData) text).getData());
					}
				}
			}
		}
		
		if (value == null) {
			return null;
		} else {
			return value.toString();
		}
	}
	
	/**
	 * 设置element的值
	 */
	private void setElementValue(Element element, Object value) {
		CharacterData data = null;
		Element prop = element;
		if (value instanceof Collection<?>) {
			for (Object valObject: ((Collection<?>) value)) {
				Document valdoc = (Document) valObject;
				NodeList nodeList = valdoc.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node newNode = element.getOwnerDocument().importNode(nodeList.item(i), true);
					element.appendChild(newNode);
				}
			}
		} else if (value instanceof Document) {
			Document valdoc = (Document) value;
			Node lastChild = valdoc.getLastChild(); // 获取根节点
			NodeList nodeList = lastChild.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node newNode = element.getOwnerDocument().importNode(nodeList.item(i), true);
				element.appendChild(newNode);
			}
		} else if (value instanceof Element) {
			Node newNode = element.getOwnerDocument().importNode((Element) value, true);
			element.appendChild(newNode);
		} else {
			// 处理text节点
			NodeList texts = prop.getChildNodes();
			if (texts.getLength() == 1) {
				Node child = texts.item(0); 
				if (child instanceof CharacterData) {
					// 使用这个现存的text元素
					data = (CharacterData) child;
				} else {
					// 删除非text元素，新增text元素
					prop.removeChild(child);
					Text text = prop.getOwnerDocument().createTextNode(String.valueOf(value));
					prop.appendChild(text);
					data = text;
				}
			} else if (texts.getLength() > 1) {
				// 删除所有元素，新增text元素
				for (int i = texts.getLength() - 1; i >= 0; i--) {
					prop.removeChild(texts.item(i));
				}
				Text text = prop.getOwnerDocument().createTextNode(String.valueOf(value));
				prop.appendChild(text);
				data = text;
			} else {
				// 无子元素，新增text元素
				Text text = prop.getOwnerDocument().createTextNode(String.valueOf(value));
				prop.appendChild(text);
				data = text;
			}
			data.setData(String.valueOf(value));
		}
	}
	
	/**
	 * 转换一个DOM的node为一个xml字符串
	 * @param node
	 * @param indent 缩进用字符串
	 * @return
	 */
	public static String nodeToString(Node node, String indent) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		
		switch (node.getNodeType()) {
			case Node.DOCUMENT_NODE:
				printWriter.println("<xml version=\"1.0\">\n");
				// 递归处理每个子节点
				NodeList nodeList = node.getChildNodes();
				if (nodeList != null) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						printWriter.print(nodeToString(nodeList.item(i), ""));
					}
				}
				break;
			case Node.ELEMENT_NODE:
				// 处理元素属性
				String name = node.getNodeName();
				printWriter.print(indent + "<" + name);
				NamedNodeMap attributes = node.getAttributes();
				for (int i = 0; i < attributes.getLength(); i++) {
					Node current = attributes.item(i);
					printWriter.print(" " + current.getNodeName() +
							"=\"" + current.getNodeValue() + "\"");
				}
				printWriter.print(">");
				// 递归处理每个子节点
				NodeList children = node.getChildNodes();
				if (children != null) {
					for (int i = 0; i < children.getLength(); i++) {
						printWriter.print(nodeToString(children.item(i), indent + indent));
					}
				}
				// 闭合元素
				printWriter.print("</" + name + ">");
				break;
			case Node.TEXT_NODE:
				printWriter.print(node.getNodeValue());
				break;
		}
		printWriter.flush();
		String result = stringWriter.getBuffer().toString();
		printWriter.close();
		return result;
	}
}
