package net.coeustec.util;

import java.util.ArrayList;


public class XmlNodeList {

	public ArrayList<XmlNode> nodes = null;

	public XmlNodeList() {
		nodes = new ArrayList<XmlNode>();
	}

	public void add(XmlNode value) {
		if (nodes != null)
			nodes.add(value);
	}

	public void clear() {
		if (nodes != null)
			nodes.clear();
	}

	public XmlNode get(int i) {
		if (nodes != null && i >= 0 && i < count()) {
			return (XmlNode) nodes.get(i);
		}
		return null;
	}

	public int count() {
		if (nodes != null)
			return nodes.size();
		return 0;
	}
}
