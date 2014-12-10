package com.example.bricks;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;

abstract public class Bloc {
	Context courant;
	abstract void run();
}

abstract class logique extends Bloc {
}

abstract class action extends Bloc {
}

class listeactions extends Bloc {
	ArrayList<Bloc> liste;
	
	listeactions(){
		liste = new ArrayList<Bloc>();
	}
	
	void ajoute(Bloc b) {
		liste.add(b);
	}
	Bloc trouve(int i) {
		return liste.get(i);
	}
	void supprime(int i) {
		liste.remove(i);
	}
	void supprimeDernier(){
		liste.remove(liste.size()-1);
	}
	void toutEffacer(){
		liste.clear();
	}
	void run(){
		for (int k = 0; k < liste.size();k++) {
			liste.get(k).run();
		}
	}
}

class condition extends Bloc {
	int entier1;
	int entier2;
	
	condition(int a, int b) {
		entier1 = a;
		entier2 = b;
	}
	
	boolean test(){
		return (entier1<entier2);
	};
	void run(){};
}

class si extends logique {
	condition condition;
	listeactions alors;
	listeactions sinon;
	
	si(condition c, listeactions a, listeactions s) {
		condition = c;
		alors = a;
		sinon = s;
	}
	
	void run(){
		if (condition.test()) {
			alors.run();
		} else {
			sinon.run();
			
		}
	}	
}

class son extends action {
	son(Context a){courant =a;}
	void run(){
		MediaPlayer mPlayer = null;
		mPlayer = MediaPlayer.create(courant, R.raw.coin);
	    mPlayer.start();
	}
	
}
