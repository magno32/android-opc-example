package com.summit.opc.structs;

import org.openscada.opc.lib.da.browser.Leaf;

public class LeafDescription implements NodeDescription, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BranchDescription parent;
	String itemId;
	String name;

	public LeafDescription(Leaf leaf, BranchDescription parent) {
		this.parent = parent;
		this.itemId = leaf.getItemId();
		this.name = leaf.getName();
	}

	public String getItemId() {
		return itemId;
	}

	
	public String getName() {
		return name;
	}

	public BranchDescription getParent() {
		return parent;
	}
}
