package com.summit.opc.structs;

import com.summit.opc.BrowserListActivity;
import com.summit.opc.HelloAndroidActivity;
import com.summit.opc.R;
import com.summit.opc.ValueMonitorActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NodeDescriptionAdapter extends ArrayAdapter<NodeDescription> {
	private BranchDescription parent;

	public NodeDescriptionAdapter(Context context, BranchDescription parent) {
		super(context, R.layout.branch_description);
		this.parent = parent;
		for (BranchDescription bd : parent.getChildrenBranches()) {
			super.add(bd);
		}
		for (LeafDescription ld : parent.getChildrenLeaves()) {
			super.add(ld);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final NodeDescription item = super.getItem(position);
		LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.branch_description, parent, false);
		TextView tv = (TextView) rowView.findViewById(R.id.branchName);
		tv.setText(item.getName());
		ImageView iv = (ImageView) rowView.findViewById(R.id.nodeDescriptionImage);
		//Add a click listener...
		if (item instanceof BranchDescription) {
			iv.setImageResource(R.drawable.folder);
			final BranchDescription bd = (BranchDescription) item;
			rowView.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					Bundle b = new Bundle();
					b.putParcelable(HelloAndroidActivity.BRANCH_INTENT_EXTRA, bd);

					Intent intent = new Intent(getContext(), BrowserListActivity.class);
					intent.putExtras(b);
					getContext().startActivity(intent);
				}
			});
		}else if(item instanceof LeafDescription){
			iv.setImageResource(R.drawable.tag);
			rowView.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					Bundle b = new Bundle();
					
					b.putParcelable(HelloAndroidActivity.LEAF_INTENT_EXTRA, (LeafDescription)item);
					Intent intent = new Intent(getContext(),ValueMonitorActivity.class);
					intent.putExtras(b);
					getContext().startActivity(intent);
				}
			});
		}

		return rowView;
	}

	public BranchDescription getParent() {
		return parent;
	}
}
