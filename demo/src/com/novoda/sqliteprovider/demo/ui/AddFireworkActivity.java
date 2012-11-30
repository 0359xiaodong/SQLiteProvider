package com.novoda.sqliteprovider.demo.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.novoda.sqliteprovider.demo.R;
import com.novoda.sqliteprovider.demo.domain.Firework;
import com.novoda.sqliteprovider.demo.domain.UseCaseInfo;
import com.novoda.sqliteprovider.demo.loader.FireworkSaver;
import com.novoda.sqliteprovider.demo.persistance.DatabaseConstants.RawSql;
import com.novoda.sqliteprovider.demo.provider.FireworkUriConstants;
import com.novoda.sqliteprovider.demo.ui.base.NovodaActivity;
import com.novoda.sqliteprovider.demo.ui.fragment.UriSqlFragment;
import com.novoda.sqliteprovider.demo.ui.input.AddFirework;
import com.novoda.sqliteprovider.demo.ui.util.FromXML;

public class AddFireworkActivity extends NovodaActivity implements LoaderCallbacks<Firework>, AddFirework {

	private EditText nameEditText;
	private EditText colorEditText;
	private EditText noiseEditText;
	private EditText typeEditText;
	private EditText priceEditText;
	private UriSqlFragment uriSqlFragment;
	private Firework firework;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_firework);
		
		nameEditText = (EditText) findViewById(R.id.add_firework_input_name);
		colorEditText = (EditText) findViewById(R.id.add_firework_input_color);
		noiseEditText = (EditText) findViewById(R.id.add_firework_input_noise);
		typeEditText = (EditText) findViewById(R.id.add_firework_input_type);
		priceEditText = (EditText) findViewById(R.id.add_firework_input_price);
		
		uriSqlFragment = (UriSqlFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_uri_sql);
		UseCaseInfo info = new UseCaseInfo(FireworkUriConstants.ADD_FIREWORK, RawSql.INSERT_FIREWORK);
		uriSqlFragment.setInfo(info);
	}
	
	@Override
	@FromXML
	public void onAddFireworkClick(View button){
		if(checkForInput(nameEditText) || checkForInput(colorEditText) ||
				checkForInput(noiseEditText) || checkForInput(typeEditText) || checkForInput(priceEditText)) {
			Toast.makeText(this, "Fill in the firework", Toast.LENGTH_SHORT).show();
			return;
		}

		String name = "";
		String color = "";
		String noise = "";
		String type = "";
		double price = 0;
		
		name = nameEditText.getText().toString();
		color = colorEditText.getText().toString();
		noise = noiseEditText.getText().toString();
		type = typeEditText.getText().toString();
		price = Double.parseDouble(priceEditText.getText().toString());
		
		firework = new Firework(name, color, type, noise, price);
		
		getSupportLoaderManager().initLoader(FireworkSaver.LOADER_ID, null, this);
	}

	private boolean checkForInput(EditText editText) {
		return TextUtils.isEmpty(editText.getText());
	}

	@Override
	public Loader<Firework> onCreateLoader(int id, Bundle args) {
		return new FireworkSaver(this, getApp().getFireworkWriter(), firework);
	}

	@Override
	public void onLoadFinished(Loader<Firework> loader, Firework data) {
		Toast.makeText(this, "Firework that goes "+ data.getNoise() +" added.", Toast.LENGTH_SHORT).show();
		
		nameEditText.setText("");
		colorEditText.setText("");
		noiseEditText.setText("");
		typeEditText.setText("");
		priceEditText.setText("");
	
		uriSqlFragment.setSql(createSQL(data));
	}

	private String createSQL(Firework data) {
		return TextUtils.replace(
				RawSql.INSERT_FIREWORK, 
				new String[]{"Na","Co","No", "Ft", "Pr","Sh"}, 
				new CharSequence[]{data.getName(), data.getColor(), data.getNoise(), data.getType(), String.valueOf(data.getPrice()), "1"})
				.toString();
	}

	@Override
	public void onLoaderReset(Loader<Firework> loader) {
	}
}