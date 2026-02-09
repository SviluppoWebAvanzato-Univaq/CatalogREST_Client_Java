package org.univaq.swa.catalogrest.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class CatalogREST_Client {

    private static final String baseURI = "http://localhost:8080/CatalogREST/rest";

    //una entry di esempio, già serializzata in JSON (come farebbe Google Gson, per esempio)  
    private static final String dummy_json_entry = "{\n"
            + "  \"codice\" : \"P-123\",\n"
            + "  \"nome\" : \"Prodotto P-123\",\n"
            + "  \"prezzo\" : 0.84,\n"
            + "  \"disponibile\" : true,\n"
            + "  \"descrizioneBreve\" : \"Questo è il prodotto P-123\",\n"
            + "  \"dettagli\" : {\n"
            + "    \"descrizioneLunga\" : null,\n"
            + "    \"immagine\" : null,\n"
            + "    \"ingredienti\" : [ ],\n"
            + "    \"allergeni\" : [ ],\n"
            + "    \"valoriNutrizionali\" : {\n"
            + "      \"kcal\" : 10.0,\n"
            + "      \"grassi\" : null,\n"
            + "      \"carboidrati\" : null,\n"
            + "      \"proteine\" : null\n"
            + "    },\n"
            + "    \"modalitaConservazione\" : \"Pronto al consumo\",\n"
            + "    \"modalitaPreparazione\" : null,\n"
            + "    \"origine\" : null,\n"
            + "    \"etichette\" : [ \"A\", \"B\" ]\n"
            + "  }\n"
            + "}";
    //la struttura usata per passere le credenziali all'endpoint login2
    private static final String dummy_json_credentials = "{ \"username\" : \"pippo\", \"password\" : \"pippopass\" }";

    //usiamo Apache Httpclient perchè molto più intuitivo della classi Java.net...
    CloseableHttpClient client = HttpClients.createDefault();

    private void logRequest(ClassicHttpRequest request) {
        try {
            System.out.println("* Metodo: " + request.getMethod());
            System.out.println("* URL: " + request.getRequestUri());
            if (request.getFirstHeader("Accept") != null) {
                System.out.println("* " + request.getFirstHeader("Accept"));
            }
            System.out.println("* Headers: ");
            Header[] request_headers = request.getHeaders();
            for (Header header : request_headers) {
                System.out.println("** " + header.getName() + " = " + header.getValue());
            }
            switch (request.getMethod()) {
                case "POST": {
                    HttpEntity e = ((HttpPost) request).getEntity();
                    System.out.print("* Payload: ");
                    e.writeTo(System.out);
                    System.out.println();
                    System.out.println("* Tipo payload: " + e.getContentType());
                    break;
                }
                case "PUT": {
                    HttpEntity e = ((HttpPut) request).getEntity();
                    System.out.print("* Payload: ");
                    e.writeTo(System.out);
                    System.out.println();
                    System.out.println("* Tipo payload: " + e.getContentType());
                    break;
                }
                case "PATCH": {
                    HttpEntity e = ((HttpPatch) request).getEntity();
                    System.out.print("* Payload: ");
                    e.writeTo(System.out);
                    System.out.println();
                    System.out.println("* Tipo payload: " + e.getContentType());
                    break;
                }
                default:
                    break;
            }
        } catch (IOException ex) {
            System.out.println("Cannot dump request: " + ex.getMessage());
        }
    }

    private void logResponse(ClassicHttpResponse response) {
        System.out.println("* Headers: ");
        Header[] response_headers = response.getHeaders();
        for (Header header : response_headers) {
            System.out.println("** " + header.getName() + " = " + header.getValue());
        }
        System.out.println("* Return status: " + response.getReasonPhrase() + " (" + response.getCode() + ")");
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                entity.writeTo(System.out);
                System.out.println();
            } catch (IOException ex) {
                System.out.println("Cannot dump response: " + ex.getMessage());
            }
        }
    }

    private void executeAndDump(String description, ClassicHttpRequest request) {

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(description);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("REQUEST: ");
        logRequest(request);
        try {
            client.execute(request, response -> {
                //preleviamo il contenuto della risposta
                System.out.println("RESPONSE: ");
                logResponse(response);
                return null;
            });
        } catch (IOException ex) {
            System.out.println("Cannot execute request: " + ex.getMessage());
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();

    }

    public void doTests() throws IOException {

        HttpGet get_request = new HttpGet(baseURI + "/sections/frutta");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Sezione singola", get_request);

        get_request = new HttpGet(baseURI + "/sections/frutta/products");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Prodotti in sezione", get_request);

        get_request = new HttpGet(baseURI + "/sections/frutta/products/count");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Numero prodotti in sezione", get_request);

        get_request = new HttpGet(baseURI + "/products?tag=bio,vegan");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Ricerca prodotti per tag", get_request);

        get_request = new HttpGet(baseURI + "/products/P-123");
        get_request.setHeader("Accept", "application/json");
        executeAndDump("Prodotto singolo", get_request);

        HttpPost post_request = new HttpPost(baseURI + "/auth/login");
        post_request.setEntity(new StringEntity(dummy_json_credentials, ContentType.APPLICATION_JSON));
        executeAndDump("Login (con oggetto credentials)", post_request);

        //ripetiamo la request per catturare il token...
        Header ah = client.execute(post_request, response -> {
            return response.getFirstHeader("Authorization");
        });

        post_request = new HttpPost(baseURI + "/auth/login2");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", "pippo"));
        params.add(new BasicNameValuePair("password", "pippopass"));
        post_request.setEntity(new UrlEncodedFormEntity(params));
        executeAndDump("Login (con form parameters)", post_request);

        post_request = new HttpPost(baseURI + "/products");
        //per una richiesta POST, prepariamo anche il payload specificandone il tipo
        HttpEntity payload = new StringEntity(dummy_json_entry, ContentType.APPLICATION_JSON);
        //e lo inseriamo nella richiesta
        post_request.setEntity(payload);
        post_request.setHeader("Authorization", ah.getValue());
        executeAndDump("Creazione prodotto", post_request);

        HttpPut put_request = new HttpPut(baseURI + "/products/P-123");
        //per una richiesta PUT, prepariamo anche il payload specificandone il tipo
        payload = new StringEntity(dummy_json_entry, ContentType.APPLICATION_JSON);
        //e lo inseriamo nella richiesta
        put_request.setEntity(payload);
        put_request.setHeader("Authorization", ah.getValue());
        executeAndDump("Aggiornamento prodotto", put_request);

        HttpDelete delete_request = new HttpDelete(baseURI + "/products/P-123");
        delete_request.setHeader("Authorization", ah.getValue());
        executeAndDump("Eliminazione prodotto", delete_request);

        //proviamo senza autenticazione...
        delete_request.removeHeaders("Authorization");
        executeAndDump("Eliminazione prodotto (senza autorizzazione)", delete_request);

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        CatalogREST_Client instance = new CatalogREST_Client();
        instance.doTests();
    }
}
