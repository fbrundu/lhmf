package it.polito.ai.lhmf.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail
{
	private static final String username = "gas.no.reply@gmail.com";
	private static final String password = "gasproject";
	
   public static void send(String mailTo, String subject, String body)
   {
      
      // Recipient's email ID needs to be mentioned.
      String to = mailTo;
      
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", "smtp.gmail.com");
      props.put("mail.smtp.port", "587");
      
      Session session = Session.getInstance(props, 
    		  new javax.mail.Authenticator() {
    	  			protected PasswordAuthentication getPasswordAuthentication() {
    	  				return new PasswordAuthentication(username, password);
    	  			}
    		  });

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(username));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)
                                  /*new InternetAddress(to)*/);

         // Set Subject: header field
         message.setSubject(subject);

         // Now set the actual message
         message.setText(body);

         // Send message
         Transport.send(message);
         //System.out.println("Sent message successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
   
   public static boolean isValidEmailAddress(String email) {
	   boolean result = true;
	   
	   try {
	      InternetAddress emailAddr = new InternetAddress(email);
	      emailAddr.validate();
	   } catch (AddressException ex) {
	      result = false;
	   }
	   return result;
   }

	public void sendAdminRegistration(	String contact, 
										String username, 
										String password, 
										String regCode, 
										Integer idMember, 
										String email) {
		
		String subject = "Conferma mail per GasProject.net";
		String id = idMember + ":1";
		
		String link = "http://localhost:8080/_lhmf/authMail?id=" + id + "&regCode=" + regCode;
		  
		
		String body = "Gentile " + contact + ",\n\n"
					+ "È stato creato un account in tuo nome. I dati di accesso sono i seguenti:\n" +
					"Username:" + username + "\n" +
					"Password:" + password + "\n\n" +
					"Cliccando il link in basso potrai attivare il tuo account su GasProject.net:\n"
					+ link + " \n\n"
					+ "Attivare il tuo account ti permetterà di accedere a tutti i servizi ddo GasProject.net\n"
					+ "Se non hai richiesto una registrazione o non vuoi utilizzare le funzionalità di GasProject.net \nti preghiamo di ignorare questa mail.\n\n" +
					"GasProject.it Staff";
	
		SendEmail.send(email, subject, body);
		
	}
	
	public void sendNormalRegistration(	String contact,  
										String regCode, 
										Integer idMember, 
										String email,
										boolean isSupplier) {
							
		String subject = "Conferma mail per GasProject.net";
		String id = idMember + ":0";
		
		String link = "http://localhost:8080/_lhmf/authMail?id=" + id + "&regCode=" + regCode;
		
		
		String body = "Gentile " + contact + ",\n\n" +
		"Hai eseguito la registrazione su GasProject.it:\n" +
		"Clicca il link in basso per verificare il tuo indirizzo di posta elettronica associato all'account GasProject.net:\n" 
		+ link + " \n\n"
		+ "Verificare il tuo indirizzo di posta elettronica ti permetterà di accedere a tutti i servizi del tuo account GasProject.net\n"
		+ "Se non hai effettuato nessuna registrazione presso GasProject.net ti preghiamo di ignorare questa mail.";
		
		SendEmail.send(email, subject, body);
		
	}

}