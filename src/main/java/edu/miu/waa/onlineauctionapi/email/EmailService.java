package edu.miu.waa.onlineauctionapi.email;

public interface EmailService {

  String sendSimpleMail(EmailDetails details);

  String sendMailWithAttachment(EmailDetails details);
}
