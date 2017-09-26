package serveur;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * ********************************************************
 * Classe JeuPendu
 * 
 * Encapsule la liste des messages envoyes par des clients.
 * Elle est observee par tous les clients:
 * A chaque fois qu'un client envoie un message, tous sont
 * avertis.
 * 
 * @author Diana Soares, Patricia Shimizu
 *
 */
public class JeuPendu extends Observable
{

	List<Message> messages = new ArrayList<Message>();
	List<String> clients = new ArrayList<String>();
	List<String> mots = new ArrayList<String>();
	/**
	 * **************************************************
	 * Ajoute a la liste de messages le message et alerte
	 * les observateurs.
	 * 
	 * Sur pour les appels multithread. Un seul message peut etre
	 * ajoute a la fois et tous les observateurs sont notifies avant qu'un
	 * nouveau message puisse etre recu.
	 * 
	 * @param message
	 */
	synchronized void parler(Message message)
	{
		messages.add(message);
		setChanged();
		notifyObservers();
	}
	/**
	 * *********************************
	 * Accesseur sur le dernier message
	 * 
	 * @return le dernier message
	 */
	Message getLastMessage()
	{
		return messages.get(messages.size() - 1);
	}
	List<String> getMots()
	{
		return mots;
	}
	/**
	 * ********************************************************
	 * Accesseur pour la liste des noms des clients en ligne
	 * @return la liste des clients actifs
	 */
	List<String> getClients()
	{
		return clients;
	}
	/**
	 * ********************************************************
	 * Ajoute un nom de client à la liste si celle-ci ne le contient pas déjà
	 * @param client
	 * @return Vrai si le client a été ajouté, false sinon.
	 */
	synchronized boolean ajouterClient(String client)
	{
		if (clients.contains(client)) {
			return false;
		}
		else {
		}
		clients.add(client);
		parler(new Message(client, "vient de se connecter"));
		return true;
	}
	/**
	 * ********************************************************
	 * Retire un nom de la liste des clients. Cette méthode est appelée lorsqu'un
	 * client se déconnecte.
	 * @param client
	 */
	synchronized void retirerClient(String client)
	{
		parler(new Message(client, "vient de se déconnecter"));
		clients.remove(client);
	}
	public void ajouterMot(String mot) {
		mots.add(mot);
	}
}

