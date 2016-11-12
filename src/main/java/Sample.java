import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Sample
{
    public static void main(String[] args)
    {
        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://api.wmata.com/TrainPositions/TrainPositions?contentType=json");


            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("api_key", "3a3ad8ee4030458387a9239bf0386f30");

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {

                System.out.println(EntityUtils.toString(entity));

            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
