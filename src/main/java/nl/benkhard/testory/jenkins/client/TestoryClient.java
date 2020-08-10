package nl.benkhard.testory.jenkins.client;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

public class TestoryClient implements Serializable {

    private TestoryClient() {}

    public static void uploadResults(String url, int applicationId, File[] files) {
        CloseableHttpClient client = HttpClients.createDefault();
        final String PATH = "/applications/%d/runs";

        try {
            String url = new URIBuilder(url)
                    .setPath(String.format(PATH, applicationId))
                    .build();
            System.out.println(String.format("Posting to url %s", uriBuilder.build().toString()));
            HttpPost post = new HttpPost(uriBuilder.build());
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

            for(File file : files) {
                entityBuilder.addPart("files", new FileBody(file));
            }

            post.setEntity(entityBuilder.build());
            CloseableHttpResponse response = client.execute(post);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
