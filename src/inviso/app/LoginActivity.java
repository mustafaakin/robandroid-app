package inviso.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	EditText txtServer;
	EditText txtUser;
	EditText txtPass;

	Button btnLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		txtServer = (EditText) findViewById(R.id.txtServer);
		txtUser = (EditText) findViewById(R.id.txtUser);
		txtPass = (EditText) findViewById(R.id.txtPass);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoginTask().execute(new String[] { txtServer.getText().toString(), txtUser.getText().toString(), txtPass.getText().toString() });
			}
		});
	}

	private class LoginTask extends AsyncTask<String, Void, Boolean> {
		String[] params = null;

		@Override
		protected Boolean doInBackground(String... params) {
			this.params = params;
			boolean loginResult = NetworkHandler.login(params[0], params[1], params[2]);
			return loginResult;
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
