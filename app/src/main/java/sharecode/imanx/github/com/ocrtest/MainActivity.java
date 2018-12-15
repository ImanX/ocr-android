package sharecode.imanx.github.com.ocrtest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

/**
 * Created by ImanX.
 * ocrtest | Copyrights 2018 ZarinPal Crop.
 */
public class MainActivity extends AppCompatActivity {


    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private TextView txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.txt = findViewById(R.id.txt);
        this.surfaceView = findViewById(R.id.surface);

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!recognizer.isOperational()) {
            Log.i("TAG", "Dependencies not supported.");
            return;
        }

        cameraSource = new CameraSource.Builder(getApplicationContext(), recognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1000, 500)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(callback);
        recognizer.setProcessor(detections);

    }


    private Detector.Processor<TextBlock> detections = new Detector.Processor<TextBlock>() {
        @Override
        public void release() {

        }

        @Override
        public void receiveDetections(Detector.Detections<TextBlock> detections) {
            SparseArray<TextBlock> items = detections.getDetectedItems();
            if (items.size() != 0) {
                final StringBuilder builder = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    TextBlock item = items.valueAt(i);
                    builder.append(item.getValue());
                    builder.append("\n");
                }

                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        txt.setText(builder.toString());
                    }
                });
            }

        }
    };


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @SuppressLint("MissingPermission")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                cameraSource.start(surfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cameraSource.stop();
        }
    };
}
