package it.unict.eve;

import java.io.*;
import java.util.*;

import edu.cornell.med.icb.clustering.*;


/**
 * Classe rappresentante un esperimento nell'ambito dello studio delle reti di regolazione genica.
 * Un esperimento è composto da una serie di Profili di espressione genica derivanti da misurazioni dell'espressione
 * di diversi geni tramite varie tecniche (es. microarray): ogni {@link Profile Profilo} rappresenta
 * l'andamento dell'espressione di un dato gene nel tempo
 */
public class Experiment
{
	/**
	 * Enumerazione di Errori massimi ammessi durante il computo dell'uguaglianza tra due double.
	 * Se si vuole impostare un errore diverso da uno di quelli di questa enumerazione,
	 * è possibile usare {@link Profile#setAdmittableError(double) setAdmittableError(double)}
	 * @author Stefano
	 */
	public static enum DoubleEqualityErrorAdmissibility{ NONE, THOUSANDTH, MILLIONTH, BILLIONTH };
	
	private Map<String, Profile> data;
	private String name;
	private String[] order;

	private double admittableError = 0.000001; //errore di default
	private Matrix.DistanceType cluDistType = Matrix.DistanceType.TotalError; //distanza di default
	private final Profile[] dataArray; //necessario per il clustering
	
	private final SimilarityDistanceCalculator distanceCalculator = new MaxLinkageDistanceCalculator()
	{
                @Override
		public double distance(final int i, final int j)
		{
            //return Math.abs(words[i].length() - words[j].length());
			//System.out.println(dataArray[instanceIndex].getName() + " vs " + dataArray[otherInstanceIndex].getName() + " d=" + dist);
            return Math.abs( dataArray[i].distance(dataArray[j]) );
        }
	};
	
	/**
	 * Costruisce un Experiment da un array di Profile.
	 * @param name nome dell'Experiment
	 * @param array array di Profile che rappresentano i dati dell'esperimento
	 * @throws NullPointerException se name o set sono null
	 * @throws IllegalArgumentException se i nomi dei profili non sono univoci (ce ne sono almeno due con lo stesso nome)
	 */
	public Experiment(String name, Profile[] array)
	{
		if(name==null) throw new NullPointerException("name points to null");
		if(array==null) throw new NullPointerException("array points to null");
		if( !Profile.areCompatible( array ) )
			throw new IllegalArgumentException("Profiles inside Experiment are not compatible");
		
		this.name = name;
		this.data = new HashMap<String, Profile>();
			for(Profile p : array)
				if( data.put(p.getName(), p) != null ) throw new IllegalArgumentException("profile names are not univocal");
		
		this.setAdmittableError(Experiment.DoubleEqualityErrorAdmissibility.MILLIONTH); //default
		this.setCluDistType(Matrix.DistanceType.TotalError); //default
		dataArray = data.values().toArray(new Profile[0]);
		
		order = new String[this.data.size()];
		int i=0;
		for(Profile p : this.data.values()) order[i++] = p.getName();
	}

	/**
	 * Costruisce un Experiment da un {@link java.util.Set set} di Profile.
	 * @param name nome dell'Experiment
	 * @param set set di Profile che rappresentano i dati dell'esperimento
	 * @throws NullPointerException se name o set sono null
	 * @throws IllegalArgumentException se i nomi dei Profile non sono univoci (ce ne sono almeno due con lo stesso nome)
	 */
	public Experiment(String name, Set<Profile> set )
	{
		this(name, set.toArray(new Profile[0]));
	}
	
	@Override
	public String toString()
	{
		double Ts = data.values().iterator().next().getSamplingTime();
		String txt = "Experiment \"" + name.toString() + "\" contains " + this.data.size() + " Profile(s), Ts = " + Ts;
		for(Profile p : this.data.values())
			txt += "\n\t" + p.getName() + "\t" + p.getSamplesNumber() + "\t" + p.dataToString();
		return txt;
	}

	public double getSamplingTime()
	{
		return this.data.entrySet().iterator().next().getValue().getSamplingTime();
	}
	
	/**
	 * Restituisce il numero di Profile prensenti all'interno dell'Experiment.
	 * @return il nome dell'Experiment
	 */
	public int getSize() { return this.data.size(); }
        
        public int getSamplesNumber()
        {
            for( Profile p : this.data.values() )
                return p.getSamplesNumber();
            throw new IllegalStateException("this experiment doesn't contain Profiles yet");
        }

	/**
	 * Restituisce il nome dell'Experiment.
	 * @return il nome dell'Experiment
	 */
	public String getName() { return name; }
	
	/**
	 * Imposta il nome dell'Experiment.
	 * @param newname nuovo nome di questo Experiment
	 * @return 
	 * @throws NullPointerException se name o set sono null
	 * @return questo Experiment
	 */
	public Experiment setName(String newname)
	{
		if( newname==null ) throw new NullPointerException("newname is null");
		this.name = newname;
		
		return this;
	}
	
	/**
	 * Restituisce l'errore massimo ammesso da questo oggetto nel considerare due double uguali.
	 * @return l'errore massimo ammesso
	 */
	public double getAdmittableError() { return admittableError; }

	/**
	 * Imposta l'errore massimo ammesso nel confronto tra due double.
	 * Questo metodo fa uso di {@link Experiment.DoubleEqualityErrorAdmissibility DoubleEqualityErrorAdmissibility}
	 * per specificare l'errore.
	 * L'errore di default è di un milionesimo.
	 * @param admittableError l'errore massimo ammissibile (dipendente dall'applicazione)
	 * @return questo Experiment
	 */
	public Experiment setAdmittableError(DoubleEqualityErrorAdmissibility admittableError)
	{
		switch(admittableError)
		{
			case NONE: 		 { this.admittableError = 0; } break;
			case THOUSANDTH: { this.admittableError = 0.001; } break;
			case MILLIONTH:  { this.admittableError = 0.000001; } break;
			case BILLIONTH:  { this.admittableError = 0.000000001; } break;
		}
		for(Profile p : data.values()) p.setAdmittableError(this.admittableError); //imposto l'errore per tutti i Profile dell'Experiment
		return this;
	}

	/**
	 * Imposta l'errore massimo ammesso nel confronto tra due double.
	 * L'errore di default è di un milionesimo.
	 * @param admittableError l'errore massimo ammissibile (dipendente dall'applicazione)
	 * @return questo Experiment
	 */
	public Experiment setAdmittableError(double admittableError)
	{
		this.admittableError = Math.abs(admittableError);
		for(Profile p : data.values()) p.setAdmittableError(this.admittableError); //imposto l'errore per tutti i Profile dell'Experiment
		return this;
	}

	/**
	 * Restituisce il tipo di distanza tra Profile di questo Experiment.
	 * @return il tipo di distanza
	 */
	public Matrix.DistanceType getCluDistType() { return this.cluDistType; }

	/**
	 * Imposta per tutti i Profile di questo Experiment il tipo di distanza da calcolare.
	 * In particolare questa impostazione ha effetto sul clustering.
	 * @param cluDistType the cluDistType to set
	 */
	public void setCluDistType(Matrix.DistanceType cluDistType)
	{
		this.cluDistType = cluDistType;
		for(Profile p: this.data.values()) p.setDistanceType(cluDistType);
	}

	/**
	 * Controlla se questo Experiment contiene un certo Profile.
	 * Il controllo è profondo, ovvero fatto campione per campione, ma ammette una soglia di errore
	 * massimo per ingentilire il controllo bit a bit che verrebbe altrimenti fatto sui double.
	 * L'errore ammissibile è quello di questo Experiment.
	 * @param profile Profile di cui controlla l'esistenza in questo Experiment
	 * @return true se questo Experiment contiene profile al suo interno, false altrimenti
	 */
	public boolean contains(Profile profile)
	{
		for(Profile p : data.values()) if( p.equals(profile) ) return true;
		
		return false;
	}

	/**
	 * Controlla che questo Experiment contenga tutti i Profile di un altro Experiment.
	 * Il controllo non fa supposizioni sull'ordine dei Profile all'interno dell'Experiment
	 * (e comunque non potrebbe), il criterio di uguaglianza tra Profile ammette una soglia di
	 * errore massimo per ingentilire il controllo bit a bit che verrebbe altrimenti fatto sui double.
	 * L'errore ammissibile è il minimo tra questo Experiment e l'altro.
	 * Il nome dei due Experiment è ignorato.
	 * @param other altro Experiment con cui eseguire il confronto
	 * @return true se i due Experiment contengono gli stessi Profile, false altrimenti
	 */
	public boolean equals(Experiment other)
	{
		for(Profile p : this.data.values()) if( !other.contains(p) ) return false;
		for(Profile p : other.data.values()) if( !this.contains(p) ) return false;
		return true;
	}

	/**
	 * Seleziona i Profile di questo Experiment in base alla varianza dei campioni.
	 * Crea un nuovo Experiment contenente solo i {@linkplain Profile Profile} che hanno varianza dei campioni oltre una certa
	 * soglia. Questo metodo serve essenzialmente ad eliminare dall'esperimento quei profili che hanno un andamento dei dati
	 * per lo più "piatto" e che quindi non apportano informazioni relativamente allo studio della rete regolatrice fra geni.
	 * Lo scopo di questo metodo è quindi ridurre la dimensione dei dati cosicchè le elaborazioni successive siano più snelle.
	 * @param minVariance varianza minima dei campioni che il Profile dell'Experiment deve possedere affinchè possa far parte
	 * del nuovo Experiment
	 * @return un nuovo oggetto Experiment da this epurato dai profili "piatti"
	 */
	public Experiment selectByMinVariance(double minVariance)
	{
		Set<Profile> newSet = new HashSet<Profile>();		
			for(Profile p : this.data.values())
				if( p.variance() >= minVariance ) newSet.add(p);
		
		return new Experiment(this.name, newSet);
	}
	
	/**
	 * Seleziona i Profile di questo Experiment in base alla varianza dei campioni.
	 * Crea un nuovo Experiment contenente solo i {@linkplain Profile Profile} che hanno varianza dei campioni minore di una certa
	 * soglia. (Usato anche da JUnit per verificare la complementarità dei risultati
	 * di {@link #selectByMinVariance(double) selectByMinVariance})
	 * @param maxVariance varianza massima dei campioni che il Profile dell'Experiment deve possedere affinchè possa far parte
	 * del nuovo Experiment
	 * @return un nuovo oggetto Experiment
	 */
	public Experiment selectByMaxVariance(double maxVariance)
	{
		Set<Profile> newSet = new HashSet<Profile>();
			for(Profile p : this.data.values())
				if( p.variance() < maxVariance ) newSet.add(p);
		
		return new Experiment(this.name, newSet);
	}
	
	/**
	 * Normalizza i campioni dei Profile contenuti.
	 * Crea un nuovo Experiment con gli stessi {@linkplain Profile Profile} di questo, ma con i campioni
	 * normalizzati nel range [-1, +1].
	 * @return un nuovo oggetto Experiment con i campioni dei Profili normalizzati
	 */
	public Experiment normalize()
	{
		Set<Profile> newSet = new HashSet<Profile>();
			for(Profile p : this.data.values()) newSet.add(p.normalize());
		
		return new Experiment(this.name, newSet);
	}

	/**
	 * Opera clustering dei Profile tramite algoritmo QTClustering.
	 * Il clustering dei Profili è effettuato tramite l'algoritmo QTClustering, il quale restituisce
	 * gruppi di Profili secondo un certo modo di calcolare la distanza tra un Profile e l'altro
	 * e secondo un certo raggio col quale l'algoritmo decide quali e quanti cluster creare.
	 * A differenza di altri algoritmi, come K-Means, l'algoritmo QTClustering non richiede
	 * che il numero di clusters sia noto a priori.
	 * Questo Experiment rimane immutato e quello che viene tornato dal metodo è un array di Profili,
	 * tali per cui ad ognuno corrisponde un cluster tale per cui i Profile all'interno dello stesso
	 * cluster hanno distanza non superiore al parametro radius
	 * @param radius è la distaza massima tra Profili all'interno dello stesso cluster
	 * @return una {@linkplain List lista} di array di Profile, ognuno corrispondente ad un cluster
	 */
	public List<Profile[]> clusterizeToProfiles(float radius)
	{
		QTClusterer clusterer = new QTClusterer(this.data.size());
		List<int[]> clusters = clusterer.cluster(this.distanceCalculator, radius);
		List<Profile[]> clu = new LinkedList<Profile[]>();
		
		for(int[] cluster : clusters)
		{
			Profile[] tmp = new Profile[cluster.length];
			for(int i=0; i<cluster.length; i++) tmp[i] = dataArray[cluster[i]];
			clu.add(tmp);
		}
		
		return clu; //lista di array di profile; ogni array è un cluster
	}
	
	/**
	 * Opera clustering dei Profile contenuti in questo Experiment tramite algoritmo QTClustering.
	 * Il clustering dei Profili è effettuato tramite l'algoritmo QTClustering, il quale restituisce
	 * una lista di array di Profile secondo un certo modo di calcolare la distanza tra un Profile e
	 * l'altro e secondo un certo raggio col quale l'algoritmo decide quali e quanti cluster creare.
	 * A differenza di altri algoritmi, come il K-Means, l'algoritmo QTClustering non richiede infatti
	 * che il numero di clusters sia noto a priori.
	 * Tramite QTClustering questo Experiment genere un nuovo Experiment ove ogni Profile al suo
	 * interno è frutto della sintesi dei Profile appartenenti allo stesso cluster.
	 * @param radius è la distaza massima tra Profili all'interno dello stesso cluster
	 * @return un nuovo Experiment in cui ogni Profile corrisponde alla sintesi di un cluster
	 */
	public Experiment clusterize(float radius)
	{
		List<Profile[]> clusters = clusterizeToProfiles(radius); //lista di array di Profile
		
		Profile[] unifiedProfiles = new Profile[clusters.size()];
		
		for(int i=0; i<clusters.size(); i++)
			unifiedProfiles[i] = Profile.unify( clusters.get(i) ); //hanno già il nome giusto
		
		return new Experiment(name, unifiedProfiles);
	}
	
	/**
	 * Crea un nuovo Experiment per interpolazione.
	 * I campioni di ogni Profile del nuovo Experiment sono ricavati dai campioni dei Profile di
	 * questo Experiment tramite algoritmo SP-Line.
	 * Il numero di campioni viene portato, per interpolazone, a newNOfSamples.
	 * Date le peculiarità di questo algoritmo di interpolazione, il tempo totale di campionamento
	 * non è considerabile nc*Ts, perchè il campione non è considerato preso a metà di Ts, ma all'inizio
	 * di Ts; si potrebbe dire che il tempo totale di campionamento è (nC-1)*Ts.
	 * @param newNOfSamples il nuovo numero di campioni per i Profile dell'Experiment
	 * @return un nuovo Experiment con stesso nome, unità di misura del tempo,
	 * ma con numero di campioni e tempo di campionamento diversi, ovvero dall'interpolazione
	 */
	public Experiment interpolate(int newNOfSamples)
	{
		Set<Profile> interp = new HashSet<Profile>();
			for(Profile p : this.data.values())
				interp.add( p.interpolate(newNOfSamples) );
		
		return new Experiment( this.name, interp );
	}
	
	/**
	 * Restituisce l'array dei nomi dei Profile contenuti in questo Experiment.
	 * @return l'array dei nomi
	 */
	public String[] getProfilesNames()
	{
		return order;
	}

	/**
	 * Restituisci i Profile contenuti in questo Experiment.
	 * Nell'array ritornato i Profile hanno lo stesso ordine di quello specificato dai nomi
	 * dell'array di stringhe passato come parametro.
	 * @param order array di stringhe per specificare l'ordine dell'array ritornato
	 * @return array di Profile secondo l'ordine specificato da order
	 * @throws IllegalArgumentException se un nome nell'array non viene trovato nell'Experiment
	 */
	public Profile[] getProfiles()
	{
		Profile[] profiles = new Profile[order.length];
		
		for(int i=0; i<order.length; i++)
			if( null == (profiles[i] = this.data.get(order[i])) )
				throw new IllegalArgumentException("Profile name not found");
		
		return profiles;
	}
	
	public Profile getProfile(String name)
	{
		Profile profile = this.data.get(name);
		if( profile == null ) throw new IllegalArgumentException("Profile name not found");
		else return profile;
	}
	
	/**
	 * Restituisce un oggetto Matrix i cui elementi sono i campioni dei Profile contenuti in questo
	 * Experiment.
	 * L'ordine delle righe è quello richiesto sal parametro order.
	 * In effetti il comportamento è simile a {@link Experiment#getProfiles(String[]) getProfiles(String[])).
	 * @param order array di stringhe per specificare l'ordine delle righe della matrice ritornata
	 * @return array di Profile secondo l'ordine specificato da order
	 * @throws IllegalArgumentException se un nome nell'array non viene trovato nell'Experiment
	 */
	public Matrix toMatrix()
	{
		return new Matrix( Profile.to2DArray( this.getProfiles()) );
	}
	
	public void forceProfileOrder(String[] forcedOrder)
	{
		if( this.data.size() != forcedOrder.length ) throw new IllegalArgumentException("array lenght must be equal to size of this Experiment");
		
		this.order = forcedOrder;
	}
	
	public static String[] readNamesFromFile(File file) throws IOException
	{
		LinkedList<String> stringList = new LinkedList<String>();
		BufferedReader reader = new BufferedReader( new FileReader(file) );

		{
			String line;
			while( (line = reader.readLine()) != null )
				if( line.isEmpty() ) continue;
				else stringList.add( line );
			reader.close();
		}
		return stringList.toArray(new String[0]);
	}
	
	public static void writeNamesToFile(String[] ss, File file) throws IOException
	{
		BufferedWriter writer = new BufferedWriter( new FileWriter(file) );
		
		for(int i=0; i<ss.length-1; i++) {writer.write(ss[i] + ""); writer.newLine();}
		writer.write(ss[ss.length-1]);
		writer.close();
	}
}