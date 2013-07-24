package karty;
import java.io.File;
import java.io.FileFilter;
import java.util.Random;


public class obrazki {
	String sciezka;
	int ilosc;
	String [] obrazek;
	File[] lista;
	
	private FileFilter filtr = new FileFilter(){
		@Override
		public boolean accept(File plik) {
			String[] formaty = new String[]{"jpg", "gif", "png"};
			for (String format : formaty) {
				if(plik.getName().toLowerCase().endsWith(format)) return true;
			}
			return false;
		}
	};
	
	
	public obrazki(String sciezka){
		this.sciezka = sciezka;
		File folder = new File(sciezka);
		lista = folder.listFiles(filtr);
		this.ilosc = lista.length;
		obrazek = new String[ilosc];
		for(int i=0; i<ilosc; i++){
			obrazek[i] = lista[i].getAbsolutePath();
		}
		zamieszaj();
	}
	
	public void zamieszaj(){
		Random rand;
		int los;
		int[] losy = new int[ilosc];
		String[]obrazek2 = new String[ilosc];
		for(int i=0; i<ilosc; i++){
			rand = new Random();
			los = rand.nextInt(ilosc);
			while(!sprawdzLos(los, losy, i)){
				rand = new Random();
				los = rand.nextInt(ilosc);
			}
			losy[i] = los;
			obrazek2[i] = obrazek[los];
		}
		obrazek = obrazek2;
	}
	
	private boolean sprawdzLos(int los, int tab[], int n){
		if(n<1) return true;
		for(int i=0; i<n; i++){
			if(tab[i]==los)return false;
		}
		return true;
	}
	
	
}
