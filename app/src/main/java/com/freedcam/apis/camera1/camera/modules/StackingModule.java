package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.apis.camera1.camera.modules.image_saver.I_WorkeDone;
import com.freedcam.apis.camera1.camera.modules.image_saver.StackSaver;
import com.freedcam.apis.i_camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;


import java.io.File;

/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class StackingModule extends PictureModule implements I_WorkeDone {

    private int FrameCount = 0;
    private boolean KeepStacking = true;

    private StackSaver stackSaver;

    public StackingModule(BaseCameraHolder cameraHandler, ModuleEventHandler eventHandler, Context context,AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context,appSettingsManager);
        name = ModuleHandler.MODULE_STACKING;

    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork() {


        if (!isWorking)
        {


                ParameterHandler.ZSL.SetValue("off", true);

            startworking();

            final String picFormat = ParameterHandler.PictureFormat.GetValue();
            if (picFormat.equals("jpeg") || picFormat.equals("yuv422") )
                takePicture();

            return true;
        }

        else {
            KeepStacking = false;
            return false;
        }

    }


    private void takePicture()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }

                final String picFormat = ParameterHandler.PictureFormat.GetValue();
                if (picFormat.equals("jpeg")) {
                    //final StackSaver stackSaver = new StackSaver(baseCameraHolder, StackingModule.this);

                    stackSaver.TakePicture();
                }
            }
        });
    }

    @Override
    public String ShortName() {
        return "Stack";
    }

    @Override
    public String LongName() {
        return "Stacking";
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void LoadNeededParameters()
    {
        stackSaver = new StackSaver(baseCameraHolder, StackingModule.this,context,appSettingsManager);
    }

    @Override
    public void UnloadNeededParameters(){

    }

    @Override
    public void OnWorkDone(File file)
    {

        baseCameraHolder.StartPreview();

        if(KeepStacking)
            takePicture();
        else {
            stopworking();

            MediaScannerManager.ScanMedia(context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);
        }
    }

    @Override
    public void OnError(String error)
    {
        baseCameraHolder.errorHandler.OnError(error);
        stopworking();
    }


   /* I_Callbacks.PictureCallback aeBracketCallback = new I_Callbacks.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data) {
            final String picFormat = ParameterHandler.PictureFormat.GetValue();
            if (picFormat.equals("jpeg")) {
                final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, aeBracketDone);
                jpegSaver.saveBytesToFile(data, new File(StringUtils.getFilePathHDR(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), jpegSaver.fileEnding, hdrCount)),true);
            }

        }
    };

    I_WorkeDone aeBracketDone = new I_WorkeDone() {
        @Override
        public void OnWorkDone(File file) {
            MediaScannerManager.ScanMedia(AppSettingsManager.APPSETTINGSMANAGER.context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);

        }
        @Override
        public void OnError(String error)
        {
            baseCameraHolder.errorHandler.OnError(error);
            stopworking();
        }
    };*/
}
