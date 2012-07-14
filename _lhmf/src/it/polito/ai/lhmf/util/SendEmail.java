package it.polito.ai.lhmf.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
//import javax.activation.*;

public class SendEmail
{
	
   public static void send(String mailTo, String subject, String body)
   {
      
      // Recipient's email ID needs to be mentioned.
      String to = mailTo;

      // Sender's email ID needs to be mentioned
      String from = "jeyaxs@gmail.com";

      // Assuming you are sending email from localhost
      String host = "smtp.gmail.com";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);
      properties.setProperty("mail.user", "jeyaxs@hotmail.com");
      properties.setProperty("mail.password", "-secure-");
      
      properties.setProperty("mail.smtp.auth", "true");
      properties.setProperty("mail.smtp.socketFactory.port", "465");
      properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      properties.setProperty("mail.smtp.socketFactory.fallback", "false");

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(to));

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
   
   public String getBodyForAuth(String firstname, String lastname, String regCode) 
   {
 	  String link = "http://localhost:8080/_lhmf/authMail?regCode=" + regCode;
 	  String body = "Gentile " + firstname + " " + lastname + ",\n\n"
 					+ "Clicca il link in basso per verificare il tuo indirizzo di posta elettronica associato all'account GasProject.net:\n" 
 					+ link + " \n\n"
 					+ "Verificare il tuo indirizzo di posta elettronica ti permetterà di accedere a tutti i servizi del tuo account GasProject.net\n"
 					+ "Se non hai effettuato nessuna registrazione presso GasProject.net ti preghiamo di ignorare questa mail.";
 	  return body;
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
}