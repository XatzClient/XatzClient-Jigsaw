package me.xatzdevelopments.xatz.client.gui.tab;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import me.xatzdevelopments.xatz.client.tools.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class TabGuiContainer extends TabGuiItem {
	
	 public static float out;
     public static int col;
     public static  float out1;
     public static float out2;
     public static float out3;
	private ArrayList<TabGuiItem> items = new ArrayList<TabGuiItem>();
	
	public int selectedIndex = 0;
	
	public void layoutItems() {
		int y = 0;
		for(TabGuiItem item : items) {
			item.x = this.x + 1;
			item.y = this.y + y + 1;
			y += item.height;
		}
	}
	
	public ArrayList<TabGuiItem> getItems() {
		return items;
	}
	
	public void addItem(TabGuiItem item) {
		item.parent = this;
		item.update();
		if(item instanceof TabGuiTitle) {
			TabGuiTitle tgt = (TabGuiTitle)item;
			if(tgt.hasnextContainer() && tgt.nextContainer.items.isEmpty()) {
				return;
			}
		}
		items.add(item);
	}
	
	public void removeItem(TabGuiItem item) {
		items.remove(item);
	}

	@Override
	public int getWidth() {
		int maxWidth = 35;
		for(TabGuiItem item : items) {
			if(item.getWidth() + 2 > maxWidth) {
				maxWidth = item.getWidth() + 2;
			}
		}
		return maxWidth;
	}


	@Override
	public int getHeigth() {
		int height = 0;
		for(TabGuiItem item : items) {
			height += item.getHeigth();
		}
		return height + 2;
	}
	
	@Override
	public void update() {
		super.update();
		for(TabGuiItem item : items) {
			item.selected = false;
		}
		if(!items.isEmpty()) {
			items.get(selectedIndex).selected = true;
		}
		for(TabGuiItem item : items) {
			item.update();
		}
		layoutItems();
	}
	
	@Override
	public void render() {
		int cxd = 0;
		
		Color color = new Color(0,255,0 , (int)255);
        col = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255).getRGB();
        cxd += 3;
        
     out=0;
       out1=1;
        out2=0;
        out3=0.96f;
		super.render();
		if(TabGui.novitex) {
			for(TabGuiItem item : items) {
				item.render();
			}
		}
		
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		
		if(TabGui.novitex) {
			GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.3f);
			 
		}
		
	
		GL11.glBegin(GL11.GL_QUADS);
		{
			
			GL11.glVertex2d(x, y );
			GL11.glVertex2d(x , y + height - 1 );
			GL11.glVertex2d(x + width , y + height -1);
			GL11.glVertex2d(x + width, y );
		}
		GL11.glEnd();
		
	
		
		if(!TabGui.novitex) {
			 
			GL11.glColor4f(out, out1, out2, 255f);
			GL11.glLineWidth(1f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			{
				GL11.glVertex2d(x, y );
				GL11.glVertex2d(x, y + height - 1);
				GL11.glVertex2d(x + width, y + height  - 1);
				GL11.glVertex2d(x + width, y );
			}
			GL11.glEnd();
			
			
		}
		
		if(!TabGui.novitex) {
			for(TabGuiItem item : items) {
				item.render();
				
			}
		}
	}
	
	@Override
	public void setValues() {
		super.setValues();
		for(TabGuiItem item : items) {
			item.setValues();
		}
	}
	
	public boolean isOneMaximised() {
		for(TabGuiItem item : getItems()) {
			if(item.maximised) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void keyPressed(int keyCode) {
		if(selectedIndex < 0) {
			selectedIndex = 0;
		}
		boolean didMaximise = false;
		if(!isOneMaximised()) {
			if (keyCode == Keyboard.KEY_UP) {
				selectedIndex--;
			}
			if (keyCode == Keyboard.KEY_DOWN) {
				selectedIndex++;
			}
			if (keyCode == Keyboard.KEY_RIGHT) {
				items.get(selectedIndex).maximised = true;
				didMaximise = true;
			}
		}
		if (keyCode == Keyboard.KEY_LEFT) {
			items.get(selectedIndex).maximised = false;
		}
		if(selectedIndex < 0) {
			selectedIndex = 0;
		}
		if(selectedIndex > getItems().size() - 1) {
			selectedIndex = getItems().size() - 1;
		}
		if(!didMaximise) {
			for(TabGuiItem item : getItems()) {
				if(item.maximised && item instanceof TabGuiTitle && ((TabGuiTitle)item).hasnextContainer()) {
					((TabGuiTitle)item).nextContainer.keyPressed(keyCode);
				}
			}
		}
		super.keyPressed(keyCode);
	}
	
}
