package com.summit.opc;

import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;

import com.summit.opc.structs.LeafDescription;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ValueMonitorActivity extends Activity {

	private LeafDescription leafDescription;
	private TextView currentValueField;
	private Server server;

	private TagUpdate tagUpdate;
	private CheckBox deviceReadCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.value_monitor);
		server = HelloAndroidActivity.server;

		deviceReadCheckBox = (CheckBox) findViewById(R.id.deviceReadCheckBox);

		Bundle b = getIntent().getExtras();
		leafDescription = (LeafDescription) b.get(HelloAndroidActivity.LEAF_INTENT_EXTRA);

		TextView tagNameField = (TextView) findViewById(R.id.tagNameField);
		tagNameField.setText(leafDescription.getName());

		currentValueField = (TextView) findViewById(R.id.currentValueVield);
		currentValueField.setText(HelloAndroidActivity.EMPTY_STRING);
		tagUpdate = new TagUpdate();

		tagUpdate.execute(leafDescription.getItemId());

		((Button) findViewById(R.id.refreshBtn)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				tagUpdate.cancel(true);
				tagUpdate = new TagUpdate();
				tagUpdate.execute(leafDescription.getItemId());
			}
		});		
	}

	private class TagUpdate extends AsyncTask<String, Object, String> {

		@Override
		protected String doInBackground(String... params) {
			String itemId = params[0];
			try {
				Group group = server.addGroup();
				Item item = group.addItem(itemId);
				ItemState itemState = item.read(deviceReadCheckBox.isChecked());
				
				group.clear();
				
				return itemState.toString();
			} catch (Exception ex) {
				return ex.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			currentValueField.setText(result);
		}
	}
}
