package com.hc.wxstcdemo.utils;

import android.graphics.Bitmap;

import com.hc.wxstcdemo.bean.Coordinates;

import java.util.ArrayList;

public class TrafficUtils {
	public int judgecolor(int pixel) {
		int color = 0;
		int r = (pixel >> 16) & 0xff;
		int g = (pixel >> 8) & 0xff;
		int b = pixel & 0xff;
		if (r > 200 && g < 180 && b < 200) {// ��ɫ
			color = 1;
		} else if (r > 80 && g > 80 && b < 50) {// ��ɫ
			color = 2;
		}
		return color;
	}

	// // ����ͼƬ����������
	ArrayList<Coordinates> listx = new ArrayList<Coordinates>();
	ArrayList<Coordinates> listy = new ArrayList<Coordinates>();
	// // ����ͼƬ����������
	ArrayList<Coordinates> rlist = new ArrayList<Coordinates>();
	ArrayList<Coordinates> glist = new ArrayList<Coordinates>();
	// // ����ͼƬ��ߵ���������
	ArrayList<Coordinates> listl = new ArrayList<Coordinates>();
	// // ����ͼƬ���ұߵ���������
	ArrayList<Coordinates> listr = new ArrayList<Coordinates>();

	public Bitmap qiege(Bitmap bp) {
		listx.clear();
		listy.clear();
		int width = bp.getWidth();
		int height = bp.getHeight();
		int[] pixels = new int[width * height];
		bp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = pixels[y * width + x];
				int color = judgecolor(pixel);
				if (color == 1 || color == 2) {
					listx.add(new Coordinates(x, y));
					break;
				}
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];
				int color = judgecolor(pixel);
				if (color == 1 || color == 2) {
					listy.add(new Coordinates(x, y));
					break;
				}
			}
		}
		if (listx.size() == 0 || listy.size() == 0) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(bp, listx.get(0).getX(),
				listy.get(0).getY(), listx.get(listx.size() - 1).getX()
						- listx.get(0).getX(), listy.get(listy.size() - 1)
						.getY() - listy.get(0).getY());

		return bitmap;
	}

	public String  shapeIdentification(Bitmap bp,trafficListener listener) {
		this.listener=listener;
		if (bp == null) {
			if(listener!=null) {
				listener.traffic(0, 0, "");
			}
			return null;
		}
		rlist.clear();
		glist.clear();
		listl.clear();
		listr.clear();
		int width = bp.getWidth();
		int leftX = (int) (width * 0.3);
		height = bp.getHeight();
		int rightX = (int) (width * 0.7);
		int[] pixels = new int[width * height];
		bp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int y = 0; y < height; y++) {// �õ�������е�����
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				int pixel = pixels[offset + x];
				if (pixel != 0xff000000) {
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
					if (r > 200 && g < 180 && b < 200) {// ��ɫ
						rlist.add(new Coordinates(x, y));
						break;
					} else if (r > 80 && g > 80 && b < 50) {// ��ɫ
						glist.add(new Coordinates(x, y));
						break;
					}
				}
			}
		}
		for (int y = 0; y < height; y++) {
			int pixel = pixels[y * width + leftX];
			int color = judgecolor(pixel);
			if (color == 1 || color == 2) {
				listl.add(new Coordinates(leftX, y));
			}
		}
		for (int y = 0; y < height; y++) {
			int pixel = pixels[y * width + rightX];
			int color = judgecolor(pixel);
			if (color == 1 || color == 2) {
				listr.add(new Coordinates(rightX, y));
			}
		}
		if (rlist.size() > glist.size()) {
			shapeResult = shape(rlist, listl, listr, 1);
		} else if (glist.size() > rlist.size()) {
			shapeResult = shape(glist, listl, listr, 2);
		}
		if(listener!=null&&shapeResult!=null) {
			listener.traffic(listl.get(0).getY(), listr.get(0).getY(), shapeResult);
		}
		return shapeResult;
	}

	// ��ͨ�ƴ���
	String shapeResult = null;
	private int height;


	private String shape(ArrayList<Coordinates> list,
						 ArrayList<Coordinates> listl, ArrayList<Coordinates> listr, int sort) {
		String result = null;
		int index = list.size();// ���ص��ܸ߶�

		if (listl.size() == 0 || listr.size() == 0) {
			return null;
		}
		int lefty = listl.get(0).getY();


		int righty = listr.get(0).getY();


		if (index > 8) {
			if (sort == 1) {// ��ɫ
				if (lefty > righty) {// ��ͷ�ұ�
					return "��ͨ��Ϊ����ɫ���Ҽ�ͷ";
				} else if (lefty < righty) {
					return "��ͨ��Ϊ����ɫ�����ͷ";
				}
			} else if (sort == 2) {// ��ɫ
				if (Math.abs(lefty - righty) < 40) {
					return "��ͨ��Ϊ����ͷ";
				} else if (lefty > righty) {// ��ͷ�ұ�
					return "��ͨ��Ϊ����ɫ���Ҽ�ͷ";
				} else if (lefty < righty) {
					return "��ͨ��Ϊ����ɫ�����ͷ";
				}
			}
		} else {
			return null;
		}
		return result;
	}

	private trafficListener listener;
	public interface trafficListener {
		void traffic(int left,int right,String result);
	}

}




