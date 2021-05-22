package com.example.cmotoemployee;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import com.squareup.picasso.Transformation;

public class RoundedTransformation implements Transformation {
    private final int margin;

    private final int radius;

    public RoundedTransformation(int paramInt1, int paramInt2) {
        this.radius = paramInt1;
        this.margin = paramInt2;
    }

    public String key() {
        return "rounded";
    }

    public Bitmap transform(Bitmap paramBitmap) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader((Shader)new BitmapShader(paramBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        Bitmap bitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int i = this.margin;
        RectF rectF = new RectF(i, i, (paramBitmap.getWidth() - this.margin), (paramBitmap.getHeight() - this.margin));
        i = this.radius;
        canvas.drawRoundRect(rectF, i, i, paint);
        if (paramBitmap != bitmap)
            paramBitmap.recycle();
        return bitmap;
    }
}

