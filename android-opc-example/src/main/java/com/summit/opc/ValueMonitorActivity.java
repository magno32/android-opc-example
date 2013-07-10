package com.summit.opc;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.summit.opc.structs.LeafDescription;

public class ValueMonitorActivity extends Activity {

	private LeafDescription leafDescription;
	private TextView currentValueField;
	private Server server;

	private TagUpdate tagUpdate;
	private CheckBox deviceReadCheckBox;

	private SeekBar seekBar;
	private TextView pollValueLabel;

	private Button subscriptionButton;
	private Button refreshButton;

	private SubscriptionHandler subscriptionHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.value_monitor);
		server = HelloAndroidActivity.server;
		seekBar = (SeekBar) findViewById(R.id.pollIntervalSeek);
		pollValueLabel = (TextView) findViewById(R.id.pollIntervalValueLabel);
		updatePollText();
		subscriptionHandler = new SubscriptionHandler();
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updatePollText();
			}
		});

		deviceReadCheckBox = (CheckBox) findViewById(R.id.deviceReadCheckBox);

		Bundle b = getIntent().getExtras();
		leafDescription = (LeafDescription) b.get(HelloAndroidActivity.LEAF_INTENT_EXTRA);

		TextView tagNameField = (TextView) findViewById(R.id.tagNameField);
		tagNameField.setText(leafDescription.getName());

		currentValueField = (TextView) findViewById(R.id.currentValueVield);
		currentValueField.setText(HelloAndroidActivity.EMPTY_STRING);
		tagUpdate = new TagUpdate();

		tagUpdate.execute(leafDescription.getItemId());

		refreshButton = ((Button) findViewById(R.id.refreshBtn));
		refreshButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				tagUpdate.cancel(true);
				tagUpdate = new TagUpdate();
				tagUpdate.execute(leafDescription.getItemId());
			}
		});

		subscriptionButton = (Button) findViewById(R.id.subscribeBtn);
		subscriptionHandler.setSubscriptionActive(false);
	}

	private int getSeekValue(){
		//Seekbar does not have a minimum... lame.
		return seekBar.getProgress()+50;
	}
	
	private void updatePollText() {
		pollValueLabel.setText(getSeekValue() + " ms");
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

				return itemState.getValue().toString();
			} catch (Exception ex) {
				return ex.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			currentValueField.setText(result);
		}
	}

	private class SubscriptionHandler {
		private AccessBase access;
		private final String TAG = SubscriptionHandler.class.getName();

		public void subscribe(final int pollMs, final String tagId) {
			new AsyncTask<Object, Object, Object>() {

				@Override
				protected Object doInBackground(Object... params) {
					if (access != null) {
						try {
							access.unbind();
						} catch (Exception ex) {
							Log.w(TAG, ex.getMessage(), ex);
						} finally {
							access = null;
						}
					}
					try {
						access = new SyncAccess(server, getSeekValue());
						access.addItem(tagId, new DataCallback() {

							public void changed(final Item item, final ItemState itemState) {
								currentValueField.post(new Runnable() {

									public void run() {
										currentValueField.setText(itemState.getValue().toString());
									}
								});
							}
						});
						access.bind();
						return access;
					} catch (Exception ex) {
						return ex;
					}

				}

				protected void onPostExecute(Object result) {
					if (result instanceof Exception) {
						Log.w(TAG, ((Exception) result).getMessage(), (Exception) result);
					} else if (result instanceof AccessBase) {
						SubscriptionHandler.this.access = (AccessBase) result;
						setSubscriptionActive(true);
					}
				};
			}.execute();

		}

		private void setSubscriptionActive(boolean active) {
			if (active) {
				subscriptionButton.setText(R.string.unSubscribeLabel);
				subscriptionButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						subscriptionHandler.unsubscribe();
					}
				});
			} else {
				subscriptionButton.setText(R.string.subscribeLabel);
				subscriptionButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						subscriptionHandler.subscribe(getSeekValue(), leafDescription.getItemId());
					}
				});
			}
			refreshButton.setEnabled(!active);
			seekBar.setEnabled(!active);
			deviceReadCheckBox.setEnabled(!active);
		}

		public void unsubscribe() {
			if (access != null) {
				new AsyncTask<Object, Object, Exception>() {

					@Override
					protected Exception doInBackground(Object... params) {
						try {
							access.unbind();
						} catch (Exception ex) {
							return ex;
						} finally {
							access = null;
						}
						return null;
					}

					protected void onPostExecute(Exception result) {
						if (result != null) {
							Log.w(TAG, result.getMessage(), result);
						}
						setSubscriptionActive(false);
					};
				}.execute();

			}
		}

	}
}
