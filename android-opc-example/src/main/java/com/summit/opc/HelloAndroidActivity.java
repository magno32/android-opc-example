package com.summit.opc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import org.jinterop.dcom.core.JISession;
import org.openscada.opc.dcom.common.Categories;
import org.openscada.opc.dcom.list.ClassDetails;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.list.Category;
import org.openscada.opc.lib.list.ServerList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HelloAndroidActivity extends Activity {

	SharedPreferences prefs;

	public static final String SERVER_PREF = "server";
	public static final String USER_PREF = "user";
	public static final String PASSWORD_PREF = "password";
	public static final String CLSID_PREF = "clsid";

	public static final String EMPTY_STRING = "";

	private static final String TAG = HelloAndroidActivity.class.getName();

	public HelloAndroidActivity() {
		super();
		// Had to set this for my vm, not sure what it does...
		// http://sourceforge.net/p/j-interop/discussion/600730/thread/0a1c609c
		System.setProperty("jcifs.encoding", "Cp1252");
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = getPreferences(MODE_PRIVATE);

		final EditText serverField = (EditText) findViewById(R.id.serverField);
		final EditText usernameField = (EditText) findViewById(R.id.userField);
		final EditText passwordField = (EditText) findViewById(R.id.passwordField);
		final EditText clsidField = (EditText) findViewById(R.id.clsidField);

		serverField.setText(prefs.getString(SERVER_PREF, EMPTY_STRING));
		usernameField.setText(prefs.getString(USER_PREF, EMPTY_STRING));
		passwordField.setText(prefs.getString(PASSWORD_PREF, EMPTY_STRING));
		clsidField.setText(prefs.getString(CLSID_PREF, EMPTY_STRING));

		Button browseButton = (Button) findViewById(R.id.browseServerBtn);
		browseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				storeLastMainInfo();
				final String serverName = serverField.getText().toString()
						.trim();
				final String username = usernameField.getText().toString()
						.trim();
				final String password = passwordField.getText().toString()
						.trim();

				if (serverName.isEmpty() || username.isEmpty()
						|| password.isEmpty()) {
					Toast.makeText(HelloAndroidActivity.this,
							R.string.browseInfoMissing, Toast.LENGTH_LONG)
							.show();
				} else {

					AsyncTask<Object,Object,AlertDialog.Builder> asyncTask = new AsyncTask<Object,Object,AlertDialog.Builder>() {
						//Using a tree map to sort by name
						Map<String, String> progIdToClsId = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

						@Override
						protected AlertDialog.Builder doInBackground(Object... params) {
							try {
								Log.i(TAG, String.format(
										"Connecting with: %s,%s,%s",
										serverName, username, password));
								JISession session = JISession.createSession("",
										username, password);
								ServerList serverList = new ServerList(session,
										serverName);
								Collection<ClassDetails> servers = serverList
										.listServersWithDetails(
												new Category[] {
														new Category(
																Categories.OPCDAServer10),
														new Category(
																Categories.OPCDAServer20) },
												new Category[] {});
								AlertDialog.Builder builder = new AlertDialog.Builder(
										HelloAndroidActivity.this);
								builder.setTitle(R.string.selectServerLabel);

								for (ClassDetails cd : servers) {
									Log.v(TAG, String.format(
											"Found: %s: %s (%s)",
											cd.getProgId(),
											cd.getDescription(), cd.getClsId()));
									progIdToClsId.put(cd.getProgId(),
											cd.getClsId());
								}
								final String[] keys = progIdToClsId.keySet().toArray(new String[]{});
								builder.setItems(keys,
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												String clsId = progIdToClsId.get(keys[which]);
												clsidField.setText(clsId);
											}
										});
								return builder;

							} catch (Exception ex) {
								Log.e(TAG, ex.getMessage(), ex);
								return null;
							}
							
						}
						@Override
						protected void onPostExecute(AlertDialog.Builder result) {
							result.create().show();
						}
					};
					asyncTask.execute(new Object[] {});
				}
			}
		});

		Button serverConnectButton = (Button) findViewById(R.id.serverConnectBtn);
		serverConnectButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				storeLastMainInfo();
				storeLastClsidInfo();

				String serverName = serverField.getText().toString().trim();
				String username = usernameField.getText().toString().trim();
				String password = passwordField.getText().toString().trim();
				String clsid = clsidField.getText().toString().trim();

				if (serverName.isEmpty() || username.isEmpty()
						|| password.isEmpty() || clsid.isEmpty()) {
					Toast.makeText(HelloAndroidActivity.this,
							R.string.connectionInfoMissing, Toast.LENGTH_LONG)
							.show();
				} else {
					final ConnectionInformation ci = new ConnectionInformation();
					ci.setHost(serverName);
					ci.setUser(username);
					ci.setPassword(password);
					ci.setClsid(clsid);

					Server server = new Server(ci, Executors
							.newSingleThreadScheduledExecutor());
					try {
						server.connect();
					} catch (Exception ex) {
						Toast.makeText(HelloAndroidActivity.this,
								ex.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, ex.getMessage(), ex);
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void storeLastMainInfo() {
		final EditText serverField = (EditText) findViewById(R.id.serverField);
		final EditText usernameField = (EditText) findViewById(R.id.userField);
		final EditText passwordField = (EditText) findViewById(R.id.passwordField);

		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(SERVER_PREF, serverField.getText().toString()
				.trim());
		prefsEditor.putString(USER_PREF, usernameField.getText().toString()
				.trim());
		prefsEditor.putString(PASSWORD_PREF, passwordField.getText().toString()
				.trim());
		prefsEditor.commit();
	}

	private void storeLastClsidInfo() {
		final EditText clsidField = (EditText) findViewById(R.id.clsidField);
		String clsid = clsidField.getText().toString().trim();
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(CLSID_PREF, clsid);
		prefsEditor.commit();
	}
}
