package serveur;
/**
 * ********************************************************
 * Activité Principal
 * 
 * @author Diana Soares, Patricia Shimizu
 *
 */
public class Main 
{
	static String motPendu;
	public static void main(String args[])
	{   	
		// Demarrer un serveur de Chat
		//motPendu = UDPClient.envoyerMessage("DD");
		
		//System.out.println("mot pendu = "+ motPendu);
		ServeurPendu sc = new ServeurPendu();
		sc.run();
	}
}
