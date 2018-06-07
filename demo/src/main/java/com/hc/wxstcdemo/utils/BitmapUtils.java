package com.hc.wxstcdemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

	public static Bitmap getSmallBitmap(Bitmap srcbitmap,float scale) {

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap bitmap = null;
		bitmap = Bitmap.createBitmap(srcbitmap, 0, 0, srcbitmap.getWidth(), srcbitmap.getHeight(), matrix, true);
		srcbitmap.recycle();
		srcbitmap = null;
		return bitmap;
	}

	// ����·�����ͼƬ��ѹ��������bitmap������ʾ
	public static Bitmap getSmallBitmap(File f) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(f.getAbsolutePath(), options);
		float scale = calculateInSampleSize(options.outHeight, options.outWidth);
		options.inJustDecodeBounds = false;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap bitmap = null;
		try {
			Bitmap srcbitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
			if (scale != 1.0f) {
				bitmap = Bitmap.createBitmap(srcbitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);
				srcbitmap.recycle();
				srcbitmap = null;
			} else {
				return srcbitmap;
			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			return null;
		}

		return bitmap;
	}

	// ����ͼƬ������??
	public static float calculateInSampleSize(int outHeight, int outWidth) {
		final int height = outHeight;
		final int width = outWidth;
		float scale = 1.0f;
		if (width < height) {
			if (width > 640) {
				scale = 640.0f / width;
			}
		} else {
			if (height > 640) {
				scale = 640.0f / height;
			}
		}

		return scale;
	}
	/**
	 * ��ȡsd����·��
	 *
	 * @return ·�����ַ���
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ���Ŀ¼
		} else {
			sdDir = Environment.getDataDirectory();
		}
		return sdDir.toString();
	}
	/**
	 * ����URI��ȡλͼ
	 *
	 * @param uri
	 * @return ��Ӧ��λͼ
	 */
	public static Bitmap decodeUriAsBitmap(Uri uri, Context context) {
		Bitmap bitmap = null;
		try {
			InputStream in=context.getContentResolver()
					.openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
