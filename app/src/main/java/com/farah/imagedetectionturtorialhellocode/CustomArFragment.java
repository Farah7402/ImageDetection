package com.farah.imagedetectionturtorialhellocode;

import android.util.Log;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomArFragment extends ArFragment {


    @Override
    protected Config getSessionConfiguration(Session session) {
        //use to off hand
        getPlaneDiscoveryController().setInstructionView(null);
        //use to update camera session LATEST_CAMERA_IMAGE.
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);
        getArSceneView().setupSession(session);
        //check database is created or not
        if ((((MainActivity) getActivity()).setupAugmentedImagesDb(config, session))) {
            Log.d("SetupAugImgDb", "Success");
        } else {
            Log.e("SetupAugImgDb","Faliure setting up db");
        }
        return config;
    }
}
