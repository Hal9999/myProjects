/**
 * Contiene le classi che compongono il componente Manager del sistema RedCat.
 * <p>Le classi fornite riguardano la gestione delle informazioni generate e ricevute dal
 * Sensor e il loro inoltro ai Client che ne hanno fatto richiesta.</p>
 * <p>Un Manager del sistema RedCat è quella componente del sistema che accetta le informazioni
 * speditegli da uno o più Sensor, accetta le connessioni da parte dei Client che richiedono
 * di essere notificati all'arrivo di una certa tipologia di informazioni e quindi gestisce e
 * inoltra opportunamente le informazioni relative al monitoraggio del server MySQL a cui
 * il Sensor è connesso.</p>
 * <p>Inoltre questo package fornisce le classi necessarie a generare Proxy per connettersi
 * ad un Manager da remoto.</p>
 */
package redcat.manager;