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
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class app extends Applet implements Runnable, MouseListener, MouseMotionListener{
	
	Thread thread;
	boolean pracuje;
	Graphics Gr;
	Graphics Gn;
	int szerokosc=5;
	int dlugosc=4;

	
	plansza plansza;
	baza baza;
	status status;
	obrazki obrazki;

	int mouseX, mouseY;
	boolean click=false;
	
	boolean nickOk, nowaGra;
	String nick;

	
	Panel panel = new Panel();
	TextField input = new TextField(20);
	TextArea textDane = new TextArea();
	
	Button buttonNick = new Button("ZatwierdŸ");
	Button buttonRestart = new Button("Nowa gra");
	Label label = new Label("Podaj swój Nick: ");
	
	public void init(){
		pracuje = false;
		nickOk = false;
		nick = "gosc";
		nowaGra = false;
		
		plansza = new plansza(szerokosc, dlugosc, 76, 120);
		status = new status();
		baza = new baza();
		baza.stworzTabele();
		obrazki = new obrazki("C:\\Documents and Settings\\dabnich\\Moje dokumenty\\Downloads");
		
		
		addMouseListener(this);
		addMouseMotionListener(this);
		resize(Constants.szerokoscOkna, Constants.wysokoscOkna);
		Gr = getGraphics();
		Gn = getGraphics();
		
		setLayout(new BorderLayout());
		panel.setBackground(new Color(200,150,150));
		textDane.setText(baza.pobierzWynikiString(10));
		textDane.setEditable(false);

		buttonNick.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				nickOk=true;
				nick = input.getText();
				rysujPlansze();
	
				panel.removeAll();
				textDane.setVisible(true);
				//panel.add(textDane);
				panel.add(buttonRestart, FlowLayout.LEFT);
				panel.setVisible(true);
			}
			
		});
		
		buttonRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				nowaGra = true;
			}
		});
		
		//panel.add(textDane);
		panel.add(label);
		panel.add(input);
		panel.add(buttonNick);
		add(panel, BorderLayout.SOUTH);
		panel.add(buttonRestart, FlowLayout.LEFT);
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
		rysujPlansze();
		wyswietlWyniki();
		while(pracuje){
			if(nickOk){
				if(nowaGra) resetujGre();
				if(click){
					click=false;
					if(plansza.czyWpolu(mouseX, mouseY)){
						if(!status.aktywny) status.wystartuj();
						plansza.odkryjMysz(mouseX, mouseY);
						if(!plansza.czyOdkryte()){
							plansza.nWcisniete++;
							status.dodajOdkrycie();
							
							if(!plansza.sprawdzWcisniete()){
								rysujKarty();
								plansza.resetujWcisniete();
								rysujStatus();
								try{
									Thread.sleep(Constants.czasOdslony);
								}
								catch(InterruptedException exc){};
	
								
								rysujKarty();
							}
							else{ 
								rysujKarty();
								if(sprawdzWygrana(plansza.odkryte)){
									baza.zapiszRekord(nick, status.odkrycia, status.podajCzas(), plansza.iloscPol);
									rysujStatus();
									wyswietlWyniki();
									while(!nowaGra){};
								}
							}
						}
					}
				}
	
				rysujStatus();
				try{
					Thread.sleep(20);
				}
				catch(InterruptedException exc){};
			}
		}
	}
	
	/*
	public void rysujPlansze(){
		Gr.clearRect(0, 0, (plansza.karta.odstep+plansza.karta.szerokosc)*plansza.szerokosc, (plansza.karta.odstep+plansza.karta.dlugosc)*plansza.dlugosc);
		int pozX, pozY;
		Gr.setFont(new Font("Arial", 30, 25));
		for(int x=0; x<plansza.pole.length; x++){
			pozX = plansza.karta.odstep*(x+1)+x*plansza.karta.szerokosc;
			for(int y=0; y<plansza.pole[0].length; y++){
				pozY = plansza.karta.odstep*(y+1)+y*plansza.karta.dlugosc;
				Gr.drawRect(pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc);
				if(plansza.odkryte[x][y]==true){
					Gr.drawString(Integer.toString(plansza.pole[x][y]), pozX + plansza.karta.szerokosc/2, pozY + plansza.karta.dlugosc/2);
				}
			}
		}
	}
	
	/*public void rysujKarte(int x, int y){
		int pozX = x*(plansza.karta.szerokosc+plansza.karta.odstep)+plansza.karta.odstep;
		int pozY = y*(plansza.karta.dlugosc+plansza.karta.odstep)+plansza.karta.odstep;
		Gr.clearRect(pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc);
		Gr.drawRect(pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc);
		
		if(plansza.odkryte[x][y]==true){
			Gr.drawString(Integer.toString(plansza.pole[x][y]), pozX + plansza.karta.szerokosc/2, pozY + plansza.karta.dlugosc/2);
		}
	}*/
	
	public void rysujKarte(int x, int y){
		int pozX = x*(plansza.karta.szerokosc+plansza.karta.odstep)+plansza.karta.odstep;
		int pozY = y*(plansza.karta.dlugosc+plansza.karta.odstep)+plansza.karta.odstep;
		Image imgZakryta = new ImageIcon("karta.jpg").getImage();
		//Image img = new ImageIcon(plansza.pole[x][y]+".jpg").getImage();
		Image img = new ImageIcon(obrazki.obrazek[plansza.pole[x][y]]).getImage();
		
		Gr.clearRect(pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc);
		
		if(plansza.odkryte[x][y]==true){
			Gr.drawImage(img, pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc, null);
		}
		else{
			Gr.drawImage(imgZakryta, pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc, null);
		}
	}
	
	public void rysujKarty(){
		if(plansza.nWcisniete>=1){
			rysujKarte(plansza.wcisniete[0][0], plansza.wcisniete[0][1]);
			if(plansza.nWcisniete==2){
				rysujKarte(plansza.wcisniete[1][0], plansza.wcisniete[1][1]);
			}
		}
		else{
			rysujKarte(plansza.wcisniete[0][0], plansza.wcisniete[0][1]);
			rysujKarte(plansza.wcisniete[1][0], plansza.wcisniete[1][1]);
		}
	}
	
	
	public void rysujPlansze(){
		int pozX, pozY;
		Image img;
		Gr.setFont(new Font("Arial", 30, 25));
		for(int x=0; x<plansza.pole.length; x++){
			pozX = plansza.karta.odstep*(x+1)+x*plansza.karta.szerokosc;
			for(int y=0; y<plansza.pole[0].length; y++){
				pozY = plansza.karta.odstep*(y+1)+y*plansza.karta.dlugosc;
				img = new ImageIcon("karta.jpg").getImage();
				Gr.drawImage(img, pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc, null);
				if(plansza.odkryte[x][y]==true){
					img = new ImageIcon(obrazki.obrazek[plansza.pole[x][y]]).getImage();
					Gr.drawImage(img, pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc, null);
				}
			}
		}
	}
	 
	
	public void rysujStatus(){
		int pozY = (plansza.karta.dlugosc+plansza.karta.odstep)*plansza.dlugosc+50;
		Gn.clearRect(0, pozY-30, 200, 300);
		Gn.drawString("Odkrycia: "+Integer.toString(status.odkrycia), 20, pozY);
		Gn.drawString("Czas: "+String.format("%.1f", status.podajCzas()), 20, pozY+30);
	}
	
	public void wyswietlWyniki(){
		int pozX = (plansza.karta.szerokosc+plansza.karta.odstep)*plansza.szerokosc+20;
		ArrayList<ArrayList<String>> wyniki = baza.pobierzWyniki(10);
		ArrayList<String> wiersz;
		Gn.clearRect(pozX, 30, getWidth()-pozX, wyniki.size()*20+(75-30));
		Gn.drawString("WYNIKI: ", pozX+140, 20);
		Gn.drawString("NICK", pozX, 50);
		Gn.drawString("WYNIK", pozX+65, 50);
		Gn.drawString("ODKRYCIA", pozX+115, 50);
		Gn.drawString("CZAS", pozX+195, 50);
		Gn.drawString("DATA", pozX+260, 50);
		for(int i=0; i<wyniki.size(); i++){
			wiersz = wyniki.get(i);
			for(int n=0; n<wiersz.size(); n++){
				Gn.drawString(wiersz.get(n), pozX+(n*65), (i*20)+75);
			}
		}
	}
	
	public void resetujGre(){
		Gr.clearRect(0, 0, getWidth(), getHeight());
		plansza = new plansza(szerokosc, dlugosc, 76, 120);
		obrazki = new obrazki("C:\\Documents and Settings\\dabnich\\Moje dokumenty\\Downloads");
		status = new status();
		nowaGra = false;
		panel.removeAll();
		panel.add(buttonRestart);
		if(!nickOk){
			panel.add(label);
			panel.add(input);
			panel.add(buttonNick);
		}
		add(panel, BorderLayout.SOUTH);
		panel.setVisible(true);
	}
	
	public boolean sprawdzWygrana(boolean[][] odkryte){
		for(int x=0; x<odkryte.length; x++){
			for(int y=0; y<odkryte[0].length; y++){
				if(odkryte[x][y]==false) return false;
			}
		}
		return true;
	}
	

	

	public void mouseClicked(MouseEvent evt){

	}
	
	public void mouseEntered(MouseEvent evt){
		
	}
	
	public void mousePressed(MouseEvent evt){
		mouseX = evt.getX();
		mouseY = evt.getY();
		click = true;

	}

	public void mouseExited(MouseEvent evt){
		
	}

	public void mouseReleased(MouseEvent evt){

	}
	
	public void mouseDragged(MouseEvent evt){

	}
	
	public void mouseMoved(MouseEvent evt){

	}
	

}

