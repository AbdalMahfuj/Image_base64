package com.example.image_base64;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int Camera_perm_code = 123;
    public static final int Camera_req_code = 102;
    // Define the button and imageview type variable
    Button camera_open_id, btn_convertToStr, btn_convertToImg;
    TextView strView;
    ImageView click_image_id, conv_image_id;
    Bitmap image;
    String imageString;
    byte[] imageBytes;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera_open_id = findViewById(R.id.camera_button);
        click_image_id = findViewById(R.id.click_image);
        conv_image_id = findViewById(R.id.click_image_conv);
        strView = findViewById(R.id.strID);
        btn_convertToStr = findViewById(R.id.btn1);
        btn_convertToImg = findViewById(R.id.btn2);

        camera_open_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();

            }
        });

        btn_convertToStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //encode image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                strView.setText(imageString);


            }
        });


        btn_convertToImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //decode base64 string to image
                imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                conv_image_id.setImageBitmap(decodedImage);
            }
        });
    }

    public void askCameraPermission() {
        // if not already given permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Camera_perm_code);
        } else { // if already given
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Camera_perm_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 0-> not granted
                // permission is given
                openCamera();
            } else { // denied to give camera permission
                Toast.makeText(this, "Camera Permission is required !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void openCamera() {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera,Camera_req_code);
    }

    // This method retrieves the image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera_req_code) {
            image = (Bitmap) data.getExtras().get("data");
            click_image_id.setImageBitmap(image);
        }
    }
}