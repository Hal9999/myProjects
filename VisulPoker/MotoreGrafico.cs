using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VisulPoker
{
    static class MotoreGrafico
    {       
        [STAThread]
        static void Main()
        {            
            ConfiguraGioco conf = new ConfiguraGioco();
            conf.ShowDialog();
            
            Applicazione app = new Applicazione(conf.getNumeroMani());
            app.Start();                    
        }

    }
}
