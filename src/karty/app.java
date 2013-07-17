package karty;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class app extends Applet implements Runnable, KeyListener{
	
	Thread thread;
	boolean pracuje;
	Graphics Gr;
	int szerokosc=4;
	int dlugosc=4;
	int pola[][] = new int[szerokosc][dlugosc];
	boolean zakryte[][] = new boolean[szerokosc][dlugosc];
	
	
	public void init(){
		pracuje = false;
		addKeyListener(this);
		resize(Constants.szerokoscOkna, Constants.wysokoscOkna);
		Gr = getGraphics();
		pola = generujPlansze(4, 4);
	}
	
	public void start(){
		thread = new Thread(this);
		thread.start();
		pracuje = true;
	}
	
	public void stop(){
		pracuje = false;
	}
	
	public void run(){
		while(pracuje){
			
			Gr.clearRect(0, 0, getWidth(), getHeight());
			rysujPlansze(pola, zakryte);
			
			try{
				Thread.sleep(1000/Constants.FPS);
			}
			catch(InterruptedException ex){}
		}
	}
	
	
	
	public void rysujPlansze(int[][] plansza, boolean[][] zakryte){
		int pozX=0;
		int pozY=0;
		int odstep = 15;
		int szer = 50;
		int dlug = 50;
		for(int x=0; x<plansza.length; x++){
			pozX = odstep*(x+1)+x*szer;
			for(int y=0; y<plansza[0].length; y++){
				pozY = odstep*(y+1)+y*dlug;
				Gr.drawRect(pozX, pozY, szer, dlug);
				Gr.setFont(new Font("Arial", 30, 25));
				Gr.drawString(Integer.toString(plansza[x][y]), pozX + szer/2, pozY + dlug/2);
			}
		}
	}
	
	
	public int[][] generujPlansze(int szerokosc, int dlugosc){
		Random rand;
		int los;
		int iloscKart = szerokosc*dlugosc/2;
		int iloscPol = szerokosc*dlugosc;
		int[] tab = new int[iloscPol];
		int[][] pola = new int[szerokosc][dlugosc];
		int x, y;
		for(int i=0; i<iloscPol; i++){
			y = (int)(i/szerokosc);
			x = i - y*szerokosc;
			rand = new Random();
			los = rand.nextInt(iloscKart);
			while(!sprawdzLos(los, tab,i)){
				rand = new Random();
				los = rand.nextInt(iloscKart);
			}
			tab[i] = los;
			pola[x][y] = los;
		}
		return pola;

	}
	
	boolean sprawdzLos(int los, int tab[], int n){
		if(n<2) return true;
		int ilosc=0;
		for(int i=0; i<n; i++){
			if(tab[i]==los){
				ilosc++;
				if(ilosc>=2) return false;
			}
		}
		return true;
	}
	
	public void keyPressed(KeyEvent evt){
		
	}
	
	public void keyTyped(KeyEvent evt){
		
	}
	
	public void keyReleased(KeyEvent evt){
		
	}

}
