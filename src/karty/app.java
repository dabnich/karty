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

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;

public class app extends Applet implements Runnable, MouseListener, MouseMotionListener{
	
	Thread thread;
	boolean pracuje;
	Graphics Gr;
	Graphics Gn;
	int szerokosc=4;
	int dlugosc=4;

	
	plansza plansza;
	baza baza;
	status status;

	int mouseX, mouseY;
	boolean click=false;
	
	boolean nickOk;
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
		
		plansza = new plansza(szerokosc, dlugosc, 120, 100);
		status = new status();
		baza = new baza();
		baza.stworzTabele();
		
		
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
				panel.add(textDane);
				panel.add(buttonRestart, FlowLayout.LEFT);
				panel.setVisible(true);
			}
			
		});
		
		buttonRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				resetujGre();
			}
		});
		
		panel.add(textDane);
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
		//rysujPlansze();
		while(pracuje){
			if(nickOk){
				if(click){
					click=false;
					
					if(plansza.czyWpolu(mouseX, mouseY)){
						if(!status.aktywny) status.wystartuj();
						plansza.odkryjMysz(mouseX, mouseY);
						if(!plansza.czyOdkryte()){
							plansza.nWcisniete++;
							status.dodajOdkrycie();
							Gr.clearRect(0, 0, getWidth(), getHeight());
							if(!plansza.sprawdzWcisniete()){
								rysujPlansze();
								plansza.resetujWcisniete();
								rysujStatus();
								try{
									Thread.sleep(Constants.czasOdslony);
								}
								catch(InterruptedException exc){};
	
								Gr.clearRect(0, 0, getWidth(), getHeight());
								rysujPlansze();
							}
							else{ 
								if(sprawdzWygrana(plansza.odkryte)){
									baza.zapiszRekord(nick, status.odkrycia, status.podajCzas(), plansza.iloscPol);
									rysujStatus();
								}
								else rysujPlansze();
									
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
	*/
	public void rysujPlansze(){
		int pozX, pozY;
		Image img;
		Gr.setFont(new Font("Arial", 30, 25));
		for(int x=0; x<plansza.pole.length; x++){
			pozX = plansza.karta.odstep*(x+1)+x*plansza.karta.szerokosc;
			for(int y=0; y<plansza.pole[0].length; y++){
				pozY = plansza.karta.odstep*(y+1)+y*plansza.karta.dlugosc;
				img = new ImageIcon("r.gif").getImage();
				Gr.drawImage(img, pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc, null);
				if(plansza.odkryte[x][y]==true){
					img = new ImageIcon(Integer.toString(plansza.pole[x][y])+".JPG").getImage();
					Gr.drawImage(img, pozX, pozY, plansza.karta.szerokosc, plansza.karta.dlugosc, null);
				}
			}
		}
	}
	
	public void rysujStatus(){
		int pozX = (plansza.karta.szerokosc+plansza.karta.odstep)*plansza.szerokosc+100;
		Gn.clearRect(pozX, 0, 200, 300);
		Gn.drawString("Odkrycia: "+Integer.toString(status.odkrycia), pozX, 50);
		Gn.drawString("Czas: "+String.format("%.1f", status.podajCzas()), pozX, 100);
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
	
	public void resetujGre(){
		Gr.clearRect(0, 0, getWidth(), getHeight());
		plansza = new plansza(szerokosc, dlugosc, 50, 50);
		status = new status();
		textDane.setText(baza.pobierzWynikiString(10));
		panel.removeAll();
		panel.add(buttonRestart);
		panel.add(textDane);
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
}

