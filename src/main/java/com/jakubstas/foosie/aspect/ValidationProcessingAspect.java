package com.jakubstas.foosie.aspect;

import com.jakubstas.foosie.rest.PrivateReply;
import com.jakubstas.foosie.slack.SlackService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Aspect
@Component
public class ValidationProcessingAspect {

    private final Logger logger = LoggerFactory.getLogger(ValidationProcessingAspect.class);

    @Autowired
    private SlackService slackService;

    @Around("com.jakubstas.foosie.aspect.Pointcuts.inPublicMethodOfGameController(responseUrl)")
    public void sendAllValidationIssuesToUserInAPrivateMessage(final ProceedingJoinPoint pjp, final String responseUrl) {
        try {
            pjp.proceed();
        } catch (ConstraintViolationException exception) {
            logger.debug("GameService has just thrown ConstraintViolationException!");

            final String message = getMessageForUserFromConstraintViolationException(exception);

            logger.debug("Message for the user has been built");

            final PrivateReply statusReply = new PrivateReply(message);
            slackService.postPrivateReplyToMessage(responseUrl, statusReply);

            logger.info("The list of validation errors was returned to the user.");
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage());
        }
    }

    private String getMessageForUserFromConstraintViolationException(final ConstraintViolationException exception) {
        final StringBuffer stringBuffer = new StringBuffer();
        int i = 1;

        stringBuffer.append("Your command has failed validation! Please review following issues:\n\n");

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            final String violationMessage = String.format("%d. %s\n", i, violation.getMessage());
            stringBuffer.append(violationMessage);
            i++;
        }

        return stringBuffer.toString();
    }
}
