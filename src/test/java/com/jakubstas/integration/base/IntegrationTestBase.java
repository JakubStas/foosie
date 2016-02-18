package com.jakubstas.integration.base;

import com.jakubstas.foosie.FoosieApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({FoosieApplication.class, IntegrationTestsConfiguration.class})
@WebIntegrationTest({"server.port=8081"})
public abstract class IntegrationTestBase {

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public final String getProposedTime() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        return sdf.format(cal.getTime());
    }
}
