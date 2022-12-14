package com.lemnisk.springdatabase.kafka;

import com.lemnisk.springdatabase.entity.Customer;
import com.lemnisk.springdatabase.repository.ICustomerRepo;
import org.apache.http.HttpEntity;
import org.apache.  http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;


@Service
public class kafkaConsumerDownstream {

    @Autowired
    ICustomerRepo iCustomerRepo;

    private static final Logger LOGGER =  Logger.getLogger(kafkaConsumerDownstream.class);

    public JSONObject getPayLoad(JSONObject newUser)
    {   String id = newUser.get("engID").toString();
        long id1 = Long.parseLong(id);
        Optional<Customer> customer = iCustomerRepo.findById(id1);
        JSONObject from = new JSONObject();
        String frmName = customer.get().getFromName();
        String frmEmail = customer.get().getFromEmail();

        from.put("fromName", frmName);
        from.put("fromEmail", frmEmail);
        JSONObject obj = new JSONObject();
        obj.put("from", from);
        obj.put("subject", newUser.get("subject_template") );
        obj.put("body", newUser.get("body_template"));
        obj.put("to", newUser.get("email"));


        return obj;
    }


    @KafkaListener(topics = "bootcamp-project-1", groupId = "myGroup")
    public void consume(JSONObject user) throws IOException, ClientProtocolException {
        LOGGER.info(String.format("Json message received -> %s",user.toString()));
        JSONObject payload = getPayLoad(user);

        HttpPost httpPost = new HttpPost("https://fbfe2e1b-c729-451a-a4c5-bf781f2bf5f2.mock.pstmn.io/sendmail");
        HttpEntity httpEntity = new StringEntity(JSONObject.toJSONString(payload), "utf-8");
        httpPost.setEntity(httpEntity);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
           // String rr = EntityUtils.toString(response.getEntity());
            //System.out.println(EntityUtils.toString(response.getEntity()));
            System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            String id = user.get("engID").toString();
            long id1 = Long.parseLong(id);
            iCustomerRepo.update(id1);
            LOGGER.info(EntityUtils.toString(response.getEntity()));
        }
        catch(Exception e)
        {   LOGGER.info("failed");
            System.out.println(e);
        }


    }


}
