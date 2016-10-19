package orsettolavatore;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;

public class OrsettoLavatore2
{
    static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public static void main(String[] args) throws AWTException, IOException, InterruptedException
    {
        RoboScreen robot = null;
        int waitTime;
        
        if( args.length == 5 && (args[0].equals("armed") || args[0].equals("unarmed")) )
        {
            waitTime = Integer.parseInt(args[1]);
            robot = new RoboScreen( args[0].equals("armed"),
                                    ImageIO.read(new File(args[2])) );
            robot.setMouseTarget(Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        }
        else if( args.length == 7 && (args[0].equals("armed") || args[0].equals("unarmed")) )
        {
            waitTime = Integer.parseInt(args[1]);
            robot = new RoboScreen( args[0].equals("armed"),
                                    ImageIO.read(new File(args[2])),
                                    Integer.parseInt(args[5]),
                                    Integer.parseInt(args[6]) );
            robot.setMouseTarget(Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        }
        else
        {
            log("Parametri errati: {armed|unarmed} waitTime(ms) pngFile (mouseTarget)x y [target coordinates][x y]");
            log("example: armed 800 target.png 15 15 ");
            log("example: armed 800 target.png 15 15 245 258");
            System.exit(0);
        }
        
        log("OrsettoLavatore started.");
        log("Status: " + (robot.isArmed() ? "ARMED" : "NOT ARMED"));

        if( !robot.isLockedOnTarget() )
        {
            log("Quando compare 0 secondi premi invio per trovare la posizione del target...");
            while( !robot.isLockedOnTarget() )
            {
                Scanner consoleInput = new Scanner(System.in);
                consoleInput.nextLine();
                log("... sto cercando...");

                if( !robot.lockOnTarget() ) log("... niente da fare, riprova...");
            }
        }
        
        Rectangle decimalRectangle =
                new Rectangle( (int)robot.getTargetRectangle().getWidth(), (int)robot.getTargetRectangle().getHeight(), 32, 15);
        
        log("Locked on target!");
        log("Target at Rectangle " + robot.getTargetRectangle());
        
        long cicles = 0;
        
        long timeA = System.currentTimeMillis();
        long timeB;
        int file = 0;
        
        for(;;cicles++)
        {
            if( cicles % 1000 == 0 )
            {   
                timeB = System.currentTimeMillis();
                log("1000 cicles passed in " + (timeB - timeA)/1000 + " ms mean; clicks = " + robot.getClickCount());
                timeA = System.currentTimeMillis();
            }
            
            if( robot.check() )
            {
                robot.moveMouseOnTarget();
                
                log("Less than 1 sec remaining! waiting for 800 ms..." );
                
                Thread.sleep(800);
                
                if( robot.check() )
                {
                    log("... still less than 1 sec remaining after 800 ms!" );

                    if ( robot.mouseClick() )
                        log("CLICKED because ARMED!!!");
                    else
                        log("NOT clicked because NOT armed!!!");
                    
                    ImageIO.write(robot.getScreeShot(decimalRectangle), "png", new File("captured" + file++ + ".png"));
                    
                    log("After click event I'm going to sleep for 8 seconds.");
                    
                    Thread.sleep(8000);
                }
                else log("All good... :-P I didn't click! Some one else did ]:-D");
                
                cicles = 0;
                timeA = System.currentTimeMillis();
            }
        }
    }
    
    private static void log(String txt)
    {
        System.out.println("[" + dateFormat.format(new Date()) + "] " + txt);
    }
}