package karty;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

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
			"INSERT INTO wyniki (nick, odkrycia, czasTrwania, wynik, iloscPol, zarejestrowano) "
			+"VALUES"
			+"('"+nick+"', "+odkrycia+", "+czasTrwania+", "+wynik+", "+iloscPol+", '"+new Date().toLocaleString()+"')";
		
		try{
			stat = conn.createStatement();
			stat.execute(query);
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
				wynik.add(result.getString("nick"));
				wynik.add(result.getString("wynik"));
				wynik.add(result.getString("odkrycia"));
				wynik.add(result.getString("czasTrwania"));
				wynik.add(result.getString("zarejestrowano"));
				wyniki.add(wynik);
				
				wynik = new ArrayList<String>();
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