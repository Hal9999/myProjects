using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace VisulPoker
{
    public partial class ConfiguraGioco : Form
    {
        
        int mani = 1000000;

        public ConfiguraGioco()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {                  
            //Chiude finestra dialogo
            this.Dispose();            
        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {                       
            try
            {
                //Prende come parametro solo numeri interi
                if(textBox1.Text != "")
                mani = int.Parse(textBox1.Text);               
            }
            catch (Exception)
            {                
                MessageBox.Show("Inserire solo numeri interi");
                textBox1.Clear();
            }
        }

        public int getNumeroMani()
        {
            return mani;
        }
    }
}
