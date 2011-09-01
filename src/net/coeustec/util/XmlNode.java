package net.coeustec.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.coeustec.util.logger.Logger;

public class XmlNode {
	private DocumentBuilderFactory docBuilderFactory_ = null;
	private DocumentBuilder docBuilder_ = null;
	private Document document_ = null;
	public Element root_ = null;

	public XmlNode() {
	}

	public XmlNode(Element el) {
		root_ = el;
	}

	public String getTagName() {
		return root_.getTagName();
	}

	public String getText() {
		return getText(root_);
	}

	/**
	 * 获取节点内容
	 * 
	 * @param node
	 * @return
	 */
	public static String getText(Node node) {
		NodeList nodeList = node.getChildNodes();
		int nodeListLength = nodeList.getLength();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nodeListLength; i++) {
			Node childNode = nodeList.item(i);
			// 若不是Element节点类型则返回
			if (childNode.getNodeType() == Node.TEXT_NODE) {
				sb.append(childNode.getNodeValue());
			}
		}
		return sb.toString().trim();
	}

	/**
	 * 获取根节点CDATA
	 * 
	 * @return
	 */
	public String getCDATA() {
		String  res = getCDATA(root_);
		if (res == null || res.length() == 0){
			return getText();
		}
		return res;//getCDATA(root_);
	}

	/**
	 * 获取节点CDATA
	 * 
	 * @param node
	 * @return
	 */
	public String getCDATA(Node node) {
		NodeList nodeList = node.getChildNodes();
		int nodeListLength = nodeList.getLength();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nodeListLength; i++) {
			Node childNode = nodeList.item(i);
			// 若不是Element节点类型则返回
			if (childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
				sb.append(childNode.getNodeValue());
			}
		}
		return sb.toString();
	}

	/**
	 * 获取根节点名
	 * 
	 * @return
	 */
	public String getName() {
		return root_.getNodeName();
	}

	/**
	 * 加载输入流解析
	 * 
	 * @param input
	 *            xml输入流
	 * @return
	 */
	public boolean loadInputStream(InputStream input) {
		try {
			docBuilderFactory_ = DocumentBuilderFactory.newInstance();
			docBuilderFactory_.setIgnoringElementContentWhitespace(true);
			docBuilder_ = docBuilderFactory_.newDocumentBuilder();
			document_ = docBuilder_.parse(input);
			root_ = document_.getDocumentElement();
		} catch (SAXException e) {
			Logger.e("XmlNode:LoadInputSource 1:", e.toString());
			return false;
		} catch (ParserConfigurationException e) {
			Logger.e("XmlNode:LoadInputSource 2:", e.toString());
			return false;
		} catch (IOException e) {
			Logger.e("XmlNode:LoadInputSource 3:", e.toString());
			return false;
		} finally {
			if (document_ == null || root_ == null) {
				document_ = null;
				return false;
			}
			document_ = null;
			docBuilder_ = null;
			docBuilderFactory_ = null;
		}

		return true;
	}

	/**
	 * 获取节点列表
	 * 
	 * @param nodename
	 *            节点名字
	 * @return
	 */
	public NodeList getNodeList(String nodename) {
		NodeList nodeList = root_.getElementsByTagName(nodename);

		return nodeList;
	}

	/**
	 * 获取节点列表
	 * 
	 * @param node
	 * @param nodename
	 * @return
	 */
	public NodeList getNodeList(Node node, String nodename) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList nodeList = ((Element) node).getElementsByTagName(nodename);
			return nodeList;
		}

		return node.getChildNodes();
	}

	/**
	 * 获取节点属性
	 * 
	 * @param node
	 *            节点
	 * @param attr
	 *            属性
	 * @return
	 */
	public String getAttribute(Node node, String attr) {
		try {
			NamedNodeMap nodeMap = node.getAttributes();

			if (nodeMap == null) {
				return "";
			}

			Node n = nodeMap.getNamedItem(attr);

			if (n == null) {
				return "";
			}

			String str = n.getNodeValue();

			return str;
		} catch (Exception e) {
			Logger.e("XmlNode.getAttribute:" + e.getMessage());
			return "";
		}
	}

	/**
	 * 设置节点属性
	 * 
	 * @param node
	 *            xml节点
	 * @param name
	 *            节点属性
	 * @param value
	 *            节点属性
	 * @return 是否设置成功
	 */
	public boolean setAttribute(Node node, String name, String value) {
		try {
			NamedNodeMap nodeMap = node.getAttributes();

			if (nodeMap == null) {
				return false;
			}

			Node n = nodeMap.getNamedItem(name);

			if (n == null) {
				return false;
			}

			n.setNodeValue(value);
			return true;
		} catch (Exception e) {
			Logger.e("XmlNode.setAttribute1:" + e.getMessage());
			return false;
		}
	}

	/**
	 * 设置属性
	 * 
	 * @param name
	 *            属性
	 * @param value
	 *            属性值
	 * @return 是否设置成功
	 */
	public boolean setAttribute(String name, String value) {
		try {
			Element el = root_;

			if (el == null) {
				return false;
			}
			if (el.hasAttribute(name)) {
				el.setAttribute(name, value);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Logger.e("XmlNode.setAttribute2:" + e.getMessage());
			return false;
		}
	}

	/**
	 * 查找指定节点名对应的节点
	 * 
	 * @param nodename
	 *            节点名字
	 * @return
	 */
	public Node selectSingleNodeByName(String nodename) {
		NodeList nodeList = root_.getElementsByTagName(nodename);

		if (nodeList.getLength() < 1) {
			return null;
		}
		return nodeList.item(0);
	}

	/**
	 * 根据节点名取单个节点
	 * 
	 * @param node
	 * @param nodename
	 * @return
	 */
	public static Node selectSingleNodeByName(Node node, String nodename) {
		NodeList nodes = node.getChildNodes();
		if (nodes.getLength() <= 0) {
			return null;
		}

		for (int i = 0; i < nodes.getLength(); i++) {
			Node tnode = nodes.item(i);
			if (nodename.equals(tnode.getNodeName())) {
				return tnode;
			}
		}
		return null;
	}

	/**
	 * 根据节点取得单个节点文本
	 * 
	 * @param node
	 * @param nodename
	 * @return
	 */
	public static String SelectSingleNodeText(Node node, String nodename) {
		Node result = selectSingleNodeByName(node, nodename);
		if (result == null) {
			return "";
		}
		String text = getText(result);
		return null == text ? "" : text;
	}

	/**
	 * 查找节点名对应节点
	 * 
	 * @param nodename
	 * @return
	 */
	public XmlNode selectSingleNode(String nodename) {
		NodeList nodeList = root_.getElementsByTagName(nodename);

		if (nodeList.getLength() < 1) {
			return null;
		}

		XmlNode xNode = new XmlNode();
		xNode.root_ = (Element) nodeList.item(0);

		return xNode;
	}

	/**
	 * 查找节点内容，同selectSingleNodeText2(String)
	 * 
	 * @param nodename
	 *            节点名字
	 * @return 节点内容
	 */
	public String selectSingleNodeText(String nodename) {
		XmlNode node = selectSingleNode(nodename);
		if (null == node) {
			return "";
		}
		return node.getText();
	}

	/**
	 * 加载xml并解析
	 * 
	 * @param xml数据
	 * @return
	 */
	public boolean loadXml(String xml) {

		if ((xml == null) || xml.length() <= 0) {
			return false;
		}

		InputStream inputstream = new ByteArrayInputStream(xml.getBytes());

		return loadInputStream(inputstream);
	}

	/**
	 * 获取根节点列表
	 * 
	 * @return
	 */
	public NodeList getChildNodeList() {
		return root_.getChildNodes();
	}

	/**
	 * 获取对应节点的节点列表
	 * 
	 * @param node
	 * @return
	 */
	public NodeList getChildNodeList(Node node) {
		return node.getChildNodes();
	}

	/**
	 * 获取根节点属性
	 * 
	 * @param name
	 *            属性
	 * @return
	 */
	public String getAttribute(String name) {
		String attributeName = root_.getAttribute(name);
		if (attributeName == null)
			attributeName = "";
		return attributeName;
	}

	/**
	 * 查找节点内容，同selectSingleNodeText(String)
	 * 
	 * @param name
	 *            节点名字
	 * @return 节点内容
	 */
	public String selectSingleNodeText2(String name) {
		Node node = selectSingleNodeByName(name);
		if (node == null)
			return "";
		return getText(node);
	}

	/**
	 * 获取节点对应的属性和值
	 * 
	 * @param node
	 * @return key-value
	 */
	public Hashtable<String, String> getAllAttributes(Node node) {
		Hashtable<String, String> hs = new Hashtable<String, String>(0);
		try {
			NamedNodeMap nodeMap = node.getAttributes();

			if (nodeMap == null) {
				return hs;
			}
			int cn = nodeMap.getLength();
			for (int i = 0; i < cn; i++) {

				Node n = nodeMap.item(i);

				if (n == null) {
					return hs;
				}
				String key = n.getNodeName();
				String value = n.getNodeValue();
				hs.put(key, value);
			}
			;

		} catch (Exception e) {
			Logger.e("XmlNode.getAllAttributes:" + e.getMessage());
			return hs;
		}
		return hs;
	}

	/**
	 * 获得子节点列表
	 * 
	 * @return
	 */
	public XmlNodeList selectChildNodes() {
		Element root = root_;
		XmlNodeList list = new XmlNodeList();

		if (root != null) {
			NodeList nodeList = root.getChildNodes();
			int len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = nodeList.item(i);
				switch (node.getNodeType()) {
				case Node.ELEMENT_NODE:
					if (!node.getNodeName().equalsIgnoreCase("")) {
						try {
							list.add(new XmlNode((Element) node));
						} catch (Exception e) {
							Logger.e("XmlNode.selectChildNodes"
									+ e.getMessage());
						}
					}
					break;

				default:
					break;
				}
			}
		}
		return list;
	}

	/**
	 * 获得指定名字的节点列表
	 * 
	 * @param eleName
	 * @return
	 */
	public XmlNodeList selectChildNodes(String eleName) {
		Element root = root_;
		XmlNodeList list = new XmlNodeList();
		if (root != null) {
			NodeList nodeList = root.getChildNodes();
			int len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node child = nodeList.item(i);
				if (child == null)
					continue;
				if (child.getNodeName().equalsIgnoreCase(eleName)) {
					try {
						list.add(new XmlNode(((Element) child)));
					} catch (Exception e) {
						Logger.e("XmlNode.selectChildNodes:" + e.getMessage());
					}
				}
			}
		}
		return list;
	}
}
