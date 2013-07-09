package com.summit.opc;

import org.openscada.opc.lib.da.browser.Branch;

public class BrowseBranchResult {
	
	
	private Exception exception = null;
	private Branch branch = null;
	
	public BrowseBranchResult(Branch branch) {
		this.branch = branch;
	}
	
	public BrowseBranchResult(Exception ex) {
		this.exception = ex;
	}
	
	/**
	 * 
	 * @return the branch from the browse operation. Null if there is an exception
	 */
	public Branch getBranch() {
		return branch;
	}
	/**
	 * 
	 * @return the exception raised (if any) from the operation.
	 */
	public Exception getException() {
		return exception;
	}
	
	public boolean hadException(){
		return exception != null;
	}
}
