package com.example.service.notification;

import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class SmppNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(SmppNotificationService.class);
    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddress;

    public SmppNotificationService() {
        try {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("sms.properties"));
            this.host = props.getProperty("smpp.host");
            this.port = Integer.parseInt(props.getProperty("smpp.port"));
            this.systemId = props.getProperty("smpp.system_id");
            this.password = props.getProperty("smpp.password");
            this.systemType = props.getProperty("smpp.system_type");
            this.sourceAddress = props.getProperty("smpp.source_addr");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SMS configuration", e);
        }
    }

    public void sendCode(String destination, String code) {
        SMPPSession session = new SMPPSession();
        try {
            BindParameter bindParameter = new BindParameter(
                    BindType.BIND_TX, systemId, password, systemType,
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, sourceAddress
            );
            session.connectAndBind(host, port, bindParameter);
            session.submitShortMessage(
                    systemType, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, sourceAddress,
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, destination,
                    new ESMClass(), (byte) 0, (byte) 1, null, null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte) 0,
                    new GeneralDataCoding(Alphabet.ALPHA_DEFAULT), (byte) 0,
                    ("Your OTP code: " + code).getBytes(StandardCharsets.UTF_8)
            );
            logger.info("SMS with OTP sent to {}", destination);
        } catch (Exception e) {
            logger.error("Error sending SMS", e);
        } finally {
            session.unbindAndClose();
        }
    }
}
