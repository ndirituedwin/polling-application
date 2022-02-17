package com.ndirituedwin.Service.mailservice;

import com.ndirituedwin.Entity.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {


    private final JavaMailSender javaMailSender;
    private  final MailContentBuilder mailContentBuilder;
  @Async
public void sendemailactivationlink(NotificationEmail notificationEmail){
      MimeMessagePreparator messagePreparator=mimeMessage -> {
          MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage);
          messageHelper.setFrom("ndiritu.edwin018@gmail.com");
          messageHelper.setTo(notificationEmail.getRecipient());
          messageHelper.setSubject(notificationEmail.getSubject());
          messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
      };
      try {
          javaMailSender.send(messagePreparator);
          log.info("Activation email sent {}",notificationEmail.getRecipient());
      }catch (MailException exception){
          log.error("an exception has occurred while sending mail to {} {}",notificationEmail.getRecipient(),exception.getMessage());
          throw new com.ndirituedwin.Exceptions.MailException("an exception has occurred whie trying to sen email to "+notificationEmail.getRecipient()+" \n "+ exception.getMessage());
      }
  }
}
