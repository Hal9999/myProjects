package orsettolavatore;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.*;

public class RoboScreen
{
    private Robot robot;
    private boolean lockedOnTarget = false;
    
    private boolean armed;
    private BufferedImage target;
    
    private Rectangle targetRectangle = null;
    private int mouseTargetX = 0, mouseTargetY = 0;
    
    private int clickCount = 0;
    
    public RoboScreen(boolean armed, BufferedImage target) throws AWTException
    {
        this.robot = new Robot();
        
        this.armed = armed;
        this.target = target;
    }

    public RoboScreen(boolean armed, BufferedImage target, int x, int y) throws AWTException
    {
        this.robot = new Robot();
        
        this.armed = armed;
        this.target = target;
        
        targetRectangle = new Rectangle(x, y, target.getWidth(), target.getHeight());
        this.lockedOnTarget = true;
    }
    
    public boolean lockOnTarget()
    {
        targetRectangle = searchSubImage( getScreeShot() , target);
        
        return lockedOnTarget =  targetRectangle != null;
    }
    
    public boolean check()
    {
        return targetRectangle!= null && checkEquality(target, robot.createScreenCapture(targetRectangle));
    }
    
    public Rectangle getTargetRectangle()
    {
        return targetRectangle;
    }
    
    public int getClickCount()
    {
        return clickCount;
    }
    
    public boolean isLockedOnTarget()
    {
        return lockedOnTarget;
    }
    
    public BufferedImage getScreeShot()
    {
        return robot.createScreenCapture(new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() ));
    }
    
    public BufferedImage getScreeShot(Rectangle rectangle)
    {
        return robot.createScreenCapture( rectangle );
    }
    
    public void setMouseTarget(int x, int y)
    {
        this.mouseTargetX = x;
        this.mouseTargetY = y;
    }
    
    public void mouseMove(int x, int y)
    {
        robot.mouseMove(x, y);
    }
    
    public void moveMouseOnTarget()
    {
        mouseMove(mouseTargetX, mouseTargetY);
    }
    
    public boolean mouseClick()
    {
        if( armed )
        {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }
        
        clickCount++;
        return armed;
    }
    
    public boolean mouseClick(int x, int y)
    {
        mouseMove(x, y);
        
        return mouseClick();
    }
    
    public boolean isArmed()
    {
        return armed;
    }
    
    public static Rectangle searchSubImage(BufferedImage base, BufferedImage target)
    {
        int baseWidth = base.getWidth();
        int baseHeight = base.getHeight();
        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();
        
        //System.out.println(baseWidth + ", " + baseHeight + ", " + targetWidth + ", " + targetHeight);
        
        for(int h = 0; h <= baseHeight - targetHeight; h++)
        for(int w = 0; w <= baseWidth  -  targetWidth; w++)
        if( checkEquality( base.getSubimage(w, h, targetWidth, targetHeight), target ) )
            return new Rectangle(w, h, targetWidth, targetHeight);
        
        return null;
    }
    
    public static boolean checkEquality(BufferedImage a, BufferedImage b)
    {
        for(int i = 0; i <  a.getWidth(); i++)
        for(int j = 0; j < a.getHeight(); j++)
        if( a.getRGB(i, j) != b.getRGB(i, j) ) return false;
        
        return true;
    }

}