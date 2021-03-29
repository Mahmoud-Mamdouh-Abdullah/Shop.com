package com.mahmoudkhalil.shopcom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.widget.Toast;

import com.mahmoudkhalil.shopcom.models.Order;
import com.mahmoudkhalil.shopcom.models.Product;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Logic {

    /**
     * checking the internet connection
     * @return false if there is no internet
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void sendEmail(Order order, String recipient, Context context) {
        String email = "houda10111997@gmail.com";
        String password = "";
        String mailBody = prepareMsg(order);
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(recipient)[0]);
            message.setSubject("Order Submission Details");
            message.setText(mailBody);
            Transport.send(message);
            Toast.makeText(context, "Email sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static String prepareMsg(Order order) {
        StringBuilder productMsg = new StringBuilder();
        List<Product> productList = order.getProductsList();
        for(int i = 0; i < productList.size(); i ++) {
            productMsg.append(productList.get(i).getProduct_title())
                    .append("\t").append("Qty : ").append(productList.get(i).getStock())
                    .append("\t").append("Price : ").append(productList.get(i).getPrice()).append("\n");
        }

        return "OrderID : " + order.getOrderID() + "\n"
                + "OrderDate : " + order.getOrderDate() + "\n"
                + "OrderLocation : " + order.getUserAddress() + "\n\n"
                + productMsg.toString() + "\n-------------------------\n"
                + "Total Price : " + order.getOrderTotal() + "\n\n\n"
                + "Thanks, \nShop.com Team.";
    }
}
