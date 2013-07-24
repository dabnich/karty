package karty;

import java.util.Date;


public class status {
	
	double czas;
	int odkrycia;
	Date data;
	long startCzas=0;
	boolean aktywny=false;
	
	public status(){
		data = new Date();
	}
	
	public void wystartuj(){
		data = new Date();
		startCzas = data.getTime();
		aktywny=true;
	}
	
	public void stopuj(){
		aktywny = false;
		czas = podajCzas();
	}

	public double podajCzas(){
		if(!aktywny){
			if(startCzas==0) return 0;
			else return czas;
		}
		data = new Date();
		
		long aktCzas = data.getTime();
		long roznica = aktCzas - startCzas;
		czas = roznica/1000;
		return (double) roznica/1000;
	}
	
	public void dodajOdkrycie(){
		odkrycia++;
	}
}
