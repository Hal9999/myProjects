package orsettolavatore;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;

public class OrsettoLavatore
{
    public static void main(String[] args) throws AWTException, IOException, InterruptedException
    {
        boolean armed = false;
        
        if( args.length != 0 && args[0].equalsIgnoreCase("armed") ) armed = true;
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Rectangle rectangle = new Rectangle(751, 353, 12, 15);
        Rectangle decimalRectangle = new Rectangle(751, 353, 32, 15);
        
        System.out.println("[" + dateFormat.format(new Date()) + "] " + "OrsettoLavatore started.");
        System.out.println("[" + dateFormat.format(new Date()) + "] Status: " + (armed ? "ARMED" : "NOT ARMED"));
        
        int clickCounter = 0;
        
        BufferedImage target = ImageIO.read(new File("../base.png"));
        
        
        Robot robot = new Robot();
        
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        boolean locked = false;
        while( !locked )
        {
            BufferedImage screen =
                    robot.createScreenCapture(new Rectangle( screenSize ));
            
            for(int l = 0; (l <= screenSize.width - target.getWidth()) && !locked; l++)
                for(int h=0; (h <= screenSize.height - target.getHeight()) && !locked; h++)
                    if( checkEquality(screen.getSubimage(l, h, target.getWidth(), target.getHeight()), target))
                    {
                        locked=true;
                        System.out.println("Found at position (" + l + ", " + h + ")");
                        rectangle = new Rectangle(l, h, 12, 15);
                        decimalRectangle = new Rectangle(l, h, 32, 15);
                    }
        }
        System.out.println("Locked!!!");
        
        long cicles = 0;
        
        long timeA = System.currentTimeMillis();
        long timeB;
        int file = 0;
        
        while(true)
        {
            cicles++;
            
            //BufferedImage screenCapture = robot.createScreenCapture(rectangle);

            //File captured = new File("../captured" + i + ".png");
           // ImageIO.write(screenCapture, "png", captured);

//            checkEquality(base, screenCapture);
            
            //boolean equality = checkEquality(base, robot.createScreenCapture(rectangle));
            
            
            if( cicles % 1000 == 0 )
            {   
                timeB = System.currentTimeMillis();
                System.out.println( "[" + dateFormat.format(new Date()) +
                        "] 1000 cicles passed in " + (timeB - timeA)/1000 + " ms medium and clicks = " + clickCounter);
                timeA = System.currentTimeMillis();
            }
            
            if( checkEquality(target, robot.createScreenCapture(rectangle)) )
            {
                robot.mouseMove(766, 445);
                
                System.out.println("[" + dateFormat.format(new Date()) + "] Less than 1 sec remaining!" );
                
                Thread.sleep(800);
                
                if( checkEquality(target, robot.createScreenCapture(rectangle)) )
                {
                    
                    if( armed )
                    {
                        //ARMED!!!
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        //ARMED!!!

                        System.out.println("[" + dateFormat.format(new Date()) + "] CLICKED because ARMED!!!");
                    }
                    
                    ImageIO.write(robot.createScreenCapture(decimalRectangle), "png", new File("captured" + file++ + ".png"));
                    
                    System.out.println("[" + dateFormat.format(new Date()) + "] Still less than 1 sec remaining after 800 ms!" );
                    clickCounter++;
                    
                    System.out.println("[" + dateFormat.format(new Date()) + "] Clicked! Sleeping 8 seconds...");
                    
                    Thread.sleep(8000);
                }
                else System.out.println("[" + dateFormat.format(new Date()) + "] All good... :-P Didn't click!");
                
                
                cicles = 0;
                timeA = System.currentTimeMillis();
            }

        }
    }
    
    private static boolean checkEquality(BufferedImage a, BufferedImage b)
    {
        for (int i = 0; i < a.getWidth(); i++)
            for (int j = 0; j < a.getHeight(); j++)
                if( a.getRGB(i, j) != b.getRGB(i, j) )
                    return false;
        return true;
    }
}