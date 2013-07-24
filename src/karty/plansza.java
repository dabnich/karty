package karty;

import java.util.Random;

public class plansza {
	
	int szerokosc=0;
	int dlugosc=0;
	int iloscPol=0;
	int iloscKart=0;
	int pole[][];
	int nWcisniete=0;
	int[][] wcisniete = new int[2][2];
	boolean odkryte[][];
	
	karta karta;
	
	
	public plansza(int szerokosc, int dlugosc, int szerKarty, int dlugKarty){
		this.szerokosc = szerokosc;
		this.dlugosc = dlugosc;
		this.iloscPol = szerokosc*dlugosc;
		this.iloscKart = szerokosc*dlugosc/2;
		pole = new int[szerokosc][dlugosc];
		pole = generuj(szerokosc, dlugosc);
		odkryte = new boolean[szerokosc][dlugosc];
		karta = new karta(szerKarty, dlugKarty);
	}
	
	
	private int[][] generuj(int szerokosc, int dlugosc){
		Random rand;
		int los;
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
	
	private boolean sprawdzLos(int los, int tab[], int n){
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
			if(pole[wcisniete[0][0]][wcisniete[0][1]]==pole[wcisniete[1][0]][wcisniete[1][1]]){
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
	
	public boolean czyOdkryte(){
		if( odkryte [wcisniete[nWcisniete][0]] [wcisniete[nWcisniete][1]] ) return true;
		return false;
	}
	
	public void resetujWcisniete(){
		odkryte[wcisniete[0][0]][wcisniete[0][1]]=false;
		odkryte[wcisniete[1][0]][wcisniete[1][1]]=false;
	}
	
	
	public void odkryj(int x, int y){
		wcisniete[nWcisniete][0] = x;
		wcisniete[nWcisniete][1] = y;
	}
	
	public void odkryjMysz(int mouseX, int mouseY){
		odkryj(poleMyszX(mouseX), poleMyszY(mouseY));
	}
	
	
	public int poleMyszX(int mouseX){
		int x = (mouseX-karta.odstep)/(karta.szerokosc+karta.odstep);
		return x;
	}
	
	public int poleMyszY(int mouseY){
		int y = (mouseY-karta.odstep)/(karta.dlugosc+karta.odstep);
		return y;
	}
	
	
	public boolean czyWpolu(int mouseX, int mouseY){
		if(	mouseX<karta.odstep || mouseY<karta.odstep || 
			mouseX>(karta.odstep+karta.szerokosc)*szerokosc || mouseY>(karta.odstep+karta.dlugosc)*dlugosc
			) 
			return false;
		
		int wsp = (mouseX-karta.odstep)/(karta.szerokosc+karta.odstep);
		int start = wsp*(karta.szerokosc+karta.odstep)+karta.odstep;
		int koniec = start+karta.szerokosc;
		if(mouseX>=start && mouseX<koniec){ 
			wsp = (mouseY-karta.odstep)/(karta.dlugosc+karta.odstep);
			start = wsp*(karta.dlugosc+karta.odstep)+karta.odstep;
			koniec = start+karta.dlugosc;
			if(mouseY>=start && mouseY<koniec) return true;
			else return false;
		}
		else return false;
	}
	


}
