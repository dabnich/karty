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

	
	plansza plansza;
	baza baza;
	status status;

	int mouseX, mouseY;
	boolean click=false;

	
	Panel panel = new Panel();
	TextField input = new TextField(20);
	TextArea textDane = new TextArea();
	//Label textDane = new Label();
	Button button = new Button("Zatwierdü");
	
	public void init(){
		pracuje = false;
		
		plansza = new plansza(szerokosc, dlugosc, 50, 50);
		baza = new baza();
		baza.stworzTabele();
		status = new status();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		resize(Constants.szerokoscOkna, Constants.wysokoscOkna);
		Gr = getGraphics();
		Gn = getGraphics();
		
		setLayout(new BorderLayout());
		
		
		textDane.setText(baza.pobierzWynikiString(10));

		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//nick = input.getText();
				//textDane.setText(nick);
			}
		});
		
		panel.add(input);
		panel.add(button);
		panel.add(textDane);
		add(panel, BorderLayout.SOUTH);
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
		while(pracuje){

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
							rysujPlansze();
							if(sprawdzWygrana(plansza.odkryte)){
								Gn.clearRect(400, 400, 1000, 1000);
								Gr.drawString("wygrana", 450, 550);
								Gn.drawString(String.format("%.1f", status.podajCzas()),500, 200);
								Gn.drawString(Integer.toString(status.odkrycia),550, 500);
								baza.zapiszRekord(input.getText(), status.odkrycia, status.podajCzas(), plansza.iloscPol);
								//init();
								//Gr.clearRect(0, 0, getWidth(), getHeight());
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
	

	
	public boolean sprawdzWygrana(boolean[][] odkryte){
		for(int x=0; x<odkryte.length; x++){
			for(int y=0; y<odkryte[0].length; y++){
				if(odkryte[x][y]==false) return false;
			}
		}
		return true;
	}
}

