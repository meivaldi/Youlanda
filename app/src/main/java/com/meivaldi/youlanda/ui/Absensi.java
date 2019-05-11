package com.meivaldi.youlanda.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.meivaldi.youlanda.R;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Absensi extends AppCompatActivity {

    private static final String MODEL_PATH = "optimized_graph.lite";
    private static final String LABEL_PATH = "retrained_labels.txt";

    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private Interpreter tflite;
    private ByteBuffer imgData;
    private List<String> labelList;
    private Dialog dialog;
    private Toolbar toolbar;
    private Button absen, ambilFoto, absenYa, absenTidak;
    private String currentPhotoPath;
    private ProgressDialog progressDialog;
    private ImageView image, photo;
    private Bitmap edited;
    private Dialog absensi;
    private TextView namaKaryawan, nikKaryawan;

    private int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
    private float[][] labelProbArray = null;

    private FirebaseVisionFaceDetectorOptions detectorOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Absensi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgData = ByteBuffer.allocateDirect(
                4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memroses ...");
        progressDialog.setCancelable(false);

        absensi = new Dialog(this);
        absensi.setContentView(R.layout.absensi);
        absensi.setCancelable(false);

        namaKaryawan = absensi.findViewById(R.id.nama_karyawan);
        nikKaryawan = absensi.findViewById(R.id.nik_karyawan);
        absenYa = absensi.findViewById(R.id.absen_ya);
        absenTidak = absensi.findViewById(R.id.absen_tidak);

        absenYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        absenTidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgData = null;
                absensi.dismiss();
                photo.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                ambilFoto.setVisibility(View.VISIBLE);
            }
        });

        ambilFoto = findViewById(R.id.ambil_foto);
        absen = findViewById(R.id.absen);

        image = findViewById(R.id.image);
        photo = findViewById(R.id.photo);

        ambilFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(Absensi.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);
            }
        });

        absen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog.show();
                    convertBitmapToByteBuffer(edited);
                    tflite = new Interpreter(loadModelFile(Absensi.this));
                    labelList = loadLabelList(Absensi.this);
                    labelProbArray = new float[1][labelList.size()];

                    if (tflite == null) {
                        Toast.makeText(Absensi.this, "tflite kosong", Toast.LENGTH_SHORT).show();
                    } else {
                        tflite.run(imgData, labelProbArray);
                    }

                    float maxValue = Float.MIN_VALUE;
                    int max = 0;
                    for (int i=0; i<labelList.size(); i++) {
                        if (labelProbArray[0][i] > maxValue) {
                            maxValue = labelProbArray[0][i];
                            max = i;
                        }
                    }

                    progressDialog.dismiss();
                    absensi.show();
                    nikKaryawan.setText(labelList.get(max));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        detectorOptions  = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build();
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);

        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();

        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight());

        int pixel = 0;
        for (int i=0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j=0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];

                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }
    }

    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(getApplicationContext(), "Kamera tidak diizinkan!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            progressDialog.show();
            final Bitmap source = BitmapFactory.decodeFile(currentPhotoPath);
            FirebaseVisionImage img = FirebaseVisionImage.fromBitmap(source);
            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(detectorOptions);

            detector.detectInImage(img)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> faces) {
                            FirebaseVisionFace face = null;

                            try {
                                face = faces.get(0);
                            } catch (IndexOutOfBoundsException e) {
                                Toast.makeText(Absensi.this, "Wajah tidak ditemukan", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }

                            Rect rect = face.getBoundingBox();
                            int width = rect.right - rect.left;
                            int height = rect.bottom - rect.top;

                            Bitmap wajah = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

                            try {
                                int px, r, g, b;
                                for (int x=0; x<width; x++) {
                                    for (int y=0; y<height; y++) {
                                        px = source.getPixel(x+rect.left, y+rect.top);
                                        r = Color.red(px);
                                        g = Color.green(px);
                                        b = Color.blue(px);

                                        wajah.setPixel(x, y, Color.rgb(r, g, b));
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                Toast.makeText(Absensi.this, "Silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }

                            edited = Bitmap.createScaledBitmap(wajah, 224, 224, false);
                            photo.setImageBitmap(edited);
                            photo.setVisibility(View.VISIBLE);
                            image.setVisibility(View.GONE);
                            ambilFoto.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Absensi.this, "Wajah tidak terdeteksi", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
