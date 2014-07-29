package MainPack;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PreProcessing {
	int width, height;
	int pixels[][];
	
	public String SelectImage()
	{
		File f=null;
        String path="";
        JFileChooser fileChooser=new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, GIF, & PNG Images", "jpg", "gif", "png");
	fileChooser.setFileFilter(filter);
        int kullaniciSecimi = fileChooser.showOpenDialog(null);
        if (kullaniciSecimi == JFileChooser.APPROVE_OPTION) 
        {
            f=fileChooser.getSelectedFile();
            path=(f.getAbsolutePath());
        }
        return path;
	}
	
	public void GetImage()
	{
		String Path=SelectImage();
		BufferedImage Imgin;
		try {
			Imgin=ImageIO.read(new File(Path));
			ImgToArray(Imgin);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int[] MatrixToArray(int[][] pmatrix)
	{
		int[] pix=new int[width*height];
		for(int i=0; i<height; i++)
			for(int j=0; j<width; j++)
			{
				pix[i*width+j]=pmatrix[i][j];
			}
		return pix;
	}
	
	public void ImgToArray(BufferedImage im)
	{
		width=im.getWidth(null);
        height=im.getHeight(null);
        pixels=new int[height][width];
        for(int i=0; i<im.getHeight(); i++)
        	for(int j=0; j<im.getWidth(); j++)
        		pixels[i][j]=im.getRGB(j,i);
		Binarization(pixels);
	}
	
	public void PaintRow(int[][] px, int y)
	{
		for(int j=0; j<width; j++)
		{
			px[y][j]=0x0000ff;
		}
	}
	
	public void FindRow(int[][] px)
	{
		boolean isrow=true;
		boolean top=true, bottom=false;
		int sayac=0;
		for(int i=0; i<height; i++)
		{
			for(int j=0; j<width; j++)
			{
				if(((px[i][j])&(0xff))==0) 
				{
					sayac++;
				}
			}
			if(sayac>0&&(!bottom)&&(!top)) bottom=true;
			if(top||(sayac==0)) PaintRow(px, i);
			if(top)
			{
				if(sayac>0)
				{
					System.out.println(sayac);
					PaintRow(px, i-3);
					top=false;
					bottom=false;
					sayac=0;
				}
			}
			else if(bottom)
			{
				if(sayac==0)
				{
					PaintRow(px, i+3);
					top=true;
					bottom=false;
					sayac=0;
				}
			}
			sayac=0;
		}
	}
	
	public void PaintColumn(int[][] px, int x )
	{
		for(int i=0; i<height; i++)
			px[i][x]=0xff000000;
	}
	
	public void FindColumn(int[][] px)
	{
		boolean iscolumn=true;
		for(int j=0; j<width; j++)
		{
			for(int i=0; i<height; i++)
			{
				if(((px[i][j])&(0xff))==0||(px[i][j]==0x0000ff))
				{
					iscolumn=false;
					break;
				}
			}
			if(iscolumn)
			{
				PaintColumn(px, j);
			}
			iscolumn=true;
		}
	}
	
	public void Binarization(int[][] px)
	{
		int r,g,b;
        int a;
        for(int i=0; i<height; i++)
        	for(int j=0; j< width; j++)
        	{
        		r=0;g=0;b=0;a=0;
        		a=px[i][j];
        		r=((0xFF)&(a>>16));
        		g=((0xFF)&(a>>8));
        		b=((0xFF)&(a));
        		a=(int)(r+g+b)/3;
        		if(a<150) a=0;
        		else a=255;
        		a=((0xff000000) | (a<<16) | (a<<8) |(a));
        		px[i][j]=a;
        	}
        FindRow(px);
        //FindColumn(px);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) image.getData();
        image.getWritableTile(0, 0).setDataElements(0, 0, width, height, MatrixToArray(pixels)); 
        try {
			ImageIO.write(image, "jpg", new File("c://abc.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
