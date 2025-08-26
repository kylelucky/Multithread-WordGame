package termproject;

import java.awt.Image;
import javax.swing.*;

public abstract class Character implements moveable
{
    public int width;
    public int height;

    public int x;
    public int y;

    public int id;
    
    public ImageIcon imgicon; 
    public Image img;

    public int count;
    
    public int start_x;
    public int start_y;
    
    public int current_x;
    public int current_y;
    
    public Character(String path, int width, int height, int id, int count, int start_x, int start_y)
    {
        this. width = width;
        this. height = height;
        
        this.id = id;
        this.count = count;
        
        this.start_x = start_x;
        this.start_y = start_y;
        
        imgicon = new ImageIcon(path); 
        img = imgicon.getImage();
    }
    
    public int update_x()
    {
    	return current_x;
    }
    public int update_y()
    {
    	return current_y;
    }
    
    public Image getimg() 
    {
    	return img;
    	
    }
}

class Professor extends Character
{
    public Professor(String path, int width, int height, int id, int count, int start_x, int start_y)
    {
        super(path, width, height, id, count, start_x, start_y);
    }

    //update()를 호출하면 
    public int update_x()
    {
    	if (id == 0)
    	{
    		current_x = start_x + 9*count;
    	}
    	
    	else if ( id == 1)
    	{
    		current_x = start_x - 9*count;
    	}
    	
    	return current_x;
    }
    
    public int update_y()
    {
    	current_y = start_y + 7*count;
    	return current_y;
              // y증가
             // x * (id 0:-1, 1: +1 
    }
    
    public Image getimg() 
    {
    	return img;
    	
    }
}

class Player extends Character
{
    public Player(String path, int width, int height, int id, int count, int start_x, int start_y)
    {
        super(path, width, height, id, count, start_x, start_y);
    }

    //update()를 호출하면 
    public int update_x()
    {
    	if (id == 0)
    	{
    		current_x = start_x + 9*count;
    	}
    	
    	else if (id == 1)
    	{
    		current_x = start_x - 9*count;
    	}
    	
    	return current_x;
    }
    
    public int update_y()
    {
    	current_y = start_y - 7*count;
    	return current_y;
              // y증가
             // x * (id 0:-1, 1: +1 
    }
    
    public Image getimg() 
    {
    	return img;
    	
    }
}

interface moveable
{
    public int update_x();
    public int update_y();
}