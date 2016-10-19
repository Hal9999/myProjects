package redcat.client;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Classe che autentica Client, secondo nome di login e password.
 *
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ClientAuthenticator
{
    private final Map<String, String> accounts;
    
    /**
     * Crea un ClientAuthenticator inizializzando l'elenco degli account
     * autorizzati con la mappa passata per argomento
     * @param accounts la mappa degli account, con chiave il login e valore la password associata
     */
    public ClientAuthenticator(Map<String, String> accounts)
    {
        this.accounts = accounts;
    }
    
    /**
     * Crea un ClientAuthenticator con la propria mappa degli account autorizzati
     * letta da un file XML
     * @param xmlFile il file XML da leggere per inizializzare la mappa degli accessi consentiti
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public ClientAuthenticator(File xmlFile) throws ParserConfigurationException, SAXException, IOException
    {
        this( new HashMap<String, String>() );
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();
        
        try
        {
            NodeList nodeList = document.getElementsByTagName("user");

            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Element element = (Element) nodeList.item(i);
                this.accounts.put( getTagValue("login", element), getTagValue("password", element));
            }
        }
        catch(Exception ex)
        {
            throw new SAXException("Malformed XML" + ex);
        }
    }
    
    /**
     * Stabilisce se il Client passato è autorizzato.
     * @param client il Client di cui verificare l'autorizzazione
     * @return {@code true} se il Client è autorizzato, {@code false} altrimenti
     */
    public boolean isLegitUser(Client client)
    {
        if( client.getLogin() == null || client.getPassword() == null)
            return false;
        
        String password = accounts.get(client.getLogin());
        
        if( password != null && password.equals(client.getPassword()) ) return true;
        else return false;
    }
    
    private String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if( nValue != null ) return nValue.getNodeValue();
        else return "";
    }
}