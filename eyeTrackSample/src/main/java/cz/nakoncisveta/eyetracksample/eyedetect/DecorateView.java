package cz.nakoncisveta.eyetracksample.eyedetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by zli on 12/11/16.
 */

public class DecorateView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder;
	private int width, height, cameraWidth, cameraHeight, dx;
	private double ratio;
	private Bitmap decoration;
	private boolean isCanvasClear;

	private static final double SCALE = 1.3;

	public DecorateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.width = width;
		this.height = height;
		prepareTransform();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public void setCameraSize(int width, int height) {
		this.cameraWidth = width;
		this.cameraHeight = height;
		prepareTransform();
	}

	private void prepareTransform() {
		if (this.width == 0 || this.height == 0 || cameraWidth == 0 || cameraHeight == 0) {
			return;
		}
		this.ratio = (double) this.height / cameraHeight;
		this.dx = (int) (this.width - cameraWidth * ratio) / 2;
	}

	public void readDecoration(int id) {
		decoration = BitmapFactory.decodeResource(getResources(), id);
	}

	public void clearCanvas() {
		if (!isCanvasClear) {
			isCanvasClear = true;
			Canvas canvas = surfaceHolder.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	public Bitmap getResizedBitmap(Bitmap bitmap, Rect edge) {
		int width = (int) (edge.width() * ratio * SCALE);
		return Bitmap.createScaledBitmap(bitmap, width, width, false);
	}

	public void drawDecoration(Rect edge) {
		if (decoration != null) {
			isCanvasClear = false;
			Canvas canvas = surfaceHolder.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			Paint p = new Paint();
			p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
			Bitmap targetBitmap = getResizedBitmap(decoration, edge);
			int shift = (int) (targetBitmap.getWidth() / (ratio * 2));
			int targetX = getTransformX((edge.left + edge.width() / 2) - shift);
			int targetY = getTransformY((edge.top + edge.height() / 2) - shift);
			canvas.drawBitmap(targetBitmap,
					targetX,
					targetY,
					p);
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	private int getTransformX(int cameraX) {
		return (int) (cameraX * ratio + dx);
	}

	private int getTransformY(int cameraY) {
		return (int) (cameraY * ratio);
	}
}
