package serveur;

/**
 * ********************************************************
 * Classe Message
 * 
 * Encapsule un message envoye par un client sur une conversation.
 * Un message est constitue de texte et d'un pseudo
 * 
 * La classe Message est immuable (Une fois creee, elle ne peut
 * plus etre modifiee. Elle est donc sure pour l'acces multi-thread)
 * 
 * @author rebecca
 *
 */
public final class Message 
{
	private final String message;
	private final String pseudo;
	
	/**
	 * ****************************
	 * Constructeur parametrique
	 * 
	 * @param pseudo
	 * @param message
	 */
	public Message(String pseudo, String message)
	{
		this.message = message;
		this.pseudo = pseudo;
	}

	/**
	 * ********************************
	 * Retourne le message sous forme :
	 * "Nom de l'envoyeur : message"
	 */
	public String toString()
	{
		return pseudo + " : " + message;
	}
}
