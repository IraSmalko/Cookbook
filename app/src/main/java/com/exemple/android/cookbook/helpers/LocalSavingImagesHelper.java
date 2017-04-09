package com.exemple.android.cookbook.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.exemple.android.cookbook.entity.Ingredient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Sakurov on 05.04.2017.
 */

public abstract class LocalSavingImagesHelper {

    public static String getPathForNewPhoto(String name, Bitmap photo, Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("Images", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File filePath = new File(directory, name + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return Uri.fromFile(filePath).toString();
    }

    public static String getDescriptionOfRecipeToShare(String recipeName, List<Ingredient> ingredients) {
        String description = recipeName + "\n \n Інгредієнти:";
        for (Ingredient ingredient : ingredients) {
            description += "\n" + "- " + ingredient.getName() + " " + ingredient.getQuantity() + ingredient.getUnit();
        }
        return description;
    }
}
