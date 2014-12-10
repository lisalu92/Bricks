package com.example.bricks;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.*;

import com.example.bricks.Instruction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.util.JsonReader;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

class Instruction {
	int id;
	String name;
	String code;
	String category;
	String language;
	String url;
}

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.bricks.MESSAGE";
	int nb_options=5;
	TextView[] options = new TextView[nb_options];
	TextView[] selected = new TextView[nb_options];
	String[] json = new String[nb_options];
	JSONObject[] jobject = new JSONObject[nb_options];
	String [] code = new String[nb_options];
	String code_tosend="";
	int cursor_select;
	List<Instruction> blocks;
	boolean connection_established=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Thread connection = new Thread(){
			public void run(){
				URL url = null;
				blocks = new ArrayList();
				
				System.out.println("On est entré dans le Thread");
				try {
					url = new URL("http://api-codebuilder.herokuapp.com/blocks.json");
					System.out.println("On est entré dans le URL");
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				InputStream is = null;
				try {
					System.out.println("je tente d'ouvrir le stream");
					is = url.openStream();
					System.out.println("On a ouvert le stream");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("erreur stream");
				}
				JsonReader rdr = null;
				try {
					rdr = new JsonReader(new InputStreamReader(is, "UTF-8"));
					System.out.println("On a créé le reader");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Instruction i;
				try {
					rdr.beginArray();
					while (rdr.hasNext()) {
						i = new Instruction();
						rdr.beginObject();
						while (rdr.hasNext()) {
							String name = rdr.nextName();
							if (name.equals("id")) {
								int v = rdr.nextInt();
								i.id = v;
							} else if (name.equals("name")) {
								String s = rdr.nextString();
								i.name = s;
							} else if (name.equals("category")) {
								String s = rdr.nextString();
								i.category = s;
							} else if (name.equals("language")) {
								String s = rdr.nextString();
								i.language = s;
							} else if (name.equals("url")) {
								String s = rdr.nextString();
								i.url = s;
							}else {
								rdr.skipValue();
							}
						}
						rdr.endObject();
						blocks.add(i);
					}
					rdr.endArray();
					System.out.println("Création du array");
					rdr.close();
					System.out.println(blocks.get(1).name);
					connection_established=true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		connection.start();

		Thread afficher_choix = new Thread(){
			public void run(){
				while(true){
					if(connection_established){
						connection_established=false;
						System.out.println("test"+blocks.get(2).id);
					}
				}
			}
		};
		afficher_choix.start();
		
		//Créer les json en tant que String
		json[0] = "{"+
				"\"id\": \"22\", "+
				"\"name\": \"si\", "+
				"\"code\": \"if (\", "+
				"\"category\": \"logique\"" +

				"}";
		json[1] = "{"+
				"\"id\": \"23\", "+
				"\"name\": \"alors\", "+
				"\"code\": \"){\", "+
				"\"category\": \"logique\"" +
				"}";

		json[2] = "{"+
				"\"id\": \"26\", "+
				"\"name\": \"end\", "+
				"\"code\": \"}\", "+
				"\"category\": \"logique\"" +
				"}";

		json[3] = "{"+
				"\"id\": \"24\", "+
				"\"name\": \"forte luminosité\", "+
				"\"code\": \"luminosité vaut tant\", "+
				"\"category\": \"action\"" +
				"}";
		json[4] = "{"+
				"\"id\": \"25\", "+
				"\"name\": \"fais cela\", "+
				"\"code\": \"fais cela\", "+
				"\"category\": \"action\"" +
				"}";



		System.out.println("fin json[]");

		//Créer les JSONObject pour chaque json
		for(int i=0; i<nb_options; i++){
			try {
				jobject[i] = (JSONObject) new JSONTokener(json[i]).nextValue();
				System.out.println("fin jobject[]");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//On nomme les TExtView correspondant aux choix
		options[0] = (TextView) findViewById(R.id.textoption1);
		options[1] = (TextView) findViewById(R.id.textoption2);
		options[2] = (TextView) findViewById(R.id.textoption3);
		options[3] = (TextView) findViewById(R.id.textoption4);
		options[4] = (TextView) findViewById(R.id.textoption5);

		//Le thread permet de prendre le nom de chaque JSONObject et de les mettre dans une case
		Thread afficheTexte = new Thread(){
			public void run(){
				for(int i=0; i<nb_options; i++){
					String tmp="";
					try {
						tmp = jobject[i].getString("name");
						System.out.println(tmp);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					options[i].setText(tmp);
				}

			}
		};
		afficheTexte.start();

		//On nomme les TextView où vont s'afficher nos choix
		selected[0] = (TextView) findViewById(R.id.textselect1);
		selected[1] = (TextView) findViewById(R.id.textselect2);
		selected[2] = (TextView) findViewById(R.id.textselect3);
		selected[3] = (TextView) findViewById(R.id.textselect4);
		selected[4] = (TextView) findViewById(R.id.textselect5);

		//Pour chaque TextView que l'on peut choisir, on crée la méthode onClick()
		//Si on clique sur le TextView, alors le nom du TextView s'affiche, on incrémente le curseur de
		//remplissage des cases et on copie le code dans le tableau code
		cursor_select=0;
		for(int i=0; i<nb_options; i++){
			final int j=i;
			options[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					selected[cursor_select].setText(options[j].getText().toString());
					try {
						code[cursor_select]=jobject[j].getString("code");
						System.out.println(Arrays.toString(code));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cursor_select++;
				}
			});
		};

		//Pour le bouton annuler, on supprime tous les choix, on remet le curseur à zéro et on vide le
		//tableau coded
		Button bouton_annuler = (Button) findViewById(R.id.bouton_annuler);
		bouton_annuler.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cursor_select=0;
				code= new String[nb_options];
				for(int i=0; i<nb_options; i++){
					selected[i].setText(" ");
				}
			}
		});

		//obtenir la valeur de réglage de la luminosité du téléphone dans curBrightnessValue
		float curBrightnessValue= 0;
		try {
			curBrightnessValue=android.provider.Settings.System.getInt(
					getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(curBrightnessValue);

		/*Vibrator vib;
		vib= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(10000);*/


	}

	//méthode utilisée quand on appuie sur le bouton Tester: on copie le code et on l'envoie sur
	//une autre page TestPage
	public void sendMessage(View v){
		for(int i=0; i<code.length; i++){
			code_tosend+=code[i];
		}
		//System.out.println(code_tosend);
		Intent intent = new Intent(this, TestPage.class);
		intent.putExtra(EXTRA_MESSAGE, code_tosend);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
