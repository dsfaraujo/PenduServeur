package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
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
 * @author Diana Soares, Patricia Shimizu
 */
public class ConnexionClient implements Observer, Runnable{
	private Socket socket, socket2;
	private JeuPendu jeu;
	static String j1, j2;
	String motChoisi = "";
	String essai = "";
	int MAX_ESSAI = 10;
	ArrayList<String> liste;
	int nombre_tiret = 100;
	int nombre_essai = MAX_ESSAI;

	/**
	 * ********************************************************
	 * Constructeur parametrique 
	 * 
	 * @param socket et socket2 la connexion vers le client
	 * @param jeu la conversation
	 */
	public ConnexionClient(Socket socket,JeuPendu jeu){
		this.socket = socket;
		this.jeu = jeu;
		jeu.addObserver(this);
	}
	
	/**
	 * ********************************************************
	 * Constructeur parametrique 
	 * 
	 * @param socket et socket2 la connexion vers le client
	 * @param jeu la conversation
	 */
	public ConnexionClient(Socket socket, Socket socket2, JeuPendu jeu){
		this.socket = socket;
		this.socket2 = socket2;
		this.jeu = jeu;
		jeu.addObserver(this);
	}

	/**
	 * ********************************************************
	 * Mise a jour de la conversation
	 */
	public void update(Observable arg0, Object arg1) {
		PrintWriter out, out2;
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
			BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	
			String nom = in.readLine();
			//********************************************************
			while (nom.equals("?") || nom.equals("QUIT")){
				out.println("S'il vous plait choisissez un nom unique et différent de ? et QUIT.");
				out.flush();
				nom = in.readLine();
			} 
			if(jeu.getClients().size() == 2) {
				/*BufferedReader in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				nom = in.readLine();
				in2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
				nom = in2.readLine();*/
			}

			//*******************************************************
			out.println("Entrer QUIT pour quitter");
			out.flush();
			out.println("Bienvenue sur le réseau de Diana et Patricia");
			out.flush();
			out.println("Voulez vous jouer contre un joueur ou l'ordinateur? [j/o]");
			out.flush();
			String msg = in.readLine();

			//*******************************************************
			if (msg.equals("o") || msg.equals("O")) {

				jeu.deleteObserver(this);  
				out.println("\nVous êtes le témoin");
				out.flush();

				motChoisi = UDPClient.envoyerMessage("MOT_CHOISI");
				System.out.println("MOT: " + motChoisi);

				liste = new ArrayList<String>(motChoisi.length());

				for(int i = 0; i < motChoisi.length(); i++) {
					liste.add(i, "__ ");
				}

				out.print("Mot choisi: ");
				for(int i = 0; i < liste.size(); i++) {
					out.print("__ ");
				}
				out.println();
				out.flush();

				for(int i = MAX_ESSAI; i >= nombre_essai && nombre_essai > 0 && nombre_tiret != 0; i--) {
					out.print("\nEntrez une lettre. Il vous reste " + nombre_essai + " essais : ");
					out.flush();
					essai = in.readLine().toUpperCase();
					jeu.ajouterMot(essai.toLowerCase());

					// Joueur rentre une lettre
					if(essai.length() == 1) {

						// Vérifier si le mot contien la lettre
						if(motChoisi.indexOf(essai)!=-1) { 
							//System.out.println("OUI");
							i+=1;
							for (int j = 0 ; j<motChoisi.length() ; j++) {
								if (motChoisi.charAt(j) == essai.charAt(0)) {
									liste.set(j, essai);
								} 
							}
						} else {
							//System.out.println("NON");
							nombre_essai -= 1;
						}
						out.println();
						out.print(jeu.getMots() + "   ");
						for(int a = 0; a < liste.size(); a++) {
							out.print(String.valueOf(liste.get(a)) + " ");
						}
					}

					// Joueur rentre un mot
					else {
						for (int p = 0; p < essai.length(); p++) {
							if (motChoisi.indexOf(essai.charAt(p))!=-1){ 
								i+=1;
								for (int j = 0 ; j<motChoisi.length() ; j++) {
									if (motChoisi.charAt(j) == essai.charAt(p)) {
										liste.set(j, String.valueOf(essai.charAt(p)));
									} 
								}
							} else {
								nombre_essai -= 1;
							}
						}
						out.println();
						out.print(jeu.getMots() + "   ");
						for(int a = 0; a < liste.size(); a++) {
							out.print(String.valueOf(liste.get(a)) + " ");
						}
					}

					nombre_tiret = liste.size();
					for(int j = 0; j < liste.size(); j++) {

						if(!(liste.get(j)).equals("__ ")) {
							nombre_tiret--;
						}
						if(nombre_tiret == 0) {
							i = -1;
						}
					}
				}

				// Verifier le gagnant
				if(nombre_tiret > 0) {
					out.println("\nL'ordinateur a gagné! \n");
					out.flush();
				} else {
					out.println("\nVous avez gagné! \n");
					out.println();
					out.flush();
				}

			}
			//*******************************************************
			//si le client demande de jouer contre une autre client
			else {
				out.println("En attente d'un autre joueur...");
				out.flush();
				
				nom = in2.readLine();
				
				while(!msg.equals("QUIT")){
					if (msg.equals("?")){
						java.util.Date date = new java.util.Date();
						System.out.println(msg + " : " + date.toString());
					}
					//si le cliet ne veut pas quitter, il rentre dans le chat
					else{
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
							
							String bourreau = joueurs.get(0);
							jeu.parler(new Message(bourreau + " est le bourreau", " Entrez une mot pour l'autre joueur déviner"));
							msg = in.readLine();
							//dès que le bourreau est choisi, il doit choisir un mot pour l'aure joueur déviner
							while (bourreau.length() >= 1) {
								//jeu.parler(new Message(pendu + " est le bourreau", " Entrez une mot pour l'autre joueur déviner"));
								msg = in.readLine();
								System.out.println(msg);
								//msg = in2.readLine();
								System.out.println(msg);
								msg = msg.toUpperCase();
								String motValide =  "TRES"; //UDPClient.envoyerMessage(msg);
								
								//valide le mot entree avec le serveur
								while(!motValide.equals(msg)) {
									jeu.parler(new Message("Mot invalide" , "Entrez un nouveau mot"));
									msg = in.readLine().toUpperCase();
									motValide = "TRES";//UDPClient.envoyerMessage(msg);
								}
								
								motPendu = msg; 
								pendu = new String(new char[motPendu.length()]).replace("\0", "_");
								taillePendu = motPendu.length();
								jeu.parler(new Message("Mot valide! "+ motPendu.length(), "lettres" ));
								msg = in.readLine();
								msg = msg.toUpperCase();
								System.out.println("msg 1 = " + msg);
								//msg = in2.readLine();
								//System.out.println("msg 2 = "+ msg);

								//*******************************************************
								//valide les entrées
								for(int i=0; i<maxFauxFois; i++) {
									String motAffiche = "";
									//transforme le mot affiché en spaces vides = "_"
									while(maxFauxFois != 0) {
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
									//msg = in.readLine();
									//msg = in2.readLine();
									//msg = msg.toUpperCase();
									//jeu.parler(new Message(nom + " entrée", msg));
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

