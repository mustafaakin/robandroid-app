package inviso.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	public void btnLoginClicked(View v) {
		Log.d("BUTTON", "LOGIN CLICKED");
		EditText txtServer;
		EditText txtUser;
		EditText txtPass;
		
		txtServer = (EditText) findViewById(R.id.txtServer);
		txtUser = (EditText) findViewById(R.id.txtUser);
		txtPass = (EditText) findViewById(R.id.txtPass);

		LoginTask t = new LoginTask();
		t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ,new String[] { txtServer.getText().toString(), txtUser.getText().toString(), txtPass.getText().toString() });
	}

	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		String[] params = null;
		
		public LoginTask(){
			Log.d("WTF", "WTF");
		}
		
		@Override
		protected void onPreExecute() {
			Log.d("Hi", "What up");
			
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			this.params = params;
			Log.d("BUTTON", "WTF");
			// boolean loginResult = NetworkHandler.login(params[0], params[1],
			// params[2]);
			// return loginResult;
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent i = new Intent(getApplicationContext(), PanelActivity.class);
				i.putExtra("server", params[0]);
				i.putExtra("user", params[1]);
				i.putExtra("pass", params[2]);
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(), "YOU SHALL NOT PASS!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
