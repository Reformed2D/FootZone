package com.example.versionfinal.User;


import android.content.Context;
import android.os.AsyncTask;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtils {
    private static final String SENDER_EMAIL = "votre.email@gmail.com"; // Remplacez par votre email
    private static final String SENDER_PASSWORD = "votre_mot_de_passe_app"; // Utilisez un mot de passe d'application Google

    public static void sendVerificationEmail(Context context, String recipientEmail, String verificationToken) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");

                    Session session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                        }
                    });

                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(SENDER_EMAIL));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                    message.setSubject("Vérification de votre compte");

                    String verificationLink = "https://votreapp.com/verify?token=" + verificationToken + "&email=" + recipientEmail;
                    String htmlContent = String.format(
                            "<h2>Vérification de votre compte</h2>" +
                                    "<p>Cliquez sur le lien ci-dessous pour vérifier votre compte :</p>" +
                                    "<a href='%s'>Vérifier mon compte</a>" +
                                    "<p>Si vous n'avez pas créé de compte, ignorez cet email.</p>",
                            verificationLink
                    );

                    message.setContent(htmlContent, "text/html; charset=utf-8");
                    Transport.send(message);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                // Gérer le résultat de l'envoi
            }
        }.execute();
    }
}
