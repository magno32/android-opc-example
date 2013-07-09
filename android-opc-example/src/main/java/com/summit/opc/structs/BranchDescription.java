package com.summit.opc.structs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;

import android.os.Parcel;
import android.os.Parcelable;

public class BranchDescription implements NodeDescription,Serializable, Parcelable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String branchName;
	
	private BranchDescription parent;
	private List<BranchDescription> childrenBranches;
	private List<LeafDescription> childrenLeaves;
	
	public BranchDescription(Branch b) {
		this(b, null);
	}
	
	public BranchDescription(Branch branch, BranchDescription parent) {
		this.childrenBranches = new ArrayList<BranchDescription>(branch.getBranches().size());
		this.childrenLeaves = new ArrayList<LeafDescription>(branch.getLeaves().size());
		
		this.branchName = branch.getName();
		this.parent = parent;
		
		for(Branch b : branch.getBranches()){
			childrenBranches.add(new BranchDescription(b, this));
		}
		for(Leaf l : branch.getLeaves()){
			childrenLeaves.add(new LeafDescription(l, this));
		}
	}
	
	private BranchDescription(String branchName, List<BranchDescription> childBranches, List<LeafDescription> childLeaves){
		this.branchName = branchName;
		this.childrenBranches = childBranches;
		this.childrenLeaves = childLeaves;
	}
	
	public String getBranchName() {
		return branchName;
	}
	public List<LeafDescription> getChildrenLeaves() {
		return Collections.unmodifiableList(childrenLeaves);
	}
	public List<BranchDescription> getChildrenBranches() {
		return Collections.unmodifiableList(childrenBranches);
	}
	public BranchDescription getParent() {
		return parent;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(new Object[]{branchName,childrenBranches,childrenLeaves});
	}
	
	public static final Parcelable.Creator<BranchDescription> CREATOR = new Parcelable.Creator<BranchDescription>() {

		public BranchDescription createFromParcel(Parcel source) {
			Object[] in = source.readArray(BranchDescription.class.getClassLoader());
			
			@SuppressWarnings("unchecked")
			BranchDescription retVal = new BranchDescription((String)in[0],(List<BranchDescription>)in[1],(List<LeafDescription>)in[2]);
			
			return retVal;
		}

		public BranchDescription[] newArray(int size) {
			return new BranchDescription[size];
		}
	};

	public String getName() {
		return getBranchName();
	}
}
