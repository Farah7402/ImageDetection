package com.farah.imagedetectionturtorialhellocode;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;

import com.google.ar.core.Session;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;

import com.google.ar.sceneform.FrameTime;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;

import com.google.ar.sceneform.ux.TransformableNode;


import java.io.IOException;
import java.io.InputStream;

import java.util.Collection;


public class MainActivity extends AppCompatActivity {


    ArFragment arFragment;
    boolean shouldAddModel1 = true;
    boolean shouldAddModel2=true;
    boolean shouldAddModel3= true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

    }


    private void placeObject(ArFragment arFragment, Anchor anchor, Uri uri) {
        ModelRenderable.builder()
                .setSource(arFragment.getContext(), uri)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable))
                .exceptionally(throwable -> {
                            Toast.makeText(arFragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            return null;
                        }

                );
    }

    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : augmentedImages) {
            //track the image if image match with database model will be placed
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
                if (augmentedImage.getName().equals("default.jpg") && shouldAddModel1)
                {
                    placeObject(arFragment, augmentedImage.createAnchor(augmentedImage.getCenterPose()), Uri.parse("Earth.sfb"));
                    shouldAddModel1 = false;

                }
             else if (augmentedImage.getName().equals("andy.jpg") && shouldAddModel2)
                {
                    placeObject(arFragment, augmentedImage.createAnchor(augmentedImage.getCenterPose()), Uri.parse("dino.sfb"));
                    shouldAddModel2 = false;
                }
             else if(augmentedImage.getName().equals("van.jpg") && shouldAddModel3)
                {
                    placeObject(arFragment, augmentedImage.createAnchor(augmentedImage.getCenterPose()), Uri.parse("andy.sfb"));
                    shouldAddModel3 = false;
                } }
            else if(augmentedImage.getTrackingState() == TrackingState.STOPPED) {
             augmentedImages.remove(augmentedImage);
            }
        }}



    public boolean setupAugmentedImagesDb(Config config, Session session) {
        AugmentedImageDatabase augmentedImageDatabase;


        try
        {
            //include reference image file
            InputStream inputStream=getAssets().open("edmtdev.imgdb");
            augmentedImageDatabase = AugmentedImageDatabase.deserialize(session,inputStream);
            config.setAugmentedImageDatabase(augmentedImageDatabase);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

    }


    private Bitmap loadAugmentedImage() {
        try (InputStream is = getAssets().open("andy.jpg")) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e("ImageLoad", "IO Exception", e);
        }

        return null;
    }


    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        //used to implement zooming and scaleable functionality
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();

    }


}
