package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;

/**
 * ********************************************************
 * Encapsule une connexion avec un client.
 * Un client roule dans un thread dedie.
 * 
 * Lorsqu'un message est envoye par un autre client, cet
 * objet est alertee.
 * 
 * @author diana
 */
public class ConnexionClient implements Observer, Runnable{
	private Socket socket;
	private JeuPendu jeu;
	static String j1, j2;

	/**
	 * ********************************************************
	 * Constructeur parametrique 
	 * 
	 * @param socket la connexion vers le client
	 * @param conversation la conversation
	 */
	public ConnexionClient(Socket socket, JeuPendu conversation)
	{
		this.socket = socket;
		this.jeu = conversation;
		conversation.addObserver(this);
	}

	/**
	 * ********************************************************
	 * Mise a jour de la conversation
	 */
	public void update(Observable arg0, Object arg1) {
		PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream());
			out.println("---> " + jeu.getLastMessage());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ********************************************************
	 * Execution du thread pour un client
	 * 
	 * Le client doit donner son nom. Ce nom doit être unique et ne peut pas
	 * être un mot réservé (ni QUIT ni ?).
	 * 
	 * Pour quitter, il doit entrer "QUIT"
	 * Pour voir la liste des clients connectés, il doit entrer "?"
	 */
	public void run() {
		PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream());
			//out.println("Connectes : " + conversation.getClients());	        
			out.println("Entrez votre nom.");
			out.flush();

			String motPendu = Main.motPendu;
			int maxFauxFois = 10;
			int taillePendu;
			String pendu = new String(new char[motPendu.length()]).replace("\0", "_");

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String nom = in.readLine();
			
			//********************************************************
			while (nom.equals("?") || nom.equals("QUIT"))
			{
				out.println("S'il vous plait choisissez un nom unique et différent de ? et QUIT.");
				out.flush();
				nom = in.readLine();
			} 
	
			//********************************************************
	
			out.println("Entrer QUIT pour quitter");
			out.flush();
			out.println("Bienvenue au jeu du Pendu");
			out.flush();
			out.println("Voulez vous jouer contre l'ordinateur ou un autre joueur? Ordi[o/j]Joueur");
			out.flush();
			String msg = in.readLine();

			//*******************************************************
			if (msg.equals("o") || msg.equals("O")) {
				jeu.deleteObserver(this);  
				out.println(pendu);
				out.flush();
			
				while(!msg.equals("QUIT"))
				{
					if (msg.equals("?"))
					{
						out.println("En ligne : " + jeu.getClients());
						out.flush();
						java.util.Date date = new java.util.Date();
						System.out.println(msg + " : " + date.toString());

					}
					//*******************************************************
					//si le client veut joueur contre l'ordinateur
					else
					{
						taillePendu = motPendu.length();
						out.println("Entrez une lettre");
						out.flush();
						
						//rentre dans le jeu
						while(maxFauxFois != 0) {
			
							String motAffiche = "";
							msg = in.readLine();
							msg = msg.toUpperCase();
							//transforme le mot affiché en spaces vides = "_"
							for (int i = 0; i < taillePendu; i++) {
								if (motPendu.charAt(i) == msg.charAt(0)) {
									motAffiche += msg.charAt(0);
								} 
								else if (pendu.charAt(i) != '_') {
									motAffiche += motPendu.charAt(i);
								} 
								else {
									motAffiche += "_";
								}
							}
							//si le joueur rentre une mot valide
							if (pendu.equals(motAffiche)) {
								maxFauxFois--;
								out.println("Mot invalide. Il reste "+ maxFauxFois +" tentatives. Rentrez un nouveau mot");
								out.flush();
								out.println(pendu);
								out.flush();
							} 
							else {
								pendu = motAffiche;
								out.println("Mot valide");
								out.flush();
								out.println(pendu);
								out.flush();
							}
							//s'il ne reste plus de fois à joueur
							if (pendu.equals(motPendu)) {
								out.println( nom +" a gagné!! Le mot est: " + pendu);
								out.flush();
								msg = "QUIT";
							}
							else if (maxFauxFois == 0) {
								out.println(nom +" a perdu! Le mot a été: " + motPendu);
								out.flush();
								msg = "QUIT";
							}
						}
						msg = in.readLine();
					}
				}
			}
			//*******************************************************
			//si le client demande de jouer contre une autre client
			else {
				out.println("En attend d'un autre joueur");
				out.flush();
	
				while(!msg.equals("QUIT"))
				{
					if (msg.equals("?"))
					{
						java.util.Date date = new java.util.Date();
						System.out.println(msg + " : " + date.toString());
					}
					//si le cliet ne veut pas quitter, il rentre dans le chat
					else
					{
						jeu.ajouterClient(nom);
						j1 = jeu.getClients().get(0);
						j2 = jeu.getClients().get(1);
						System.out.println("j1 = "+j1);
						System.out.println("j2 = "+j2);
						List<String> joueurs = jeu.getClients();
						
						//s'il y a deux joueurs, le jeu commence
						if (joueurs.size() ==1) {
							while(joueurs.size() ==1) {
								Thread.sleep(1);
							}
							
						}
						if (joueurs.size() == 2) {
							
						
							String bourreau = joueurs.get(new Random().nextInt(joueurs.size()));
							jeu.parler(new Message(bourreau + " est le bourreau", " Entrez une mot pour l'autre joueur déviner"));
							
							//dès que le bourreau est choisi, il doit choisir un mot pour l'aure joueur déviner
							while (bourreau.length() >= 1) {

								msg = in.readLine();
								System.out.println(msg);
								msg = msg.toUpperCase();
								//valide le mot entree avec le serveur
								msg = UDPClient.envoyerMessage(msg);
								motPendu = msg; 
								pendu = new String(new char[motPendu.length()]).replace("\0", "_");
								taillePendu = motPendu.length();
								jeu.parler(new Message(pendu, "Entrez une lettre"));
								out.flush();
								msg = in.readLine();
								msg = msg.toUpperCase();
								
								//*******************************************************
								//valide les entrées
								while(maxFauxFois != 0) {

									String motAffiche = "";
									//msg = in.readLine();
									//msg = msg.toUpperCase();

									//transforme le mot affiché en spaces vides = "_"
									for (int i = 0; i < taillePendu; i++) {
										if (motPendu.charAt(i) == msg.charAt(0)) {
											motAffiche += msg.charAt(0);
										} 
										else if (pendu.charAt(i) != '_') {
											motAffiche += motPendu.charAt(i);
										} 
										else {
											motAffiche += "_";
										}
									}
									
									//si le joueur rentre une mot valide
									if (pendu.equals(motAffiche)) {
										maxFauxFois--;
										jeu.parler(new Message("Mot invalide. Il reste "+ maxFauxFois, " tentatives. Rentrez un nouveau mot"));
										jeu.parler(new Message(pendu, ""));
										
									} else {
										pendu = motAffiche;
										out.println("Mot valide");
										jeu.parler(new Message(pendu, ""));
										
									}
									//s'il ne reste plus de fois à joueur
									if (pendu.equals(motPendu)) {
										jeu.parler(new Message( nom +" a gagné!! Le mot est",  pendu));
										msg = "QUIT";
									}
									else if (maxFauxFois == 0) {
										jeu.parler(new Message(nom +" a perdu! Le mot a été", motPendu));
										msg = "QUIT";
									}
									msg = in.readLine();
									msg = msg.toUpperCase();
									//conversation.parler(new Message(nom + " entrée", msg));
								}
							}
						}
					}
				}
			}
			jeu.deleteObserver(this);  
			jeu.retirerClient(nom);
			socket.close();
		}
		//*******************************************************
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
