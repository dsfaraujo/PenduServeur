package serveur;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Classe utilitaire permettant de faire appel a un serveur UDP
 *
 * @author rebecca
 *
 */
public final class UDPClient{
	public static final String IP = "192.168.9.8";
	public static final int PORT = 6112;
	
	/**
	 * Envoie une chaine de caractere au serveur UDP et 
     * retourne la reponse
     *
	 * @param s Le message a envoyer au serveur
	 * @return La reponse du serveur
	 */
	public static String envoyerMessage(String message)
	{
	     String reponse = "";	
		 try 
		 {
			 DatagramSocket clientSocket = new DatagramSocket();
			 InetAddress IPAddress = InetAddress.getByName(IP);
			 byte[] sendData = new byte[1024];
			 byte[] receiveData = new byte[1024];
			 
			 sendData = message.getBytes();
			 DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
			 clientSocket.send(sendPacket);
			 
			 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			 clientSocket.receive(receivePacket);
			 reponse = new String(receivePacket.getData());
			 
			 clientSocket.close();
		 } 
		 catch (IOException e) 
		 {
			 e.printStackTrace();
		 }
		 return reponse.trim();
	}
}
