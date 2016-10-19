package it.unict.eve;

import flanagan.interpolation.CubicSpline;

/**
 * Classe rappresentante profili di espressione genica.
 * Un Profile contiene i campionamenti fatti nel tempo ed è individuato da un nome (in genere il nome
 * del gene campionato).
 */
public class Profile
{
	/**
	 * Enumerazione di Errori massimi ammessi durante il computo dell'uguaglianza tra due double.
	 * Se si vuole impostare un errore diverso da uno di quelli di questa enumerazione,
	 * è possibile usare {@link Profile#setAdmittableError(double) setAdmittableError(double)}
	 * @author Stefano
	 */
	public static enum DoubleEqualityErrorAdmissibility{ NONE, THOUSANDTH, MILLIONTH, BILLIONTH };
	private double admittableError = 0.000001; //errore di default
	
	private Matrix.DistanceType distType = Matrix.DistanceType.TotalError; //distanza di default
	
	private Matrix data;
	
	private double samplingTime;
	private String name;
	private String timeUnit = null;
	
	/**
	 * Costruisce un Profilo da un array di campioni associato ad un tempo di campionamento.
	 * @param name nome del profilo genico (in genere è il nome del gene studiato da questo profilo
	 * di espressione genica)
	 * @param data array di campioni
	 * @param samplingTime tempo di campionamento (adimensionale: l'utente della classe deve
	 * preoccuparsi della omogeneità del tempo
	 * di campionamento qualora voglia fare interagire più oggetti Profilo con unità di misura di Ts non omogenei)
	 * @throws NullPointerException se data o name sono null
	 */
	public Profile(String name, double[] data, double samplingTime)
	{
		if(data == null) throw new NullPointerException("data is null");
		if(name == null) throw new NullPointerException("name is null");
		if(samplingTime <= 0) throw new IllegalArgumentException("samplingTime must be greater than zero");
		
		this.name = name;
		this.data = new Matrix(data, Matrix.MatrixType.RowMatrix);
			this.data.setAdmittableError(this.admittableError);
		this.samplingTime = samplingTime;
	}

	/**
	 * Stampa i campioni in una forma simpatica.
	 * @return la stringa con i campioni racchiusi da parentesi quadre
	 */
	public String dataToString()
	{
		return "[ " + this.data.toString() + " ]";
	}

	/**
	 * Stampa tutte le informazioni di questo Profile in una Stringa.
	 * @return un oggetto String
	 */
	@Override
	public String toString()
	{
		String txt = "Profilo \"" + name.toString() + "\"\t" + "N = " + data.nCols() + "\t" + "Ts = " +
					+ samplingTime + (timeUnit!=null ? (" " + timeUnit) : "") + "\t" + this.dataToString();
		return txt;
	}

	/**
	 * Imposta un nome alla misura del samplig time (es "h", "m", "s").
	 * In effetti è una caratteristica facoltativa di un profilo
	 * @param u unità di tempo in forma testuale
	 */
	public void setTimeUnit(String u)
	{
		this.timeUnit = u;
	}
	
	/**
	 * Restituisce il nome della misura del samplig time (es "h", "m", "s").
	 * @return il nome della misura del samplig time
	 */
	public String getTimeUnit()
	{
		return timeUnit;
	}
	
	/**
	 * Restituisce il nome del profilo.
	 * @return il nome del profilo
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Imposta il nome di questo Profile.
	 * @param name nome del Profile
	 * @throws NullPointerException se name è null
	 * @return questo Profile
	 */
	public Profile setName(String name)
	{
		if(name==null) throw new NullPointerException("name point to null");
		else this.name = name;
		
		return this;
	}
	
	/**
	 * Restituisce il tempo di campionamento.
	 * @return il tempo di campionamento Ts
	 */
	public double getSamplingTime()
	{
		return samplingTime;
	}
	
	public int getSamplesNumber()
	{
		return this.data.nCols();
	}
	
	/**
	 * Restituisce l'errore massimo ammesso da questo oggetto nel considerare due double uguali.
	 * @return the admittableError
	 */
	public double getAdmittableError()
	{
		return admittableError;
	}
	
	/**
	 * Imposta l'errore massimo ammesso nel confronto tra due double.
	 * Questo metodo fa uso di {@link Profile.DoubleEqualityErrorAdmissibility DoubleEqualityErrorAdmissibility}
	 * per specificare l'errore.
	 * L'errore di default è di un milionesimo.
	 * @param admittableError l'errore massimo ammissibile (dipendente dall'applicazione)
	 * @return questo Profile
	 */
	public Profile setAdmittableError(DoubleEqualityErrorAdmissibility admittableError)
	{
		switch(admittableError)
		{
			case NONE: 		 { this.admittableError = 0; } break;
			case THOUSANDTH: { this.admittableError = 0.001; } break;
			case MILLIONTH:  { this.admittableError = 0.000001; } break;
			case BILLIONTH:  { this.admittableError = 0.000000001; } break;
		}
		this.data.setAdmittableError(this.admittableError);
		return this;
	}

	/**
	 * Imposta l'errore massimo ammesso nel confronto tra due double.
	 * L'errore di default è di un milionesimo.
	 * @param admittableError l'errore massimo ammissibile (dipendente dall'applicazione)
	 * @return questo Profile
	 */
	public Profile setAdmittableError(double admittableError)
	{
		this.admittableError = Math.abs(admittableError);
		this.data.setAdmittableError(this.admittableError);
		return this;
	}

	/**
	 * Restituisce il tipo di distanza computato tra questo Profile e un altro Profile.
	 * @return the distType
	 */
	public Matrix.DistanceType getDistanceType()
	{
		return distType;
	}

	/**
	 * Imposta per questo Profile la tipologia di distanza computata.
	 * Questa impostazione ha effetto sul clustering.
	 * @param distType the distType to set
	 */
	public void setDistanceType(Matrix.DistanceType distType)
	{
		this.distType = distType;
	}

	/**
	 * Controlla la compatibilità con un altro Profile.
	 * Basato sul confronto del numero di campioni e sull'uguaglianza dei tempi di campionamento.
	 * @param o Profile x
	 * @return true, se i due tempi di misurazione di this e x corrispondono,
	 * false altrimenti
	 */
	public boolean isCompatible(Profile o)
	{
		return this.data.isIsoDimensional(o.data) &&
				Matrix.areEqualWithinLimit(this.samplingTime, o.samplingTime, this.admittableError);
	}
	
	/**
	 * Controlla la compatibilità tra i Profile di un array.
	 * @param data array di Profile
	 * @return true se tutti i tempi totali di campionamento dei Profile nell'array corrispondono,
	 * false altrimenti
	 */
	public static boolean areCompatible(Profile[] data)
	{
		for(int i=0; i<data.length-1; i++) //i<data.length-1 perchè le iterazioni confrontano il profilo i col profilo i+1
			if( !data[i].isCompatible(data[i+1]) ) return false;
		
		return true;
	}
	
	/**
	 * Controlla l'eguaglianza tra due Profile consentendo ai campioni di differire al più
	 * di un certo errore.
	 * Se l'errore dei campioni è minore della soglia impostata, allora i due campioni sono
	 * considerati uguali.
	 * @param p altro oggetto Profile con cui effettuare il confronto
	 * @param err errore massimo ammissibile per considerare due double (e quindi due campioni) uguali
	 * @return true se i due Profile hanno gli stessi campioni entro un certo range di errore, false altrimenti
	 */
	public boolean equals(Profile p)
	{
		if( !this.isCompatible(p) ) return false;
		if( !this.data.equals(p.data) ) return false; //equals è fatto eseguire da this.data perchè vogliamo che l'errore massimo ammesso sia quello impostato per questo profilo
		if( !this.name.equals(p.name) ) return false;
		if( this.timeUnit!=null && p.timeUnit!=null && !this.timeUnit.equals(p.timeUnit) ) return false;
	
		return true;
	}
	
	/**
	 * Controlla l'eguaglianza tra due Profile consentendo ai campioni di differire al più
	 * di un certo errore.
	 * Se l'errore dei campioni è minore della soglia impostata, allora i due campioni sono
	 * considerati uguali.
	 * Il nome viene tralasciato.
	 * @param p altro oggetto Profile con cui effettuare il confronto
	 * @param err errore massimo ammissibile per considerare due double (e quindi due campioni) uguali
	 * @return true se i due Profile hanno gli stessi campioni entro un certo range di errore, false altrimenti
	 */
	public boolean equalsButName(Profile p)
	{
		if( !this.isCompatible(p) ) return false;
		if( !this.data.equals(p.data) ) return false; //equals è fatto eseguire da this.data perchè vogliamo che l'errore massimo ammesso sia quello impostato per questo profilo
		//if( !this.name.equals(p.name) ) return false;
		if( this.timeUnit!=null && p.timeUnit!=null && !this.timeUnit.equals(p.timeUnit) ) return false;
	
		return true;
	}

	/**
	 * Calcola la varianza dei campioni contenuti in questo Profile.
	 * @return la varianza calcolata
	 */
	public double variance() { return data.variance(); }

	/**
	 * Calcola la media dei campioni contenuti in questo Profile.
	 * @return la media calcolata
	 */
	public double mean() { return data.mean(); }
	
	/**
	 * Calcola la differenza totale fra campioni corrispondenti di questo Profile con un altro Profile.
	 * @param o altro Profile
	 * @return la differenza totale fra i campioni di questo Profile con l'altro Profile
	 * @throws IllegalArgumentException se i due Profile (this e o) non sono compatibili
	 */
	public double distance(Profile o)
	{
		if( !this.isCompatible(o) ) throw new IllegalArgumentException("Profiles are not compatible");
		
		return this.data.distance(o.data, this.distType);
	}

	/**
	 * Crea un nuovo Profile con i campioni di questo Profile, ma normalizzati in [-1,+1].
	 * Normalizza i campioni in base alla media e al massimo valore assoluto dei campioni.
	 * I campioni normalizzati sono calcolati alla prima invocazione di questo metodo e l'array
	 * calcolato viene conservato e non viene ricalcolato alle successive invocazioni
	 * @return l'array di campioni normalizzato
	 */
	public Profile normalize()
	{
		Matrix dataMinusMean = data.minus( data.mean() );
		double normFactor = dataMinusMean.abs().maximum();
		Matrix normMatrix;
		if(normFactor == 0) normMatrix = dataMinusMean.newLikeThis().fill(0); 
		else normMatrix = dataMinusMean.scalarDivision(normFactor);
		
		Profile normalizedP = this.clone();
			normalizedP.data = normMatrix;
		return normalizedP;
	}
	
	/**
	 * Crea un nuovo Profile da interpolazione.
	 * I campioni del nuovo Profile sono ricavati da questo Profile tramite algoritmo SP-Line.
	 * Il numero di campioni viene portato, per interpolazone, a newNOfSamples.
	 * Date le peculiarità di questo algoritmo di interpolazione, il tempo totale di campionamento
	 * non è considerabile nc*Ts, perchè il campione non è considerato preso a metà di Ts, ma all'inizio
	 * di Ts; si potrebbe dire che il tempo totale di campionamento è (nC-1)*Ts.
	 * Il name del nuovo Profile è lo stesso di questo.
	 * @param newNOfSamples il nuovo numero di campioni
	 * @return un nuovo Profile con stesso nome, unità di misura del tempo,
	 * ma con numero di campioni e tempo di campionamento diversi, ovvero ottenuti per interpolazione
	 */
	public Profile interpolate(int newNOfSamples)
	{
		//Calcolo il nuovo tempo di campionamento
		double newSamplingTime = (this.data.nCols()-1) *samplingTime/(newNOfSamples-1);
		
		//1 -> array con i tempi di campionamento:
		double[] Tcamp = new double[this.data.nCols()];
			for(int i=0; i<data.nCols(); i++)
				Tcamp[i] = i*this.samplingTime;

		//2 -> array con i campioni nei corrispondenti istanti di tempo
		double[] samples = this.data.getRow();
		
		CubicSpline interpolator = new CubicSpline(Tcamp, samples);
		
		double[] newSamples = new double[newNOfSamples];
			for(int i=0; i<newNOfSamples; i++)
				newSamples[i] = interpolator.interpolate(i*newSamplingTime);
		
		Profile newP = this.clone();
			newP.data = new Matrix(newSamples, Matrix.MatrixType.RowMatrix);
			newP.samplingTime = newSamplingTime;
		return newP;
	}

	/**
	 * Media i campioni di un array di Profile in un nuovo unico Profile.
	 * Controlla preliminarmente la compatibilità tra i Profile dell'array e, se sono compatibili,
	 * media i corrispondenti campioni temporali e crea un nuovo Profile con questi nuovi dati.
	 * In genere lo si usa dopo il clustering per unificare i Profile dello stesso cluster in un unico
	 * Profile.
	 * Il nome del Profile viene calcolato come somma dei nomi dei Profile componenti separati da virgole.
	 * Se questo modo di calcolare il nome non piace è sempre possibile impostarne uno nuovo tramite i
	 * metodi del nuovo Profile.
	 * Il sampling time del nuovo Profile è quello dei Profile dell'array.
	 * L'unità di misura del tempo di campionamento non viene impostata, ma è impostabile sul nuovo
	 * Profile tramite il suo metodo {@link #setTimeUnit(String) setTimeUnit(String)}.
	 * @param profiles array di Profile da unire tramite media dei campioni
	 * @return un nuovo Profile frutto della media dei Profile dell'array
	 * @throws IllegalArgumentException se i Profile contenuti in profiles non sono compatibili
	 */
	public static Profile unify(Profile[] profiles)
	{
		if( !Profile.areCompatible(profiles) ) throw new IllegalArgumentException("not compatible Profiles found");
		
		Matrix[] mArr = new Matrix[profiles.length];
			for(int i=0; i<profiles.length; i++) mArr[i] = profiles[i].data;
		Matrix appendProf = Matrix.newFromMatrixArray(mArr, Matrix.AppendMode.RowMode); //righe una sull'altra a formare una nuova matrice
		double[] means = new double[profiles[0].data.nCols()];
			for(int i=0; i<profiles[0].data.nCols(); i++)
				means[i] = appendProf.columnMean(i);
		
		String name = profiles[0].name;
		for(int i=1; i<profiles.length; i++) name += ", " + profiles[i].name;
		//name contiene il nuovo nome del Profile: la somma dei nomi dei Profile componenti
		
		Profile uni = profiles[0].clone();
			uni.name = name;
			uni.data = new Matrix(means, Matrix.MatrixType.RowMatrix);
		return uni;
	}
	
	/**
	 * Trasforma un array di Profile in un array bidimensionale di double.
	 * Ogni riga del nuovo array bidimensionale corrisponde ai campioni di un Profile
	 * dell'array passato come parametro.
	 * L'ordine delle righe dell'array ritornato rispetta l'ordine delle righe dell'array di Profile
	 * passato come parametro.
	 * @param profs array di Profile da elaborare
	 * @return array bidimensionale di double contenenti tutti i campioni dei Profile
	 */
	public static double[][] to2DArray(Profile[] profs)
	{
		if( !Profile.areCompatible(profs) ) throw new IllegalArgumentException("not compatible Profiles found");
		
		double[][] samples = new double[profs.length][];
			for(int i=0; i<profs.length; i++)
				samples[i] = profs[i].data.getRow().clone();
		
		return samples;
	}

	protected Profile clone()
	{
		Profile p = new Profile(this.name, new double[]{0}, this.samplingTime);
			p.data = this.data;
			p.timeUnit = this.timeUnit;
			p.admittableError = this.admittableError;
			p.distType = this.distType;
		return p;
	}
}