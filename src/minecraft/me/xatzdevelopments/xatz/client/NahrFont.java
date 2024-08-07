package me.xatzdevelopments.xatz.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class NahrFont
{
  private BufferedImage bufferedImage;
  private DynamicTexture dynamicTexture;
  private final int endChar;
  private float extraSpacing;
  private final float fontSize;
  private final Pattern patternControlCode;
  private final Pattern patternUnsupported;
  private ResourceLocation resourceLocation;
  private final int startChar;
  private Font theFont;
  private Graphics2D theGraphics;
  private FontMetrics theMetrics;
  private final float[] xPos;
  private final float[] yPos;
  
  public NahrFont(Object font, float size)
  {
    this(font, size, 0.0F);
  }
  
  public NahrFont(Object font, float size, float spacing)
  {
    this.extraSpacing = 0.0F;
    this.patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OG]");
    this.patternUnsupported = Pattern.compile("(?i)\\u00A7[K-O]");
    this.fontSize = size;
    this.startChar = 32;
    this.endChar = 255;
    this.extraSpacing = spacing;
    this.xPos = new float[this.endChar - this.startChar];
    this.yPos = new float[this.endChar - this.startChar];
    setupGraphics2D();
    createFont(font, size);
  }
  
  private void createFont(Object font, float size)
  {
    try
    {
      if ((font instanceof Font)) {
        this.theFont = ((Font)font);
      } else if ((font instanceof File)) {
        this.theFont = Font.createFont(0, (File)font).deriveFont(size);
      } else if ((font instanceof InputStream)) {
        this.theFont = Font.createFont(0, (InputStream)font).deriveFont(size);
      } else if ((font instanceof String)) {
        this.theFont = new Font((String)font, 0, Math.round(size));
      } else {
        this.theFont = new Font("Verdana", 0, Math.round(size));
      }
      this.theGraphics.setFont(this.theFont);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      this.theFont = new Font("Verdana", 0, Math.round(size));
      this.theGraphics.setFont(this.theFont);
    }
    this.theGraphics.setColor(new Color(255, 255, 255, 0));
    this.theGraphics.fillRect(0, 0, 256, 256);
    this.theGraphics.setColor(Color.white);
    this.theMetrics = this.theGraphics.getFontMetrics();
    float x = 5.0F;
    float y = 5.0F;
    for (int i = this.startChar; i < this.endChar; i++)
    {
      this.theGraphics.drawString(Character.toString((char)i), x, y + this.theMetrics.getAscent());
      this.xPos[(i - this.startChar)] = x;
      this.yPos[(i - this.startChar)] = (y - this.theMetrics.getMaxDescent());
      x += this.theMetrics.stringWidth(Character.toString((char)i)) + 2.0F;
      if (x >= 250 - this.theMetrics.getMaxAdvance())
      {
        x = 5.0F;
        y += this.theMetrics.getMaxAscent() + this.theMetrics.getMaxDescent() + this.fontSize / 2.0F;
      }
    }
    TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    String string = "font" + font.toString() + size;
    DynamicTexture dynamicTexture = new DynamicTexture(this.bufferedImage);
    this.dynamicTexture = dynamicTexture;
    this.resourceLocation = textureManager.getDynamicTextureLocation(string, dynamicTexture);
  }
  
  private void drawChar(char character, float x, float y)
    throws ArrayIndexOutOfBoundsException
  {
    Rectangle2D bounds = this.theMetrics.getStringBounds(Character.toString(character), this.theGraphics);
    drawTexturedModalRect(x, y, this.xPos[(character - this.startChar)], this.yPos[(character - this.startChar)], (float)bounds.getWidth(), (float)bounds.getHeight() + this.theMetrics.getMaxDescent() + 1.0F);
  }
  
  private void drawer(String text, float x, float y, int color)
  {
    x *= 2.0F;
    y *= 2.0F;
    GL11.glEnable(3553);
    Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLocation);
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GL11.glColor4f(red, green, blue, alpha);
    float startX = x;
    for (int i = 0; i < text.length(); i++) {
      if ((text.charAt(i) == '�') && (i + 1 < text.length()))
      {
        char oneMore = Character.toLowerCase(text.charAt(i + 1));
        if (oneMore == 'n')
        {
          y += this.theMetrics.getAscent() + 2;
          x = startX;
        }
        int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
        if (colorCode < 16) {
          try
          {
            int newColor = Minecraft.getMinecraft().fontRendererObj.colorCode[colorCode];
            GL11.glColor4f((newColor >> 16) / 255.0F, (newColor >> 8 & 0xFF) / 255.0F, (newColor & 0xFF) / 255.0F, alpha);
          }
          catch (Exception exception)
          {
            exception.printStackTrace();
          }
        } else if (oneMore == 'f') {
          GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        } else if (oneMore == 'r') {
          GL11.glColor4f(red, green, blue, alpha);
        } else if (oneMore == 'g') {
          GL11.glColor4f(0.3F, 0.7F, 1.0F, alpha);
        }
        i++;
      }
      else
      {
        try
        {
          char c = text.charAt(i);
          drawChar(c, x, y);
          x += getStringWidth(Character.toString(c)) * 2.0F;
        }
        catch (ArrayIndexOutOfBoundsException indexException)
        {
          text.charAt(i);
        }
      }
    }
  }
  
  public void drawString(String text, float x, float y, FontType fontType, int color, int color2)
  {
    text = stripUnsupported(text);
    GL11.glEnable(3042);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    String text2 = stripControlCodes(text);
    switch (fontType.ordinal())
    {
    case 4: 
      drawer(text2, x + 1.0F, y + 1.0F, color2);
      break;
    case 5: 
      drawer(text2, x + 0.5F, y + 0.5F, color2);
      break;
    case 3: 
      drawer(text2, x + 0.5F, y, color2);
      drawer(text2, x - 0.5F, y, color2);
      drawer(text2, x, y + 0.5F, color2);
      drawer(text2, x, y - 0.5F, color2);
      break;
    case 2: 
      drawer(text2, x, y + 0.5F, color2);
      break;
    case 1: 
      drawer(text2, x, y - 0.5F, color2);
    }
    drawer(text, x, y, color);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
  }
  
  private void drawTexturedModalRect(float x, float y, float u, float v, float width, float height)
  {
    float scale = 0.0039063F;
    WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
    Tessellator tessellator = Tessellator.getInstance();
    worldRenderer.startDrawingQuads();
    worldRenderer.addVertexWithUV(x + 0.0F, y + height, 0.0D, (u + 0.0F) * 0.0039063F, (v + height) * 0.0039063F);
    worldRenderer.addVertexWithUV(x + width, y + height, 0.0D, (u + width) * 0.0039063F, (v + height) * 0.0039063F);
    worldRenderer.addVertexWithUV(x + width, y + 0.0F, 0.0D, (u + width) * 0.0039063F, (v + 0.0F) * 0.0039063F);
    worldRenderer.addVertexWithUV(x + 0.0F, y + 0.0F, 0.0D, (u + 0.0F) * 0.0039063F, (v + 0.0F) * 0.0039063F);
    tessellator.draw();
  }
  
  private Rectangle2D getBounds(String text)
  {
    return this.theMetrics.getStringBounds(text, this.theGraphics);
  }
  
  public Font getFont()
  {
    return this.theFont;
  }
  
  private String getFormatFromString(String par0Str)
  {
    String var1 = "";
    int var2 = -1;
    int var3 = par0Str.length();
    while ((var2 = par0Str.indexOf('�', var2 + 1)) != -1) {
      if (var2 < var3 - 1)
      {
        char var4 = par0Str.charAt(var2 + 1);
        if (isFormatColor(var4)) {
          var1 = "�" + var4;
        } else if (isFormatSpecial(var4)) {
          var1 = String.valueOf(var1) + "�" + var4;
        }
      }
    }
    return var1;
  }
  
  public Graphics2D getGraphics()
  {
    return this.theGraphics;
  }
  
  public float getStringHeight(String text)
  {
    return (float)getBounds(text).getHeight() / 2.0F;
  }
  
  public float getStringWidth(String text)
  {
    return (float)(getBounds(text).getWidth() + this.extraSpacing) / 2.0F;
  }
  
  private boolean isFormatColor(char par0)
  {
    return ((par0 >= '0') && (par0 <= '9')) || ((par0 >= 'a') && (par0 <= 'f')) || ((par0 >= 'A') && (par0 <= 'F'));
  }
  
  private boolean isFormatSpecial(char par0)
  {
    return ((par0 >= 'k') && (par0 <= 'o')) || ((par0 >= 'K') && (par0 <= 'O')) || (par0 == 'r') || (par0 == 'R');
  }
  
  public List listFormattedStringToWidth(String s, int width)
  {
    return Arrays.asList(wrapFormattedStringToWidth(s, width).split("\n"));
  }
  
  private void setupGraphics2D()
  {
    this.bufferedImage = new BufferedImage(256, 256, 2);
    (this.theGraphics = (Graphics2D)this.bufferedImage.getGraphics()).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  }
  
  private int sizeStringToWidth(String par1Str, float par2)
  {
    int var3 = par1Str.length();
    float var4 = 0.0F;
    int var5 = 0;
    int var6 = -1;
    boolean var7 = false;
    while (var5 < var3)
    {
      char var8 = par1Str.charAt(var5);
      switch (var8)
      {
      case '\n': 
        var5--;
        break;
      case '�': 
        if (var5 < var3 - 1)
        {
          var5++;
          char var9 = par1Str.charAt(var5);
          if ((var9 == 'l') || (var9 == 'L')) {
            var7 = true;
          } else if ((var9 == 'r') || (var9 == 'R') || (isFormatColor(var9))) {
            var7 = false;
          }
        }
        break;
      case ' ': 
        var6 = var5;
      case '-': 
        var6 = var5;
      case '_': 
        var6 = var5;
      case ':': 
        var6 = var5;
      default: 
        String text = String.valueOf(var8);
        var4 += getStringWidth(text);
        if (var7) {
          var4 += 1.0F;
        }
        break;
      }
      if (var8 == '\n')
      {
        var5++;var6 = var5;
      }
      else
      {
        if (var4 > par2) {
          break;
        }
      }
      var5++;
    }
    return (var5 != var3) && (var6 != -1) && (var6 < var5) ? var6 : var5;
  }
  
  public String stripControlCodes(String s)
  {
    return this.patternControlCode.matcher(s).replaceAll("");
  }
  
  public String stripUnsupported(String s)
  {
    return this.patternUnsupported.matcher(s).replaceAll("");
  }
  
  public String wrapFormattedStringToWidth(String s, float width)
  {
    int wrapWidth = sizeStringToWidth(s, width);
    if (s.length() <= wrapWidth) {
      return s;
    }
    String split = s.substring(0, wrapWidth);
    String split2 = String.valueOf(getFormatFromString(split)) + s.substring(wrapWidth + ((s.charAt(wrapWidth) == ' ') || (s.charAt(wrapWidth) == '\n') ? 1 : 0));
    try
    {
      return String.valueOf(split) + "\n" + wrapFormattedStringToWidth(split2, width);
    }
    catch (Exception e)
    {
      System.out.println("Cannot wrap string to width.");
    }
    return "";
  }
  
  public static enum FontType
  {
    EMBOSS_BOTTOM("EMBOSS_BOTTOM", 0),  EMBOSS_TOP("EMBOSS_TOP", 1),  NORMAL("NORMAL", 2),  OUTLINE_THIN("OUTLINE_THIN", 3),  SHADOW_THICK("SHADOW_THICK", 4),  SHADOW_THIN("SHADOW_THIN", 5);
    
    private FontType(String s, int n) {}
  }
}
