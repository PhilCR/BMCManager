package com.solucionamos.bmcmanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bmcmanager.R;
import com.solucionamos.bmcmanager.model.Server;

public class AddServerActivity extends Activity implements
        AsyncResponse<BMCResponse> {

    // It has a reference to the button and the EditText, in the future, there
    // should be a reference to each text field in the fragment.
    private EditText serverName;
    private EditText hostname;
    private EditText username;
    private EditText password;
    private Spinner spinner;
    private String spinnerText;

    private DBHelper db;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_server_frag);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))
            getActionBar().setHomeAsUpIndicator(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        spinnerText = "";

        // serverProtocol = (EditText)
        // this.findViewById(R.id.serverProtocolTxt);
        serverName = (EditText) this.findViewById(R.id.add_server_name_txt);
        hostname = (EditText) this.findViewById(R.id.add_server_address_txt);
        username = (EditText) this.findViewById(R.id.add_server_username_txt);
        password = (EditText) this.findViewById(R.id.add_server_password_txt);
        spinner = (Spinner) this.findViewById(R.id.add_server_protocol_spinner);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                spinnerText = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        this.getActionBar().setTitle(R.string.action_titleAddServer);
    }

    private void confirmServerData() {
        try {

            String name = serverName.getText().toString().trim();
            String address = hostname.getText().toString().trim();
            String username = this.username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            System.out.println("SPINNER: " + spinnerText);
            Server server = new Server(spinnerText, "IVB", serverName.getText()
                    .toString(), hostname.getText().toString(), this.username
                    .getText().toString(), password.getText().toString());

            if (!(spinnerText.length() == 0 || name.length() == 0
                    || address.length() == 0 || username.length() == 0 || pass
                    .length() == 0)) {
                db = new DBHelper(this);
                Server el = db.getServer(name);
                if (el != null) {
                    Context context = this.getApplicationContext();
                    CharSequence text = getString(R.string.server_exists);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    TestConnectionTask asyncTask = new TestConnectionTask();
                    asyncTask.delegate = this;
                    asyncTask.execute(server);
                }
            } else {
                Context context = this.getApplicationContext();
                CharSequence text = getString(R.string.required_string) + "\n";
                if (name.length() == 0)
                    text = text
                            + " -"
                            + getString(R.string.add_server_name).replace(":",
                            "") + "\n";
                if (address.length() == 0)
                    text = text
                            + " -"
                            + getString(R.string.add_server_address).replace(
                            ":", "") + "\n";
                if (username.length() == 0)
                    text = text
                            + " -"
                            + getString(R.string.add_server_username).replace(
                            ":", "") + "\n";
                if (pass.length() == 0)
                    text = text
                            + " -"
                            + getString(R.string.add_server_password).replace(
                            ":", "") + "\n";
                if (spinnerText.length() == 0) {
                    text = text
                            + " -"
                            + getString(R.string.add_server_protocol).replace(
                            ":", "") + "\n";
                }
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        } catch (Exception e) {
            Context context = this.getApplicationContext();
            CharSequence text = getString(R.string.connection_no_success);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            onBackPressed();
            return true;
        } else if (id == R.id.action_bar_confirm_btn) {
            confirmServerData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addServer(Server el) {
        db = new DBHelper(this);
        db.insertServer(el);

        try {
            Intent k = new Intent(AddServerActivity.this, ServerListActivity.class);
            k.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);
        } catch (Exception e) {

            Context context = this.getApplicationContext();
            CharSequence text = getString(R.string.connection_no_success);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public void processFinish(BMCResponse response, Exception ex) {
        if (ex != null) {

            System.out.println(ex.toString());
            //ex.printStackTrace();
            Context context = this.getApplicationContext();
            CharSequence text = getString(R.string.connection_no_success);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            this.addServer(response.getServer());
        }
    }
}
