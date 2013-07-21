package karty;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.awt.event.MouseMotionListener;
import java.util.Date;

public class app extends Applet implements Runnable, MouseListener, MouseMotionListener{
	
	Thread thread;
	boolean pracuje;
	Graphics Gr;
	Graphics Gn;
	int szerokosc=4;
	int dlugosc=4;
	int pola[][] = new int[szerokosc][dlugosc];
	boolean odkryte[][] = new boolean[szerokosc][dlugosc];
	int nWcisniete = 0;
	int[][] wcisniete = new int[3][2];
	int ileOdkryc=0;
	
	
	int pozX=0;
	int pozY=0;
	int odstep = 15;
	int szer = 50;
	int dlug = 50;
	
	int mouseX, mouseY;
	boolean click=false;
	
	Date data;
	long startTime; 
	double currTime;
	
	String nick;
	baza baza;
	
	Panel panel = new Panel();
	TextField input = new TextField(20);
	TextArea textDane = new TextArea();
	Button button = new Button("Zatwierdü");
	
	public void init(){
		pracuje = false;
		baza = new baza();
		baza.stworzTabele();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		resize(Constants.szerokoscOkna, Constants.wysokoscOkna);
		Gr = getGraphics();
		Gn = getGraphics();
		
		setLayout(new BorderLayout());
		
		textDane.setText(baza.pobierzWynikiString(10));

		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				nick = input.getText();
				textDane.setText(nick);
			}
		});
		
		panel.add(input);
		panel.add(button);
		panel.add(textDane);
		add(panel, BorderLayout.SOUTH);
		
		pola = generujPlansze(szerokosc, dlugosc);
		data = new Date();
		startTime = data.getTime();
		ileOdkryc=0;
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
		rysujPlansze(pola, odkryte);
		while(pracuje){

			if(click){
				click=false;
				if(czyWpolu(mouseX, mouseY)){
					wcisniete[nWcisniete] = poleMysz(mouseX, mouseY);
					if(!czyOdkryte(wcisniete[nWcisniete][0], wcisniete[nWcisniete][1])){
						nWcisniete++;
						ileOdkryc++;
						Gr.clearRect(0, 0, getWidth(), getHeight());
						if(!sprawdzWcisniete()){
							rysujPlansze(pola, odkryte);
							resetujWcisniete();
							Gn.drawString(String.format("%.1f", currTime),500, 500);
							Gn.drawString(Integer.toString(ileOdkryc),550, 500);
							try{
								Thread.sleep(Constants.czasOdslony);
							}
							catch(InterruptedException exc){};

							Gr.clearRect(0, 0, getWidth(), getHeight());
							rysujPlansze(pola, odkryte);
						}
						else{ 
							rysujPlansze(pola, odkryte);
							if(sprawdzWygrana(odkryte)){
								Gn.clearRect(400, 400, 1000, 1000);
								Gr.drawString("wygrana", 450, 550);
								Gn.drawString(String.format("%.1f", currTime),500, 500);
								Gn.drawString(Integer.toString(ileOdkryc),550, 500);
								baza.zapiszRekord(input.getText(), ileOdkryc, currTime, szerokosc*dlugosc);
								pracuje=false;
							}
								
						}
					}
				}
			}
			if(ileOdkryc<1){
				currTime = 0;
			}
			else{
				if(ileOdkryc==1){
					data = new Date();
					startTime = data.getTime();
				}
				data = new Date();
				currTime = (double)(data.getTime()-startTime)/1000;
			}
			Gn.clearRect(400, 400, 1000, 1000);
			Gn.drawString(String.format("%.1f", currTime),500, 500);
			Gn.drawString(Integer.toString(ileOdkryc),550, 500);
			try{
				Thread.sleep(20);
			}
			catch(InterruptedException exc){};
		}
	}
	
	
	
	public void rysujPlansze(int[][] plansza, boolean[][] odkryte){
		Gr.setFont(new Font("Arial", 30, 25));
		
		for(int x=0; x<plansza.length; x++){
			pozX = odstep*(x+1)+x*szer;
			for(int y=0; y<plansza[0].length; y++){
				pozY = odstep*(y+1)+y*dlug;
				Gr.drawRect(pozX, pozY, szer, dlug);
				if(odkryte[x][y]==true){
					Gr.drawString(Integer.toString(plansza[x][y]), pozX + szer/2, pozY + dlug/2);
				}
			}
		}
		Gr.drawString(Integer.toString(nWcisniete), 400, 400);
		
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
	
	public boolean sprawdzWcisniete(){
		if(nWcisniete==1){
			odkryte[wcisniete[0][0]][wcisniete[0][1]]=true;
			return true;
		}
		if(nWcisniete==2){
			odkryte[wcisniete[0][0]][wcisniete[0][1]]=true;
			odkryte[wcisniete[1][0]][wcisniete[1][1]]=true;
			nWcisniete=0;
			if(pola[wcisniete[0][0]][wcisniete[0][1]]==pola[wcisniete[1][0]][wcisniete[1][1]]){
				return true;
			}
			else return false;
		}
		return true;
	}
	
	public boolean czyOdkryte(int x, int y){
		if(odkryte[x][y]) return true;
		return false;
	}
	
	public void resetujWcisniete(){
		odkryte[wcisniete[0][0]][wcisniete[0][1]]=false;
		odkryte[wcisniete[1][0]][wcisniete[1][1]]=false;
	}
	public void mouseClicked(MouseEvent evt){
		mouseX = evt.getX();
		mouseY = evt.getY();
		click = true;
	}
	
	public void mouseEntered(MouseEvent evt){
		
	}
	
	public void mousePressed(MouseEvent evt){


	}

	public void mouseExited(MouseEvent evt){
		
	}

	public void mouseReleased(MouseEvent evt){

	}
	
	public void mouseDragged(MouseEvent evt){

	}
	
	public void mouseMoved(MouseEvent evt){

	}
	
	
	public boolean czyWpolu(int mouseX, int mouseY){
		if(mouseX<odstep || mouseY<odstep) return false;
		
		int wsp = (mouseX-odstep)/(szer+odstep);
		int start = wsp*(szer+odstep)+odstep;
		int koniec = start+szer;
		if(mouseX>=start && mouseX<koniec){ 
			wsp = (mouseY-odstep)/(dlug+odstep);
			start = wsp*(dlug+odstep)+odstep;
			koniec = start+dlug;
			if(mouseY>=start && mouseY<koniec) return true;
			else return false;
		}
		else return false;
	}
	
	public int[] poleMysz(int mouseX, int mouseY){
		int x = (mouseX-odstep)/(szer+odstep);
		int y = (mouseY-odstep)/(dlug+odstep);
		return new int[] {x,y};
	}
	
	public boolean sprawdzWygrana(boolean[][] odkryte){
		for(int x=0; x<odkryte.length; x++){
			for(int y=0; y<odkryte[0].length; y++){
				if(odkryte[x][y]==false) return false;
			}
		}
		return true;
	}
}

