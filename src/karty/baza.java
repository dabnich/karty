package karty;

import java.sql.*;
import java.util.ArrayList;

import org.sqlite.*;

public class baza {

	public static String driver = "org.sqlite.JDBC";
	public static String url = "jdbc:sqlite:baza";
	public boolean ok=false;
	Connection conn = null;
	Statement stat = null;
	
	public baza(){
		try{
			Class.forName(driver);
		}
		catch(Exception e){
			System.err.print(e.getMessage());
			ok=false;
		}
		
		try{
			conn = DriverManager.getConnection(url);
		}
		catch(Exception e){
			System.err.print(e.getMessage());
			ok=false;
		}
		ok=true;
	}
	
	public void stworzTabele(){
		String query = 
			"CREATE TABLE IF NOT EXISTS wyniki("
				+"id int AUTO_INCREMENT PRIMARY KEY,"
				+"nick varchar(20) NOT NULL DEFAULT 'gosc',"
				+"odkrycia int NOT NULL,"
				+"czasTrwania DOUBLE NOT NULL,"
				+"wynik int DEFAULT 0,"
				+"iloscPol int DEFAULT 16,"
				+"zarejestrowano TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
			+")";
		
		try{
			stat = conn.createStatement();
			stat.execute(query);
		}
		catch(Exception e){
			System.err.print(e.getMessage());
			//return false;
		}
	}
	
	public void zapiszRekord(String nick, int odkrycia, double czasTrwania, int iloscPol){
		int wynik = obliczWynik(odkrycia, czasTrwania, iloscPol);
		String query = 
			"INSERT INTO wyniki (nick, odkrycia, czasTrwania, wynik, iloscPol) "
			+"VALUES"
			+"('"+nick+"', "+odkrycia+", "+czasTrwania+", "+wynik+", "+iloscPol+")";
		
		try{
			stat = conn.createStatement();
			stat.execute(query);
		}
		catch(Exception e){
			System.err.print(e.getMessage());
			//return false;
		}
	}
	
	public void wyswietlWyniki(){
		String query = "SELECT nick, odkrycia, czasTrwania, wynik, iloscPol, zarejestrowano FROM wyniki";
		ResultSet result = null;
		try{
			stat = conn.createStatement();
			result = stat.executeQuery(query);
			while(result.next()){
				System.out.print(result.getString("nick")+"\n");
				System.out.print(result.getString("odkrycia")+"\n");
				System.out.print(result.getString("czasTrwania")+"\n");
				System.out.print(result.getString("wynik")+"\n");
				System.out.print(result.getString("iloscPol")+"\n");
				System.out.print(result.getString("zarejestrowano")+"\n");
			}
		}
		catch(Exception e){
			System.err.print(e.getMessage());
			//return false;
		}
	}
	
	public ArrayList<ArrayList<String>> pobierzWyniki(int maxIlosc){
		String query = "SELECT nick, odkrycia, czasTrwania, wynik, iloscPol, zarejestrowano FROM wyniki ORDER BY wynik DESC LIMIT "+maxIlosc;
		ResultSet result = null;
		ArrayList<ArrayList<String>> wyniki = new ArrayList<ArrayList<String>>();
		ArrayList<String> wynik = new ArrayList<String>();
		try{
			stat = conn.createStatement();
			result = stat.executeQuery(query);

			while(result.next()){
				wynik.add(0, result.getString("nick"));
				wynik.add(1, result.getString("wynik"));
				wynik.add(2, result.getString("odkrycia"));
				wynik.add(3, result.getString("czasTrwania"));
				wynik.add(4, result.getString("zarejestrowano"));
				wyniki.add(wynik);
				
				wynik.clear();
				//System.out.print(result.getString("iloscPol")+"\n");
			}
			
		}
		catch(Exception e){
			System.err.print(e.getMessage());
			//return false;
		}
		return wyniki;
	}
	
	public String pobierzWynikiString(int maxIlosc){
		String query = "SELECT nick, odkrycia, czasTrwania, wynik, iloscPol, zarejestrowano FROM wyniki ORDER BY wynik DESC LIMIT "+maxIlosc;
		ResultSet result = null;
		String wynik = "";
		try{
			stat = conn.createStatement();
			result = stat.executeQuery(query);

			while(result.next()){
				wynik += result.getString("nick")+"\t";
				wynik += result.getString("wynik")+"\t";
				wynik += result.getString("odkrycia")+"\t";
				wynik += result.getString("czasTrwania")+"\t";
				wynik += result.getString("zarejestrowano")+"\t";
				wynik += "\n";
				//System.out.print(result.getString("iloscPol")+"\n");
			}
			
		}
		catch(Exception e){
			System.err.print(e.getMessage());
			//return false;
		}
		return wynik;
	}
	
	public int obliczWynik(int odkrycia, double czasTrwania, int iloscPol){
		int maxWynik = Constants.maxWynik;
		int czasReakcji = 250;
		int czasOdslony = Constants.czasOdslony;
		
		double wspOdkryc = (double)iloscPol/odkrycia;
		
		int minCzas = (czasOdslony+czasReakcji)*iloscPol;
		double wspCzasu = (double)(minCzas/(double)(czasTrwania*1000));
		
		int wynik = (int)(wspOdkryc*(maxWynik/2)+wspCzasu*(maxWynik/2));
		return wynik;
	}
}