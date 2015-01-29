package com.frank.dynamicloaderdex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.frank.dynamicloaderdex.proxy.gson.Gson;
import com.frank.dynamicloaderdex.proxy.gson.GsonBuilder;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Gson gson = new GsonBuilder().create();
		DemoTest demoTest = gson.fromJson(bowlingJson("Frank", "GO GO GO "),
				DemoTest.class);
		Toast.makeText(MainActivity.this, demoTest.winCondition,
				Toast.LENGTH_LONG).show();
	}

	private String bowlingJson(String player1, String player2) {
		return "{'winCondition':'HIGH_SCORE'," + "'name':'Bowling',"
				+ "'round':4," + "'dateStarted':'1367702378785'" + "}";
	}

	class DemoTest {
		public String winCondition;
		public String name;
		public int round;
		public String dateStarted;
	}

}
