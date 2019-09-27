//================================================================================================================================
//
// Copyright (c) 2015-2019 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.helloarmultitargetsi;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.easyar.Buffer;
import cn.easyar.CameraDevice;
import cn.easyar.CameraDeviceFocusMode;
import cn.easyar.CameraDevicePreference;
import cn.easyar.CameraDeviceSelector;
import cn.easyar.CameraDeviceType;
import cn.easyar.CameraParameters;
import cn.easyar.DelayedCallbackScheduler;
import cn.easyar.FeedbackFrameFork;
import cn.easyar.FrameFilterResult;
import cn.easyar.FunctorOfVoidFromTargetAndBool;
import cn.easyar.Image;
import cn.easyar.ImageTarget;
import cn.easyar.ImageTracker;
import cn.easyar.ImageTrackerResult;
import cn.easyar.InputFrame;
import cn.easyar.InputFrameFork;
import cn.easyar.InputFrameThrottler;
import cn.easyar.InputFrameToFeedbackFrameAdapter;
import cn.easyar.InputFrameToOutputFrameAdapter;
import cn.easyar.Matrix44F;
import cn.easyar.OutputFrame;
import cn.easyar.OutputFrameBuffer;
import cn.easyar.OutputFrameFork;
import cn.easyar.OutputFrameJoin;
import cn.easyar.Target;
import cn.easyar.TargetInstance;
import cn.easyar.TargetStatus;
import cn.easyar.Vec2F;
import cn.easyar.Vec2I;

public class HelloAR
{
    private DelayedCallbackScheduler scheduler;
    private CameraDevice camera;
    private ArrayList<ImageTracker> trackers;
    private BGRenderer bgRenderer;
    private BoxRenderer boxRenderer;
    private InputFrameThrottler throttler;
    private FeedbackFrameFork feedbackFrameFork;
    private InputFrameToOutputFrameAdapter i2OAdapter;
    private InputFrameFork inputFrameFork;
    private OutputFrameJoin join;
    private OutputFrameBuffer oFrameBuffer;
    private InputFrameToFeedbackFrameAdapter i2FAdapter;
    private OutputFrameFork outputFrameFork;
    private int previousInputFrameIndex = -1;

    public HelloAR()
    {
        scheduler = new DelayedCallbackScheduler();
        trackers = new ArrayList<ImageTracker>();
    }

    private void loadFromImage(ImageTracker tracker, String path, String name)
    {
        ImageTarget target = ImageTarget.createFromImageFile(path,1,name, "", "", 1.0f);
        if(target == null) {
            Log.e("HelloAR","target create failed or key is not correct");
            return;
        }
        tracker.loadTarget(target, scheduler, new FunctorOfVoidFromTargetAndBool() {
            @Override
            public void invoke(Target target, boolean status) {
                Log.i("HelloAR", String.format("load target (%b): %s (%d)", status, target.name(), target.runtimeID()));
            }
        });
    }

    public void recreate_context()
    {
        if (bgRenderer != null) {
            bgRenderer.dispose();
            bgRenderer = null;
        }
        if (boxRenderer != null) {
            boxRenderer.dispose();
            boxRenderer = null;
        }
        previousInputFrameIndex = -1;
        bgRenderer = new BGRenderer();
        boxRenderer = new BoxRenderer();
    }

    public void initialize()
    {
        recreate_context();

        camera = CameraDeviceSelector.createCameraDevice(CameraDevicePreference.PreferObjectSensing);
		camera.setBufferCapacity(6);
        throttler = InputFrameThrottler.create();
        inputFrameFork = InputFrameFork.create(2);
        join = OutputFrameJoin.create(2);
        oFrameBuffer = OutputFrameBuffer.create();
        i2OAdapter = InputFrameToOutputFrameAdapter.create();
        i2FAdapter = InputFrameToFeedbackFrameAdapter.create();
        outputFrameFork = OutputFrameFork.create(2);

        boolean status = true;
        status &= camera.openWithType(CameraDeviceType.Default);
        camera.setFocusMode(CameraDeviceFocusMode.Continousauto);
        camera.setSize(new Vec2I(1920, 1080));
        camera.setBufferCapacity(5 + 11);
        if (!status) { return; }
        ImageTracker tracker = ImageTracker.create();

        //Maximum amount of items tracked simultaneously
        //Over 6 I already got not enough memory and app shutdown (3GB memory phone)
        tracker.setSimultaneousNum(6);
            loadFromImage(tracker, "1.jpg", "1");
            loadFromImage(tracker, "1.jpg", "11");
            loadFromImage(tracker, "2.jpg", "2");
            loadFromImage(tracker, "2.jpg", "22");
            loadFromImage(tracker, "3.jpg", "3");
            loadFromImage(tracker, "3.jpg", "33");
            loadFromImage(tracker, "4.jpg", "4");
            loadFromImage(tracker, "4.jpg", "44");
        for(int i = 0; i < 4; i++){
            loadFromImage(tracker, "VŠB.jpg", "VŠB" + i);
        }
        trackers.add(tracker);

        feedbackFrameFork = FeedbackFrameFork.create(trackers.size());

        camera.inputFrameSource().connect(throttler.input());
        throttler.output().connect(inputFrameFork.input());
        inputFrameFork.output(0).connect(i2OAdapter.input());
        i2OAdapter.output().connect(join.input(0));

        inputFrameFork.output(1).connect(i2FAdapter.input());
        i2FAdapter.output().connect(feedbackFrameFork.input());
        int k = 0;
        for (ImageTracker _tracker : trackers)
        {
            feedbackFrameFork.output(k).connect(_tracker.feedbackFrameSink());
            tracker.outputFrameSource().connect(join.input(k+1));
            k++;
        }

        join.output().connect(outputFrameFork.input());
        outputFrameFork.output(0).connect(oFrameBuffer.input());
        outputFrameFork.output(1).connect(i2FAdapter.sideInput());
        oFrameBuffer.signalOutput().connect(throttler.signalInput());
    }

    public void dispose()
    {
        for (ImageTracker tracker : trackers) {
            tracker.dispose();
        }
        trackers.clear();
        if (bgRenderer != null) {
            bgRenderer.dispose();
            bgRenderer = null;
        }
        if (boxRenderer != null) {
            boxRenderer.dispose();
            boxRenderer = null;
        }
        if (camera != null) {
            camera.dispose();
            camera = null;
        }
        if (scheduler != null) {
            scheduler.dispose();
            scheduler = null;
        }
    }

    public boolean start()
    {
        boolean status = true;
        if (camera != null) {
            status &= camera.start();
        } else {
            status = false;
        }
        for (ImageTracker tracker : trackers) {
            status &= tracker.start();
        }
        return status;
    }

    public void stop()
    {
        if (camera != null) {
            camera.stop();
        }
        for (ImageTracker tracker : trackers) {
            tracker.stop();
        }
    }

    public void render(int width, int height, int screenRotation)
    {
        while (scheduler.runOne())
        {
        }

        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0.f, 0.f, 0.f, 1.f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        OutputFrame oframe = oFrameBuffer.peek();
        if (oframe == null) { return;}
        InputFrame iframe = oframe.inputFrame();
        if (iframe == null) { oframe.dispose(); return; }
        CameraParameters cameraParameters = iframe.cameraParameters();
        if (cameraParameters == null) { oframe.dispose(); iframe.dispose(); return; }
        float viewport_aspect_ratio = (float)width / (float)height;
        Matrix44F imageProjection = cameraParameters.imageProjection(viewport_aspect_ratio, screenRotation, true, false);
        Image image = iframe.image();

        try {
            if (iframe.index() != previousInputFrameIndex) {
                Buffer buffer = image.buffer();
                try {
                    byte[] bytes = new byte[buffer.size()];
                    buffer.copyToByteArray(bytes);
                    bgRenderer.upload(image.format(), image.width(), image.height(), ByteBuffer.wrap(bytes));
                } finally {
                    buffer.dispose();
                }
                previousInputFrameIndex = iframe.index();
            }
            bgRenderer.render(imageProjection);

            Matrix44F projectionMatrix = cameraParameters.projection(0.01f, 1000.f, viewport_aspect_ratio, screenRotation, true,false);
            for (FrameFilterResult oResult : oframe.results()) {
                ImageTrackerResult result = (ImageTrackerResult)oResult;
                if (result != null) {
                    for (TargetInstance targetInstance : result.targetInstances()) {
                        int status = targetInstance.status();
                        if (status == TargetStatus.Tracked) {
                            Target target = targetInstance.target();
                            ImageTarget imagetarget = target instanceof ImageTarget ? (ImageTarget) (target) : null;
                            if (imagetarget == null) {
                                continue;
                            }
                            ArrayList<Image> images = ((ImageTarget) target).images();
                            Image targetImg = images.get(0);
                            float targetScale = imagetarget.scale();
                            Vec2F scale = new Vec2F(targetScale, targetScale * targetImg.height() / targetImg.width());
                            //Each VŠB items in different scale and color + white box numero uno
                            if(target.name().equals("VŠB1")){
                                new BoxRenderer(255, 0, 0).render(projectionMatrix, targetInstance.pose(), scale, 0.35f);
                            } else if(target.name().equals("VŠB2")) {
                                new BoxRenderer(0, 255, 0).render(projectionMatrix, targetInstance.pose(), scale, .4f);
                            } else if(target.name().equals("VŠB3")) {
                                new BoxRenderer(0, 0, 255).render(projectionMatrix, targetInstance.pose(), scale, .45f);
                            } else if (target.name().equals("1")){
                                new BoxRenderer(255, 255, 255).render(projectionMatrix, targetInstance.pose(), scale);
                            } else {
                                boxRenderer.render(projectionMatrix, targetInstance.pose(), scale);
                            }
                            for (Image img : images) {
                                img.dispose();
                            }
                        }
                    }
                    result.dispose();
                }
            }
        } finally {
            iframe.dispose();
            oframe.dispose();
            if (cameraParameters != null) {
                cameraParameters.dispose();
            }
            image.dispose();
        }
    }
}
