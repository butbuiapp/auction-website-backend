package edu.miu.waa.onlineauctionapi.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GmailEmailService implements EmailService {
  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String sender;

  @Override
  public String sendSimpleMail(EmailDetails details) {
    try {
      SimpleMailMessage mailMessage = new SimpleMailMessage();

      mailMessage.setFrom(sender);
      mailMessage.setTo(details.getRecipient());
      mailMessage.setText(details.getMsgBody());
      mailMessage.setSubject(details.getSubject());

      javaMailSender.send(mailMessage);
      return "Mail Sent Successfully...";
    } catch (Exception e) {
      return "Error while Sending Mail";
    }
  }

  @Override
  public String sendMailWithAttachment(EmailDetails details) {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper;
    try {
      mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
      mimeMessageHelper.setFrom(sender);
      mimeMessageHelper.setTo(details.getRecipient());
      mimeMessageHelper.setText(details.getMsgBody());
      mimeMessageHelper.setSubject(details.getSubject());

      //            ClassPathResource res = new ClassPathResource("classpath:logo.png"); // logo.png
      //            File file = new File(res.getPath());
      URL url = this.getClass().getClassLoader().getResource("logo.png");

      InputStream inputStream = (new ClassPathResource("logo.png")).getInputStream();
      File tempFile = File.createTempFile("logo", ".png");
      try {
        FileUtils.copyInputStreamToFile(inputStream, tempFile);
        mimeMessageHelper.addAttachment(tempFile.getName(), tempFile);
      } finally {
        IOUtils.closeQuietly(inputStream);
      }

      javaMailSender.send(mimeMessage);
      return "Mail sent Successfully";
    } catch (MessagingException e) {
      return "Error while sending mail!!!";
    } catch (IOException e) {
      return "Error while reading attachment!";
    }
  }
}
