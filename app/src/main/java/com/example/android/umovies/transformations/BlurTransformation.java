package com.example.android.umovies.transformations;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import com.example.android.umovies.MainActivity;
import com.squareup.picasso.Transformation;

// ************************************************************** //
// *** Basic BlurTransformer for Square's Picasso *** //
// *** Source: https://gist.github.com/ryanbateman/6667995.js *** //
// ************************************************************** //


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class BlurTransformation implements Transformation {
    private RenderScript rs;
    private int radius;
    private long startTime;

    public BlurTransformation(Context context, int radius) {
        super();
        rs = RenderScript.create(context);
        this.radius = radius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        startTime = System.currentTimeMillis();

        // Create another bitmap that will hold the results of the filter.
        Bitmap blurredBitmap = Bitmap.createBitmap(source);

        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);

        // Set the blur radius
        script.setRadius(radius);

        // Start the ScriptIntrinisicBlur
        script.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap);

        source.recycle();

        Log.v(MainActivity.TAG, "Log - Blur Running Time: " + (System.currentTimeMillis() - startTime) + "ms");
        return blurredBitmap;
    }

    @Override
    public String key() {
        return "blur";
    }
}
