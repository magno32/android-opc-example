package com.summit.opc.structs;

import org.openscada.opc.lib.da.browser.Leaf;

import android.os.Parcel;
import android.os.Parcelable;

public class LeafDescription implements NodeDescription, java.io.Serializable, Parcelable {

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
	
	private LeafDescription(String name, String itemId){
		this.name = name;
		this.itemId = itemId;
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

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[]{getName(),getItemId()});
	}
	
	public static final Parcelable.Creator<LeafDescription> CREATOR = new Parcelable.Creator<LeafDescription>() {

		public LeafDescription createFromParcel(Parcel source) {
			String[] vals = new String[2];
			source.readStringArray(vals);
			LeafDescription retVal = new LeafDescription(vals[0],vals[1]);
			return retVal;
		}

		public LeafDescription[] newArray(int size) {
			return new LeafDescription[size];
		}
	};
}
