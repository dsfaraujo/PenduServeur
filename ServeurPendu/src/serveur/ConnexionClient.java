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
 * Encapsule une connexion avec un client.
 * Un client roule dans un thread dedie.
 * 
 * Lorsqu'un message est envoye par un autre client, cet
 * objet est alertee.
 * 
 * @author rebecca
 */
public class ConnexionClient implements Observer, Runnable{
	private Socket socket;
	private Conversation conversation;
	static String j1, j2;

	/**
	 * Constructeur parametrique 
	 * 
	 * @param socket la connexion vers le client
	 * @param conversation la conversation
	 */
	public ConnexionClient(Socket socket, Conversation conversation)
	{
		this.socket = socket;
		this.conversation = conversation;
		conversation.addObserver(this);
	}

	/**
	 * Mise a jour de la conversation
	 */
	public void update(Observable arg0, Object arg1) {
		PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream());
			out.println("********** " + conversation.getLastMessage());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
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
			out.println("Connectes : " + conversation.getClients());	        
			out.println("Entrez votre nom.");
			out.flush();

			String motPendu = Main.motPendu;
			int maxFauxFois = 10;
			int taillePendu;
			String pendu = new String(new char[motPendu.length()]).replace("\0", "_");


			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String nom = in.readLine();
			while (nom.equals("?") || nom.equals("QUIT") || !conversation.ajouterClient(nom))
			{
				out.println("S'il vous plait choisissez un nom unique et différent de ? et QUIT.");
				out.flush();
				nom = in.readLine();
			} 

			out.println("Entrer QUIT pour quitter");
			out.flush();
			out.println("Bienvenue au jeu du Pendu");
			out.flush();
			out.println("Voulez vous jouer contre l'ordinateur ou un autre joueur? Ordi[o/j]Joueur");
			out.flush();
			String msg = in.readLine();
			if (msg.equals("o") || msg.equals("O")) {
				out.println(pendu);
				out.flush();
				String penduTemp; 

				while(!msg.equals("QUIT"))
				{
					if (msg.equals("?"))
					{
						out.println("En ligne : " + conversation.getClients());
						out.flush();
						java.util.Date date = new java.util.Date();
						System.out.println(msg + " : " + date.toString());

					}
					//si le client veut joueur contre l'ordinateur

					else
					{
						taillePendu = motPendu.length();
						out.println("Entrez une lettre");
						out.flush();
						while(maxFauxFois != 0) {
							char x = 0;
							String y; 
							String motAffiche = "";

							msg = in.readLine();
							//y = msg.toCharArray();


							for (int i = 0; i < taillePendu; i++) {
								x = motPendu.charAt(i);
								if (motPendu.charAt(i) == msg.charAt(0)) {
									motAffiche += msg.charAt(0);
								} else if (pendu.charAt(i) != '_') {
									motAffiche += motPendu.charAt(i);
								} else {
									motAffiche += "_";
								}
							}
							if (pendu.equals(motAffiche)) {
								maxFauxFois--;
								out.println("Mot invalide. Il reste "+ maxFauxFois +" tentatives. Rentrez un nouveau mot");
								//out.println("Incorrect! "+ nom +" essayez de nouveau");
								out.flush();
								out.println(pendu);
								out.flush();
							} else {
								pendu = motAffiche;
								out.println("Mot valide");
								//out.println("Cool, " +msg+ " est correct! "+ nom +" essayez une autre lettre");
								out.flush();
								out.println(pendu);
								out.flush();
							}
							if (pendu.equals(motPendu)) {
								out.println( nom +" a gagné!! Le mot est: " + pendu);
								out.flush();
								msg = "QUIT";
							}
							else if (maxFauxFois == 0) {
								out.println(nom +" a perdu! Le mot a été:" + motPendu);
								out.flush();
								msg = "QUIT";
							}

							//conversation.parler(new Message(nom + " entrée", msg));

						}

						msg = in.readLine();
					}
				}
			}
			//si le client demande de jouer contre une autre client
			else {
				out.println("En attend d'un autre joueur");
				out.flush();


				while(!msg.equals("QUIT"))
				{
					if (msg.equals("?"))
					{
						out.println("En ligne : " + conversation.getClients());
						out.flush();
						java.util.Date date = new java.util.Date();
						System.out.println(msg + " : " + date.toString());

					}
					else
					{

						j1 = conversation.getClients().get(0);
						j2 = conversation.getClients().get(1);
						System.out.println("j1 = "+j1);
						System.out.println("j2 = "+j2);




						List<String> joueurs = conversation.getClients();
						if (joueurs.size() == 2) {
							String bourreau = joueurs.get(new Random().nextInt(joueurs.size()));
							conversation.parler(new Message(bourreau + " est le bourreau", " Entrez une mot pour l'autre joueur déviner"));
							
							while (bourreau.length() >= 1) {

								//out.flush();
							
								msg = in.readLine();
								System.out.println(msg);
								
								
								motPendu = msg; 
								pendu = new String(new char[motPendu.length()]).replace("\0", "_");

								taillePendu = motPendu.length();
								conversation.parler(new Message(pendu, "Entrez une lettre"));
								out.flush();
								msg = in.readLine();
								while(maxFauxFois != 0) {
							
									char x = 0;
									String y; 
									String motAffiche = "";

									msg = in.readLine();
									//y = msg.toCharArray();


									for (int i = 0; i < taillePendu; i++) {
										x = motPendu.charAt(i);
										if (motPendu.charAt(i) == msg.charAt(0)) {
											motAffiche += msg.charAt(0);
										} else if (pendu.charAt(i) != '_') {
											motAffiche += motPendu.charAt(i);
										} else {
											motAffiche += "_";
										}
									}
									if (pendu.equals(motAffiche)) {
										maxFauxFois--;
										conversation.parler(new Message("Mot invalide. Il reste "+ maxFauxFois, " tentatives. Rentrez un nouveau mot"));
										//out.println("Incorrect! "+ nom +" essayez de nouveau");
										//out.flush();
										conversation.parler(new Message(pendu, ""));
										out.flush();
									} else {
										pendu = motAffiche;
										out.println("Mot valide");
										//out.println("Cool, " +msg+ " est correct! "+ nom +" essayez une autre lettre");
										//out.flush();
										conversation.parler(new Message(pendu, ""));
										//out.flush();
									}
									if (pendu.equals(motPendu)) {
										conversation.parler(new Message( nom +" a gagné!! Le mot est: ",  pendu));
										//out.flush();
										msg = "QUIT";
									}
									else if (maxFauxFois == 0) {
										conversation.parler(new Message(nom +" a perdu! Le mot a été:", motPendu));
										//out.flush();
										msg = "QUIT";
									}

									//conversation.parler(new Message(nom + " entrée", msg));
									msg = in.readLine();
								}

								msg = in.readLine();
							}

						}
					}

				}
			}

			conversation.deleteObserver(this);  
			conversation.retirerClient(nom);
			socket.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public static boolean dansMotRentree( char mot, char[] motRentre) {
		return new String(motRentre).contains(String.valueOf(mot));
	}





}
