/**
 * Contiene le classi che compongono il componente Sensore del sistema RedCat.
 * <p>Un Sensor del sistema RedCat è quella componente del sistema che ci interfaccia
 * ad un server MySQL e ne controlla i parametri di funzionamento.</p>
 * <p>Da questa osservazione dei parametri di funzionamento, il Sensor genera ExtendedEvent,
 * che possono essere visualizzati, registrati e spediti via connessione remota ad un Manager
 * che provverà a ridistriuire tali informazioni ai client, secondo certi criteri.</p>
 */
package redcat.sensor;