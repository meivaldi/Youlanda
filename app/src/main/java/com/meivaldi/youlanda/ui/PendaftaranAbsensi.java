package com.meivaldi.youlanda.ui;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Base64;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.meivaldi.youlanda.R;
import com.meivaldi.youlanda.data.network.GetDataService;
import com.meivaldi.youlanda.data.network.ResponseApi;
import com.meivaldi.youlanda.data.network.RetrofitClientInstance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PendaftaranAbsensi extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE_1 = 1;
    static final int REQUEST_IMAGE_CAPTURE_2 = 2;
    static final int REQUEST_IMAGE_CAPTURE_3 = 3;

    private Toolbar toolbar;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Button foto, foto2, foto3, proses;
    private ImageView image, image2, image3;
    private ImageView photo, photo2, photo3;
    private Bitmap edited1, edited2, edited3;
    private EditText nikET;
    private String file1, file2, file3;

    private FirebaseVisionFaceDetectorOptions detectorOptions;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran_absensi);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Pendaftaran Absensi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        foto = findViewById(R.id.foto1);
        foto2 = findViewById(R.id.foto2);
        foto3 = findViewById(R.id.foto3);

        photo = findViewById(R.id.photo);
        photo2 = findViewById(R.id.photo2);
        photo3 = findViewById(R.id.photo3);

        image = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);

        nikET = findViewById(R.id.nikET);

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(PendaftaranAbsensi.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE_1);
            }
        });

        foto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(PendaftaranAbsensi.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE_2);
            }
        });

        foto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(PendaftaranAbsensi.this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE_3);
            }
        });

        detectorOptions  = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build();

        proses = findViewById(R.id.proses);
        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });

    }

    private void sendImage() {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        GetDataService apiInterface = retrofit.create(GetDataService.class);

        savePictureOne(apiInterface);
        savePictureTwo(apiInterface);
        savePictureThree(apiInterface);

        nikET.setText("");

        photo.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        foto.setVisibility(View.VISIBLE);

        photo2.setVisibility(View.GONE);
        image2.setVisibility(View.VISIBLE);
        foto2.setVisibility(View.VISIBLE);

        photo3.setVisibility(View.GONE);
        image3.setVisibility(View.VISIBLE);
        foto3.setVisibility(View.VISIBLE);
    }

    private void savePictureOne(GetDataService api) {
        String photo = toBase64(edited1);
        String nik = nikET.getText().toString();

        Call<ResponseApi> call = api.uploadImage(photo, file1, nik);

        call.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                Toast.makeText(PendaftaranAbsensi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Toast.makeText(PendaftaranAbsensi.this, "Request Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePictureTwo(GetDataService api) {
        String photo = toBase64(edited2);
        String nik = nikET.getText().toString();

        Call<ResponseApi> call = api.uploadImage(photo, file2, nik);

        call.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                Toast.makeText(PendaftaranAbsensi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Toast.makeText(PendaftaranAbsensi.this, "Request Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePictureThree(GetDataService api) {
        String photo = toBase64(edited3);
        String nik = nikET.getText().toString();

        Call<ResponseApi> call = api.uploadImage(photo, file3, nik);

        call.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                Toast.makeText(PendaftaranAbsensi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Toast.makeText(PendaftaranAbsensi.this, "Request Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dispatchTakePictureIntent(int code) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();

                if (code == REQUEST_IMAGE_CAPTURE_1) {
                    file1 = photoFile.getName();
                } else if (code == REQUEST_IMAGE_CAPTURE_2) {
                    file2 = photoFile.getName();
                } else if (code == REQUEST_IMAGE_CAPTURE_3) {
                    file3 = photoFile.getName();
                }

            } catch (IOException ex) {
                Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, code);
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
            case REQUEST_IMAGE_CAPTURE_1:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_1);
                } else {
                    Toast.makeText(getApplicationContext(), "Kamera tidak diizinkan!", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_IMAGE_CAPTURE_2:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_2);
                } else {
                    Toast.makeText(getApplicationContext(), "Kamera tidak diizinkan!", Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_IMAGE_CAPTURE_3:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_3);
                } else {
                    Toast.makeText(getApplicationContext(), "Kamera tidak diizinkan!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_1 && resultCode == RESULT_OK) {
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
                                Toast.makeText(PendaftaranAbsensi.this, "Wajah tidak ditemukan", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PendaftaranAbsensi.this, "Silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }

                            edited1 = Bitmap.createScaledBitmap(wajah, 224, 224, false);
                            photo.setImageBitmap(edited1);
                            photo.setVisibility(View.VISIBLE);
                            image.setVisibility(View.GONE);
                            foto.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PendaftaranAbsensi.this, "Wajah tidak terdeteksi", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_2 && resultCode == RESULT_OK) {
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
                                Toast.makeText(PendaftaranAbsensi.this, "Wajah tidak ditemukan", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PendaftaranAbsensi.this, "Silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }

                            edited2 = Bitmap.createScaledBitmap(wajah, 224, 224, false);
                            photo2.setImageBitmap(edited2);
                            photo2.setVisibility(View.VISIBLE);
                            image2.setVisibility(View.GONE);
                            foto2.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PendaftaranAbsensi.this, "Wajah tidak terdeteksi", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_3 && resultCode == RESULT_OK) {
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
                                Toast.makeText(PendaftaranAbsensi.this, "Wajah tidak ditemukan", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PendaftaranAbsensi.this, "Silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }

                            edited3 = Bitmap.createScaledBitmap(wajah, 224, 224, false);
                            photo3.setImageBitmap(edited3);
                            photo3.setVisibility(View.VISIBLE);
                            image3.setVisibility(View.GONE);
                            foto3.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PendaftaranAbsensi.this, "Wajah tidak terdeteksi", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
