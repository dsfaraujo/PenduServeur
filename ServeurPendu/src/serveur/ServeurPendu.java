package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ********************************************************
 * Encapsule un serveur qui permet a un maximum de NB_CLIENTS clients a la fois
 * de participer a une conversation sous forme de protocole TCP sur port PORT
 * 
 * @author Diana Soares, Patricia Shimizu
 *
 */
public class ServeurPendu implements Observer{

	JeuPendu jeu = new JeuPendu();

	final int NB_CLIENTS = 30;
	final int PORT = 8888;
	static Socket socketVersLeClient;

	static Socket socketVersLeClient2;


	/**
	 * ********************
	 * Execution du serveur
	 */
	public void run()
	{
		ServerSocket socketDuServeur = null;// Le socket qui écoute sur le port 8000 et accepte les connexions.
		ExecutorService service = null;

		try 
		{
			socketDuServeur = new ServerSocket(PORT);
			System.out.println("Le serveur est à l'écoute du port " + socketDuServeur.getLocalPort());

			service = Executors.newFixedThreadPool(NB_CLIENTS);

			jeu.addObserver(this);

			while(true)
			{
				// Connexion d'un client
				socketVersLeClient = socketDuServeur.accept();
				socketVersLeClient2 = null;
				
					System.out.println("Un client s'est connecté");
					service.submit(new ConnexionClient(socketVersLeClient, jeu));
					//s'il y a déjà 1 client connecté, il crée un nouveau socket pour le 2eme
				while(socketVersLeClient.isConnected()) {
					socketVersLeClient2 = socketDuServeur.accept();
					System.out.println("Un 2eme client s'est connecté");
					//service.submit(new ConnexionClient(socketVersLeClient, socketVersLeClient2, jeu));
					service.submit(new ConnexionClient(socketVersLeClient2, socketVersLeClient, jeu));

				}
				
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				socketDuServeur.close();  
				shutdownAndAwaitTermination(service);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * *****************************************
	 * Utilitaire pour fermer le pool de thread.
	 * Calqué de la documentation Oracle pour la classe ExecutorService
	 * @param pool
	 */
	static void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * ****************************************************
	 * Lorsqu'un client envoie un message a la conversation
	 * Le serveur est alerte. Ceci sert uniquemet a afficher
	 * la conversation a la console.
	 */
	public void update(Observable arg0, Object arg1) 
	{		
		System.out.println(jeu.getLastMessage());
	}
}
