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

	public double podajCzas(){
		if(!aktywny) return 0;
		data = new Date();
		long aktCzas = data.getTime();
		long roznica = aktCzas - startCzas;
		return (double) roznica/1000;
	}
	
	public void dodajOdkrycie(){
		odkrycia++;
	}
}
