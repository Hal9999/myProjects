using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Reflection;
using System.Threading;
using System.Drawing.Imaging;
using System.Drawing.Drawing2D;
using System.IO;
using MySql.Data.MySqlClient;
using System.Text.RegularExpressions;



namespace VisulPoker
{
       
    class Applicazione : ConfiguraGioco
    {
        //********************COORDINATE CARTE E PULSANTI GIOCO*********************//
        //Posizione finestra
         int finestraPosX;
         int finestraPosY;
        //Conterranno i valori delle carte
         String ICarta = "";
         String IICarta = "";
         String IIICarta = "";
         String IVCarta = "";
         String VCarta = "";
        //Coordinate carte da gioco
         int ICartaX = 229;
         int ICartaY = 442;
         int IICartaX = 313;
         int IICartaY = 442;
         int IIICartaX = 404;
         int IIICartaY = 442;
         int IVCartaX = 490;
         int IVCartaY = 442;
         int VCartaX = 588;
         int VCartaY = 442;
        //Coordinate tasti gioco
         int betMaxX = 500;
         int betMaxY = 560;
         int drawX = 400;
         int drawY = 560;       
         int speedDialX = 34;
         int speedDialY = 540;
         int connessioneInterrottaX = 400;
         int connessioneInterrottaY = 285;
         int cinqueX = 610;
         int cinqueY = 580;         

        //********************VARIABILI PER L'ANALISI DELLE SCREENSHOT*********************//
         Bitmap screenshot = null;        
         int [] numeriCambioMossa;
         Color colorePixelBetMax;
         Color colorePixelDraw;
         Color colorePixelConnessione;
         Color colorePixelCinque;         
         int numeroMani;
        //Boolean betMaxStefano, drawStefano, cinqueStefano;
        //Controllo interfaccia grafica
         Boolean betMax, draw, cinque, connessione = false;
         //GlobalVar conteggio screenshot per ogni fase AttendiPulsantiAttivi()
         int globalVar;
         int giocate=0;
         Random RandomClass = new Random();
         StreamWriter sw;
        //Matrice
         int [,] matrix = new int[,]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 0}, {1, 1, 1, 0, 1}, {1, 1, 0, 1, 1},
                                            {1, 0, 1, 1, 1}, {0, 1, 1, 1, 1}, {1, 1, 1, 0, 0}, {1, 1, 0, 1, 0},
                                            {1, 0, 1, 1, 0}, {0, 1, 1, 1, 0}, {1, 1, 0, 0, 1}, {1, 0, 1, 0, 1},
                                            {0, 1, 1, 0, 1}, {1, 0, 0, 1, 1}, {0, 1, 0, 1, 1}, {0, 0, 1, 1, 1},
                                            {0, 0, 0, 1, 1}, {0, 0, 1, 0, 1}, {0, 1, 0, 0, 1}, {1, 0, 0, 0, 1},
                                            {0, 0, 1, 1, 0}, {0, 1, 0, 1, 0}, {1, 0, 0, 1, 0}, {0, 1, 1, 0, 0},
                                            {1, 0, 1, 0, 0}, {1, 1, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 1, 0},
                                            {0, 0, 1, 0, 0}, {0, 1, 0, 0, 0}, {1, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
       

        //Recupera path del PC
         string appPath = System.IO.Path.GetDirectoryName(Assembly.GetEntryAssembly().Location);

        //********************Database MySql*********************//
         MySqlConnection conn;
         MySqlDataReader reader;
         MySqlCommand command;


        

        
        //Costruttore
        public Applicazione(int m) 
        {
            //Imposta numero mani da giocare. Default 1000000 mani
            numeroMani = m;                                 
        }

        
        public void Start()
        {
            PortaFinestraTop();
            Thread.Sleep(1000);
            CatturaSchermata();
            CercaFinestra();

            Console.WriteLine("Connessione al database");
            Console.WriteLine(DateTime.Now.ToString());
            // Create an instance of StreamWriter to write text to a file.
            // The using statement also closes the StreamWriter.
            using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true))
            {
                // Add some text to the file.
                sw.WriteLine("Start: Connessione al database");
                sw.WriteLine("The date is: " + DateTime.Now);
                sw.WriteLine("--------------------------------");
            }
            ConnessioneAlDatabase();
            Console.WriteLine(DateTime.Now.ToString());


            while (true)
            {
                GC.Collect();
                BloccaEsecuzioneApp();

                if (numeroMani > 0)
                    Player();
                else
                    ChiusuraApp("Numero di mani completati. ", true);

                GC.WaitForPendingFinalizers();
            }            
        }

        void Player()
        {
            BloccaEsecuzioneApp();
         
            AttendiPulsantiAttivi();                       

            //Se è attivo betmax
            if (betMax)
            {
                Thread.Sleep(RandomClass.Next(200, 400));
                clickBetMax();

                globalVar = 0;
            }            

            //Se è attivo draw
            if (draw)
            {
                Thread.Sleep(RandomClass.Next(200, 400));
                AnalisiCarte();                
                numeriCambioMossa = ValutaMossa(ICarta + " " + IICarta + " " + IIICarta + " " + IVCarta + " " + VCarta);
                SelezioneCarteDaTenere(numeriCambioMossa);
                clickDraw();
               
                Console.WriteLine("Numero Mano\t" + giocate);
                //Scrive sul file
                using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true))
                    sw.WriteLine("Numero Mano\t" + ++giocate);
                
                //decrementa il numero mani
                --numeroMani;                
                globalVar = 0;
            }

            if (cinque && !draw && !betMax)
            {
                ChiusuraApp("Inserire credito", true);
            }

            if (connessione)
            {                
                ChiusuraApp("Problemi di connessione o credito esaurito. ",true);
            }
            screenshot.Dispose();
            Thread.Sleep(400);
            
        }

        void AttendiPulsantiAttivi()
        {
            BloccaEsecuzioneApp();
            //Imposto variabili  false
            betMax = false; draw = false; cinque = false; connessione = false;
            //Console.WriteLine("Attendi... " + ++globalVar);

            while (true)
            {
                BloccaEsecuzioneApp();
               
                try
                {  
                    
                    screenshot = new Bitmap(Screen.PrimaryScreen.Bounds.Width, Screen.PrimaryScreen.Bounds.Height, PixelFormat.Format32bppArgb);
                    Graphics gfxSS = Graphics.FromImage(screenshot);
                    gfxSS.CopyFromScreen(Screen.PrimaryScreen.Bounds.X, Screen.PrimaryScreen.Bounds.Y, 0, 0, Screen.PrimaryScreen.Bounds.Size, CopyPixelOperation.SourceCopy);

                    //screenshot.Save(appPath + @"\..\..\risorse\Screenshot" + globalVar + ".bmp", ImageFormat.Bmp);
                    //Console.Write("Screenshot " + globalVar + " salvato\n");
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Source + ex.ToString() + ex.Message + ex.StackTrace + ex.InnerException + ex.Data);
                }

                //Ottengo pixel del pulsante da controllare
                colorePixelBetMax = screenshot.GetPixel(finestraPosX + betMaxX, finestraPosY + betMaxY);
                colorePixelDraw = screenshot.GetPixel(finestraPosX + drawX, finestraPosY + drawY);
                colorePixelCinque = screenshot.GetPixel(finestraPosX + cinqueX, finestraPosY + cinqueY);                
                colorePixelConnessione = screenshot.GetPixel(finestraPosX + connessioneInterrottaX, finestraPosY + connessioneInterrottaY);
                

                //Controllo pixel, se uno dei pixel è corretto imposto variabiabile a true ed esco dal metodo
                if (colorePixelBetMax.B == 0 && colorePixelBetMax.G == 167 && colorePixelBetMax.R == 55) betMax = true;                
                if (colorePixelDraw.B == 0 && colorePixelDraw.G == 147 && colorePixelDraw.R == 55) draw = true;
                if (colorePixelCinque.B == 2 && colorePixelCinque.G == 2 && colorePixelCinque.R == 143) cinque = true;                
                if (colorePixelConnessione.B == 227 && colorePixelConnessione.G == 223 && colorePixelConnessione.R == 224) connessione = true;
               
                Console.WriteLine(++globalVar + ": BetMax>" + betMax +" Draw>" + draw + " 5$>" + cinque + " Stato Connessione o Fine credito>" + connessione);


                
                if (betMax || draw || cinque || connessione) break;
                else
                {
                    screenshot.Dispose();
                    Thread.Sleep(200);
                }


                //Se fa troppi screenshot senza esito positivo blocca l'applicazione 
                //(Circa 20 secondi di attesa)
                if (globalVar == 100) ChiusuraApp("Troppi tentativi per riconoscere la schermata; Input non gestito. ",true);                
            }
        }                                

        void SelezioneCarteDaTenere(int [] n)
        {                        
            //***********SE L'ARRAY NELLA POSIZIONE INDICATA CONTIENE 1, CLICCA SULLA CARTA****************//
            //Prima Posizione
            //int time = 100;
            int time = RandomClass.Next(100, 300);
            Thread.Sleep(time);
            if (n[0]==1)
            {                
                Cursor.Position = new Point(finestraPosX + ICartaX, finestraPosY + ICartaY);                
                SendClick();
                Thread.Sleep(time);
            }
            //Seconda Posizione
            if (n[1] == 1)
            {                
                Cursor.Position = new Point(finestraPosX + IICartaX, finestraPosY + IICartaY);
                SendClick();
                Thread.Sleep(time);
            }
            //Terza Posizione
            if (n[2] == 1)
            {                
                Cursor.Position = new Point(finestraPosX + IIICartaX, finestraPosY + IIICartaY);
                SendClick();
                Thread.Sleep(time);
            }
            //Quarta Posizione
            if (n[3] == 1)
            {                
                Cursor.Position = new Point(finestraPosX + IVCartaX, finestraPosY + IVCartaY);
                SendClick();
                Thread.Sleep(time);
            }
            //Quinta Posizione
            if (n[4] == 1)
            {                
                Cursor.Position = new Point(finestraPosX + VCartaX, finestraPosY + VCartaY);
                SendClick();
                Thread.Sleep(time);
            }            
        }                        

        //Valuta Mossa da effettuare
        int [] ValutaMossa(String inp)
        {
            Console.WriteLine(inp);
            using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true)) sw.WriteLine(inp);
            int[] c = new int[5];
            int[,] order = new int[5, 2];

            if (!Regex.IsMatch(inp, "^[123456789ADJQKadjqk][CQPFJcqpfj] [123456789ADJQKadjqk][CQPFJcqpfj] [123456789ADJQKadjqk][CQPFJcqpfj] [123456789ADJQKadjqk][CQPFJcqpfj] [123456789ADJQKadjqk][CQPFJcqpfj]$"))
                throw new System.ArgumentException("regex not matched");
            char[] inpa = inp.ToCharArray();
            if (inpa.Length != 14) throw new System.ArgumentException("Wrong lenght");
            for (int i = 0, j = 0; i < 14; i += 3, j++)
            {
                if ((inpa[i] == 'J' || inpa[i] == 'j') && (inpa[i + 1] == 'J' || inpa[i + 1] == 'j')) c[j] = 52;
                else if (inpa[i] == 'A' || inpa[i] == 'a') c[j] = 0;
                else if (inpa[i] >= '2' && inpa[i] <= '9') c[j] = inpa[i] - '0' - 1;
                else if (inpa[i] == 'D' || inpa[i] == 'd') c[j] = 9;
                else if (inpa[i] == 'J' || inpa[i] == 'j') c[j] = 10;
                else if (inpa[i] == 'Q' || inpa[i] == 'q') c[j] = 11;
                else if (inpa[i] == 'K' || inpa[i] == 'k') c[j] = 12;
                else throw new System.ArgumentException("can't parse " + inpa[i] + inpa[i + 1]);

                if ((inpa[i] == 'J' || inpa[i] == 'j') && (inpa[i + 1] == 'J' || inpa[i + 1] == 'j')) c[j] = 52;
                else if (inpa[i + 1] == 'C' || inpa[i + 1] == 'c') c[j] += 0;
                else if (inpa[i + 1] == 'Q' || inpa[i + 1] == 'q') c[j] += 13;
                else if (inpa[i + 1] == 'P' || inpa[i + 1] == 'p') c[j] += 26;
                else if (inpa[i + 1] == 'F' || inpa[i + 1] == 'f') c[j] += 39;
                else throw new System.ArgumentException("can't parse " + inpa[i] + inpa[i + 1]);
            }
            for (int i = 0; i < 5; i++) //verifichiamo che sono tutte carte diverse
                for (int j = 0; j < 5; j++)
                    if (i != j && c[i] == c[j]) throw new System.ArgumentException("Error: doubled card: " + c[i] + " " + c[j]);
            for (int i = 0; i < 5; i++) order[i, 0] = c[i]; //conserviamo in order[][0] l'ordine di immissione delle carte, in order[][1] metteremo la mossa consigliata
            Console.WriteLine(c[0] + " " + c[1] + " " + c[2] + " " + c[3] + " " + c[4] + " ");

            //using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true)) sw.WriteLine(c[0] + " " + c[1] + " " + c[2] + " " + c[3] + " " + c[4] + " ");

            //Ordina array
            Array.Sort(c);

            Console.WriteLine(c[0] + " " + c[1] + " " + c[2] + " " + c[3] + " " + c[4] + " ");
            //Scrivo su file
            //using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true)) sw.WriteLine(c[0] + " " + c[1] + " " + c[2] + " " + c[3] + " " + c[4] + " ");

            int n = InterrogaDB(c[0] + " " + c[1] + " " + c[2] + " " + c[3] + " " + c[4]);

            Console.WriteLine(matrix[n, 0] + "  " + matrix[n, 1] + "  " + matrix[n, 2] + "  " + matrix[n, 3] + "  " + matrix[n, 4]);
            //Scrivo su file
            //using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true)) sw.WriteLine(matrix[n, 0] + "  " + matrix[n, 1] + "  " + matrix[n, 2] + "  " + matrix[n, 3] + "  " + matrix[n, 4]);

            for (int i = 0; i < 5; i++) //contatore per c[]
                for (int j = 0; j < 5; j++) //contatore per order[]
                    if (c[i] == order[j, 0]) order[j, 1] = matrix[n, i];
            Console.WriteLine((order[0, 1] == 1 ? "H  " : "   ") + (order[1, 1] == 1 ? "H  " : "   ") + (order[2, 1] == 1 ? "H  " : "   ") + (order[3, 1] == 1 ? "H  " : "   ") + (order[4, 1] == 1 ? "H  " : "   "));
             //Scrivo su file
             using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true))
                 sw.WriteLine((order[0, 1] == 1 ? "H  " : "   ") + (order[1, 1] == 1 ? "H  " : "   ") + (order[2, 1] == 1 ? "H  " : "   ") + (order[3, 1] == 1 ? "H  " : "   ") + (order[4, 1] == 1 ? "H  " : "   "));

            int [] mossa = new int[5];
            for (int i = 0; i < 5; i++) mossa[i] = order[i, 1];
            return mossa;
        }

         int InterrogaDB(string s)
        {
            int mossa;
            string str = null;

            command = conn.CreateCommand();
            
            command.CommandText = "Select SubNumber from Hands where Hand = '" + s + "'";
            reader = command.ExecuteReader();

            while (reader.Read())
            {
                Console.WriteLine(reader["SubNumber"].ToString());
                str = reader["SubNumber"].ToString();
                //int h = (int) reader["SubNumber"];
            }
            //Cast da string a int
            mossa = Convert.ToInt32(str);

            command.Dispose();
            reader.Close();
            return mossa;            
        }
        //Analizza le carte
         void AnalisiCarte()
        {
            PrimaCarta();
            SecondaCarta();
            TerzaCarta();
            QuartaCarta();
            QuintaCarta();
        }

        // Cerca la finestra di gioco e setta le posizioni finestraPosX e finestraPosY del client
         void CercaFinestra()
        {
            Color colorePixel;
                        
            for (int x = 0; x < screenshot.Width; x++)
            {
                for (int y = 45; y < screenshot.Height; y++)
                {
                    colorePixel = screenshot.GetPixel(x, y);
                    if (colorePixel.B == 126 && colorePixel.G == 102 && colorePixel.R == 102)
                    {
                        finestraPosX = x;
                        finestraPosY = y;
                        //Console.WriteLine("Finestra trovata nella posizione\tx:" + finestraPosX+ "\ty:" + finestraPosY);
                        return;
                    }
                }
            }
        }
       
        //Cattura e salva Screenshot
         void CatturaSchermata()
        {           
            Graphics gfxSS = null;
            try
            {
                screenshot = new Bitmap(Screen.PrimaryScreen.Bounds.Width,
                       Screen.PrimaryScreen.Bounds.Height,
                       PixelFormat.Format32bppArgb);

                gfxSS = Graphics.FromImage(screenshot);

                gfxSS.CopyFromScreen(
                    Screen.PrimaryScreen.Bounds.X,
                    Screen.PrimaryScreen.Bounds.Y,
                    0,
                    0,
                    Screen.PrimaryScreen.Bounds.Size,
                    CopyPixelOperation.SourceCopy);

                //screenshot.Save(appPath + @"\..\..\risorse\Screenshot.bmp", ImageFormat.Bmp);
                //Console.Write("Screenshot effettuato e salvato\n");                
            }

            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }
        
        
        //Click Pulsanti
         void ClickSpeedDial()
        {
            Cursor.Position = new Point(finestraPosX + speedDialX, finestraPosY + speedDialY);
            mouse_event(MouseEventLeftDown, 0, 0, 0, new System.IntPtr());
            mouse_event(MouseEventLeftUp, 0, 0, 0, new System.IntPtr());
        }
         void clickBetMax()
        {
            Cursor.Position = new Point(finestraPosX + betMaxX, finestraPosY + betMaxY);
            mouse_event(MouseEventLeftDown, 0, 0, 0, new System.IntPtr());
            mouse_event(MouseEventLeftUp, 0, 0, 0, new System.IntPtr());
        }
         void clickFiveDollars()
        {
            Cursor.Position = new Point(finestraPosX + cinqueX, finestraPosY + cinqueY);
            mouse_event(MouseEventLeftDown, 0, 0, 0, new System.IntPtr());
            mouse_event(MouseEventLeftUp, 0, 0, 0, new System.IntPtr());
        }         
         void clickDraw()
        {
            Cursor.Position = new Point(finestraPosX + drawX, finestraPosY + drawY);
            mouse_event(MouseEventLeftDown, 0, 0, 0, new System.IntPtr());
            mouse_event(MouseEventLeftUp, 0, 0, 0, new System.IntPtr());
        }

         //*********Variabili per la gestione del click del mouse**************//        
         [DllImport("user32.dll")]
         static extern void mouse_event(UInt32 dwFlags, UInt32 dx, UInt32 dy, UInt32 dwData, IntPtr dwExtraInfo);
         //Variabili per il tasto sinistro
         const UInt32 MouseEventLeftDown = 0x0002;
         const UInt32 MouseEventLeftUp = 0x0004;
         //Click del mouse
         void SendClick()
         {
             mouse_event(MouseEventLeftDown, 0, 0, 0, new System.IntPtr());
             mouse_event(MouseEventLeftUp, 0, 0, 0, new System.IntPtr());
         }
        
        
        //Carte
         void QuintaCarta()
        {
            //Posizione iniziale carattere seconda carta
            int posX = finestraPosX + 550;
            int posY = finestraPosY + 387;
            int posFinaleX = 24;
            int posFinaleY = 27;
            //Posizione iniziale seme seconda carta
            int posSemeX = finestraPosX + 572;
            int posSemeY = finestraPosY + 454;
            int posSemeFinaleX = 32;
            int posSemeFinaleY = 37;    
            //Carta
            String carattereCarta="";
            String seme="";
           
            //Ottiene il formato dallo screenshot 
            System.Drawing.Imaging.PixelFormat format = screenshot.PixelFormat;
            //Ritaglio area contenente il carattere
            Rectangle areaCarattere = new Rectangle(posX, posY, posFinaleX, posFinaleY);            
            //Ritaglio area contenente il seme
            Rectangle areaSeme = new Rectangle(posSemeX, posSemeY, posSemeFinaleX, posSemeFinaleY);


            //Ottengo dallo screenshot l'area contenete il numero
            Bitmap imgCarattere = screenshot.Clone(areaCarattere, format);
            //Ottengo screenshot l'area contenete il seme
            Bitmap imgSeme = screenshot.Clone(areaSeme, format);
           

            //Salvo immagine numero
            //imgCarattere.Save(appPath + @"\..\..\risorse\imgCarattere5.bmp");            
            //Salvo immagine seme
            //imgSeme.Save(appPath + @"\..\..\risorse\imgSeme5.bmp");

            //Console.WriteLine("Quinta carta\t");

            //Conta il numero di pixel bianche nell'immagine
            int numeroPixel = GetPixelBianchiImmagine(imgCarattere);
            //Confronta il numeroPixel per identificare la carta
            carattereCarta = IdentificaCarta(numeroPixel, imgCarattere);            

            //Analizza immagine seme
            seme = analizzaSeme(imgSeme);
            //Stringa contenente la carta
            VCarta = carattereCarta + seme;
            //Console.WriteLine(VCarta);

            imgCarattere.Dispose();
            imgSeme.Dispose();
        }
         void QuartaCarta()
        {
            //Posizione iniziale carattere seconda carta
            int posX = finestraPosX + 460;
            int posY = finestraPosY + 387;
            int posFinaleX = 24;
            int posFinaleY = 27;
            //Posizione iniziale seme seconda carta
            int posSemeX = finestraPosX + 482;
            int posSemeY = finestraPosY + 454;
            int posSemeFinaleX = 32;
            int posSemeFinaleY = 37;
            //Carta
            String carattereCarta = "";
            String seme = "";


            //Ottiene il formato dallo screenshot 
            System.Drawing.Imaging.PixelFormat format = screenshot.PixelFormat;
            //Ritaglio area contenente il carattere
            Rectangle areaCarattere = new Rectangle(posX, posY, posFinaleX, posFinaleY);
            //Ritaglio area contenente il seme
            Rectangle areaSeme = new Rectangle(posSemeX, posSemeY, posSemeFinaleX, posSemeFinaleY);


            //Ottengo l'immagine contenete il carattere
            Bitmap imgCarattere = screenshot.Clone(areaCarattere, format);
            //Ottengo l'immagine contenete il seme
            Bitmap imgSeme = screenshot.Clone(areaSeme, format);

            ////Salvo immagine carattere
            //imgCarattere.Save(appPath + @"\..\..\risorse\imgCarattere4.bmp");            
            ////Salvo immagine seme
            //imgSeme.Save(appPath + @"\..\..\risorse\imgSeme4.bmp");

            //Console.WriteLine("Quarta carta\t");

            //Conta il numero di pixel bianche nell'immagine
            int numeroPixel = GetPixelBianchiImmagine(imgCarattere);
            //Confronta il numeroPixel per identificare la carta
            carattereCarta = IdentificaCarta(numeroPixel, imgCarattere);            
            
            ////Analizza immagine seme
            seme = analizzaSeme(imgSeme);

            //Stringa contenente la carta
            IVCarta = carattereCarta + seme;
            //Console.WriteLine(IVCarta);
            imgCarattere.Dispose();
            imgSeme.Dispose();
        }
         void TerzaCarta()
        {
            //Posizione iniziale carattere seconda carta
            int posX = finestraPosX + 370;
            int posY = finestraPosY + 387;
            int posFinaleX = 24;
            int posFinaleY = 27;
            //Posizione iniziale seme seconda carta
            int posSemeX = finestraPosX + 392;
            int posSemeY = finestraPosY + 454;
            int posSemeFinaleX = 32;
            int posSemeFinaleY = 37;
            //Carta
            String carattereCarta = "";
            String seme = "";

            //Ottiene il formato dallo screenshot 
            System.Drawing.Imaging.PixelFormat format = screenshot.PixelFormat;
            //Ritaglio area contenente il carattere
            Rectangle areaCarattere = new Rectangle(posX, posY, posFinaleX, posFinaleY);
            //Ritaglio area contenente il seme
            Rectangle areaSeme = new Rectangle(posSemeX, posSemeY, posSemeFinaleX, posSemeFinaleY);


            //Ottengo l'immagine contenete il carattere
            Bitmap imgCarattere = screenshot.Clone(areaCarattere, format);
            //Ottengo l'immagine contenete il seme
            Bitmap imgSeme = screenshot.Clone(areaSeme, format);
                        

            ////Salvo immagine carattere
            //imgCarattere.Save(appPath + @"\..\..\risorse\imgCarattere3.bmp");            
            ////Salvo immagine seme
            //imgSeme.Save(appPath + @"\..\..\risorse\imgSeme3.bmp");

            //Console.WriteLine("Terza carta\t");
            //Conta il numero di pixel bianche nell'immagine
            int numeroPixel = GetPixelBianchiImmagine(imgCarattere);
            //Confronta il numeroPixel per identificare la carta
            carattereCarta = IdentificaCarta(numeroPixel, imgCarattere);            
           
            //Analizza immagine seme
            seme = analizzaSeme(imgSeme);

            //Stringa contenente la carta
            IIICarta = carattereCarta + seme;
            //Console.WriteLine(IIICarta);
            imgCarattere.Dispose();
            imgSeme.Dispose();
        }
         void SecondaCarta()
        {
            //Posizione iniziale carattere seconda carta
            int posX = finestraPosX + 280;
            int posY = finestraPosY + 387;
            int posFinaleX = 24;
            int posFinaleY = 27;
            //Posizione iniziale seme seconda carta
            int posSemeX = finestraPosX + 302;
            int posSemeY = finestraPosY + 454;
            int posSemeFinaleX = 32;
            int posSemeFinaleY = 37;
            //Carta
            String carattereCarta = "";
            String seme = "";

            //Ottiene il formato dallo screenshot 
            System.Drawing.Imaging.PixelFormat format = screenshot.PixelFormat;
            //Ritaglio area contenente il carattere
            Rectangle areaCarattere = new Rectangle(posX, posY, posFinaleX, posFinaleY);
            //Ritaglio area contenente il seme
            Rectangle areaSeme = new Rectangle(posSemeX, posSemeY, posSemeFinaleX, posSemeFinaleY);


            //Ottengo l'immagine contenete il carattere
            Bitmap imgCarattere = screenshot.Clone(areaCarattere, format);
            //Ottengo l'immagine contenete il seme
            Bitmap imgSeme = screenshot.Clone(areaSeme, format);
           

            ////Salvo immagine carattere
            //imgCarattere.Save(appPath + @"\..\..\risorse\imgCarattere2.bmp");            
            ////Salvo immagine seme
            //imgSeme.Save(appPath + @"\..\..\risorse\imgSeme2.bmp");

            //Console.WriteLine("Seconda carta\t");
            //Conta il numero di pixel bianche nell'immagine
            int numeroPixel = GetPixelBianchiImmagine(imgCarattere);
            
            //Confronta il numeroPixel per identificare la carta
            carattereCarta = IdentificaCarta(numeroPixel, imgCarattere);            
           
            //Analizza immagine seme
            seme = analizzaSeme(imgSeme);

            //Stringa contenente la carta
            IICarta = carattereCarta + seme;
            //Console.WriteLine(IICarta);  
            imgCarattere.Dispose();
            imgSeme.Dispose();
        }
         void PrimaCarta()
        {
            //Posizione iniziale carattere prima carta
            int posX = finestraPosX + 190;
            int posY = finestraPosY + 387;
            int posFinaleX = 24;
            int posFinaleY = 27;
            //Posizione iniziale seme prima carta
            int posSemeX = finestraPosX + 212;
            int posSemeY = finestraPosY + 454;
            int posSemeFinaleX = 32;
            int posSemeFinaleY = 37;
            //Carta
            String carattereCarta = "";
            String seme = "";

            //Ottiene il formato dallo screenshot 
            System.Drawing.Imaging.PixelFormat format = screenshot.PixelFormat;
            //Ritaglio area contenente il carattere
            Rectangle areaCarattere = new Rectangle(posX, posY, posFinaleX, posFinaleY);
            //Ritaglio area contenente il seme
            Rectangle areaSeme = new Rectangle(posSemeX, posSemeY, posSemeFinaleX, posSemeFinaleY);


            //Ottengo l'immagine contenete il carattere
            Bitmap imgCarattere = screenshot.Clone(areaCarattere, format);
            //Ottengo l'immagine contenete il seme
            Bitmap imgSeme = screenshot.Clone(areaSeme, format);

                       

            ////Salvo immagine carattere
            //imgCarattere.Save(appPath + @"\..\..\risorse\imgCarattere1.bmp");            
            ////Salvo immagine seme
            //imgSeme.Save(appPath + @"\..\..\risorse\imgSeme1.bmp");

            //Console.WriteLine("Prima carta\t");
            //Conta il numero di pixel bianche nell'immagine
            int numeroPixel = GetPixelBianchiImmagine(imgCarattere);
            //Confronta il numeroPixel per identificare la carta
            carattereCarta = IdentificaCarta(numeroPixel, imgCarattere);            
            
            //Analizza immagine seme
            seme = analizzaSeme(imgSeme);

            //Stringa contenente la carta
            ICarta = carattereCarta + seme;
            //Console.WriteLine(ICarta);
            imgCarattere.Dispose();
            imgSeme.Dispose();
        }

        //Conta il numero di pixel di colore bianco dell'imamgine Bitmap
         int GetPixelBianchiImmagine(Bitmap imgCarta)
        {
            Color colorePixel;
            int numeroColori = 0;

            for (int i = 1; i < imgCarta.Width; i++)
            {
                for (int j = 1; j < imgCarta.Height; j++)
                {
                    colorePixel = imgCarta.GetPixel(i, j);
                    if (colorePixel.B == 255 && colorePixel.G == 255 && colorePixel.R == 255)
                        numeroColori++;
                }
            }
            //Console.WriteLine(numeroColori);
            return numeroColori;
        }

        //Riconosce il numero della carta dall'immagine in base al numero di pixel bianchi
         string IdentificaCarta(int numeroPixel,Bitmap img)
        {
            //Conterrà il numero riconosciuto
            String carattere = "";

            //Un ulteriore controllo su carte con lo stesso numero di pixel bianco
            Color colorePixel = img.GetPixel(16, 16);
            Color colorePixel2 = img.GetPixel(18, 15);

            //Controllo dei pixel                     
            if (numeroPixel == 364)
                carattere = "A";
            if (numeroPixel == 314 || numeroPixel == 218 || numeroPixel == 182)
                carattere = "2";
            if (numeroPixel == 305 || numeroPixel == 298 || numeroPixel == 297)
                carattere = "3";
            if (numeroPixel == 317 && colorePixel2.B != 255 && colorePixel2.G != 255 && colorePixel2.R != 255)
                carattere = "3";
            if (numeroPixel == 376 || numeroPixel == 316)
                carattere = "4";
            if (numeroPixel == 318 && colorePixel.B != 255 && colorePixel.G != 255 && colorePixel.R != 255)
                carattere = "4"; 
            if (numeroPixel == 272)
                carattere = "5";
            if (numeroPixel == 217 || numeroPixel == 225)
                carattere = "6";
            if (numeroPixel == 389)
                carattere = "7";
            if (numeroPixel == 292 || numeroPixel == 269)
                carattere = "8";
            if (numeroPixel == 248)
                carattere = "9";
            if (numeroPixel == 194 || numeroPixel == 195)
                carattere = "D";
            if (numeroPixel == 425)
                carattere = "J";
            if (numeroPixel == 251 || numeroPixel == 236)
                carattere = "Q";
            if (numeroPixel == 332)
                carattere = "K";
            if (numeroPixel == 318 && colorePixel.B == 255 && colorePixel.G == 255 && colorePixel.R == 255)
                carattere = "K";
            if (numeroPixel == 317 && colorePixel2.B == 255 && colorePixel2.G == 255 && colorePixel2.R == 255)
                carattere = "K";
            if (numeroPixel == 452)
                carattere = "JJ";

            return carattere;
        }

        //Riconosce il seme dall'immagine
         string analizzaSeme(Bitmap i)
        {
            //Conterrà il seme identificato
            String seme = "";

            //Posizione centro immagine
            int posX = 15;
            int posY = 20;            

            //Posizione controllo pixel fiori
            int posFX = 6;
            int posFY = 12;
            //Posizione controllo pixel quadri
            int posQX = 7;
            int posQY = 8;

            //Ottengo il colore del pixel nel centro dell'immagine
            Color colorePixel = i.GetPixel(posX, posY);

            //Se il pixel è nero
            if (colorePixel.B == 0 && colorePixel.G == 0 && colorePixel.R == 0)
            {
                ////Pixel di controllo del seme Fiori
                colorePixel = i.GetPixel(posFX, posFY);
                if (colorePixel.B == 255 && colorePixel.G == 255 && colorePixel.R == 255)
                {
                    seme = "F";
                }
                else
                {
                    seme = "P";
                }
            }
            else
                //Se il pixel è rosso
                if (colorePixel.B == 52 && colorePixel.G == 46 && colorePixel.R == 237)
                {
                    //Pixel di controllo del seme Quadri
                    colorePixel = i.GetPixel(posQX, posQY);
                    if (colorePixel.B == 255 && colorePixel.G == 255 && colorePixel.R == 255)
                    {
                        seme = "Q";
                    }
                    else
                    {
                        seme = "C";
                    }
                }
                else
                    //Se il pixel è Bianco, siamo in presenza di un Jolly "J"
                    if (colorePixel.B == 255 && colorePixel.G == 255 && colorePixel.R == 255)
                        seme = "";

            return seme;
            
          }

        
        //Blocca esecuzione dell'applicazione se viene premuto il tasto shift
         void BloccaEsecuzioneApp()
        {
            if (Control.ModifierKeys == Keys.Shift)
            {                                
                ChiusuraApp("Intercettato tasto 'Shift'. ",true);
            }
        }

        //Chiude l'applicazione Killando il processo
         void ChiusuraApp(string s, Boolean controlloStato)
        {
            if (controlloStato)
            {
                Process [] Processi = Process.GetProcesses();
                for (int i = 0; i < Processi.Length; i++)
                {
                    if (Processi[i].ProcessName == "VisulPoker")
                    {                        
                        MessageBox.Show(s + "L'applicazione verrà chiusa",
                          "Chiusura applicazione", MessageBoxButtons.OK,
                             MessageBoxIcon.Information);
                        //Scrivo su file
                        using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true))
                        {
                            sw.WriteLine(DateTime.Now);
                            sw.WriteLine("**************************FINE***************************");                            
                        }
                        Processi[i].Kill();
                    }
                }
            }
        }
        

        //Connessione al database
         void ConnessioneAlDatabase()
        {

            string connString = "database=casdb;" +
                                "server=localhost;" +
                                "port=3306;" +
                                "username=root;" +
                                "password=88654788";

            conn = new MySqlConnection(connString);
            try
            {
                conn.Open();
                //Console.WriteLine("Connessione al database riuscita");
            }
            catch (Exception e)
            {
                //MessageBox.Show(e.Message);                
                using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true))
                    sw.WriteLine("Connessione fallita");
                ChiusuraApp(e.Message + "\n"+"Problema col database."+"\n"+"Assicurati di aver avviato il server mysql. ",true);

            }

        }

        //***************VARIABILI PORTA PER MASSIMIZZARE FINESTRA CASINO**********************//        
        // SW_SHOWMAXIMIZED to maximize the window  
         const int SW_SHOWMAXIMIZED = 3;
        [DllImport("user32.dll")]
         static extern bool ShowWindowAsync(IntPtr hWnd, int nCmdShow);

         void PortaFinestraTop()
        {
            Boolean controllo = true;
            Process[] Processi = Process.GetProcesses();
            for (int i = 0; i < Processi.Length; i++)
            {
                if (Processi[i].ProcessName == "VirtualBox")
                {
                    controllo = false;
                    //Ottiene VirtualBox main window handle
                    IntPtr hWnd = Processi[i].MainWindowHandle;
                    if (!hWnd.Equals(IntPtr.Zero))                                                                                      
                        ShowWindowAsync(hWnd, SW_SHOWMAXIMIZED);                    
                }
               
            }
            if (controllo)
            {
                using (sw = new StreamWriter(appPath + @"\..\..\..\log.txt", true))
                    sw.WriteLine("Macchiana vituale non trovata");
                ChiusuraApp("Macchina Virtuale non trovata, si prega di avviare la macchina virtuale e rilanciare l'applicazione. ", true);                    
            }
        }        
    }

}
