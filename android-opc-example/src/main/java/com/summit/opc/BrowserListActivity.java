package com.summit.opc;

import android.app.ListActivity;
import android.os.Bundle;

import com.summit.opc.structs.BranchDescription;
import com.summit.opc.structs.NodeDescriptionAdapter;

public class BrowserListActivity extends ListActivity{
	public BrowserListActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		BranchDescription bd = bundle.getParcelable(HelloAndroidActivity.BRANCH_INTENT_EXTRA);
		
		setListAdapter(new NodeDescriptionAdapter(BrowserListActivity.this, bd));
	}
}
